package com.obiapp.models.google_models;

import java.io.Serializable;

public class HourModel implements Serializable {
    private String day;
    private String time;

    public HourModel(String day, String time) {
        this.day = day;
        this.time = time;
    }

    public String getDay() {
        return day;
    }

    public String getTime() {
        return time;
    }
}
