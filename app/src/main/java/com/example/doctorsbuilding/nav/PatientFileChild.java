package com.example.doctorsbuilding.nav;

/**
 * Created by hossein on 9/4/2016.
 */
public class PatientFileChild {
    private String date;
    private String longDate;
    private String time;
    private String description;
    private int totalPayment;
    private int payment;
    private int remain;
    private int reservationId;
    private int firstReservationId;

    public PatientFileChild(PatientFile patientFile) {
        this.date = patientFile.getDate();
        this.longDate = patientFile.getLongDate();
        this.totalPayment = patientFile.getTotalPayment();
        this.reservationId = patientFile.getReservationId();
        this.firstReservationId = patientFile.getFirstReservationId();
        this.time = patientFile.getTime();
        this.description = patientFile.getDescription();
        this.payment = patientFile.getPayment();
        this.remain = patientFile.getRemain();
    }

    public String getDate() {
        return date;
    }

    public String getLongDate() {
        return longDate;
    }

    public String getTime() {
        return time;
    }

    public String getDescription() {
        return description;
    }

    public int getTotalPayment() {
        return totalPayment;
    }

    public int getPayment() {
        return payment;
    }

    public int getRemain() {
        return remain;
    }

    public int getReservationId() {
        return reservationId;
    }

    public int getFirstReservationId() {
        return firstReservationId;
    }
}
