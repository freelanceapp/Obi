package com.obiapp.models.google_models;

import java.io.Serializable;
import java.util.List;

public class CategoryDataModel implements Serializable {
    private List<CategoryModel> date;

    public List<CategoryModel> getGoogle_categories() {
        return date;
    }
}
