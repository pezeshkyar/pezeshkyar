package com.example.doctorsbuilding.nav.MainForm;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.Spinner;

import com.example.doctorsbuilding.nav.Expert;
import com.example.doctorsbuilding.nav.G;
import com.example.doctorsbuilding.nav.PException;
import com.example.doctorsbuilding.nav.R;
import com.example.doctorsbuilding.nav.SubExpert;
import com.example.doctorsbuilding.nav.User.City;
import com.example.doctorsbuilding.nav.User.State;
import com.example.doctorsbuilding.nav.Util.MessageBox;
import com.example.doctorsbuilding.nav.Web.WebService;

import java.util.ArrayList;

/**
 * Created by hossein on 12/3/2016.
 */
public class DialogSearchFilter extends Dialog {
    private Context contex;
    private int provinceId = -1;
    private int cityId = -1;
    private int specId = -1;
    private int subSpecId = -1;
    private String fname = null;
    private String lname = null;
    private boolean applyFilter = false;
    EditText firstname;
    EditText lastname;
    Button applyBtn;
    Spinner state;
    Spinner city;
    Spinner expert;
    Spinner subExpert;
    ArrayList<City> cities;
    ArrayList<State> states;
    ArrayList<Expert> experts;
    ArrayList<SubExpert> subExperts;
    ArrayAdapter<State> stateAdapter;
    ArrayAdapter<City> cityAdapter;
    ArrayAdapter<Expert> expertAdapter;
    ArrayAdapter<SubExpert> subExpertAdapter;
    int stateSelectedIndex = -1;
    int expertSelectedIndex = -1;

    AsyncCallStateWS getStateTask;
    AsyncCallGetExpertWS getExpertTask;
    AsyncCallCityWS getCityTask;
    AsyncCallSubExpertWS getSubExpertTask;

    public DialogSearchFilter(Context context, int themeResId) {
        super(context, themeResId);
        this.contex = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_filter_offices);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        initViews();
        eventListener();

        getStateTask = new AsyncCallStateWS();
        getStateTask.execute();

