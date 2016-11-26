package com.example.doctorsbuilding.nav.MainForm;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.doctorsbuilding.nav.Databases.DatabaseAdapter;
import com.example.doctorsbuilding.nav.Dr.Clinic.Office;
import com.example.doctorsbuilding.nav.G;
import com.example.doctorsbuilding.nav.MainActivity;
import com.example.doctorsbuilding.nav.PException;
import com.example.doctorsbuilding.nav.R;
import com.example.doctorsbuilding.nav.User.User;
import com.example.doctorsbuilding.nav.UserType;
import com.example.doctorsbuilding.nav.Util.MessageBox;
import com.example.doctorsbuilding.nav.Web.WebService;

import java.util.EventListener;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by hossein on 11/16/2016.
 */
public class ActivityLoading extends AppCompatActivity {
    Button btn_wifi;
    Button btn_mobiledata;
    Button btn_reconect;
    FrameLayout frm_run;
    FrameLayout frm_error;
    public UserType menu = UserType.None;
    private SharedPreferences settings;
    DatabaseAdapter database;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.app_loading_layout);
        initViews();
        eventListener();
        checkIsOnline();
        database = new DatabaseAdapter(ActivityLoading.this);
        database.initialize();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private void initViews() {
        btn_wifi = (Button) findViewById(R.id.app_loading_wifi);
        btn_mobiledata = (Button) findViewById(R.id.app_loading_mobildata);
        btn_reconect = (Button) findViewById(R.id.app_loading_reconect);
        frm_run = (FrameLayout) findViewById(R.id.app_loading_run);
        frm_error = (FrameLayout) findViewById(R.id.app_loading_error);
    }

    private void eventListener() {
        btn_wifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
            }
        });

        btn_mobiledata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS));
            }
        });
        btn_reconect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                frm_error.setVisibility(View.GONE);
                checkIsOnline();

            }
        });

    }

    private void checkIsOnline() {
        if (isOnline()) {
            loadData();
        } else {
            frm_run.setVisibility(View.GONE);
            frm_error.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {

    }

    private void loadData() {
        frm_run.setVisibility(View.VISIBLE);
        if (G.UserInfo == null)
            G.UserInfo = new User();
        loadUser();
        AsyncCallGetData task = new AsyncCallGetData();
        task.execute();
    }

    private boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private void loadUser() {

        settings = G.getSharedPreferences();
        G.UserInfo.setUserName(settings.getString("user", ""));
        G.UserInfo.setPassword(settings.getString("pass", ""));

    }

    private class AsyncCallGetData extends AsyncTask<String, Void, Void> {
        String msg = null;
        int result = 0;

        @Override
        protected Void doInBackground(String... strings) {
            try {

                if (G.UserInfo.getUserName().length() != 0 && G.UserInfo.getPassword().length() != 0) {
                    result = WebService.invokeLogin3WS(G.UserInfo.getUserName(), G.UserInfo.getPassword());
                    if (result != 0) {
//                        G.UserInfo = WebService.invokeGetUserInfoWS(G.UserInfo.getUserName(), G.UserInfo.getPassword(), G.officeId);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString("user", G.UserInfo.getUserName());
                        editor.putString("pass", G.UserInfo.getPassword());
                        editor.putInt("role", result);
                        editor.apply();
                    }
                }

            } catch (PException ex) {
                msg = ex.getMessage();

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (msg != null) {
                new MessageBox(ActivityLoading.this, msg).show();
                frm_run.setVisibility(View.GONE);
                frm_error.setVisibility(View.VISIBLE);
            } else {

//                if (result != 0) {
//                    if (result.toUpperCase().equals("OK")) {
                    startActivity(new Intent(ActivityLoading.this, ActivityOffices.class));
                    finish();
//                    } else {


//                        final MessageBox errorMessage = new MessageBox(ActivityLoading.this, result);
//                        errorMessage.setCancelable(false);
//                        errorMessage.show();
//                        errorMessage.setOnDismissListener(new DialogInterface.OnDismissListener() {
//                            @Override
//                            public void onDismiss(DialogInterface dialogInterface) {
//                                if (errorMessage.pressAcceptButton()) {
//                                    finish();
//                                }
//                            }
//                        });

//                    }
//                } else {
//                    startActivity(new Intent(ActivityLoading.this, ActivityOffices.class));
//                    finish();
//                }
//                }
            }
        }
    }
}
