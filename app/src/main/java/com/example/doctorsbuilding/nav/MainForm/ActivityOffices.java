package com.example.doctorsbuilding.nav.MainForm;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
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
import android.text.Spannable;
import android.text.SpannableString;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.example.doctorsbuilding.nav.ContactUs;
import com.example.doctorsbuilding.nav.CustomTypefaceSpan;
import com.example.doctorsbuilding.nav.Databases.DatabaseAdapter;
import com.example.doctorsbuilding.nav.Dr.Clinic.Office;
import com.example.doctorsbuilding.nav.G;
import com.example.doctorsbuilding.nav.PException;
import com.example.doctorsbuilding.nav.R;
import com.example.doctorsbuilding.nav.SignInActivity;
import com.example.doctorsbuilding.nav.User.User;
import com.example.doctorsbuilding.nav.User.UserNewsActivity;
import com.example.doctorsbuilding.nav.UserType;
import com.example.doctorsbuilding.nav.Util.MessageBox;
import com.example.doctorsbuilding.nav.Web.WebService;

import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by hossein on 11/16/2016.
 */
public class ActivityOffices extends AppCompatActivity {

    FloatingActionButton addButton;
    Button btn_myDoctor;
    Button btn_myOffice;
    Button btn_signIn;
    Button btn_signUp;
    LinearLayout bottomLayout;
    FrameLayout welcomePage;
    DrawerLayout mDrawer;
    NavigationView mNavigation = null;
    ImageView btn_menu;
    ListView officesListView;
    ListView doctorsListView;
    ArrayList<Office> offices = new ArrayList<Office>();
    ArrayList<Office> doctors = new ArrayList<Office>();
    ViewFlipper mViewFlipper;
    CustomOfficesListAdapter adapter_office = null;
    CustomDoctorsListAdapter adapter_doctors = null;
    DatabaseAdapter database = null;
    boolean doubleBackToExitPressedOnce = false;
    AsyncGetOfficeForUser task_getOffices = null;
    AsyncGetDoctorPic task_getDoctorPic = null;
    AsyncGetOfficeForDoctorOrSercretary task_getOfficeForDoctorOrSercretary;
    private static final int MY_OFFICE = 0;
    private static final int MY_DOCTOR = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_public_main);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        initViews();
        eventListener();

        adapter_office = new CustomOfficesListAdapter(ActivityOffices.this, offices);
        officesListView.setAdapter(adapter_office);
        adapter_doctors = new CustomDoctorsListAdapter(ActivityOffices.this, doctors);
        doctorsListView.setAdapter(adapter_doctors);

        initActivity();

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
        welcomePage = (FrameLayout) findViewById(R.id.welcome_page);
        btn_signIn = (Button) findViewById(R.id.welcome_signIn);
        btn_signUp = (Button) findViewById(R.id.welcome_signUp);
        mViewFlipper = (ViewFlipper) findViewById(R.id.offices_viewFlipper);
        bottomLayout = (LinearLayout) findViewById(R.id.offices_bottom_layout);
        btn_myDoctor = (Button) findViewById(R.id.offices_my_doctor);
        btn_myOffice = (Button) findViewById(R.id.offices_my_office);
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
                    final DialogAddOffice dialog_addOffice = new DialogAddOffice(ActivityOffices.this, R.style.AlertDialogCustom);
                    dialog_addOffice.show();
                    dialog_addOffice.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {

                            Office office = dialog_addOffice.getOffice();
                            if (office != null) {
                                if (database.openConnection()) {
                                    doctors = database.getoffices();
                                    database.closeConnection();
                                }
                                if (doctors != null && doctors.size() > 0) {
                                    adapter_doctors.addAll(doctors);
                                }
                            }

                            try {
                                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                            } catch (Exception ex) {
                            }
                        }
                    });
                } else {
                    startActivity(new Intent(ActivityOffices.this, SignInActivity.class));
                }
            }
        });
        mNavigation.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {

                return false;
            }
        });
        btn_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawer.openDrawer(Gravity.RIGHT);
            }
        });

        officesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
