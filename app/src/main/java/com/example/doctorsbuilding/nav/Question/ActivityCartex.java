package com.example.doctorsbuilding.nav.Question;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.doctorsbuilding.nav.G;
import com.example.doctorsbuilding.nav.PException;
import com.example.doctorsbuilding.nav.R;
import com.example.doctorsbuilding.nav.Util.MessageBox;
import com.example.doctorsbuilding.nav.Web.WebService;

import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by hossein on 11/12/2016.
 */
public class ActivityCartex extends AppCompatActivity {

    Button backBtn;
    Button insertBtn;
    TextView pageTitle;
    LinearLayout layout;
    ArrayList<Question> questions = null;
    asyncGetQuestion task_getQuestion;
    asyncInsertCartex task_insertCartex;
    asyncGetReply task_getReply;
    ProgressDialog loadingDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_cartex);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        initViews();
        eventListener();
        task_getQuestion = new asyncGetQuestion();
        task_getQuestion.execute();
    }

    private void initViews() {
        backBtn = (Button) findViewById(R.id.toolbar_backBtn);
        pageTitle = (TextView) findViewById(R.id.toolbar_title);
        pageTitle.setText("سابقه پزشکی");
        layout = (LinearLayout) findViewById(R.id.cartex_layout);
        insertBtn = new Button(ActivityCartex.this);

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    private void eventListener() {
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        insertBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                task_insertCartex = new asyncInsertCartex();
                task_insertCartex.execute();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (task_getQuestion != null)
            task_getQuestion.cancel(true);
        if (task_insertCartex != null)
            task_insertCartex.cancel(true);
    }

    private void createCartex(ArrayList<Question> questions) {

        layout.setGravity(Gravity.END);
        int id = 0;
        for (Question q : questions) {


            if (q.getReplyType() == ReplyType.selection.ordinal()) {

                CheckBox chbox = new CheckBox(ActivityCartex.this);
                chbox.setId(q.getId());
                chbox.setText(q.getLabel());
                chbox.setTypeface(G.getNormalFont());
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 10, 0, 0);
                chbox.setLayoutParams(params);
                chbox.setGravity(Gravity.END);
                layout.addView(chbox);

            } else {
                EditText mTxt = new EditText(ActivityCartex.this);
                mTxt.setId(q.getId());
                mTxt.setHint(q.getLabel());
                mTxt.setTypeface(G.getNormalFont());
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 10, 0, 0);
                mTxt.setLayoutParams(params);
                mTxt.setPadding(10, 10, 10, 10);
                mTxt.setBackgroundResource(R.drawable.my_edit_text);
                layout.addView(mTxt);
            }
        }

        insertBtn.setText("ثبت اطلاعات");
        insertBtn.setTypeface(G.getNormalFont());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 20, 0, 0);
        insertBtn.setBackgroundResource(R.drawable.my_button);
        insertBtn.setTextColor(Color.WHITE);
        insertBtn.setLayoutParams(params);
        layout.addView(insertBtn);

    }

    private class asyncGetQuestion extends AsyncTask<String, Void, Void> {
        String msg = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingDialog = ProgressDialog.show(ActivityCartex.this, "", "در حال دریافت اطلاعات ...");
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
                new MessageBox(ActivityCartex.this, msg).show();
            } else {
                if (questions != null && questions.size() > 0) {
                    createCartex(questions);
                    task_getReply = new asyncGetReply();
                    task_getReply.execute();
                }else {
                    loadingDialog.dismiss();
                }
            }
        }
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
            dialog = ProgressDialog.show(ActivityCartex.this, "", "در حال ثبت اطلاعات ...");
            dialog.getWindow().setGravity(Gravity.END);
            dialog.setCancelable(true);
            insertBtn.setClickable(false);

            ViewGroup group = layout;
            questionIds = new int[questions.size()];
            answers = new String[questions.size()];

            for (int i = 0; i < group.getChildCount(); i++) {

                View view = group.getChildAt(i);
                if (view instanceof EditText) {
                    questionIds[i] = view.getId();
                    answers[i] = ((EditText) view).getText().toString().trim();

                } else if (view instanceof CheckBox) {
                    questionIds[i] = view.getId();
                    answers[i] = ((CheckBox) view).isChecked() ? "1" : "0";
                }
            }

        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                result = WebService.invokeSetReplyBatchWS(G.UserInfo.getUserName(), G.UserInfo.getPassword(), G.officeId, questionIds, answers);
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
                new MessageBox(ActivityCartex.this, msg).show();
            } else {
                dialog.dismiss();
                if (result) {
                    Toast.makeText(ActivityCartex.this, "ثبت اطلاعات با موفقیت انجام شد .", Toast.LENGTH_SHORT).show();
                } else {
                    new MessageBox(ActivityCartex.this, "ثبت اطلاعات با مشکل مواجه شده است .").show();
                }
            }
            insertBtn.setClickable(true);
        }
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
                replies = WebService.invokeGetReplyWS(G.UserInfo.getUserName(), G.UserInfo.getPassword(), G.officeId, G.UserInfo.getUserName());
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
                new MessageBox(ActivityCartex.this, msg).show();
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
                    if (view.getId() == r.getId()) {
                        ((EditText) view).setText(r.getReply());
                        break;
                    }
                }

            } else if (view instanceof CheckBox) {
                for (Reply r : replies) {
                    if (view.getId() == r.getId()) {
                        ((CheckBox) view).setChecked(r.getReply().equals("1"));
                        break;
                    }
                }
            }
        }
    }
}
