package com.obiapp.models;

import java.io.Serializable;

public class TypeModel implements Serializable {
   private int id;
   private String title;
   private String created_at;
   private String updated_at;
   private boolean isSelected = false;

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
