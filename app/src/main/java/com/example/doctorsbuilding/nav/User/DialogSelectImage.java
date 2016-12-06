package com.example.doctorsbuilding.nav.User;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.example.doctorsbuilding.nav.MainForm.DialogSearchFilter;
import com.example.doctorsbuilding.nav.R;

/**
 * Created by hossein on 12/6/2016.
 */
public class DialogSelectImage extends Dialog {
    private Context context;
    private Button btnCamera;
    private Button btnGallery;
    private String sourceType = "";
    public static final String CAMERA = "camera";
    public static final String GALLERY = "gallery";
    public DialogSelectImage(Context context, int themeResId) {
        super(context, themeResId);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_select_image);
        btnCamera = (Button)findViewById(R.id.btn_selectFromCamera);
        btnGallery = (Button)findViewById(R.id.btn_selectFromGallery);
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSourceType(CAMERA);
                dismiss();
            }
        });
        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSourceType(GALLERY);
                dismiss();
            }
        });
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }
}
