package com.example.doctorsbuilding.nav.MainForm;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.doctorsbuilding.nav.Databases.DatabaseAdapter;
import com.example.doctorsbuilding.nav.Dr.Clinic.Office;
import com.example.doctorsbuilding.nav.G;
import com.example.doctorsbuilding.nav.MainActivity;
import com.example.doctorsbuilding.nav.PException;
import com.example.doctorsbuilding.nav.R;
import com.example.doctorsbuilding.nav.Reservation;
import com.example.doctorsbuilding.nav.User.User;
import com.example.doctorsbuilding.nav.UserType;
import com.example.doctorsbuilding.nav.Util.DbBitmapUtility;
import com.example.doctorsbuilding.nav.Util.MessageBox;
import com.example.doctorsbuilding.nav.Util.Util;
import com.example.doctorsbuilding.nav.Web.WebService;

import java.util.ArrayList;
import java.util.EventListener;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by hossein on 11/16/2016.
 */
public class DialogAddOffice extends Dialog {

    EditText txt_officeid;
    Button btn_add;
    Context context;
    Office office = null;
    DatabaseAdapter database;
    int officeId = -1;
    AsyncInsertOffice task_insertOffice = null;

    public DialogAddOffice(Context context, int themeResId) {
        super(context, themeResId);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_add_office);
        setTitle("لطفا کد مطب را وارد نمایید :");
        initViews();
        eventListener();

    }

    private void initViews() {
        txt_officeid = (EditText) findViewById(R.id.add_office_txt);
        btn_add = (Button) findViewById(R.id.add_office_btn);
    }

    private void eventListener() {
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkField()) {
                    officeId = Integer.valueOf(txt_officeid.getText().toString().trim());
                    task_insertOffice = new AsyncInsertOffice();
                    task_insertOffice.execute();
                }
            }
        });
    }

    private boolean checkField() {
        if (txt_officeid.getText().toString().trim().isEmpty()) {
            new MessageBox(context, "لطفا کد مطب را وارد نمایید .").show();
            return false;
        }
        if (checkIfExistOfficeId(Integer.valueOf(txt_officeid.getText().toString()))) {
            new MessageBox(context, "کد مطب تکراری می باشد .").show();
            return false;
        }
        return true;
    }

    private boolean checkIfExistOfficeId(int officeId) {
        boolean exist = false;
        DatabaseAdapter database = new DatabaseAdapter(context);
        ArrayList<Office> offices = new ArrayList<Office>();
        if (database.openConnection()) {
            offices = database.getoffices();
        }
        for (Office of : offices) {
            if (of.getId() == officeId) {
                exist = true;
                break;
            }
        }
        return exist;
    }

    public void setOffice(Office office) {
        this.office = office;
    }

    public Office getOffice() {
        return this.office;
    }

    private class AsyncInsertOffice extends AsyncTask<String, Void, Void> {
        String msg = null;
        Bitmap doctorPic = null;
        String result = null;
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(context, "", "در حال ثبت مطب ...");
            dialog.show();
            dialog.getWindow().setGravity(Gravity.END);
            dialog.setCancelable(true);
            btn_add.setClickable(false);
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                result = WebService.invokeAddOfficeForUserWS(G.UserInfo.getUserName(), G.UserInfo.getPassword(), officeId);
                office = WebService.invokeGetOfficeInfoWS(G.UserInfo.getUserName(), G.UserInfo.getPassword(), officeId);
                doctorPic = WebService.invokeGetDoctorPicWS(G.UserInfo.getUserName(), G.UserInfo.getPassword(), officeId);
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
                new MessageBox(context, msg).show();
                btn_add.setClickable(true);
            } else {
                dialog.dismiss();
                if (result != null && result.toUpperCase().equals("OK")) {
                    if (office != null) {
                        if (doctorPic == null)
                            doctorPic = BitmapFactory.decodeResource(context.getResources(), R.drawable.doctor);

                        office.setPhoto(doctorPic);
                        database = new DatabaseAdapter(context);
                        if (database.openConnection()) {
                            long result = database.insertoffice(office);
                            if (result == -1) {
                                database.updateOffice(officeId, office);
                            }
                            database.closeConnection();
                        }
                        dismiss();
                        Toast.makeText(context, "ثبت با موفقیت انجام شد .", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    new MessageBox(context, result).show();
                }
                btn_add.setClickable(true);
            }
        }
    }

}
