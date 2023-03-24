package com.itstep.fragmentandactivity.model;

import android.graphics.Bitmap;

public class FlagInfoModel {
    String countryName;
    String path;
    Bitmap bitmap;

    public FlagInfoModel(String countryName, String path, Bitmap bitmap) {
        this.countryName = countryName;
        this.path = path;
        this.bitmap = bitmap;
    }

    public String getCountryName() {
        return countryName;
    }

    public String getPath() {
        return path;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }
}
