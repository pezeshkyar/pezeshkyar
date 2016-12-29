package com.example.doctorsbuilding.nav.MainForm;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.example.doctorsbuilding.nav.ActivityFactor;
import com.example.doctorsbuilding.nav.ActivityNotificationDialog;
import com.example.doctorsbuilding.nav.ActivityPaymnet;
import com.example.doctorsbuilding.nav.ContactUs;
import com.example.doctorsbuilding.nav.CustomNavListView;
import com.example.doctorsbuilding.nav.Databases.DatabaseAdapter;
import com.example.doctorsbuilding.nav.Dr.Clinic.Office;
import com.example.doctorsbuilding.nav.Dr.Profile.PersonalInfoActivity;
import com.example.doctorsbuilding.nav.G;
import com.example.doctorsbuilding.nav.MessageInfo;
import com.example.doctorsbuilding.nav.PException;
import com.example.doctorsbuilding.nav.R;
import com.example.doctorsbuilding.nav.SignInActivity;
import com.example.doctorsbuilding.nav.User.User;
import com.example.doctorsbuilding.nav.User.UserInboxActivity;
import com.example.doctorsbuilding.nav.User.UserNewsActivity;
import com.example.doctorsbuilding.nav.User.UserProfileActivity;
import com.example.doctorsbuilding.nav.UserType;
import com.example.doctorsbuilding.nav.Util.MessageBox;
import com.example.doctorsbuilding.nav.Web.WebService;
import com.example.doctorsbuilding.nav.support.ActivityTickets;
import com.readystatesoftware.viewbadger.BadgeView;

import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by hossein on 11/16/2016.
 */
public class ActivityOffices extends AppCompatActivity {

