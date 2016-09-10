package com.example.doctorsbuilding.nav.Dr.Profile;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.doctorsbuilding.nav.G;
import com.example.doctorsbuilding.nav.R;
import com.example.doctorsbuilding.nav.User.User;
import com.example.doctorsbuilding.nav.Web.WebService;

import java.lang.reflect.Array;
import java.util.ArrayList;


public class PersonalInfoFragment extends Fragment {
    private static final String ARG_SECTION_NUMBER = "section_number";

    public PersonalInfoFragment() {
    }
    public static PersonalInfoFragment newInstance(int section_number) {
        PersonalInfoFragment fragment = new PersonalInfoFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER,section_number);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_info, container, false);
        ListView listView = (ListView)rootView.findViewById(R.id.lv_frag_info);
        String[] titles = {"نام و نام خانوادگی", "تخصص"};
        String[] details = {G.officeInfo.getFirstname()+" "+G.officeInfo.getLastname(), G.officeInfo.getExpertName()};

        listView.setAdapter(new CustomListAdapterInformation(getContext(),titles, details));
        return  rootView;
    }

}
