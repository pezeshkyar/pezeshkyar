package com.example.doctorsbuilding.nav;

import android.graphics.drawable.Drawable;

/**
 * Created by hossein on 12/10/2016.
 */
public class NavMenuItem {
    int id;
    private int icon;
    private String title;
    private String item_name;

    public NavMenuItem() {
    }

    public NavMenuItem(String title, int icon) {
        this.title = title;
        this.icon = icon;
    }

    public int getId() {

        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String text) {
        this.title = text;
    }
}
