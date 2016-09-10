package com.example.doctorsbuilding.nav.User;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.doctorsbuilding.nav.R;

import java.util.ArrayList;

/**
 * Created by hossein on 6/13/2016.
 */
public class UserNewsActivity extends AppCompatActivity {
    Button backBtn;
    TextView nothingTxt;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_user_news);
        initViews();
        nothingTxt.setText("هیچ خبرنامه ای درج نشده است .");
    }

    private void initViews() {
        backBtn = (Button)findViewById(R.id.news_backBtn);
        nothingTxt = (TextView)findViewById(R.id.newsTxtNothing);

//        ArrayList<String> item;
//        ArrayList<ArrayList<String>> items = new ArrayList<ArrayList<String>>();
//        for (int i = 0; i < 10; i++) {
//            item = new ArrayList<String>();
//            for (int j = 0; j < 3; j++) {
//                item.add("محسن سالخورده گرمستانی");
//                item.add("استرس با فشارخون بالا ارتباط دارد، همچنین استرس می\u200Cتواند سبب ضربان غیرطبیعی قلب، لخته خونی و سختی شریان شود (آترواسکلروزیس). همچنین استرس با بیماری\u200Cهای شریانی قلب، حملات قلبی و نارسایی قلبی ارتباط دارد.");
//                item.add("1389/05/06 12:06 AM");
//            }
//            items.add(item);
//        }
//        ListView listView = (ListView) findViewById(R.id.userNews_listView);
//        ListAdapter adapter = new CustomListAdapterUserNews(UserNewsActivity.this, items);
//        listView.setAdapter(adapter);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserNewsActivity.this.onBackPressed();
            }
        });
    }
}
