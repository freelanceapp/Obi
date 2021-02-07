package com.obiapp.models;

import java.io.Serializable;

public class SettingDataModel extends StatusResponse implements Serializable {

    private Setting data;

    public Setting getData() {
        return data;
    }

    public static class Setting implements Serializable {
        private String phone1;
        private String email1;
        private String facebook;
        private String twitter;
        private String instagram;
        private String whatsapp;

        public String getPhone1() {
            return phone1;
        }

        public String getEmail1() {
            return email1;
        }

        public String getFacebook() {
            return facebook;
        }

        public String getTwitter() {
            return twitter;
        }

        public String getInstagram() {
            return instagram;
        }

        public String getWhatsapp() {
            return whatsapp;
        }
    }
}
