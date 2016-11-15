package com.example.doctorsbuilding.nav;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.example.doctorsbuilding.nav.User.User;
import com.example.doctorsbuilding.nav.Util.MessageBox;
import com.example.doctorsbuilding.nav.Util.MoneyTextWatcher;
import com.example.doctorsbuilding.nav.Util.Util;
import com.example.doctorsbuilding.nav.Web.WebService;

import java.util.ArrayList;

public class DialogAddTurn extends Dialog {

    private ViewFlipper viewFlipper;
    private CheckBox myCheckBox;
    private Context context;
    private int turnId;
    private int resevationId = 0;
    private Spinner taskGroupSpinner;
    private Spinner taskSpinner;
    private Button taskBackBtn;
    private Button addTurnBtn;
    private TextView addTurnPatientName;

    private TextView memberChboxTitle;
    private TextView taskPrice;
    private ListView memberListView;
    private EditText memberUsername;
    private EditText memberName;
    private EditText memberFamily;
    private EditText memberMobile;
    private Button memberSearchBtn;

    private TextView nonMemberChboxTitle;
    private ListView nonMemberListView;
    private EditText nonMemberName;
    private EditText nonMemberFamily;
    private EditText nonMemberMobile;
    private Button nonMemberSearchBtn;
    private Button nonMemberInsertBtn;
    private ArrayAdapter<User> user_adapter;
    private ArrayAdapter<Task> task_adapter;
    private ArrayList<User> users = null;
    private ArrayList<TaskGroup> taskGroups;
    private ArrayAdapter<TaskGroup> taskGroup_adapter;
    private int selectedItem = -1;

    asyncCallGetTaskes asyncGetTaskes;
    asyncCallGetTaskGroups asyncGetTaskGroups;
    asyncCallSearchUser searchUserTask;
    asyncCallReserveForUserWS reserveForUserTask;
    asyncCallReserveForMeWS reserveForMeTask;
    asyncCallReserveForGuestWS reserveForGuestTask;


