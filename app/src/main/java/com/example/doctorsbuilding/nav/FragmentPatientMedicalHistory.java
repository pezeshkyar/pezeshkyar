package com.example.doctorsbuilding.nav;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.doctorsbuilding.nav.Question.Question;
import com.example.doctorsbuilding.nav.Question.Reply;
import com.example.doctorsbuilding.nav.Question.ReplyType;
import com.example.doctorsbuilding.nav.Util.MessageBox;
import com.example.doctorsbuilding.nav.Web.WebService;

import java.util.ArrayList;

/**
 * Created by hossein on 11/13/2016.
 */
public class FragmentPatientMedicalHistory extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String PATIENT_USER_NAME = "patientUserName";
    private String patientUserName = null;
    private ProgressDialog loadingDialog;

    Button insertBtn;
    LinearLayout layout;
    ArrayList<Question> questions = null;
    asyncGetQuestion task_getQuestion;
    asyncInsertCartex task_insertCartex;
    asyncGetReply task_getReply;

    public static FragmentPatientMedicalHistory newInstance(int sectionNumber, String patientUsername) {
        FragmentPatientMedicalHistory fragment = new FragmentPatientMedicalHistory();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        args.putString(PATIENT_USER_NAME, patientUsername);
        fragment.setArguments(args);
        return fragment;
    }

    public FragmentPatientMedicalHistory() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.patient_medical_history, container, false);
        layout = (LinearLayout) rootView.findViewById(R.id.cartex_layout);
        insertBtn = new Button(getActivity());
        insertBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                task_insertCartex = new asyncInsertCartex();
                task_insertCartex.execute();
            }
        });
        Bundle bundle = this.getArguments();
        if (bundle != null)
            patientUserName = bundle.getString("patientUserName");

        if (patientUserName != null) {
            task_getQuestion = new asyncGetQuestion();
            task_getQuestion.execute();
        }

        return rootView;


    }

    private class asyncGetReply extends AsyncTask<String, Void, Void> {
        String msg = null;
        ArrayList<Reply> replies = new ArrayList<Reply>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                replies = WebService.invokeGetReplyWS(G.UserInfo.getUserName(), G.UserInfo.getPassword(), G.officeId, patientUserName);
            } catch (PException ex) {
                msg = ex.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (msg != null) {
                loadingDialog.dismiss();
                new MessageBox(getActivity(), msg).show();
            } else {
                if (replies != null && replies.size() > 0) {
                    if (questions != null && questions.size() > 0) {
                        fillCartex(replies);
                    }
                }
                loadingDialog.dismiss();
            }
        }
    }

    private void fillCartex(ArrayList<Reply> replies) {

        ViewGroup group = layout;
        for (int i = 0; i < group.getChildCount(); i++) {

            View view = group.getChildAt(i);
            if (view instanceof EditText) {
                for (Reply r : replies) {
                    if (view.getId() == r.getQuestionId()) {
                        ((EditText) view).setText(r.getReply());
                        break;
                    }
                }

            } else if (view instanceof CheckBox) {
                for (Reply r : replies) {
                    if (view.getId() == r.getQuestionId()) {
                        ((CheckBox) view).setChecked(r.getReply().equals("1"));
                        break;
                    }
                }
            }
        }
    }

    private class asyncGetQuestion extends AsyncTask<String, Void, Void> {
        String msg = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingDialog = ProgressDialog.show(getActivity(), "", "در حال دریافت اطلاعات ...");
            loadingDialog.getWindow().setGravity(Gravity.END);
            loadingDialog.setCancelable(true);

        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                questions = WebService.invokeGetQuestionsWS(G.UserInfo.getUserName(), G.UserInfo.getPassword(), G.officeId);
            } catch (PException ex) {
                msg = ex.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (msg != null) {
                loadingDialog.dismiss();
                new MessageBox(getActivity(), msg).show();
            } else {
                if (questions != null && questions.size() > 0) {
                    createCartex(questions);
                    task_getReply = new asyncGetReply();
                    task_getReply.execute();
                } else {
                    loadingDialog.dismiss();
                }
            }
        }
    }

    private void createCartex(ArrayList<Question> questions) {

        layout.setGravity(Gravity.END);

        int id = 0;
        for (Question q : questions) {

            if (q.getReplyType() == ReplyType.selection.ordinal()) {

                CheckBox chbox = new CheckBox(getActivity());
                chbox.setId(q.getId());
                chbox.setText(q.getLabel());
                chbox.setTypeface(G.getNormalFont());
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 10, 0, 0);
                chbox.setLayoutParams(params);
                chbox.setGravity(Gravity.END);
                layout.addView(chbox);

            } else {
                TextView mTV = new TextView(getActivity());
                mTV.setText(q.getLabel());
                mTV.setTypeface(G.getNormalFont());
                mTV.setTextColor(Color.BLACK);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 10, 0, 0);
                mTV.setLayoutParams(params);
                layout.addView(mTV);

                EditText mTxt = new EditText(getActivity());
                mTxt.setId(q.getId());
                mTxt.setHint(q.getLabel());
                mTxt.setTypeface(G.getNormalFont());
                LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params1.setMargins(0, 10, 0, 0);
                mTxt.setLayoutParams(params1);
                mTxt.setPadding(10, 10, 10, 10);
                mTxt.setBackgroundResource(R.drawable.my_edit_text);
                layout.addView(mTxt);
            }
        }

        insertBtn.setText("ثبت اطلاعات");
        insertBtn.setTypeface(G.getNormalFont());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 20, 0, 20);
        insertBtn.setBackgroundResource(R.drawable.my_button);
        insertBtn.setTextColor(Color.WHITE);
        insertBtn.setLayoutParams(params);
        layout.addView(insertBtn);

        View mV = new View(getActivity());
        mV.setMinimumHeight(30);
        layout.addView(mV);

    }

    private class asyncInsertCartex extends AsyncTask<String, Void, Void> {
        String msg = null;
        ProgressDialog dialog;
        boolean result = false;
        int[] questionIds;
        String[] answers;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(getActivity(), "", "در حال ثبت اطلاعات ...");
            dialog.getWindow().setGravity(Gravity.END);
            dialog.setCancelable(true);
            insertBtn.setClickable(false);

            ViewGroup group = layout;
            questionIds = new int[questions.size()];
            answers = new String[questions.size()];

            int k = 0;
            for (int i = 0; i < group.getChildCount(); i++) {

                View view = group.getChildAt(i);
                if (view instanceof EditText) {
                    questionIds[k] = view.getId();
                    answers[k] = ((EditText) view).getText().toString().trim();
                    k += 1;

                } else if (view instanceof CheckBox) {
                    questionIds[k] = view.getId();
                    answers[k] = ((CheckBox) view).isChecked() ? "1" : "0";
                    k += 1;
                }
            }
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                if (G.UserInfo.getRole() == UserType.User.ordinal()) {
                    result = WebService.invokeSetReplyBatchWS(G.UserInfo.getUserName(), G.UserInfo.getPassword(), G.officeId,
                            questionIds, answers);
                } else {
                    result = WebService.invokeSetReplyBatchForUserWS(G.UserInfo.getUserName(), G.UserInfo.getPassword(), G.officeId,
                            patientUserName, questionIds, answers);
                }
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
                new MessageBox(getActivity(), msg).show();
            } else {
                dialog.dismiss();
                if (result) {
                    Toast.makeText(getActivity(), "ثبت اطلاعات با موفقیت انجام شد .", Toast.LENGTH_SHORT).show();
                } else {
                    new MessageBox(getActivity(), "ثبت اطلاعات با مشکل مواجه شده است .").show();
                }
            }
            insertBtn.setClickable(true);
        }
    }
}
