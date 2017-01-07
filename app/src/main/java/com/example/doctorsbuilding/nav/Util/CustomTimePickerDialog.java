package com.example.doctorsbuilding.nav.Util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TimePicker;

import com.example.doctorsbuilding.nav.Dr.persindatepicker.util.PersianCalendar;
import com.example.doctorsbuilding.nav.R;

import java.util.TimeZone;

public class CustomTimePickerDialog extends Dialog implements
        View.OnClickListener {

    private Context context;
    private Button btnAccept;
    private TimePicker timePicker;
    public int BUTTON_TYPE = Dialog.BUTTON_NEGATIVE;

    public CustomTimePickerDialog(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_time_picker);
        btnAccept = (Button) findViewById(R.id.acceptBtn);
        timePicker = (TimePicker) findViewById(R.id.timePicker);
        btnAccept.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.acceptBtn:
                BUTTON_TYPE = Dialog.BUTTON_POSITIVE;
                break;
            default:
                break;
        }
        dismiss();
    }

    public String getTime(){
        int hour, minute;
        String time;
        PersianCalendar pc = new PersianCalendar();
        pc.setTimeZone(TimeZone.getDefault());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            hour = timePicker.getHour();
            minute = timePicker.getMinute();
        } else {
            hour = timePicker.getCurrentHour();
            minute = timePicker.getCurrentMinute();
        }
        time = Integer.toString(hour) + ":" + Integer.toString(minute);
        return time;
    }

}