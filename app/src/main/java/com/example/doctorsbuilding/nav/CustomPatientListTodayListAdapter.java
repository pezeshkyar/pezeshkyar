package com.example.doctorsbuilding.nav;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by hossein on 9/3/2016.
 */
public class CustomPatientListTodayListAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<PatientInfo> patientInfos = new ArrayList<PatientInfo>();

    public CustomPatientListTodayListAdapter(Context context) {
        this.context = context;
    }

    class Holder {
        public TextView name;
        public TextView mobile;

        public Holder(View row) {
            name = (TextView) row.findViewById(R.id.search_item_name);
            mobile = (TextView) row.findViewById(R.id.search_item_mobile);
        }
    }

    public void addAll(ArrayList<PatientInfo> items){
        patientInfos.clear();
        patientInfos.addAll(items);
        notifyDataSetChanged();
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
        holder.name.setText(patientInfos.get(position).getFirstName().concat(" " + patientInfos.get(position).getLastName()));
        holder.mobile.setText(patientInfos.get(position).getMobileNo());
        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ActivityReception.class);
                intent.putExtra("patientInfo", patientInfos.get(position));
                context.startActivity(intent);
            }
        });
        return rowView;
    }

    @Override
    public int getCount() {
        return patientInfos.size();
    }

    @Override
    public Object getItem(int position) {
        return patientInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

}
