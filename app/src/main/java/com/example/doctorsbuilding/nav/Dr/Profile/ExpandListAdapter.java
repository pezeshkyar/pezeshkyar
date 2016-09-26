package com.example.doctorsbuilding.nav.Dr.Profile;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.doctorsbuilding.nav.CancelReservationDialog;
import com.example.doctorsbuilding.nav.DialogAddTurn;
import com.example.doctorsbuilding.nav.G;
import com.example.doctorsbuilding.nav.R;
import com.example.doctorsbuilding.nav.Turn;
import com.example.doctorsbuilding.nav.UserType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hossein on 5/3/2016.
 */
public class ExpandListAdapter extends BaseExpandableListAdapter {
    private Context context;
    private ArrayList<ExpGroup> groups;
    private DialogAddTurn dialogAddTurn = null;

    public ExpandListAdapter(Context context, ArrayList<ExpGroup> groups) {
        this.context = context;
        this.groups = groups;
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
        ImageView expand_more = (ImageView) rowView.findViewById(R.id.turns_ic_expand_more);
        ImageView expand_less = (ImageView) rowView.findViewById(R.id.turns_ic_expand_less);

        groupName.setText(group.getName());

        if (isExpanded) {
            expand_more.setVisibility(View.GONE);
            expand_less.setVisibility(View.VISIBLE);
            groupName.setTextColor(ContextCompat.getColor(context, R.color.expGroupTextColorWhenIsExpanded));
        } else {
            expand_less.setVisibility(View.GONE);
            expand_more.setVisibility(View.VISIBLE);
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


            addBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    reservation(turn);
                }
            });
            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cancelReservation(turn);
                }
            });
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


//        if (child.getTurn().getReserved() > 0) {
//            holder.addNobatBtn.setVisibility(View.INVISIBLE);
//            holder.fullCapacityBtn.setVisibility(View.INVISIBLE);
//            holder.addBtn.setVisibility(View.VISIBLE);
//            holder.deleteBtn.setVisibility(View.VISIBLE);
//
//
//        }
//        if (child.getTurn().getCapacity() <= child.getTurn().getReserved()) {
//            holder.addBtn.setVisibility(View.INVISIBLE);
//            holder.addNobatBtn.setVisibility(View.INVISIBLE);
//            holder.deleteBtn.setVisibility(View.INVISIBLE);
//            holder.fullCapacityBtn.setVisibility(View.VISIBLE);
//
//        } else {
//
//            holder.deleteBtn.setVisibility(View.INVISIBLE);
//            holder.fullCapacityBtn.setVisibility(View.INVISIBLE);
//            holder.addBtn.setVisibility(View.INVISIBLE);
//            holder.addNobatBtn.setVisibility(View.VISIBLE);
//        }

        return rowView;
    }

    private void checkTurn(Holder holder, ExpChild child) {

        if (G.UserInfo.getRole() == UserType.Dr.ordinal() || G.UserInfo.getRole() == UserType.secretary.ordinal()) {
            if (child.getTurn().getReserved() > 0) {
                holder.addNobatBtn.setVisibility(View.INVISIBLE);
                holder.fullCapacityBtn.setVisibility(View.INVISIBLE);
                holder.addBtn.setVisibility(View.VISIBLE);
                holder.deleteBtn.setVisibility(View.VISIBLE);


            } else {
                holder.deleteBtn.setVisibility(View.INVISIBLE);
                holder.fullCapacityBtn.setVisibility(View.INVISIBLE);
                holder.addBtn.setVisibility(View.INVISIBLE);
                holder.addNobatBtn.setVisibility(View.VISIBLE);
            }
            if (child.getTurn().getCapacity() <= child.getTurn().getReserved()) {
                holder.addBtn.setVisibility(View.INVISIBLE);
                holder.addNobatBtn.setVisibility(View.INVISIBLE);
                holder.deleteBtn.setVisibility(View.VISIBLE);
                holder.fullCapacityBtn.setVisibility(View.INVISIBLE);
            }

        } else {
            if (child.getTurn().getReserved() > 0) {
                if (child.getTurn().getIsReserved()) {
                    holder.addNobatBtn.setVisibility(View.INVISIBLE);
                    holder.fullCapacityBtn.setVisibility(View.INVISIBLE);
                    holder.addBtn.setVisibility(View.VISIBLE);
                    holder.deleteBtn.setVisibility(View.VISIBLE);
                } else {
                    holder.addNobatBtn.setVisibility(View.VISIBLE);
                    holder.fullCapacityBtn.setVisibility(View.INVISIBLE);
                    holder.addBtn.setVisibility(View.INVISIBLE);
                    holder.deleteBtn.setVisibility(View.INVISIBLE);
                }
            } else {
                holder.deleteBtn.setVisibility(View.INVISIBLE);
                holder.fullCapacityBtn.setVisibility(View.INVISIBLE);
                holder.addBtn.setVisibility(View.INVISIBLE);
                holder.addNobatBtn.setVisibility(View.VISIBLE);
            }
            if (child.getTurn().getCapacity() <= child.getTurn().getReserved()) {

                if (child.getTurn().getIsReserved()) {
                    holder.addBtn.setVisibility(View.INVISIBLE);
                    holder.addNobatBtn.setVisibility(View.INVISIBLE);
                    holder.deleteBtn.setVisibility(View.VISIBLE);
                    holder.fullCapacityBtn.setVisibility(View.INVISIBLE);
                } else {
                    holder.addBtn.setVisibility(View.INVISIBLE);
                    holder.addNobatBtn.setVisibility(View.INVISIBLE);
                    holder.deleteBtn.setVisibility(View.INVISIBLE);
                    holder.fullCapacityBtn.setVisibility(View.VISIBLE);
                }

            }
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
        final DialogAddTurn addTurn = new DialogAddTurn(context, turn.getId());
        if (G.UserInfo.getRole() == UserType.Dr.ordinal() || G.UserInfo.getRole() == UserType.secretary.ordinal()) {
            addTurn.show();
             addTurn.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    if (addTurn.getResevationId() > 0) {
                        temp.setReserved(turn.getReserved() + 1);
                        temp.setIsReserved(true);
                        notifyDataSetChanged();
                    }
                }
            });

        } else if (G.UserInfo.getRole() == UserType.User.ordinal()) {

            addTurn.show();
            addTurn.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    if (addTurn.getResevationId() > 0) {
                        temp.setReserved(turn.getReserved() + 1);
                        temp.setIsReserved(true);
                        notifyDataSetChanged();
                    }
                }
            });
        }
    }

    private void cancelReservation(final Turn turn) {
        temp = turn;
//        if (G.UserInfo.getRole() == UserType.Dr.ordinal() | G.UserInfo.getRole() == UserType.secretary.ordinal()) {

        final CancelReservationDialog dialog = new CancelReservationDialog(context, turn.getId());
        dialog.show();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                if (dialog.getCancelationResult()) {
                    temp.setReserved(turn.getReserved() - 1);
                    notifyDataSetChanged();
                }
            }
        });

//        } else if (G.UserInfo.getRole() == UserType.User.ordinal()) {
//
//        }
    }

}
