package com.example.doctorsbuilding.nav;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.doctorsbuilding.nav.Databases.DatabaseAdapter;
import com.example.doctorsbuilding.nav.Dr.Clinic.DrClinicActivity;
import com.example.doctorsbuilding.nav.Dr.Nobat.DrNobatActivity;
import com.example.doctorsbuilding.nav.Dr.Notification.ManagementNotificationActivity;
import com.example.doctorsbuilding.nav.Dr.Profile.DrProfileActivity;
import com.example.doctorsbuilding.nav.Dr.Profile.PersonalInfoActivity;
import com.example.doctorsbuilding.nav.User.User;
import com.example.doctorsbuilding.nav.User.UserInboxActivity;
import com.example.doctorsbuilding.nav.User.UserMyNobatActivity;
import com.example.doctorsbuilding.nav.User.UserNewsActivity;
import com.example.doctorsbuilding.nav.User.UserProfileActivity;
import com.example.doctorsbuilding.nav.Util.DbBitmapUtility;
import com.example.doctorsbuilding.nav.Util.MessageBox;
import com.example.doctorsbuilding.nav.Util.RoundedImageView;
import com.example.doctorsbuilding.nav.Web.WebService;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.readystatesoftware.viewbadger.BadgeView;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
        , OnMapReadyCallback {

    boolean doubleBackToExitPressedOnce = false;
    NavigationView navigationView = null;
    public UserType menu = UserType.None;
    private SharedPreferences settings;
    private GoogleMap mMap;

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
    final static int imagePrdileId = 1;
    CirclePageIndicator indicator;
    static ViewPager mPager;
    ArrayList<PhotoDesc> baners;
    asyncGetGalleryPic asyncGetGalleryPic;
    AsyncGetDoctorPic getDoctorPic;
    AsyncCallGetUnreadMessagesWs asyncGetMessage;
    ArrayList<Boolean> banerTaskList;
    asyncGetImageIdFromWeb asyncBaner;
    ImageView btn_menu;
    DrawerLayout mDrawerLayout;
    ImageView menu_header_image;
    TextView menu_header_name;
    TextView menu_header_version;
    ProgressBar baner_progress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        settings = G.getSharedPreferences();
        loadUser();
        initViews();

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onResume() {
        super.onResume();
        updatePage();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (asyncGetGalleryPic != null)
            asyncGetGalleryPic.cancel(true);

        if (getDoctorPic != null) {
            getDoctorPic.cancel(true);
        }

        if (asyncGetMessage != null) {
            asyncGetMessage.cancel(true);
        }
        if (asyncBaner != null) {
            asyncBaner.cancel(true);
        }
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
        } else {
            setNavigationViewMenu(menu);
        }
        if (G.officeInfo != null) {

            drName.setText(G.officeInfo.getFirstname().concat(" " + G.officeInfo.getLastname()));
            drExpert.setText(G.officeInfo.getSubExpertName());
            drAddress.setText(G.officeInfo.getAddress());
            drPhone.setText(G.officeInfo.getPhone());
            drBiography.setText(G.officeInfo.getBiography());
            mapFragment.getMapAsync(this);

        }

        if (G.doctorImageProfile != null) {
            //  drImgProfile.setImageBitmap(G.doctorImageProfile);
            setNavigationViewMenu(menu);
            G.UserInfo.setImgProfile(G.doctorImageProfile);
        } else {
            int id = R.mipmap.doctor;
            G.UserInfo.setImgProfile(BitmapFactory.decodeResource(getBaseContext().getResources(), id));
        }
        if (unreadMessages != null) {
            unreadMessages.clear();
        }

        database = new DatabaseAdapter(MainActivity.this);
        asyncBaner = new asyncGetImageIdFromWeb();
        asyncBaner.execute();

        if(G.UserInfo.getRole()!=0) {
            getDoctorPic = new AsyncGetDoctorPic();
            getDoctorPic.execute();
            asyncGetMessage = new AsyncCallGetUnreadMessagesWs();
            asyncGetMessage.execute();
        }


    }

    private void initSlideShow(ArrayList<Integer> imageIdsInWeb) {

        banerTaskList = new ArrayList<Boolean>();
        baners = new ArrayList<PhotoDesc>();
        for (int i = 0; i < imageIdsInWeb.size(); i++) {
            banerTaskList.add(false);
            PhotoDesc aks = new PhotoDesc();
            aks.setId(imageIdsInWeb.get(i));
            aks.setDescription("");
            aks.setDate("");
            aks.setPhoto(BitmapFactory.decodeResource(getResources(), R.mipmap.image_holder));
            baners.add(aks);
        }
        mPager.setAdapter(new SlidingImage_Adapter(MainActivity.this, baners));
        indicator.setViewPager(mPager);
        final float density = getResources().getDisplayMetrics().density;
        indicator.setRadius(5 * density);

//        NUM_PAGES = ImagesArray.size();
//
//        // Auto start of viewpager
//        final Handler handler = new Handler();
//        final Runnable Update = new Runnable() {
//            public void run() {
//                if (currentPage == NUM_PAGES) {
//                    currentPage = 0;
//                }
//                mPager.setCurrentItem(currentPage++, true);
//            }
//        };
//        swipeTimer = new Timer();
//        swipeTimer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                handler.post(Update);
//            }
//        }, 3000, 3000);

        banerTaskList.set(0, true);
        asyncGetGalleryPic task = new asyncGetGalleryPic();
        task.execute(String.valueOf(baners.get(0).getId()), String.valueOf(0));
        indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                if (!banerTaskList.get(position)) {
                    banerTaskList.set(position, true);
                    asyncGetGalleryPic task = new asyncGetGalleryPic();
                    task.execute(String.valueOf(baners.get(position).getId()), String.valueOf(position));
                }
            }

            @Override
            public void onPageScrolled(int pos, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int pos) {

            }
        });

    }

    private void initViews() {

        //final SlidingUpPanelLayout layout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);

        //layout.setDragView(findViewById(R.id.content_main_info));

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar33);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
//        collapsingToolbar.setTitleEnabled(false);
//        collapsingToolbar.setTitle("Second Activity");

        mPager = (ViewPager) findViewById(R.id.pager);

