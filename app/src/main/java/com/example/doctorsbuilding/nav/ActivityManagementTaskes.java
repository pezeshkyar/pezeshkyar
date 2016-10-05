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
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.ViewFlipper;

import com.example.doctorsbuilding.nav.Util.MessageBox;
import com.example.doctorsbuilding.nav.Util.Util;
import com.example.doctorsbuilding.nav.Web.WebService;

import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by hossein on 10/3/2016.
 */
public class ActivityManagementTaskes extends AppCompatActivity {

    private ArrayList<Task> taskes;
    private ListView taskListView;
    private ListView subtask_listview;
    private FrameLayout listview_highlight;
    private EditText task_name;
    private EditText subtask_name;
    private EditText subtask_price;
    private Button task_btn_add;
    private Button task_btn_edit;
    private Button backBtn;
    private Button subtask_btnBack;
    private Button subTask_btnDelete;
    private Button subTask_btnEdit;
    private Button subTask_btnAdd;
    private ImageView task_btn_refresh;
    private ViewFlipper mViewFlipper;
    private Spinner subtask_spinner;
    private LinearLayout subTask_editLayout;
    private int selected_task_id = -1;
    private int selected_subtask_id = -1;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_manage_tasks);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        initViews();
        eventListeners();

        asyncCallGetTaskes getTaskes = new asyncCallGetTaskes();
        getTaskes.execute();
    }

    private void refreshTaskForm() {
        selected_task_id = -1;
        task_name.setText("");
        task_btn_add.setVisibility(View.VISIBLE);
        task_btn_edit.setVisibility(View.GONE);
        listview_highlight.setVisibility(View.GONE);
        taskListView.setEnabled(true);
    }

    private void lockTaskForm(int position){
        selected_task_id = position;
        task_name.setText(taskes.get(position).toString());
        task_btn_add.setVisibility(View.GONE);
        task_btn_edit.setVisibility(View.VISIBLE);
        listview_highlight.setVisibility(View.VISIBLE);
        taskListView.setEnabled(false);
    }

    private void refreshSubTaskForm() {
        selected_subtask_id = -1;
        subtask_spinner.setSelection(0);
        subtask_name.setText("");
        subtask_price.setText("");
        subTask_btnAdd.setVisibility(View.VISIBLE);
        subTask_editLayout.setVisibility(View.GONE);

    }
    private void lockSubTaskActivity(int position){
        selected_subtask_id = position;
        subtask_spinner.setSelection(selected_subtask_id);
        subtask_name.setText(taskes.get(selected_subtask_id).toString());
        subtask_price.setText(Util.getCurrency(taskes.get(selected_subtask_id).getPrice()));
        subTask_btnAdd.setVisibility(View.GONE);
        subTask_editLayout.setVisibility(View.VISIBLE);
    }

    private void initViews() {
        taskListView = (ListView) findViewById(R.id.manage_task_listView);
        subtask_listview = (ListView) findViewById(R.id.add_task_listView);
        task_name = (EditText) findViewById(R.id.manage_taskes_taskname);
        subtask_name = (EditText) findViewById(R.id.subtask_name);
        subtask_price = (EditText) findViewById(R.id.subtask_price);
        task_btn_add = (Button) findViewById(R.id.manage_taskes_addBtn);
        task_btn_edit = (Button) findViewById(R.id.manage_taskes_editBtn);
        listview_highlight = (FrameLayout) findViewById(R.id.manage_task_listView_highlight);
        task_btn_refresh = (ImageView) findViewById(R.id.manage_task_refresh);
        backBtn = (Button) findViewById(R.id.manage_taskes_backBtn);
        subtask_btnBack = (Button) findViewById(R.id.add_task_btnBack);
        mViewFlipper = (ViewFlipper) findViewById(R.id.manage_taskes_viewFlipper);
        subtask_spinner = (Spinner) findViewById(R.id.add_task_taskes);
        subTask_btnEdit = (Button) findViewById(R.id.subTask_editBtn);
        subTask_btnDelete = (Button) findViewById(R.id.subTask_deleteBtn);
        subTask_editLayout = (LinearLayout) findViewById(R.id.subTask_editLayout);
        subTask_btnAdd = (Button) findViewById(R.id.add_task_acceptBtn);
    }

    private void eventListeners() {
        task_btn_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             refreshTaskForm();
            }
        });
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        task_btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNext();
                subTask_btnAdd.setVisibility(View.VISIBLE);
                mViewFlipper.setDisplayedChild(1);
                ArrayAdapter<Task> adpater = new ArrayAdapter<Task>(ActivityManagementTaskes.this, R.layout.spinner_item, taskes);
                subtask_spinner.setAdapter(adpater);
                subtask_listview.setAdapter(adpater);
                subtask_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                       lockSubTaskActivity(position);
                    }
                });
            }
        });
        subtask_btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPrevious();
                refreshSubTaskForm();
                refreshTaskForm();
                mViewFlipper.setDisplayedChild(0);
            }
        });
        task_btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNext();
                subTask_editLayout.setVisibility(View.VISIBLE);
                subTask_btnAdd.setVisibility(View.GONE);
                mViewFlipper.setDisplayedChild(1);
                ArrayAdapter<Task> adpater = new ArrayAdapter<Task>(ActivityManagementTaskes.this, R.layout.spinner_item, taskes);
                subtask_spinner.setAdapter(adpater);
                subtask_spinner.setSelection(selected_task_id);
                subtask_listview.setAdapter(adpater);
            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private void showNext() {
        mViewFlipper.setInAnimation(getBaseContext(), R.anim.slide_in_from_left);
        mViewFlipper.setOutAnimation(getBaseContext(), R.anim.slide_out_to_right);
        mViewFlipper.showNext();
    }

    private void showPrevious() {
        mViewFlipper.setInAnimation(getBaseContext(), R.anim.slide_in_from_right);
        mViewFlipper.setOutAnimation(getBaseContext(), R.anim.slide_out_to_left);
        mViewFlipper.showPrevious();
    }

    private class asyncCallGetTaskes extends AsyncTask<String, Void, Void> {
        String msg = null;
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(ActivityManagementTaskes.this, "", "در حال دریافت اطلاعات ...");
            dialog.getWindow().setGravity(Gravity.END);
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                taskes = WebService.invokeGetTaskWS(G.UserInfo.getUserName(), G.UserInfo.getPassword(), G.officeId);
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
                new MessageBox(ActivityManagementTaskes.this, msg).show();
            } else {
                if (taskes != null && taskes.size() != 0) {
                    ArrayAdapter<Task> adapter = new ArrayAdapter<Task>(ActivityManagementTaskes.this, R.layout.spinner_item, taskes);
                    taskListView.setAdapter(adapter);
                    taskListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                           lockTaskForm(position);
                        }
                    });
                }
                dialog.dismiss();
            }
        }
    }
}
