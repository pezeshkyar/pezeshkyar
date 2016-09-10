package com.example.doctorsbuilding.nav;

import java.net.ConnectException;

/**
 * Created by hossein on 8/24/2016.
 */
public class PException extends Exception {
    private String msg;
    public PException(String msg){
        this.msg = msg;
    }

    @Override
    public String getMessage() {
        return msg;
    }
}
