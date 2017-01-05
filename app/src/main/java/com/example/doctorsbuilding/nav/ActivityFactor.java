package com.example.doctorsbuilding.nav;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.doctorsbuilding.nav.Util.Util;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by hossein on 12/20/2016.
 */
public class ActivityFactor extends AppCompatActivity {
    private ImageButton backBtn;
    private Button btnPayment;
    private TextView pageTitle;
    private TextView patientName;
    private TextView doctorName;
    private TextView date;
    private TextView time;
    private TextView payment;
    private int requestCode;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        G.setStatusBarColor(ActivityFactor.this);

        setContentView(R.layout.activity_factor_layout);
        requestCode = getIntent().getIntExtra("requestCode", -1);
        intiViews();
        eventlistener();

    }

    private void intiViews() {
        pageTitle = (TextView) findViewById(R.id.toolbar_title);
        pageTitle.setText("پیش فاکتور");
        backBtn = (ImageButton) findViewById(R.id.toolbar_backBtn);
        btnPayment = (Button) findViewById(R.id.factor_btn_payment);
        patientName = (TextView) findViewById(R.id.factor_name);
        doctorName = (TextView) findViewById(R.id.factor_drname);
        date = (TextView) findViewById(R.id.factor_date);
        time = (TextView) findViewById(R.id.factor_time);
        payment = (TextView) findViewById(R.id.factor_payment);

        if (G.reservationInfo.getPatientFirstName() == null || G.reservationInfo.getPatientLastName() == null)
            patientName.setText(G.UserInfo.getFirstName().concat(" ").concat(G.UserInfo.getLastName()));

        else
            patientName.setText(G.reservationInfo.getPatientFirstName().concat(" ").concat(G.reservationInfo.getPatientLastName()));

        doctorName.setText(G.officeInfo.getFirstname().concat(" ").concat(G.officeInfo.getLastname()));
        date.setText(G.reservationInfo.getDate());
        time.setText(G.reservationInfo.getTime());
        payment.setText(Util.getCurrency(G.reservationInfo.getPrice()).concat("  ریال"));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void eventlistener() {
        btnPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final DialogPayType dialogPayType = new DialogPayType(ActivityFactor.this);
                dialogPayType.show();
                dialogPayType.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        if(dialogPayType.getPayWay() == 0){
                            //SAMAN BANK
                            Intent intent = new Intent(ActivityFactor.this, ActivityPaymnet.class);
                            intent.putExtra("amount", G.reservationInfo.getPrice());
                            intent.putExtra("requestCode", requestCode);
                            startActivityForResult(intent, requestCode);
                        }else if(dialogPayType.getPayWay() == 1){
                            //wallet
                            setResult(requestCode);
                            finish();
                        }
                    }
                });
            }
        });
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        setResult(resultCode);
        finish();
    }
}
