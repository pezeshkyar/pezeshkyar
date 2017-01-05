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
import com.example.doctorsbuilding.nav.Util.Util;
import com.example.doctorsbuilding.nav.Web.Hashing;
import com.example.doctorsbuilding.nav.Web.WebService;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.EventListener;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by hossein on 6/18/2016.
 */
public class SignInActivity extends AppCompatActivity {
    private ViewFlipper viewFlipper;
    private Button forgetButton;
    private Button btnGetPassword;
    private Button backButton;
    private Button btnSignIn;
    private EditText txtUserName;
    private EditText txtPassword;
    private EditText txtmelicode;
    private SharedPreferences settings;
    private String password;
    private EditText txtSmsCode;
    private Button btnSmsCode;
    private EditText txtNewPwd;
    private EditText txtReNewPwd;
    private Button btnNewPwd;
    private Button btnResendSms;
    AsyncCallLoginWS loginWS;
    AsyncForgetPasswordWS task_forgetPassword;
    AsyncVerifySecurityCodeWS verifySecurityCodeWS;
    AsyncChangePWDWS changePWDWS;


    @Override

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        G.setStatusBarColor(SignInActivity.this);
        setContentView(R.layout.activity_sign_in);
        settings = G.getSharedPreferences();
        G.setStatusBarColor(SignInActivity.this);
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
        if (loginWS != null)
            loginWS.cancel(true);

        if (task_forgetPassword != null)
            task_forgetPassword.cancel(true);

        if (changePWDWS != null)
            changePWDWS.cancel(true);

