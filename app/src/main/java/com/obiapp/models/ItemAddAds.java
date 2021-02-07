package com.obiapp.models;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.google.gson.annotations.SerializedName;
import com.obiapp.BR;

import java.io.Serializable;

public class ItemAddAds extends BaseObservable implements Serializable {
    private String id;
    @SerializedName("title")
    private String title_of_attribute;
    private String value_of_attribute;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Bindable
    public String getTitle_of_attribute() {
        return title_of_attribute;
    }

    public void setTitle_of_attribute(String title_of_attribute) {
        this.title_of_attribute = title_of_attribute;
        notifyPropertyChanged(BR.title_of_attribute);
    }

    @Bindable
    public String getValue_of_attribute() {
        return value_of_attribute;
    }

    public void setValue_of_attribute(String value_of_attribute) {
        this.value_of_attribute = value_of_attribute;
        notifyPropertyChanged(BR.value_of_attribute);

    }


}
