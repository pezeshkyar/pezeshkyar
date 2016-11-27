package com.example.doctorsbuilding.nav.User;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.doctorsbuilding.nav.Databases.DatabaseAdapter;
import com.example.doctorsbuilding.nav.G;
import com.example.doctorsbuilding.nav.PException;
import com.example.doctorsbuilding.nav.R;
import com.example.doctorsbuilding.nav.UserType;
import com.example.doctorsbuilding.nav.Util.MessageBox;
import com.example.doctorsbuilding.nav.Util.RoundedImageView;
import com.example.doctorsbuilding.nav.Web.Hashing;
import com.example.doctorsbuilding.nav.Web.WebService;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by hossein on 6/9/2016.
 */
public class UserProfileActivity extends AppCompatActivity {
    private EditText txtFirstName;
    private EditText txtLastName;
    private EditText txtMobile;
    private EditText txtUserName;
    private EditText txtPassword;
    private EditText txtRePassword;
    private Spinner spinnerState;
    private Spinner spinnerCity;
    private Button btnInsert;
    private ImageView profileImage;
    private Button btnImgSelect;
    private Bitmap drPic;
    private Bitmap roundedDrPic;
    private ArrayAdapter<String> stateAdapter;
    private ArrayAdapter<String> cityAdapter;
    private ArrayList<State> stateList;
    private ArrayList<City> cityList;
    private ProgressDialog loadingDialog;
    private DatabaseAdapter database;
    private String password;

    private int stateID = -1;
    private static int imageProfileId = 1;
    Button backBtn;

    private AsyncCallRegisterWS registerTask = null;
    private AsyncCallCityWS cityTask = null;
    private AsyncCallStateWS stateTask = null;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_personal_info);
        initViews();
        stateTask = new AsyncCallStateWS();
        stateTask.execute();
        eventListener();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (registerTask != null) {
            registerTask.cancel(true);
        }
        if (cityTask != null) {
            cityTask.cancel(true);
        }
        if (stateTask != null) {
            stateTask.cancel(true);
        }
    }

    private void initViews() {
        database = new DatabaseAdapter(UserProfileActivity.this);
        profileImage = (ImageView) findViewById(R.id.dr_imgProfile);
        Bitmap bmpImg = BitmapFactory.decodeResource(getResources(), R.mipmap.doctor);
        profileImage.setImageBitmap(RoundedImageView.getCroppedBitmap(bmpImg, 160));
        backBtn = (Button) findViewById(R.id.personalInfo_backBtn);
        txtFirstName = (EditText) findViewById(R.id.dr_FirstName);
        txtLastName = (EditText) findViewById(R.id.dr_LastName);
        txtMobile = (EditText) findViewById(R.id.dr_Mobile);
        txtUserName = (EditText) findViewById(R.id.dr_UserName);
        txtPassword = (EditText) findViewById(R.id.dr_Password);
        txtRePassword = (EditText) findViewById(R.id.dr_ConfirmPassword);
        spinnerState = (Spinner) findViewById(R.id.dr_profile_state);
        spinnerCity = (Spinner) findViewById(R.id.dr_profile_city);
        btnInsert = (Button) findViewById(R.id.dr_btnPersonalInfoInsert);
        profileImage = (ImageView) findViewById(R.id.dr_imgProfile);
        btnImgSelect = (Button) findViewById(R.id.dr_btnImgProfile);
        btnImgSelect.setText("انتخاب عکس");
        // progressBar = (ProgressBar) findViewById(R.id.user_profile_progressBar);
    }

    private void eventListener() {
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserProfileActivity.this.onBackPressed();
            }
        });
        spinnerState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                stateID = stateList.get(position).GetStateID();
                cityTask = new AsyncCallCityWS();
                cityTask.execute();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        btnInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkFields()) {
                    registerTask = new AsyncCallRegisterWS();
                    registerTask.execute();
                }
            }
        });
        btnImgSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//                intent.setType("image/*");
