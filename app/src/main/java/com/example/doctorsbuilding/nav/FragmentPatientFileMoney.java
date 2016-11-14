package com.example.doctorsbuilding.nav;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.example.doctorsbuilding.nav.Util.MessageBox;
import com.example.doctorsbuilding.nav.Web.WebService;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by hossein on 11/13/2016.
 */
public class FragmentPatientFileMoney extends Fragment {

    int lastExpandedPosition = -1;
    int lastClickedPosition = 0;
    TextView nothing;
    ExpandableListView expListView;
    PatientFileListAdapter expAdapter;
    asyncGetPatientFile task_getPatientFile = null;
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String PATIENT_USER_NAME = "patientUserName";
    private String patientUserName = null;

    public static FragmentPatientFileMoney newInstance(int sectionNumber, String patientUserName) {
        FragmentPatientFileMoney fragment = new FragmentPatientFileMoney();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        args.putString(PATIENT_USER_NAME, patientUserName);

        fragment.setArguments(args);
        return fragment;
    }

    public FragmentPatientFileMoney() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.patient_file_money, container, false);
        expListView = (ExpandableListView) rootView.findViewById(R.id.patient_file_exp_lv);
        nothing = (TextView) rootView.findViewById(R.id.patientfile_nothing);
        setListener();

        Bundle bundle = this.getArguments();
        if (bundle != null)
            patientUserName = bundle.getString("patientUserName");

        if (patientUserName != null) {
            task_getPatientFile = new asyncGetPatientFile();
            task_getPatientFile.execute(patientUserName);
        }
        return rootView;

    }

    private void setListener() {
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
    }

    private class asyncGetPatientFile extends AsyncTask<String, Void, Void> {
        String msg = null;
        ProgressDialog dialog;
        Map<Integer, ArrayList<PatientFile>> map = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(getActivity(), "", "در حال دریافت اطلاعات ...");
            dialog.getWindow().setGravity(Gravity.END);
            dialog.setCancelable(true);
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                map = WebService.invokeGetPatientFileWS(G.UserInfo.getUserName(), G.UserInfo.getPassword(), G.officeId, strings[0]);
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
                if (map.size() > 0) {
                    fillPatientList(map);
                } else {
                    nothing.setVisibility(View.VISIBLE);
                }

                dialog.dismiss();
            }
        }
    }

    private void fillPatientList(Map<Integer, ArrayList<PatientFile>> map) {
        ArrayList<PatientFileGroup> groups = new ArrayList<PatientFileGroup>();
        Iterator it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            ArrayList<PatientFile> patientFiles = (ArrayList<PatientFile>) pair.getValue();
            PatientFileGroup group = new PatientFileGroup(patientFiles.get(0));

            ArrayList<PatientFileChild> childs = new ArrayList<PatientFileChild>();
            for (int j = 0; j < patientFiles.size(); j++) {
                PatientFileChild child = new PatientFileChild(patientFiles.get(j));
                childs.add(child);
            }
            group.setItem(childs);
            groups.add(group);
        }
        expAdapter = new PatientFileListAdapter(getActivity(), groups);
        expListView.setAdapter(expAdapter);
        for (int i = 0; i < expAdapter.getGroupCount(); i++)
            expListView.expandGroup(i);
        //setListener();
    }
}
