package com.widevision.pillreminder.dao;

import android.graphics.Bitmap;

/**
 * Created by mercury-one on 21/12/15.
 */
public class Drug {
    String name;
    Bitmap image;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }
}
