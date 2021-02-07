package com.obiapp.models;

import java.io.Serializable;

public class CouponModel implements Serializable {

    private int id;
    private int user_id;
    private String title;
    private String brand_title;
    private String coupon_code;
    private String coupon_image;
    private String offer_type;
    private double offer_value;
    private String from_date;
    private String to_date;
    private String is_shown;
    private String created_at;
    private String updated_at;
    private int likes_count;
    private int dislikes_count;
    private LikeAction like_action;
    private UserModel.Data user;


    public int getId() {
        return id;
    }

    public int getUser_id() {
        return user_id;
    }

    public String getTitle() {
        return title;
    }

    public String getBrand_title() {
        return brand_title;
    }

    public String getCoupon_code() {
        return coupon_code;
    }

    public String getCoupon_image() {
        return coupon_image;
    }

    public String getOffer_type() {
        return offer_type;
    }

    public double getOffer_value() {
        return offer_value;
    }

    public String getFrom_date() {
        return from_date;
    }

    public String getTo_date() {
        return to_date;
    }

    public String getIs_shown() {
        return is_shown;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public int getLikes_count() {
        return likes_count;
    }

    public int getDislikes_count() {
        return dislikes_count;
    }

    public void setLikes_count(int likes_count) {
        this.likes_count = likes_count;
    }

    public void setDislikes_count(int dislikes_count) {
        this.dislikes_count = dislikes_count;
    }

    public void setLike_action(LikeAction like_action) {
        this.like_action = like_action;
    }

    public LikeAction getLike_action() {
        return like_action;
    }

    public UserModel.Data getUser() {
        return user;
    }

    public static class LikeAction implements Serializable {
        private int id;
        private int user_id;
        private int coupon_id;
        private String type;
        private String created_at;
        private String updated_at;

        public int getId() {
            return id;
        }

        public int getUser_id() {
            return user_id;
        }

        public int getCoupon_id() {
            return coupon_id;
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

        public void setType(String type) {
            this.type = type;
        }
    }
}
