package com.obiapp.models;

import java.io.Serializable;
import java.util.List;

public class ProductModel implements Serializable {

   private int id;
   private String title;
   private String desc;
   private String main_image;
   private String video;
   private int user_id;
   private int category_id;
   private int sub_category_id;
   private int price;
   private int old_price;
   private String address;
   private double latitude;
   private double longitude;
   private String have_offer;
   private String offer_type;
   private int offer_value;
   private String offer_started_at;
   private String offer_finished_at;
   private int rating_value;
   private String is_shown;
   private String created_at;
   private String updated_at;
   private int time_in_days_from_creating;
   private String is_favorite;
   private String is_report;
   private UserModel.Data user;
   private Category category;
   private SubCategory sub_category;
   private List<ProductImageModel> product_images;
   private List<ProductDetail> product_details;
   private List<ProductType> product_types;

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDesc() {
        return desc;
    }

    public String getMain_image() {
        return main_image;
    }

    public String getVideo() {
        return video;
    }

    public int getUser_id() {
        return user_id;
    }

    public int getCategory_id() {
        return category_id;
    }

    public int getSub_category_id() {
        return sub_category_id;
    }

    public int getPrice() {
        return price;
    }

    public int getOld_price() {
        return old_price;
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

    public String getHave_offer() {
        return have_offer;
    }

    public String getOffer_type() {
        return offer_type;
    }

    public int getOffer_value() {
        return offer_value;
    }

    public String getOffer_started_at() {
        return offer_started_at;
    }

    public String getOffer_finished_at() {
        return offer_finished_at;
    }

    public int getRating_value() {
        return rating_value;
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

    public int getTime_in_days_from_creating() {
        return time_in_days_from_creating;
    }

    public UserModel.Data getUser() {
        return user;
    }

    public Category getCategory() {
        return category;
    }

    public SubCategory getSub_category() {
        return sub_category;
    }

    public List<ProductImageModel> getProduct_images() {
        return product_images;
    }

    public List<ProductDetail> getProduct_details() {
        return product_details;
    }

    public List<ProductType> getProduct_types() {
        return product_types;
    }

    public String getIs_favorite() {
        return is_favorite;
    }

    public String getIs_report() {
        return is_report;
    }

    public void setIs_favorite(String is_favorite) {
        this.is_favorite = is_favorite;
    }

    public void setIs_report(String is_report) {
        this.is_report = is_report;
    }

    public static class Category implements Serializable
    {
        private int id;
        private String title;
        private String desc;
        private String image;
        private int parent_id;
        private String level;
        private String is_shown;
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

        public int getParent_id() {
            return parent_id;
        }

        public String getLevel() {
            return level;
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


    }

    public static class SubCategory implements Serializable
    {
        private int id;
        private String title;
        private String desc;
        private String image;
        private int parent_id;
        private String level;
        private String is_shown;
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

        public int getParent_id() {
            return parent_id;
        }

        public String getLevel() {
            return level;
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
    }

    public static class ProductDetail implements Serializable
    {
        private int id;
        private String icon;
        private String title;
        private String value;
        private int product_id;
        private String created_at;
        private String updated_at;

        public int getId() {
            return id;
        }

        public String getIcon() {
            return icon;
        }

        public String getTitle() {
            return title;
        }

        public String getValue() {
            return value;
        }

        public int getProduct_id() {
            return product_id;
        }

        public String getCreated_at() {
            return created_at;
        }

        public String getUpdated_at() {
            return updated_at;
        }
    }

    public static class Type implements Serializable
    {
        private int id;
        private String title;
        private String created_at;
        private String updated_at;

        public int getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public String getCreated_at() {
            return created_at;
        }

        public String getUpdated_at() {
            return updated_at;
        }
    }

    public static class ProductType implements Serializable
    {
        private int id;
        private int type_id;
        private int product_id;
        private String created_at;
        private String updated_at;
        private Type type;

        public int getId() {
            return id;
        }

        public int getType_id() {
            return type_id;
        }

        public int getProduct_id() {
            return product_id;
        }

        public String getCreated_at() {
            return created_at;
        }

        public String getUpdated_at() {
            return updated_at;
        }

        public Type getType() {
            return type;
        }
    }


}
