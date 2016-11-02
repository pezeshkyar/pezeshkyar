package com.example.doctorsbuilding.nav;

import android.content.Intent;

/**
 * Created by hossein on 6/29/2016.
 */
public enum UserType {
    None(0),
    Dr(1),
    User(2),
    secretary(3),
    Guest(4);
    private final int userType;
    private static final String name = UserType.class.getName();

    UserType(int menutype) {
        this.userType = menutype;
    }

    public int getUsertype() {
        return this.userType;
    }

    public void attachTo(Intent intent) {
        intent.putExtra(name, ordinal());
    }

    public static UserType detachFrom(Intent intent) {
        if (!intent.hasExtra(name)) throw new IllegalStateException();
        return values()[intent.getIntExtra(name, -1)];
    }

}
