package com.example.doctorsbuilding.nav;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

/**
 * Created by hossein on 12/29/2016.
 */
public class DialogPayType extends Dialog {
    private Context context;
    private int payWay = -1;
    private Button btn_saman;
    private Button btn_etebar;

    public DialogPayType(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.dialog_pay_type);
        btn_etebar = (Button) findViewById(R.id.pay_btn_etebar);
        btn_saman = (Button) findViewById(R.id.pay_btn_saman);
        btn_etebar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPayWay(1);
                dismiss();
            }
        });
        btn_saman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPayWay(0);
                dismiss();
            }
        });

    }

    public int getPayWay() {
        return payWay;
    }

    public void setPayWay(int payWay) {
        this.payWay = payWay;
    }
}
