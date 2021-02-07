package com.obiapp.models;

import java.io.Serializable;
import java.util.List;

public class CouponDataModel extends StatusResponse implements Serializable {
    private List<CouponModel> data;

    public List<CouponModel> getData() {
        return data;
    }
}
