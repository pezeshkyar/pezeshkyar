package com.example.doctorsbuilding.nav.Dr.Profile;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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
import com.example.doctorsbuilding.nav.User.City;
import com.example.doctorsbuilding.nav.User.State;
import com.example.doctorsbuilding.nav.User.User;
import com.example.doctorsbuilding.nav.UserType;
import com.example.doctorsbuilding.nav.Util.DbBitmapUtility;
import com.example.doctorsbuilding.nav.Util.FormatHelper;
import com.example.doctorsbuilding.nav.Util.MessageBox;
import com.example.doctorsbuilding.nav.Util.RoundedImageView;
import com.example.doctorsbuilding.nav.Util.Util;
import com.example.doctorsbuilding.nav.Web.Hashing;
import com.example.doctorsbuilding.nav.Web.WebService;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by hossein on 5/23/2016.
 */
public class PersonalInfoActivity extends AppCompatActivity {
    private EditText txtFirstName;
    private EditText txtLastName;
    private EditText txtMobile;
    private EditText txtUserName;
    private EditText txtPassword;
    private EditText txtRePassword;
    private EditText txtEmail;
    private Spinner spinnerState;
    private Spinner spinnerCity;
    private Button btnInsert;
    private ArrayAdapter<String> stateAdapter;
    private ArrayAdapter<String> cityAdapter;
    private ArrayList<State> stateList;
    private ArrayList<City> cityList;
    private int stateID = -1;

    private Context context;
    private ImageView profileImage;
    private Button btnImgSelect;
    private String selectedImagePath;
    private LayoutInflater inflater = null;
    private SharedPreferences setting;
    private boolean isUserExist;
    private Bitmap drPic;
    private Bitmap roundedDrPic;
    private int role;
    private Button backBtn;
    ProgressDialog progressDialog;
    private DatabaseAdapter database;
    String password;

    AsyncCallStateWS getStateTask;
    AsyncCallCityWS getCityTask;
    AsyncCallRegisterWS registerTask;
    AsyncUpdateDrPicWS updatePicTask;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_personal_info);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        initViews();

        eventListener();

        if (G.UserInfo != null) {
            isUserExist = true;
            showDrInfo();
        }
        progressDialog = ProgressDialog.show(PersonalInfoActivity.this, "", "در حال دریافت اطلاعات ...");
        progressDialog.getWindow().setGravity(Gravity.END);
        progressDialog.setCancelable(true);
        btnInsert.setClickable(false);

        getStateTask = new AsyncCallStateWS();
        getStateTask.execute();

