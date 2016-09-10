package com.example.doctorsbuilding.nav.Dr.Notification;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.doctorsbuilding.nav.R;


public class NotificationFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    public NotificationFragment() {
    }

    public static NotificationFragment newInstance(int section_number) {
        NotificationFragment fragment = new NotificationFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, section_number);
        fragment.setArguments(args);
        return fragment;
    }


//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        View rootView =  inflater.inflate(R.layout.fragment_notification, container, false);
//        ListView listView = (ListView)rootView.findViewById(R.id.lv_frag_notification);
//        listView.setAdapter(new CustomListAdapterNotifications(getContext()
//                , getResources().getStringArray(R.array.list_groups)
//                , getResources().getStringArray(R.array.list_groups)
//                , getResources().getStringArray(R.array.list_groups)));
//        return  rootView;
//    }

}
