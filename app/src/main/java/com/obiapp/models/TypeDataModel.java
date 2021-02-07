package com.obiapp.models;

import java.io.Serializable;
import java.util.List;

public class TypeDataModel extends StatusResponse implements Serializable {
    private List<TypeModel> date;

    public List<TypeModel> getData() {
        return date;
    }
}
