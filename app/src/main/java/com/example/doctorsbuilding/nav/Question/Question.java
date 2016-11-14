package com.example.doctorsbuilding.nav.Question;

/**
 * Created by hossein on 11/8/2016.
 */
public class Question {
    private int id;
    private int officeId;
    private String label;
    private int replyType;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOfficeId() {
        return officeId;
    }

    public void setOfficeId(int officeId) {
        this.officeId = officeId;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getReplyType() {
        return replyType;
    }

    public void setReplyType(int replyType) {
        this.replyType = replyType;
    }

    @Override
    public String toString() {
        return label;
    }
}
