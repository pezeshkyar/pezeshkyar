package com.example.doctorsbuilding.nav;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.example.doctorsbuilding.nav.User.User;
import com.example.doctorsbuilding.nav.Util.MessageBox;
import com.example.doctorsbuilding.nav.Web.WebService;

import java.util.ArrayList;

/**
 * Created by hossein on 7/24/2016.
 */
public class DialogSearchPatient extends Dialog {

    private EditText firstName;
    private EditText lastName;
    private EditText userName;
    private EditText mobile;
    private Button searchBtn;
    private ListView listView;
    private int resevationId = 0;
    private int turnId;
    ArrayList<User> users = null;

    public DialogSearchPatient(Context context, int turnId) {
        super(context, android.R.style.Theme_DeviceDefault_Dialog_MinWidth);
        this.turnId = turnId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setTitle("جستجوی بیمار");
        setContentView(R.layout.dialog_search_layout);
        firstName = (EditText) findViewById(R.id.search_name);
        lastName = (EditText) findViewById(R.id.search_family);
        userName = (EditText) findViewById(R.id.search_username);
        mobile = (EditText) findViewById(R.id.search_mobile);
        searchBtn = (Button) findViewById(R.id.search_btn);
        listView = (ListView) findViewById(R.id.search_listView);

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                asyncCallSearchUser task = new asyncCallSearchUser();
                task.execute();
            }
        });

    }

    public int getResevationId() {
        return resevationId;
    }

    private class asyncCallSearchUser extends AsyncTask<String, Void, Void> {
        User user = null;
        ProgressDialog dialog;
        String msg = null;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(getContext());
            dialog.setTitle("در حال ارسال اطلاعات ...");
            dialog.setCancelable(false);
            dialog.show();
            user = new User();
            user.setUserName(userName.getText().toString().trim());
            user.setFirstName(firstName.getText().toString().trim());
            user.setLastName(lastName.getText().toString().trim());
            user.setPhone(mobile.getText().toString());
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
                new MessageBox(getContext(), msg).show();
            } else {
//            listView.setAdapter(new CustomReservationListAdapter(getContext(), users, turnId));
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View container, int position, long id) {
                        asyncCallReserveWS task = new asyncCallReserveWS();
                        task.execute(users.get(position).getUserName());
                    }
                });
                dialog.dismiss();
            }
        }
    }

    private class asyncCallReserveWS extends AsyncTask<String, Void, Void> {

        int result = 0;
        Reservation reservation = null;
        String msg = null;
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(getContext());
            dialog.setTitle("در حال ارسال اطلاعات ...");
            dialog.setCancelable(false);
            dialog.show();
            reservation = new Reservation();
            reservation.setTurnId(turnId);
            reservation.setFirstReservationId(0);
            reservation.setTaskId(1);
            reservation.setNumberOfTurns(1);
        }

        @Override
        protected Void doInBackground(String... strings) {
            reservation.setPatientUserName(strings[0]);
            try {
                result = WebService.invokeResevereForUser(G.UserInfo.getUserName(), G.UserInfo.getPassword(), reservation);
            } catch (PException ex) {
                msg = ex.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(msg!=null){
                dialog.dismiss();
                    new MessageBox(getContext(), msg).show();
            }else {
                if (result > 0) {
                    resevationId = result;
                    dialog.dismiss();
                    dismiss();
                }
                dialog.dismiss();
            }
        }
    }

}
