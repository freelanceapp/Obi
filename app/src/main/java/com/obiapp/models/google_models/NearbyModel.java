package com.obiapp.models.google_models;

import java.io.Serializable;
import java.util.List;

public class NearbyModel implements Serializable {
    private String next_page_token;
    private List<Result> results;
    private String status;

    public String getNext_page_token() {
        return next_page_token;
    }

    public List<Result> getResults() {
        return results;
    }

    public String getStatus() {
        return status;
    }

    public static class Result implements Serializable {
        private String id;
        private String icon;
        private String name;
        private String place_id;
        private double rating=0.0;
        private String vicinity;
        private List<Photo> photos;
        private Geometry geometry;
        private List<String> types;
        private double distance =0.0;
        private boolean isOpen = false;
        private PlaceDetailsModel.Opening_Hours work_hours;
        private List<PhotosModel> photosModels;
        private List<PlaceDetailsModel.Reviews> reviews;

        public String getId() {
            return id;
        }

        public String getIcon() {
            return icon;
        }

        public String getName() {
            return name;
        }

        public String getPlace_id() {
            return place_id;
        }

        public double getRating() {
            return rating;
        }

        public String getVicinity() {
            return vicinity;
        }

        public List<Photo> getPhotos() {
            return photos;
        }

        public Geometry getGeometry() {
            return geometry;
        }

        public double getDistance() {
            return distance;
        }

        public void setDistance(double distance) {
            this.distance = distance;
        }

        public boolean isOpen() {
            return isOpen;
        }

        public void setOpen(boolean open) {
            isOpen = open;
        }



        public PlaceDetailsModel.Opening_Hours getWork_hours() {
            return work_hours;
        }

        public void setWork_hours(PlaceDetailsModel.Opening_Hours work_hours) {
            this.work_hours = work_hours;
        }

        public List<PhotosModel> getPhotosModels() {
            return photosModels;
        }

        public void setPhotosModels(List<PhotosModel> photosModels) {
            this.photosModels = photosModels;
        }

        public List<PlaceDetailsModel.Reviews> getReviews() {
            return reviews;
        }

        public void setReviews(List<PlaceDetailsModel.Reviews> reviews) {
            this.reviews = reviews;
        }

        public List<String> getTypes() {
            return types;
        }


    }

    public static class Photo implements Serializable {
        private String photo_reference;

        public String getPhoto_reference() {
            return photo_reference;
        }
    }

    public static class Geometry implements Serializable {
        private Location location;

        public Location getLocation() {
            return location;
        }
    }

    public static class Location implements Serializable {
        private double lat;
        private double lng;

        public double getLat() {
            return lat;
        }

        public double getLng() {
            return lng;
        }
    }
}