    public DialogAddTurn(Context context, int turnId) {
        super(context);
        this.context = context;
        this.turnId = turnId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.activity_turn);
        initViews();
        viewListener();
    }

    private void initViews() {
        taskPrice = (TextView) findViewById(R.id.addTask_price);
        taskSpinner = (Spinner) findViewById(R.id.addTask_subtask);
//        taskes = new ArrayList<Task>();
        viewFlipper = (ViewFlipper) findViewById(R.id.addTurn_viewSwitcher);
        myCheckBox = (CheckBox) findViewById(R.id.addTurn_chbox);
        taskGroupSpinner = (Spinner) findViewById(R.id.addTask_task);
        taskBackBtn = (Button) findViewById(R.id.addTask_backBtn);
        addTurnBtn = (Button) findViewById(R.id.addTask_addBtn);
        addTurnPatientName = (TextView) findViewById(R.id.addTask_patient_name);

        memberChboxTitle = (TextView) findViewById(R.id.addTurn_chbox_textDr);
        memberListView = (ListView) findViewById(R.id.addTurn_listView);
        memberUsername = (EditText) findViewById(R.id.addTurn_member_username);
        memberName = (EditText) findViewById(R.id.addTurn_member_name);
        memberFamily = (EditText) findViewById(R.id.addTurn_member_family);
        memberMobile = (EditText) findViewById(R.id.addTurn_member_mobile);
        memberSearchBtn = (Button) findViewById(R.id.addTurn_member_btnSearch);

        nonMemberChboxTitle = (TextView) findViewById(R.id.addTurn_chbox_textUser);
        nonMemberInsertBtn = (Button) findViewById(R.id.addTurn_nonMember_insertBtn);
        nonMemberName = (EditText) findViewById(R.id.addTurn_nonMember_name);
        nonMemberFamily = (EditText) findViewById(R.id.addTurn_nonMember_family);
        nonMemberMobile = (EditText) findViewById(R.id.addTurn_nonMember_mobile);

        task_adapter = new ArrayAdapter<Task>(context, R.layout.spinner_item);
        taskGroup_adapter = new ArrayAdapter<TaskGroup>(context, R.layout.spinner_item);
        taskSpinner.setAdapter(task_adapter);
        taskGroupSpinner.setAdapter(taskGroup_adapter);

        if (G.UserInfo.getRole() == UserType.User.ordinal()) {
            taskBackBtn.setVisibility(View.INVISIBLE);
            memberChboxTitle.setVisibility(View.GONE);
            nonMemberChboxTitle.setVisibility(View.VISIBLE);
            nonMemberName.setText(G.UserInfo.getFirstName());
            nonMemberFamily.setText(G.UserInfo.getLastName());
            nonMemberMobile.setText(G.UserInfo.getPhone());
            nonMemberName.setEnabled(false);
            nonMemberFamily.setEnabled(false);
            nonMemberMobile.setEnabled(false);
            viewFlipper.setDisplayedChild(1);

        }

    }

    private void stopAllTaskes() {

        if (asyncGetTaskes != null) {
            asyncGetTaskes.cancel(true);
            asyncGetTaskes = null;
        }

        if (searchUserTask != null) {
            searchUserTask.cancel(true);
            searchUserTask = null;
        }

        if (asyncGetTaskGroups != null) {
            asyncGetTaskGroups.cancel(true);
            asyncGetTaskGroups = null;
        }

        if (reserveForUserTask != null) {
            reserveForUserTask.cancel(true);
            reserveForUserTask = null;
        }

        if (reserveForMeTask != null) {
            reserveForMeTask.cancel(true);
            reserveForMeTask = null;
        }

        if (reserveForGuestTask != null) {
            reserveForGuestTask.cancel(true);
            reserveForGuestTask = null;
        }

    }

    @Override
    public void dismiss() {
        super.dismiss();
        stopAllTaskes();
    }

    private void viewListener() {
        myCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {

                stopAllTaskes();

                if (G.UserInfo.getRole() == UserType.Dr.ordinal() || G.UserInfo.getRole() == UserType.secretary.ordinal()) {
                    if (checked) {
                        taskBackBtn.setVisibility(View.INVISIBLE);
                        viewFlipper.setDisplayedChild(0);
                    } else {
                        taskBackBtn.setVisibility(View.INVISIBLE);
                        viewFlipper.setDisplayedChild(1);
                    }
                } else if (G.UserInfo.getRole() == UserType.User.ordinal()) {
                    if (checked) {
                        taskBackBtn.setVisibility(View.INVISIBLE);
                        viewFlipper.setDisplayedChild(1);
                        nonMemberName.setText(G.UserInfo.getFirstName());
                        nonMemberFamily.setText(G.UserInfo.getLastName());
                        nonMemberMobile.setText(G.UserInfo.getPhone());
                        nonMemberName.setEnabled(false);
                        nonMemberFamily.setEnabled(false);
                        nonMemberMobile.setEnabled(false);
                    } else {
                        taskBackBtn.setVisibility(View.INVISIBLE);
                        viewFlipper.setDisplayedChild(1);
                        nonMemberName.setEnabled(true);
                        nonMemberFamily.setEnabled(true);
                        nonMemberMobile.setEnabled(true);
                        nonMemberName.setText("");
                        nonMemberFamily.setText("");
                        nonMemberMobile.setText("");
                    }
                }
            }
        });
        memberSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchUserTask = new asyncCallSearchUser();
                searchUserTask.execute();
            }
        });

