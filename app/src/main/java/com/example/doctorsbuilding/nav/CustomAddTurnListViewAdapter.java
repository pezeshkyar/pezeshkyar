package com.example.doctorsbuilding.nav;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by hossein on 7/31/2016.
 */
public class CustomAddTurnListViewAdapter extends BaseAdapter {


    /**
     * Created by hossein on 6/12/2016.
     */
    private Context context;
    private ArrayList<ArrayList<String>> items;

    public CustomAddTurnListViewAdapter(Context context, ArrayList<ArrayList<String>> items) {
        this.context = context;
        this.items = items;
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
        public TextView time;
        public TextView task;

        public Holder(View v) {
            name = (TextView) v.findViewById(R.id.addTurn_listViewItem_name);
            time = (TextView) v.findViewById(R.id.addTurn_listViewItem_time);
            task = (TextView) v.findViewById(R.id.addTurn_listViewItem_task);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        View rowView = convertView;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.turn_activity_listview_item, null);
            holder = new Holder(rowView);
            rowView.setTag(holder);
        } else {
            holder = (Holder) rowView.getTag();
        }
        holder.name.setText(items.get(position).get(0));
        holder.time.setText(items.get(position).get(1));
        holder.task.setText(items.get(position).get(2));

        return rowView;
    }
}
