package com.example.doctorsbuilding.nav;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.ResultReceiver;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.example.doctorsbuilding.nav.MainForm.ActivityLoading;
import com.example.doctorsbuilding.nav.Util.Util;
import com.example.doctorsbuilding.nav.Web.Hashing;
import com.example.doctorsbuilding.nav.Web.WebService;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by hossein on 1/4/2017.
 */
public class ActivityUpdate extends AppCompatActivity {

    ProgressDialog mProgressDialog;
    private AsyncGetVersionInfo getVersionInfo;
    private PackageInfo pInfo = null;
    Button btn_wifi;
    Button btn_mobiledata;
    Button btn_reconect;
    FrameLayout frm_run;
    FrameLayout frm_error;
    public UserType menu = UserType.None;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.app_loading_layout);
        G.setStatusBarColor(ActivityUpdate.this);
        initViews();
        eventListener();
        checkIsOnline();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private void initViews() {
        ImageView imageView = (ImageView) findViewById(R.id.mPro);
        Animation pulse = AnimationUtils.loadAnimation(this, R.anim.pulse);
        imageView.startAnimation(pulse);
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

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    private void checkIsOnline() {
        if (isOnline()) {
            loadData();
        } else {
            frm_run.setVisibility(View.GONE);
            frm_error.setVisibility(View.VISIBLE);
        }
    }

    private void loadData() {
//        G.getSharedPreferences().edit().remove("ignore").apply();
        frm_run.setVisibility(View.VISIBLE);
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (pInfo != null) {
            getVersionInfo = new AsyncGetVersionInfo();
            getVersionInfo.execute(pInfo.versionName);
        }
    }

    private boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private class AsyncGetVersionInfo extends AsyncTask<String, Void, Void> {
        String msg = null;
        VersionInfo versionInfo = null;
        String pwd = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                pwd = Hashing.SHA1(Util.getStringWS(R.string.updateCode));
            } catch (NoSuchAlgorithmException e) {
                // e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                // e.printStackTrace();
            }
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                if (pwd != null)
                    versionInfo = WebService.getVersionInfo(strings[0], pwd);

            } catch (PException ex) {
                msg = ex.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (versionInfo != null) {

                if (G.getSharedPreferences().getString("ignore", "").equals(String.valueOf(versionInfo.getVersionName()))) {
                    startActivity(new Intent(ActivityUpdate.this, ActivityLoading.class));
                    ActivityUpdate.this.overridePendingTransition(0, 0);
                    finish();
                    return;
                }

                if ((Double.valueOf(pInfo.versionName) >= versionInfo.getVersionName())) {
                    startActivity(new Intent(ActivityUpdate.this, ActivityLoading.class));
                    ActivityUpdate.this.overridePendingTransition(0, 0);
                    finish();
                    return;
                }

                MyAlertDialogFragment.OnClickListener myDialogFrag = new MyAlertDialogFragment.OnClickListener() {
                    @Override
                    public void onClick(int whichButton) {
                        switch (whichButton) {
                            case DialogInterface.BUTTON_POSITIVE:
                                mProgressDialog = new ProgressDialog(ActivityUpdate.this);
                                mProgressDialog.setMessage(Util.getStringWS(R.string.loadingATVdwn));
                                mProgressDialog.setIndeterminate(false);
                                mProgressDialog.setMax(100);
                                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                                mProgressDialog.setCancelable(false);
                                mProgressDialog.show();

                                Intent intent = new Intent(ActivityUpdate.this, DownloadService.class);
                                intent.putExtra("url", versionInfo.getUrl());
                                intent.putExtra("receiver", new DownloadReceiver(new Handler()));
                                startService(intent);
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                SharedPreferences.Editor editor = G.getSharedPreferences().edit();
                                editor.putString(Util.getStringWS(R.string.ws_Ignore), String.valueOf(versionInfo.getVersionName()));
                                editor.apply();
                                startActivity(new Intent(ActivityUpdate.this, ActivityLoading.class));
                                ActivityUpdate.this.overridePendingTransition(0, 0);
                                finish();
                                break;
                            case DialogInterface.BUTTON_NEUTRAL:
                                mProgressDialog = new ProgressDialog(ActivityUpdate.this);
                                mProgressDialog.setMessage(Util.getStringWS(R.string.loadingATVdwn));
                                mProgressDialog.setIndeterminate(false);
                                mProgressDialog.setMax(100);
                                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                                mProgressDialog.setCancelable(false);
                                mProgressDialog.show();

                                Intent intent1 = new Intent(ActivityUpdate.this, DownloadService.class);
                                intent1.putExtra("url", versionInfo.getUrl());
                                intent1.putExtra("receiver", new DownloadReceiver(new Handler()));
                                startService(intent1);
                                finish();
                                break;
                        }
                    }
                };
                if (versionInfo.isForce()) {
                    MyAlertDialogFragment builder = MyAlertDialogFragment.newInstance("بروزرسانی"
                            , versionInfo.getDetails(), "نسخه جدید");
                    builder.setOnClickListener(myDialogFrag).show(getFragmentManager(), "");
                    builder.setCancelable(false);
                } else {
                    MyAlertDialogFragment builder = MyAlertDialogFragment.newInstance("بروزرسانی"
                            , versionInfo.getDetails(), "نسخه جدید", "انصراف");
                    builder.setOnClickListener(myDialogFrag).show(getFragmentManager(), "");
                    builder.setCancelable(false);
                }
            }
        }
    }

    @SuppressLint("ParcelCreator")
    private class DownloadReceiver extends ResultReceiver {
        public DownloadReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            super.onReceiveResult(resultCode, resultData);
            if (resultCode == DownloadService.UPDATE_PROGRESS) {
                int progress = resultData.getInt("progress");
                mProgressDialog.setProgress(progress);
                if (progress == 100) {
                    mProgressDialog.dismiss();
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    String path = Environment.getExternalStorageDirectory() + "/download/" + "pezeshkyar.apk";
                    intent.setDataAndType(Uri.fromFile(new File(path)), "application/vnd.android.package-archive");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        }
    }
}
