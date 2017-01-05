package com.example.doctorsbuilding.nav;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.doctorsbuilding.nav.Dr.Profile.ExpChild;
import com.example.doctorsbuilding.nav.Dr.Profile.ExpGroup;
import com.example.doctorsbuilding.nav.Util.MessageBox;
import com.example.doctorsbuilding.nav.Web.WebService;

import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by hossein on 9/4/2016.
 */
public class ActivityAddNextTurn extends AppCompatActivity {
    private PatientInfo patientInfo;
    private String patientUsername;
    private int firstReservationId;
    private CustomListAdapterAddNextTurn expAdapter;
    private ExpandableListView expListView;
    private int lastExpandedPosition = -1;
    private int lastClickedPosition = 0;
    private ArrayList<ExpGroup> list_Group;
    private ArrayList<ExpGroup> groups;
    private ArrayList<ExpChild> childs;
    private ImageButton backBtn;
    TextView pageTitle;
    asyncCallTurn task_getTurn =null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        G.setStatusBarColor(ActivityAddNextTurn.this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.activity_add_next_turn);
        patientInfo = (PatientInfo)getIntent().getSerializableExtra("patientInfo");
        expListView = (ExpandableListView) findViewById(R.id.addNextTurn_explv);
        pageTitle = (TextView)findViewById(R.id.toolbar_title);
        pageTitle.setText("نوبت ها");
        backBtn = (ImageButton) findViewById(R.id.toolbar_backBtn);

        task_getTurn = new asyncCallTurn();
        task_getTurn.execute();

    }
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(task_getTurn != null)
            task_getTurn.cancel(true);
    }

    private void setListener(){
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        expListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener()

                                             {

                                                 @Override
                                                 public void onGroupExpand(int groupPosition) {
                                                     if (lastExpandedPosition != -1
                                                             && groupPosition != lastExpandedPosition) {
                                                         expListView.collapseGroup(lastExpandedPosition);
                                                     }
                                                     lastExpandedPosition = groupPosition;

                                                 }
                                             }

        );
        expListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener()

                                            {
                                                @Override
                                                public boolean onGroupClick(ExpandableListView expandableListView, View view,
                                                                            int groupPosition, long id) {
                                                    Boolean shouldExpand = (!expListView.isGroupExpanded(groupPosition));
                                                    expListView.collapseGroup(lastClickedPosition);

                                                    if (shouldExpand) {
                                                        expListView.expandGroup(groupPosition);
                                                        expListView.setSelectionFromTop(groupPosition, 0);
                                                    }
                                                    lastClickedPosition = groupPosition;
                                                    return true;
                                                }

                                            }
        );

    }

    private class asyncCallTurn extends AsyncTask<String, Void, Void> {
        ArrayList<Turn> result;
        String msg = null;
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(ActivityAddNextTurn.this, "","در حال دریافت اطلاعات ...");
            progressDialog.getWindow().setGravity(Gravity.END);
            progressDialog.setCancelable(true);
        }


        @Override
        protected Void doInBackground(String... strings) {
            try {
                result = WebService.invokeGetAllTurnFromToday(G.UserInfo.getUserName(), G.UserInfo.getPassword(), G.officeId);
            } catch (PException ex) {
                msg = ex.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (msg != null) {
                progressDialog.dismiss();
                new MessageBox(ActivityAddNextTurn.this, msg).show();
            } else {
                groups = new ArrayList<ExpGroup>();
                int i = 0;
                while (i < result.size()) {

                    String str = result.get(i).getDate();
                    ExpChild child = new ExpChild(result.get(i));
                    childs = new ArrayList<ExpChild>();
                    childs.add(child);

                    while (i + 1 < result.size() && str.equals(result.get(i + 1).getDate())) {

                        child = new ExpChild(result.get(i + 1));
                        childs.add(child);
                        i++;
                    }

                    ExpGroup group = new ExpGroup();
                    group.setName(result.get(i).getLongDate());
                    group.setItem(childs);
                    groups.add(group);
                    i++;
                }

                list_Group = groups;
                try {
                    expAdapter = new CustomListAdapterAddNextTurn(ActivityAddNextTurn.this, list_Group, patientInfo);
                    expListView.setAdapter(expAdapter);
                }catch (Exception ex){
                    ex.getMessage();
                }

                setListener();
                progressDialog.dismiss();
            }
        }
    }

}
