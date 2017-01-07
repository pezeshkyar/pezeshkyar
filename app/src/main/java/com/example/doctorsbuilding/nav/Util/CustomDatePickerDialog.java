package com.example.doctorsbuilding.nav.Util;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.example.doctorsbuilding.nav.Dr.persindatepicker.PersianDatePicker;
import com.example.doctorsbuilding.nav.Dr.persindatepicker.util.PersianCalendar;
import com.example.doctorsbuilding.nav.R;

/**
 * Created by hossein on 5/29/2016.
 */
public class CustomDatePickerDialog extends Dialog implements View.OnClickListener {

    private Context context;
    private Button btnAccept;
    private PersianDatePicker datePicker;
    public int BUTTON_TYPE = Dialog.BUTTON_NEGATIVE;

    public CustomDatePickerDialog(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_date_picker);
        btnAccept = (Button) findViewById(R.id.date_acceptBtn);
        datePicker = (PersianDatePicker) findViewById(R.id.datePicker);
        btnAccept.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.date_acceptBtn:
                BUTTON_TYPE = Dialog.BUTTON_POSITIVE;
                break;
            default:
                break;
        }
        dismiss();
    }

    public String getDate() {
        PersianCalendar date = datePicker.getDisplayPersianDate();
        return date.getPersianLongDate();
    }
    public String getShortDate(){
        PersianCalendar date = datePicker.getDisplayPersianDate();
        return date.getPersianShortDate();
    }

}

