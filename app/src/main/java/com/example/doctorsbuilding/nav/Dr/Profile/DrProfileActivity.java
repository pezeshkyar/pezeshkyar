package com.example.doctorsbuilding.nav.Dr.Profile;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.doctorsbuilding.nav.ActivityImageShow;
import com.example.doctorsbuilding.nav.Dr.Gallery.GalleryActivity;
import com.example.doctorsbuilding.nav.Dr.Nobat.DrNobatFragment;
import com.example.doctorsbuilding.nav.Dr.Notification.NotificationFragment;
import com.example.doctorsbuilding.nav.G;
import com.example.doctorsbuilding.nav.LazyLoad.Gallery3;
import com.example.doctorsbuilding.nav.R;
import com.example.doctorsbuilding.nav.Util.RoundedImageView;
import com.example.doctorsbuilding.nav.Web.WebService;
import com.example.doctorsbuilding.nav.gallery2;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class DrProfileActivity extends AppCompatActivity{


    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private ImageButton btnGallery;
    private ImageButton profileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_dr_profile);
        initViews();
    }
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
    private void initViews() {

        TextView drName = (TextView)findViewById(R.id.tv_doctorName);
        TextView drExpert = (TextView) findViewById(R.id.tv_doctorInfo);
        drName.setTypeface(G.getBoldFont());
        drExpert.setTypeface(G.getBoldFont());
        drName.setText(G.officeInfo.getFirstname()+" "+G.officeInfo.getLastname());
        drExpert.setText(G.officeInfo.getSubExpertName());
        setImageProfile();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);


    }

    private void setImageProfile() {
        profileImage = (ImageButton) findViewById(R.id.drProfile_btnDrPhoto);
        if(G.doctorImageProfile != null) {
            Bitmap imgRound = RoundedImageView.getCroppedBitmap(G.doctorImageProfile, 160);
            profileImage.setImageBitmap(imgRound);
        }
        btnGallery = (ImageButton) findViewById(R.id.drProfile_btnGallery);
        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DrProfileActivity.this, gallery2.class));
            }
        });
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(DrProfileActivity.this, ActivityImageShow.class));
            }
        });
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
                    frag = new DrNobatFragment().newInstance(position + 1);
                    break;
                case 1:
                    frag = new PersonalInfoFragment().newInstance(position + 1);
                    break;
                case 2:
                    frag = new NotificationFragment().newInstance(position + 1);
                    break;
            }
            return frag;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getResources().getString(R.string.pageTitle_nobat);
                case 1:
                    return getResources().getString(R.string.pageTitle_info);
                case 2:
                    return getResources().getString(R.string.pageTitle_comments);
            }
            return null;
        }
    }

}
