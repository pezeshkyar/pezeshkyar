package com.example.doctorsbuilding.nav.Dr.Nobat;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.doctorsbuilding.nav.Dr.persindatepicker.util.PersianCalendar;
import com.example.doctorsbuilding.nav.G;
import com.example.doctorsbuilding.nav.PException;
import com.example.doctorsbuilding.nav.R;
import com.example.doctorsbuilding.nav.Turn;
import com.example.doctorsbuilding.nav.Util.CustomDatePickerDialog;
import com.example.doctorsbuilding.nav.Util.CustomTimePickerDialog;
import com.example.doctorsbuilding.nav.Util.FormatHelper;
import com.example.doctorsbuilding.nav.Util.MessageBox;
import com.example.doctorsbuilding.nav.Util.NonScrollListView;
import com.example.doctorsbuilding.nav.Web.WebService;

import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by hossein on 5/23/2016.
 */
public class DrNobatActivity extends AppCompatActivity {

    TextView startDate;
    TextView endDate;
    TextView startTime;
    TextView endTime;
    EditText maxCapacity;
    Button btnInsert;
    Button btnShowTurn;
    CheckBox chboxShanbe;
    CheckBox chbox1Shanbe;
    CheckBox chbox2Shanbe;
    CheckBox chbox3Shanbe;
    CheckBox chbox4Shanbe;
    CheckBox chbox5Shanbe;
    NonScrollListView listView;
    int hour, min, duration, capacity;
    String shortStartDate, shortEndDate;
    String dayOfWeek;
    ImageButton backBtn;
    TextView pageTitle;

    TextView txtWait;

    private ArrayList<Turn> turns;
    private CustomListAdapterNobat adpter;

    asyncCallGetAllTurn task_getAllTurn = null;
    callAsyncAddTurnWS task_addTurn = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        G.setStatusBarColor(DrNobatActivity.this);
        setContentView(R.layout.activity_dr_nobat);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        initViews();
        eventListener();

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(task_addTurn != null)
            task_addTurn.cancel(true);
        if(task_getAllTurn != null)
            task_getAllTurn.cancel(true);
    }

    private void initViews() {
        pageTitle = (TextView)findViewById(R.id.toolbar_title);
        pageTitle.setText("مدیریت نوبت دهی");
        backBtn = (ImageButton) findViewById(R.id.toolbar_backBtn);
        txtWait = (TextView) findViewById(R.id.nobat_wait);
        startDate = (TextView) findViewById(R.id.nobat_start_date);
        endDate = (TextView) findViewById(R.id.nobat_end_date);
        startTime = (TextView) findViewById(R.id.nobat_start_time);
        endTime = (TextView) findViewById(R.id.nobat_end_time);
        maxCapacity = (EditText) findViewById(R.id.nobat_capacity);
        maxCapacity.setRawInputType(Configuration.KEYBOARD_QWERTY);
        btnInsert = (Button) findViewById(R.id.dr_btnNobatInsert);
        btnShowTurn = (Button) findViewById(R.id.dr_btnNobatShow);
        listView = (NonScrollListView) findViewById(R.id.nobat_listView);
        chboxShanbe = (CheckBox) findViewById(R.id.chboxShanbe);
        chbox1Shanbe = (CheckBox) findViewById(R.id.chbox1Shanbe);
        chbox2Shanbe = (CheckBox) findViewById(R.id.chbox2Shanbe);
        chbox3Shanbe = (CheckBox) findViewById(R.id.chbox3Shanbe);
        chbox4Shanbe = (CheckBox) findViewById(R.id.chbox4Shanbe);
        chbox5Shanbe = (CheckBox) findViewById(R.id.chbox5Shanbe);


    }

    private void eventListener() {
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DrNobatActivity.this.onBackPressed();
            }
        });
        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setStartDate();
            }
        });
        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setEndDate();
            }
        });
        startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setStartTime();
            }
        });
        endTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setEndTime();
            }
        });
        btnInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertItem();
            }
        });
        btnShowTurn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fromDate = startDate.getText().toString().trim().isEmpty() ? "" : shortStartDate;
                String toDate = endDate.getText().toString().trim().isEmpty() ? "" : shortEndDate;
                if (checkFieldShow(fromDate, toDate)) {
                    task_getAllTurn = new asyncCallGetAllTurn();
                    task_getAllTurn.execute(fromDate, toDate);
                }
