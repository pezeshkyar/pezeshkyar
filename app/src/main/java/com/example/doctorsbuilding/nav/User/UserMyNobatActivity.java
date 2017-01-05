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

import com.example.doctorsbuilding.nav.G;
import com.example.doctorsbuilding.nav.PException;
import com.example.doctorsbuilding.nav.R;
import com.example.doctorsbuilding.nav.ReservationByUser;
import com.example.doctorsbuilding.nav.Util.MessageBox;
import com.example.doctorsbuilding.nav.Web.WebService;

import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by hossein on 6/9/2016.
 */
public class UserMyNobatActivity extends AppCompatActivity {

    Button moreBtn;
    ListView listView;
    ListAdapter adapter;
    private ArrayList<ReservationByUser> resevations = new ArrayList<ReservationByUser>();
    private final int count = 10;
    private int index = 0;
    ImageButton backBtn;
    TextView pageTitle;
    TextView turnTxtNothing;
    asyncCallGetReservayionByUserWS getReservayionByUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        G.setStatusBarColor(UserMyNobatActivity.this);
        setContentView(R.layout.activity_my_nobat_user);
        initViews();
        turnTxtNothing.setVisibility(View.GONE);
        asyncCallGetReservayionByUserWS task = new asyncCallGetReservayionByUserWS();
        task.execute();
        eventListener();
    }
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(getReservayionByUser != null)
            getReservayionByUser.cancel(true);
    }

    private void initViews() {
        turnTxtNothing = (TextView) findViewById(R.id.turnTxtNothing);
        listView = (ListView) findViewById(R.id.user_my_nobat_listview);
        moreBtn = (Button) findViewById(R.id.user_my_nobat_moreBtn);
        pageTitle = (TextView)findViewById(R.id.toolbar_title);
        pageTitle.setText("نوبت های من");
        backBtn = (ImageButton) findViewById(R.id.toolbar_backBtn);
    }

    private void eventListener() {
        moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                asyncCallGetReservayionByUserWS task = new asyncCallGetReservayionByUserWS();
                task.execute();
            }
        });
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserMyNobatActivity.this.onBackPressed();
            }
        });

    }


    private class asyncCallGetReservayionByUserWS extends AsyncTask<String, Void, Void> {
        ArrayList<ReservationByUser> result = null;
        String msg = null;
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            moreBtn.setEnabled(false);
            dialog = ProgressDialog.show(UserMyNobatActivity.this, "", "در حال دریافت اطلاعات ...");
            dialog.getWindow().setGravity(Gravity.END);
            dialog.setCancelable(true);
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                result = WebService.invokeGetReservayionByUserWS(G.UserInfo.getUserName(), G.UserInfo.getPassword()
                        , G.officeId, count, index);
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
                new MessageBox(UserMyNobatActivity.this, msg).show();
            } else {
                if (result != null && result.size() != 0) {
                    turnTxtNothing.setVisibility(View.GONE);
                    if (result.size() > 3) moreBtn.setVisibility(View.VISIBLE);
                    else moreBtn.setVisibility(View.GONE);
                    resevations.addAll(result);
                    adapter = new CustomListAdapterMyNobat(UserMyNobatActivity.this, resevations);
                    listView.setAdapter(adapter);
                    moreBtn.setEnabled(true);
                    index++;
                    dialog.dismiss();
                } else {
                    if (resevations.size() == 0) {
                        dialog.dismiss();
                        turnTxtNothing.setText("هیچ نوبتی برای شما ثبت نشده است !");
                        turnTxtNothing.setVisibility(View.VISIBLE);
                        Toast.makeText(UserMyNobatActivity.this, "هیچ نوبتی برای شما ثبت نشده است !", Toast.LENGTH_SHORT).show();
                    } else {
                        dialog.dismiss();
                        Toast.makeText(UserMyNobatActivity.this, "پیام بیشتری وجود ندارد .", Toast.LENGTH_SHORT).show();
                        moreBtn.setEnabled(true);
                    }
                }
            }
        }
    }
}
