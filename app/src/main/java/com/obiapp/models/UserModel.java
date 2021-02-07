package com.obiapp.models;

import java.io.Serializable;
import java.util.List;

public class UserModel extends StatusResponse implements Serializable {

    private Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public static class Data implements Serializable {
       private int id;
       private String user_type;
       private String name;
       private String email;
       private String phone_code;
       private String phone;
       private String  whatsapp_number;
       private String logo;
       private String banner;
       private String  address;
       private double latitude;
       private double longitude;
       private String  city_id;
       private String notification_status;
       private String is_confirmed;
       private String phone_is_shown;
       private String is_block;
       private String is_login;
       private String forget_password_code;
       private String software_type;
       private String email_verified_at;
       private String deleted_at;
       private String created_at;
       private String updated_at;
       private String token;
       private String firebaseToken="";
       private List<ProductModel> products;
       private String follow_status;
       private int user_paids_count;
       private long logout_time;

        public int getId() {
            return id;
        }

        public String getUser_type() {
            return user_type;
        }

        public String getName() {
            return name;
        }

        public String getEmail() {
            return email;
        }

        public String getPhone_code() {
            return phone_code;
        }

        public String getPhone() {
            return phone;
        }

        public String getWhatsapp_number() {
            return whatsapp_number;
        }

        public String getLogo() {
            return logo;
        }

        public String getBanner() {
            return banner;
        }

        public String getAddress() {
            return address;
        }

        public double getLatitude() {
            return latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public String getCity_id() {
            return city_id;
        }

        public String getNotification_status() {
            return notification_status;
        }

        public String getIs_confirmed() {
            return is_confirmed;
        }

        public String getPhone_is_shown() {
            return phone_is_shown;
        }

        public String getIs_block() {
            return is_block;
        }

        public String getIs_login() {
            return is_login;
        }

        public long getLogout_time() {
            return logout_time;
        }

        public String getForget_password_code() {
            return forget_password_code;
        }

        public String getSoftware_type() {
            return software_type;
        }

        public String getEmail_verified_at() {
            return email_verified_at;
        }

        public String getDeleted_at() {
            return deleted_at;
        }

        public String getCreated_at() {
            return created_at;
        }

        public String getUpdated_at() {
            return updated_at;
        }

        public String getToken() {
            return token;
        }

        public String getFirebaseToken() {
            return firebaseToken;
        }

        public void setFirebaseToken(String firebaseToken) {
            this.firebaseToken = firebaseToken;
        }

        public List<ProductModel> getProducts() {
            return products;
        }

        public String getFollow_status() {
            return follow_status;
        }

        public void setFollow_status(String follow_status) {
            this.follow_status = follow_status;
        }

        public int getUser_paids_count() {
            return user_paids_count;
        }
    }
}
