package com.example.doctorsbuilding.nav.Dr.Profile;

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
public class CustomListAdapterInformation extends BaseAdapter {
    Context context;
    String[] titles;
    String[] details;
    private static LayoutInflater inflater = null;
    public CustomListAdapterInformation(Context context, String[] titles, String[] details){
        this.context = context;
        this.titles = titles;
        this.details = details;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return titles.length;
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
        TextView title;
        TextView detail;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Holder holder = new Holder();
        View rowView = inflater.inflate(R.layout.lv_frag_info,null);
        holder.title = (TextView) rowView.findViewById(R.id.tvTitle_frag_info);
        holder.detail = (TextView) rowView.findViewById(R.id.tvDetail_frag_info);
        holder.title.setText(titles[position]);
        holder.detail.setText(details[position]);
        return rowView;
    }
}
