package com.example.doctorsbuilding.nav;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.doctorsbuilding.nav.User.UserInboxActivity;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by hossein on 6/12/2016.
 */
public class CustomListAdapterNotificationDialog extends BaseAdapter {
    private Context context;
    private ArrayList<MessageInfo> items;
    private Dialog parentDialog;

    public CustomListAdapterNotificationDialog(Context context, ArrayList<MessageInfo> items, Dialog parent) {
        this.context = context;
        this.items = items;
        this.parentDialog = parent;
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

    class Holder {
        public TextView name;
        public TextView date;
        public TextView message;

        public Holder(View v) {
            name = (TextView) v.findViewById(R.id.notification_rowItem_drName);
            date = (TextView) v.findViewById(R.id.notification_rowItem_date);
            message = (TextView) v.findViewById(R.id.notification_rowItem_message);
        }
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        Holder holder;
        View rowView = convertView;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.notification_row_item, null);
            holder = new Holder(rowView);
            rowView.setTag(holder);
        } else {
            holder = (Holder) rowView.getTag();
        }
        holder.name.setText(items.get(position).getSenderFirstName() + " " + items.get(position).getSenderLastName());
        holder.date.setText(items.get(position).getDate() + "   " + items.get(position).getTime());
        String str = items.get(position).getMessage();
        str = str.length()>100 ? str.substring(0,100)+" ..." : str;
        holder.message.setText(str);

        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, UserInboxActivity.class);
                intent.putExtra("MessageInfo", items.get(position));
                context.startActivity(intent);
                parentDialog.dismiss();
            }
        });

        return rowView;
    }
}
