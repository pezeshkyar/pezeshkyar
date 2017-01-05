package com.example.doctorsbuilding.nav.User;

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
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.doctorsbuilding.nav.Dr.Nobat.DrNobatActivity;
import com.example.doctorsbuilding.nav.G;
import com.example.doctorsbuilding.nav.MessageInfo;
import com.example.doctorsbuilding.nav.PException;
import com.example.doctorsbuilding.nav.R;
import com.example.doctorsbuilding.nav.Util.MessageBox;
import com.example.doctorsbuilding.nav.Web.WebService;

import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by hossein on 6/13/2016.
 */
public class UserInboxActivity extends AppCompatActivity {

    private MessageInfo messageInfo = null;
    private ArrayList<MessageInfo> messageInfos = null;
    private ListView listView;
    private ListAdapter adapter;
    private ImageButton backBtn;
    TextView pageTitle;
    AsyncCallGetAllMessagesWs task_getAllMessages = null;
    AsyncCallSetMessageReadWs task_SetMessageRead = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        G.setStatusBarColor(UserInboxActivity.this);
        setContentView(R.layout.activity_user_inbox);
        listView = (ListView) findViewById(R.id.userInbox_listView);
        messageInfos = new ArrayList<MessageInfo>();
        pageTitle = (TextView)findViewById(R.id.toolbar_title);
        pageTitle.setText("صندوق پیام");
        backBtn = (ImageButton) findViewById(R.id.toolbar_backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserInboxActivity.this.onBackPressed();
            }
        });

        messageInfo = (MessageInfo) getIntent().getSerializableExtra("MessageInfo");
        if (messageInfo != null) {
            messageInfos.add(messageInfo);
            adapter = new CustomListAdapterUserInbox(UserInboxActivity.this, messageInfos);
            listView.setAdapter(adapter);
            task_SetMessageRead = new AsyncCallSetMessageReadWs();
            task_SetMessageRead.execute();

        } else {
            task_getAllMessages = new AsyncCallGetAllMessagesWs();
            task_getAllMessages.execute();
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (task_getAllMessages != null)
            task_getAllMessages.cancel(true);
        if (task_SetMessageRead != null)
            task_SetMessageRead.cancel(true);
    }

    private class AsyncCallGetAllMessagesWs extends AsyncTask<String, Void, Void> {
        String msg = null;
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(UserInboxActivity.this, "", "در حال دریافت اطلاعات ...");
            dialog.getWindow().setGravity(Gravity.END);
            dialog.setCancelable(true);
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                messageInfos = WebService.invokeGetAllMessagesWS(G.UserInfo.getUserName(), G.UserInfo.getPassword());
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
                new MessageBox(UserInboxActivity.this, msg).show();
            } else {
                if (messageInfos != null && messageInfos.size() != 0) {
                    listView = (ListView) findViewById(R.id.userInbox_listView);
                    adapter = new CustomListAdapterUserInbox(UserInboxActivity.this, messageInfos);
                    listView.setAdapter(adapter);
                    dialog.dismiss();
                } else {
                    dialog.dismiss();
                    Toast.makeText(UserInboxActivity.this, "هیچ پیامی برای شما وجود ندارد .",Toast.LENGTH_LONG).show();
                }
            }
        }


    }

    private class AsyncCallSetMessageReadWs extends AsyncTask<String, Void, Void> {
        String msg = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                WebService.invokeSetMessageRead2WS(G.UserInfo.getUserName(), G.UserInfo.getPassword(), messageInfo.getId());
            } catch (PException ex) {
                msg = ex.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (msg != null) {
                new MessageBox(UserInboxActivity.this, msg).show();
            }
        }
    }
}
