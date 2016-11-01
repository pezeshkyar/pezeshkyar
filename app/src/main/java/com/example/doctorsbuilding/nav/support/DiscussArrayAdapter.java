package com.example.doctorsbuilding.nav.support;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.doctorsbuilding.nav.G;
import com.example.doctorsbuilding.nav.R;

import java.util.ArrayList;
import java.util.List;

public class DiscussArrayAdapter extends ArrayAdapter<OneComment> {

    private List<OneComment> countries = new ArrayList<OneComment>();
    private LinearLayout wrapper;
    private LinearLayout layout_row;

    @Override
    public void add(OneComment object) {
        countries.add(object);
        super.add(object);
    }

    public DiscussArrayAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public int getCount() {
        return this.countries.size();
    }

    public OneComment getItem(int index) {
        return this.countries.get(index);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.listitem_discuss, parent, false);
        }

        wrapper = (LinearLayout) row.findViewById(R.id.wrapper);
        layout_row = (LinearLayout) row.findViewById(R.id.support_row);
        OneComment coment = getItem(position);
        layout_row.setBackgroundResource(coment.left ? R.drawable.bubble_yellow : R.drawable.bubble_green);
        wrapper.setGravity(coment.left ? Gravity.LEFT : Gravity.RIGHT);


        TextView sender = (TextView) row.findViewById(R.id.support_sender);
        sender.setTypeface(G.getBoldFont());
        sender.setText(coment.sender);

        TextView comment = (TextView) row.findViewById(R.id.support_comment);
        comment.setTypeface(G.getNormalFont());
        comment.setText(coment.comment);

        TextView date = (TextView) row.findViewById(R.id.support_date);
        date.setTypeface(G.getNormalFont());
        date.setText(coment.date);

//        date.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
//        comment.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
//
//        int date_size = date.getMeasuredWidth();
//        int commnet_size = comment.getMeasuredWidth();
//
//        if (date_size > commnet_size) {
//            RelativeLayout.LayoutParams params_date = (RelativeLayout.LayoutParams) comment.getLayoutParams();
//            params_date.addRule(RelativeLayout.RIGHT_OF, R.id.support_date);
//            comment.setLayoutParams(params_date);
//        } else {
//            RelativeLayout.LayoutParams params_date1 = (RelativeLayout.LayoutParams) comment.getLayoutParams();
//            params_date1.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
//            comment.setLayoutParams(params_date1);
//        }

        return row;
    }

    public Bitmap decodeToBitmap(byte[] decodedByte) {
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }

}