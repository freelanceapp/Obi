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
    private int sub_category_id;
    private String name;
    private String price;
    private String old_price;
    private String have_offer;
    private String offer_value;
    private String details;
    private String address;
    private double lat;
    private double lng;
    private boolean hasExtraItems;
    private boolean swear = false;
    private String video_url;
    private List<Integer> types;
    private List<String> imagesList;
    private List<ItemAddAds> itemAddAdsList;


    public ObservableField<String> error_name = new ObservableField<>();
    public ObservableField<String> error_price = new ObservableField<>();
    public ObservableField<String> error_old_price = new ObservableField<>();
    public ObservableField<String> error_offer_value = new ObservableField<>();
    public ObservableField<String> error_details = new ObservableField<>();
    public ObservableField<String> error_address = new ObservableField<>();


    public boolean isDataValid(Context context)
    {

        if (category_id!=0&&
                sub_category_id!=0&&
                !name.isEmpty() &&
                !old_price.isEmpty() &&
                !details.isEmpty() &&
                !address.isEmpty() &&
                imagesList.size()>0&&
                imagesList.size() <= 5&&
                swear

        ) {

            error_name.set(null);
            error_old_price.set(null);
            error_details.set(null);
            error_address.set(null);
            if (hasExtraItems){
                if (itemAddAdsList.size()>0){

                    if (isDataItemValid(context)){
                        if (have_offer.equals("with_offer")){

                            if (!price.isEmpty()&&!offer_value.isEmpty()&&Integer.parseInt(offer_value)>0){
                                error_offer_value.set(null);
                                error_price.set(null);
                                return true;

                            }else {

                                if (price.isEmpty()){
                                    error_price.set(context.getString(R.string.field_required));

                                }else {
                                    error_price.set(null);

                                }

                                if (offer_value.isEmpty()){
                                    error_offer_value.set(context.getString(R.string.field_required));

                                }else if (Integer.parseInt(offer_value)==0){
                                    error_offer_value.set(context.getString(R.string.inv_value));
                                }else {
                                    error_offer_value.set(null);

                                }


                                return false;

                            }
                        }else {
                            return true;

                        }

                    }else {
                        return false;
                    }
                }else {
                    return false;
                }
            }
            else {

                if (have_offer.equals("with_offer")){

                    if (!price.isEmpty()&&!offer_value.isEmpty()){
                        error_offer_value.set(null);
                        error_price.set(null);
                        return true;

                    }else {

                        if (price.isEmpty()){
                            error_price.set(context.getString(R.string.field_required));

                        }else {
                            error_price.set(null);

                        }

                        if (offer_value.isEmpty()){
                            error_offer_value.set(context.getString(R.string.field_required));

                        }else if (Integer.parseInt(offer_value)==0){
                            error_offer_value.set(context.getString(R.string.inv_value));
                        }else {
                            error_offer_value.set(null);

                        }


                        return false;

                    }
                }else {
                    return true;

                }


            }

        } else {

            if (name.isEmpty()){
                error_name.set(context.getString(R.string.field_required));

            }else {
                error_name.set(null);

            }

            if (old_price.isEmpty()){
                error_old_price.set(context.getString(R.string.field_required));

            }else {
                error_old_price.set(null);

            }

            if (details.isEmpty()){
                error_details.set(context.getString(R.string.field_required));

            }else {
                error_details.set(null);

            }

            if (address.isEmpty()){
                error_address.set(context.getString(R.string.field_required));

            }else {
                error_address.set(null);

            }

            if (imagesList.size()==0){
                Toast.makeText(context, R.string.ch_ad_image, Toast.LENGTH_SHORT).show();
            }

            if (category_id==0){
                Toast.makeText(context, R.string.ch_dept, Toast.LENGTH_SHORT).show();

            }

            if (sub_category_id==0){
                Toast.makeText(context, R.string.ch_sub_dept, Toast.LENGTH_SHORT).show();

            }


            if (have_offer.equals("with_offer")){
                if (price.isEmpty()){
                    error_price.set(context.getString(R.string.field_required));

                }else {
                    error_price.set(null);

                }

                if (offer_value.isEmpty()){
                    error_offer_value.set(context.getString(R.string.field_required));

                }else if (Integer.parseInt(offer_value)==0){
                    error_offer_value.set(context.getString(R.string.inv_value));
                }else {
                    error_offer_value.set(null);

                }
            }

            if (!swear){
                Toast.makeText(context, R.string.accept_terms, Toast.LENGTH_SHORT).show();
            }


            if (hasExtraItems&&itemAddAdsList.size()>0){
                if (isDataItemValid(context)){

                }
            }

            return false;
        }
    }


    public AddAdsModel() {
        category_id = 0;
        sub_category_id = 0;
        name = "";
        price = "";
        old_price="";
        details = "";
        address = "";
        lat = 0.0;
        lng = 0.0;
        video_url = "";
        offer_value="";
        have_offer="without_offer";
        imagesList = new ArrayList<>();
        hasExtraItems = false;
        swear = false;
        itemAddAdsList = new ArrayList<>();
        types = new ArrayList<>();
    }


    public int getCategory_id() {
        return category_id;
    }

    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }

    public int getSub_category_id() {
        return sub_category_id;
    }

    public void setSub_category_id(int sub_category_id) {
        this.sub_category_id = sub_category_id;
    }

    public String getOld_price() {
        return old_price;
    }

    public void setOld_price(String old_price) {
        this.old_price = old_price;
    }

    public String getHave_offer() {
        return have_offer;
    }

    public void setHave_offer(String have_offer) {
        this.have_offer = have_offer;
    }

    public String getOffer_value() {
        return offer_value;
    }

    public void setOffer_value(String offer_value) {
        this.offer_value = offer_value;
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

    public boolean isDataItemValid(Context context){
        boolean valid = true;


        for (ItemAddAds itemAddAds:itemAddAdsList){
            if (itemAddAds.getValue_of_attribute().isEmpty()){
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

    public List<Integer> getTypes() {
        return types;
    }

    public void setTypes(List<Integer> types) {
        this.types = types;
    }
}
