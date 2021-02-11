package com.obiapp.models;

import java.io.Serializable;
import java.util.List;

public class AdminMessageDataModel extends StatusResponse implements Serializable {
    private Data data;

    public Data getData() {
        return data;
    }

    public static class Data implements Serializable {
        private MessageModel.RoomModel room;
        private List<AdminMessageModel> messages;

        public List<AdminMessageModel> getMessages() {
            return messages;
        }

        public MessageModel.RoomModel getRoom() {
            return room;
        }
    }

}
