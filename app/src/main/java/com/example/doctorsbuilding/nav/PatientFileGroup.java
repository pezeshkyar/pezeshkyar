package com.example.doctorsbuilding.nav;

import com.example.doctorsbuilding.nav.Util.Util;

import java.util.ArrayList;

/**
 * Created by hossein on 9/4/2016.
 */
public class PatientFileGroup {
    private int taskId;
    private String taskName;
    private int price;
    private ArrayList<PatientFileChild> childs;

    public PatientFileGroup(PatientFile patientFile) {
        this.taskId = patientFile.getTaskId();
        this.taskName = patientFile.getTaskName();
        this.price = patientFile.getPrice();
    }

    public void setItem(ArrayList<PatientFileChild> childs) {
        this.childs = childs;
    }

    public ArrayList<PatientFileChild> getChilds() {
        return childs;
    }

    public int getTaskId() {
        return taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public int getPrice() {
        return price;
    }
    public String getPriceString(){
        return "مبلغ کل : ".concat(Util.getCurrency(price));
    }
}