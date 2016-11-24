package com.example.doctorsbuilding.nav.Dr.Clinic;

import android.graphics.Bitmap;

/**
 * Created by hossein on 7/18/2016.
 */
public class Office {
    private int id;
    private String firstname;
    private String lastname;
    private String drUsername;
    private boolean isMyOffice;
    private int cityId;
    private String cityName;
    private int stateId;
    private String stateName;
    private int expertId;
    private String expertName;
    private int subExpertId;
    private String subExpertName;
    private String address;
    private String phone;
    private double latitude;
    private double longitude;
    private String biography;
    private int timeQuantum;
    private Bitmap photo;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getDrUsername() {
        return drUsername;
    }

    public void setDrUsername(String drUsername) {
        this.drUsername = drUsername;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public int getStateId() {
        return stateId;
    }

    public void setStateId(int stateId) {
        this.stateId = stateId;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public int getExpertId() {
        return expertId;
    }

    public void setExpertId(int expertId) {
        this.expertId = expertId;
    }

    public String getExpertName() {
        return expertName;
    }

    public void setExpertName(String expertName) {
        this.expertName = expertName;
    }

    public int getSubExpertId() {
        return subExpertId;
    }

    public void setSubExpertId(int subExpertId) {
        this.subExpertId = subExpertId;
    }

    public String getSubExpertName() {
        return subExpertName;
    }

    public void setSubExpertName(String subExpertName) {
        this.subExpertName = subExpertName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    public int getTimeQuantum() {
        return timeQuantum;
    }

    public void setTimeQuantum(int timeQuantum) {
        this.timeQuantum = timeQuantum;
    }

    public Bitmap getPhoto() {
        return photo;
    }

    public void setPhoto(Bitmap photo) {
        this.photo = photo;
    }

    public int isMyOffice() {
        return (isMyOffice ? 1 : 0);
    }

    public void setMyOffice(boolean myOffice) {
        isMyOffice = myOffice;
    }

    public Office clone() {
        Office office = new Office();
        office.id = this.id;
        office.firstname = this.firstname;
        office.lastname = this.lastname;
        office.drUsername = this.drUsername;
        office.cityId = this.cityId;
        office.cityName = this.cityName;
        office.stateId = this.stateId;
        office.stateName = this.stateName;
        office.expertId = this.expertId;
        office.expertName = this.expertName;
        office.subExpertId = this.subExpertId;
        office.subExpertName = this.subExpertName;
        office.address = this.address;
        office.phone = this.phone;
        office.latitude = this.latitude;
        office.longitude = this.longitude;
        office.biography = this.biography;
        office.timeQuantum = this.timeQuantum;
        office.photo = this.photo;

        return office;
    }

}
