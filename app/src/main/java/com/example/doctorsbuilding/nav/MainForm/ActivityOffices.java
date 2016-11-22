package com.example.doctorsbuilding.nav.MainForm;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
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
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.doctorsbuilding.nav.ContactUs;
import com.example.doctorsbuilding.nav.CustomTypefaceSpan;
import com.example.doctorsbuilding.nav.Databases.DatabaseAdapter;
import com.example.doctorsbuilding.nav.Dr.Clinic.Office;
import com.example.doctorsbuilding.nav.G;
import com.example.doctorsbuilding.nav.R;
import com.example.doctorsbuilding.nav.SignInActivity;
import com.example.doctorsbuilding.nav.User.User;
import com.example.doctorsbuilding.nav.User.UserNewsActivity;
import com.example.doctorsbuilding.nav.User.UserProfileActivity;

import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by hossein on 11/16/2016.
 */
public class ActivityOffices extends AppCompatActivity {

    FloatingActionButton fabButton;
    DrawerLayout mDrawer;
    NavigationView mNavigation = null;
    ImageView btn_menu;
    ListView mListView;
    TextView txt_nothing;
    TextView txt_logo;
    ArrayList<Office> offices = new ArrayList<Office>();
    CustomOfficesListAdapter adapter_office = null;
    DatabaseAdapter database = null;
    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_public_main);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
//        }

        initViews();
        eventListener();

        adapter_office = new CustomOfficesListAdapter(ActivityOffices.this, offices);
        mListView.setAdapter(adapter_office);

    }


    @Override
    protected void onResume() {
        super.onResume();
        if (G.UserInfo == null)
            G.UserInfo = new User();
        G.UserInfo.setUserName(G.getSharedPreferences().getString("user", ""));
        G.UserInfo.setPassword(G.getSharedPreferences().getString("pass", ""));
        if (G.UserInfo != null && G.UserInfo.getUserName().length() != 0 && G.UserInfo.getPassword().length() != 0) {
            if (mNavigation != null) {
                mNavigation.getMenu().getItem(0).setVisible(false);
                mNavigation.getMenu().getItem(1).setVisible(false);
                mNavigation.getMenu().getItem(2).setVisible(true);
                mNavigation.getMenu().getItem(3).setVisible(true);
                mNavigation.getMenu().getItem(4).setVisible(true);
            }
            database = new DatabaseAdapter(ActivityOffices.this);
            if (database.openConnection()) {
                offices = database.getoffices();
                database.closeConnection();
            }
            if (offices != null && offices.size() > 0) {
                txt_nothing.setVisibility(View.GONE);
                adapter_office.addAll(offices);
            }
        } else {
            txt_nothing.setVisibility(View.VISIBLE);
            if (mNavigation != null) {
                mNavigation.getMenu().getItem(0).setVisible(true);
                mNavigation.getMenu().getItem(1).setVisible(true);
                mNavigation.getMenu().getItem(2).setVisible(true);
                mNavigation.getMenu().getItem(3).setVisible(true);
                mNavigation.getMenu().getItem(4).setVisible(false);
            }
        }
    }

    private void initViews() {
        txt_logo = (TextView) findViewById(R.id.offices_title);
        txt_logo.setText("پزشک یار");
        txt_logo.setTypeface(G.getDastnevisFont());
        txt_logo.setTextColor(Color.WHITE);
        txt_nothing = (TextView) findViewById(R.id.offices_help);
        mListView = (ListView) findViewById(R.id.offices_listview);
        btn_menu = (ImageView) findViewById(R.id.offices_menu_btn);
        mDrawer = (DrawerLayout) findViewById(R.id.offices_drawer);
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;

        if (currentapiVersion >= Build.VERSION_CODES.JELLY_BEAN) {
            ViewCompat.setLayoutDirection(mDrawer, ViewCompat.LAYOUT_DIRECTION_RTL);
        }
        fabButton = (FloatingActionButton) findViewById(R.id.offices_fab);
        mNavigation = (NavigationView) findViewById(R.id.offices_nav);
        setNavigationDrawer();

    }

    private void eventListener() {
        fabButton.setOnClickListener(new View.OnClickListener() {
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
                                    offices = database.getoffices();
                                    database.closeConnection();
                                }
                                if (offices != null && offices.size() > 0) {
                                    txt_nothing.setVisibility(View.GONE);
                                    adapter_office.addAll(offices);
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

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
                        startActivity(new Intent(ActivityOffices.this, SignInActivity.class));
                        break;
                    case R.id.nav_signUp_user:
                        startActivity(new Intent(ActivityOffices.this, UserProfileActivity.class));
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

//        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
//                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
//                    fabButton.show();
//                }
//            }
//
//            @Override
//            public void onScroll(AbsListView absListView, int io, int dx, int dy) {
//                if (dy > 0 || dy < 0 && fabButton.isShown()) {
//                    fabButton.hide();
//                }
//            }
//        });

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
        txt_nothing.setVisibility(View.VISIBLE);


//
//        startActivity(new Intent(MainActivity.this, SignInActivity.class));
//        finish();

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

}
