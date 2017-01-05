package com.example.doctorsbuilding.nav.MainForm;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.example.doctorsbuilding.nav.Databases.DatabaseAdapter;
import com.example.doctorsbuilding.nav.G;
import com.example.doctorsbuilding.nav.PException;
import com.example.doctorsbuilding.nav.R;
import com.example.doctorsbuilding.nav.User.User;
import com.example.doctorsbuilding.nav.UserType;
import com.example.doctorsbuilding.nav.Util.MessageBox;
import com.example.doctorsbuilding.nav.Web.WebService;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by hossein on 11/16/2016.
 */
public class ActivityLoading extends AppCompatActivity {
    public UserType menu = UserType.None;
    private SharedPreferences settings;
    DatabaseAdapter database;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        G.setStatusBarColor(ActivityLoading.this);
        setContentView(R.layout.app_loading_layout);
        G.setStatusBarColor(ActivityLoading.this);
        initViews();
        loadData();
        database = new DatabaseAdapter(ActivityLoading.this);
        database.initialize();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private void initViews() {
        ImageView imageView = (ImageView) findViewById(R.id.mPro);
        Animation pulse = AnimationUtils.loadAnimation(this, R.anim.pulse);
        imageView.startAnimation(pulse);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    private void loadData() {
        if (G.UserInfo == null)
            G.UserInfo = new User();
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
        User user = null;
        int result = 0;
        Bitmap userPic = null;

        @Override
        protected Void doInBackground(String... strings) {
            try {

                if (G.UserInfo.getUserName().length() != 0 && G.UserInfo.getPassword().length() != 0) {
                    result = WebService.invokeLogin3WS(G.UserInfo.getUserName(), G.UserInfo.getPassword());
                    if (result != 0) {
                        user = WebService.invokeGetUserInfoWS(G.UserInfo.getUserName(), G.UserInfo.getPassword(), G.officeId);
                        if (user != null) {
                            userPic = WebService.invokeGetUserPicWS(G.UserInfo.getUserName(), G.UserInfo.getPassword());
                        }
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
            } else {
                if (user != null) {
                    G.UserInfo = user;
                    if (userPic != null) {
                        G.UserInfo.setImgProfile(userPic);
                    } else {
                        G.UserInfo.setImgProfile(BitmapFactory.decodeResource(getResources(), R.drawable.doctor));
                    }
                } else {
                    G.getSharedPreferences().edit().remove("user").apply();
                    G.getSharedPreferences().edit().remove("pass").apply();
                    G.getSharedPreferences().edit().remove("role").apply();
                }
                startActivity(new Intent(ActivityLoading.this, ActivityOffices.class));
                finish();
            }
        }
    }

}
