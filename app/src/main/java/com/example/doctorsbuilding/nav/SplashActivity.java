package com.example.doctorsbuilding.nav;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.doctorsbuilding.nav.Databases.DatabaseAdapter;
import com.example.doctorsbuilding.nav.Dr.Clinic.Office;
import com.example.doctorsbuilding.nav.User.User;
import com.example.doctorsbuilding.nav.Util.MessageBox;
import com.example.doctorsbuilding.nav.Web.WebService;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by hossein on 7/24/2016.
 */
public class SplashActivity extends AppCompatActivity {
    TextView splashTv;
    ProgressBar progressBar;
    public UserType menu = UserType.None;
    private SharedPreferences settings;
    DatabaseAdapter database;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);

        splashTv = (TextView) findViewById(R.id.splash_tv);
        progressBar = (ProgressBar) findViewById(R.id.splash_prbar);
        progressBar.setVisibility(View.VISIBLE);
        loadData();
        database = new DatabaseAdapter(SplashActivity.this);

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    @Override
    public void onBackPressed() {
        this.moveTaskToBack(true);
    }

    private void loadData() {
        splashTv.setText("در حال دریافت اطلاعات ...");
        loadUser();
        AsyncCallGetData task = new AsyncCallGetData();
        task.execute();
    }

    private void loadUser() {

        settings = G.getSharedPreferences();
        G.UserInfo.setUserName(settings.getString("user", ""));
        G.UserInfo.setPassword(settings.getString("pass", ""));
    }

    private class AsyncCallGetData extends AsyncTask<String, Void, Void> {
        String msg = null;
        Bitmap doctorPic = null;
        boolean result = true;

        @Override
        protected Void doInBackground(String... strings) {
            try {

                if (G.UserInfo.getUserName().length() != 0 && G.UserInfo.getPassword().length() != 0) {
                    G.UserInfo = WebService.invokeGetUserInfoWS(G.UserInfo.getUserName(), G.UserInfo.getPassword(), G.officeId);
                }
                doctorPic = WebService.invokeGetDoctorPicWS(G.UserInfo.getUserName(), G.UserInfo.getPassword(), G.officeId);
                G.officeInfo = WebService.invokeGetOfficeInfoWS(G.UserInfo.getUserName(), G.UserInfo.getPassword(), G.officeId);
            } catch (PException ex) {
                msg = ex.getMessage();

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (msg != null) {
                new MessageBox(SplashActivity.this, msg).show();
            } else {

                if (G.UserInfo.getRole() == UserType.None.ordinal()) {
                    result = false;
                }
                if (G.officeInfo != null) {
                    if (doctorPic == null)
                        doctorPic = BitmapFactory.decodeResource(SplashActivity.this.getResources(), R.drawable.doctor);

                    G.officeInfo.setPhoto(doctorPic);
                    database = new DatabaseAdapter(SplashActivity.this);
                    if (database.openConnection()) {
                        long result = database.insertoffice(G.officeInfo);
                        if (result == -1) {
                            database.updateOffice(G.officeId, G.officeInfo);
                        }
                        database.closeConnection();
                    }

                } else {result = false;}
                if (result) {
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                }
                finish();
            }
        }
    }
}
