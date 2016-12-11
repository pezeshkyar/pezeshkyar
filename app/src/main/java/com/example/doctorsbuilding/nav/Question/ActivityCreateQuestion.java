package com.example.doctorsbuilding.nav.Question;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
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
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.doctorsbuilding.nav.G;
import com.example.doctorsbuilding.nav.PException;
import com.example.doctorsbuilding.nav.PatientFile;
import com.example.doctorsbuilding.nav.R;
import com.example.doctorsbuilding.nav.Util.MessageBox;
import com.example.doctorsbuilding.nav.Util.TextViewLinkHandler;
import com.example.doctorsbuilding.nav.Web.WebService;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;
import java.util.Map;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by hossein on 11/8/2016.
 */
public class ActivityCreateQuestion extends AppCompatActivity {


    EditText txt_topic;
    TextView txt_help;
    TextView pageTitle;
    LinearLayout layout_help;
    Button btn_insert;
    Button btn_delete;
    ImageButton backBtn;
    RadioButton rb_Selection;
    RadioButton rb_text;
    ListView mListView;
    FrameLayout frm_layout;
    ImageView refresh_lv;
    int selectedPositionItem = -1;

    ArrayAdapter<Question> mAdapter;
    asyncSetQuestion task_setQuestion;
    asyncGetQuestion task_getQuestion;
    asyncDeleteQuestion task_deleteQuestion;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_question);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        initViews();
        eventListeners();

        task_getQuestion = new asyncGetQuestion();
        task_getQuestion.execute();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (task_setQuestion != null)
            task_setQuestion.cancel(true);
        if (task_getQuestion != null)
            task_getQuestion.cancel(true);
        if (task_deleteQuestion != null)
            task_deleteQuestion.cancel(true);
    }

    private void initViews() {
        backBtn = (ImageButton) findViewById(R.id.toolbar_backBtn);
        frm_layout = (FrameLayout) findViewById(R.id.question_listView_highlight);
        refresh_lv = (ImageView) findViewById(R.id.question_refresh);
        pageTitle = (TextView) findViewById(R.id.toolbar_title);
        pageTitle.setText("بانک سوالات");
        txt_topic = (EditText) findViewById(R.id.question_topic);
        txt_help = (TextView) findViewById(R.id.question_help);
        Spanned str = Html.fromHtml("<a href='#'>راهنما</a>");
        txt_help.setMovementMethod(new TextViewLinkHandler() {
            @Override
            public void onLinkClick(String url) {
                if (layout_help.getVisibility() == View.VISIBLE)
                    layout_help.setVisibility(View.GONE);
                else
                    layout_help.setVisibility(View.VISIBLE);
            }
        });
        txt_help.setText(str);
        txt_help.setLinksClickable(true);
        layout_help = (LinearLayout) findViewById(R.id.question_layout_help);
        btn_insert = (Button) findViewById(R.id.question_btn_insert);
        btn_delete = (Button) findViewById(R.id.question_btn_delete);
        rb_Selection = (RadioButton) findViewById(R.id.question_rb_selection);
        rb_text = (RadioButton) findViewById(R.id.question_rb_text);
        mListView = (ListView) findViewById(R.id.question_listView);
        mAdapter = new ArrayAdapter<Question>(ActivityCreateQuestion.this, R.layout.spinner_item);
        mListView.setAdapter(mAdapter);
    }

    private void eventListeners() {

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        btn_insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!txt_topic.getText().toString().trim().isEmpty()) {
                    task_setQuestion = new asyncSetQuestion();
                    task_setQuestion.execute();
                } else {
                    new MessageBox(ActivityCreateQuestion.this, "لطفا متن سوال را وارد نمایید .").show();
                }
            }
        });
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                selectedPositionItem = position;
                afterListViewItemClicked();
            }
        });

        refresh_lv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                afterRefreshButtonClicked();
            }
        });
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                task_deleteQuestion = new asyncDeleteQuestion();
                task_deleteQuestion.execute();
            }
        });

    }

    private void afterListViewItemClicked() {
        btn_insert.setVisibility(View.GONE);
        btn_delete.setVisibility(View.VISIBLE);
        frm_layout.setVisibility(View.VISIBLE);
        mListView.setEnabled(false);
        txt_topic.setText(mAdapter.getItem(selectedPositionItem).toString());
        ReplyType replyType = ReplyType.values()[mAdapter.getItem(selectedPositionItem).getReplyType()];
        if (replyType == ReplyType.selection)
            rb_Selection.setChecked(true);
        else
            rb_text.setChecked(true);
    }

    private void afterRefreshButtonClicked() {
        selectedPositionItem = -1;
        btn_insert.setVisibility(View.VISIBLE);
        btn_delete.setVisibility(View.GONE);
        frm_layout.setVisibility(View.GONE);
        mListView.setEnabled(true);
        txt_topic.setText("");
        rb_Selection.setChecked(true);
    }

    private class asyncSetQuestion extends AsyncTask<String, Void, Void> {
        String msg = null;
        ProgressDialog dialog;
        int result = 0;
        String label;
        int replyType;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(ActivityCreateQuestion.this, "", "در حال ارسال اطلاعات ...");
            dialog.getWindow().setGravity(Gravity.END);
            dialog.setCancelable(true);
            btn_insert.setClickable(false);
            label = txt_topic.getText().toString().trim();
            replyType = rb_Selection.isChecked() ? ReplyType.selection.ordinal() : ReplyType.text.ordinal();

        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                result = WebService.invokeSetQuestionWS(G.UserInfo.getUserName(), G.UserInfo.getPassword(), G.officeId, label, replyType);
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
                new MessageBox(ActivityCreateQuestion.this, msg).show();
            } else {
                if (result > 0) {
                    dialog.dismiss();
                    Question question = new Question();
                    question.setId(result);
                    question.setLabel(txt_topic.getText().toString().trim());
                    question.setOfficeId(G.officeId);
                    question.setReplyType(rb_Selection.isChecked() ? ReplyType.selection.ordinal() : ReplyType.text.ordinal());
                    mAdapter.add(question);
                    Toast.makeText(ActivityCreateQuestion.this, "ثبت با موفقیت انجام شده است .", Toast.LENGTH_SHORT).show();
                } else {
                    dialog.dismiss();
                    new MessageBox(ActivityCreateQuestion.this, "خطایی در ثبت سوال رخ داده است .").show();
                }
            }
            btn_insert.setClickable(true);
        }
    }

    private class asyncDeleteQuestion extends AsyncTask<String, Void, Void> {
        String msg = null;
        ProgressDialog dialog;
        boolean result = false;
        int questionId;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(ActivityCreateQuestion.this, "", "در حال حذف  ...");
            dialog.getWindow().setGravity(Gravity.END);
            dialog.setCancelable(true);
            btn_delete.setClickable(false);
            questionId = mAdapter.getItem(selectedPositionItem).getId();

        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                result = WebService.invokeDeleteQuestionWS(G.UserInfo.getUserName(), G.UserInfo.getPassword(), G.officeId, questionId);
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
                new MessageBox(ActivityCreateQuestion.this, msg).show();
            } else {
                if (result) {
                    dialog.dismiss();
                    mAdapter.remove(mAdapter.getItem(selectedPositionItem));
                    Toast.makeText(ActivityCreateQuestion.this, "حذف با موفقیت انجام شده است .", Toast.LENGTH_SHORT).show();
                    afterRefreshButtonClicked();
                } else {
                    dialog.dismiss();
                    new MessageBox(ActivityCreateQuestion.this, "خطایی در عملیات حذف رخ داده است .").show();
                }
            }
            btn_delete.setClickable(true);
        }
    }

    private class asyncGetQuestion extends AsyncTask<String, Void, Void> {
        String msg = null;
        ProgressDialog dialog;
        ArrayList<Question> questions = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(ActivityCreateQuestion.this, "", "در حال دریافت اطلاعات ...");
            dialog.getWindow().setGravity(Gravity.END);
            dialog.setCancelable(true);
            btn_insert.setClickable(false);

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
                dialog.dismiss();
                new MessageBox(ActivityCreateQuestion.this, msg).show();
            } else {
                if (questions != null && questions.size() > 0) {
                    for (Question q : questions)
                        mAdapter.add(q);
                }
                dialog.dismiss();
            }
            btn_insert.setClickable(true);
        }
    }
}
