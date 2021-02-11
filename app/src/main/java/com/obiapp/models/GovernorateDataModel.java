package com.obiapp.models;

import java.io.Serializable;
import java.util.List;

public class GovernorateDataModel extends StatusResponse implements Serializable {
    private List<GovernorateModel> date;

    public List<GovernorateModel> getData() {
        return date;
    }
}