        getExpertTask = new AsyncCallGetExpertWS();
        getExpertTask.execute();
    }

    public void stopAllAsyncTask() {
        if (getStateTask != null)
            getStateTask.cancel(true);

        if (getExpertTask != null)
            getExpertTask.cancel(true);

        if (getCityTask != null)
            getCityTask.cancel(true);

        if (getSubExpertTask != null)
            getSubExpertTask.cancel(true);

    }

    private void initViews() {
        state = (Spinner) findViewById(R.id.filter_office_province);
        city = (Spinner) findViewById(R.id.filter_office_city);
        firstname = (EditText) findViewById(R.id.filter_office_name);
        lastname = (EditText) findViewById(R.id.filter_office_lastname);
        expert = (Spinner) findViewById(R.id.filter_office_expert);
        subExpert = (Spinner) findViewById(R.id.filter_office_subExpert);
        applyBtn = (Button) findViewById(R.id.filter_office_btn_apply);

    }

    private void eventListener() {

        state.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                stateSelectedIndex = i;
                getCityTask = new AsyncCallCityWS();
                getCityTask.execute();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        expert.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                expertSelectedIndex = i;
                getSubExpertTask = new AsyncCallSubExpertWS();
                getSubExpertTask.execute();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        applyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (state.getSelectedItemPosition() != 0)
                        setProvinceId(((State) state.getSelectedItem()).GetStateID());
                } catch (Exception ex) {
                    setProvinceId(-1);
                }
                try {
                    if (city.getSelectedItemPosition() != 0)
                        setCityId(((City) city.getSelectedItem()).GetCityID());
                } catch (Exception ex) {
                    setCityId(-1);
                }
                try {
                    if (expert.getSelectedItemPosition() != 0)
                        setSpecId(((Expert) expert.getSelectedItem()).getId());
                } catch (Exception ex) {
                    setSpecId(-1);
                }
                try {
                    if (subExpert.getSelectedItemPosition() != 0)
                        setSubSpecId(((SubExpert) subExpert.getSelectedItem()).getId());
                } catch (Exception ex) {
                    setSubSpecId(-1);
                }
                setFname(firstname.getText().toString().trim());
                setLname(lastname.getText().toString().trim());
                setApplyFilter(true);
                dismiss();
            }
        });

    }

    private class AsyncCallStateWS extends AsyncTask<String, Void, Void> {

        String msg = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                states = WebService.invokeGetProvinceNameWS();
                cities = WebService.invokeGetCityNameWS(1);
            } catch (PException ex) {
                msg = ex.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (msg != null) {
                new MessageBox(contex, msg).show();
            } else {

                setStateSpinner();

                setCitySpinner();
            }
        }

        private void setStateSpinner() {
            State s = new State();
            s.SetStateName("استان ...");
            states.add(0, s);
            stateAdapter = new ArrayAdapter<State>(contex
                    , R.layout.spinner_item, states);
            state.setAdapter(stateAdapter);
        }

        private void setCitySpinner() {
            City c = new City();
            c.SetCityName("شهر ...");
            cities.add(0, c);
            cityAdapter = new ArrayAdapter<City>(contex
                    , R.layout.spinner_item, cities);
            city.setAdapter(cityAdapter);
        }

    }

    // set city spinner

    private class AsyncCallCityWS extends AsyncTask<String, Void, Void> {
        String msg = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                if (stateSelectedIndex != -1) {
                    cities = WebService.invokeGetCityNameWS(states.get(stateSelectedIndex).GetStateID());
                }
            } catch (PException ex) {
                msg = ex.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (msg != null) {
                new MessageBox(contex, msg).show();
            } else {
                setCitySpinner();
            }
        }

        private void setCitySpinner() {
            City c = new City();
            c.SetCityName("شهر ...");
            cities.add(0, c);
            cityAdapter = new ArrayAdapter<City>(contex
                    , R.layout.spinner_item, cities);
            city.setAdapter(cityAdapter);
        }

    }

    //set expert spinner ..........................................................................

    private class AsyncCallGetExpertWS extends AsyncTask<String, Void, Void> {
        String msg = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                experts = WebService.invokeGetSpecWS();

                subExperts = WebService.invokeGetSubSpecWS(1);

            } catch (PException ex) {
                msg = ex.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (msg != null) {
                new MessageBox(contex, msg).show();
            } else {
                setExpertSpinner();

                setSubExpertSpinner();
            }
        }

        private void setExpertSpinner() {
            Expert e = new Expert();
            e.setName("تخصص ...");
            experts.add(0, e);
            expertAdapter = new ArrayAdapter<Expert>(contex
                    , R.layout.spinner_item, experts);
            expert.setAdapter(expertAdapter);
        }

        private void setSubExpertSpinner() {
            SubExpert s = new SubExpert();
            s.setName("شاخه تخصص ...");
            subExperts.add(0, s);
            subExpertAdapter = new ArrayAdapter<SubExpert>(contex
                    , R.layout.spinner_item, subExperts);
            subExpert.setAdapter(subExpertAdapter);
        }

    }

    //set sub expert  ..............................................................

    private class AsyncCallSubExpertWS extends AsyncTask<String, Void, Void> {
        String msg = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... strings) {
            if (expertSelectedIndex != -1) {
                try {
                    subExperts = WebService.invokeGetSubSpecWS(experts.get(expertSelectedIndex).getId());
                } catch (PException ex) {
                    msg = ex.getMessage();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (msg != null) {
                new MessageBox(contex, msg).show();
            } else {
                setSubExpertSpinner();
            }
        }

        private void setSubExpertSpinner() {
            SubExpert s = new SubExpert();
            s.setName("شاخه تخصص ...");
            s.setId(-1);
            subExperts.add(0, s);
            subExpertAdapter = new ArrayAdapter<SubExpert>(contex
                    , R.layout.spinner_item, subExperts);
            subExpert.setAdapter(subExpertAdapter);
        }

    }

    public int getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(int provinceId) {
        this.provinceId = provinceId;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public int getSpecId() {
        return specId;
    }

    public void setSpecId(int specId) {
        this.specId = specId;
    }

    public int getSubSpecId() {
        return subSpecId;
    }

    public void setSubSpecId(int subSpecId) {
        this.subSpecId = subSpecId;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public boolean isApplyFilter() {
        return applyFilter;
    }

    public void setApplyFilter(boolean applyFilter) {
        this.applyFilter = applyFilter;
    }
}