//        setting = getSharedPreferences("doctorBuilding", 0);
//
//        try {
//            username = setting.getString("user", "");
//            password = setting.getString("pass", "");
//            callAsyncUserInfoWs userInfoWs = new callAsyncUserInfoWs();
//            userInfoWs.execute();
//
//        } catch (Exception e) {
//        }


    }

    @Override
    protected void onStop() {
        super.onStop();
        if (getCityTask != null) {
            getCityTask.cancel(true);
        }
        if (getStateTask != null) {
            getStateTask.cancel(true);
        }
        if (registerTask != null) {
            registerTask.cancel(true);
        }
        if (updatePicTask != null) {
            updatePicTask.cancel(true);
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private void showDrInfo() {

        txtFirstName.setText(G.UserInfo.getFirstName());
        txtUserName.setText(G.UserInfo.getUserName());
        txtUserName.setEnabled(false);
        txtPassword.setText(G.UserInfo.getPassword());
        txtRePassword.setText(G.UserInfo.getPassword());
        txtLastName.setText(G.UserInfo.getLastName());
        txtMobile.setText(G.UserInfo.getPhone());
        txtEmail.setText(G.UserInfo.getEmail());


        if (G.UserInfo.getImgProfile() != null) {

            Bitmap imgRound = RoundedImageView.getCroppedBitmap(G.UserInfo.getImgProfile(), 160);
            profileImage.setImageBitmap(imgRound);

        } else {
            profileImage.setImageBitmap(RoundedImageView.getCroppedBitmap(G.doctorImageProfile, 160));
        }
    }

    private void initViews() {
        backBtn = (Button) findViewById(R.id.personalInfo_backBtn);
        role = G.UserInfo.getRole();
        txtFirstName = (EditText) findViewById(R.id.dr_FirstName);
        txtLastName = (EditText) findViewById(R.id.dr_LastName);
        txtMobile = (EditText) findViewById(R.id.dr_Mobile);
        txtUserName = (EditText) findViewById(R.id.dr_UserName);
        txtPassword = (EditText) findViewById(R.id.dr_Password);
        txtRePassword = (EditText) findViewById(R.id.dr_ConfirmPassword);
        txtEmail = (EditText) findViewById(R.id.dr_email);
        spinnerState = (Spinner) findViewById(R.id.dr_profile_state);
        spinnerCity = (Spinner) findViewById(R.id.dr_profile_city);
        btnInsert = (Button) findViewById(R.id.dr_btnPersonalInfoInsert);
        profileImage = (ImageView) findViewById(R.id.dr_imgProfile);
        btnImgSelect = (Button) findViewById(R.id.dr_btnImgProfile);
        database = new DatabaseAdapter(PersonalInfoActivity.this);
        if (role == UserType.Dr.ordinal()) {
            //Bitmap bmpImg = BitmapFactory.decodeResource(getResources(), R.mipmap.doctor);
        } else {
            //Bitmap bmpImg = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_user_profile);
            //profileImage.setImageBitmap(bmpImg);
//            profileImage.setVisibility(View.GONE);
//            btnImgSelect.setVisibility(View.GONE);
        }

    }

    private void eventListener() {
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PersonalInfoActivity.this.onBackPressed();
            }
        });
        spinnerState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                stateID = stateList.get(position).GetStateID();
                getCityTask = new AsyncCallCityWS();
                getCityTask.execute();

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
                        G.UserInfo.setImgProfile(roundedDrPic);
                        updatePicTask = new AsyncUpdateDrPicWS();
                        updatePicTask.execute();


//                        currentUser.image = resizedBitmap;
//                        Database.UpdateCurrentUser(currentUser);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        }
    }

//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (resultCode == Activity.RESULT_OK && data != null) {
//            String realPath;
//            // SDK < API11
//            if (Build.VERSION.SDK_INT < 11)
//                realPath = RealPathUtil.getRealPathFromURI_BelowAPI11(getBaseContext(), data.getData());
//
//                // SDK >= 11 && SDK < 19
//            else if (Build.VERSION.SDK_INT < 19)
//                realPath = RealPathUtil.getRealPathFromURI_API11to18(getBaseContext(), data.getData());
//
//                // SDK > 19 (Android 4.4)
//            else
//                realPath = RealPathUtil.getRealPathFromURI_API19(getBaseContext(), data.getData());
//
//
//            setImageView(realPath);
//        }
//    }