    BadgeView badge;
    FloatingActionButton addButton;
    Button btn_signIn;
    Button btn_signUp;
    RelativeLayout unreadMessageLayout;
    FrameLayout welcomePage;
    DrawerLayout mDrawer;
    NavigationView mNavigation = null;
    ImageView btn_menu;
    ListView officesListView;
    ListView doctorsListView;
    ArrayList<Office> offices = new ArrayList<Office>();
    ArrayList<Office> doctors = new ArrayList<Office>();
    ArrayList<MessageInfo> unreadMessages = null;
    ViewFlipper mViewFlipper;
    CustomOfficesListAdapter adapter_office = null;
    CustomDoctorsListAdapter adapter_doctors = null;
    DatabaseAdapter database = null;
    AsyncGetOfficeForUser task_getOffices = null;
    AsyncGetDoctorPic task_getDoctorPic = null;
    AsyncGetOfficeForDoctorOrSercretary task_getOfficeForDoctorOrSercretary;
    AsyncCallGetUnreadMessagesWs task_unreadMessages;
    private CustomNavListView adapter_nav;
    private ListView menu_listview;
    private ArrayList<MenuItem> menuItems = new ArrayList<MenuItem>();
    private static final int MY_OFFICE = 0;
    private static final int MY_DOCTOR = 1;
    private static final int LOGIN_REQUEST = 1;
    private static final int SIGNUP_REQUEST = 2;
    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_public_main);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        G.setStatusBarColor(ActivityOffices.this);
        initViews();
        eventListener();

        adapter_office = new CustomOfficesListAdapter(ActivityOffices.this, new ArrayList<Office>());
        officesListView.setAdapter(adapter_office);
        adapter_doctors = new CustomDoctorsListAdapter(ActivityOffices.this, new ArrayList<Office>());
        doctorsListView.setAdapter(adapter_doctors);

        initActivity();

    }

    @Override
    protected void onResume() {
        super.onResume();
        readSharedPrefrence();
        if (G.UserInfo.getUserName() != null && G.UserInfo.getUserName().length() != 0) {
            task_unreadMessages = new AsyncCallGetUnreadMessagesWs();
            task_unreadMessages.execute();
            if (G.UserInfo.getRole() == UserType.User.ordinal()) {
                setUserLayout(false);
            }
        }

    }

    private void readSharedPrefrence() {
        G.UserInfo.setUserName(G.getSharedPreferences().getString("user", ""));
        G.UserInfo.setPassword(G.getSharedPreferences().getString("pass", ""));
        G.UserInfo.setRole(G.getSharedPreferences().getInt("role", 0));
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (task_getOffices != null) {
            task_getOffices.cancel(true);
        }
        if (task_getDoctorPic != null) {
            task_getDoctorPic.cancel(true);
        }
        if (task_getOfficeForDoctorOrSercretary != null) {
            task_getOfficeForDoctorOrSercretary.cancel(true);
        }
        if (task_unreadMessages != null) {
            task_unreadMessages.cancel(true);
        }
    }

    private void initActivity() {
        if (G.UserInfo == null)
            G.UserInfo = new User();
        G.UserInfo.setUserName(G.getSharedPreferences().getString("user", ""));
        G.UserInfo.setPassword(G.getSharedPreferences().getString("pass", ""));
        if (G.UserInfo != null && G.UserInfo.getUserName().length() != 0 && G.UserInfo.getPassword().length() != 0) {

            UserType user = UserType.values()[G.getSharedPreferences().getInt("role", 0)];
            if (user == UserType.Dr || user == UserType.secretary)
                setDoctorLayout(false);

            else if (user == UserType.User)
                setUserLayout(false);

        } else {
            setWellcomeLayout();
        }
    }

    private void initViews() {
        adapter_nav = new CustomNavListView(ActivityOffices.this, new ArrayList<MenuItem>());
        menu_listview = (ListView) findViewById(R.id.offices_nav_lv);
        menu_listview.setAdapter(adapter_nav);
        unreadMessageLayout = (RelativeLayout) findViewById(R.id.unreadMessage);
        badge = new BadgeView(ActivityOffices.this, unreadMessageLayout);
        badge.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
        database = new DatabaseAdapter(ActivityOffices.this);
        welcomePage = (FrameLayout) findViewById(R.id.welcome_page);
        btn_signIn = (Button) findViewById(R.id.welcome_signIn);
        btn_signUp = (Button) findViewById(R.id.welcome_signUp);
        mViewFlipper = (ViewFlipper) findViewById(R.id.offices_viewFlipper);
        officesListView = (ListView) findViewById(R.id.offices_lv_my_office);
        doctorsListView = (ListView) findViewById(R.id.offices_lv_my_doctor);
        btn_menu = (ImageView) findViewById(R.id.offices_menu_btn);
        mDrawer = (DrawerLayout) findViewById(R.id.offices_drawer);
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;

        if (currentapiVersion >= Build.VERSION_CODES.JELLY_BEAN) {
            ViewCompat.setLayoutDirection(mDrawer, ViewCompat.LAYOUT_DIRECTION_RTL);
        }
        addButton = (FloatingActionButton) findViewById(R.id.offices_add_dr);
        mNavigation = (NavigationView) findViewById(R.id.offices_nav);
        setNavigationDrawer();

    }

    private void eventListener() {
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (G.UserInfo != null && G.UserInfo.getUserName().length() != 0 && G.UserInfo.getPassword().length() != 0) {
                    startActivity(new Intent(ActivityOffices.this, ActivityAllDoctors.class));
                } else {
                    startActivity(new Intent(ActivityOffices.this, SignInActivity.class));
                }
            }
        });
        btn_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawer.openDrawer(Gravity.RIGHT);
            }
        });

        menu_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                switch (menuItems.get(position).getItemId()) {
                    case R.id.nav1_signIn:
                        btn_signIn.performClick();
                        break;
                    case R.id.nav1_signUp:
                        btn_signUp.performClick();
                        break;
                    case R.id.nav1_news:
                        startActivity(new Intent(ActivityOffices.this, UserNewsActivity.class));
                        break;
                    case R.id.nav1_about:
//                        startActivity(new Intent(ActivityOffices.this, ContactUs.class));
                        startActivity(new Intent(ActivityOffices.this, ActivityFactor.class));
                        break;
                    case R.id.nav1_support:
                        startActivity(new Intent(ActivityOffices.this, ActivityTickets.class));
                        break;
                    case R.id.nav1_logout:
                        logOut();
                        break;
                    case R.id.nav1_account:
                        startActivity(new Intent(ActivityOffices.this, PersonalInfoActivity.class));
                        break;
                    case R.id.nav1_mydoctor:
                        startActivity(new Intent(ActivityOffices.this, ActivityMyDoctors.class));
                        break;
                    case R.id.nav1_inbox:
                        startActivity(new Intent(ActivityOffices.this, UserInboxActivity.class));
                        break;
                }
                mDrawer.closeDrawers();
            }
        });


        btn_signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(ActivityOffices.this, SignInActivity.class), LOGIN_REQUEST);
            }
        });
        btn_signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(ActivityOffices.this, UserProfileActivity.class), SIGNUP_REQUEST);
            }
        });
        unreadMessageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (unreadMessages != null && unreadMessages.size() != 0) {
                    ActivityNotificationDialog dialog = new ActivityNotificationDialog(ActivityOffices.this,
                            android.R.style.Theme_DeviceDefault_Dialog_MinWidth, unreadMessages);
                    dialog.show();
                } else {
                    Toast.makeText(ActivityOffices.this, "هیچ پیام جدیدی برای خواندن وجود ندارد .", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setNavigationItem(UserType user) {
        PopupMenu p = new PopupMenu(this, null);
        Menu menu = p.getMenu();
        getMenuInflater().inflate(R.menu.activity_public_main_drawer, menu);
        if (user == UserType.Guest) {
            menuItems.add(menu.findItem(R.id.nav1_signIn));
            menuItems.add(menu.findItem(R.id.nav1_signUp));
            menuItems.add(menu.findItem(R.id.nav1_intro));
            menuItems.add(menu.findItem(R.id.nav1_about));
        } else if (user == UserType.User) {
            menuItems.add(menu.findItem(R.id.nav1_account));
            menuItems.add(menu.findItem(R.id.nav1_inbox));
            menuItems.add(menu.findItem(R.id.nav1_support));
            menuItems.add(menu.findItem(R.id.nav1_intro));
            menuItems.add(menu.findItem(R.id.nav1_logout));
        } else if (user == UserType.Dr || user == UserType.secretary) {
            menuItems.add(menu.findItem(R.id.nav1_account));
            menuItems.add(menu.findItem(R.id.nav1_inbox));
            menuItems.add(menu.findItem(R.id.nav1_mydoctor));
            menuItems.add(menu.findItem(R.id.nav1_support));
            menuItems.add(menu.findItem(R.id.nav1_intro));
            menuItems.add(menu.findItem(R.id.nav1_logout));
        }
        adapter_nav.addAll(menuItems);
    }


    private void setNavigationViewMenu(UserType menu) {

        ImageView menu_header_image = (ImageView) mNavigation.findViewById(R.id.img_profile33);
        TextView menu_header_name = (TextView) mNavigation.findViewById(R.id.name33);
        TextView menu_header_version = (TextView) mNavigation.findViewById(R.id.pezashyar_type33);
        setNavigationItem(menu);
        if (menu != UserType.Guest) {
            menu_header_image.setImageBitmap(G.UserInfo.getImgProfile());
            menu_header_name.setText(G.UserInfo.getFirstName().concat(" " + G.UserInfo.getLastName()));
        } else {
            menu_header_name.setText("کاربر میهمان");
            menu_header_image.setImageResource(R.drawable.doctor);
            menu_header_version.setText("");
        }

        switch (menu) {
            case Dr:
                menu_header_version.setText("نسخه پزشک");
                break;
            case secretary:
                menu_header_version.setText("نسخه منشی");
                break;
            case User:
                menu_header_version.setText("نسخه بیمار");
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == LOGIN_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                readSharedPrefrence();
                menuItems.clear();
                adapter_nav.removeAll();
                if (G.UserInfo.getRole() == UserType.Dr.ordinal() || G.UserInfo.getRole() == UserType.secretary.ordinal()) {

                    setDoctorLayout(true);

                } else if (G.UserInfo.getRole() == UserType.User.ordinal()) {

                    setUserLayout(true);

                }

            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
        if (requestCode == SIGNUP_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                menuItems.clear();
                adapter_nav.removeAll();
                startActivity(new Intent(ActivityOffices.this, ActivityAllDoctors.class));
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }

    private void logOut() {
        G.getSharedPreferences().edit().remove("user").apply();
        G.getSharedPreferences().edit().remove("pass").apply();
        G.getSharedPreferences().edit().remove("role").apply();
        setNavigationItem(UserType.Guest);
        G.UserInfo = new User();
        if (database.openConnection()) {
            database.deleteAllOffice();
            database.closeConnection();
        }
        onPause();
        adapter_office.removeAll(offices);
        adapter_doctors.removeAll(doctors);
        if (unreadMessages != null)
            unreadMessages.clear();
        offices = new ArrayList<Office>();
        doctors = new ArrayList<Office>();
        menuItems.clear();
        adapter_nav.removeAll();
        setWellcomeLayout();

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private void setNavigationDrawer() {

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.offices_drawer);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
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

    private void setWellcomeLayout() {
        badge.hide();
        addButton.setVisibility(View.GONE);
        mViewFlipper.setVisibility(View.GONE);
        if (mNavigation != null && menuItems.size() == 0) {
            setNavigationViewMenu(UserType.Guest);
        }
        unreadMessageLayout.setVisibility(View.GONE);
        welcomePage.setVisibility(View.VISIBLE);
    }

    private void setUserLayout(boolean readDataFromWeb) {
        unreadMessageLayout.setVisibility(View.VISIBLE);
        welcomePage.setVisibility(View.GONE);
        addButton.setVisibility(View.GONE);
        mViewFlipper.setVisibility(View.VISIBLE);
        mViewFlipper.setDisplayedChild(MY_DOCTOR);
        doctorsListView.setEnabled(false);
        officesListView.setEnabled(false);
        if (mNavigation != null && menuItems.size() == 0) {
            setNavigationViewMenu(UserType.values()[G.getSharedPreferences().getInt("role", 0)]);
        }
        if (readDataFromWeb) {
            task_getOffices = new AsyncGetOfficeForUser();
            task_getOffices.execute();

        } else {
            if (database.openConnection()) {
                doctors = database.getoffices();
                database.closeConnection();
            }
            if (doctors != null && doctors.size() > 0) {
                adapter_doctors.addAll(doctors);
            }
        }
        doctorsListView.setEnabled(true);
        officesListView.setEnabled(true);
        addButton.setVisibility(View.VISIBLE);
    }

    private void setDoctorLayout(boolean readDataFromWeb) {
        unreadMessageLayout.setVisibility(View.VISIBLE);
        welcomePage.setVisibility(View.GONE);
        addButton.setVisibility(View.GONE);
        mViewFlipper.setVisibility(View.VISIBLE);
        mViewFlipper.setDisplayedChild(MY_OFFICE);
        doctorsListView.setEnabled(false);
        officesListView.setEnabled(false);
        if (mNavigation != null && menuItems.size() == 0) {
            setNavigationViewMenu(UserType.values()[G.getSharedPreferences().getInt("role", 0)]);
        }
        if (readDataFromWeb) {
            task_getOfficeForDoctorOrSercretary = new AsyncGetOfficeForDoctorOrSercretary();
            task_getOfficeForDoctorOrSercretary.execute();

        } else {
            if (database.openConnection()) {
                offices = database.getMyOffice();
                doctors = database.getMyDoctorOffice();
                database.closeConnection();
            }
            if (offices != null && offices.size() > 0) {
                adapter_office.addAll(offices);
            }
            if (doctors != null && doctors.size() > 0) {
                adapter_doctors.addAll(doctors);
            }

        }
        doctorsListView.setEnabled(true);
        officesListView.setEnabled(true);
        addButton.setVisibility(View.VISIBLE);
    }

    private class AsyncGetOfficeForUser extends AsyncTask<String, Void, Void> {
        String msg = null;
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(ActivityOffices.this, "", "در حال دریافت اطلاعات شما ...");
            dialog.show();
            dialog.getWindow().setGravity(Gravity.END);
            dialog.setCancelable(true);
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                doctors = WebService.invokeGetOfficeForUserWS(G.UserInfo.getUserName(), G.UserInfo.getPassword());
            } catch (PException ex) {
                msg = ex.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (msg != null) {
                dialog.dismiss();
                new MessageBox(ActivityOffices.this, msg).show();
            } else {
                dialog.dismiss();
                if (doctors != null && doctors.size() > 0) {
                    if (database.openConnection()) {
                        for (Office of : doctors) {
                            of.setPhoto(BitmapFactory.decodeResource(getResources(), R.drawable.doctor));
                            database.insertoffice(of);
                            adapter_doctors.add(of);
                        }
                        database.closeConnection();
                    }
                    for (int i = 0; i < doctors.size(); i++) {
                        task_getDoctorPic = new AsyncGetDoctorPic();
                        task_getDoctorPic.execute(String.valueOf(i), String.valueOf(MY_DOCTOR));
                    }
                }
            }
        }
    }

    private class AsyncGetOfficeForDoctorOrSercretary extends AsyncTask<String, Void, Void> {
        String msg = null;
        ProgressDialog dialog;
        ArrayList<Office> allOffices = new ArrayList<Office>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(ActivityOffices.this, "", "در حال دریافت اطلاعات شما ...");
            dialog.show();
            dialog.getWindow().setGravity(Gravity.END);
            dialog.setCancelable(true);
            G.UserInfo.setUserName(G.getSharedPreferences().getString("user", ""));
            G.UserInfo.setPassword(G.getSharedPreferences().getString("pass", ""));
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                allOffices = WebService.invokeGetOfficeForDoctorOrSecretaryWS(G.UserInfo.getUserName(), G.UserInfo.getPassword());
            } catch (PException ex) {
                msg = ex.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (msg != null) {
                dialog.dismiss();
                new MessageBox(ActivityOffices.this, msg).show();
            } else {
                dialog.dismiss();
                if (allOffices != null && allOffices.size() > 0) {
                    if (database.openConnection()) {
                        for (Office of : allOffices) {
                            of.setPhoto(BitmapFactory.decodeResource(getResources(), R.drawable.doctor));
                            database.insertoffice(of);
                            offices.add(of);
                            adapter_office.add(of);
                        }
                    }
                    database.closeConnection();
                }
                for (int i = 0; i < offices.size(); i++) {
                    task_getDoctorPic = new AsyncGetDoctorPic();
                    task_getDoctorPic.execute(String.valueOf(i), String.valueOf(MY_OFFICE));
                }
            }
        }
    }

    private class AsyncGetDoctorPic extends AsyncTask<String, Void, Void> {
        String msg = null;
        Bitmap drpic = null;
        int position;
        ArrayList<Office> mOffices = new ArrayList<Office>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                position = Integer.valueOf(strings[0]);
                if (Integer.valueOf(strings[1]) == MY_DOCTOR) {
                    mOffices = doctors;
                } else {
                    mOffices = offices;
                }
                drpic = WebService.invokeGetDoctorPicWS(G.UserInfo.getUserName(), G.UserInfo.getPassword(), mOffices.get(position).getId());
            } catch (PException ex) {
                msg = ex.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (msg != null) {
                new MessageBox(ActivityOffices.this, msg).show();
            } else {
                if (drpic != null) {
                    if (database.openConnection()) {
                        Office office = mOffices.get(position);
                        office.setPhoto(drpic);
                        database.updateOffice(office.getId(), office);
                        if (office.isMyOffice() == 1)
                            adapter_office.update(position, office);
                        else
                            adapter_doctors.update(position, office);
                        database.closeConnection();
                    }
                }
            }
        }
    }

    private class AsyncCallGetUnreadMessagesWs extends AsyncTask<String, Void, Void> {

        String msg = null;
        ArrayList<MessageInfo> messageInfos = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                messageInfos = WebService.invokeGetAllUnreadMessagesWS(G.UserInfo.getUserName(), G.UserInfo.getPassword());
            } catch (PException ex) {
                msg = ex.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (msg != null) {
                new MessageBox(ActivityOffices.this, msg).show();
            } else {
                if (messageInfos != null && messageInfos.size() != 0) {
                    unreadMessages = new ArrayList<MessageInfo>();
                    unreadMessages.addAll(messageInfos);
                    badge.setText(String.valueOf(messageInfos.size()));
                    badge.show();
                } else {
                    if (unreadMessages != null)
                        unreadMessages.clear();
                    badge.hide();
                }
            }
        }

    }
}
