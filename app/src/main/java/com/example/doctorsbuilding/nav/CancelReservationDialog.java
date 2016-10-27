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
import android.widget.ProgressBar;
import android.widget.TextView;
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
    private TextView nothingTxt;
    private ArrayList<Reservation> reservations;
    private ProgressBar progressBar;

    asyncCallGetReservationByTurnIdWS getReservationByTurnIdTask;
    asyncCallCancelReservationWS cancelReservationTask;

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
        progressBar = (ProgressBar)findViewById(R.id.cancelReservation_Progress);
        nothingTxt = (TextView)findViewById(R.id.cancelReservation_nothing);
        getReservationByTurnIdTask = new asyncCallGetReservationByTurnIdWS();
        getReservationByTurnIdTask.execute();
    }


    @Override
    public void dismiss() {
        super.dismiss();
        if(getReservationByTurnIdTask != null){
            getReservationByTurnIdTask.cancel(true);
        }
        if(cancelReservationTask != null){
            cancelReservationTask.cancel(true);
        }
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
                            cancelReservationTask = new asyncCallCancelReservationWS();
                            cancelReservationTask.execute();
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
                    progressBar.setVisibility(View.GONE);
                }else {
                    progressBar.setVisibility(View.GONE);
                    nothingTxt.setVisibility(View.VISIBLE);
                }
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
            dialog.setCancelable(true);
            dialog.getWindow().setGravity(Gravity.END);
            mListView.setEnabled(false);
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
            mListView.setEnabled(true);
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
