package com.obiapp.models;

import java.io.Serializable;
import java.util.List;

public class SliderModel implements Serializable {

    public List<Data> data;
    private int status;

    public List<Data> getData() {
        return data;
    }

    public int getStatus() {
        return status;
    }

    public class Data {
        private int id;
        private String title;
        private String desc;
        private String image;
        private String action_link;
        private String action_link_title;
        private String type;
        private String created_at;
        private String updated_at;

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

        public String getAction_link() {
            return action_link;
        }

        public String getAction_link_title() {
            return action_link_title;
        }

        public String getType() {
            return type;
        }

        public String getCreated_at() {
            return created_at;
        }

        public String getUpdated_at() {
            return updated_at;
        }
    }

}
