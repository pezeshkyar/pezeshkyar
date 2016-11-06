package com.example.doctorsbuilding.nav.Dr.Nobat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.doctorsbuilding.nav.CancelReservationDialog;
import com.example.doctorsbuilding.nav.Dr.Profile.ExpChild;
import com.example.doctorsbuilding.nav.G;
import com.example.doctorsbuilding.nav.PException;
import com.example.doctorsbuilding.nav.R;
import com.example.doctorsbuilding.nav.Turn;
import com.example.doctorsbuilding.nav.Util.MessageBox;
import com.example.doctorsbuilding.nav.Web.WebService;

import java.util.ArrayList;

/**
 * Created by hossein on 5/30/2016.
 */
public class CustomListAdapterNobat extends BaseAdapter {

    private Context context;
    private ArrayList<Turn> turns;

    public CustomListAdapterNobat(Context context, ArrayList<Turn> turns) {
        this.context = context;
        this.turns = turns;
    }

    @Override
    public int getCount() {
        return turns.size();
    }

    @Override
    public Object getItem(int position) {
        return turns.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    class Holder {
        TextView date;
        TextView time;
        TextView capacity;
        Button addBtn;
        Button deleteButton;
        Button fullCapacityBtn;
        Button addNobatBtn;
        Turn turn;

        public Holder(View view, final int position) {
            date = (TextView) view.findViewById(R.id.txtTitle);
            time = (TextView) view.findViewById(R.id.txtTiming);
            capacity = (TextView) view.findViewById(R.id.txtCapacity);
            addBtn = (Button) view.findViewById(R.id.addBtn);
            deleteButton = (Button) view.findViewById(R.id.deleteBtn);
            fullCapacityBtn = (Button) view.findViewById(R.id.fullCapacityBtn);
            addNobatBtn = (Button) view.findViewById(R.id.addNobatBtn);
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    final MessageBox messageBox = new MessageBox(context, "شما در حال حذف این نوبت می باشید !");
                    messageBox.show();
                    messageBox.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                            if (messageBox.pressAcceptButton()) {
                                asyncCallRemoveTurn task = new asyncCallRemoveTurn();
                                task.execute(String.valueOf(turn.getId()), String.valueOf(position));
                            }
                        }
                    });
                }
            });
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    final CancelReservationDialog dialog = new CancelReservationDialog(context, turn.getId());
                    dialog.show();
                    dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                            if (dialog.getCancelationResult()) {
                                turn.setReserved(turn.getReserved() - 1);
                                notifyDataSetChanged();
                            }
                        }
                    });
                }
            });
        }
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        Holder holder;
        View rowView = convertView;
        ExpChild child = new ExpChild(turns.get(position));
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.exp_lv_item, null);
            holder = new Holder(rowView, position);
            rowView.setTag(holder);
        } else {
            holder = (Holder) rowView.getTag();
        }
        holder.addBtn.setVisibility(View.GONE);
        holder.addNobatBtn.setVisibility(View.GONE);
        holder.fullCapacityBtn.setVisibility(View.GONE);
        holder.deleteButton.setVisibility(View.VISIBLE);
        holder.turn = child.getTurn();
        holder.date.setText(child.getDate());
        holder.capacity.setText(child.getCapacity());
        holder.time.setText(child.getTime());
        rowView.setBackgroundResource(R.drawable.layout_shadow);

        return rowView;
    }

    private class asyncCallRemoveTurn extends AsyncTask<String, Void, Void> {

        boolean result = false;
        int position;
        String msg = null;
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(context, "", "در حال لغو نوبت ...");
            dialog.getWindow().setGravity(Gravity.END);
            dialog.setCancelable(true);
        }

        @Override
        protected Void doInBackground(String... strings) {
            int turnId = Integer.valueOf(strings[0]);
            position = Integer.valueOf(strings[1]);
            try {
                result = WebService.invokeRemoveTurnWS(G.UserInfo.getUserName(), G.UserInfo.getPassword(), G.officeId, turnId);
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
                if (result) {
                    dialog.dismiss();
                    Toast.makeText(context, "حذف نوبت با موفقیت انجام شد .", Toast.LENGTH_SHORT).show();
                    turns.remove(position);
                    notifyDataSetChanged();
                } else {
                    dialog.dismiss();
                    new MessageBox(context, "حذف نوبت با مشکل مواجه شد !").show();
                }
            }
        }
    }


}
