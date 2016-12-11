package com.example.doctorsbuilding.nav;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.example.doctorsbuilding.nav.Util.MessageBox;
import com.example.doctorsbuilding.nav.Util.MoneyTextWatcher;
import com.example.doctorsbuilding.nav.Util.Util;
import com.example.doctorsbuilding.nav.Web.WebService;

import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by hossein on 10/3/2016.
 */
public class ActivityManagementTaskes extends AppCompatActivity {

    private ArrayAdapter<TaskGroup> taskGroup_adapter;
    private ListView taskGroup_ListView;
    private EditText taskGroup_name;
    private Button taskGroup_btn_add;
    private Button taskGroup_btn_edit;
    private ImageView taskGroup_btn_refresh;
    private FrameLayout taskGroup_listView_lock;
    private RelativeLayout mtoolbar;
    private int taskGroup_selectedId = -1;

    private ArrayAdapter<Task> task_adapter;
    private ListView task_listview;
    private Button task_btnBack;
    private Button task_btn_delete;
    private Button task_btn_edit;
    private Button task_btn_add;
    private Button task_btn_add_;
    private EditText task_name;
    private EditText task_price;
    private Spinner task_spinner_taskGroup;
    private RelativeLayout task_layout_edit;
    private int task_selectedId = -1;

    private ViewFlipper mViewFlipper;
    private ImageButton backBtn;
    private TextView pageTitle;

