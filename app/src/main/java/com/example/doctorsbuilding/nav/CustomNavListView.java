package com.example.doctorsbuilding.nav;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by hossein on 12/10/2016.
 */
public class CustomNavListView extends BaseAdapter {
    private Context context;
    private ArrayList<MenuItem> items;

    public CustomNavListView(Context context, ArrayList<MenuItem> items) {
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
        public ImageView icon;
        public TextView text;

        public Holder(View v) {
            icon = (ImageView) v.findViewById(R.id.nav1_item_icon);
            text = (TextView) v.findViewById(R.id.nav1_item_text);
        }
    }

    public void addAll(ArrayList<MenuItem> itemha){
        items.clear();
        items.addAll(itemha);
        notifyDataSetChanged();
    }

    public void removeAll(){
        items.clear();
        notifyDataSetChanged();
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        Holder holder;
        View rowView = convertView;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.nav1_item, null);
            holder = new Holder(rowView);
            rowView.setTag(holder);
        } else {
            holder = (Holder) rowView.getTag();
        }
        holder.icon.setImageDrawable(items.get(position).getIcon());
        holder.text.setText(items.get(position).getTitle());
        return rowView;
    }
}
