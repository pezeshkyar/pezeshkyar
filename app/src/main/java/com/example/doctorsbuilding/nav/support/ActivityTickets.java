package com.example.doctorsbuilding.nav.support;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.doctorsbuilding.nav.ActivityImageShow;
import com.example.doctorsbuilding.nav.G;
import com.example.doctorsbuilding.nav.PException;
import com.example.doctorsbuilding.nav.R;
import com.example.doctorsbuilding.nav.Util.MessageBox;
import com.example.doctorsbuilding.nav.Web.WebService;

import java.util.ArrayList;
import java.util.EventListener;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by hossein on 11/1/2016.
 */
public class ActivityTickets extends AppCompatActivity {

    private ListView mListView;
    private FloatingActionButton mFab;
    private ImageButton backBtn;
    private TextView pageTitle;
    private AsyncGetTicketWS getTicketTask;

    @Override
    protected void onPause() {
        super.onPause();
        if(getTicketTask != null)
            getTicketTask.cancel(true);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        G.setStatusBarColor(ActivityTickets.this);

        setContentView(R.layout.activity_tickets);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        initViews();
        eventListener();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private void initViews() {
        mListView = (ListView)findViewById(R.id.tickets_listView);
        mFab = (FloatingActionButton)findViewById(R.id.tickets_fab);
        pageTitle = (TextView)findViewById(R.id.toolbar_title);
        pageTitle.setText("لیست درخواست ها");
        backBtn = (ImageButton) findViewById(R.id.toolbar_backBtn);
        G.mAdapter = new ArrayAdapter<Ticket>(ActivityTickets.this, R.layout.spinner_item);
        mListView.setAdapter(G.mAdapter);

        getTicketTask = new AsyncGetTicketWS();
        getTicketTask.execute();
    }

    private void eventListener() {
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Ticket t = G.mAdapter.getItem(position);
                Intent intent = new Intent(ActivityTickets.this, DiscussActivity.class);
                intent.putExtra("id", t.getId());
                startActivity(intent);
            }
        });
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ActivityTickets.this, ActivityTicket.class));
            }
        });

    }

    private class AsyncGetTicketWS extends AsyncTask<String, Void, Void> {
        ArrayList<Ticket> ticket_list;
        String msg = null;
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(ActivityTickets.this, "", "در حال دریافت اطلاعات ...");
            dialog.getWindow().setGravity(Gravity.END);
            dialog.setCancelable(true);
            mFab.setClickable(false);
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                ticket_list = WebService.invokeGetTicketWS(G.UserInfo.getUserName(), G.UserInfo.getPassword(), G.officeId);
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
                new MessageBox(ActivityTickets.this, msg).show();
            } else {
                if (ticket_list != null && ticket_list.size() != 0) {

                    for (Ticket t : ticket_list)
                        G.mAdapter.add(t);

                    dialog.dismiss();
                } else {
                    dialog.dismiss();
//                    new MessageBox(ActivityTickets.this, "دریافت اطلاعات با مشکل مواجه شده است .").show();
                }
            }
            mFab.setClickable(true);
        }
    }
}
