package com.example.doctorsbuilding.nav.Dr.Profile;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.PopupMenu;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.doctorsbuilding.nav.Databases.DatabaseAdapter;
import com.example.doctorsbuilding.nav.G;
import com.example.doctorsbuilding.nav.PException;
import com.example.doctorsbuilding.nav.R;
import com.example.doctorsbuilding.nav.User.City;
import com.example.doctorsbuilding.nav.User.DialogSelectImage;
import com.example.doctorsbuilding.nav.User.State;
import com.example.doctorsbuilding.nav.User.User;
import com.example.doctorsbuilding.nav.UserType;
import com.example.doctorsbuilding.nav.Util.DbBitmapUtility;
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
    private Bitmap userPic;
    private int stateID = -1;

    private TextView txt_name;
    private ImageView profileImage;
    private FloatingActionButton btnImgSelect;
    private Button backBtn;
    private ImageButton btn_setting;
    ProgressDialog progressDialog;
    private DatabaseAdapter database;
    String password = null;
    private PopupMenu popupMenu;

    AsyncCallStateWS getStateTask;
    AsyncCallCityWS getCityTask;
    AsyncCallRegisterWS registerTask;
    AsyncUpdateDrPicWS updatePicTask;
    private static final int CAMERA_REQUEST = 1888;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_personal_info);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        initViews();
        eventListener();
        showDrInfo();
        progressDialog = ProgressDialog.show(PersonalInfoActivity.this, "", "در حال دریافت اطلاعات ...");
        progressDialog.getWindow().setGravity(Gravity.END);
        progressDialog.setCancelable(true);
        btnInsert.setClickable(false);

        getStateTask = new AsyncCallStateWS();
        getStateTask.execute();

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

        txt_name.setText(G.UserInfo.getFirstName().concat(" ").concat(G.UserInfo.getLastName()));
        txtFirstName.setText(G.UserInfo.getFirstName());
        txtLastName.setText(G.UserInfo.getLastName());
        txtMobile.setText(G.UserInfo.getPhone());
        txtEmail.setText(G.UserInfo.getEmail());
        profileImage.setImageBitmap(G.UserInfo.getImgProfile());
    }

    private void initViews() {
        popupMenu = new PopupMenu(PersonalInfoActivity.this, btn_setting);
        popupMenu.inflate(R.menu.menu_action_bar);
        btn_setting = (ImageButton) findViewById(R.id.personalInfo_setting);
        txt_name = (TextView) findViewById(R.id.profile_name);
        backBtn = (Button) findViewById(R.id.personalInfo_backBtn);
        txtFirstName = (EditText) findViewById(R.id.dr_FirstName);
        txtLastName = (EditText) findViewById(R.id.dr_LastName);
        txtMobile = (EditText) findViewById(R.id.dr_Mobile);
        txtUserName = (EditText) findViewById(R.id.dr_UserName);
        txtUserName.setVisibility(View.GONE);
        txtPassword = (EditText) findViewById(R.id.dr_Password);
        txtPassword.setVisibility(View.GONE);
        txtRePassword = (EditText) findViewById(R.id.dr_ConfirmPassword);
        txtRePassword.setVisibility(View.GONE);
        txtEmail = (EditText) findViewById(R.id.dr_email);
        spinnerState = (Spinner) findViewById(R.id.dr_profile_state);
        spinnerCity = (Spinner) findViewById(R.id.dr_profile_city);
        btnInsert = (Button) findViewById(R.id.dr_btnPersonalInfoInsert);
        profileImage = (ImageView) findViewById(R.id.dr_imgProfile);
        btnImgSelect = (FloatingActionButton) findViewById(R.id.dr_btnImgProfile);
        database = new DatabaseAdapter(PersonalInfoActivity.this);

    }

    private void eventListener() {

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.menu_change_pass) {
                    final DialogChangePassword dialog = new DialogChangePassword(PersonalInfoActivity.this, android.R.style.Theme_DeviceDefault_Light_Dialog);
                    dialog.show();
                    dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                            if (dialog.getResult()) {
                                password = dialog.getPassword();
                                try {
                                    password = Hashing.SHA1(password);
                                } catch (NoSuchAlgorithmException e) {
                                } catch (UnsupportedEncodingException e) {
                                }
                                registerTask = new AsyncCallRegisterWS();
                                registerTask.execute();
                            }
                        }
                    });
                }
                return false;
            }
        });

        btn_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MenuPopupHelper menuHelper = new MenuPopupHelper(PersonalInfoActivity.this, (MenuBuilder) popupMenu.getMenu(), btn_setting);
                menuHelper.setForceShowIcon(true);
                menuHelper.show();
            }
        });

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
                final DialogSelectImage dialog2 = new DialogSelectImage(PersonalInfoActivity.this, android.R.style.Theme_DeviceDefault_Light_Dialog);
                dialog2.show();
                dialog2.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        if (dialog2.getSourceType().equals("camera")) {
                            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(cameraIntent, CAMERA_REQUEST);
                        } else if (dialog2.getSourceType().equals("gallery")) {
                            changePic();
                        }
                    }
                });

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
                        userPic = Bitmap.createScaledBitmap(bitmap, 512, nh, true);
                        updatePicTask = new AsyncUpdateDrPicWS();
                        updatePicTask.execute();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        }
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                    int nh = (int) (bitmap.getHeight() * (512.0 / bitmap.getWidth()));
                    userPic = Bitmap.createScaledBitmap(bitmap, 512, nh, true);
                    updatePicTask = new AsyncUpdateDrPicWS();
                    updatePicTask.execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }

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
                if (stateList == null) {
                    stateID = G.UserInfo.getStateID();
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
                    for (int i = 0; i < stateList.size(); i++) {
                        if (stateList.get(i).GetStateID() == G.UserInfo.getStateID()) {
                            spinnerState.setSelection(i);
                            break;
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
                    for (int i = 0; i < cityList.size(); i++) {
                        if (cityList.get(i).GetCityID() == G.UserInfo.getCityID()) {
                            spinnerCity.setSelection(i);
                            break;
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
            btnImgSelect.setClickable(false);
            btn_setting.setClickable(false);
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                result = WebService.invokeUpdateUserWS(G.UserInfo.getUserName(), G.UserInfo.getPassword(), setUserData());
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
                btnInsert.setClickable(true);
                btn_setting.setClickable(true);
            } else {
                if (result.equals("OK")) {
                    Toast.makeText(PersonalInfoActivity.this, "ثبت مشخصات با موفقیت انجام شد .", Toast.LENGTH_SHORT).show();
                    G.UserInfo = user;
                    SharedPreferences.Editor editor = G.getSharedPreferences().edit();
                    editor.putString("pass", G.UserInfo.getPassword());
                    editor.apply();
                    finish();
                } else {
                    new MessageBox(PersonalInfoActivity.this, "خطایی در ثبت اطلاعات رخ داده است .").show();
                }
                dialog.dismiss();
                btnImgSelect.setClickable(true);
                btnInsert.setClickable(true);
                btn_setting.setClickable(true);
            }
        }

        private User setUserData() {
            user = new User();
            user.setFirstName(txtFirstName.getText().toString().trim());
            user.setLastName(txtLastName.getText().toString().trim());
            user.setUserName(G.UserInfo.getUserName());
            user.setPassword(password != null ? password : G.UserInfo.getPassword());
            user.setPhone(txtMobile.getText().toString().trim());
            user.setEmail(txtEmail.getText().toString().trim());
            user.setRole(G.UserInfo.getRole());
            user.setStateID((stateList.get(spinnerState.getSelectedItemPosition()).GetStateID()));
            user.setCityID((cityList.get(spinnerCity.getSelectedItemPosition()).GetCityID()));
            user.setImgProfile(G.UserInfo.getImgProfile());
            return user;
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
            btnInsert.setClickable(false);
            btn_setting.setClickable(false);
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                result = WebService.invokeUpdateUserPicWS(G.UserInfo.getUserName(), G.UserInfo.getPassword(), userPic);
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
                btnInsert.setClickable(true);
                btn_setting.setClickable(true);
            } else {
                if (result) {
                    profileImage.setImageBitmap(userPic);
                    G.UserInfo.setImgProfile(userPic);
                    if (G.UserInfo.getRole() == UserType.Dr.ordinal())
                        G.doctorImageProfile = userPic;

                    if (database.openConnection()) {
                        database.saveImageProfile(imageProfileId, DbBitmapUtility.getBytes(userPic));
                        database.closeConnection();
                    }
                } else {
                    new MessageBox(PersonalInfoActivity.this, "تغییر عکس پروفایل با مشکل مواجه شده است .").show();
                }
                dialog.dismiss();
                btnImgSelect.setClickable(true);
                btnInsert.setClickable(true);
                btn_setting.setClickable(true);
            }
        }
    }
}
