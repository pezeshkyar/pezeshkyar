package com.example.doctorsbuilding.nav;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.doctorsbuilding.nav.User.User;

import java.util.ArrayList;

/**
 * Created by hossein on 7/24/2016.
 */
public class CustomReservationListAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<ArrayList<String>> users = new ArrayList<ArrayList<String>>();
    private int turnId;

    public CustomReservationListAdapter(Context context, ArrayList<ArrayList<String>> users, int turnId) {
        this.context = context;
        this.users = users;
        this.turnId = turnId;
    }

    class Holder {
        public TextView name;
        public TextView mobile;

        public Holder(View row) {
            name = (TextView) row.findViewById(R.id.search_item_name);
            mobile = (TextView) row.findViewById(R.id.search_item_mobile);
        }
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        Holder holder;
        View rowView = convertView;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.search_item, null);
            holder = new Holder(rowView);
            rowView.setTag(holder);
        } else {
            holder = (Holder) rowView.getTag();
        }
        holder.name.setText(users.get(position).get(0));
        holder.mobile.setText(users.get(position).get(1));
        return rowView;
    }

    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public Object getItem(int position) {
        return users.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

}
