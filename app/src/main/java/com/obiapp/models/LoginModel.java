package com.obiapp.models;

import android.content.Context;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.ObservableField;

import com.obiapp.BR;
import com.obiapp.R;


public class LoginModel extends BaseObservable {
    private String phone;
    private String phone_code;
    public ObservableField<String> error_phone = new ObservableField<>();


    public boolean isDataValid(Context context) {
        if (!phone.trim().isEmpty()) {
            error_phone.set(null);
            return true;
        } else {
            error_phone.set(context.getString(R.string.field_required));
            return false;
        }
    }

    public LoginModel() {
        setPhone_code("+20");
        setPhone("");
    }

    @Bindable
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
        notifyPropertyChanged(BR.phone);
    }

    public String getPhone_code() {
        return phone_code;
    }

    public void setPhone_code(String phone_code) {
        this.phone_code = phone_code;
    }
}
