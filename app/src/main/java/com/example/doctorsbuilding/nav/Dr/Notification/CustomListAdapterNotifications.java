package com.example.doctorsbuilding.nav.Dr.Notification;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.doctorsbuilding.nav.R;

/**
 * Created by hossein on 5/10/2016.
 */
public class CustomListAdapterNotifications extends BaseAdapter {
    Context context;
    String[] names;
    String[] notifications;
    String[] dates;
    private static LayoutInflater inflater = null;
    public CustomListAdapterNotifications(Context context, String[] names, String[] notifications, String[] dates){
        this.context = context;
        this.names = names;
        this.notifications = notifications;
        this.dates = dates;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return names.length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    public class Holder{
        TextView name;
        TextView date;
        TextView notification;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Holder holder = new Holder();
        View rowView = inflater.inflate(R.layout.lv_frag_notification,null);
        holder.name = (TextView) rowView.findViewById(R.id.tvName_frag_notification);
        holder.date = (TextView) rowView.findViewById(R.id.tvDate_frag_notification);
        holder.notification = (TextView) rowView.findViewById(R.id.tvNotification_frag_Notification);
        holder.name.setText(names[position]);
        holder.date.setText(dates[position]);
        holder.notification.setText(notifications[position]);
        return rowView;
    }
}
