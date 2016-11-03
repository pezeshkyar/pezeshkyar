package com.example.doctorsbuilding.nav.support;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.example.doctorsbuilding.nav.Dr.persindatepicker.util.PersianCalendar;
import com.example.doctorsbuilding.nav.G;
import com.example.doctorsbuilding.nav.PException;
import com.example.doctorsbuilding.nav.R;
import com.example.doctorsbuilding.nav.Web.WebService;

import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class DiscussActivity extends Activity {
    private int ticketId;
    private DiscussArrayAdapter adapter;
    private ListView mListView;
    private EditText mEditText;
    private ImageButton mBtnSend;
    PersianCalendar persianCalendar = new PersianCalendar();

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discuss);

        ticketId = this.getIntent().getIntExtra("id", 0);

        mListView = (ListView) findViewById(R.id.support_listview);
        mBtnSend = (ImageButton) findViewById(R.id.support_btn_send);
        adapter = new DiscussArrayAdapter(getApplicationContext(), R.layout.listitem_discuss);
        mListView.setAdapter(adapter);
        mEditText = (EditText) findViewById(R.id.support_edit_text);

        mBtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                adapter.add(new OneComment(false, mEditText.getText().toString()
//                        , persianCalendar.getPersianLongDateAndTime(), G.UserInfo.getFirstName() + " " + G.UserInfo.getLastName()));
                new messageSender().execute("");

                mEditText.setText("");
                try {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                } catch (Exception ex) {

                }
                mListView.setSelection(mListView.getChildCount());
            }
        });

        addItems();
    }


    private void addItems() {
//        adapter.add(new OneComment(true, "سلام، مشکل چیه ؟!", persianCalendar.getPersianLongDateAndTime(), "حسین سالخورده"));
        new messageReciever().execute("");
    }

    class messageSender extends AsyncTask<String, String, Boolean>{
        String msg;
        ProgressDialog dialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(DiscussActivity.this, "", "در حال ارسال اطلاعات ...");
            dialog.getWindow().setGravity(Gravity.END);
            dialog.setCancelable(true);
            msg = mEditText.getText().toString();
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            boolean res;
            try {
                String str = WebService.invokeSetUserTicketMessageWS(G.UserInfo.getUserName(), G.UserInfo.getPassword(), G.officeId, ticketId, msg);
                res = str.toLowerCase().equals("ok") ?true : false;
            } catch (PException e) {
                return false;
            } catch(Throwable t){
                res = false;
                System.out.println(t.getMessage());
            }
            return res;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if(result){
                adapter.add(new OneComment(false, msg, persianCalendar.getPersianLongDateAndTime(), G.UserInfo.getFirstName() +   " " + G.UserInfo.getLastName()));
                dialog.dismiss();
            } else {
                dialog.dismiss();
                Toast.makeText(DiscussActivity.this,"خطا در ارتباط با سرور", Toast.LENGTH_LONG).show();
            }
        }
    }

    class messageReciever extends AsyncTask<String, String, Boolean>{
        ArrayList<Message> messages;
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(DiscussActivity.this, "", "در حال دریافت اطلاعات ...");
            dialog.getWindow().setGravity(Gravity.END);
            dialog.setCancelable(true);
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            boolean res = true;
            try {
                messages = WebService.invokeGetUserTicketMessageWS(G.UserInfo.getUserName(), G.UserInfo.getPassword(), G.officeId, ticketId);
            } catch (PException e) {
                res = false;
            }
            return res;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            if(messages != null) {
                for (Message m : messages) {
                    boolean isLeft = m.getUsername().equals(G.UserInfo.getUserName()) ? false : true;
                    adapter.add(new OneComment(isLeft, m.getMessage(), m.getDate(), m.getFirstName() + " " + m.getLastName()));
                }
            }
            dialog.dismiss();
        }
    }
}