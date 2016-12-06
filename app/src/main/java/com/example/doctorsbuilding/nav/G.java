package com.example.doctorsbuilding.nav;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.CalendarContract;
import android.support.v4.content.ContextCompat;
import android.view.WindowManager;
import android.widget.ArrayAdapter;

import com.example.doctorsbuilding.nav.Dr.Clinic.Office;
import com.example.doctorsbuilding.nav.User.User;
import com.example.doctorsbuilding.nav.support.Ticket;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by hossein on 7/18/2016.
 */
public class G extends Application {
    public static int officeId = -1;
    public static User UserInfo;
    public static Office officeInfo = null;
    public static Bitmap doctorImageProfile;

    private static G instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/IRANSansMobile(FaNum).ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
    }

    public static boolean isOnline() {
        if (instance == null) return false;
        ConnectivityManager cm =
                (ConnectivityManager) instance.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public static SharedPreferences getSharedPreferences() {
        return instance.getSharedPreferences("pezeshkyarDemo", 0);
    }

    public static Typeface getBoldFont() {
        return Typeface.createFromAsset(instance.getAssets(), "fonts/IRANSansMobile_Bold.ttf");
    }
    public static Typeface getNormalFont() {
        return Typeface.createFromAsset(instance.getAssets(), "fonts/IRANSansMobile(FaNum).ttf");
    }

    public static Typeface getDastnevisFont() {
        return Typeface.createFromAsset(instance.getAssets(), "fonts/persianDanstevis.ttf");
    }
    //Added By Ardeshir
    public static ArrayAdapter<Ticket> mAdapter;

    public static void setStatusBarColor(Activity activity){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            activity.getWindow().setStatusBarColor(ContextCompat.getColor(activity, R.color.statusBarColor));
        }
    }
}
