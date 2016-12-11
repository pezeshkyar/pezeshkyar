package com.example.doctorsbuilding.nav;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.doctorsbuilding.nav.Util.MessageBox;
import com.example.doctorsbuilding.nav.Util.NonScrollListView;
import com.example.doctorsbuilding.nav.Web.WebService;

import java.util.ArrayList;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by hossein on 9/3/2016.
 */
public class ActivityPatientListToday extends AppCompatActivity {
    private ImageButton backBtn;
    TextView pageTitle;
    private ListView mListView;
    asyncCallGetPatientList task_getPatientList=null;
    private CustomPatientListTodayListAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_patient_list_today);
        adapter = new CustomPatientListTodayListAdapter(ActivityPatientListToday.this);
        pageTitle = (TextView)findViewById(R.id.toolbar_title);
        pageTitle.setText("لیست پذیرش امروز");
        backBtn = (ImageButton) findViewById(R.id.toolbar_backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        mListView = (ListView) findViewById(R.id.patientList_listview);
        mListView.setAdapter(adapter);
        task_getPatientList = new asyncCallGetPatientList();
        task_getPatientList.execute();

    }
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(task_getPatientList != null)
            task_getPatientList.cancel(true);
    }

    private class asyncCallGetPatientList extends AsyncTask<String, Void, Void> {
        String msg = null;
        ProgressDialog dialog;
        ArrayList<PatientInfo> patientInfos = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(ActivityPatientListToday.this, "", "در حال دریافت اطلاعات ...");
            dialog.getWindow().setGravity(Gravity.END);
            dialog.setCancelable(true);
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                patientInfos = WebService.invokeGetTodayPatientWS(G.UserInfo.getUserName(), G.UserInfo.getPassword(), G.officeId);
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
                new MessageBox(ActivityPatientListToday.this, msg).show();
            } else {
                dialog.dismiss();
                if (patientInfos != null && patientInfos.size() > 0) {
                    adapter.addAll(patientInfos);

                } else {
                    Toast.makeText(ActivityPatientListToday.this, "بیماری برای پذیرش وجود ندارد .",Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
