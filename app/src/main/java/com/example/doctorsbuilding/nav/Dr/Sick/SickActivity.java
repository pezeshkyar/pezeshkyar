package com.example.doctorsbuilding.nav.Dr.Sick;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.doctorsbuilding.nav.R;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by hossein on 6/8/2016.
 */
public class SickActivity extends AppCompatActivity {

    TextView name;
    TextView family;
    TextView address;
    TextView phone;
    ListView listView;
    ListAdapter adapter;
    ImageButton backBtn;
    TextView pageTitle;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.actvity_sick);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        initViews();
        setViews();
    }
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
    private void initViews() {
        pageTitle = (TextView)findViewById(R.id.toolbar_title);
        pageTitle.setText("بیماران");
        backBtn = (ImageButton) findViewById(R.id.toolbar_backBtn);
        name = (TextView)findViewById(R.id.sick_name);
        family = (TextView)findViewById(R.id.sick_family);
        address = (TextView)findViewById(R.id.sick_address);
        phone = (TextView)findViewById(R.id.sick_phone);
        listView = (ListView)findViewById(R.id.sick_listView);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SickActivity.this.onBackPressed();
            }
        });
    }
    private void setViews() {
        name.setText("حسین");
        family.setText("سالخورده گرمستانی");
        address.setText("ساری - بلوار کشاورز - کوی بهار ازادی - کوچه چهارم شهید مطهری");
        phone.setText("09384782571");
        adapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item
                , getResources().getStringArray(R.array.pay_types));
        listView.setAdapter(adapter);
    }


}
