package com.example.doctorsbuilding.nav.Dr.Profile;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Trace;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.example.doctorsbuilding.nav.G;
import com.example.doctorsbuilding.nav.R;
import com.example.doctorsbuilding.nav.User.DialogSelectImage;
import com.example.doctorsbuilding.nav.Util.MessageBox;
import com.example.doctorsbuilding.nav.Web.Hashing;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

/**
 * Created by hossein on 12/8/2016.
 */
public class DialogChangePassword extends Dialog {
    private Context context;
    private EditText txtOldPass;
    private EditText txtNewPass;
    private Button btnApply;
    private boolean result = false;
    private String password;

    public DialogChangePassword(Context context, int themeResId) {
        super(context, themeResId);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_change_password);
        txtOldPass = (EditText) findViewById(R.id.changePass_oldPass);
        txtNewPass = (EditText) findViewById(R.id.changePass_newPass);
        btnApply = (Button) findViewById(R.id.changePass_apply);
        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkField()) {
                    setResult(true);
                    setPassword(txtNewPass.getText().toString().trim());
                    dismiss();
                }
            }
        });
    }

    private boolean checkField() {
        if (txtOldPass.getText().toString().trim().isEmpty()) {
            new MessageBox(context, "لطفا پسورد قبلی خود را وارد نمایید .").show();
            return false;
        }
        if (txtNewPass.getText().toString().trim().isEmpty()) {
            new MessageBox(context, "لطفا پسورد جدید خود را وارد نمایید .").show();
            return false;
        }
        if (!isValidOldPassword()) {
            new MessageBox(context, "پسورد قبلی شما نادرست می باشد .").show();
            return false;
        }
        if (txtNewPass.getText().toString().trim().length() < 4) {
            new MessageBox(context, "تعداد کاراکترهای پسورد نباید کمتر از 4 تا باشد .").show();
            return false;
        }
        return true;
    }

    private boolean isValidOldPassword() {
        String pwd = txtOldPass.getText().toString().trim();
        try {
            pwd = Hashing.SHA1(pwd);
        } catch (NoSuchAlgorithmException e) {
        } catch (UnsupportedEncodingException e) {
        }
        if (!pwd.equals(G.getSharedPreferences().getString("pass", "")))
            return false;
        return true;
    }

    public boolean getResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
