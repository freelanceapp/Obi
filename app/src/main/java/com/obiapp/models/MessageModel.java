package com.obiapp.models;

import java.io.Serializable;

public class MessageModel implements Serializable {

    private int id;
    private int chat_room_id;
    private int from_user_id;
    private int to_user_id;
    private String message_kind;
    private String message;
    private String file_link;
    private long date;
    private String is_read;
    private RoomModel room;
    private UserModel.Data from_user;
    private UserModel.Data to_user;


    public MessageModel(int id, int chat_room_id, int from_user_id, int to_user_id, String message_kind, String message, String file_link, long date, RoomModel room, UserModel.Data from_user, UserModel.Data to_user) {
        this.id = id;
        this.chat_room_id = chat_room_id;
        this.from_user_id = from_user_id;
        this.to_user_id = to_user_id;
        this.message_kind = message_kind;
        this.message = message;
        this.file_link = file_link;
        this.date = date;
        this.room = room;
        this.from_user = from_user;
        this.to_user = to_user;
    }

    public int getId() {
        return id;
    }

    public int getChat_room_id() {
        return chat_room_id;
    }

    public int getFrom_user_id() {
        return from_user_id;
    }

    public int getTo_user_id() {
        return to_user_id;
    }

    public String getMessage_kind() {
        return message_kind;
    }

    public String getMessage() {
        return message;
    }

    public String getFile_link() {
        return file_link;
    }

    public long getDate() {
        return date;
    }

    public String getIs_read() {
        return is_read;
    }

    public RoomModel getRoom() {
        return room;
    }

    public UserModel.Data getFrom_user() {
        return from_user;
    }

    public UserModel.Data getTo_user() {
        return to_user;
    }

    public static class RoomModel implements Serializable{
        private int id;
        private int first_user_id;
        private int second_user_id;
        public String is_approved;


        public RoomModel() {
        }

        public RoomModel(int id, int first_user_id, int second_user_id) {
            this.id = id;
            this.first_user_id = first_user_id;
            this.second_user_id = second_user_id;
        }

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


    }

}
