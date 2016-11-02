package com.example.doctorsbuilding.nav.support;

/**
 * Created by hossein on 11/1/2016.
 */
public class Subject {
    private int id;
    private String subject;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    @Override
    public String toString() {
        return subject;
    }
}
