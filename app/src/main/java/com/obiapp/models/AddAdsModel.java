package com.obiapp.models;

import android.content.Context;
import android.widget.Toast;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.ObservableField;

import com.obiapp.BR;
import com.obiapp.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AddAdsModel extends BaseObservable implements Serializable {
    private int category_id;
    private int governate_id;
    private int type_id;
    private String name;
    private String price;
    private String details;
    private String address;
    private double lat;
    private double lng;
    private boolean hasExtraItems;
    private boolean swear = false;
    private String video_url;
    private List<String> imagesList;
    private List<ItemAddAds> itemAddAdsList;


    public ObservableField<String> error_name = new ObservableField<>();
    public ObservableField<String> error_price = new ObservableField<>();
    public ObservableField<String> error_details = new ObservableField<>();
    public ObservableField<String> error_address = new ObservableField<>();


    public boolean isDataValid(Context context) {

        if (category_id != 0 &&
                governate_id != 0 &&
                type_id != 0 &&
                !name.isEmpty() &&
                !price.isEmpty() &&
                !details.isEmpty() &&
                !address.isEmpty() &&
                imagesList.size() > 0 &&
                imagesList.size() <= 5 &&
                swear

        ) {

            error_name.set(null);
            error_price.set(null);
            error_details.set(null);
            error_address.set(null);
            if (hasExtraItems) {
                if (itemAddAdsList.size() > 0) {

                    if (isDataItemValid(context)) {
                        return true;


                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            } else {

                return true;


            }

        } else {

            if (name.isEmpty()) {
                error_name.set(context.getString(R.string.field_required));

            } else {
                error_name.set(null);

            }


            if (details.isEmpty()) {
                error_details.set(context.getString(R.string.field_required));

            } else {
                error_details.set(null);

            }

            if (address.isEmpty()) {
                error_address.set(context.getString(R.string.field_required));

            } else {
                error_address.set(null);

            }

            if (imagesList.size() == 0) {
                Toast.makeText(context, R.string.ch_ad_image, Toast.LENGTH_SHORT).show();
            }

            if (category_id == 0) {
                Toast.makeText(context, R.string.ch_dept, Toast.LENGTH_SHORT).show();

            }

            if (governate_id == 0) {
                Toast.makeText(context, R.string.ch_governorate, Toast.LENGTH_SHORT).show();

            }

            if (type_id == 0) {
                Toast.makeText(context, R.string.ch_type, Toast.LENGTH_SHORT).show();

            }

            if (!swear) {
                Toast.makeText(context, R.string.accept_terms, Toast.LENGTH_SHORT).show();
            }

            if (price.isEmpty()) {
                error_price.set(context.getString(R.string.field_required));

            } else {
                error_price.set(null);

            }

            if (hasExtraItems && itemAddAdsList.size() > 0) {
                if (isDataItemValid(context)) {

                }
            }

            return false;
        }
    }


    public AddAdsModel() {
        category_id = 0;
        governate_id = 0;
        type_id = 0;
        name = "";
        price = "";
        details = "";
        address = "";
        lat = 0.0;
        lng = 0.0;
        video_url = "";
        imagesList = new ArrayList<>();
        hasExtraItems = false;
        swear = false;
        itemAddAdsList = new ArrayList<>();
    }


    public int getCategory_id() {
        return category_id;
    }

    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }

    public int getGovernate_id() {
        return governate_id;
    }

    public void setGovernate_id(int governate_id) {
        this.governate_id = governate_id;
    }


    @Bindable
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        notifyPropertyChanged(BR.name);
    }

    @Bindable
    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
        notifyPropertyChanged(BR.price);

    }

    @Bindable
    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
        notifyPropertyChanged(BR.details);

    }

    @Bindable
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
        notifyPropertyChanged(BR.address);

    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public boolean isHasExtraItems() {
        return hasExtraItems;
    }

    public void setHasExtraItems(boolean hasExtraItems) {
        this.hasExtraItems = hasExtraItems;
    }

    public String getVideo_url() {
        return video_url;
    }

    public void setVideo_url(String video_url) {
        this.video_url = video_url;
    }

    public List<String> getImagesList() {
        return imagesList;
    }

    public void setImagesList(List<String> imagesList) {
        this.imagesList = imagesList;
    }

    public List<ItemAddAds> getItemAddAdsList() {
        return itemAddAdsList;
    }

    public void setItemAddAdsList(List<ItemAddAds> itemAddAdsList) {
        this.itemAddAdsList = itemAddAdsList;
    }

    public boolean isDataItemValid(Context context) {
        boolean valid = true;


        for (ItemAddAds itemAddAds : itemAddAdsList) {
            if (itemAddAds.getValue_of_attribute().isEmpty()) {
                valid = false;
            }
        }


        return valid;
    }

    public boolean isSwear() {
        return swear;
    }

    public void setSwear(boolean swear) {
        this.swear = swear;
    }

    public int getType_id() {
        return type_id;
    }

    public void setType_id(int type_id) {
        this.type_id = type_id;
    }
}
