package com.example.doctorsbuilding.nav;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.doctorsbuilding.nav.Databases.DatabaseAdapter;
import com.example.doctorsbuilding.nav.Dr.Clinic.DrClinicActivity;
import com.example.doctorsbuilding.nav.Dr.Gallery.GalleryActivity;
import com.example.doctorsbuilding.nav.Dr.Nobat.DrNobatActivity;
import com.example.doctorsbuilding.nav.Dr.Notification.ManagementNotificationActivity;
import com.example.doctorsbuilding.nav.Dr.Profile.DrProfileActivity;
import com.example.doctorsbuilding.nav.Dr.Profile.PersonalInfoActivity;
import com.example.doctorsbuilding.nav.LazyLoad.Gallery3;
import com.example.doctorsbuilding.nav.User.User;
import com.example.doctorsbuilding.nav.User.UserInboxActivity;
import com.example.doctorsbuilding.nav.User.UserMyNobatActivity;
import com.example.doctorsbuilding.nav.User.UserNewsActivity;
import com.example.doctorsbuilding.nav.User.UserProfileActivity;
import com.example.doctorsbuilding.nav.Util.DbBitmapUtility;
import com.example.doctorsbuilding.nav.Util.MessageBox;
import com.example.doctorsbuilding.nav.Web.WebService;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.readystatesoftware.viewbadger.BadgeView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
        , OnMapReadyCallback {

    boolean doubleBackToExitPressedOnce = false;
    NavigationView navigationView = null;
    public UserType menu = UserType.None;
    private SharedPreferences settings;
    private GoogleMap mMap;

    ImageView drImgProfile;
    TextView drName;
    TextView drExpert;
    TextView drAddress;
    TextView drPhone;
    TextView drBiography;
    SupportMapFragment mapFragment;
    ArrayList<MessageInfo> unreadMessages = null;
    RelativeLayout btnUnreadMessage;
    BadgeView badge;
    DatabaseAdapter database;
    private final static int imagePrdileId = 1;
//    double latitude;
//    double longitiude;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        settings = getSharedPreferences("doctorBuilding", 0);

//        if (G.UserInfo == null)
//            G.UserInfo = new User();
//        if (G.officeInfo == null)
//            G.officeInfo = new Office();

        loadUser();
        initViews();
        AsyncGetDoctorPic task = new AsyncGetDoctorPic();
        task.execute();
        updatePage();
//
//        if (menu != UserType.Guest) {
//            AsyncUserInfoWs taskUserInfo = new AsyncUserInfoWs();
//            taskUserInfo.execute();
//        }
//
//        AsyncGetOfficeInfo taskOfficeInfo = new AsyncGetOfficeInfo();
//        taskOfficeInfo.execute();
//
//        AsyncGetDoctorPic taskGetDrPic = new AsyncGetDoctorPic();
//        taskGetDrPic.execute();

    }

    @Override
    protected void onResume() {
        super.onResume();
        updatePage();
    }

    private void updatePage() {
        if (G.UserInfo == null) {
            G.UserInfo = new User();
            G.UserInfo.setUserName(settings.getString("user", ""));
            G.UserInfo.setPassword(settings.getString("pass", ""));
            G.UserInfo.setRole(settings.getInt("role", 0));

            if (G.UserInfo.getUserName().length() == 0 && G.UserInfo.getPassword().length() == 0) {
                G.UserInfo.setUserName("guest");
                G.UserInfo.setPassword("8512046384");
                G.UserInfo.setRole(UserType.Guest.ordinal());
            }
        }
        if (G.officeInfo != null) {

            drName.setText(G.officeInfo.getFirstname().concat(" " + G.officeInfo.getLastname()));
            drExpert.setText(G.officeInfo.getExpertName().concat(" " + G.officeInfo.getSubExpertName()));
            drAddress.setText(G.officeInfo.getAddress());
            drPhone.setText(G.officeInfo.getPhone());
            drBiography.setText(G.officeInfo.getBiography());
            mapFragment.getMapAsync(this);

        }

        if (G.doctorImageProfile != null) {
            drImgProfile.setImageBitmap(G.doctorImageProfile);
            //G.UserInfo.setImgProfile(G.doctorImageProfile);
        } else {
            int id = R.mipmap.doctor;
            drImgProfile.setImageResource(id);
            G.UserInfo.setImgProfile(BitmapFactory.decodeResource(getBaseContext().getResources(), id));
        }
        if (unreadMessages != null) {
            unreadMessages.clear();
        }
        AsyncCallGetUnreadMessagesWs task = new AsyncCallGetUnreadMessagesWs();
        task.execute();


    }

    private void initViews() {
        database = new DatabaseAdapter(MainActivity.this);
        btnUnreadMessage = (RelativeLayout) findViewById(R.id.unreadMessage);
        badge = new BadgeView(MainActivity.this, btnUnreadMessage);
        badge.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
        drImgProfile = (ImageView) findViewById(R.id.content_main_img);
        drName = (TextView) findViewById(R.id.content_main_name);
        drExpert = (TextView) findViewById(R.id.content_main_expert);
        drAddress = (TextView) findViewById(R.id.content_main_address);
        drPhone = (TextView) findViewById(R.id.content_main_tel);
        drBiography = (TextView) findViewById(R.id.content_main_biography);
        //drBiography.setMovementMethod(new ScrollingMovementMethod());

        setNavigationDrawer();

//        FloatingActionButton floatButton = (FloatingActionButton) findViewById(R.id.fab);


        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

        btnUnreadMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (unreadMessages != null && unreadMessages.size() != 0) {
                    ActivityNotificationDialog dialog = new ActivityNotificationDialog(MainActivity.this,
                            android.R.style.Theme_DeviceDefault_Dialog_MinWidth, unreadMessages);
                    dialog.show();
                } else {
                    Toast.makeText(MainActivity.this, "هیچ پیام جدیدی برای خواندن وجود ندارد .", Toast.LENGTH_SHORT).show();
                }
            }
        });

