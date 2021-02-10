package com.obiapp.models;

import java.io.Serializable;
import java.util.List;

public class ProductsDataModel extends StatusResponse implements Serializable {
    private List<ProductModel> data;
    private int status;

    public List<ProductModel> getData() {
        return data;
    }


    public int getStatus() {
        return status;
    }
}