//                Toast.makeText(ActivityOffices.this, "dsfsd", Toast.LENGTH_SHORT).show();
            }
        });

        mNavigation.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_signIn:
                        btn_signIn.performClick();
                        break;
                    case R.id.nav_signUp_user:
                        btn_signUp.performClick();
                        break;
                    case R.id.nav_signUp_news:
                        startActivity(new Intent(ActivityOffices.this, UserNewsActivity.class));
                        break;
                    case R.id.nav_signUp_call:
                        startActivity(new Intent(ActivityOffices.this, ContactUs.class));
                        break;
                    case R.id.nav_signUp_logout:
                        logOut();
                        break;
                }
                return false;
            }
        });
        btn_myDoctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mViewFlipper.getDisplayedChild() != MY_DOCTOR) {
                    showPrevious();
                    mViewFlipper.setDisplayedChild(MY_DOCTOR);
                }
            }
        });
        btn_myOffice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mViewFlipper.getDisplayedChild() != MY_OFFICE) {
                    showNext();
                    mViewFlipper.setDisplayedChild(MY_OFFICE);
                }

            }
        });
        btn_signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(ActivityOffices.this, SignInActivity.class), 1);
            }
        });
        btn_signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(ActivityOffices.this, SignInActivity.class), 2);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                UserType user = UserType.values()[G.getSharedPreferences().getInt("role", 0)];
                if (user == UserType.Dr || user == UserType.secretary) {

                    setDoctorLayout(true);

                } else if (user == UserType.User) {

                    setUserLayout(true);

                }

            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
        if (requestCode == 2) {
            if (resultCode == Activity.RESULT_OK) {
                task_getOffices = new AsyncGetOfficeForUser();
                task_getOffices.execute();
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
        mNavigation.getMenu().getItem(4).setVisible(false);
        mNavigation.getMenu().getItem(0).setVisible(true);
        mNavigation.getMenu().getItem(1).setVisible(true);
        mNavigation.getMenu().getItem(2).setVisible(true);
        mNavigation.getMenu().getItem(3).setVisible(true);
        G.UserInfo = null;
        if (database.openConnection()) {
            database.deleteAllOffice();
            database.closeConnection();
        }
        adapter_office.removeAll(offices);
        finish();
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
        setNavigationViewMenuFontStyle();
        setNavigationViewMenu();

    }

    private void setNavigationViewMenuFontStyle() {
        Menu m = mNavigation.getMenu();
        for (int i = 0; i < m.size(); i++) {
            MenuItem mi = m.getItem(i);

            SubMenu subMenu = mi.getSubMenu();
            if (subMenu != null && subMenu.size() > 0) {
                for (int j = 0; j < subMenu.size(); j++) {
                    MenuItem subMenuItem = subMenu.getItem(j);
                    applyFontToMenuItem(subMenuItem);
                }
            }

            applyFontToMenuItem(mi);
        }
    }

    private void applyFontToMenuItem(MenuItem mi) {
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/IRANSans.ttf");
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("", font), 0, mNewTitle.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
    }


    private void setNavigationViewMenu() {

        ImageView menu_header_image = (ImageView) mNavigation.findViewById(R.id.header1_image);
        TextView menu_header_name = (TextView) mNavigation.findViewById(R.id.header1_text);

        mNavigation.getMenu().setGroupVisible(R.id.navigation_view_user, false);
        mNavigation.getMenu().setGroupVisible(R.id.navigation_view_dr, false);
        mNavigation.getMenu().setGroupVisible(R.id.navigation_view_secretary, false);
        mNavigation.getMenu().setGroupVisible(R.id.navigation_view_signUp, true);

        menu_header_name.setText("مطب هوشمند پزشکیار");
        menu_header_image.setImageResource(R.mipmap.ic_launcher);
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

    private void showPrevious() {
        mViewFlipper.setInAnimation(getBaseContext(), R.anim.slide_in_from_left);
        mViewFlipper.setOutAnimation(getBaseContext(), R.anim.slide_out_to_right);
        mViewFlipper.showNext();
    }

    private void showNext() {
        mViewFlipper.setInAnimation(getBaseContext(), R.anim.slide_in_from_right);
        mViewFlipper.setOutAnimation(getBaseContext(), R.anim.slide_out_to_left);
        mViewFlipper.showPrevious();
    }

    private void setWellcomeLayout() {
        addButton.setVisibility(View.GONE);
        bottomLayout.setVisibility(View.GONE);
        mViewFlipper.setVisibility(View.GONE);
        if (mNavigation != null) {
            mNavigation.getMenu().getItem(0).setVisible(true);
            mNavigation.getMenu().getItem(1).setVisible(true);
            mNavigation.getMenu().getItem(2).setVisible(true);
            mNavigation.getMenu().getItem(3).setVisible(true);
            mNavigation.getMenu().getItem(4).setVisible(false);
        }
        welcomePage.setVisibility(View.VISIBLE);
    }

    private void setUserLayout(boolean readDataFromWeb) {
        welcomePage.setVisibility(View.GONE);
        addButton.setVisibility(View.GONE);
        bottomLayout.setVisibility(View.GONE);
        mViewFlipper.setVisibility(View.VISIBLE);
        mViewFlipper.setDisplayedChild(MY_DOCTOR);
        if (mNavigation != null) {
            mNavigation.getMenu().getItem(0).setVisible(false);
            mNavigation.getMenu().getItem(1).setVisible(false);
            mNavigation.getMenu().getItem(2).setVisible(true);
            mNavigation.getMenu().getItem(3).setVisible(true);
            mNavigation.getMenu().getItem(4).setVisible(true);
        }
        if (readDataFromWeb) {

            task_getOffices = new AsyncGetOfficeForUser();
            task_getOffices.execute();

        } else {
            database = new DatabaseAdapter(ActivityOffices.this);
            if (database.openConnection()) {
                doctors = database.getoffices();
                database.closeConnection();
            }
            if (doctors != null && doctors.size() > 0) {
                adapter_doctors.addAll(doctors);
            }
        }
        addButton.setVisibility(View.VISIBLE);
    }

    private void setDoctorLayout(boolean readDataFromWeb) {
        welcomePage.setVisibility(View.GONE);
        addButton.setVisibility(View.GONE);
        bottomLayout.setVisibility(View.VISIBLE);
        mViewFlipper.setVisibility(View.VISIBLE);
        mViewFlipper.setDisplayedChild(MY_OFFICE);
        if (mNavigation != null) {
            mNavigation.getMenu().getItem(0).setVisible(false);
            mNavigation.getMenu().getItem(1).setVisible(false);
            mNavigation.getMenu().getItem(2).setVisible(true);
            mNavigation.getMenu().getItem(3).setVisible(true);
            mNavigation.getMenu().getItem(4).setVisible(true);
        }
        if (readDataFromWeb) {
            task_getOfficeForDoctorOrSercretary = new AsyncGetOfficeForDoctorOrSercretary();
            task_getOfficeForDoctorOrSercretary.execute();

        } else {
            database = new DatabaseAdapter(ActivityOffices.this);
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
            addButton.hide();
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
                addButton.show();
            } else {
                dialog.dismiss();
                if (doctors != null && doctors.size() > 0) {
                    database = new DatabaseAdapter(ActivityOffices.this);
                    if (database.openConnection()) {
                        for (Office of : doctors) {
                            of.setPhoto(BitmapFactory.decodeResource(getResources(), R.drawable.doctor));
                            database.insertoffice(of);
                        }
                        database.closeConnection();
                    }
                    adapter_doctors.addAll(doctors);
                    for (int i = 0; i < doctors.size(); i++) {
                        task_getDoctorPic = new AsyncGetDoctorPic();
                        task_getDoctorPic.execute(String.valueOf(i), String.valueOf(MY_DOCTOR));
                    }
                }
                addButton.show();
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
                    database = new DatabaseAdapter(ActivityOffices.this);
                    if (database.openConnection()) {
                        for (Office of : allOffices) {
                            of.setPhoto(BitmapFactory.decodeResource(getResources(), R.drawable.doctor));
                            database.insertoffice(of);
                            if (of.isMyOffice() == 0) {
                                doctors.add(of);
                            } else {
                                offices.add(of);
                            }
                        }
                        database.closeConnection();
                    }
                    adapter_office.addAll(offices);
                    adapter_doctors.addAll(doctors);
//                    for (int i = 0; i < offices.size(); i++) {
//                        task_getDoctorPic = new AsyncGetDoctorPic();
//                        task_getDoctorPic.execute(String.valueOf(i), String.valueOf(0));
//                    }
//                    for (int i = 0; i < doctors.size(); i++) {
//                        task_getDoctorPic = new AsyncGetDoctorPic();
//                        task_getDoctorPic.execute(String.valueOf(i), String.valueOf(1));
//                    }
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
                    database = new DatabaseAdapter(ActivityOffices.this);
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

}
