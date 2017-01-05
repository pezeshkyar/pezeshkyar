package com.example.doctorsbuilding.nav;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * Created by hossein on 10/4/2016.
 */
public class ActivityImageShow extends AppCompatActivity {
    ImageView imageView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        G.setStatusBarColor(ActivityImageShow.this);
        setContentView(R.layout.activity_showimage_profile);
        imageView = (ImageView)findViewById(R.id.zoomImageProfile);

        imageView.setImageBitmap(G.doctorImageProfile);
    }
}
