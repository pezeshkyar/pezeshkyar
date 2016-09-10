package com.example.doctorsbuilding.nav;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by hossein on 8/10/2016.
 */
public class CustomAdapterSpinner extends ArrayAdapter<String> {

    private Context context;
    private ArrayList<Rturn> mData;
    private ArrayList<Boolean> CheckedItems;
    public Resources res;
    Rturn mRturn = null;
    LayoutInflater inflater = null;

    public CustomAdapterSpinner(Context context, int textViewResourceId, ArrayList objects, Resources resLocal, ArrayList<Boolean> CheckedItems) {
        super(context, textViewResourceId, objects);
        this.context = context;
        this.mData = objects;
        this.res = resLocal;
        this.CheckedItems = CheckedItems;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }


    public ArrayList<Boolean> getUserForMessage() {
        return CheckedItems;
    }

    public class ViewHolder {
        public TextView name;
        public TextView time;
        public TextView date;
        public CheckBox chbox;
        public View divider;

        public ViewHolder(final View row, final int position) {
            name = (TextView) row.findViewById(R.id.message_spinnerItem_patientName);
            time = (TextView) row.findViewById(R.id.message_spinnerItem_time);
            date = (TextView) row.findViewById(R.id.message_spinnerItem_date);
            chbox = (CheckBox) row.findViewById(R.id.message_spinnerItem_chbox);
            divider = row.findViewById(R.id.message_spinnerItem_line);
        }
    }

    public View getCustomView(final int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.message_spinner_item, parent, false);
            holder = new ViewHolder(convertView, position);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        mRturn = null;
        mRturn = mData.get(position);
        if (position == 0) {
            holder.name.setText("لیست دریافت کنندگان پیام ...");
            holder.name.setTextSize(16f);
            holder.name.setTextColor(Color.BLACK);
            holder.date.setVisibility(View.GONE);
            holder.time.setVisibility(View.GONE);
            holder.chbox.setVisibility(View.GONE);
            holder.divider.setVisibility(View.GONE);
        } else {
            holder.name.setText(mRturn.getPatientFirstName() + " " + mRturn.getPatientLastName());
            holder.name.setTextSize(14f);
            holder.name.setTextColor(Color.BLACK);
            holder.time.setVisibility(View.VISIBLE);
            holder.time.setText(mRturn.getHour() + " : " + mRturn.getMin() + " الی " + (mRturn.getHour() + (mRturn.getDuration() / 60)) + " : " + (mRturn.getMin() + (mRturn.getDuration() % 60)));
            holder.date.setVisibility(View.VISIBLE);
            holder.date.setText(mRturn.getLongDate());
            holder.chbox.setVisibility(View.VISIBLE);
            holder.chbox.setChecked(CheckedItems.get(position));
            holder.divider.setVisibility(View.VISIBLE);

            holder.chbox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (holder.chbox.isChecked()) {
                        CheckedItems.set(position, true);
                    } else {
                        CheckedItems.set(position, false);
                    }
                 }
            });
        }
        return convertView;

//        View row = inflater.inflate(R.layout.message_spinner_item, parent, false);
//        mRturn = null;
//        mRturn = mData.get(position);
//
//        TextView name = (TextView) row.findViewById(R.id.message_spinnerItem_patientName);
//        TextView time = (TextView) row.findViewById(R.id.message_spinnerItem_time);
//        TextView date = (TextView) row.findViewById(R.id.message_spinnerItem_date);
//        CheckBox chbox = (CheckBox) row.findViewById(R.id.message_spinnerItem_chbox);
//        View divider = row.findViewById(R.id.message_spinnerItem_line);
//        chbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
//                if(checked){
//                    mTempData.set(position, true);
//                }else {
//                    mTempData.set(position, false);
//                }
//            }
//        });
//
//        if (position == 0) {
//            name.setText("لیست نوبت دهی ...");
//            name.setTextSize(16f);
//            name.setTextColor(Color.BLACK);
//            date.setVisibility(View.GONE);
//            time.setVisibility(View.GONE);
//            chbox.setVisibility(View.GONE);
//            divider.setVisibility(View.GONE);
//        } else {
//            name.setText(mRturn.getPatientFirstName() + " " + mRturn.getPatientLastName());
//            time.setText(mRturn.getHour() + " : " + mRturn.getMin() + " الی " + (mRturn.getHour() + (mRturn.getDuration() / 60)) + " : " + (mRturn.getMin() + (mRturn.getDuration() % 60)));
//            date.setText(mRturn.getLongDate());
//        }
//
//        return row;
    }

}