//        mPager.setPageMargin(20);
//        mPager.setPageMarginDrawable(R.color.blueColor);
//        mPager.setPageMarginDrawable(int)

//        int pagerPadding = 50;
//        mPager.setClipToPadding(false);
//        mPager.setPadding(pagerPadding, 0, pagerPadding, 0);
        indicator = (CirclePageIndicator)
                findViewById(R.id.indicator);
        baner_progress = (ProgressBar) findViewById(R.id.baner_progress);
        btnUnreadMessage = (RelativeLayout) findViewById(R.id.unreadMessage33);
        badge = new BadgeView(MainActivity.this, btnUnreadMessage);
        badge.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
        badge.setBadgeBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.blueColor));
        drName = (TextView) findViewById(R.id.content_main_name);
        drExpert = (TextView) findViewById(R.id.content_main_expert);
        drAddress = (TextView) findViewById(R.id.content_main_address);
        drPhone = (TextView) findViewById(R.id.content_main_tel);
        drBiography = (TextView) findViewById(R.id.content_main_biography);
        btn_menu = (ImageView) findViewById(R.id.menu_btn);

        setNavigationDrawer();

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

        btnUnreadMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(menu != UserType.Guest) {
                    if (unreadMessages != null && unreadMessages.size() != 0) {
                        ActivityNotificationDialog dialog = new ActivityNotificationDialog(MainActivity.this,
                                android.R.style.Theme_DeviceDefault_Dialog_MinWidth, unreadMessages);
                        dialog.show();
                    } else {
                        Toast.makeText(MainActivity.this, "هیچ پیام جدیدی برای خواندن وجود ندارد .", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        btn_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawerLayout.openDrawer(Gravity.RIGHT);
            }
        });


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng latLng = new LatLng(G.officeInfo.getLatitude(), G.officeInfo.getLongitude());
        mMap.addMarker(new MarkerOptions().position(latLng));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

    }


    private void loadUser() {
//        try {
//            menu = UserType.values()[settings.getInt("role", 0)];
//        }catch (Exception ex){
//            menu = UserType.Guest;
//        }
        menu = UserType.values()[G.UserInfo.getRole()];

        switch (menu) {
            case Dr:
                menu = UserType.Dr;
                break;
            case User:
                menu = UserType.User;
                break;
            case secretary:
                menu = UserType.secretary;
                break;
            case None:
                menu = UserType.Guest;
            default:
                break;
        }

    }

    private void logOut() {
        if (asyncGetGalleryPic != null)
            asyncGetGalleryPic.cancel(true);

        if (getDoctorPic != null) {
            getDoctorPic.cancel(true);
        }

        if (asyncGetMessage != null) {
            asyncGetMessage.cancel(true);
        }
        if (asyncBaner != null) {
            asyncBaner.cancel(true);
        }
        settings.edit().remove("user").apply();
        settings.edit().remove("pass").apply();
        settings.edit().remove("role").apply();
        setNavigationViewMenu(UserType.Guest);
        G.UserInfo = null;
        badge.hide();
//
//        startActivity(new Intent(MainActivity.this, SignInActivity.class));
//        finish();

    }

    private void setNavigationDrawer() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        menu_header_image = (ImageView) navigationView.findViewById(R.id.img_profile33);
        menu_header_name = (TextView) navigationView.findViewById(R.id.name33);
        menu_header_version = (TextView) navigationView.findViewById(R.id.pezashyar_type33);
