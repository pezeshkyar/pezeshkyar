package com.example.doctorsbuilding.nav;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.example.doctorsbuilding.nav.Util.NonScrollListView;

/**
 * Created by hadi on 09/18/2016.
 */
public class ContactUs extends AppCompatActivity {
    Button backBtn;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_contact_us);
        initView();
        eventListener();
    }
    private void initView() {
        backBtn = (Button) findViewById(R.id.contact_us_backBtn);
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
