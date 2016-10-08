package com.example.doctorsbuilding.nav;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.doctorsbuilding.nav.Util.NonScrollListView;
import com.example.doctorsbuilding.nav.Util.RoundedImageView;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by hadi on 09/18/2016.
 */
public class ContactUs extends AppCompatActivity {
    Button backBtn;
    ImageView imageView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_contact_us);
        initView();
        eventListener();
    }
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
    private void initView() {
        backBtn = (Button) findViewById(R.id.contact_us_backBtn);
        imageView = (ImageView) findViewById(R.id.company_name_icon);
        imageView.setImageBitmap(RoundedImageView.getCroppedBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.doctor), 200));
    }

    private void eventListener() {
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContactUs.this.onBackPressed();
            }
        });
    }
}