    private asyncCallGetTaskGroups getTaskGroups;
    private asyncCallGetTask asyncGetTask;
    private asyncAddTaskGroup addTaskGroups;
    private asyncUpdateTaskGroup updateTaskGroup;
    private asyncDeleteTaskGroup deleteTaskGroup;
    private asyncAddTask addTask;
    private asyncCallUpdateTask asyncUpdateTask;
    private asyncCallDeleteTask asyncDeleteTask;
    private asyncAddTask asyncAddTask;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_manage_tasks);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        initViews();
        eventListeners();

        getTaskGroups = new asyncCallGetTaskGroups();
        getTaskGroups.execute();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (getTaskGroups != null)
            getTaskGroups.cancel(true);
        if (asyncGetTask != null)
            asyncGetTask.cancel(true);
        if (addTaskGroups != null)
            addTaskGroups.cancel(true);
        if (updateTaskGroup != null)
            updateTaskGroup.cancel(true);
        if (deleteTaskGroup != null)
            deleteTaskGroup.cancel(true);
        if (addTask != null)
            addTask.cancel(true);
        if (asyncUpdateTask != null)
            asyncUpdateTask.cancel(true);
        if (asyncDeleteTask != null)
            asyncDeleteTask.cancel(true);
        if (asyncAddTask != null)
            asyncAddTask.cancel(true);

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private void refreshTaskGroupForm() {
        taskGroup_selectedId = -1;
        taskGroup_name.setText("");
        taskGroup_btn_add.setVisibility(View.VISIBLE);
        taskGroup_btn_edit.setVisibility(View.GONE);
        taskGroup_listView_lock.setVisibility(View.GONE);
        taskGroup_ListView.setEnabled(true);
    }

    private void lockTaskGroupForm(int position) {
        taskGroup_selectedId = position;
        taskGroup_name.setText(taskGroup_adapter.getItem(position).toString());
        taskGroup_btn_add.setVisibility(View.GONE);
        taskGroup_btn_edit.setVisibility(View.VISIBLE);
        taskGroup_listView_lock.setVisibility(View.VISIBLE);
        taskGroup_ListView.setEnabled(false);
    }

    private void refreshTaskForm() {
        task_selectedId = -1;
        task_spinner_taskGroup.setEnabled(true);
        task_spinner_taskGroup.setSelection(0);
        task_btn_add_.setEnabled(true);
        task_name.setText("");
        task_price.setText("");
        task_adapter.clear();
        if (task_adapter != null) {
            task_adapter.notifyDataSetChanged();
        }
        task_btn_add.setVisibility(View.VISIBLE);
        task_layout_edit.setVisibility(View.GONE);

    }

    private void refreshCurrentTaskForm() {
        task_selectedId = -1;
        task_btn_add_.setEnabled(true);
        task_name.setText("");
        task_price.setText("");
    }

    private void lockTaskForm(int position) {
        task_selectedId = position;
        task_btn_add_.setEnabled(false);
        task_name.setText(task_adapter.getItem(task_selectedId).toString());
        task_price.setText(Util.getCurrency(task_adapter.getItem(task_selectedId).getPrice()));
    }

    private boolean checkField_taskGroup() {
        if (taskGroup_name.getText().toString().trim().equals("")) {
            new MessageBox(ActivityManagementTaskes.this, "لطفا نام گروه خدمات را وارد نمایید .").show();
            return false;
        }
        return true;
    }

    private boolean checkField_task() {
        if (task_name.getText().toString().trim().equals("")) {
            new MessageBox(ActivityManagementTaskes.this, "لطفا نام زیرگروه خدمات را وارد نمایید .").show();
            return false;
        }
        if (task_price.getText().toString().trim().equals("")) {
            new MessageBox(ActivityManagementTaskes.this, "لطفا مبلغ پایه را مشخص نمایید .").show();
            return false;
        }
        try {
            int test = Integer.valueOf(Util.getNumber(task_price.getText().toString().trim()));
        } catch (Exception ex) {
            new MessageBox(ActivityManagementTaskes.this, "مبلغ وارد شده نادرست می باشد .").show();
            return false;
        }
        return true;
    }

    private void initViews() {
        mtoolbar = (RelativeLayout)findViewById(R.id.taskes_toolbar);
        pageTitle = (TextView)findViewById(R.id.toolbar_title);
        pageTitle.setText("مدیریت خدمات");
        backBtn = (ImageButton) findViewById(R.id.toolbar_backBtn);
        mViewFlipper = (ViewFlipper) findViewById(R.id.manage_taskes_viewFlipper);

        taskGroup_adapter = new ArrayAdapter<TaskGroup>(ActivityManagementTaskes.this, R.layout.spinner_item);
        taskGroup_ListView = (ListView) findViewById(R.id.manage_task_listView);
        taskGroup_ListView.setAdapter(taskGroup_adapter);
        taskGroup_name = (EditText) findViewById(R.id.manage_taskes_taskname);
        taskGroup_btn_add = (Button) findViewById(R.id.manage_taskes_addBtn);
        taskGroup_btn_edit = (Button) findViewById(R.id.manage_taskes_editBtn);
        taskGroup_listView_lock = (FrameLayout) findViewById(R.id.manage_task_listView_highlight);
        taskGroup_btn_refresh = (ImageView) findViewById(R.id.manage_task_refresh);


        task_adapter = new ArrayAdapter<Task>(ActivityManagementTaskes.this, R.layout.spinner_item);
        task_listview = (ListView) findViewById(R.id.add_task_listView);
        task_listview.setAdapter(task_adapter);
        task_spinner_taskGroup = (Spinner) findViewById(R.id.add_task_taskes);
        task_btnBack = (Button) findViewById(R.id.add_task_btnBack);
        task_name = (EditText) findViewById(R.id.subtask_name);
        task_price = (EditText) findViewById(R.id.subtask_price);
        task_btn_edit = (Button) findViewById(R.id.subTask_editBtn);
        task_btn_delete = (Button) findViewById(R.id.subTask_deleteBtn);
        task_btn_add_ = (Button) findViewById(R.id.subTask_addBtn);
        task_layout_edit = (RelativeLayout) findViewById(R.id.subTask_editLayout);
        task_btn_add = (Button) findViewById(R.id.add_task_acceptBtn);
    }

    private void eventListeners() {

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        taskGroup_btn_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refreshTaskGroupForm();
            }
        });

        taskGroup_btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (checkField_taskGroup()) {
                    addTaskGroups = new asyncAddTaskGroup();
                    addTaskGroups.execute();
                }
            }
        });

        taskGroup_btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNext();
                task_layout_edit.setVisibility(View.VISIBLE);
                task_btn_add.setVisibility(View.GONE);
                mViewFlipper.setDisplayedChild(1);
                task_spinner_taskGroup.setAdapter(taskGroup_adapter);
                task_spinner_taskGroup.setSelection(taskGroup_selectedId);
                task_spinner_taskGroup.setEnabled(false);
                asyncGetTask = new asyncCallGetTask();
                asyncGetTask.execute();

            }
        });

        task_price.addTextChangedListener(new MoneyTextWatcher(task_price));

        task_btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPrevious();
                refreshTaskForm();
                refreshTaskGroupForm();
                mViewFlipper.setDisplayedChild(0);
            }
        });

        task_btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkField_task()) {
                    addTask = new asyncAddTask();
                    addTask.execute();
                }
            }
        });

        task_btn_add_.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkField_task()) {
                    asyncAddTask = new asyncAddTask();
                    asyncAddTask.execute();
                }
            }
        });

        task_btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkField_task()) {
                    if (task_selectedId != -1) {
                        asyncUpdateTask = new asyncCallUpdateTask();
                        asyncUpdateTask.execute();
                    } else {
                        final MessageBox messageBox = new MessageBox(ActivityManagementTaskes.this, "شما در حال بروزرسانی این گروه خدمات می باشید، در صورت تمایل دکمه قبول را بزنید .");
                        messageBox.show();
                        messageBox.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialogInterface) {
                                if (messageBox.pressAcceptButton()) {
                                    updateTaskGroup = new asyncUpdateTaskGroup();
                                    updateTaskGroup.execute();
                                }
                            }
                        });
                    }
                }
            }
        });

        task_btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (task_selectedId != -1) {
                    asyncDeleteTask = new asyncCallDeleteTask();
                    asyncDeleteTask.execute();
                } else {
                    final MessageBox messageBox =
                            new MessageBox(ActivityManagementTaskes.this
                                    , "شما در حال حذف این گروه خدمات می باشید، در صورت تمایل دکمه قبول را بزنید .");
                    messageBox.show();
                    messageBox.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                            if (messageBox.pressAcceptButton()) {
                                deleteTaskGroup = new asyncDeleteTaskGroup();
                                deleteTaskGroup.execute();
                            }
                        }
                    });
                }
            }
        });

        task_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                lockTaskForm(position);
            }
        });

        taskGroup_ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                lockTaskGroupForm(position);
            }
        });


