package com.example.doctorsbuilding.nav;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;

public class MyAlertDialogFragment extends DialogFragment {
    private OnClickListener onClickListener;

    public static MyAlertDialogFragment newInstance(String title, String msg, String positive, String negative) {
        MyAlertDialogFragment frag = new MyAlertDialogFragment();

        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("msg", msg);
        args.putString("positive", positive);
        args.putString("negative", negative);
        frag.setArguments(args);
        return frag;
    }

    public static MyAlertDialogFragment newInstance(String title, String msg, String neutral) {
        MyAlertDialogFragment frag = new MyAlertDialogFragment();

        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("msg", msg);
        args.putString("neutral", neutral);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String title = getArguments().getString("title");
        String msg = getArguments().getString("msg");
        String positive = getArguments().getString("positive");
        String negative = getArguments().getString("negative");
        String neutral = getArguments().getString("neutral");

        AlertDialog dialog = null;
        if (neutral == null) {
            dialog = new AlertDialog.Builder(getActivity())
                    .setIcon(android.R.drawable.ic_menu_info_details)
                    .setTitle(title)
                    .setMessage(msg)
                    .setPositiveButton(positive,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    onClickListener.onClick(DialogInterface.BUTTON_POSITIVE);
                                }
                            }
                    )
                    .setNegativeButton(negative,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    onClickListener.onClick(DialogInterface.BUTTON_NEGATIVE);
                                }
                            }
                    )
                    .create();
        } else {
            dialog = new AlertDialog.Builder(getActivity())
                    .setIcon(android.R.drawable.ic_menu_info_details)
                    .setTitle(title)
                    .setMessage(msg)
                    .setNeutralButton(neutral,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    onClickListener.onClick(DialogInterface.BUTTON_NEUTRAL);
                                }
                            }
                    )
                    .create();
        }
        return dialog;
    }

    public MyAlertDialogFragment setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
        return this;
    }

    public interface OnClickListener {
        void onClick(int whichButton);
    }
}