//        taskGroupSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
////                ((TextView) parent.getChildAt(0)).setTextColor(context.getResources().getColor(R.color.textColor));
//            }
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });


        taskBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                stopAllTaskes();

                if (myCheckBox.isChecked()) {
                    if (G.UserInfo.getRole() == UserType.Dr.ordinal() || G.UserInfo.getRole() == UserType.secretary.ordinal()) {
                        taskBackBtn.setVisibility(View.INVISIBLE);
                        viewFlipper.setDisplayedChild(0);
                    } else {
                        taskBackBtn.setVisibility(View.INVISIBLE);
                        viewFlipper.setDisplayedChild(1);
                    }
                } else {
                    if (G.UserInfo.getRole() == UserType.Dr.ordinal() || G.UserInfo.getRole() == UserType.secretary.ordinal()) {
                        taskBackBtn.setVisibility(View.INVISIBLE);
                        viewFlipper.setDisplayedChild(1);
                    } else {
                        taskBackBtn.setVisibility(View.INVISIBLE);
                        viewFlipper.setDisplayedChild(1);
                    }
                }

            }
        });

        addTurnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addTurnBtn.setClickable(false);
                myCheckBox.setClickable(false);
                taskBackBtn.setClickable(false);
                if (myCheckBox.isChecked()) {
                    if (G.UserInfo.getRole() == UserType.Dr.ordinal() || G.UserInfo.getRole() == UserType.secretary.ordinal()) {
                        if (reserveForUserTask == null) {
                            reserveForUserTask = new asyncCallReserveForUserWS();
                            reserveForUserTask.execute(users.get(selectedItem).getUserName());
                        }
                    } else {
                        if (reserveForMeTask == null) {
                            reserveForMeTask = new asyncCallReserveForMeWS();
                            reserveForMeTask.execute();
                        }
                    }
                } else {
                    if (G.UserInfo.getRole() == UserType.Dr.ordinal() || G.UserInfo.getRole() == UserType.secretary.ordinal()) {
                        if (reserveForGuestTask == null) {
                            reserveForGuestTask = new asyncCallReserveForGuestWS();
                            reserveForGuestTask.execute(nonMemberName.getText().toString().trim()
                                    , nonMemberFamily.getText().toString().trim(), nonMemberMobile.getText().toString().trim());
                        }
                    } else {
                        if (reserveForGuestTask == null) {
                            reserveForGuestTask = new asyncCallReserveForGuestWS();
                            reserveForGuestTask.execute(nonMemberName.getText().toString().trim()
                                    , nonMemberFamily.getText().toString().trim(), nonMemberMobile.getText().toString().trim());
                        }
                    }
                }
            }
        });


        nonMemberInsertBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkField()) {
                    if (G.UserInfo.getRole() == UserType.Dr.ordinal() || G.UserInfo.getRole() == UserType.secretary.ordinal()) {
                        addTurnPatientName.setText(nonMemberName.getText().toString().trim().concat(" " + nonMemberFamily.getText().toString().trim()));
                        if (asyncGetTaskGroups == null) {
                            asyncGetTaskGroups = new asyncCallGetTaskGroups();
                            asyncGetTaskGroups.execute();
                        }
                    } else {
                        if (myCheckBox.isChecked()) {

                            addTurnPatientName.setText(nonMemberName.getText().toString().trim().concat(" " + nonMemberFamily.getText().toString().trim()));
                            if (asyncGetTaskGroups == null) {
                                asyncGetTaskGroups = new asyncCallGetTaskGroups();
                                asyncGetTaskGroups.execute();
                            }

                        } else {

                            addTurnPatientName.setText(nonMemberName.getText().toString().trim().concat(" " + nonMemberFamily.getText().toString().trim()));
                            if (asyncGetTaskGroups == null) {
                                asyncGetTaskGroups = new asyncCallGetTaskGroups();
                                asyncGetTaskGroups.execute();
                            }
//                            viewFlipper.setDisplayedChild(2);
//                            taskBackBtn.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
        });

        taskGroupSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (asyncGetTaskes == null) {
                    asyncGetTaskes = new asyncCallGetTaskes();
                    asyncGetTaskes.execute();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        taskSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                taskPrice.setText(Util.getCurrency(((Task) taskSpinner.getSelectedItem()).getPrice()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private boolean checkField() {
        if (nonMemberName.getText().toString().trim().equals("")) {
            new MessageBox(context, "لطفا نام بیمار را وارد نمایید .").show();
            return false;
        }
        if (nonMemberFamily.getText().toString().trim().equals("")) {
            new MessageBox(context, "لطفا نام خانوادگی بیمار را وارد نمایید .").show();
            return false;
        }
        if (nonMemberMobile.getText().toString().trim().equals("")) {
            new MessageBox(context, "لطفا شماره تلفن همراه بیمار را وارد نمایید .").show();
            return false;
        }
        return true;
    }

    public int getResevationId() {
        return resevationId;
    }


    private class asyncCallSearchUser extends AsyncTask<String, Void, Void> {
        User user = null;
        String msg = null;
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            memberSearchBtn.setClickable(false);
            dialog = ProgressDialog.show(context, "", "در حال دریافت اطلاعات ...");
            dialog.setCancelable(true);
            dialog.getWindow().setGravity(Gravity.END);
            user = new User();
            user.setUserName(memberUsername.getText().toString().trim());
            user.setFirstName(memberName.getText().toString().trim());
            user.setLastName(memberFamily.getText().toString().trim());
            user.setPhone(memberMobile.getText().toString());
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
                memberSearchBtn.setClickable(true);
                dialog.dismiss();
                new MessageBox(context, msg).show();
            } else {
                if (users != null && users.size() != 0) {
                    ArrayList<String> userInfo = null;
                    final ArrayList<ArrayList<String>> userha = new ArrayList<ArrayList<String>>();
                    for (User user : users) {
                        userInfo = new ArrayList<String>();
                        userInfo.add(user.getFirstName() + " " + user.getLastName());
                        userInfo.add(user.getPhone());
                        userha.add(userInfo);
                    }
                    memberListView.setAdapter(new CustomReservationListAdapter(getContext(), userha, turnId));
                    memberListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View container, int position, long id) {
                            addTurnPatientName.setText(userha.get(position).get(0));
                            selectedItem = position;
                            if (asyncGetTaskGroups == null) {
                                asyncGetTaskGroups = new asyncCallGetTaskGroups();
                                asyncGetTaskGroups.execute();
                            }
                        }
                    });
                    memberSearchBtn.setClickable(true);
                    dialog.dismiss();
                } else {
                    memberSearchBtn.setClickable(true);
                    dialog.dismiss();
                    memberListView.setAdapter(new CustomReservationListAdapter(getContext()
                            , new ArrayList<ArrayList<String>>(), turnId));
                    Toast.makeText(context, "بیماری با این مشخصات یافت نشده است .", Toast.LENGTH_SHORT).show();
                }
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            memberSearchBtn.setClickable(true);
        }
    }

    private class asyncCallReserveForUserWS extends AsyncTask<String, Void, Void> {

        int result = 0;
        Reservation reservation = null;
        String msg = null;
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(context, "", "در حال رزرو نوبت ...");
            dialog.setCancelable(true);
            dialog.getWindow().setGravity(Gravity.END);
            reservation = new Reservation();
            reservation.setTurnId(turnId);
            reservation.setFirstReservationId(0);
            reservation.setTaskId(((Task) taskSpinner.getSelectedItem()).getId());
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
            if (msg != null) {
                dialog.dismiss();
                new MessageBox(context, msg).show();
            } else {
                if (result > 0) {
                    resevationId = result;
                    dialog.dismiss();
                    Toast.makeText(context, "ثبت نوبت با موفقیت انجام شد .", Toast.LENGTH_SHORT).show();
                    dismiss();
                } else {
                    dialog.dismiss();
                    new MessageBox(context, "ثبت نوبت با مشکل مواجه شد !").show();
                }
            }
        }
    }

    private class asyncCallReserveForGuestWS extends AsyncTask<String, Void, Void> {

        int result = 0;
        Reservation reservation = null;
        String msg = null;
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(context, "", "در حال رزرو نوبت ...");
            dialog.setCancelable(true);
            dialog.getWindow().setGravity(Gravity.END);
            reservation = new Reservation();
            reservation.setTurnId(turnId);
            reservation.setFirstReservationId(0);
//            if (G.UserInfo.getRole() == UserType.User.ordinal()) {
//                reservation.setTaskId(1);
//            } else {
            reservation.setTaskId(((Task) taskSpinner.getSelectedItem()).getId());
//            }
            reservation.setNumberOfTurns(1);
        }

        @Override
        protected Void doInBackground(String... strings) {
            reservation.setPatientFirstName(strings[0]);
            reservation.setPatientLastName(strings[1]);
            reservation.setPatientPhoneNo(strings[2]);
            try {
                result = WebService.invokeReserveForGuestWS(G.UserInfo.getUserName(), G.UserInfo.getPassword(), reservation, G.UserInfo.getCityID());
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
            } else {
                if (result > 0) {
                    resevationId = result;
                    dialog.dismiss();
                    Toast.makeText(context, "ثبت نوبت با موفقیت انجام شد .", Toast.LENGTH_SHORT).show();
                    dismiss();
                } else {
                    dialog.dismiss();
                    new MessageBox(context, "ثبت نوبت با مشکل مواجه شد !").show();
                }
            }
        }
    }

    private class asyncCallGetReservationByTurnIdWS extends AsyncTask<String, Void, Void> {
        Reservation reservation = null;
        ArrayList<Reservation> reservations;
        String msg = null;
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(context, "", "در حال دریافت اطلاعات ...");
            dialog.setCancelable(true);
            dialog.getWindow().setGravity(Gravity.END);
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                reservations = WebService.invokeGetReservationByTurnIdWS(G.UserInfo.getUserName(), G.UserInfo.getPassword(), G.officeId, turnId);
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
            } else {
                if (reservations != null) {
                    ArrayList<String> userInfo = null;
                    ArrayList<ArrayList<String>> users = new ArrayList<ArrayList<String>>();
                    for (Reservation res : reservations) {
                        userInfo = new ArrayList<String>();
                        userInfo.add(res.getPatientFirstName() + " " + res.getPatientLastName());
                        userInfo.add(res.getPatientPhoneNo());
                        users.add(userInfo);
                    }
                    memberListView.setAdapter(new CustomReservationListAdapter(getContext(), users, turnId));
                }
                dialog.dismiss();
            }
        }
    }


    private class asyncCallGetTaskGroups extends AsyncTask<String, Void, Void> {
        String msg = null;
        ProgressDialog dialog;
        ArrayList<TaskGroup> taskGroups = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(context, "", "در حال دریافت اطلاعات ...");
            dialog.setCancelable(true);
            dialog.getWindow().setGravity(Gravity.END);
            addTurnBtn.setClickable(false);
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
                new MessageBox(context, msg).show();
            } else {
                if (taskGroups != null && taskGroups.size() != 0) {

                    taskBackBtn.setVisibility(View.VISIBLE);
                    viewFlipper.setDisplayedChild(2);
                    taskGroup_adapter.addAll(taskGroups);

                }else {
                    new MessageBox(context, "خدماتی برای مطب شما ثبت نشده است، لطفا از منوی خدمات اقدام به ثبت نمایید .").show();
                }
                dialog.dismiss();
            }
        }
    }

    private class asyncCallGetTaskes extends AsyncTask<String, Void, Void> {
        String msg = null;
        int taskGroupId;
        ArrayList<Task> taskes = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            taskGroupId = ((TaskGroup) taskGroupSpinner.getSelectedItem()).getId();
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                taskes = WebService.invokeGetTaskWS(G.UserInfo.getUserName(), G.UserInfo.getPassword(), G.officeId, taskGroupId);
            } catch (PException ex) {
                msg = ex.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (msg != null) {
                new MessageBox(context, msg).show();
            } else {
                if (taskes != null && taskes.size() != 0) {
                    task_adapter.addAll(taskes);
                    addTurnBtn.setClickable(true);
                }else {
                    new MessageBox(context, "لطفا از منوی خدمات اقدام به ثبت زیرگروه خدمات نمایید .").show();
                }
            }
        }
    }

    private class asyncCallReserveForMeWS extends AsyncTask<String, Void, Void> {

        int result = 0;
        Reservation reservation = null;
        String msg = null;
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(context, "", "در حال رزرو نوبت ...");
            dialog.setCancelable(true);
            dialog.getWindow().setGravity(Gravity.END);
            reservation = new Reservation();
            reservation.setTurnId(turnId);
            reservation.setFirstReservationId(0);
//            if (G.UserInfo.getRole() == UserType.User.ordinal()) {
//                reservation.setTaskId(1);
//            } else {
            reservation.setTaskId(((Task) taskSpinner.getSelectedItem()).getId());
//            }
            reservation.setNumberOfTurns(1);
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                result = WebService.invokeReserveForMeWS(G.UserInfo.getUserName(), G.UserInfo.getPassword(), reservation);
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
            } else {
                if (result > 0) {
                    resevationId = result;
                    dialog.dismiss();
                    Toast.makeText(context, "ثبت نوبت با موفقیت انجام شد .", Toast.LENGTH_SHORT).show();
                    dismiss();
                } else {
                    dialog.dismiss();
                    new MessageBox(context, "ثبت نوبت با مشکل مواجه شد !").show();
                }
            }
        }
    }
}
