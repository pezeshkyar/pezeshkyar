package com.example.doctorsbuilding.nav.Dr.Notification;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.doctorsbuilding.nav.CustomAdapterSpinner;
import com.example.doctorsbuilding.nav.G;
import com.example.doctorsbuilding.nav.PException;
import com.example.doctorsbuilding.nav.R;
import com.example.doctorsbuilding.nav.Rturn;
import com.example.doctorsbuilding.nav.Util.CustomDatePickerDialog;
import com.example.doctorsbuilding.nav.Util.MessageBox;
import com.example.doctorsbuilding.nav.Web.WebService;

import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by hossein on 6/8/2016.
 */
public class ManagementNotificationActivity extends AppCompatActivity {

    TextView fromDate;
    TextView toDate;
    String shortFromDate;
    String shortToDate;
    Spinner mSpinner;
    EditText message;
    Button btnSend;
    Button btnShow;
    private ArrayList<Boolean> checkedItems = null;
    CustomAdapterSpinner mAdpater = null;
    private ArrayList<Rturn> mRturns = null;
    ImageButton backBtn;
    TextView pageTitle;
    asyncCallGetPatientTurnInfoByDateWS task_getPatientTurnInfoByDateWS = null;
    asyncCallSendMessageBatchWS task_sendMessageBatchWS = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        G.setStatusBarColor(ManagementNotificationActivity.this);
        setContentView(R.layout.activity_notification_management);
        initViews();
        eventListener();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (task_getPatientTurnInfoByDateWS != null)
            task_getPatientTurnInfoByDateWS.cancel(true);
        if (task_sendMessageBatchWS != null)
            task_sendMessageBatchWS.cancel(true);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private void initViews() {
        pageTitle = (TextView)findViewById(R.id.toolbar_title);
        pageTitle.setText("ارسال پیام");
        backBtn = (ImageButton) findViewById(R.id.toolbar_backBtn);
        fromDate = (TextView) findViewById(R.id.manageNotify_spinner_fromDate);
        toDate = (TextView) findViewById(R.id.manageNotify_spinner_toDate);
        message = (EditText) findViewById(R.id.manageNotify_message);
        mSpinner = (Spinner) findViewById(R.id.manageNotify_spinner_nobat);
        btnSend = (Button) findViewById(R.id.manageNotify_btn_send);
        btnShow = (Button) findViewById(R.id.manageNotify_show_nobat);

    }

