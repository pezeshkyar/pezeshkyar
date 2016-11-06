package com.example.doctorsbuilding.nav.User;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.doctorsbuilding.nav.G;
import com.example.doctorsbuilding.nav.MessageInfo;
import com.example.doctorsbuilding.nav.PException;
import com.example.doctorsbuilding.nav.R;
import com.example.doctorsbuilding.nav.Util.MessageBox;
import com.example.doctorsbuilding.nav.Web.WebService;

import java.util.ArrayList;

/**
 * Created by hossein on 6/13/2016.
 */
public class CustomListAdapterUserInbox extends BaseAdapter {
    private Context context;
    private ArrayList<MessageInfo> items;

    public CustomListAdapterUserInbox(Context context, ArrayList<MessageInfo> items) {
        this.context = context;
        this.items = items;
    }

    class Holder {
        public TextView name;
        public TextView date;
        public TextView message;
        public Button btnDelete;

        public Holder(View v) {
            name = (TextView) v.findViewById(R.id.userInbox_drName);
            date = (TextView) v.findViewById(R.id.userInbox_date);
            message = (TextView) v.findViewById(R.id.userInbox_message);
            btnDelete = (Button) v.findViewById(R.id.userInbox_btnDelete);
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Holder holder;
        View rowView = convertView;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.user_inbox_row_item, null);
            holder = new Holder(rowView);
            rowView.setTag(holder);
        } else {
            holder = (Holder) rowView.getTag();
        }
        holder.name.setText(items.get(position).getSenderFirstName().concat(" " + items.get(position).getSenderLastName()));
        holder.date.setText(items.get(position).getDate().concat("   " + items.get(position).getTime()));
        holder.message.setText(items.get(position).getMessage());
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                asyncRemoveMessage task = new asyncRemoveMessage();
                task.execute(String.valueOf(items.get(position).getId()), String.valueOf(position));
            }
        });


        return rowView;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class asyncRemoveMessage extends AsyncTask<String, String, Void> {

        private boolean result = false;
        private int possition = -1;
        String msg = null;
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(context, "", "در حال حذف پیام ...");
            dialog.getWindow().setGravity(Gravity.END);
            dialog.setCancelable(true);
        }

        @Override
        protected Void doInBackground(String... strings) {
            possition = Integer.parseInt(strings[1]);
            try {
                result = WebService.invokeRemoveMessageWS(G.UserInfo.getUserName(), G.UserInfo.getPassword(), G.officeId
                        , Integer.parseInt(strings[0]));
            } catch (PException ex) {
                msg = ex.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (msg != null) {
                dialog.dismiss();
                new MessageBox(context, msg).show();
            } else {
                dialog.dismiss();
                if (result) {
                    Toast.makeText(context, "حذف پیام با موفقیت انجام شد .", Toast.LENGTH_SHORT).show();
                    items.remove(possition);
                    notifyDataSetChanged();

                } else {
                    new MessageBox(context, "حذف پیام با خطا مواجه شد !").show();
                }
            }
        }
    }
}
