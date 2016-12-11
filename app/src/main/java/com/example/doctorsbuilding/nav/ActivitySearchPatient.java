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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.doctorsbuilding.nav.User.User;
import com.example.doctorsbuilding.nav.Util.MessageBox;
import com.example.doctorsbuilding.nav.Web.WebService;

import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by hossein on 9/6/2016.
 */
public class ActivitySearchPatient extends AppCompatActivity {
    private EditText username;
    private EditText name;
    private EditText lastname;
    private EditText mobileNo;
    private ImageButton backBtn;
    private Button searchBtn;
    TextView pageTitle;
    private ListView mListView;
    asyncCallSearchUser task_searchUser = null;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_search_patient);
        intiViews();
        eventsListener();
    }
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(task_searchUser != null)
            task_searchUser.cancel(true);
    }

    private void intiViews() {
        username = (EditText) findViewById(R.id.searchPatient_username);
        name = (EditText) findViewById(R.id.searchPatient_name);
        lastname = (EditText)findViewById(R.id.searchPatient_family);
        mobileNo = (EditText)findViewById(R.id.searchPatient_mobile);
        pageTitle = (TextView)findViewById(R.id.toolbar_title);
        pageTitle.setText("جستجو");
        backBtn = (ImageButton) findViewById(R.id.toolbar_backBtn);
        searchBtn = (Button) findViewById(R.id.searchPatient_btnSearch);
        mListView = (ListView) findViewById(R.id.searchPatient_listView);
    }

    private void eventsListener() {
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                task_searchUser = new asyncCallSearchUser();
                task_searchUser.execute();
            }
        });
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private class asyncCallSearchUser extends AsyncTask<String, Void, Void> {
        User user = null;
        ArrayList<User> users = new ArrayList<User>();
        String msg = null;
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(ActivitySearchPatient.this, "", "در حال دریافت اطلاعات ...");
            dialog.getWindow().setGravity(Gravity.END);
            dialog.setCancelable(true);
            searchBtn.setClickable(false);
            mListView.setEnabled(false);
            user = new User();
            user.setUserName(username.getText().toString().trim());
            user.setFirstName(name.getText().toString().trim());
            user.setLastName(lastname.getText().toString().trim());
            user.setPhone(mobileNo.getText().toString());
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                users = WebService.invokeSearchUserWS(user.getUserName(), user.getFirstName(), user.getLastName(), user.getPhone());
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
                new MessageBox(ActivitySearchPatient.this, msg).show();
            } else {
                if (users != null && users.size() != 0) {
                    ArrayList<String> userInfo = null;
                    final ArrayList<ArrayList<String>> userha = new ArrayList<ArrayList<String>>();
                    for (User user : users) {
                        userInfo = new ArrayList<String>();
                        userInfo.add(user.getFirstName() + " " + user.getLastName());
                        userInfo.add(user.getPhone());
                        userInfo.add(user.getUserName());
                        userha.add(userInfo);
                    }
                    mListView.setAdapter(new CustomListViewSearchPatient(ActivitySearchPatient.this, userha));
                    dialog.dismiss();
                } else {
                    dialog.dismiss();
                    Toast.makeText(ActivitySearchPatient.this, "بیماری با این مشخصات یافت نشده است .", Toast.LENGTH_SHORT).show();
                }
            }
            searchBtn.setClickable(true);
            mListView.setEnabled(true);
        }
    }

}
