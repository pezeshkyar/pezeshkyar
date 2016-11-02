package com.example.doctorsbuilding.nav.support;

/**
 * Created by hossein on 11/1/2016.
 */
public class Message {
    private int id;
    private int user_id;
    private int ticket_id;
    private String send_message;
    private String recieve_message;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getTicket_id() {
        return ticket_id;
    }

    public void setTicket_id(int ticket_id) {
        this.ticket_id = ticket_id;
    }

    public String getSend_message() {
        return send_message;
    }

    public void setSend_message(String send_message) {
        this.send_message = send_message;
    }

    public String getRecieve_message() {
        return recieve_message;
    }

    public void setRecieve_message(String recieve_message) {
        this.recieve_message = recieve_message;
    }
}