//                asyncCallTurnFromToday task = new asyncCallTurnFromToday();
//                task.execute();
            }
        });

    }

    private boolean checkFieldShow(String fromDate, String toDate) {
        if (fromDate.equals("")) {
            new MessageBox(DrNobatActivity.this, "تاریخ شروع مشخص نشده است !").show();
            return false;
        }
        if (toDate.equals("")) {
            new MessageBox(DrNobatActivity.this, "تاریخ پایان مشخص نشده است !").show();
            return false;
        }
        if (!IsEndDateGreatherThanStartDate(toDate, fromDate)) {
            new MessageBox(DrNobatActivity.this, "تاریخ پایان باید از تاریخ شروع نوبت دهی بیشتر باشد !").show();
            return false;
        }
        return true;
    }

    private void setEndTime() {
        final CustomTimePickerDialog timePicker = new CustomTimePickerDialog(DrNobatActivity.this);
        timePicker.show();
        timePicker.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                if (timePicker.BUTTON_TYPE == Dialog.BUTTON_POSITIVE) {
                    endTime.setText(timePicker.getTime());
                }
            }
        });
    }

    private void setStartTime() {
        final CustomTimePickerDialog timePicker = new CustomTimePickerDialog(DrNobatActivity.this);
        timePicker.show();
        timePicker.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                if (timePicker.BUTTON_TYPE == Dialog.BUTTON_POSITIVE) {
                    startTime.setText(timePicker.getTime());
                }
            }
        });
    }

    private void setStartDate() {
        final CustomDatePickerDialog datePicker = new CustomDatePickerDialog(this);
        datePicker.show();
        datePicker.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                if (datePicker.BUTTON_TYPE == Dialog.BUTTON_POSITIVE) {
                    startDate.setText(datePicker.getDate());
                    shortStartDate = datePicker.getShortDate();
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
                if (datePicker.BUTTON_TYPE == Dialog.BUTTON_POSITIVE) {
                    endDate.setText(datePicker.getDate());
                    shortEndDate = datePicker.getShortDate();
                }
            }
        });
    }

    private void insertItem() {
        if (checkField()) {
            task_addTurn = new callAsyncAddTurnWS();
            task_addTurn.execute();
        }
    }

    private void clearForm(ViewGroup group) {
        startTime.setText("");
        endTime.setText("");
        startDate.setText("");
        endDate.setText("");
        for (int i = 0, count = group.getChildCount(); i < count; ++i) {
            View view = group.getChildAt(i);
            if (view instanceof EditText) {
                ((EditText) view).setText("");
            }

            if (view instanceof ViewGroup && (((ViewGroup) view).getChildCount() > 0))
                clearForm((ViewGroup) view);
        }
    }

