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
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.doctorsbuilding.nav.Dr.Profile.ExpandListAdapter;
import com.example.doctorsbuilding.nav.Util.MessageBox;
import com.example.doctorsbuilding.nav.Web.WebService;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by hossein on 9/4/2016.
 */
public class ActivityPatientFile extends AppCompatActivity {
    PatientFileListAdapter expAdapter;
    ExpandableListView expListView;
    int lastExpandedPosition = -1;
    int lastClickedPosition = 0;
    String patientUserName = null;
    Button backBtn;

    TextView nothing;
    asyncGetPatientFile task_getPatientFile = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_patient_file);
        patientUserName = getIntent().getExtras().getString("patientUserName");
        expListView = (ExpandableListView) findViewById(R.id.patient_file_exp_lv);
        backBtn = (Button) findViewById(R.id.patientfile_backBtn);
        nothing = (TextView) findViewById(R.id.patientfile_nothing);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        if(patientUserName!=null) {
            task_getPatientFile = new asyncGetPatientFile();
            task_getPatientFile.execute(patientUserName);
        }

    }
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(task_getPatientFile != null)
            task_getPatientFile.cancel(true);
    }

    private void setListener(){
        expListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
                if (lastExpandedPosition != -1
                        && groupPosition != lastExpandedPosition) {
                    expListView.collapseGroup(lastExpandedPosition);
                }
                lastExpandedPosition = groupPosition;

            }
        });
        expListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int groupPosition, long id) {
                Boolean shouldExpand = (!expListView.isGroupExpanded(groupPosition));
                expListView.collapseGroup(lastClickedPosition);

                if (shouldExpand) {
                    expListView.expandGroup(groupPosition);
                    expListView.setSelectionFromTop(groupPosition, 0);
                }
                lastClickedPosition = groupPosition;
                return true;
            }
        });
    }

    private class asyncGetPatientFile extends AsyncTask<String, Void, Void> {
        String msg = null;
        ProgressDialog dialog;
        Map<Integer, ArrayList<PatientFile>> map = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(ActivityPatientFile.this, "", "در حال دریافت اطلاعات ...");
            dialog.getWindow().setGravity(Gravity.END);
            dialog.setCancelable(true);
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                map = WebService.invokeGetPatientFileWS(G.UserInfo.getUserName(), G.UserInfo.getPassword(), G.officeId, strings[0]);
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
                new MessageBox(ActivityPatientFile.this, msg).show();
            } else {
                if(map.size() > 0) {
                    fillPatientList(map);
                }else {
                    nothing.setVisibility(View.VISIBLE);
                }

                dialog.dismiss();
            }
        }
    }

    private void fillPatientList(Map<Integer, ArrayList<PatientFile>> map) {
        ArrayList<PatientFileGroup> groups = new ArrayList<PatientFileGroup>();
        Iterator it = map.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry pair = (Map.Entry)it.next();
            ArrayList<PatientFile> patientFiles = (ArrayList<PatientFile>)pair.getValue();
            PatientFileGroup group = new PatientFileGroup(patientFiles.get(0));

            ArrayList<PatientFileChild> childs = new ArrayList<PatientFileChild>();
            for(int j = 0; j < patientFiles.size(); j++){
                PatientFileChild child = new PatientFileChild(patientFiles.get(j));
                childs.add(child);
            }
            group.setItem(childs);
            groups.add(group);
        }
        expAdapter = new PatientFileListAdapter(ActivityPatientFile.this, groups);
        expListView.setAdapter(expAdapter);
        for(int i=0; i < expAdapter.getGroupCount(); i++)
            expListView.expandGroup(i);
        //setListener();
    }

}
