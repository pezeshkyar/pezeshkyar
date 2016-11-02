package com.example.doctorsbuilding.nav.support;

import android.content.Intent;

/**
 * Created by hossein on 11/1/2016.
 */
public enum Priority {
    hight(1),
    medium(2),
    low(3);

    private final int priority;
    private static final String name = Priority.class.getName();

    Priority(int menutype) {
        this.priority = menutype;
    }

    public int getUsertype() {
        return this.priority;
    }

    public void attachTo(Intent intent) {
        intent.putExtra(name, ordinal());
    }

    public static Priority detachFrom(Intent intent) {
        if (!intent.hasExtra(name)) throw new IllegalStateException();
        return values()[intent.getIntExtra(name, -1)];
    }

    @Override
    public String toString() {
        String str = "";
        if (priority == 1)
            str = "الویت زیاد";
        if (priority == 2)
            str = "الویت متوسط";
        if (priority == 3)
            str = "الویت کم";
        return str;
    }
}
