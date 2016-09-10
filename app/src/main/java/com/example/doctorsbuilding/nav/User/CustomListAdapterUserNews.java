package com.example.doctorsbuilding.nav.User;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.doctorsbuilding.nav.R;

import java.util.ArrayList;

/**
 * Created by hossein on 6/13/2016.
 */
public class CustomListAdapterUserNews extends BaseAdapter {
    private Context context;
    private ArrayList<ArrayList<String>> items;

    public CustomListAdapterUserNews(Context context, ArrayList<ArrayList<String>> items) {
        this.context = context;
        this.items = items;
    }

    class Holder {
        TextView drName;
        TextView news;
        TextView date;

        public Holder(View v) {
            drName = (TextView) v.findViewById(R.id.userNews_rowItem_drName);
            news = (TextView) v.findViewById(R.id.userNews_rowItem_news);
            date = (TextView) v.findViewById(R.id.userNews_rowItem_date);
        }
    }

    @Override
    public View getView(int position, View converView, ViewGroup parent) {
        Holder holder;
        View rowView = converView;
        if (converView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.user_news_row_item, null);
            holder = new Holder(rowView);
            rowView.setTag(holder);
        } else {
            holder = (Holder) rowView.getTag();
        }
        holder.drName.setText(items.get(position).get(0));
        holder.news.setText(items.get(position).get(1));
        holder.date.setText(items.get(position).get(2));

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
}
