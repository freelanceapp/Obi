package com.obiapp.models;

import android.content.Context;
import android.widget.Toast;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.ObservableField;

import com.obiapp.BR;
import com.obiapp.R;


public class AddCouponModel extends BaseObservable {
    private String name;
    private String brand_name;
    private String code;
    private String offer_value;
    private String date;
    private String image_url;
    public ObservableField<String> error_name = new ObservableField<>();
    public ObservableField<String> error_brand_name = new ObservableField<>();
    public ObservableField<String> error_code = new ObservableField<>();
    public ObservableField<String> error_offer_value = new ObservableField<>();
    public ObservableField<String> error_date = new ObservableField<>();


    public AddCouponModel() {
        name = "";
        brand_name = "";
        code = "";
        offer_value = "";
        date = "";
        image_url = null;


    }

    public boolean isDataValid(Context context) {

        if (!name.isEmpty() &&
                !brand_name.isEmpty()&&
                !code.isEmpty()&&
                !offer_value.isEmpty()&&
                !date.isEmpty()&&
                image_url!=null
        ) {
            error_name.set(null);
            error_brand_name.set(null);
            error_code.set(null);
            error_date.set(null);
            error_offer_value.set(null);
            return true;
        } else {
            if (name.isEmpty()){
                error_name.set(context.getString(R.string.field_required));

            }else {
                error_name.set(null);

            }

            if (brand_name.isEmpty()){
                error_brand_name.set(context.getString(R.string.field_required));

            }else {
                error_brand_name.set(null);

            }

            if (code.isEmpty()){
                error_code.set(context.getString(R.string.field_required));

            }else {
                error_code.set(null);

            }

            if (offer_value.isEmpty()){
                error_offer_value.set(context.getString(R.string.field_required));

            }else {
                error_offer_value.set(null);

            }

            if (date.isEmpty()){
                error_date.set(context.getString(R.string.field_required));

            }else {
                error_date.set(null);

            }


            if (image_url==null){
                Toast.makeText(context, R.string.ch_image, Toast.LENGTH_SHORT).show();
            }

            return false;
        }
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
    public String getBrand_name() {
        return brand_name;
    }

    public void setBrand_name(String brand_name) {
        this.brand_name = brand_name;
        notifyPropertyChanged(BR.brand_name);

    }

    @Bindable
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
        notifyPropertyChanged(BR.code);

    }

    @Bindable
    public String getOffer_value() {
        return offer_value;
    }

    public void setOffer_value(String offer_value) {
        this.offer_value = offer_value;
        notifyPropertyChanged(BR.offer_value);

    }

    @Bindable
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
        notifyPropertyChanged(BR.date);

    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }
}
