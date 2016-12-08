package com.example.doctorsbuilding.nav.MainForm;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.doctorsbuilding.nav.Dr.Clinic.Office;
import com.example.doctorsbuilding.nav.FragmentPatientMedicalHistory;
import com.example.doctorsbuilding.nav.G;
import com.example.doctorsbuilding.nav.PException;
import com.example.doctorsbuilding.nav.R;
import com.example.doctorsbuilding.nav.Util.MessageBox;
import com.example.doctorsbuilding.nav.Web.WebService;

import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by hossein on 12/4/2016.
 */
public class ActivityAllDoctors extends AppCompatActivity implements EndLessListView.EndlessListener {

    private int provinceId, cityId, specId, subSpecId;
    private String firstname, lastname;
    private ProgressDialog dialog;
    private TextView pageTitle;
    private FrameLayout frm_nothing;
    private Button backBtn;
    private EndLessListView mListView;
    private FloatingActionButton mFab;
    private ArrayList<Office> offices = new ArrayList<Office>();
    private CustomOfficeEndLessAdapter adapter_office = null;
    private AsyncGetAllOfficeForCity task_getAllOfficeForCity;
    private AsyncGetAllOfficeByFilter task_getAllOfficeByFilter;
    private AsyncGetDoctorPic task_getDoctorPic;
    private ArrayList<Boolean> vist_list = new ArrayList<>();
    private static final int COUNT = 5;
    private int step = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_all_doctors);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        G.setStatusBarColor(ActivityAllDoctors.this);
        initViews();

        adapter_office = new CustomOfficeEndLessAdapter(this, new ArrayList<Office>());
        mListView.setLoadingView(R.layout.loading_layout);
        mListView.setAdapter(adapter_office);
        mListView.setListener(this);

        dialog = ProgressDialog.show(ActivityAllDoctors.this, "", "لطفا شکیبا باشید ...");
        dialog.show();
        dialog.getWindow().setGravity(Gravity.END);
        dialog.setCancelable(true);

        task_getAllOfficeForCity = new AsyncGetAllOfficeForCity();
        task_getAllOfficeForCity.execute();

        eventListener();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (task_getAllOfficeForCity != null)
            task_getAllOfficeForCity.cancel(true);
        if (task_getDoctorPic != null)
            task_getDoctorPic.cancel(true);
        if (task_getAllOfficeByFilter != null) {
            task_getAllOfficeByFilter.cancel(true);
        }
    }

    private void resetForm() {
//        adapter_office.removeAll(offices);
        offices.clear();
        vist_list.clear();
        step = 1;
    }

    private void initViews() {
        pageTitle = (TextView) findViewById(R.id.toolbar_title);
        pageTitle.setText("پزشک های سامانه");
        frm_nothing = (FrameLayout) findViewById(R.id.myDoctor_nothing);
        backBtn = (Button) findViewById(R.id.toolbar_backBtn);
        mListView = (EndLessListView) findViewById(R.id.mydoctor_lv);
        mFab = (FloatingActionButton) findViewById(R.id.mydoctor_add_dr);
    }

    private void eventListener() {
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                frm_nothing.setVisibility(View.GONE);
                onPause();
                dialog.dismiss();
                final DialogSearchFilter filterDialog = new DialogSearchFilter(ActivityAllDoctors.this, android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar_MinWidth);
                filterDialog.show();
                filterDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        filterDialog.stopAllAsyncTask();
                        if (filterDialog.isApplyFilter()) {
                            provinceId = filterDialog.getProvinceId();
                            cityId = filterDialog.getCityId();
                            specId = filterDialog.getSpecId();
                            subSpecId = filterDialog.getSubSpecId();
                            firstname = filterDialog.getFname();
                            lastname = filterDialog.getLname();
                            resetForm();
                            task_getAllOfficeByFilter = new AsyncGetAllOfficeByFilter();
                            task_getAllOfficeByFilter.execute();

                        }
                    }
                });
            }
        });

    }

    @Override
    public void loadData() {
        if (mListView.getChildCount() >= COUNT * step) {
            step += 1;
            if (task_getAllOfficeByFilter != null) {
                task_getAllOfficeByFilter = new AsyncGetAllOfficeByFilter();
                task_getAllOfficeByFilter.execute();
            } else {
                task_getAllOfficeForCity = new AsyncGetAllOfficeForCity();
                task_getAllOfficeForCity.execute();
            }
        } else {
            mListView.stopLoading();
        }

    }

    @Override
    public void loadImage(int position) {
        if (!vist_list.get(position)) {
            vist_list.set(position, true);
            task_getDoctorPic = new AsyncGetDoctorPic();
            task_getDoctorPic.execute(String.valueOf(position));
        }
    }

    private class AsyncGetAllOfficeForCity extends AsyncTask<String, Void, Void> {
        String msg = null;
        ArrayList<Office> officeha = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                officeha = WebService.invokeGetAllOfficesForCityWS(
                        G.UserInfo.getUserName(), G.UserInfo.getPassword(), G.UserInfo.getCityID(), COUNT, step);
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
                new MessageBox(ActivityAllDoctors.this, msg).show();
            } else {
                dialog.dismiss();

                if (officeha != null && officeha.size() > 0) {
                    for (Office of : officeha) {
                        of.setPhoto(BitmapFactory.decodeResource(getResources(), R.drawable.doctor));
                        offices.add(of);
                        vist_list.add(false);
                    }
                    mListView.addNewData(offices);
                    if (step == 1 && offices.size() != 0) {
                        if (!vist_list.get(0) && !isCancelled()) {
                            vist_list.set(0, true);
                            task_getDoctorPic = new AsyncGetDoctorPic();
                            task_getDoctorPic.execute(String.valueOf(0));
                        }
                    }
                } else {
                    if (step == 1) {
                        frm_nothing.setVisibility(View.VISIBLE);
                        mFab.bringToFront();
                    }
                }

            }
        }

    }

    private class AsyncGetAllOfficeByFilter extends AsyncTask<String, Void, Void> {
        String msg = null;
        ArrayList<Office> officeha = null;
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(ActivityAllDoctors.this, "", "لطفا شکیبا باشید ...");
            dialog.setCancelable(false);
            dialog.getWindow().setGravity(Gravity.END);
            mFab.setClickable(false);

        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                officeha = WebService.invokeGetOfficeByFilterWS(
                        G.UserInfo.getUserName(), G.UserInfo.getPassword(), provinceId, cityId, specId, subSpecId, firstname, lastname, COUNT, step);
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
                mFab.setClickable(true);
                new MessageBox(ActivityAllDoctors.this, msg).show();
            } else {
                dialog.dismiss();
                mFab.setClickable(true);
                if (officeha != null && officeha.size() > 0) {
                    for (Office of : officeha) {
                        of.setPhoto(BitmapFactory.decodeResource(getResources(), R.drawable.doctor));
                        offices.add(of);
                        vist_list.add(false);
                    }
                    mListView.addNewData(offices);
                    if (step == 1 && offices.size() != 0) {
                        if (!vist_list.get(0) && !isCancelled()) {
                            vist_list.set(0, true);
                            task_getDoctorPic = new AsyncGetDoctorPic();
                            task_getDoctorPic.execute(String.valueOf(0));
                        }
                    }
                } else {
                    if (step == 1)
                        new MessageBox(ActivityAllDoctors.this, "هیچ نتیجه ای با پارامتر های ورودی شما یافت نشده است .").show();
                }
            }

        }
    }

    private class AsyncGetDoctorPic extends AsyncTask<String, Void, Void> {
        String msg = null;
        Bitmap drpic = null;
        int position;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                position = Integer.valueOf(strings[0]);
                drpic = WebService.invokeGetDoctorPicWS(G.UserInfo.getUserName(), G.UserInfo.getPassword(), offices.get(position).getId());

            } catch (PException ex) {
                msg = ex.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (msg != null) {
                new MessageBox(ActivityAllDoctors.this, msg).show();
            } else {

                if (drpic != null) {
                    Office office = offices.get(position);
                    office.setPhoto(drpic);
                    adapter_office.update(position, office);
                }
                int index = getNextPicIndex();
                if (step == 1 && offices.size() != 0 && !vist_list.get(index)) {
                    vist_list.set(index, true);
                    task_getDoctorPic = new AsyncGetDoctorPic();
                    task_getDoctorPic.execute(String.valueOf(index));
                }

            }
        }
    }

    private int getNextPicIndex() {
        int index = 0;
        for (int i = 0; i < vist_list.size(); i++) {
            if (!vist_list.get(i)) {
                index = i;
                break;
            }
        }
        return index;
    }

}
