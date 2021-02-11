package com.obiapp.models.google_models;

import java.io.Serializable;
import java.util.List;

public class PlaceDetailsModel implements Serializable {

    private PlaceDetails result;

    public PlaceDetails getResult() {
        return result;
    }

    public class PlaceDetails implements Serializable
    {
        private Opening_Hours opening_hours;
        private List<PhotosModel> photos;
        private List<Reviews> reviews;
        public Opening_Hours getOpening_hours() {
            return opening_hours;
        }

        public List<PhotosModel> getPhotos() {
            return photos;
        }

        public List<Reviews> getReviews() {
            return reviews;
        }
    }

    public class Opening_Hours implements Serializable
    {

        private boolean open_now = false;
        private List<Periods> periods;
        private List<String> weekday_text;
        public List<Periods> getPeriods() {
            return periods;
        }

        public List<String> getWeekday_text() {
            return weekday_text;
        }

        public boolean isOpen_now() {
            return open_now;
        }
    }

    public class Periods implements Serializable
    {
        private Open open;

        public Open getOpen() {
            return open;
        }
    }

    public class Open implements Serializable
    {
        private String time;

        public String getTime() {
            return time;
        }
    }



    public class Reviews implements Serializable
    {
        private String author_name;
        private String profile_photo_url;
        private String relative_time_description;
        private double rating;
        private String text;

        public String getAuthor_name() {
            return author_name;
        }

        public String getProfile_photo_url() {
            return profile_photo_url;
        }

        public String getRelative_time_description() {
            return relative_time_description;
        }

        public double getRating() {
            return rating;
        }

        public String getText() {
            return text;
        }
    }
}
