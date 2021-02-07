package com.obiapp.models;

import java.io.Serializable;

public class NotificationModel implements Serializable {
    private int id;
    private String title;
    private String message;
    private int from_user_id;
    private int to_user_id;
    private int notification_date;
    private String is_read;

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public int getFrom_user_id() {
        return from_user_id;
    }

    public int getTo_user_id() {
        return to_user_id;
    }

    public int getNotification_date() {
        return notification_date;
    }

    public String getIs_read() {
        return is_read;
    }
}
