package com.example.doctorsbuilding.nav.Dr.Sick;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.doctorsbuilding.nav.Util.CustomDatePickerDialog;
import com.example.doctorsbuilding.nav.Util.MessageBox;
import com.example.doctorsbuilding.nav.R;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by hossein on 6/6/2016.
 */
public class PatientsActivity extends AppCompatActivity implements View.OnClickListener {

    EditText name;
    EditText family;
    TextView startDate;
    TextView endDate;
    Button btnSearch;
    ListView listView;
    ListAdapter adapter;
    Button backBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_patients);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        initViews();
        setListView();
        startDate.setOnClickListener(this);
        endDate.setOnClickListener(this);
        btnSearch.setOnClickListener(this);
        backBtn.setOnClickListener(this);

    }
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
    private void setListView() {
        adapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, getResources().getStringArray(R.array.list_groups));
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                startActivity(new Intent(PatientsActivity.this, SickActivity.class));
            }
        });
    }

    private void initViews() {
        backBtn = (Button) findViewById(R.id.patients_backBtn);
        name = (EditText) findViewById(R.id.patients_name);
        family = (EditText) findViewById(R.id.patients_family);
        startDate = (TextView) findViewById(R.id.patients_start_date);
        endDate = (TextView) findViewById(R.id.patients_end_date);
        btnSearch = (Button) findViewById(R.id.patients_btn_search);
        listView = (ListView)findViewById(R.id.patients_listView);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.patients_start_date:
                setStartDate();
                break;
            case R.id.patients_end_date:
                setEndDate();
                break;
            case R.id.patients_btn_search:
                checkFields();
                break;
            case  R.id.patients_backBtn:
                PatientsActivity.this.onBackPressed();
            default:
                break;
        }
    }

    private void checkFields() {
        if(!startDate.getText().toString().trim().isEmpty() && !endDate.getText().toString().trim().isEmpty() ) {
            String date_start = startDate.getText().toString().trim();
            String date_end = endDate.getText().toString().trim();
            if (date_start.compareToIgnoreCase(date_end) >= 1) {
                new MessageBox(this, "تاریخ پایان باید بزرگتر یا مساوی تاریخ شروع باشد .").show();
            }
        }
    }

    private void setStartDate() {
        final CustomDatePickerDialog datePicker = new CustomDatePickerDialog(this);
        datePicker.show();
        datePicker.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                if(datePicker.BUTTON_TYPE == Dialog.BUTTON_POSITIVE){
                    startDate.setText(datePicker.getShortDate());
                }
            }
        });
    }

    private void setEndDate() {
        final CustomDatePickerDialog datePicker = new CustomDatePickerDialog(this);
        datePicker.show();
        datePicker.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                if(datePicker.BUTTON_TYPE == Dialog.BUTTON_POSITIVE){
                    endDate.setText(datePicker.getShortDate());
                }
            }
        });
    }
}
