package com.example.doctorsbuilding.nav;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by hossein on 6/13/2016.
 */
public class ActivityNotificationDialog extends Dialog {
    private Context context;
    private ArrayList<MessageInfo> items;
    public ActivityNotificationDialog(Context context, ArrayList<MessageInfo> items) {
        super(context);
        this.context = context;
        this.items = items;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.notification_layout_dialog);
        ListView listView = (ListView)findViewById(R.id.notificationDialog_listView);
        ListAdapter adapter = new CustomListAdapterNotificationDialog(context, items, this);
        listView.setAdapter(adapter);
    }
}