    private void eventListener() {
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ManagementNotificationActivity.this.onBackPressed();
            }
        });
        fromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setFromDate();
            }
        });
        toDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setToDate();
            }
        });
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkFieldsSend()) {
                    task_sendMessageBatchWS = new asyncCallSendMessageBatchWS();
                    task_sendMessageBatchWS.execute("", message.getText().toString().trim());
                }

            }
        });

        btnShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkFieldsShow()) {
                    if (fromDate.getText().toString().trim().length() != 0 && toDate.getText().toString().trim().length() != 0) {
                        task_getPatientTurnInfoByDateWS = new asyncCallGetPatientTurnInfoByDateWS();
                        task_getPatientTurnInfoByDateWS.execute(shortFromDate, shortToDate);
                    }
                }
            }
        });

    }

    private void setFromDate() {
        final CustomDatePickerDialog datePicker = new CustomDatePickerDialog(this);
        datePicker.show();
        datePicker.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                if (datePicker.BUTTON_TYPE == Dialog.BUTTON_POSITIVE) {
                    fromDate.setText(datePicker.getDate());
                    shortFromDate = datePicker.getShortDate();
                }
            }
        });

    }

    private void setToDate() {
        final CustomDatePickerDialog datePicker = new CustomDatePickerDialog(this);
        datePicker.show();
        datePicker.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                if (datePicker.BUTTON_TYPE == Dialog.BUTTON_POSITIVE) {
                    toDate.setText(datePicker.getDate());
                    shortToDate = datePicker.getShortDate();
                }
            }
        });

    }

    private boolean checkFieldsShow() {
        if (fromDate.getText().toString().trim().equals("")) {
            new MessageBox(ManagementNotificationActivity.this, "تاریخ شروع نوبت دهی مشخص نشده است !").show();
            return false;
        }
        if (toDate.getText().toString().trim().equals("")) {
            new MessageBox(ManagementNotificationActivity.this, "تاریخ پایان نوبت دهی مشخص نشده است !").show();
            return false;
        }
        if (!IsEndDateGreatherThanStartDate(shortToDate, shortFromDate)) {
            new MessageBox(ManagementNotificationActivity.this, "تاریخ پایان نوبت دهی باید بزرگتر از تاریخ شروع ان باشد !").show();
            return false;
        }
        return true;
    }

    private boolean checkFieldsSend() {
        if (message.getText().toString().trim().equals("")) {
            new MessageBox(ManagementNotificationActivity.this, "لطفا متن پیغام را وارد نمایید !").show();
            return false;
        }
        if (mRturns == null) {
            new MessageBox(ManagementNotificationActivity.this, "گیرنده پیام مشخص نشده است ! ").show();
            return false;
        }
        if (mRturns.size() == 0) {
            new MessageBox(ManagementNotificationActivity.this, "گیرنده پیام مشخص نشده است ! ").show();
            return false;
        }
        checkedItems = mAdpater.getUserForMessage();
        boolean isEmpty = true;
        for (int i = 1; i < checkedItems.size(); i++) {
            if (checkedItems.get(i)) {
                isEmpty = false;
                break;
            }
        }
        if (isEmpty) {
            new MessageBox(ManagementNotificationActivity.this, "گیرنده پیام مشخص نشده است ! ").show();
            return false;
        }
        return true;
    }

    private boolean IsEndDateGreatherThanStartDate(String fromDate, String toDate) {

        String[] a_startDate = fromDate.split("/");
        String[] a_endDate = toDate.split("/");

        int y = Integer.parseInt(a_startDate[0]);
        int m = Integer.parseInt(a_startDate[1]);
        int d = Integer.parseInt(a_startDate[2]);

        int y1 = Integer.parseInt(a_endDate[0]);
        int m1 = Integer.parseInt(a_endDate[1]);
        int d1 = Integer.parseInt(a_endDate[2]);
        if (y > y1)
            return true;
        else if (y < y1)
            return false;
        else {
            if (m > m1)
                return true;
            else if (m < m1)
                return false;
            else {
                if (d > d1)
                    return true;
                else if (d < d1)
                    return false;
                else
                    return true;
            }
        }
    }

    private class asyncCallGetPatientTurnInfoByDateWS extends AsyncTask<String, Void, Void> {
        String msg = null;
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(ManagementNotificationActivity.this, "", "در حال دریافت اطلاعات ...");
            dialog.getWindow().setGravity(Gravity.END);
            dialog.setCancelable(true);
            btnShow.setClickable(false);
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                mRturns = WebService.invokeGetPatientTurnInfoByDate(G.UserInfo.getUserName(), G.UserInfo.getPassword(), G.officeId
                        , strings[0], strings[1]);
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
                new MessageBox(ManagementNotificationActivity.this, msg).show();
            } else {
                if (mRturns == null || mRturns.size() == 0) {
                    dialog.dismiss();
                    Toast.makeText(ManagementNotificationActivity.this, "هیچ موردی در این تاریخ پیدا نشده است .", Toast.LENGTH_SHORT).show();
                } else {
                    mRturns.add(0, new Rturn());
                    if (mRturns != null && mRturns.size() != 0) {
                        checkedItems = new ArrayList<Boolean>();
                        for (int i = 0; i < mRturns.size(); i++) {
                            checkedItems.add(true);
                        }
                        mAdpater = new CustomAdapterSpinner(ManagementNotificationActivity.this, R.layout.message_spinner_item, mRturns, getResources(), checkedItems);
                        mSpinner.setAdapter(mAdpater);
                    }
                    mSpinner.performClick();
                    dialog.dismiss();
                }
            }
            btnShow.setClickable(true);
        }
    }

    private class asyncCallSendMessageBatchWS extends AsyncTask<String, Void, Void> {

        ArrayList<String> receivers = null;
        ArrayList<String> phoneNos = null;
        Boolean result = false;
        String msg = null;
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(ManagementNotificationActivity.this, "", "در حال ارسال اطلاعات ...");
            dialog.getWindow().setGravity(Gravity.END);
            dialog.setCancelable(true);
            btnSend.setClickable(false);
            btnShow.setClickable(false);

            receivers = new ArrayList<String>();
            phoneNos = new ArrayList<String>();
            for (int i = 1; i < checkedItems.size(); i++) {
                if (checkedItems.get(i)) {
                    if (mRturns.get(i).getPatientUsername().equals(""))
                        receivers.add(mRturns.get(i).getUsername());
                    else
                        receivers.add(mRturns.get(i).getPatientUsername());


                    phoneNos.add(mRturns.get(i).getPatientPhoneNo());
                }
            }
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                result = WebService.invokeSendMessageBatchWS(G.UserInfo.getUserName(), G.UserInfo.getPassword(), G.officeId
                        , receivers, phoneNos, strings[0], strings[1]);
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
                new MessageBox(ManagementNotificationActivity.this, msg).show();
            } else {
                dialog.dismiss();
                if (result) {
                    Toast.makeText(ManagementNotificationActivity.this, "ارسال پیام با موفقیت انجام شد .", Toast.LENGTH_SHORT).show();
                }
            }
            btnSend.setClickable(true);
            btnShow.setClickable(true);
        }
    }
}
