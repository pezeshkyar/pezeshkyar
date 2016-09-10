package com.example.doctorsbuilding.nav.User;

/**
 * Created by hossein on 6/26/2016.
 */
public class City {
    private int id;
    private int stateId;
    private String name;

    public int GetCityID() {
        return this.id;
    }

    public void SetCityID(int id) {
        this.id = id;
    }

    public String GetCityName() {
        return this.name;
    }

    public void SetCityName(String name) {
        this.name = name;
    }

    public int GetStateID() {
        return this.stateId;
    }

    public void SetStateID(int id) {
        this.stateId = id;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
