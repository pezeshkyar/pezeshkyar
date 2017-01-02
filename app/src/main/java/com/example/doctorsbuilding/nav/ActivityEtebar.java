package com.example.doctorsbuilding.nav;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.doctorsbuilding.nav.Dr.Clinic.Office;
import com.example.doctorsbuilding.nav.Util.MessageBox;
import com.example.doctorsbuilding.nav.Util.MoneyTextWatcher;
import com.example.doctorsbuilding.nav.Util.Util;
import com.example.doctorsbuilding.nav.Web.WebService;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by hossein on 12/31/2016.
 */
public class ActivityEtebar extends AppCompatActivity {
    private TextView etebarFeli;
    private EditText txtAmount;
    private Button btnPay;
    TextView pageTitle;
    int amount = -1;
    ImageButton backBtn;
    private AsyncGetWallet getWallet;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_etebar);
        pageTitle = (TextView) findViewById(R.id.toolbar_title);
        pageTitle.setText(Util.getStringWS(R.string.etebar_title));
        backBtn = (ImageButton) findViewById(R.id.toolbar_backBtn);
        etebarFeli = (TextView) findViewById(R.id.etebar);
        txtAmount = (EditText) findViewById(R.id.etebar_amount);
        btnPay = (Button) findViewById(R.id.etebar_btn);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!txtAmount.getText().toString().trim().isEmpty()) {
                    Intent intent = new Intent(ActivityEtebar.this, ActivityPaymnet.class);
                    intent.putExtra("amount", Integer.valueOf(Util.getNumber(txtAmount.getText().toString().trim())));
                    intent.putExtra("requestCode", 1002);
                    startActivityForResult(intent, 1002);
                } else {
                    new MessageBox(ActivityEtebar.this, Util.getStringWS(R.string.etebar_err)).show();
                }
            }
        });
        txtAmount.addTextChangedListener(new MoneyTextWatcher(txtAmount));

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (getWallet != null) {
            getWallet.cancel(true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWallet = new AsyncGetWallet();
        getWallet.execute();

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private class AsyncGetWallet extends AsyncTask<String, Void, Void> {
        String msg = null;
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(ActivityEtebar.this, "", "لطفاً شکیبا باشید ...");
            dialog.show();
            dialog.getWindow().setGravity(Gravity.END);
            dialog.setCancelable(true);
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                amount = WebService.invokeGetWalletWS(G.UserInfo.getUserName(), G.UserInfo.getPassword());
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
                new MessageBox(ActivityEtebar.this, msg).show();
            } else {
                dialog.dismiss();
                if (amount != -1) {
                    etebarFeli.setText(Util.getStringWS(R.string.etebar_p1).concat(" ")
                            .concat(String.valueOf(amount)).concat(" ")
                            .concat(Util.getStringWS(R.string.etebar_p2)));
                }
            }
        }
    }

}
