package com.example.doctorsbuilding.nav.support;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.example.doctorsbuilding.nav.Dr.persindatepicker.util.PersianCalendar;
import com.example.doctorsbuilding.nav.R;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class DiscussActivity extends Activity {
    private DiscussArrayAdapter adapter;
    private ListView mListView;
    private EditText mEditText;
    private ImageButton mBtnSend;
    PersianCalendar persianCalendar = new PersianCalendar();

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discuss);

        mListView = (ListView) findViewById(R.id.support_listview);
        mBtnSend = (ImageButton) findViewById(R.id.support_btn_send);
        adapter = new DiscussArrayAdapter(getApplicationContext(), R.layout.listitem_discuss);
        mListView.setAdapter(adapter);
        mEditText = (EditText) findViewById(R.id.support_edit_text);

        mBtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                adapter.add(new OneComment(false, mEditText.getText().toString()
                        , persianCalendar.getPersianLongDateAndTime(), "اردشیر بهاریان"));
                mEditText.setText("");
                try {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                } catch (Exception ex) {

                }
                mListView.setSelection(mListView.getChildCount());
            }
        });

        addItems();
    }


    private void addItems() {
        adapter.add(new OneComment(true, "سلام، مشکل چیه ؟!", persianCalendar.getPersianLongDateAndTime(), "حسین سالخورده"));
    }

}