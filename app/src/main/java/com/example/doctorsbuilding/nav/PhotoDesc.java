package com.example.doctorsbuilding.nav;

import android.graphics.Bitmap;

/**
 * Created by hossein on 9/11/2016.
 */
public class PhotoDesc {
    public int id;
    public Bitmap photo;
    public String description;
    public String date;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Bitmap getPhoto() {
        return photo;
    }

    public void setPhoto(Bitmap photo) {
        this.photo = photo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
