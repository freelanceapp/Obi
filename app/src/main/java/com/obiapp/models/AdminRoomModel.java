package com.obiapp.models;

import java.io.Serializable;

public class AdminRoomModel implements Serializable {
    private int user_id;
    private int admin_id;
    private int id;
    private int my_message_unread_count;
    private String last_message_type;
    private String last_message;
    private String last_message_date;

    public int getUser_id() {
        return user_id;
    }

    public int getAdmin_id() {
        return admin_id;
    }

    public int getId() {
        return id;
    }

    public int getMy_message_unread_count() {
        return my_message_unread_count;
    }

    public String getLast_message_type() {
        return last_message_type;
    }

    public String getLast_message() {
        return last_message;
    }

    public String getLast_message_date() {
        return last_message_date;
    }
}
