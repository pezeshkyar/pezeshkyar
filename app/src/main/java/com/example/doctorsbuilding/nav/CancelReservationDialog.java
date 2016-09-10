package com.example.doctorsbuilding.nav;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.doctorsbuilding.nav.Util.MessageBox;
import com.example.doctorsbuilding.nav.Web.WebService;

import java.util.ArrayList;

/**
 * Created by hossein on 8/1/2016.
 */
public class CancelReservationDialog extends Dialog {

    private Context context;
    private int turnId;
    private boolean cancleResult = false;
    private int selectedItem = -1;
    private ListView mListView;
    private ArrayList<Reservation> reservations;

    public CancelReservationDialog(Context context, int turnId) {
        super(context);
        this.context = context;
        this.turnId = turnId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.cancel_reservayion_layout);
        initViews();
        eventListener();
    }

    private void initViews() {
        mListView = (ListView) findViewById(R.id.cancelReservationListView);
        asyncCallGetReservationByTurnIdWS task = new asyncCallGetReservationByTurnIdWS();
        task.execute();
    }

    private void eventListener() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                final MessageBox message = new MessageBox(context, "شما در حال حذف این نوبت می باشید !");
                message.show();

                message.setOnDismissListener(new OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        if (message.pressAcceptButton()) {
                            selectedItem = position;
                            asyncCallCancelReservationWS task = new asyncCallCancelReservationWS();
                            task.execute();
                        }
                    }
                });

            }
        });
    }

    public boolean getCancelationResult() {
        return cancleResult;
    }

    private class asyncCallGetReservationByTurnIdWS extends AsyncTask<String, Void, Void> {
        Reservation reservation = null;
        String msg = null;
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(context, "", "در حال دریافت اطلاعات ...");
            dialog.getWindow().setGravity(Gravity.END);
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                reservations = WebService.invokeGetReservationByTurnIdWS(G.UserInfo.getUserName(), G.UserInfo.getPassword(), G.officeId, turnId);
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
                if (reservations.size() != 0) {
                    ArrayList<String> userInfo = null;
                    ArrayList<ArrayList<String>> users = new ArrayList<ArrayList<String>>();
                    for (Reservation res : reservations) {
                        userInfo = new ArrayList<String>();
                        userInfo.add(res.getPatientFirstName() + " " + res.getPatientLastName());
                        userInfo.add(res.getPatientPhoneNo());
                        users.add(userInfo);
                    }
                    mListView.setAdapter(new CustomReservationListAdapter(getContext(), users, turnId));
                }else {
                    Toast.makeText(context, "هیچ موردی یافت نشده است .",Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }
        }
    }

    private class asyncCallCancelReservationWS extends AsyncTask<String, Void, Void> {
        boolean result = false;
        String msg = null;
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(context, "", "در حال حذف نوبت ...");
            dialog.getWindow().setGravity(Gravity.END);
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                result = WebService.invokeCancleReservation(G.UserInfo.getUserName(), G.UserInfo.getPassword(), reservations.get(selectedItem).getId());
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
                    cancleResult = result;
                    dialog.dismiss();
                    Toast.makeText(context, "حذف نوبت با موفقیت انجام شد .", Toast.LENGTH_SHORT).show();
                    dismiss();
                } else {
                    dialog.dismiss();
                    new MessageBox(context, "عملیات حذف با مشکل مواجه شد !").show();
                }
            }
        }
    }
}
