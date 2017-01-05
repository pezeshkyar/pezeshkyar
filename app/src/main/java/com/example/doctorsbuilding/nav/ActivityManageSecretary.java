package com.example.doctorsbuilding.nav;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.media.MediaRouter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.doctorsbuilding.nav.User.User;
import com.example.doctorsbuilding.nav.Util.MessageBox;
import com.example.doctorsbuilding.nav.Web.WebService;

import java.sql.Array;
import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by hossein on 10/15/2016.
 */
public class ActivityManageSecretary extends AppCompatActivity {

    private EditText txt_username;
    private Button btn_add;
    private ImageButton backBtn;
    private TextView pageTitle;
    private Button btn_delete;
    private ListView mListview;
    private FrameLayout lock_pan;
    private ImageView btn_refresh;
    private int selected_id = -1;
    private ArrayAdapter<User> mAdpater;
    private ArrayList<User> secretaries;

    private AsyncGetSecretary asyncGetSecretary;
    private AsyncAddSecretary asyncAddSecretary;
    private AsyncRemoveSecretary asyncRemoveSecretary;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        G.setStatusBarColor(ActivityManageSecretary.this);
        setContentView(R.layout.activity_manage_secretary);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        initView();

        asyncGetSecretary = new AsyncGetSecretary();
        asyncGetSecretary.execute();

        eventListener();

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (asyncAddSecretary != null) {
            asyncAddSecretary.cancel(true);
        }
        if (asyncGetSecretary != null) {
            asyncGetSecretary.cancel(true);
        }
        if (asyncRemoveSecretary != null) {
            asyncRemoveSecretary.cancel(true);
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private void initView() {
        txt_username = (EditText) findViewById(R.id.manage_secretary_username);
        btn_add = (Button) findViewById(R.id.manage_secretary_addBtn);
        btn_delete = (Button) findViewById(R.id.manage_secretary_deleteBtn);
        btn_refresh = (ImageView) findViewById(R.id.manage_secretary_refresh);
        lock_pan = (FrameLayout) findViewById(R.id.manage_secretary_framLayout);
        mListview = (ListView) findViewById(R.id.manage_secretary_listView);
        pageTitle = (TextView)findViewById(R.id.toolbar_title);
        pageTitle.setText("مدیریت منشی");
        backBtn = (ImageButton) findViewById(R.id.toolbar_backBtn);
        secretaries = new ArrayList<User>();

    }

    private void eventListener() {
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkField_add()) {
                    asyncAddSecretary = new AsyncAddSecretary();
                    asyncAddSecretary.execute();
                }
            }
        });
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                asyncRemoveSecretary = new AsyncRemoveSecretary();
                asyncRemoveSecretary.execute();
            }
        });

        btn_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refreshForm();
            }
        });

        mListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                lockForm(position);
                txt_username.setText(secretaries.get(selected_id).getUserName());
                txt_username.setEnabled(false);
            }
        });
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private boolean checkField_add() {
        if (txt_username.getText().toString().trim().equals("")) {
            new MessageBox(ActivityManageSecretary.this, "لطفا نام کاربری منشی را وارد نمایید .").show();
            return false;
        }
        return true;
    }

    private void lockForm(int position) {
        selected_id = position;
        mListview.setEnabled(false);
        lock_pan.setVisibility(View.VISIBLE);
        btn_add.setVisibility(View.GONE);
        btn_delete.setVisibility(View.VISIBLE);
    }

    private void refreshForm() {
        selected_id = -1;
        mListview.setEnabled(true);
        lock_pan.setVisibility(View.GONE);
        btn_add.setVisibility(View.VISIBLE);
        btn_delete.setVisibility(View.GONE);
        txt_username.setEnabled(true);
        txt_username.setText("");
    }

    private class AsyncGetSecretary extends AsyncTask<String, Void, Void> {

        private String msg = null;
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(ActivityManageSecretary.this, "", "در حال دریافت اطلاعات ...");
            dialog.getWindow().setGravity(Gravity.END);
            dialog.setCancelable(true);
            btn_add.setClickable(false);
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                secretaries = WebService.invokeGetSecretaryInfoWS(G.UserInfo.getUserName(), G.UserInfo.getPassword(), G.officeId);
            } catch (PException ex) {
                msg = ex.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dialog.dismiss();
            if (msg != null) {
                new MessageBox(ActivityManageSecretary.this, msg).show();
            } else {
                if (secretaries != null && secretaries.size() != 0) {
                    mAdpater = new ArrayAdapter<User>(ActivityManageSecretary.this, R.layout.spinner_item, secretaries);
                    mListview.setAdapter(mAdpater);
                }
            }

            btn_add.setClickable(true);

        }
    }

    private class AsyncAddSecretary extends AsyncTask<String, Void, Void> {

        private String msg = null;
        private User result;
        private String secretary_username;
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(ActivityManageSecretary.this,"", "در حال افزودن منشی ...");
            dialog.getWindow().setGravity(Gravity.END);
            dialog.setCancelable(true);
            btn_add.setClickable(false);
            mListview.setEnabled(false);
            secretary_username = txt_username.getText().toString().trim();
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                result = WebService.invokeAddSecretaryWS(G.UserInfo.getUserName(), G.UserInfo.getPassword(), G.officeId, secretary_username);

            } catch (PException ex) {
                msg = ex.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dialog.dismiss();
            if (msg != null) {
                dialog.dismiss();
                new MessageBox(ActivityManageSecretary.this, msg).show();
            } else {
                if (result != null) {
                    secretaries.add(result);
                    mAdpater = new ArrayAdapter<User>(ActivityManageSecretary.this, R.layout.spinner_item, secretaries);
                    mListview.setAdapter(mAdpater);
                    mAdpater.notifyDataSetChanged();
                    dialog.dismiss();
                } else {
                    dialog.dismiss();
                    new MessageBox(ActivityManageSecretary.this, "نام کاربری وارد شده مجاز نیست .").show();
                }
            }
            btn_add.setClickable(true);
            mListview.setEnabled(true);
        }
    }

    private class AsyncRemoveSecretary extends AsyncTask<String, Void, Void> {

        private String msg = null;
        private boolean result;
        private String secretary_username;
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(ActivityManageSecretary.this, "", "در حال حذف منشی ...");
            dialog.getWindow().setGravity(Gravity.END);
            dialog.setCancelable(true);
            btn_delete.setClickable(false);
            mListview.setEnabled(false);
            secretary_username = txt_username.getText().toString().trim();
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                result = WebService.invokeRemoveSecretaryWS(G.UserInfo.getUserName(), G.UserInfo.getPassword(), G.officeId, secretary_username);

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
                new MessageBox(ActivityManageSecretary.this, msg).show();
            } else {
                if (result) {
                    secretaries.remove(selected_id);
                    mAdpater = new ArrayAdapter<User>(ActivityManageSecretary.this, R.layout.spinner_item, secretaries);
                    mAdpater.notifyDataSetChanged();
                    dialog.dismiss();
                    refreshForm();
                }
            }
            btn_delete.setClickable(true);
            mListview.setEnabled(true);
        }
    }
}
