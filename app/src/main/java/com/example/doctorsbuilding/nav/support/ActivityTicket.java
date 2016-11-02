package com.example.doctorsbuilding.nav.support;

import android.app.ProgressDialog;
import android.media.tv.TvView;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.doctorsbuilding.nav.Dr.Clinic.Office;
import com.example.doctorsbuilding.nav.Expert;
import com.example.doctorsbuilding.nav.G;
import com.example.doctorsbuilding.nav.PException;
import com.example.doctorsbuilding.nav.R;
import com.example.doctorsbuilding.nav.SubExpert;
import com.example.doctorsbuilding.nav.User.City;
import com.example.doctorsbuilding.nav.Util.MessageBox;
import com.example.doctorsbuilding.nav.Web.WebService;

import java.util.ArrayList;

/**
 * Created by hossein on 11/1/2016.
 */
public class ActivityTicket extends AppCompatActivity {

    private EditText topic;
    private Spinner sp_subject;
    private Spinner sp_priority;
    private EditText content;
    private Button backBtn;
    private Button sendBtn;
    private ArrayAdapter<Priority> priority_adapter;
    private ArrayAdapter<Subject> subject_adapter;
    private AsyncGetSubjectWS getSubjectTask;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_ticket);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        initViews();
        eventListener();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (getSubjectTask != null)
            getSubjectTask.cancel(true);
    }

    private void initViews() {
        topic = (EditText) findViewById(R.id.ticket_topic);
        sp_subject = (Spinner) findViewById(R.id.ticket_subject);
        sp_priority = (Spinner) findViewById(R.id.ticket_priority);
        content = (EditText) findViewById(R.id.ticket_message);
        backBtn = (Button) findViewById(R.id.ticket_backBtn);
        sendBtn = (Button) findViewById(R.id.ticket_send);

        subject_adapter = new ArrayAdapter<Subject>(ActivityTicket.this, R.layout.spinner_item);
        sp_subject.setAdapter(subject_adapter);

        getSubjectTask = new AsyncGetSubjectWS();
        getSubjectTask.execute();

        priority_adapter = new ArrayAdapter<Priority>(ActivityTicket.this, R.layout.spinner_item);
        sp_priority.setAdapter(priority_adapter);
        addItemmToSpPriority();

    }

    private void addItemmToSpPriority() {
        priority_adapter.add(Priority.low);
        priority_adapter.add(Priority.medium);
        priority_adapter.add(Priority.hight);
    }

    private void eventListener() {
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private boolean check_field() {
        if (topic.getText().toString().trim().isEmpty()) {
            new MessageBox(this, "لطفا عنوان درخواست را مشخص نمایید .").show();
            return false;
        }
        if (sp_priority.getSelectedItemPosition() == -1) {
            new MessageBox(this, "لطفا موضوع درخواست را مشخص نمایید .").show();
            return false;
        }
        if (sp_priority.getSelectedItemPosition() == -1) {
            new MessageBox(this, "لطفا اولویت درخواست را مشخص نمایید .").show();
            return false;
        }
        if (content.getText().toString().trim().isEmpty()) {
            new MessageBox(this, "لطفا متن درخواست را مشخص نمایید .").show();
            return false;
        }

        return true;
    }


    private class AsyncGetSubjectWS extends AsyncTask<String, Void, Void> {
        ArrayList<Subject> subject_list;
        String msg = null;
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(ActivityTicket.this, "", "در حال دریافت اطلاعات ...");
            dialog.getWindow().setGravity(Gravity.END);
            dialog.setCancelable(true);
            sendBtn.setClickable(false);
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                subject_list = WebService.invokeGetTicketSubjectWS(G.UserInfo.getUserName(), G.UserInfo.getPassword(), G.officeId);
            } catch (PException ex) {
                msg = ex.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            sendBtn.setClickable(true);
            if (msg != null) {
                dialog.dismiss();
                new MessageBox(ActivityTicket.this, msg).show();
            } else {
                if (subject_list != null && subject_list.size() != 0) {

                    for (Subject s : subject_list)
                        subject_adapter.add(s);

                    dialog.dismiss();
                } else {
                    dialog.dismiss();
                    new MessageBox(ActivityTicket.this, "دریافت اطلاعات با مشکل مواجه شده است .").show();
                }
            }
        }
    }
}
