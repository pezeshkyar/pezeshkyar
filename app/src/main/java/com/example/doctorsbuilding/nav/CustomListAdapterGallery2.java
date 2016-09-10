package com.example.doctorsbuilding.nav;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by hossein on 9/8/2016.
 */
public class CustomListAdapterGallery2 extends BaseAdapter {
    /**
     * Created by hossein on 6/12/2016.
     */
    private Context context;
    private ArrayList<String> items;

    public CustomListAdapterGallery2(Context context, ArrayList<String> items) {
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
        public ImageView image;
        public TextView description;

        public Holder(View v) {
            image = (ImageView) v.findViewById(R.id.gallery2_image);
            description = (TextView) v.findViewById(R.id.gallery2_description);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        View rowView = convertView;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.gallery2_item, null);
            holder = new Holder(rowView);
            rowView.setTag(holder);
        } else {
            holder = (Holder) rowView.getTag();
        }

        holder.image.setImageBitmap(G.doctorImageProfile);
        holder.description.setText(items.get(position));

        return rowView;
    }
}
