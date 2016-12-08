package com.example.doctorsbuilding.nav;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.example.doctorsbuilding.nav.Databases.DatabaseAdapter;
import com.example.doctorsbuilding.nav.MainForm.ActivityOffices;
import com.example.doctorsbuilding.nav.User.User;
import com.example.doctorsbuilding.nav.Util.MessageBox;
import com.example.doctorsbuilding.nav.Web.Hashing;
import com.example.doctorsbuilding.nav.Web.WebService;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by hossein on 6/18/2016.
 */
public class SignInActivity extends AppCompatActivity implements View.OnClickListener {
    private ViewFlipper viewFlipper;
    private Button forgetButton;
    private Button backButton;
    private Button btnSignIn;
    private EditText txtUserName;
    private EditText txtPassword;
    private SharedPreferences settings;
    private String password;
    AsyncCallLoginWS loginWS;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_sign_in);
        settings = G.getSharedPreferences();
        G.setStatusBarColor(SignInActivity.this);
        initViews();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (loginWS != null) {
            loginWS.cancel(true);
        }
    }

    private void initViews() {
        viewFlipper = (ViewFlipper) findViewById(R.id.login_viewFlipper);
        backButton = (Button) viewFlipper.findViewById(R.id.login_btnBack);
        forgetButton = (Button) viewFlipper.findViewById(R.id.login_btn_forget);
        btnSignIn = (Button) viewFlipper.findViewById(R.id.login_btn_signIn);
        txtUserName = (EditText) viewFlipper.findViewById(R.id.login_userName);
        txtPassword = (EditText) viewFlipper.findViewById(R.id.login_password);
        forgetButton.setOnClickListener(this);
        backButton.setOnClickListener(this);
        btnSignIn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_btn_forget:
                showNext();
                break;
            case R.id.login_btnBack:
                showPrevious();
                break;
            case R.id.login_btn_signIn:
                signIn();
                break;
        }

    }

    private void signIn() {
        if (checkField()) {
            loginWS = new AsyncCallLoginWS();
            loginWS.execute();
        }
    }

    private boolean checkField() {
        if (txtUserName.getText().toString().trim().equals("")) {
            new MessageBox(SignInActivity.this, "لطفا کد ملی را وارد نمایید .").show();
            return false;
        }
        if (txtPassword.getText().toString().trim().equals("")) {
            new MessageBox(SignInActivity.this, "لطفا کلمه عبور را وارد نمایید .").show();
            return false;
        }
        return true;
    }

    private void showPrevious() {
        viewFlipper.setInAnimation(getBaseContext(), R.anim.slide_in_from_left);
        viewFlipper.setOutAnimation(getBaseContext(), R.anim.slide_out_to_right);
        viewFlipper.showNext();
    }

    private void showNext() {
        viewFlipper.setInAnimation(getBaseContext(), R.anim.slide_in_from_right);
        viewFlipper.setOutAnimation(getBaseContext(), R.anim.slide_out_to_left);
        viewFlipper.showPrevious();
    }

    private class AsyncCallLoginWS extends AsyncTask<String, Void, Void> {
        private int result = -1;
        private UserType userType = UserType.None;
        String msg = null;
        User user = null;
        Bitmap userPic = null;
        ProgressDialog dialog;
        String username;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(SignInActivity.this, "", "لطفا شکیبا باشید ...");
            dialog.getWindow().setGravity(Gravity.END);
            username = txtUserName.getText().toString().trim();
            password = txtPassword.getText().toString().trim();
            try {
                password = Hashing.SHA1(password);
            } catch (NoSuchAlgorithmException e) {
//                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
            }

        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                result = WebService.invokeLogin3WS(username, password);
                if (result != 0) {
                    user = WebService.invokeGetUserInfoWS(username, password, G.officeId);
                    if (user != null) {
                        userPic = WebService.invokeGetUserPicWS(username, password);
                    }
                }
            } catch (PException ex) {
                msg = ex.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (msg != null) {
                dialog.dismiss();
                new MessageBox(SignInActivity.this, msg).show();
            } else {
                dialog.dismiss();
                if (result != -1) {
                    userType = UserType.values()[result];
                    Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                    intent.putExtra("menu", userType);
                    switch (userType) {
                        case None:
                            new MessageBox(SignInActivity.this, "نام کاربری یا کلمه عبور اشتباه می باشد .").show();
                            break;
                        case User:
                            UserType.User.attachTo(intent);
                            save(result);
                            break;
                        case Dr:
                            UserType.Dr.attachTo(intent);
                            save(result);
                            break;
                        case secretary:
                            UserType.Dr.attachTo(intent);
                            save(result);
                            break;
                        default:
                            break;


                    }
                }
                if (user != null) {
                    G.UserInfo = user;
                    if (userPic != null) {
                        G.UserInfo.setImgProfile(userPic);
                    } else {
                        G.UserInfo.setImgProfile(BitmapFactory.decodeResource(getResources(), R.drawable.doctor));
                    }
                    setResult(Activity.RESULT_OK);
                    finish();
                }
            }
        }

        private void save(int role) {
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("user", txtUserName.getText().toString());
            editor.putString("pass", password);
            editor.putInt("role", role);
            editor.apply();
            try {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            } catch (Exception ex) {
            }
        }

    }
}
