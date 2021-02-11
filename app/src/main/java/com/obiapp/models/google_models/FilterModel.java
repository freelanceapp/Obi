package com.obiapp.models.google_models;

import java.io.Serializable;

public class FilterModel implements Serializable {
    private int distance;
    private double rate;
    private String keyword;
    public FilterModel() {
    }

    public FilterModel(int distance, double rate, String keyword) {
        this.distance = distance;
        this.rate = rate;
        this.keyword = keyword;
    }

    public int getDistance() {
        return distance;
    }

    public double getRate() {
        return rate;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
}
