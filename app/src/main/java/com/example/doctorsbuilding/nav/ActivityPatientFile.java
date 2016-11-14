package com.example.doctorsbuilding.nav;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.doctorsbuilding.nav.Dr.Nobat.DrNobatFragment;
import com.example.doctorsbuilding.nav.Dr.Notification.NotificationFragment;
import com.example.doctorsbuilding.nav.Dr.Profile.ExpandListAdapter;
import com.example.doctorsbuilding.nav.Dr.Profile.PersonalInfoFragment;
import com.example.doctorsbuilding.nav.Util.MessageBox;
import com.example.doctorsbuilding.nav.Web.WebService;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by hossein on 9/4/2016.
 */
public class ActivityPatientFile extends AppCompatActivity {

    SectionsPagerAdapter mSectionsPagerAdapter;
    Button backBtn;
    TextView pageTitle;
    String patientUsername;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_patient_file);

        patientUsername = getIntent().getExtras().getString("patientUserName");
        pageTitle = (TextView)findViewById(R.id.toolbar_title);
        pageTitle.setText("پرونده بیمار");
        backBtn = (Button)findViewById(R.id.toolbar_backBtn);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        ViewPager mViewPager = (ViewPager)findViewById(R.id.patient_file_pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        TabLayout mTableLayout = (TabLayout) findViewById(R.id.patient_file_tab);
        mTableLayout.setupWithViewPager(mViewPager);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment frag = null;
            switch (position) {
                case 0:
                    frag = new FragmentPatientFileMoney().newInstance(position + 1, patientUsername);
                    break;
                case 1:
                    frag = new FragmentPatientMedicalHistory().newInstance(position + 1, patientUsername);
                    break;
            }
            return frag;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "پرونده مالی";
                case 1:
                    return "سوابق پزشکی";
            }
            return null;
        }
    }

}