//        setNavigationViewMenu(menu);
        navigationView.setNavigationItemSelectedListener(this);


        setNavigationViewMenuFontStyle();

    }

    private void setNavigationViewMenuFontStyle() {
        Menu m = navigationView.getMenu();
        for (int i = 0; i < m.size(); i++) {
            MenuItem mi = m.getItem(i);

            //for aapplying a font to subMenu ...
            SubMenu subMenu = mi.getSubMenu();
            if (subMenu != null && subMenu.size() > 0) {
                for (int j = 0; j < subMenu.size(); j++) {
                    MenuItem subMenuItem = subMenu.getItem(j);
                    applyFontToMenuItem(subMenuItem);
                }
            }

            //the method we have create in activity
            applyFontToMenuItem(mi);
        }
    }

    private void applyFontToMenuItem(MenuItem mi) {
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/IRANSans.ttf");
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("", font), 0, mNewTitle.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
    }

    private void setNavigationViewMenu(UserType menu) {

        switch (menu) {
            case Guest:
                navigationView.getMenu().setGroupVisible(R.id.navigation_view_user, false);
                navigationView.getMenu().setGroupVisible(R.id.navigation_view_dr, false);
                navigationView.getMenu().setGroupVisible(R.id.navigation_view_secretary, false);
                navigationView.getMenu().setGroupVisible(R.id.navigation_view_signUp, true);

                menu_header_name.setText("کاربر میهمان");
                menu_header_version.setText("");
                menu_header_image.setImageResource(R.mipmap.ic_launcher);
                break;
            case Dr:
                navigationView.getMenu().setGroupVisible(R.id.navigation_view_user, false);
                navigationView.getMenu().setGroupVisible(R.id.navigation_view_signUp, false);
                navigationView.getMenu().setGroupVisible(R.id.navigation_view_secretary, false);
                navigationView.getMenu().setGroupVisible(R.id.navigation_view_dr, true);

                menu_header_name.setText(G.UserInfo.getFirstName().concat(" " + G.UserInfo.getLastName()));
                menu_header_version.setText("نسخه پزشک");
                try {
                    Bitmap drPic = RoundedImageView.getCroppedBitmap(G.UserInfo.getImgProfile(), 160);
                    menu_header_image.setImageBitmap(drPic);
                }catch (Exception ex){
                    Bitmap drPic = RoundedImageView.getCroppedBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.doctor), 160);
                    menu_header_image.setImageBitmap(drPic);
                }

                break;
            case secretary:
                navigationView.getMenu().setGroupVisible(R.id.navigation_view_user, false);
                navigationView.getMenu().setGroupVisible(R.id.navigation_view_signUp, false);
                navigationView.getMenu().setGroupVisible(R.id.navigation_view_dr, false);
                navigationView.getMenu().setGroupVisible(R.id.navigation_view_secretary, true);
                menu_header_name.setText(G.UserInfo.getFirstName().concat(" " + G.UserInfo.getLastName()));
                menu_header_version.setText("نسخه منشی");
                try {
                    Bitmap drPic = RoundedImageView.getCroppedBitmap(G.UserInfo.getImgProfile(), 160);
                    menu_header_image.setImageBitmap(drPic);
                }catch (Exception ex){
                    Bitmap drPic = RoundedImageView.getCroppedBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.doctor), 160);
                    menu_header_image.setImageBitmap(drPic);
                }
                break;
            case User:
                navigationView.getMenu().setGroupVisible(R.id.navigation_view_user, false);
                navigationView.getMenu().setGroupVisible(R.id.navigation_view_signUp, false);
                navigationView.getMenu().setGroupVisible(R.id.navigation_view_secretary, false);
                navigationView.getMenu().setGroupVisible(R.id.navigation_view_user, true);
                menu_header_name.setText(G.UserInfo.getFirstName().concat(" " + G.UserInfo.getLastName()));
                menu_header_version.setText("نسخه بیمار");
                try {
                    Bitmap drPic = RoundedImageView.getCroppedBitmap(G.UserInfo.getImgProfile(), 160);
                    menu_header_image.setImageBitmap(drPic);
                }catch (Exception ex){
                    Bitmap drPic = RoundedImageView.getCroppedBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.doctor), 160);
                    menu_header_image.setImageBitmap(drPic);
                }
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
                startActivity(new Intent(MainActivity.this, gallery2.class));
                break;
            case R.id.nav_dr_patients:
                startActivity(new Intent(MainActivity.this, ActivityPatientListToday.class));
                break;
            case R.id.nav_dr_taskes:
                startActivity(new Intent(MainActivity.this, ActivityManagementTaskes.class));
                break;
            case R.id.nav_dr_secretary:
                startActivity(new Intent(MainActivity.this, ActivityManageSecretary.class));
                break;
            case R.id.nav_dr_call:
                startActivity(new Intent(MainActivity.this, ContactUs.class));
                break;
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
            case R.id.nav_dr_logout:
                logOut();
                break;
            ////////////////////////////////////////////////////////////////////////////////////////////////////
            case R.id.nav_secretary_clinic:
                startActivity(new Intent(MainActivity.this, DrClinicActivity.class));
                break;
            case R.id.nav_secretary_profile:
                startActivity(new Intent(MainActivity.this, DrProfileActivity.class));
                break;
            case R.id.nav_secretary_personalInfo:
                startActivity(new Intent(MainActivity.this, PersonalInfoActivity.class));
                break;
            case R.id.nav_secretary_nobat:
                startActivity(new Intent(MainActivity.this, DrNobatActivity.class));
                break;
            case R.id.nav_secretary_gallery:
                startActivity(new Intent(MainActivity.this, gallery2.class));
                break;
            case R.id.nav_secretary_patients:
                startActivity(new Intent(MainActivity.this, ActivityPatientListToday.class));
                break;
            case R.id.nav_secretary_taskes:
                startActivity(new Intent(MainActivity.this, ActivityManagementTaskes.class));
                break;
            case R.id.nav_secretary_call:
                startActivity(new Intent(MainActivity.this, ContactUs.class));
                break;
            case R.id.nav_secretary_notification:
                startActivity(new Intent(MainActivity.this, ManagementNotificationActivity.class));
                break;
            case R.id.nav_secretary_inbox:
                startActivity(new Intent(MainActivity.this, UserInboxActivity.class));
                break;
            case R.id.nav_secretary_map:
                startActivity(new Intent(MainActivity.this, MapActivity.class));
                break;
            case R.id.nav_secretary_patientFile:
                startActivity(new Intent(MainActivity.this, ActivitySearchPatient.class));
                break;
            case R.id.nav_secretary_logout:
                logOut();
                break;
            ////////////////////////////////////////////////////////////////////////////////////////////////////
            case R.id.nav_user_addTurn:
                startActivity(new Intent(MainActivity.this, DrProfileActivity.class));
                break;
            case R.id.nav_user_profile:
                startActivity(new Intent(MainActivity.this, PersonalInfoActivity.class));
                break;
            case R.id.nav_user_patientFile:
                Intent intent = new Intent(MainActivity.this, ActivityPatientFile.class);
                intent.putExtra("patientUserName", G.UserInfo.getUserName());
                startActivity(intent);
                break;
            case R.id.nav_user_call:
                startActivity(new Intent(MainActivity.this, ContactUs.class));
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
            case R.id.nav_user_gallery:
                startActivity(new Intent(MainActivity.this, gallery2.class));
                break;
            case R.id.nav_user_logout:
                logOut();
                break;
            //////////////////////////////////////////////////////////////////////////////////////////////////
            case R.id.nav_signUp_user:
                startActivity(new Intent(MainActivity.this, UserProfileActivity.class));
                break;
            case R.id.nav_signUp_news:
                startActivity(new Intent(MainActivity.this, UserNewsActivity.class));
                break;
            case R.id.nav_signUp_call:
                break;
            case R.id.nav_signIn:
                startSignInActivity();
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
                    if (database.openConnection()) {
                        database.saveImageProfile(imagePrdileId, DbBitmapUtility.getBytes(G.doctorImageProfile));
                    }

                } else {
                    int id = R.mipmap.doctor;
                    G.doctorImageProfile = BitmapFactory.decodeResource(getBaseContext().getResources(), id);
                    G.UserInfo.setImgProfile(G.doctorImageProfile);
                    if (database.openConnection()) {
                        database.saveImageProfile(imagePrdileId, DbBitmapUtility.getBytes(G.doctorImageProfile));
                    }
                }
            }
        }
    }

    private class asyncGetGalleryPic extends AsyncTask<String, Void, Void> {
        private String msg = null;
        private PhotoDesc photo;
        private int photoId;
        private int currentPageNum;
        private boolean existInPhone = true;

        @Override
        protected Void doInBackground(String... strings) {
            try {

                photoId = Integer.parseInt(strings[0]);
                currentPageNum = Integer.parseInt(strings[1]);
                if(database.openConnection()) {
                    photo = database.getImageFromGallery(photoId);
                }
                if (photo == null) {
                    existInPhone = false;
                    photo = WebService.invokeGetGalleryPicWS(G.UserInfo.getUserName(), G.UserInfo.getPassword()
                            , G.officeId, photoId);
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
                new MessageBox(MainActivity.this, msg).show();
            } else {
                if (photo != null) {
                    if (!existInPhone) {
                        if (database.openConnection()) {
                            database.saveImageToGallery(photo.getId(), photo.getDate(),
                                    photo.getDescription(), DbBitmapUtility.getBytes(photo.getPhoto()));
                        }
                    }

                    baners.set(currentPageNum, photo);
                    mPager.getAdapter().notifyDataSetChanged();

                }
            }

        }
    }


    private class asyncGetImageIdFromWeb extends AsyncTask<String, Void, Void> {

        private ArrayList<Integer> imageIdsInWeb = null;
        private String msg = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (banerTaskList == null)
                baner_progress.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                imageIdsInWeb = WebService.invokegetAllGalleyPicIdWS(G.UserInfo.getUserName(), G.UserInfo.getPassword(), G.officeId);
            } catch (PException ex) {
                msg = ex.getMessage();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            baner_progress.setVisibility(View.GONE);
            if (msg != null) {
                new MessageBox(MainActivity.this, msg).show();
            } else {
                if (imageIdsInWeb != null && imageIdsInWeb.size() > 0) {

                    initSlideShow(imageIdsInWeb);

                }
            }
        }
    }
}

