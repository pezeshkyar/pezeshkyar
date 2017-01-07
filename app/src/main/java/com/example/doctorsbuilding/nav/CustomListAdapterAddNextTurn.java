package com.example.doctorsbuilding.nav;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.doctorsbuilding.nav.Dr.Profile.ExpChild;
import com.example.doctorsbuilding.nav.Dr.Profile.ExpGroup;
import com.example.doctorsbuilding.nav.Util.MessageBox;
import com.example.doctorsbuilding.nav.Web.WebService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hossein on 9/5/2016.
 */
public class CustomListAdapterAddNextTurn extends BaseExpandableListAdapter {
    private Context context;
    private ArrayList<ExpGroup> groups;
    private PatientInfo patientInfo;

    public CustomListAdapterAddNextTurn(Context context, ArrayList<ExpGroup> groups, PatientInfo patientInfo) {
        this.context = context;
        this.groups = groups;
        this.patientInfo = patientInfo;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        View rowView = convertView;
        ExpGroup group = (ExpGroup) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater inf = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            rowView = inf.inflate(R.layout.exp_lv_group, null);
        }
        TextView groupName = (TextView) rowView.findViewById(R.id.lbl_groupItem);
        groupName.setText(group.getName());

        if (isExpanded) {
            groupName.setTextColor(ContextCompat.getColor(context, R.color.expGroupTextColorWhenIsExpanded));
        } else {
            groupName.setTextColor(ContextCompat.getColor(context, R.color.textColor));
        }
        return rowView;
    }

    class Holder {
        TextView txtDayOfMonth;
        TextView txtCapacity;
        TextView txtTiming;
        Button addBtn;
        Button deleteBtn;
        Button fullCapacityBtn;
        Button addNobatBtn;
        Turn turn;
        int groupPosition;
        int childPosition;

        public Holder(View v) {
            addBtn = (Button) v.findViewById(R.id.addBtn);
            deleteBtn = (Button) v.findViewById(R.id.deleteBtn);
            fullCapacityBtn = (Button) v.findViewById(R.id.fullCapacityBtn);
            addNobatBtn = (Button) v.findViewById(R.id.addNobatBtn);
            txtDayOfMonth = (TextView) v.findViewById(R.id.txtTitle);
            txtCapacity = (TextView) v.findViewById(R.id.txtCapacity);
            txtTiming = (TextView) v.findViewById(R.id.txtTiming);

            addNobatBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    reservation(turn);
                }

            });
        }
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final Holder holder;
        View rowView = convertView;
        final ExpChild child = (ExpChild) getChild(groupPosition, childPosition);
        if (convertView == null) {

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.exp_lv_item, parent, false);
            holder = new Holder(rowView);
            rowView.setTag(holder);
        } else {
            holder = (Holder) rowView.getTag();
        }

        holder.groupPosition = groupPosition;
        holder.childPosition = childPosition;
        holder.turn = child.getTurn();
        holder.txtDayOfMonth.setText(child.getDate());
        holder.txtCapacity.setText(child.getCapacity());
        holder.txtTiming.setText(child.getTime());

        checkTurn(holder, child);

        return rowView;
    }

    private void checkTurn(Holder holder, ExpChild child) {

        holder.addBtn.setVisibility(View.GONE);
        holder.deleteBtn.setVisibility(View.GONE);
        if (child.getTurn().getCapacity() <= child.getTurn().getReserved()) {
            holder.addNobatBtn.setVisibility(View.GONE);
            holder.fullCapacityBtn.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        List<ExpChild> chList = groups.get(groupPosition).getItems();
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
        List<ExpChild> chList = groups.get(groupPosition).getItems();
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

    private Turn temp = null;

    private void reservation(final Turn turn) {
        temp = turn;
        asyncCallReserveForUserWS task = new asyncCallReserveForUserWS();
        task.execute();
    }

    private class asyncCallReserveForUserWS extends AsyncTask<String, Void, Void> {

        int result = 0;
        Reservation reservation = null;
        String msg = null;
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(context, "", "در حال رزرو نوبت ...");
            dialog.getWindow().setGravity(Gravity.END);
            reservation = new Reservation();
            reservation.setTurnId(temp.getId());

            if (patientInfo.getFirstReservationId() != 0)
                reservation.setFirstReservationId(patientInfo.getFirstReservationId());
            else
                reservation.setFirstReservationId(patientInfo.getReservationId());

            reservation.setTaskId(patientInfo.getTaskId());
            reservation.setNumberOfTurns(1);
            reservation.setPatientUserName(patientInfo.getUsername());
        }

        @Override
        protected Void doInBackground(String... strings) {

            try {
                result = WebService.invokeResevereForUser(G.UserInfo.getUserName(), G.UserInfo.getPassword(), reservation);
            } catch (PException ex) {
                msg = ex.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (msg != null) {
                dialog.dismiss();
                new MessageBox(context, msg).show();
            } else {
                if (result > 0) {
                    temp.setReserved(temp.getReserved() + 1);
                    temp.setIsReserved(true);
                    notifyDataSetChanged();
                    dialog.dismiss();
                    Toast.makeText(context, "ثبت نوبت با موفقیت انجام شد .", Toast.LENGTH_SHORT).show();
                } else {
                    dialog.dismiss();
                    new MessageBox(context, "ثبت نوبت با مشکل مواجه شد !").show();
                }
            }
        }
    }
}
