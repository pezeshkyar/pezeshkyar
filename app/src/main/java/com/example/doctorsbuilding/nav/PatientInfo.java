package com.example.doctorsbuilding.nav;

import java.io.Serializable;

/**
 * Created by hossein on 9/3/2016.
 */
public class PatientInfo implements Serializable {
    private String firstName;
    private String lastName;
    private String mobileNo;
    private String username;
    private int reservationId;
    private int firstReservationId;
    private int taskId;
    private String taskName;
    private int taskGroupId;
    private String taskGroupName;
    private int payment;
    private String description;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getReservationId() {
        return reservationId;
    }

    public void setReservationId(int reservationId) {
        this.reservationId = reservationId;
    }

    public int getFirstReservationId() {
        return firstReservationId;
    }

    public void setFirstReservationId(int firstReservationId) {
        this.firstReservationId = firstReservationId;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public int getPayment() {
        return payment;
    }

    public void setPayment(int payment) {
        this.payment = payment;
    }

    public String getDescription() {
        return description;
    }

    public int getTaskGroupId() {
        return taskGroupId;
    }

    public void setTaskGroupId(int taskGroupId) {
        this.taskGroupId = taskGroupId;
    }

    public String getTaskGroupName() {
        return taskGroupName;
    }

    public void setTaskGroupName(String taskGroupName) {
        this.taskGroupName = taskGroupName;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