//    private void setImageView(String realPath) {
//
//        Uri uriFromPath = Uri.fromFile(new File(realPath));
//        Bitmap bitmap = null;
//        try {
//            bitmap = BitmapFactory.decodeStream(getBaseContext().getContentResolver().openInputStream(uriFromPath));
//        } catch (FileNotFoundException e) {
//            Toast.makeText(context, e.toString(),Toast.LENGTH_LONG).show();
//        }
//        Bitmap bmp = RoundedImageView.getCroppedBitmap(bitmap, 200);
//        drPic = bitmap;
//        roundedDrPic = bmp;
//        AsyncUpdateDrPicWS updateDrPicWS = new AsyncUpdateDrPicWS();
//        updateDrPicWS.execute();
//
//    }

    private boolean checkFields() {
        if (txtFirstName.getText().toString().trim().isEmpty()) {
            new MessageBox(PersonalInfoActivity.this, "لطفا نام خود را وارد نمایید .").show();
            return false;
        }
        if (txtLastName.getText().toString().trim().isEmpty()) {
            new MessageBox(PersonalInfoActivity.this, "لطفا نام خانوادگی خود را وارد نمایید .").show();
            return false;
        }
        if (txtMobile.getText().toString().trim().isEmpty()) {
            new MessageBox(PersonalInfoActivity.this, "لطفا شماره تلفن همراه خود را وارد نمایید .").show();
            return false;
        }
        if (txtUserName.getText().toString().trim().isEmpty()) {
            new MessageBox(PersonalInfoActivity.this, "لطفا کد ملی خود را وارد نمایید .").show();
            return false;
        }
        if (!Util.IsValidCodeMeli(txtUserName.getText().toString().trim())) {
            new MessageBox(PersonalInfoActivity.this, "کد ملی وارد شده نادرست می باشد .").show();
            return false;
        }
        if (txtPassword.getText().toString().trim().isEmpty()) {
            new MessageBox(PersonalInfoActivity.this, "لطفا پسورد خود را وارد نمایید .").show();
            return false;
        }
        if (txtPassword.getText().toString().trim().length() < 4) {
            new MessageBox(PersonalInfoActivity.this, "تعداد کاراکترهای پسورد نباید کمتر از 4 تا باشد .").show();
            return false;
        }
        if (txtRePassword.getText().toString().trim().isEmpty()) {
            new MessageBox(PersonalInfoActivity.this, "لطفا پسورد خود را دوباره وارد نمایید .").show();
            return false;
        }
        if (!txtPassword.getText().toString().trim().equals(txtRePassword.getText().toString().trim())) {
            new MessageBox(this, "پسورد وارد شده با هم مطابقت ندارد .").show();
            return false;
        }
        if (spinnerState.getSelectedItemPosition() == -1) {
            new MessageBox(PersonalInfoActivity.this, "لطفا استان محل سکونت خود را وارد نمایید .").show();
            return false;
        }
        if (spinnerCity.getSelectedItemPosition() == -1) {
            new MessageBox(PersonalInfoActivity.this, "لطفا شهر محل سکونت خود را وارد نمایید .").show();
            return false;
        }
        return true;
    }


    //set state spinner ............................................................................

    private class AsyncCallStateWS extends AsyncTask<String, Void, Void> {

        private String msg = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                stateList = WebService.invokeGetProvinceNameWS();
                if (isUserExist) {
                    stateID = G.UserInfo.getStateID();
                }
                cityList = WebService.invokeGetCityNameWS(stateID);
                if (stateList == null) {
                    stateList = WebService.invokeGetProvinceNameWS();
                    if (isUserExist) {
                        stateID = G.UserInfo.getStateID();
                    } else {
                        stateID = stateList.get(25).GetStateID();
                    }
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
                progressDialog.dismiss();
                btnInsert.setClickable(true);
                new MessageBox(PersonalInfoActivity.this, msg).show();
            } else {
                if (stateList != null && stateList.size() != 0) {
                    setStateSpinner();
                    if (isUserExist) {
                        for (int i = 0; i < stateList.size(); i++) {
                            if (stateList.get(i).GetStateID() == G.UserInfo.getStateID()) {
                                spinnerState.setSelection(i);
                                break;
                            }
                        }

                    }
                    if (stateID != -1) {
                        setCitySpinner();
                    }


                } else {
                    progressDialog.dismiss();
                    btnInsert.setClickable(true);
                    new MessageBox(PersonalInfoActivity.this, "خطایی در دریافت اطلاعات رخ داده است .").show();
                }
            }
        }

        private void setStateSpinner() {
            ArrayList<String> states = new ArrayList<String>();
            for (State s : stateList) {
                states.add(s.GetStateName());
            }
            stateAdapter = new ArrayAdapter<String>(PersonalInfoActivity.this
                    , R.layout.support_simple_spinner_dropdown_item, states);
            spinnerState.setAdapter(stateAdapter);
        }

        private void setCitySpinner() {
            ArrayList<String> cities = new ArrayList<String>();
            for (City c : cityList) {
                cities.add(c.GetCityName());
            }
            cityAdapter = new ArrayAdapter<String>(PersonalInfoActivity.this
                    , R.layout.support_simple_spinner_dropdown_item, cities);
            spinnerCity.setAdapter(cityAdapter);
        }

    }

    // set city spinner

    private class AsyncCallCityWS extends AsyncTask<String, Void, Void> {
        String msg = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

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
                progressDialog.dismiss();
                btnInsert.setClickable(true);
                new MessageBox(PersonalInfoActivity.this, msg).show();
            } else {
                if (cityList != null && cityList.size() != 0) {
                    ArrayList<String> cities = new ArrayList<String>();
                    for (City c : cityList) {
                        cities.add(c.GetCityName());
                    }
                    cityAdapter = new ArrayAdapter<String>(PersonalInfoActivity.this
                            , R.layout.support_simple_spinner_dropdown_item, cities);
                    spinnerCity.setAdapter(cityAdapter);
                    if (isUserExist) {
                        for (int i = 0; i < cityList.size(); i++) {
                            if (cityList.get(i).GetCityID() == G.UserInfo.getCityID()) {
                                spinnerCity.setSelection(i);
                                break;
                            }
                        }
                    }
                    progressDialog.dismiss();
                    btnInsert.setClickable(true);
                } else {
                    progressDialog.dismiss();
                    btnInsert.setClickable(true);
                    new MessageBox(PersonalInfoActivity.this, "خطایی در دریافت اطلاعات رخ داده است .").show();
                }
            }
        }

    }

    //register user data ..........................................................................


    private class AsyncCallRegisterWS extends AsyncTask<String, Void, Void> {
        private String result;
        User user;
        String msg = null;
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(PersonalInfoActivity.this, "", "در حال ثبت اطلاعات ...");
            dialog.getWindow().setGravity(Gravity.END);
            dialog.setCancelable(true);
            btnInsert.setClickable(false);
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                if (!isUserExist) {
                    result = WebService.invokeRegisterWS(setUserData());
                } else {
                    result = WebService.invokeUpdateUserWS(G.UserInfo.getUserName(), G.UserInfo.getPassword(), setUserData());
                }
            } catch (PException ex) {
                msg = ex.getMessage();
            }
            return null;
        }

        private User setUserData() {
            user = new User();
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
            user.setEmail(txtEmail.getText().toString().trim());
            if (role == UserType.Dr.ordinal() || role == UserType.User.ordinal() || role == UserType.secretary.ordinal()) {
                user.setRole(role);
            } else {
                user.setRole(UserType.User.getUsertype());
            }
            user.setStateID((stateList.get(spinnerState.getSelectedItemPosition()).GetStateID()));
            user.setCityID((cityList.get(spinnerCity.getSelectedItemPosition()).GetCityID()));
            if (role == UserType.Dr.ordinal()) {
                Bitmap imgRound = RoundedImageView.getCroppedBitmap(G.UserInfo.getImgProfile(), 160);
                user.setImgProfile(imgRound);
            } else {
                Bitmap imgRound = RoundedImageView.getCroppedBitmap(G.UserInfo.getImgProfile(), 160);
                user.setImgProfile(imgRound);
            }
            return user;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (msg != null) {
                dialog.dismiss();
                new MessageBox(PersonalInfoActivity.this, msg).show();
                btnInsert.setClickable(true);
            } else {
                if (result.equals("OK")) {
                    Toast.makeText(PersonalInfoActivity.this, "ثبت مشخصات با موفقیت انجام شد .", Toast.LENGTH_SHORT).show();
                    G.UserInfo = user;
                    if (user.getRole() == UserType.Dr.ordinal()) {
                        G.officeInfo.setFirstname(user.getFirstName());
                        G.officeInfo.setLastname(user.getLastName());
                        G.UserInfo.setImgProfile(user.getImgProfile());
                        G.UserInfo.setFirstName(user.getFirstName());
                        G.UserInfo.setLastName(user.getLastName());
                        G.UserInfo.setPassword(user.getPassword());
                        G.UserInfo.setEmail(user.getEmail());
                        G.UserInfo.setPhone(user.getPhone());
                        G.UserInfo.setStateID(user.getStateID());
                        G.UserInfo.setCityID(user.getCityID());
                        SharedPreferences.Editor editor = G.getSharedPreferences().edit();
                        editor.putString("user", txtUserName.getText().toString());
                        editor.putString("pass", password);
                        editor.putInt("role", role);
                        editor.apply();
                        finish();
                    } else {
                        G.UserInfo.setImgProfile(user.getImgProfile());
                        G.UserInfo.setFirstName(user.getFirstName());
                        G.UserInfo.setLastName(user.getLastName());
                        G.UserInfo.setEmail(user.getEmail());
                        G.UserInfo.setPassword(user.getPassword());
                        G.UserInfo.setPhone(user.getPhone());
                        G.UserInfo.setStateID(user.getStateID());
                        G.UserInfo.setCityID(user.getCityID());
                        SharedPreferences.Editor editor = G.getSharedPreferences().edit();
                        editor.putString("user", txtUserName.getText().toString());
                        editor.putString("pass", password);
                        editor.putInt("role", role);
                        editor.apply();
                        finish();
                    }

                    dialog.dismiss();
                    btnInsert.setClickable(true);
                } else {
                    dialog.dismiss();
                    new MessageBox(PersonalInfoActivity.this, "خطایی در ثبت اطلاعات رخ داده است .").show();
                    btnInsert.setClickable(true);
                }
                dialog.dismiss();
                btnInsert.setClickable(true);
            }
        }
    }

    private class AsyncUpdateDrPicWS extends AsyncTask<String, Void, Void> {
        private boolean result;
        String msg = null;
        ProgressDialog dialog;
        final int imageProfileId = 1;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(PersonalInfoActivity.this, "", "در حال تغییر عکس پروفایل ...");
            dialog.getWindow().setGravity(Gravity.END);
            dialog.setCancelable(true);
            btnImgSelect.setClickable(false);
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                result = WebService.invokeUpdateDoctorPicWS(G.UserInfo.getUserName(), G.UserInfo.getPassword(), drPic);
            } catch (PException ex) {
                msg = ex.getMessage();
            }
            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            if (msg != null) {
                dialog.dismiss();
                new MessageBox(PersonalInfoActivity.this, msg).show();
                btnImgSelect.setClickable(true);
            } else {
                if (result) {
                    profileImage.setImageBitmap(roundedDrPic);
                    G.UserInfo.setImgProfile(drPic);
                    if (G.UserInfo.getRole() == UserType.Dr.ordinal()) {
                        G.doctorImageProfile = drPic;

                        if (database.openConnection()) {
                            database.saveImageProfile(imageProfileId, DbBitmapUtility.getBytes(G.doctorImageProfile));
                            database.closeConnection();
                        } else {
                            dialog.dismiss();
                            new MessageBox(PersonalInfoActivity.this, "تغییر عکس پروفایل با مشکل مواجه شده است .").show();
                            btnImgSelect.setClickable(true);
                        }
                    } else {
//                        int nh = (int) (drPic.getHeight() * (64.0 / drPic.getWidth()));
//                        Bitmap scaled = Bitmap.createScaledBitmap(drPic, 64, nh, true);
//                        G.doctorImageProfile = scaled;

                        if (database.openConnection()) {
                            database.saveImageProfile(imageProfileId, DbBitmapUtility.getBytes(G.doctorImageProfile));
                            database.closeConnection();
                        } else {
                            dialog.dismiss();
                            new MessageBox(PersonalInfoActivity.this, "تغییر عکس پروفایل با مشکل مواجه شده است .").show();
                            btnImgSelect.setClickable(true);
                        }
                    }
                }
                dialog.dismiss();
                btnImgSelect.setClickable(true);
            }
        }
    }
}
