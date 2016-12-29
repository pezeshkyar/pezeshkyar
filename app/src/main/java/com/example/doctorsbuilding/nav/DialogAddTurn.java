package com.example.doctorsbuilding.nav;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import com.example.doctorsbuilding.nav.Dr.Profile.ExpChild;
import com.example.doctorsbuilding.nav.User.User;
import com.example.doctorsbuilding.nav.Util.MessageBox;
import com.example.doctorsbuilding.nav.Util.Util;
import com.example.doctorsbuilding.nav.Web.WebService;

import java.io.IOException;
import java.util.ArrayList;

public class DialogAddTurn extends DialogFragment {

    private ViewFlipper viewFlipper;
    private CheckBox myCheckBox;
    private Context context;
    private Turn turnData;
    private int resevationId = -1;
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
    asyncCallReserveForGuestFromUserWS reserveForGuestFromUserTask;

    private DialogCallback dialogCallback;
    private View rootView;

    public DialogFragment setCallBack(DialogCallback dialogCallback) {
        this.dialogCallback = dialogCallback;
        return this;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        return dialog;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_turn, container, false);
        initViews();
        viewListener();
        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        dialogCallback.getResult(resevationId);
    }

    public static DialogAddTurn newInstance(Context context, Turn turnData) {
        DialogAddTurn frag = new DialogAddTurn();
        frag.setTurnData(turnData);
        frag.setContext(context);
        return frag;
    }

    public void setTurnData(Turn turn) {
        turnData = turn;
    }

    public void setContext(Context ctx) {
        context = ctx;
    }

    private void initViews() {
        taskPrice = (TextView) rootView.findViewById(R.id.addTask_price);
        taskSpinner = (Spinner) rootView.findViewById(R.id.addTask_subtask);
//        taskes = new ArrayList<Task>();
        viewFlipper = (ViewFlipper) rootView.findViewById(R.id.addTurn_viewSwitcher);
        myCheckBox = (CheckBox) rootView.findViewById(R.id.addTurn_chbox);
        taskGroupSpinner = (Spinner) rootView.findViewById(R.id.addTask_task);
        taskBackBtn = (Button) rootView.findViewById(R.id.addTask_backBtn);
        addTurnBtn = (Button) rootView.findViewById(R.id.addTask_addBtn);
        addTurnPatientName = (TextView) rootView.findViewById(R.id.addTask_patient_name);

        memberChboxTitle = (TextView) rootView.findViewById(R.id.addTurn_chbox_textDr);
        memberListView = (ListView) rootView.findViewById(R.id.addTurn_listView);
        memberUsername = (EditText) rootView.findViewById(R.id.addTurn_member_username);
        memberName = (EditText) rootView.findViewById(R.id.addTurn_member_name);
        memberFamily = (EditText) rootView.findViewById(R.id.addTurn_member_family);
        memberMobile = (EditText) rootView.findViewById(R.id.addTurn_member_mobile);
        memberSearchBtn = (Button) rootView.findViewById(R.id.addTurn_member_btnSearch);

        nonMemberChboxTitle = (TextView) rootView.findViewById(R.id.addTurn_chbox_textUser);
        nonMemberInsertBtn = (Button) rootView.findViewById(R.id.addTurn_nonMember_insertBtn);
        nonMemberName = (EditText) rootView.findViewById(R.id.addTurn_nonMember_name);
        nonMemberFamily = (EditText) rootView.findViewById(R.id.addTurn_nonMember_family);
        nonMemberMobile = (EditText) rootView.findViewById(R.id.addTurn_nonMember_mobile);

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
        if (reserveForGuestFromUserTask != null) {
            reserveForGuestFromUserTask.cancel(true);
            reserveForGuestFromUserTask = null;
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
                            G.reservationInfo = getPayInfo();
                            G.reservationInfo.setOwner(UserType.User);
                            Intent intent = new Intent(context, ActivityFactor.class);
                            intent.putExtra("requestCode", UserType.User.ordinal());
                            startActivityForResult(intent, UserType.User.ordinal());
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
                            G.reservationInfo = getPayInfo();
                            G.reservationInfo.setOwner(UserType.Guest);
                            Intent intent = new Intent(context, ActivityFactor.class);
                            intent.putExtra("requestCode", UserType.Guest.ordinal());
                            startActivityForResult(intent, UserType.Guest.ordinal());
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        addTurnBtn.setClickable(true);
        myCheckBox.setClickable(true);
        taskBackBtn.setClickable(true);
        if (resultCode == UserType.User.ordinal()) {
            if (G.UserInfo.getRole() == UserType.User.ordinal()) {

                reserveForMeTask = new asyncCallReserveForMeWS();
                reserveForMeTask.execute();

            }
        }
        if (resultCode == UserType.Guest.ordinal()) {
            if (G.UserInfo.getRole() == UserType.User.ordinal()) {

                reserveForGuestFromUserTask = new asyncCallReserveForGuestFromUserWS();
                reserveForGuestFromUserTask.execute(nonMemberName.getText().toString().trim()
                        , nonMemberFamily.getText().toString().trim(), nonMemberMobile.getText().toString().trim());
            }
        }
    }

    private Reservation getPayInfo() {
        Reservation reserve = getReservation();
        ExpChild child = new ExpChild(turnData);
        reserve.setDate(child.getDate());
        reserve.setTime(child.getTime());
        reserve.setPrice(Integer.valueOf(Util.getNumber(taskPrice.getText().toString())));
        return reserve;
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
                    memberListView.setAdapter(new CustomReservationListAdapter(getContext(), userha, turnData.getId()));
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
                            , new ArrayList<ArrayList<String>>(), turnData.getId()));
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
            dialog = ProgressDialog.show(context, "", "لطفا شکیبا باشید ...");
            dialog.setCancelable(true);
            dialog.getWindow().setGravity(Gravity.END);
            reservation = getReservation();
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
            dialog = ProgressDialog.show(context, "", "لطفا شکیبا باشید ...");
            dialog.setCancelable(true);
            dialog.getWindow().setGravity(Gravity.END);
            reservation = getReservation();
        }

        @Override
        protected Void doInBackground(String... strings) {
            reservation.setPatientFirstName(strings[0]);
            reservation.setPatientLastName(strings[1]);
            reservation.setPatientPhoneNo(strings[2]);
            reservation.setCityId(G.UserInfo.getCityID());
            try {
                result = WebService.invokeReserveForGuestWS(G.UserInfo.getUserName(), G.UserInfo.getPassword(), reservation, G.UserInfo.getCityID());
                G.resNum = -1;
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
                dialog.dismiss();
                if (result > 0) {
                    resevationId = result;
                    Toast.makeText(context, "ثبت نوبت با موفقیت انجام شد .", Toast.LENGTH_SHORT).show();
                    dismiss();
                } else{
                    new MessageBox(context, "ثبت نوبت با مشکل مواجه شد !").show();
                }
            }
        }
    }

    private class asyncCallReserveForGuestFromUserWS extends AsyncTask<String, Void, Void> {

        int result = 0;
        Reservation reservation = null;
        String msg = null;
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(context, "", "لطفا شکیبا باشید ...");
            dialog.setCancelable(true);
            dialog.getWindow().setGravity(Gravity.END);
            reservation = getReservation();
        }

        @Override
        protected Void doInBackground(String... strings) {
            reservation.setPatientFirstName(strings[0]);
            reservation.setPatientLastName(strings[1]);
            reservation.setPatientPhoneNo(strings[2]);
            reservation.setCityId(G.UserInfo.getCityID());
            try {
                result = WebService.invokeReserveForGuestFromUserWS(G.UserInfo.getUserName(), G.UserInfo.getPassword(), reservation, G.resNum);
                G.resNum = -1;
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
                dialog.dismiss();
                if (result > 0) {
                    resevationId = result;
                    Toast.makeText(context, "ثبت نوبت با موفقیت انجام شد .", Toast.LENGTH_SHORT).show();
                    dismiss();
                } else if (result == 0) {
                    new MessageBox(context, "ثبت نوبت با مشکل مواجه شد !").show();
                } else if (result == -1) {
                    new MessageBox(context, "موجودی حساب شما کافی نمی باشد .").show();
                }
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

                } else {
                    new MessageBox(context, "خدماتی برای مطب ثبت نشده است .").show();
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
                } else {
                    new MessageBox(context, "زیر گروه خدمات ثبت نشده است .").show();
                }
            }
        }
    }

    private Reservation getReservation() {

        Reservation reserve = new Reservation();
        reserve.setTurnId(turnData.getId());
        reserve.setFirstReservationId(0);
        reserve.setTaskId(((Task) taskSpinner.getSelectedItem()).getId());
        reserve.setNumberOfTurns(1);
        return reserve;
    }

    private class asyncCallReserveForMeWS extends AsyncTask<String, Void, Void> {

        int result = 0;
        Reservation reservation = null;
        String msg = null;
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(context, "", "لطفا شکیبا باشید ...");
            dialog.setCancelable(true);
            dialog.getWindow().setGravity(Gravity.END);
            reservation = getReservation();
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                result = WebService.invokeReserveForMeWS(G.UserInfo.getUserName(), G.UserInfo.getPassword(), reservation, G.resNum);
                G.resNum = -1;
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
                dialog.dismiss();
                if (result > 0) {
                    resevationId = result;
                    Toast.makeText(context, "ثبت نوبت با موفقیت انجام شد .", Toast.LENGTH_SHORT).show();
                    dismiss();
                } else if (result == 0) {
                    new MessageBox(context, "ثبت نوبت با مشکل مواجه شد !").show();
                } else if (result == -1) {
                    new MessageBox(context, "موجودی حساب شما کافی نمی باشد .").show();
                }
            }
        }
    }

}
