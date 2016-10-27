package com.example.doctorsbuilding.nav.Dr.Nobat;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.example.doctorsbuilding.nav.Dr.Profile.ExpChild;
import com.example.doctorsbuilding.nav.Dr.Profile.ExpGroup;
import com.example.doctorsbuilding.nav.Dr.Profile.ExpandListAdapter;
import com.example.doctorsbuilding.nav.G;
import com.example.doctorsbuilding.nav.PException;
import com.example.doctorsbuilding.nav.R;
import com.example.doctorsbuilding.nav.Turn;
import com.example.doctorsbuilding.nav.Util.MessageBox;
import com.example.doctorsbuilding.nav.Web.WebService;

import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class DrNobatFragment extends Fragment {

    ExpandListAdapter expAdapter;
    ExpandableListView expListView;
    ArrayList<ExpGroup> list_Group;
    int lastExpandedPosition = -1;
    int lastClickedPosition = 0;
    ArrayList<ExpGroup> groups;
    ArrayList<ExpChild> childs;

    asyncCallTurn getAllTurnTask;

    private static final String ARG_SECTION_NUMBER = "section_number";

    public static DrNobatFragment newInstance(int sectionNumber) {
        DrNobatFragment fragment = new DrNobatFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public DrNobatFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_nobat, container, false);


        expListView = (ExpandableListView) rootView.findViewById(R.id.lvExp);

        expListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
                if (lastExpandedPosition != -1
                        && groupPosition != lastExpandedPosition) {
                    expListView.collapseGroup(lastExpandedPosition);
                }
                lastExpandedPosition = groupPosition;

            }
        });
        expListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int groupPosition, long id) {
                Boolean shouldExpand = (!expListView.isGroupExpanded(groupPosition));
                expListView.collapseGroup(lastClickedPosition);

                if (shouldExpand) {
                    expListView.expandGroup(groupPosition);
                    expListView.setSelectionFromTop(groupPosition, 0);
                }
                lastClickedPosition = groupPosition;
                return true;
            }
        });


        getAllTurnTask = new asyncCallTurn();
        getAllTurnTask.execute();


        return rootView;

    }

    private class asyncCallTurn extends AsyncTask<String, Void, Void> {
        ArrayList<Turn> result;
        String msg = null;
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(getActivity(), "", "در حال دریافت اطلاعات ...");
            dialog.setCancelable(true);
            dialog.getWindow().setGravity(Gravity.END);
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                result = WebService.invokeGetAllTurnFromToday(G.UserInfo.getUserName(), G.UserInfo.getPassword(), G.officeId);
            } catch (PException ex) {
                msg = ex.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (msg != null) {
                dialog.dismiss();
                new MessageBox(getActivity(), msg).show();
            } else {
                groups = new ArrayList<ExpGroup>();
                int i = 0;
                while (i < result.size()) {

                    String str = result.get(i).getDate();
                    ExpChild child = new ExpChild(result.get(i));
                    childs = new ArrayList<ExpChild>();
                    childs.add(child);

                    while (i + 1 < result.size() && str.equals(result.get(i + 1).getDate())) {

                        child = new ExpChild(result.get(i + 1));
                        childs.add(child);
                        i++;
                    }

                    ExpGroup group = new ExpGroup();
                    group.setName(result.get(i).getLongDate());
                    group.setItem(childs);
                    groups.add(group);
                    i++;
                }

                list_Group = groups;
                expAdapter = new ExpandListAdapter(getContext(), list_Group);
                expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                    @Override
                    public boolean onChildClick(ExpandableListView parent, View view, int groupPosition, int childPosition, long id) {

                        return false;
                    }
                });
                expListView.setAdapter(expAdapter);
                dialog.dismiss();
            }
        }
    }
}

