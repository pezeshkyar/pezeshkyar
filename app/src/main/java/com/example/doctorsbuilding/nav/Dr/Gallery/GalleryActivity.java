package com.example.doctorsbuilding.nav.Dr.Gallery;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;

import com.example.doctorsbuilding.nav.R;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class GalleryActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_gallery);
        int[] mResources = new int[8];
        for(int i=0;i<8;i++){
            mResources[i] = R.mipmap.doctor;
            mResources[i] = R.mipmap.doctor;
            mResources[i] = R.mipmap.doctor;
            mResources[i] = R.mipmap.doctor;
            mResources[i] = R.mipmap.doctor;
            mResources[i] = R.mipmap.doctor;
            mResources[i] = R.mipmap.doctor;
            mResources[i] = R.mipmap.doctor;
        }
        CustomPagerAdapter customPagerAdapter = new CustomPagerAdapter(this, mResources);

        ViewPager viewPager = (ViewPager) findViewById(R.id.gallery_viewPager);
        viewPager.setAdapter(customPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.gallery_tabLayout);
        tabLayout.setupWithViewPager(viewPager);


    }
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
