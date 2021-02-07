package com.obiapp.models;

import java.io.Serializable;
import java.util.List;

public class NotificationDataModel extends StatusResponse implements Serializable {

    private List<NotificationModel> data;

    public List<NotificationModel> getData() {
        return data;
    }


}
