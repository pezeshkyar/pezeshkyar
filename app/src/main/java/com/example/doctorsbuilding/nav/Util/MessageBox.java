package com.example.doctorsbuilding.nav.Util;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.example.doctorsbuilding.nav.R;

/**
 * Created by hossein on 6/5/2016.
 */
public class MessageBox extends Dialog implements View.OnClickListener{
    Context context;
    TextView tv_error;
    Button btn_accept;
    String errorMessage;
    boolean btnSatate = false;


    public MessageBox(Context context, String errorMessage) {
        super(context, android.R.style.Theme_DeviceDefault_Dialog_MinWidth);
        this.context = context;
        this.errorMessage = errorMessage;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.message_box);
        initViews();
    }

    private void initViews(){
        tv_error = (TextView)findViewById(R.id.message_error);
        btn_accept = (Button)findViewById(R.id.message_btn_accept);
        //set views
        tv_error.setText(errorMessage);
        btn_accept.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.message_btn_accept){
            btnSatate = true;
            dismiss();
        }
    }
    public boolean pressAcceptButton(){
        return btnSatate;
    }
}
