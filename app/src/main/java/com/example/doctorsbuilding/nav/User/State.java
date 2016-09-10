package com.example.doctorsbuilding.nav.User;

/**
 * Created by hossein on 6/25/2016.
 */
public class State {
    private int id;
    private String name;
    public void SetStateID(int id){
        this.id = id;
    }
    public int GetStateID(){
        return this.id;
    }
    public void SetStateName(String name){
        this.name = name;
    }
    public String GetStateName(){
        return this.name;
    }

    @Override
    public String toString() {
        return name;
    }
}
