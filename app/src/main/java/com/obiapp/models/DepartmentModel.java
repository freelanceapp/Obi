package com.obiapp.models;

import java.io.Serializable;
import java.util.List;

public class DepartmentModel implements Serializable {

    private int id;
    private String title;
    private String desc;
    private String image;
    private String parent_id;
    private String level;
    private String is_shown;
    private String created_at;
    private String updated_at;
    private List<SubCategory> sub_categories;




    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public String getImage() {
        return image;
    }

    public String getParent_id() {
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

    public List<SubCategory> getSub_categories() {
        return sub_categories;
    }

    public void setSub_categories(List<SubCategory> sub_categories) {
        this.sub_categories = sub_categories;
    }

    public static class CategoryDetail implements Serializable {
        private int id;
        private String title;
        private String desc;
        private int category_id;
        private int sub_category_id;
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

        public int getCategory_id() {
            return category_id;
        }

        public int getSub_category_id() {
            return sub_category_id;
        }

        public String getCreated_at() {
            return created_at;
        }

        public String getUpdated_at() {
            return updated_at;
        }
    }

    public static class SubCategory implements Serializable {
        private int id;
        private String title;
        private String desc;
        private String image;
        private int parent_id;
        private String level;
        private String is_shown;
        private String created_at;
        private String updated_at;
        private List<CategoryDetail> category_details;
        private boolean isSelected=false;


        public void setId(int id) {
            this.id = id;
        }

        public void setTitle(String title) {
            this.title = title;
        }

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

        public List<CategoryDetail> getCategory_details() {
            return category_details;
        }

        public boolean isSelected() {
            return isSelected;
        }

        public void setSelected(boolean selected) {
            isSelected = selected;
        }
    }

}
