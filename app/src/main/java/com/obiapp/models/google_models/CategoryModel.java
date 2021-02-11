package com.obiapp.models.google_models;

import java.io.Serializable;

public class CategoryModel implements Serializable {
    private String id;
    private String ar_title;
    private String en_title;
    private String keyword_google;
    private String image;
    private String type;

    public String getId() {
        return id;
    }

    public String getAr_title() {
        return ar_title;
    }

    public String getEn_title() {
        return en_title;
    }

    public String getKeyword_google() {
        return keyword_google;
    }

    public String getImage() {
        return image;
    }

    public String getType() {
        return type;
    }
}
