package com.example.doctorsbuilding.nav;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.doctorsbuilding.nav.Dr.Profile.ExpChild;
import com.example.doctorsbuilding.nav.Dr.Profile.ExpGroup;
import com.example.doctorsbuilding.nav.Util.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hossein on 9/4/2016.
 */
public class PatientFileListAdapter extends BaseExpandableListAdapter {
    private Context context;
    private ArrayList<PatientFileGroup> groups;


    public PatientFileListAdapter(Context context, ArrayList<PatientFileGroup> groups) {
        this.context = context;
        this.groups = groups;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        View rowView = convertView;
        PatientFileGroup group = (PatientFileGroup) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater inf = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            rowView = inf.inflate(R.layout.patient_file_group, null);
        }
        TextView groupTaskName = (TextView) rowView.findViewById(R.id.patient_file_task);
        groupTaskName.setText(group.getTaskName());
        TextView groupPrice = (TextView) rowView.findViewById(R.id.patient_file_price);
        groupPrice.setText(group.getPriceString());
        ImageView arrow_group = (ImageView)rowView.findViewById(R.id.patient_file_arrow);
        if(isExpanded){
            arrow_group.setImageResource(R.drawable.ic_expand_less);
        }else{
            arrow_group.setImageResource(R.drawable.ic_expand_more);
        }

        return rowView;
    }

    class Holder {
        TextView date;
        TextView time;
        TextView payment;
        TextView remain;
        TextView description;

        public Holder(View v) {
            date = (TextView) v.findViewById(R.id.patient_file_date);
            time = (TextView) v.findViewById(R.id.patient_file_time);
            payment = (TextView) v.findViewById(R.id.patient_file_pay);
            remain = (TextView) v.findViewById(R.id.patient_file_remain);
            description = (TextView) v.findViewById(R.id.patient_file_description);
        }
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final Holder holder;
        View rowView = convertView;
        final PatientFileChild child = (PatientFileChild) getChild(groupPosition, childPosition);
        if (convertView == null) {

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.patient_file_item, parent, false);
            holder = new Holder(rowView);
            rowView.setTag(holder);
        } else {
            holder = (Holder) rowView.getTag();
        }

        holder.date.setText(child.getLongDate());
        holder.time.setText(child.getTime());
        holder.payment.setText(Util.getCurrency(child.getPayment()));
        holder.remain.setText(Util.getCurrency(child.getRemain()));
        holder.description.setText(child.getDescription());

        return rowView;
    }


    @Override
    public Object getChild(int groupPosition, int childPosition) {
        List<PatientFileChild> chList = groups.get(groupPosition).getChilds();
        return chList.get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public void onGroupCollapsed(int groupPosition) {
        super.onGroupCollapsed(groupPosition);
    }

    @Override
    public void onGroupExpanded(int groupPosition) {
        super.onGroupExpanded(groupPosition);
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        List<PatientFileChild> chList = groups.get(groupPosition).getChilds();
        return chList.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groups.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return groups.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

}
