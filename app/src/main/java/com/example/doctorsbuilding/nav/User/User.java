package com.example.doctorsbuilding.nav.User;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;

import java.io.ByteArrayOutputStream;

/**
 * Created by hossein on 6/26/2016.
 */
public class User {
    private String firstName;
    private String lastName;
    private String phone;
    private String email;
    private String userName;
    private String password;
    private int role;
    private int cityID;
    private String cityName;
    private int stateID;
    private String stateName;
    private Bitmap imgProfile;
    private int i = 0;

    public String getFirstName() {
        return this.firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return this.phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getRole() {
        return this.role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public Bitmap getImgProfile() {
        return this.imgProfile;
    }

    public void setImgProfile(Bitmap image) {
        this.imgProfile = image;
    }

    public int getCityID() {
        return this.cityID;
    }

    public void setCityID(int cityID) {
        this.cityID = cityID;
    }

    public String getCityName() {
        return this.cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public int getStateID() {
        return this.stateID;
    }

    public void setStateID(int stateID) {
        this.stateID = stateID;
    }

    public String getStateName() {
        return this.stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }


    @Override
    public String toString() {
        return getFirstName().concat(" "+getLastName());
    }

}
