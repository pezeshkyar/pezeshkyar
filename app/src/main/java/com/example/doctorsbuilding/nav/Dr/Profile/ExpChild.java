package com.example.doctorsbuilding.nav.Dr.Profile;

import com.example.doctorsbuilding.nav.Turn;

import java.io.Serializable;

/**
 * Created by hossein on 5/5/2016.
 */
public class ExpChild {
    private Turn turn;

    public ExpChild(Turn turn) {
        this.turn = turn;
    }

    public String getCapacity() {
        return "ظرفیت خالی : " + String.valueOf(turn.getCapacity() - turn.getReserved() + " نفر");
    }

    public String getDate() {
        return turn.getLongDate();
    }

    public String getTime() {
        int endHour = turn.getHour() + turn.getDuration() / 60;
        int endMinute = turn.getMin() + turn.getDuration() % 60;
        return String.valueOf(turn.getMin()) + " : "
                + String.valueOf(turn.getHour()) + "  الی  " + String.valueOf(endMinute) + " : " + String.valueOf(endHour);
    }

    public Turn getTurn() {
        return turn;
    }

    public void setTurn(Turn turn) {
        this.turn = turn;
    }
}
