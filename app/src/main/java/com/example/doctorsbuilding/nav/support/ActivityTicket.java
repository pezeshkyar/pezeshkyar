package com.example.doctorsbuilding.nav.support;

import android.app.ProgressDialog;
import android.content.Context;
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
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
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

import java.sql.SQLException;
import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by hossein on 11/1/2016.
 */
public class ActivityTicket extends AppCompatActivity {

    private EditText topic;
    private Spinner sp_subject;
    private Spinner sp_priority;
    private EditText content;
    private ImageButton backBtn;
    private TextView pageTitle;
    private Button sendBtn;
    private ArrayAdapter<Priority> priority_adapter;
    private ArrayAdapter<Subject> subject_adapter;
    private AsyncGetSubjectWS getSubjectTask;
    private TicketSender ticketSender;

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
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
    @Override
    protected void onPause() {
        super.onPause();
        if (getSubjectTask != null)
            getSubjectTask.cancel(true);
        if (ticketSender != null)
            ticketSender.cancel(true);
    }

    private void initViews() {
        topic = (EditText) findViewById(R.id.ticket_topic);
        sp_subject = (Spinner) findViewById(R.id.ticket_subject);
        sp_priority = (Spinner) findViewById(R.id.ticket_priority);
        content = (EditText) findViewById(R.id.ticket_message);
        backBtn = (ImageButton) findViewById(R.id.toolbar_backBtn);
        pageTitle = (TextView)findViewById(R.id.toolbar_title);
        pageTitle.setText("درخواست جدید");
        sendBtn = (Button) findViewById(R.id.ticket_send);

        subject_adapter = new ArrayAdapter<Subject>(ActivityTicket.this, R.layout.spinner_item);
        sp_subject.setAdapter(subject_adapter);

        getSubjectTask = new AsyncGetSubjectWS();
        getSubjectTask.execute();

        priority_adapter = new ArrayAdapter<Priority>(ActivityTicket.this, R.layout.spinner_item);
        sp_priority.setAdapter(priority_adapter);
        addItemmToSpPriority();

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(check_field())
                    new TicketSender().execute("");
            }
        });

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

    class TicketSender extends AsyncTask<String, String, String>{
        Ticket t;
        String errMsg = null;
        int ticketId;
        String ticketMsg;
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(ActivityTicket.this, "", "در حال ارسال اطلاعات ...");
            dialog.getWindow().setGravity(Gravity.END);
            dialog.setCancelable(true);
            sendBtn.setClickable(false);

            t = new Ticket();
            Subject s = (Subject)sp_subject.getSelectedItem();
            t.setSubject(s.getSubject());
            t.setSubject_id(s.getId());
            t.setPriority((int) sp_priority.getSelectedItemId());
            t.setTopic(topic.getText().toString());
            ticketMsg = content.getText().toString();
        }

        @Override
        protected String doInBackground(String... strings) {
            String res = null;
            try {
                ticketId = WebService.invokeRegisterTicketWS(
                        G.UserInfo.getUserName(), G.UserInfo.getPassword(),
                        G.officeId, t);
                t.setId(ticketId);
                res = WebService.invokeSetUserTicketMessageWS(
                        G.UserInfo.getUserName(), G.UserInfo.getPassword(), ticketId, ticketMsg);
            } catch (PException e) {
                errMsg = e.getMessage();
            }

            return res;
        }

        @Override
        protected void onPostExecute(String res) {
            super.onPostExecute(res);
            sendBtn.setClickable(true);
            if(errMsg != null) {
                dialog.dismiss();
                new MessageBox(ActivityTicket.this, errMsg).show();
            }else {
                if (res != null && !res.toLowerCase().equals("error")) {
                    G.mAdapter.add(t);
                    ActivityTicket.this.finish();
                }else {
                    dialog.dismiss();
                    new MessageBox(ActivityTicket.this, "خطایی در ثبت درخواست رخ داده است .").show();
                }
            }
        }
    }
}
