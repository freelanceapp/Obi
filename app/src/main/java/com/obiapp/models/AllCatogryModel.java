package com.obiapp.models;

import java.io.Serializable;
import java.util.List;

public class AllCatogryModel implements Serializable {
    private List<SingleCategoryModel> data;
private int status;
    public List<SingleCategoryModel> getData() {
        return data;
    }

    public int getStatus() {
        return status;
    }
}