//        task_spinner_taskGroup.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
//
//            }
//        });
    }


    private void showNext() {
        mtoolbar.setVisibility(View.GONE);
        mViewFlipper.setInAnimation(getBaseContext(), R.anim.slide_in_from_left);
        mViewFlipper.setOutAnimation(getBaseContext(), R.anim.slide_out_to_right);
        mViewFlipper.showNext();
    }

    private void showPrevious() {
        mtoolbar.setVisibility(View.VISIBLE);
        mViewFlipper.setInAnimation(getBaseContext(), R.anim.slide_in_from_right);
        mViewFlipper.setOutAnimation(getBaseContext(), R.anim.slide_out_to_left);
        mViewFlipper.showPrevious();
    }

    private class asyncCallGetTaskGroups extends AsyncTask<String, Void, Void> {
        String msg = null;
        ProgressDialog dialog;
        ArrayList<TaskGroup> taskGroups = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(ActivityManagementTaskes.this, "", "در حال دریافت اطلاعات ...");
            dialog.getWindow().setGravity(Gravity.END);
            dialog.setCancelable(true);
            taskGroup_btn_add.setClickable(false);
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                taskGroups = WebService.invokeGetTaskGroupsWS(G.UserInfo.getUserName(), G.UserInfo.getPassword(), G.officeId);
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
                if (taskGroups != null && taskGroups.size() != 0) {
                    for (TaskGroup tg : taskGroups)
                        taskGroup_adapter.add(tg);

                }
                dialog.dismiss();
            }
            taskGroup_btn_add.setClickable(true);
        }
    }

    private class asyncAddTaskGroup extends AsyncTask<String, Void, Void> {
        String msg = null;
        ProgressDialog dialog;
        String taskGroupName;
        int taskGroupId = 0;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            taskGroupName = taskGroup_name.getText().toString().trim();
            dialog = ProgressDialog.show(ActivityManagementTaskes.this, "", "در حال ثبت گروه خدمات ...");
            dialog.getWindow().setGravity(Gravity.END);
            dialog.setCancelable(true);
            taskGroup_btn_add.setClickable(false);
            taskGroup_ListView.setEnabled(false);
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                taskGroupId = WebService.invokeAddTaskGroupWS(G.UserInfo.getUserName(), G.UserInfo.getPassword()
                        , G.officeId, taskGroupName);
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
                if (taskGroupId != 0) {

                    TaskGroup taskGroup = new TaskGroup();
                    taskGroup.setId(taskGroupId);
                    taskGroup.setOfficeId(G.officeId);
                    taskGroup.setName(taskGroupName);
                    taskGroup_adapter.add(taskGroup);

                    showNext();

                    mViewFlipper.setDisplayedChild(1);
                    task_btn_add.setVisibility(View.VISIBLE);

                    task_spinner_taskGroup.setAdapter(taskGroup_adapter);
                    task_spinner_taskGroup.setSelection(taskGroup_adapter.getCount() - 1);
                    task_spinner_taskGroup.setEnabled(false);

                }
                dialog.dismiss();
            }
            taskGroup_btn_add.setClickable(true);
            taskGroup_ListView.setEnabled(true);
        }
    }

    private class asyncAddTask extends AsyncTask<String, Void, Void> {
        String msg = null;
        ProgressDialog dialog;
        String taskName;
        int price;
        int taskGroupId;
        int taskId;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            taskName = task_name.getText().toString().trim();
            String str = task_price.getText().toString().trim();
            String str2 = Util.getNumber(str);
            price = Integer.valueOf(str2);
            taskGroupId = ((TaskGroup) task_spinner_taskGroup.getSelectedItem()).getId();
            dialog = ProgressDialog.show(ActivityManagementTaskes.this, "", "در حال ثبت زیر گروه خدمات ...");
            dialog.getWindow().setGravity(Gravity.END);
            dialog.setCancelable(true);
            task_btn_add.setClickable(false);
            task_btn_delete.setClickable(false);
            task_btn_edit.setClickable(false);
            task_listview.setEnabled(false);
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                taskId = WebService.invokeAddTaskWS(G.UserInfo.getUserName(), G.UserInfo.getPassword()
                        , G.officeId, taskName, taskGroupId, price);
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
                if (taskId != 0) {

                    Task task = new Task();
                    task.setId(taskId);
                    task.setGroupId(taskGroupId);
                    task.setOfficeId(G.officeId);
                    task.setPrice(price);
                    task.setName(taskName);
                    task_adapter.add(task);
                    Toast.makeText(ActivityManagementTaskes.this, "ثبت زیر گروه خدمات با موفقیت انجام شد .", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }
            task_btn_add.setClickable(true);
            task_btn_delete.setClickable(true);
            task_btn_edit.setClickable(true);
            task_listview.setEnabled(true);
        }
    }

    private class asyncCallGetTask extends AsyncTask<String, Void, Void> {
        String msg = null;
        ProgressDialog dialog;
        int taskGroupId;
        ArrayList<Task> taskes = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            taskGroupId = ((TaskGroup) task_spinner_taskGroup.getSelectedItem()).getId();
            dialog = ProgressDialog.show(ActivityManagementTaskes.this, "", "در حال دریافت اطلاعات ...");
            dialog.getWindow().setGravity(Gravity.END);
            dialog.setCancelable(true);
            task_btn_add.setClickable(false);
            task_btn_delete.setClickable(false);
            task_btn_edit.setClickable(false);
            task_listview.setEnabled(false);
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                taskes = WebService.invokeGetTaskWS(G.UserInfo.getUserName(), G.UserInfo.getPassword()
                        , G.officeId, taskGroupId);
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
                    for (Task t : taskes)
                        task_adapter.add(t);
                }
                dialog.dismiss();
            }
            task_btn_add.setClickable(true);
            task_btn_delete.setClickable(true);
            task_btn_edit.setClickable(true);
            task_listview.setEnabled(true);
        }
    }

    private class asyncUpdateTaskGroup extends AsyncTask<String, Void, Void> {
        String msg = null;
        ProgressDialog dialog;
        int taskGroupId;
        String taskGroupName;
        String result = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            taskGroupId = taskGroup_adapter.getItem(taskGroup_selectedId).getId();
            taskGroupName = taskGroup_name.getText().toString().trim();
            dialog = ProgressDialog.show(ActivityManagementTaskes.this, "", "در حال بروز رسانی گروه خدمات ...");
            dialog.getWindow().setGravity(Gravity.END);
            dialog.setCancelable(true);
            task_btn_add.setClickable(false);
            task_btn_delete.setClickable(false);
            task_btn_edit.setClickable(false);
            task_listview.setEnabled(false);
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                result = WebService.invokeUpdateTaskGroupWS(G.UserInfo.getUserName(), G.UserInfo.getPassword()
                        , G.officeId, taskGroupId, taskGroupName);
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
                if (result.toUpperCase().equals("OK")) {
                    taskGroup_adapter.getItem(taskGroup_selectedId).setName(taskGroupName);
                    Toast.makeText(ActivityManagementTaskes.this, "عملیات بروز رسانی با موفقیت انجام شد .", Toast.LENGTH_SHORT).show();
                    task_btnBack.performClick();
                } else {
                    new MessageBox(ActivityManagementTaskes.this, result).show();
                }
                dialog.dismiss();
            }
            task_btn_add.setClickable(true);
            task_btn_delete.setClickable(true);
            task_btn_edit.setClickable(true);
            task_listview.setEnabled(true);
        }
    }

    private class asyncDeleteTaskGroup extends AsyncTask<String, Void, Void> {
        String msg = null;
        ProgressDialog dialog;
        int taskGroupId;
        String result = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            taskGroupId = taskGroup_adapter.getItem(taskGroup_selectedId).getId();
            dialog = ProgressDialog.show(ActivityManagementTaskes.this, "", "در حال حذف گروه خدمات ...");
            dialog.getWindow().setGravity(Gravity.END);
            dialog.setCancelable(true);
            task_btn_add.setClickable(false);
            task_btn_delete.setClickable(false);
            task_btn_edit.setClickable(false);
            task_listview.setEnabled(false);
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                result = WebService.invokeDeleteTaskGroupWS(G.UserInfo.getUserName(), G.UserInfo.getPassword()
                        , G.officeId, taskGroupId);
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
                if (result.equals("OK")) {
                    taskGroup_adapter.remove(taskGroup_adapter.getItem(taskGroup_selectedId));
                    Toast.makeText(ActivityManagementTaskes.this, "عملیات حذف با موفقیت انجام شد .", Toast.LENGTH_SHORT).show();
                    task_btnBack.performClick();
                } else {
                    new MessageBox(ActivityManagementTaskes.this, result).show();
                }
                dialog.dismiss();
            }
            task_btn_add.setClickable(true);
            task_btn_delete.setClickable(true);
            task_btn_edit.setClickable(true);
            task_listview.setEnabled(true);
        }
    }

    private class asyncCallUpdateTask extends AsyncTask<String, Void, Void> {
        String msg = null;
        ProgressDialog dialog;
        String taskName;
        int taskPrice;
        int taskId;
        String result_update_taskName;
        String result_update_taskPrice;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            taskId = task_adapter.getItem(task_selectedId).getId();
            taskName = task_name.getText().toString().trim();
            taskPrice = Integer.valueOf(Util.getNumber(task_price.getText().toString().trim()));
            dialog = ProgressDialog.show(ActivityManagementTaskes.this, "", "در حال بروزرسانی زیر گروه خدمات ...");
            dialog.getWindow().setGravity(Gravity.END);
            dialog.setCancelable(true);
            task_btn_add.setClickable(false);
            task_btn_delete.setClickable(false);
            task_btn_edit.setClickable(false);
            task_listview.setEnabled(false);
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {

                result_update_taskName = WebService.invokeUpdateTaskNameWS(G.UserInfo.getUserName(), G.UserInfo.getPassword()
                        , G.officeId, taskId, taskName);

                result_update_taskPrice = WebService.invokeUpdateTaskPriceWS(G.UserInfo.getUserName(), G.UserInfo.getPassword()
                        , G.officeId, taskId, taskPrice);

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
                if (result_update_taskName.toUpperCase().equals("OK")) {
                    task_adapter.getItem(task_selectedId).setName(taskName);
                    Toast.makeText(ActivityManagementTaskes.this, "عملیات بروزرسانی نام زیر گروه خدمات با موفقیت انجام شد.", Toast.LENGTH_SHORT).show();
                } else {
                    new MessageBox(ActivityManagementTaskes.this, result_update_taskName).show();
                }

                if (result_update_taskPrice.toUpperCase().equals("OK")) {
                    task_adapter.getItem(task_selectedId).setPrice(taskPrice);
                    Toast.makeText(ActivityManagementTaskes.this, "عملیات بروز رسانی قیمت با موفقیت انجام شد .", Toast.LENGTH_SHORT).show();
                } else {
                    new MessageBox(ActivityManagementTaskes.this, result_update_taskPrice);
                }
                dialog.dismiss();
                refreshCurrentTaskForm();
            }
            task_btn_add.setClickable(true);
            task_btn_delete.setClickable(true);
            task_btn_edit.setClickable(true);
            task_listview.setEnabled(true);
        }
    }

    private class asyncCallDeleteTask extends AsyncTask<String, Void, Void> {
        String msg = null;
        ProgressDialog dialog;
        int taskId;
        String result = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            taskId = task_adapter.getItem(task_selectedId).getId();
            dialog = ProgressDialog.show(ActivityManagementTaskes.this, "", "در حال حذف گروه خدمات ...");
            dialog.getWindow().setGravity(Gravity.END);
            dialog.setCancelable(true);
            task_btn_add.setClickable(false);
            task_btn_delete.setClickable(false);
            task_btn_edit.setClickable(false);
            task_listview.setEnabled(false);
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                result = WebService.invokeDeleteTaskWS(G.UserInfo.getUserName(), G.UserInfo.getPassword()
                        , G.officeId, taskId);
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
                if (result.equals("OK")) {
                    task_adapter.remove(task_adapter.getItem(task_selectedId));
                    Toast.makeText(ActivityManagementTaskes.this, "عملیات حذف با موفقیت انجام شد .", Toast.LENGTH_SHORT).show();
                    refreshCurrentTaskForm();
                } else {
                    new MessageBox(ActivityManagementTaskes.this, result).show();
                }
                dialog.dismiss();

            }
            task_btn_add.setClickable(true);
            task_btn_delete.setClickable(true);
            task_btn_edit.setClickable(true);
            task_listview.setEnabled(true);
        }
    }

}
