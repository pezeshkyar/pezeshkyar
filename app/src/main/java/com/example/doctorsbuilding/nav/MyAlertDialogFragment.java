package com.example.doctorsbuilding.nav;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.doctorsbuilding.nav.support.Message;

public class MyAlertDialogFragment extends DialogFragment {
    private OnClickListener onClickListener;
    private View rootView;
    private TextView title;
    private ImageView icon;
    private TextView message;
    private Button neutralButton;
    private Button positiveButton;
    private Button negativeButton;

    public static MyAlertDialogFragment newInstance(String title, int icon, String msg, String positive, String negative) {
        MyAlertDialogFragment frag = new MyAlertDialogFragment();

        Bundle args = new Bundle();
        args.putString("title", title);
        args.putInt("icon", icon);
        args.putString("msg", msg);
        args.putString("positive", positive);
        args.putString("negative", negative);
        frag.setArguments(args);
        return frag;
    }

    public static MyAlertDialogFragment newInstance(String title, int icon, String msg, String neutral) {
        MyAlertDialogFragment frag = new MyAlertDialogFragment();

        Bundle args = new Bundle();
        args.putString("title", title);
        args.putInt("icon", icon);
        args.putString("msg", msg);
        args.putString("neutral", neutral);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        return dialog;

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.message_box, container, false);
        initViews();
        viewListener();
        return rootView;
    }

    private void initViews() {
        title = (TextView) rootView.findViewById(R.id.msg_title);
        icon = (ImageView) rootView.findViewById(R.id.msg_icon);
        message = (TextView) rootView.findViewById(R.id.msg_message);
        neutralButton = (Button) rootView.findViewById(R.id.msg_neutralButton);
        positiveButton = (Button) rootView.findViewById(R.id.msg_positiveButton);
        negativeButton = (Button) rootView.findViewById(R.id.msg_negativeButton);

        title.setText(getArguments().getString("title"));
        icon.setImageResource(getArguments().getInt("icon"));
        message.setText(getArguments().getString("msg"));
        if (getArguments().getString("neutral") == null) {
            neutralButton.setVisibility(View.GONE);
            positiveButton.setVisibility(View.VISIBLE);
            negativeButton.setVisibility(View.VISIBLE);
            positiveButton.setText(getArguments().getString("positive"));
            negativeButton.setText(getArguments().getString("negative"));
        } else {
            neutralButton.setText(getArguments().getString("neutral"));
        }
    }

    private void viewListener() {
        neutralButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickListener.onClick(DialogInterface.BUTTON_NEUTRAL);
                dismiss();
            }
        });
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickListener.onClick(DialogInterface.BUTTON_POSITIVE);
                dismiss();
            }
        });
        negativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickListener.onClick(DialogInterface.BUTTON_NEGATIVE);
                dismiss();
            }
        });
    }

    public MyAlertDialogFragment setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
        return this;
    }

    public interface OnClickListener {
        void onClick(int whichButton);
    }
}