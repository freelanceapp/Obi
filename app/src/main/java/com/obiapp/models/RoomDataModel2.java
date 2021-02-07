package com.obiapp.models;

import java.io.Serializable;

public class RoomDataModel2 extends StatusResponse implements Serializable {
    private MessageModel.RoomModel data;

    public MessageModel.RoomModel getData() {
        return data;
    }
}
