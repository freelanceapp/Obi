package com.obiapp.activities_fragments.activity_home.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;


import com.obiapp.R;
import com.obiapp.activities_fragments.activity_add_ads.AddAdsActivity;
import com.obiapp.activities_fragments.activity_chat_us.ChatUsActivity;
import com.obiapp.activities_fragments.activity_commission.CommissionActivity;
import com.obiapp.activities_fragments.activity_home.HomeActivity;
import com.obiapp.activities_fragments.activity_my_ads.MyAdsActivity;
import com.obiapp.activities_fragments.activity_my_favorite.MyFavoriteActivity;
import com.obiapp.activities_fragments.activity_setting.SettingActivity;
import com.obiapp.activities_fragments.activity_sign_up.SignUpActivity;
import com.obiapp.activity_contact_us.ContactUsActivity;
import com.obiapp.databinding.FragmentProfileBinding;
import com.obiapp.interfaces.Listeners;
import com.obiapp.models.UserModel;
import com.obiapp.preferences.Preferences;

import io.paperdb.Paper;

public class Fragment_Profile extends Fragment implements Listeners.ProfileActions {

    private HomeActivity activity;
    private FragmentProfileBinding binding;
    private Preferences preferences;
    private String lang;
    private UserModel userModel;

    public static Fragment_Profile newInstance() {

        return new Fragment_Profile();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();

    }

    private void initView() {
        activity = (HomeActivity) getActivity();
        preferences = Preferences.getInstance();
        userModel = preferences.getUserData(activity);
        Paper.init(activity);
        lang = Paper.book().read("lang", "ar");
        binding.setLang(lang);
        binding.setModel(userModel);
        binding.setActions(this);


    }


    @Override
    public void onAddAd() {
        if (userModel != null) {
            Intent intent = new Intent(activity, AddAdsActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(activity, getString(R.string.please_sign_in_or_sign_up), Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    public void onContactUs() {
        Intent intent = new Intent(activity, ContactUsActivity.class);
        startActivity(intent);
    }

    @Override
    public void onMyFavorite() {
        if (userModel != null) {
            Intent intent = new Intent(activity, MyFavoriteActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(activity, getString(R.string.please_sign_in_or_sign_up), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onMyAds() {
        if (userModel != null) {
            Intent intent = new Intent(activity, MyAdsActivity.class);
            startActivityForResult(intent, 200);
        } else {
            Toast.makeText(activity, getString(R.string.please_sign_in_or_sign_up), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCommission() {
        Intent intent = new Intent(activity, CommissionActivity.class);
        startActivity(intent);
    }

    @Override
    public void onLogout() {
        activity.deleteFirebaseToken();
    }

    @Override
    public void onSetting() {
        Intent intent = new Intent(activity, SettingActivity.class);
        startActivityForResult(intent, 300);
    }

    @Override
    public void onChatUs() {
        Intent intent = new Intent(activity, ChatUsActivity.class);
        startActivity(intent);
    }

    @Override
    public void onUpdateProfile() {
        if (userModel != null) {
            Intent intent = new Intent(activity, SignUpActivity.class);
            startActivityForResult(intent, 400);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            userModel = preferences.getUserData(activity);
            binding.setModel(userModel);

        } else if (requestCode == 200 && resultCode == Activity.RESULT_OK) {
            activity.refreshFragmentOffers();

        } else if (requestCode == 300 && resultCode == Activity.RESULT_OK && data != null) {
            String lang = data.getStringExtra("lang");
            activity.refreshActivity(lang);
        } else if (requestCode == 400 && resultCode == Activity.RESULT_OK) {
            userModel = preferences.getUserData(activity);
            binding.setModel(userModel);
        }
    }
}
