package com.example.doctorsbuilding.nav;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.doctorsbuilding.nav.Util.MessageBox;
import com.example.doctorsbuilding.nav.Util.NonScrollListView;
import com.example.doctorsbuilding.nav.Web.WebService;

import java.util.ArrayList;

/**
 * Created by hossein on 9/3/2016.
 */
public class ActivityPatientListToday extends AppCompatActivity {
    private Button backBtn;
    private NonScrollListView listView;
    private TextView txtNothing;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_patient_list_today);
        backBtn = (Button) findViewById(R.id.patientList_backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        listView = (NonScrollListView) findViewById(R.id.patientList_listview);
        txtNothing = (TextView) findViewById(R.id.patientList_nothing);
        asyncCallGetPatientList task = new asyncCallGetPatientList();
        task.execute();

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
                if (patientInfos != null && patientInfos.size() > 0) {
                    listView.setAdapter(new CustomPatientListTodayListAdapter(ActivityPatientListToday.this, patientInfos));
                    dialog.dismiss();
                } else {
                    txtNothing.setVisibility(View.VISIBLE);
                    dialog.dismiss();
                }
            }
        }
    }
}
