package com.example.doctorsbuilding.nav;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.PopupMenu;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.doctorsbuilding.nav.Dr.Profile.DialogChangePassword;
import com.example.doctorsbuilding.nav.Util.MessageBox;
import com.example.doctorsbuilding.nav.Util.MoneyTextWatcher;
import com.example.doctorsbuilding.nav.Util.Util;
import com.example.doctorsbuilding.nav.Web.Hashing;
import com.example.doctorsbuilding.nav.Web.WebService;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by hossein on 9/3/2016.
 */
public class ActivityReception extends AppCompatActivity {
    private TextView nameTxt;
    private TextView taskTxt;
    private TextView txtPayment;
    private EditText costTxt;
    private EditText detailsTxt;
    private Button insertBtn;
    private ImageButton backBtn;
    private ImageButton btn_menu;
    TextView pageTitle;
    private PatientInfo patientInfo = null;
    asyncCallReception task_reception = null;
    private PopupMenu popupMenu;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        G.setStatusBarColor(ActivityReception.this);
        setContentView(R.layout.activity_reception);
        initViews();
        eventListener();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (task_reception != null)
            task_reception.cancel(true);

    }

    private void initViews() {
        popupMenu = new PopupMenu(ActivityReception.this, btn_menu);
        popupMenu.inflate(R.menu.menu_reception);
        btn_menu = (ImageButton) findViewById(R.id.menu_toolbar_setting);
        nameTxt = (TextView) findViewById(R.id.reception_name);
        txtPayment = (TextView) findViewById(R.id.reception_payment);
        taskTxt = (TextView) findViewById(R.id.reception_task);
        costTxt = (EditText) findViewById(R.id.reception_price);
        costTxt.setRawInputType(Configuration.KEYBOARD_QWERTY);
        detailsTxt = (EditText) findViewById(R.id.reception_detail);
        insertBtn = (Button) findViewById(R.id.reception_addBtn);
        pageTitle = (TextView) findViewById(R.id.menu_toolbar_Title);
        pageTitle.setText("پذیرش");
        backBtn = (ImageButton) findViewById(R.id.menu_toolbar_backBtn);
        patientInfo = (PatientInfo) getIntent().getSerializableExtra("patientInfo");
        if (patientInfo != null) {
            nameTxt.setText(patientInfo.getFirstName().concat(" " + patientInfo.getLastName()));
            taskTxt.setText(patientInfo.getTaskGroupName().concat(" - ").concat(patientInfo.getTaskName()));

            txtPayment.setText("مبلغ  ".concat(Util.getCurrency(patientInfo.getPayment())).concat("  ریال توسط بیمار پرداخت شده است ."));

            if (!patientInfo.getDescription().equals("") && !patientInfo.getDescription().equals("anyType{}"))
                detailsTxt.setText(patientInfo.getDescription());
        }

    }

    private void eventListener() {
        btn_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MenuPopupHelper menuHelper = new MenuPopupHelper(ActivityReception.this, (MenuBuilder) popupMenu.getMenu(), btn_menu);
                menuHelper.setForceShowIcon(true);
                menuHelper.show();
            }
        });
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.reception_next_turn:
                        Intent intent = new Intent(ActivityReception.this, ActivityAddNextTurn.class);
                        intent.putExtra("patientInfo", patientInfo);
                        startActivity(intent);
                        break;
                    case R.id.reception_patient_file:
                        Intent intent1 = new Intent(ActivityReception.this, ActivityPatientFile.class);
                        intent1.putExtra("patientUserName", patientInfo.getUsername());
                        startActivity(intent1);
                        break;
                }
                return false;
            }
        });
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        insertBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean res = true;
                try {
                    if (!costTxt.getText().toString().trim().isEmpty()) {
                        int temp = Integer.valueOf(Util.getNumber(costTxt.getText().toString()));
                    }
                } catch (Exception ex) {
                    new MessageBox(ActivityReception.this, "مبلغ وارد شده نادرست می باشد .").show();
                    res = false;
                }
                if (res) {
                    task_reception = new asyncCallReception();
                    task_reception.execute();
                }
            }
        });

        costTxt.addTextChangedListener(new MoneyTextWatcher(costTxt));
    }


    private class asyncCallReception extends AsyncTask<String, Void, Void> {

        ProgressDialog dialog;
        boolean result = false;
        int reservationId, payment;
        String description;
        String msg = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(ActivityReception.this, "", "در حال ثبت اطلاعات ...");
            dialog.getWindow().setGravity(Gravity.END);
            reservationId = patientInfo.getReservationId();
            dialog.setCancelable(true);
            insertBtn.setClickable(false);
            if (costTxt.getText().toString().trim().equals("")) {
                payment = patientInfo.getPayment();
            } else {
                payment = Integer.parseInt(Util.getNumber(costTxt.getText().toString().trim())) + patientInfo.getPayment();
            }
            description = detailsTxt.getText().toString().trim();
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                result = WebService.invokeReceptionWS(G.UserInfo.getUserName(), G.UserInfo.getPassword(), G.officeId,
                        reservationId, payment, description);
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
                new MessageBox(ActivityReception.this, msg).show();
            } else {
                dialog.dismiss();
                Toast.makeText(ActivityReception.this, "ثبت اطلاعات با موفقیت انجام شده است .", Toast.LENGTH_SHORT).show();
            }
            insertBtn.setClickable(true);
        }
    }

}
