package com.example.doctorsbuilding.nav.Dr.Profile;

import java.io.Serializable;
import java.security.Policy;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hossein on 5/5/2016.
 */
public class ExpGroup {
    private String name;
    private ArrayList<ExpChild> item;

    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name = name;
    }
    public List<ExpChild> getItems(){
        return item;
    }
    public void setItem(ArrayList<ExpChild> item){
        this.item = item;
    }
}
