package com.obiapp.models;

import java.io.Serializable;

public class RoomModel implements Serializable {
    private int id;
    private int first_user_id;
    private int second_user_id;
    private String is_approved;
    private String created_at;
    private String updated_at;
    private int chatRooms_count;
    private int my_message_unread_count;
    private String last_message_type;
    private String last_message;
    private long last_message_date;
    private String other_user_name;
    private String other_user_email;
    private String other_user_phone_code;
    private String other_user_phone;
    private String other_user_logo;
    private String note;

    public int getId() {
        return id;
    }

    public int getFirst_user_id() {
        return first_user_id;
    }

    public int getSecond_user_id() {
        return second_user_id;
    }

    public String getIs_approved() {
        return is_approved;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public int getChatRooms_count() {
        return chatRooms_count;
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

    public long getLast_message_date() {
        return last_message_date;
    }

    public String getOther_user_name() {
        return other_user_name;
    }

    public String getOther_user_email() {
        return other_user_email;
    }

    public String getOther_user_phone_code() {
        return other_user_phone_code;
    }

    public String getOther_user_phone() {
        return other_user_phone;
    }

    public String getOther_user_logo() {
        return other_user_logo;
    }

    public String getNote() {
        return note;
    }
}
