package com.obiapp.models;

import java.io.Serializable;

public class SliderModel implements Serializable {

    private Data data;

    public Data getData() {
        return data;
    }

    private class Data{
        private int id;
        private String title;
        private String desc;
        private String image;

        public int getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public String getDesc() {
            return desc;
        }

        public String getImage() {
            return image;
        }
    }

}