//                startActivityForResult(intent, 0);
                changePic();
            }
        });
    }

    private void changePic() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "?????? ???"), 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                        int nh = (int) (bitmap.getHeight() * (512.0 / bitmap.getWidth()));
                        Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 512, nh, true);
                        Bitmap bmp = RoundedImageView.getCroppedBitmap(scaled, 200);
                        drPic = scaled;
                        roundedDrPic = bmp;
                        profileImage.setImageBitmap(roundedDrPic);
//                        AsyncUpdateDrPicWS updateDrPicWS = new AsyncUpdateDrPicWS();
//                        updateDrPicWS.execute();


//                        currentUser.image = resizedBitmap;
//                        Database.UpdateCurrentUser(currentUser);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        }
    }

    private void clearForm(ViewGroup group) {
        for (int i = 0, count = group.getChildCount(); i < count; ++i) {
            View view = group.getChildAt(i);
            if (view instanceof EditText) {
                ((EditText) view).setText("");
            }

            if (view instanceof ViewGroup && (((ViewGroup) view).getChildCount() > 0))
                clearForm((ViewGroup) view);
        }
    }

    private class AsyncCallStateWS extends AsyncTask<String, Void, Void> {
        String msg = null;

        @Override
        protected void onPreExecute() {
            loadingDialog = ProgressDialog.show(UserProfileActivity.this, "", "درحال دریافت اطلاعات ...");
            loadingDialog.getWindow().setGravity(Gravity.END);
            loadingDialog.setCancelable(true);
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                if (stateList == null) {
                    stateList = WebService.invokeGetProvinceNameWS();
                    stateID = stateList.get(25).GetStateID();
                    cityList = WebService.invokeGetCityNameWS(stateID);
                } else {
                    cityList = WebService.invokeGetCityNameWS(stateID);
                }
            } catch (PException ex) {
                msg = ex.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (msg != null) {
                loadingDialog.dismiss();
                new MessageBox(UserProfileActivity.this, msg).show();
            } else {
                setStateSpinner();
                if (stateID != -1) {
                    setCitySpinner();
                }
            }
        }

        private void setStateSpinner() {
            ArrayList<String> states = new ArrayList<String>();
            for (State s : stateList) {
                states.add(s.GetStateName());
            }
            stateAdapter = new ArrayAdapter<String>(UserProfileActivity.this
                    , R.layout.support_simple_spinner_dropdown_item, states);
            spinnerState.setAdapter(stateAdapter);
        }

        private void setCitySpinner() {
            ArrayList<String> cities = new ArrayList<String>();
            for (City c : cityList) {
                cities.add(c.GetCityName());
            }
            cityAdapter = new ArrayAdapter<String>(UserProfileActivity.this
                    , R.layout.support_simple_spinner_dropdown_item, cities);
            spinnerCity.setAdapter(cityAdapter);
        }


    }

    private class AsyncCallCityWS extends AsyncTask<String, Void, Void> {
        String msg = null;

        @Override
        protected Void doInBackground(String... strings) {
            try {
                cityList = WebService.invokeGetCityNameWS(stateID);
            } catch (PException ex) {
                msg = ex.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (msg != null) {
                loadingDialog.dismiss();
                new MessageBox(UserProfileActivity.this, msg).show();
            } else {
                setCitySpinner();
                loadingDialog.dismiss();
            }
        }

        private void setCitySpinner() {
            ArrayList<String> cities = new ArrayList<String>();
            for (City c : cityList) {
                cities.add(c.GetCityName());
            }
            cityAdapter = new ArrayAdapter<String>(UserProfileActivity.this
                    , R.layout.support_simple_spinner_dropdown_item, cities);
            spinnerCity.setAdapter(cityAdapter);
        }

    }

    private class AsyncCallRegisterWS extends AsyncTask<String, Void, Void> {
        private String result;
        private User user;
        String msg = null;
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            btnInsert.setClickable(false);
            dialog = ProgressDialog.show(UserProfileActivity.this, "", "در حال ذخیره اطلاعات ...");
            dialog.getWindow().setGravity(Gravity.END);
            dialog.setCancelable(true);
        }

        @Override
        protected Void doInBackground(String... strings) {
            user = setUserData();
            try {
                result = WebService.invokeRegisterWS(user);
            } catch (PException ex) {
                msg = ex.getMessage();

            }
            return null;
        }

        private User setUserData() {
            User user = new User();
            user.setFirstName(txtFirstName.getText().toString().trim());
            user.setLastName(txtLastName.getText().toString().trim());
            user.setUserName(txtUserName.getText().toString().trim());
            password = txtPassword.getText().toString().trim();
            try {
                password = Hashing.SHA1(password);
            } catch (NoSuchAlgorithmException e) {
//                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
            }
            user.setPassword(password);
            user.setPhone(txtMobile.getText().toString().trim());
            user.setRole(UserType.User.ordinal());
            user.setCityID((cityList.get(spinnerCity.getSelectedItemPosition()).GetCityID()));

//            Bitmap imgUser = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
            if (drPic == null) {
                Bitmap imgUser = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
                user.setImgProfile(imgUser);
            } else {
                user.setImgProfile(drPic);
            }

            return user;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            btnInsert.setClickable(true);
            if (msg != null) {
                dialog.dismiss();
                new MessageBox(UserProfileActivity.this, msg).show();
            } else {
                if (result.equals("OK")) {
                    dialog.dismiss();
                    Toast.makeText(UserProfileActivity.this, "ثبت مشخصات با موفقیت انجام شد .", Toast.LENGTH_SHORT).show();
                    SharedPreferences.Editor editor = G.getSharedPreferences().edit();
                    editor.putString("user", txtUserName.getText().toString());
                    editor.putString("pass", password);
                    editor.putInt("role", UserType.User.ordinal());
                    editor.apply();
                    setResult(Activity.RESULT_OK);
                    try {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    } catch (Exception ex) {
                    }
                    finish();
                } else {
                    dialog.dismiss();
                    new MessageBox(UserProfileActivity.this, result).show();
                }

            }
        }

    }

    private boolean checkFields() {
        if (txtFirstName.getText().toString().trim().isEmpty()) {
            new MessageBox(UserProfileActivity.this, "لطفا نام خود را وارد نمایید .").show();
            return false;
        }
        if (txtLastName.getText().toString().trim().isEmpty()) {
            new MessageBox(UserProfileActivity.this, "لطفا نام خانوادگی خود را وارد نمایید .").show();
            return false;
        }
        if (txtMobile.getText().toString().trim().isEmpty()) {
            new MessageBox(UserProfileActivity.this, "لطفا شماره تلفن همراه خود را وارد نمایید .").show();
            return false;
        }
        if (txtUserName.getText().toString().trim().isEmpty()) {
            new MessageBox(UserProfileActivity.this, "لطفا نام کاربری خود را وارد نمایید .").show();
            return false;
        }
        if (txtPassword.getText().toString().trim().isEmpty()) {
            new MessageBox(UserProfileActivity.this, "لطفا پسورد خود را وارد نمایید .").show();
            return false;
        }
        if (txtRePassword.getText().toString().trim().isEmpty()) {
            new MessageBox(UserProfileActivity.this, "لطفا پسورد خود را دوباره وارد نمایید .").show();
            return false;
        }

        if (spinnerState.getSelectedItemPosition() == -1) {
            new MessageBox(UserProfileActivity.this, "لطفا استان محل سکونت خود را وارد نمایید .").show();
            return false;
        }
        if (spinnerCity.getSelectedItemPosition() == -1) {
            new MessageBox(UserProfileActivity.this, "لطفا شهر محل سکونت خود را وارد نمایید .").show();
            return false;
        }
        return true;
    }
}
