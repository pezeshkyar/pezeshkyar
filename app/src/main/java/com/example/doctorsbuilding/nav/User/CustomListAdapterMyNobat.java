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
import com.example.doctorsbuilding.nav.PException;
import com.example.doctorsbuilding.nav.R;
import com.example.doctorsbuilding.nav.ReservationByUser;
import com.example.doctorsbuilding.nav.Util.MessageBox;
import com.example.doctorsbuilding.nav.Web.WebService;

import java.util.ArrayList;

/**
 * Created by hossein on 6/11/2016.
 */
public class CustomListAdapterMyNobat extends BaseAdapter {
    Context context;
    ArrayList<ReservationByUser> items;

    public CustomListAdapterMyNobat(Context context, ArrayList<ReservationByUser> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    class Holder {
        public TextView date;
        public TextView time;
        public TextView patientName;
        public TextView taskName;
        public TextView drCost;
        public Button btnDelte;

        public Holder(View v) {
            date = (TextView) v.findViewById(R.id.myNobat_li_date);
            time = (TextView) v.findViewById(R.id.myNobat_li_time);
            patientName = (TextView) v.findViewById(R.id.myNobat_li_patientName);
            taskName = (TextView) v.findViewById(R.id.myNobat_li_taskName);
            drCost = (TextView) v.findViewById(R.id.myNobat_li_drCost);
            btnDelte = (Button) v.findViewById(R.id.myNobat_li_btnDelete);
        }

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Holder holder;
        View rowView = convertView;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.my_nobat_list_item, null);
            holder = new Holder(rowView);
            rowView.setTag(holder);
        } else {
            holder = (Holder) rowView.getTag();
        }
        holder.date.setText(items.get(position).longDate);
        holder.time.setText(items.get(position).getTime());
        holder.patientName.setText(items.get(position).getPatientFirstName() + " " + items.get(position).getPatientLastName());
        holder.taskName.setText(items.get(position).getTaskName());
        holder.drCost.setText(String.valueOf(items.get(position).getPayment()));
        holder.btnDelte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                asyncCallCancelReservationWS task = new asyncCallCancelReservationWS();
                task.execute(String.valueOf(items.get(position).getReservationId()), String.valueOf(position));
            }
        });
        return rowView;
    }

    private class asyncCallCancelReservationWS extends AsyncTask<String, Void, Void> {
        String result = null;
        private int position;
        String msg = null;
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(context, "", "در حال لغو نوبت ...");
            dialog.getWindow().setGravity(Gravity.END);
        }

        @Override
        protected Void doInBackground(String... strings) {
            position = Integer.parseInt(strings[1]);
            try {
                result = WebService.invokeCancleReservation(G.UserInfo.getUserName(), G.UserInfo.getPassword(), Integer.parseInt(strings[0]));
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
                if (result != null) {
                    dialog.dismiss();
                    if (result.toUpperCase().equals("OK")) {
                        Toast.makeText(context, "حذف نوبت با موفقیت انجام شد .", Toast.LENGTH_SHORT).show();
                        items.remove(position);
                        notifyDataSetChanged();
                    } else {
                        new MessageBox(context, result).show();
                    }

                } else {
                    dialog.dismiss();
                    new MessageBox(context, "عملیات حذف با مشکل مواجه شد !").show();
                }
            }
        }
    }
}