//        floatButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                AsyncCallGetUnreadMessagesWs task = new AsyncCallGetUnreadMessagesWs();
//                task.execute();
//            }
//        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng latLng = new LatLng(G.officeInfo.getLatitude(), G.officeInfo.getLongitude());
        mMap.addMarker(new MarkerOptions().position(latLng));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

    }


    private void loadUser() {

        menu = UserType.values()[settings.getInt("role", 0)];
        switch (menu) {
            case Dr:
                menu = UserType.Dr;
                break;
            case User:
                menu = UserType.User;
                break;
            case secretary:
                menu = UserType.Dr;
                break;
            case None:
                menu = UserType.Guest;
            default:
                break;
        }

    }

    private void logOut() {
        settings.edit().remove("user").apply();
        settings.edit().remove("pass").apply();
        settings.edit().remove("role").apply();
        setNavigationViewMenu(UserType.Guest);
        G.UserInfo = null;
        badge.hide();
        startActivity(new Intent(MainActivity.this, SignInActivity.class));
        finish();

    }

    private void setNavigationDrawer() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        setNavigationViewMenu(menu);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void setNavigationViewMenu(UserType menu) {
        switch (menu) {
            case Guest:
                navigationView.getMenu().setGroupVisible(R.id.navigation_view_user, false);
                navigationView.getMenu().setGroupVisible(R.id.navigation_view_dr, false);
                navigationView.getMenu().setGroupVisible(R.id.navigation_view_signUp, true);
                break;
            case Dr:
                navigationView.getMenu().setGroupVisible(R.id.navigation_view_user, false);
                navigationView.getMenu().setGroupVisible(R.id.navigation_view_signUp, false);
                navigationView.getMenu().setGroupVisible(R.id.navigation_view_dr, true);
                break;
            case User:
                navigationView.getMenu().setGroupVisible(R.id.navigation_view_user, false);
                navigationView.getMenu().setGroupVisible(R.id.navigation_view_signUp, false);
                navigationView.getMenu().setGroupVisible(R.id.navigation_view_user, true);
                break;
        }

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_dr_clinic:
                startActivity(new Intent(MainActivity.this, DrClinicActivity.class));
                break;
            case R.id.nav_dr_profile:
                startActivity(new Intent(MainActivity.this, DrProfileActivity.class));
                break;
            case R.id.nav_dr_personalInfo:
                startActivity(new Intent(MainActivity.this, PersonalInfoActivity.class));
                break;
            case R.id.nav_dr_nobat:
                startActivity(new Intent(MainActivity.this, DrNobatActivity.class));
                break;
            case R.id.nav_dr_gallery:
//                startActivity(new Intent(MainActivity.this, GalleryActivity.class));
                startActivity(new Intent(MainActivity.this, gallery2.class));
                break;
            case R.id.nav_user_gallery:
//                startActivity(new Intent(MainActivity.this, GalleryActivity.class));
                startActivity(new Intent(MainActivity.this, gallery2.class));
                break;
            case R.id.nav_dr_patients:
                startActivity(new Intent(MainActivity.this, ActivityPatientListToday.class));
                break;
//            case R.id.nav_dr_patients:
//                startActivity(new Intent(MainActivity.this, PatientsActivity.class));
//                break;
            case R.id.nav_dr_notification:
                startActivity(new Intent(MainActivity.this, ManagementNotificationActivity.class));
                break;
            case R.id.nav_dr_inbox:
                startActivity(new Intent(MainActivity.this, UserInboxActivity.class));
                break;
            case R.id.nav_dr_map:
                startActivity(new Intent(MainActivity.this, MapActivity.class));
                break;
            case R.id.nav_dr_patientFile:
                startActivity(new Intent(MainActivity.this, ActivitySearchPatient.class));
                break;
            case R.id.nav_user_addTurn:
                startActivity(new Intent(MainActivity.this, DrProfileActivity.class));
                break;
//            case R.id.nav_user_profile:
//                startActivity(new Intent(MainActivity.this, UserProfileActivity.class));
//                break;
            case R.id.nav_user_profile:
                startActivity(new Intent(MainActivity.this, PersonalInfoActivity.class));
                break;
            case R.id.nav_user_patientFile:
                Intent intent = new Intent(MainActivity.this, ActivityPatientFile.class);
                intent.putExtra("patientUserName", G.UserInfo.getUserName());
                startActivity(intent);
                break;
            case R.id.nav_user_nobat:
                startActivity(new Intent(MainActivity.this, UserMyNobatActivity.class));
                break;
            case R.id.nav_user_inbox:
                startActivity(new Intent(MainActivity.this, UserInboxActivity.class));
                break;
            case R.id.nav_user_news:
                startActivity(new Intent(MainActivity.this, UserNewsActivity.class));
                break;
//            case R.id.nav_signUp_dr:
//                startActivity(new Intent(MainActivity.this, PersonalInfoActivity.class));
//                break;
            case R.id.nav_signUp_user:
                startActivity(new Intent(MainActivity.this, UserProfileActivity.class));
                break;
            case R.id.nav_signUp_news:
                startActivity(new Intent(MainActivity.this, UserNewsActivity.class));
                break;
            case R.id.nav_dr_call:
                startActivity(new Intent(MainActivity.this,ContactUs.class));
                break;
            case R.id.nav_user_call:
                startActivity(new Intent(MainActivity.this,ContactUs.class));
                break;
            case R.id.nav_signUp_call:
                break;
            case R.id.nav_signIn:
                startSignInActivity();
                break;
            case R.id.nav_dr_logout:
                logOut();
                break;
            case R.id.nav_user_logout:
                logOut();
                break;
            default:
                break;
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void startSignInActivity() {
        Intent intent = new Intent(MainActivity.this, SignInActivity.class);
        startActivityForResult(intent, 1);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (doubleBackToExitPressedOnce) {
//            super.onBackPressed();
//            moveTaskToBack(true);
            this.finish();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "برای خروج دو بار دکمه BACK را بزنید", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    private class AsyncCallGetUnreadMessagesWs extends AsyncTask<String, Void, Void> {

        String msg = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                unreadMessages = WebService.invokeGetUnreadMessagesWS(G.UserInfo.getUserName(), G.UserInfo.getPassword(), G.officeId);
            } catch (PException ex) {
                msg = ex.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (msg != null) {
                new MessageBox(MainActivity.this, msg).show();
            } else {
                if (unreadMessages != null && unreadMessages.size() != 0) {
                    badge.setText(String.valueOf(unreadMessages.size()));
                    badge.show();
                } else {
                    badge.hide(false);
                }
            }
        }


    }

    private class AsyncGetDoctorPic extends AsyncTask<String, Void, Void> {

        String msg = null;

        @Override
        protected Void doInBackground(String... strings) {
            try {
                String username = G.UserInfo.getUserName();
                String password = G.UserInfo.getPassword();
                G.doctorImageProfile = WebService.invokeGetDoctorPicWS(username, password, G.officeId);
                G.UserInfo.setImgProfile(WebService.invokeGetUserPicWS(username, password));

            } catch (PException ex) {
                msg = ex.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (msg != null) {
                new MessageBox(MainActivity.this, msg).show();
            } else {
                if (G.doctorImageProfile != null) {
                    drImgProfile.setImageBitmap(G.doctorImageProfile);
                    if (database.openConnection()) {
                        database.saveImageProfile(imagePrdileId, DbBitmapUtility.getBytes(G.doctorImageProfile));
                    }

                } else {
                    int id = R.mipmap.doctor;
                    G.doctorImageProfile = BitmapFactory.decodeResource(getBaseContext().getResources(), id);
                    drImgProfile.setImageBitmap(G.doctorImageProfile);
                    if (database.openConnection()) {
                        database.saveImageProfile(imagePrdileId, DbBitmapUtility.getBytes(G.doctorImageProfile));
                    }
                }
                database.closeConnection();
            }
        }
    }
}
//    //get user info ................................................................................
//
//    private class AsyncUserInfoWs extends AsyncTask<String, Void, Void> {
//
//        @Override
//        protected Void doInBackground(String... strings) {
//
//            G.UserInfo = WebService.invokeGetUserInfoWS(G.UserInfo.getUserName(), G.UserInfo.getPassword());
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
//            updatePage();
//        }
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//        }
//    }
//
//    private class AsyncGetOfficeInfo extends AsyncTask<String, Void, Void> {
//
//        @Override
//        protected Void doInBackground(String... strings) {
//
//            G.officeInfo = WebService.invokeGetOfficeInfoWS(G.UserInfo.getUserName(), G.UserInfo.getPassword(), G.officeId);
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
//            updatePage();
//        }
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//        }
//    }
//