        if (verifySecurityCodeWS != null)
            verifySecurityCodeWS.cancel(true);
    }

    private void initViews() {
        viewFlipper = (ViewFlipper) findViewById(R.id.login_viewFlipper);
        backButton = (Button) viewFlipper.findViewById(R.id.login_btnBack);
        forgetButton = (Button) viewFlipper.findViewById(R.id.login_btn_forget);
        btnGetPassword = (Button) viewFlipper.findViewById(R.id.login_btn_sms);
        btnSignIn = (Button) viewFlipper.findViewById(R.id.login_btn_signIn);
        txtUserName = (EditText) viewFlipper.findViewById(R.id.login_userName);
        txtPassword = (EditText) viewFlipper.findViewById(R.id.login_password);
        txtmelicode = (EditText) viewFlipper.findViewById(R.id.login_melicode);
        txtSmsCode = (EditText) viewFlipper.findViewById(R.id.login_txt_smsCode);
        btnSmsCode = (Button) viewFlipper.findViewById(R.id.login_btn_smsCode);
        txtNewPwd = (EditText) viewFlipper.findViewById(R.id.login_txt_newPwd);
        txtReNewPwd = (EditText) viewFlipper.findViewById(R.id.login_txt_reNewPwd);
        btnNewPwd = (Button) viewFlipper.findViewById(R.id.login_btn_newPwd);
        btnResendSms = (Button) viewFlipper.findViewById(R.id.login_btn_resendSms);

    }

    private void eventListener() {
        forgetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!G.getSharedPreferences().getString("meli", "").isEmpty()){
                    showNext();
                    viewFlipper.setDisplayedChild(2);
                }else {
                    showNext();
                    viewFlipper.setDisplayedChild(1);
                }
            }
        });
        btnResendSms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                G.getSharedPreferences().edit().remove("meli").apply();
                showPrevious();
                viewFlipper.setDisplayedChild(1);
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPrevious();
                viewFlipper.setDisplayedChild(0);
            }
        });
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });
        btnGetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkFieldForgetPass()) {
                    task_forgetPassword = new AsyncForgetPasswordWS();
                    task_forgetPassword.execute();
                }
            }
        });
        btnSmsCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkFieldVerifySecurityCode()) {
                    verifySecurityCodeWS = new AsyncVerifySecurityCodeWS();
                    verifySecurityCodeWS.execute();
                }
            }
        });
        btnNewPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkFieldChangePwd()) {
                    changePWDWS = new AsyncChangePWDWS();
                    changePWDWS.execute();
                }
            }
        });
    }

    private void signIn() {
        if (checkField()) {
            loginWS = new AsyncCallLoginWS();
            loginWS.execute();
        }
    }

    private boolean checkFieldForgetPass() {
        if (txtmelicode.getText().toString().trim().equals("")) {
            new MessageBox(SignInActivity.this, "لطفا کد ملی را وارد نمایید .").show();
            return false;
        }
        if (!Util.IsValidCodeMeli(txtmelicode.getText().toString().trim())) {
            new MessageBox(SignInActivity.this, "کد ملی وارد شده نادرست می باشد .").show();
            return false;
        }
        return true;
    }

    private boolean checkFieldChangePwd() {
        if (txtNewPwd.getText().toString().trim().isEmpty()) {
            new MessageBox(SignInActivity.this, "لطفا پسورد خود را وارد نمایید .").show();
            return false;
        }
        if (txtNewPwd.getText().toString().trim().length() < 4) {
            new MessageBox(SignInActivity.this, "تعداد کاراکترهای پسورد نباید کمتر از 4 تا باشد .").show();
            return false;
        }
        if (txtReNewPwd.getText().toString().trim().isEmpty()) {
            new MessageBox(SignInActivity.this, "لطفا پسورد خود را دوباره وارد نمایید .").show();
            return false;
        }
        if (!txtNewPwd.getText().toString().trim().equals(txtReNewPwd.getText().toString().trim())) {
            new MessageBox(SignInActivity.this, "پسورد وارد شده با هم مطابقت ندارد .").show();
            return false;
        }
        return true;
    }


    private boolean checkFieldVerifySecurityCode() {
        if (txtSmsCode.getText().toString().trim().equals("")) {
            new MessageBox(SignInActivity.this, "لطفا رمزی که به تلفن همراه شما پیامک شده را وارد نمایید .").show();
            return false;
        }
        return true;
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
//        viewFlipper.showNext();
    }

    private void showNext() {
        viewFlipper.setInAnimation(getBaseContext(), R.anim.slide_in_from_right);
        viewFlipper.setOutAnimation(getBaseContext(), R.anim.slide_out_to_left);
//        viewFlipper.showPrevious();
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
                            new MessageBox(SignInActivity.this, "کد ملی یا کلمه عبور اشتباه می باشد .").show();
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

    private class AsyncForgetPasswordWS extends AsyncTask<String, Void, Void> {
        private String result = null;
        String msg = null;
        ProgressDialog dialog;
        String _codeMeli;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            _codeMeli = txtmelicode.getText().toString().trim();
            dialog = ProgressDialog.show(SignInActivity.this, "", "لطفا شکیبا باشید ...");
            dialog.getWindow().setGravity(Gravity.END);
            dialog.setCancelable(true);
            btnGetPassword.setClickable(false);
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                result = WebService.ForegetPasswordWS(_codeMeli);
            } catch (PException ex) {
                msg = ex.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (msg != null) {
                btnGetPassword.setClickable(true);
                dialog.dismiss();
                new MessageBox(SignInActivity.this, msg).show();
            } else {
                dialog.dismiss();
                btnGetPassword.setClickable(true);
                if (result != null) {
                    if (result.toUpperCase().equals("OK")) {
                        Toast.makeText(SignInActivity.this, Util.getStringWS(R.string.siginACT_forget_msg), Toast.LENGTH_LONG).show();
                        //save melicode
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString("meli", _codeMeli);
                        editor.apply();

                        //show next layout
                        showNext();
                        viewFlipper.setDisplayedChild(2);

                    } else {
                        new MessageBox(SignInActivity.this, result).show();
                    }
                } else {
                    new MessageBox(SignInActivity.this, "درخواست شما با مشکل مواجه شده است .").show();
                }
            }
        }
    }

    private class AsyncVerifySecurityCodeWS extends AsyncTask<String, Void, Void> {
        private String result = null;
        String msg = null;
        ProgressDialog dialog;
        private String secutyCode;
        private String _meliCode;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            _meliCode = G.getSharedPreferences().getString("meli", "");
            try {
                secutyCode = Hashing.SHA1(txtSmsCode.getText().toString().trim());
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            dialog = ProgressDialog.show(SignInActivity.this, "", "لطفا شکیبا باشید ...");
            dialog.getWindow().setGravity(Gravity.END);
            dialog.setCancelable(true);
            btnSmsCode.setClickable(false);
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                result = WebService.verifySecurityCodeWS(_meliCode, secutyCode);
            } catch (PException ex) {
                msg = ex.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (msg != null) {
                btnSmsCode.setClickable(true);
                dialog.dismiss();
                new MessageBox(SignInActivity.this, msg).show();
            } else {
                dialog.dismiss();
                btnSmsCode.setClickable(true);
                if (result != null) {
                    if (result.toUpperCase().equals("OK")) {
                        showNext();
                        viewFlipper.setDisplayedChild(3);
                    } else {
                        new MessageBox(SignInActivity.this, result).show();
                    }
                } else {
                    new MessageBox(SignInActivity.this, "درخواست شما با مشکل مواجه شده است .").show();
                }
            }
        }
    }

    private class AsyncChangePWDWS extends AsyncTask<String, Void, Void> {
        private String result = null;
        String msg = null;
        ProgressDialog dialog;
        private String newPwd;
        private String _meliCode;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            _meliCode = G.getSharedPreferences().getString("meli", "");
            try {
                newPwd = Hashing.SHA1(txtNewPwd.getText().toString().trim());
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            dialog = ProgressDialog.show(SignInActivity.this, "", "لطفا شکیبا باشید ...");
            dialog.getWindow().setGravity(Gravity.END);
            dialog.setCancelable(true);
            btnNewPwd.setClickable(false);
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                result = WebService.changePasswordWS(_meliCode, newPwd);
            } catch (PException ex) {
                msg = ex.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (msg != null) {
                btnNewPwd.setClickable(true);
                dialog.dismiss();
                new MessageBox(SignInActivity.this, msg).show();
            } else {
                dialog.dismiss();
                btnNewPwd.setClickable(true);
                if (result != null) {
                    if (result.toUpperCase().equals("OK")) {
                        txtUserName.setText(G.getSharedPreferences().getString("meli", ""));
                        txtPassword.setText(txtNewPwd.getText().toString().trim());
                        G.getSharedPreferences().edit().remove("meli").apply();
                        btnSignIn.performClick();
                    } else {
                        new MessageBox(SignInActivity.this, result).show();
                    }
                } else {
                    new MessageBox(SignInActivity.this, "درخواست شما با مشکل مواجه شده است .").show();
                }
            }
        }
    }
}

