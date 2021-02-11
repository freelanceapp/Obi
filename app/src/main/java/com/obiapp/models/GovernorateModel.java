package com.obiapp.models;

import java.io.Serializable;

public class GovernorateModel implements Serializable {
   private int id;
   private String governorate_name;
   private String governorate_name_en;


    public GovernorateModel(int id, String governorate_name, String governorate_name_en) {
        this.id = id;
        this.governorate_name = governorate_name;
        this.governorate_name_en = governorate_name_en;
    }

    public int getId() {
        return id;
    }

    public String getGovernorate_name() {
        return governorate_name;
    }

    public String getGovernorate_name_en() {
        return governorate_name_en;
    }
}
