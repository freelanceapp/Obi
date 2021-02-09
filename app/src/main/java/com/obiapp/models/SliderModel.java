package com.obiapp.models;

import java.io.Serializable;

public class SliderModel implements Serializable {

    public Data data;

    public Data getData() {
        return data;
    }

    public class Data{
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