//    private void scrollMyListViewToBottom() {
//        listView.post(new Runnable() {
//            @Override
//            public void run() {
//                listView.setSelection(myListAdapter.getCount() - 1);
//            }
//        });
//    }

    private boolean checkField() {

        if (startTime.getText().toString().trim().isEmpty()) {
            new MessageBox(DrNobatActivity.this, "زمان شروع نوبت دهی مشخص نشده است !").show();
            return false;
        }
        if (endTime.getText().toString().trim().isEmpty()) {
            new MessageBox(DrNobatActivity.this, "زمان پایان نوبت دهی مشخص نشده است !").show();
            return false;
        }
        if (maxCapacity.getText().toString().trim().isEmpty()) {
            new MessageBox(DrNobatActivity.this, "ظرفیت نوبت دهی مشخص نشده است !").show();
            return false;
        }
        if (Integer.parseInt(maxCapacity.getText().toString().trim()) <= 0) {
            new MessageBox(DrNobatActivity.this, "ظرفیت نوبت دهی نباید کمتر از یک باشد !").show();
            return false;
        }
        if (startDate.getText().toString().trim().isEmpty()) {
            new MessageBox(DrNobatActivity.this, "تاریخ شروع نوبت دهی مشخص نشده است !").show();
            return false;
        }
        if (endDate.getText().toString().trim().isEmpty()) {
            new MessageBox(DrNobatActivity.this, "تاریخ پایان نوبت دهی مشخص نشده است !").show();
            return false;
        }
        if (!IsEndDateGreatherThanStartDate(shortStartDate, new PersianCalendar().getPersianShortDate())) {
            new MessageBox(DrNobatActivity.this, "تاریخ شروع نوبت دهی نامعتبر است !").show();
            return false;
        }
        if (!IsEndDateGreatherThanStartDate(shortEndDate, new PersianCalendar().getPersianShortDate())) {
            new MessageBox(DrNobatActivity.this, "تاریخ پایان نوبت دهی نامعتبر است !").show();
            return false;
        }
        if (!isEndTimeGreaterThanStartTime()) {
            new MessageBox(DrNobatActivity.this, "زمان شروع نوبت دهی باید از زمان پایان نوبت دهی کوچکتر باشد .").show();
            return false;
        }
        if (!IsEndDateGreatherThanStartDate(shortEndDate, shortStartDate)) {
            new MessageBox(DrNobatActivity.this, "تاریخ پایان نوبت دهی نباید کمتر از تاریخ شروع نوبت دهی باشد !").show();
            return false;
        }
        return true;
    }


    private boolean IsEndDateGreatherThanStartDate(String fromDate, String toDate) {

        String[] a_startDate = fromDate.split("/");
        String[] a_endDate = toDate.split("/");

        int y = Integer.parseInt(a_startDate[0]);
        int m = Integer.parseInt(a_startDate[1]);
        int d = Integer.parseInt(a_startDate[2]);

        int y1 = Integer.parseInt(a_endDate[0]);
        int m1 = Integer.parseInt(a_endDate[1]);
        int d1 = Integer.parseInt(a_endDate[2]);
        if (y > y1)
            return true;
        if (y < y1)
            return false;
        if (m > m1)
            return true;
        if (m < m1)
            return false;
        if (d > d1)
            return true;
        if (d < d1)
            return false;
        return true;
    }

    private boolean isEndTimeGreaterThanStartTime() {
        String start = startTime.getText().toString().trim();
        String end = endTime.getText().toString().trim();

        String[] startTime = start.split(":");
        String[] endTime = end.split(":");

        int startHour = Integer.parseInt(startTime[0]);
        int startMinute = Integer.parseInt(startTime[1]);

        int endHour = Integer.parseInt(endTime[0]);
        int endMinute = Integer.parseInt(endTime[1]);

        if (startHour > endHour) {
            return false;
        } else if (startHour == endHour) {
            if (startMinute > endMinute) {
                return false;
            }
        }
        hour = startHour;
        min = startMinute;
        duration = ((endHour - startHour) * 60) + ((endMinute - startMinute));
        return true;
    }

    private class callAsyncAddTurnWS extends AsyncTask<String, Void, Void> {
        boolean result;
        String weak;
        String msg = null;
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(DrNobatActivity.this, "", "در حال ثبت نوبت ...");
            dialog.getWindow().setGravity(Gravity.END);
            dialog.setCancelable(true);
            btnInsert.setClickable(false);
            btnShowTurn.setClickable(false);
            capacity = Integer.parseInt(maxCapacity.getText().toString().trim());
            dayOfWeek = (chboxShanbe.isChecked()) ? "0" : "";
            dayOfWeek += (chbox1Shanbe.isChecked()) ? "1" : "";
            dayOfWeek += (chbox2Shanbe.isChecked()) ? "2" : "";
            dayOfWeek += (chbox3Shanbe.isChecked()) ? "3" : "";
            dayOfWeek += (chbox4Shanbe.isChecked()) ? "4" : "";
            dayOfWeek += (chbox5Shanbe.isChecked()) ? "5" : "";
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                result = WebService.invokeAddTurnByDateWS(G.UserInfo.getUserName(), G.UserInfo.getPassword()
                        , G.officeId, shortStartDate, shortEndDate, hour, min, duration, capacity, dayOfWeek);
            } catch (PException ex) {
                msg = ex.getMessage();
            }
            return null;

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (msg != null) {
                dialog.dismiss();
                new MessageBox(DrNobatActivity.this, msg).show();
            } else {
                dialog.dismiss();
                if (result) {
                    Toast.makeText(DrNobatActivity.this, "ثبت نوبت با موفقیت انجام شد .", Toast.LENGTH_SHORT).show();
                    clearForm(((ViewGroup) findViewById(R.id.group_drNobatActivity)));
                } else {
                    new MessageBox(DrNobatActivity.this, "خطایی در ثبت اطلاعات رخ داده است، لطفا پارامترهای ورودی را چک نمایید.").show();
                }
            }
            btnInsert.setClickable(true);
            btnShowTurn.setClickable(true);
        }
    }

    private class asyncCallGetAllTurn extends AsyncTask<String, Void, Void> {
        String msg = null;
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(DrNobatActivity.this, "", "در حال دریافت اطلاعات ...");
            dialog.getWindow().setGravity(Gravity.END);
            dialog.setCancelable(true);
            btnShowTurn.setClickable(false);
            btnInsert.setClickable(false);
            txtWait.setVisibility(View.VISIBLE);

        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                turns = WebService.invokeGetAllTurnWS(G.UserInfo.getUserName(), G.UserInfo.getPassword(), G.officeId
                        , strings[0], strings[1]);
            } catch (PException ex) {
                msg = ex.getMessage();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (msg != null) {
                dialog.dismiss();
                new MessageBox(DrNobatActivity.this, msg).show();
            } else {
                if (turns != null && turns.size() != 0) {
                    adpter = new CustomListAdapterNobat(DrNobatActivity.this, turns);
                    listView.setAdapter(adpter);
                    txtWait.setVisibility(View.GONE);
                    dialog.dismiss();
                } else {
                    txtWait.setVisibility(View.GONE);
                    listView.setAdapter(null);
                    dialog.dismiss();
                    Toast.makeText(DrNobatActivity.this, "هیچ نوبتی در این بازه زمانی وجود ندارد .", Toast.LENGTH_SHORT).show();
                }
            }
            btnShowTurn.setClickable(true);
            btnInsert.setClickable(true);
        }
    }
}
