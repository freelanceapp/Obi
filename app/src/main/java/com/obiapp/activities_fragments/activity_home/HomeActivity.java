package com.obiapp.activities_fragments.activity_home;

import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.iid.FirebaseInstanceId;
import com.obiapp.R;
import com.obiapp.activities_fragments.activity_department_details.DepartmentDetailsActivity;
import com.obiapp.activities_fragments.activity_home.fragments.Fragment_Chat;
import com.obiapp.activities_fragments.activity_home.fragments.Fragment_Home;
import com.obiapp.activities_fragments.activity_home.fragments.Fragment_Offer;
import com.obiapp.activities_fragments.activity_home.fragments.Fragment_Profile;
import com.obiapp.activities_fragments.activity_login.LoginActivity;
import com.obiapp.activities_fragments.activity_my_coupon.MyCouponsActivity;
import com.obiapp.activities_fragments.activity_notification.NotificationActivity;
import com.obiapp.adapters.ExpandDepartmentAdapter;
import com.obiapp.databinding.ActivityHomeBinding;
import com.obiapp.language.Language;
import com.obiapp.models.DepartmentDataModel;
import com.obiapp.models.DepartmentModel;
import com.obiapp.models.NotFireModel;
import com.obiapp.models.StatusResponse;
import com.obiapp.models.UserModel;
import com.obiapp.preferences.Preferences;
import com.obiapp.remote.Api;
import com.obiapp.share.Common;
import com.obiapp.tags.Tags;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {
    private ActivityHomeBinding binding;
    private Preferences preferences;
    private FragmentManager fragmentManager;
    private Fragment_Home fragment_home;
    private Fragment_Profile fragment_profile;
    private Fragment_Chat fragment_chat;
    private Fragment_Offer fragment_offer;
    private UserModel userModel;
    private String lang;
    private ActionBarDrawerToggle toggle;
    private List<DepartmentModel> departmentModelList;
    private ExpandDepartmentAdapter expandDepartmentAdapter;
    private int parent_pos = -1, child_pos = -1;


    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang", "ar")));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home);
        initView();


    }

    private void initView() {
        departmentModelList = new ArrayList<>();
        fragmentManager = getSupportFragmentManager();
        preferences = Preferences.getInstance();
        userModel = preferences.getUserData(this);
        Paper.init(this);
        lang = Paper.book().read("lang", "ar");
        binding.setLang(lang);
        toggle = new ActionBarDrawerToggle(this, binding.drawerLayout, binding.toolbar, R.string.open, R.string.close);
//        toggle.setDisplayHomeAsUpEnabled(true);
//        toggle.setDisplayShowHomeEnabled(true);

        toggle.syncState();

        binding.toolbar.setNavigationIcon(R.drawable.ic_squares);

        binding.toolbar.getNavigationIcon().setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        binding.flNotification.setOnClickListener(view -> {

            Intent intent = new Intent(HomeActivity.this, NotificationActivity.class);
            startActivity(intent);

        });
        binding.setModel(userModel);


        binding.recViewNavigation.setLayoutManager(new LinearLayoutManager(this));
        expandDepartmentAdapter = new ExpandDepartmentAdapter(departmentModelList, this);
        binding.recViewNavigation.setAdapter(expandDepartmentAdapter);

        binding.flHome.setOnClickListener(v -> {
            displayFragmentMain();
        });


        binding.flProfile.setOnClickListener(v -> {
            displayFragmentProfile();

        });

        binding.flChat.setOnClickListener(v -> {
            displayFragmentChat();

        });
        binding.edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                  if(fragment_home!=null){
                      fragment_home.search(editable.toString());
                  }

            }
        });
        binding.llCoupon.setOnClickListener(view -> {
            Intent intent = new Intent(this, MyCouponsActivity.class);
            startActivity(intent);
        });
        binding.flOffer.setOnClickListener(v -> {
            displayFragmentOffer();

        });


        displayFragmentMain();

        if (userModel != null) {
            EventBus.getDefault().register(this);
            updateTokenFireBase();

        }


        binding.swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        binding.swipeRefresh.setOnRefreshListener(this::getDepartments);
        getDepartments();

    }


    private void getDepartments() {
        try {

            Api.getService(Tags.base_url)
                    .getDepartment()
                    .enqueue(new Callback<DepartmentDataModel>() {
                        @Override
                        public void onResponse(Call<DepartmentDataModel> call, Response<DepartmentDataModel> response) {
                            binding.progBarNavigation.setVisibility(View.GONE);
                            binding.swipeRefresh.setRefreshing(false);
                            if (response.isSuccessful() && response.body() != null) {
                                if (response.body().getStatus() == 200) {
                                    if (response.body().getData().size() > 0) {
                                        departmentModelList.clear();
                                        departmentModelList.addAll(response.body().getData());
                                        expandDepartmentAdapter.notifyDataSetChanged();
                                        binding.tvNoDataNavigation.setVisibility(View.GONE);
                                    } else {
                                        binding.tvNoDataNavigation.setVisibility(View.VISIBLE);

                                    }
                                } else {
                                    Toast.makeText(HomeActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                binding.swipeRefresh.setRefreshing(false);

                                binding.progBarNavigation.setVisibility(View.GONE);

                                if (response.code() == 500) {
                                    Toast.makeText(HomeActivity.this, "Server Error", Toast.LENGTH_SHORT).show();


                                } else {
                                    Toast.makeText(HomeActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

                                    try {

                                        Log.e("error", response.code() + "_" + response.errorBody().string());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<DepartmentDataModel> call, Throwable t) {
                            try {
                                binding.swipeRefresh.setRefreshing(false);

                                binding.progBarNavigation.setVisibility(View.GONE);

                                if (t.getMessage() != null) {
                                    Log.e("error", t.getMessage());
                                    if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                        Toast.makeText(HomeActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(HomeActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                            } catch (Exception e) {
                            }
                        }
                    });
        } catch (Exception e) {

        }
    }


    private void readNotificationCount() {
        binding.setNotCount(0);
    }


    public void displayFragmentMain() {
        try {
            updateHomUi();
            if (fragment_home == null) {
                fragment_home = Fragment_Home.newInstance();
            }


            if (fragment_offer != null && fragment_offer.isAdded()) {
                fragmentManager.beginTransaction().hide(fragment_offer).commit();
            }
            if (fragment_profile != null && fragment_profile.isAdded()) {
                fragmentManager.beginTransaction().hide(fragment_profile).commit();
            }

            if (fragment_chat != null && fragment_chat.isAdded()) {
                fragmentManager.beginTransaction().hide(fragment_chat).commit();
            }
            if (fragment_home.isAdded()) {
                fragmentManager.beginTransaction().show(fragment_home).commit();

            } else {
                fragmentManager.beginTransaction().add(R.id.fragment_app_container, fragment_home, "fragment_home").commit();

            }
            binding.setTitle(getString(R.string.home));
        } catch (Exception e) {
        }

    }

    public void displayFragmentOffer() {

        try {

            updateOfferUi();
            if (fragment_offer == null) {
                fragment_offer = Fragment_Offer.newInstance();
            }


            if (fragment_home != null && fragment_home.isAdded()) {
                fragmentManager.beginTransaction().hide(fragment_home).commit();
            }
            if (fragment_chat != null && fragment_chat.isAdded()) {
                fragmentManager.beginTransaction().hide(fragment_chat).commit();
            }

            if (fragment_profile != null && fragment_profile.isAdded()) {
                fragmentManager.beginTransaction().hide(fragment_profile).commit();
            }


            if (fragment_offer.isAdded()) {
                fragmentManager.beginTransaction().show(fragment_offer).commit();

            } else {
                fragmentManager.beginTransaction().add(R.id.fragment_app_container, fragment_offer, "fragment_offer").commit();

            }
            binding.setTitle(getString(R.string.offers));
        } catch (Exception e) {
        }
    }

    public void displayFragmentProfile() {

        try {
            updateProfileUi();
            if (fragment_profile == null) {
                fragment_profile = Fragment_Profile.newInstance();
            }


            if (fragment_home != null && fragment_home.isAdded()) {
                fragmentManager.beginTransaction().hide(fragment_home).commit();
            }
            if (fragment_offer != null && fragment_offer.isAdded()) {
                fragmentManager.beginTransaction().hide(fragment_offer).commit();
            }

            if (fragment_chat != null && fragment_chat.isAdded()) {
                fragmentManager.beginTransaction().hide(fragment_chat).commit();
            }


            if (fragment_profile.isAdded()) {
                fragmentManager.beginTransaction().show(fragment_profile).commit();

            } else {
                fragmentManager.beginTransaction().add(R.id.fragment_app_container, fragment_profile, "fragment_profile").commit();

            }
            binding.setTitle(getString(R.string.profile));
        } catch (Exception e) {
        }
    }

    public void displayFragmentChat() {

        try {
            updateChatUi();
            if (fragment_chat == null) {
                fragment_chat = Fragment_Chat.newInstance();
            }


            if (fragment_home != null && fragment_home.isAdded()) {
                fragmentManager.beginTransaction().hide(fragment_home).commit();
            }
            if (fragment_offer != null && fragment_offer.isAdded()) {
                fragmentManager.beginTransaction().hide(fragment_offer).commit();
            }

            if (fragment_profile != null && fragment_profile.isAdded()) {
                fragmentManager.beginTransaction().hide(fragment_profile).commit();
            }


            if (fragment_chat.isAdded()) {
                fragmentManager.beginTransaction().show(fragment_chat).commit();

            } else {
                fragmentManager.beginTransaction().add(R.id.fragment_app_container, fragment_chat, "fragment_profile").addToBackStack("fragment_profile").commit();

            }
            binding.setTitle(getString(R.string.chats));
        } catch (Exception e) {
        }
    }


    private void updateHomUi() {

        binding.flOffer.setBackgroundResource(0);
        binding.iconOffer.setColorFilter(ContextCompat.getColor(this, R.color.black));
        binding.tvOffer.setTextColor(ContextCompat.getColor(this, R.color.black));
        binding.tvOffer.setVisibility(View.GONE);

        binding.flHome.setBackgroundResource(R.drawable.small_rounded_btn_primary);
        binding.iconHome.setColorFilter(ContextCompat.getColor(this, R.color.white));
        binding.tvHome.setTextColor(ContextCompat.getColor(this, R.color.white));
        binding.tvHome.setVisibility(View.VISIBLE);

        binding.flProfile.setBackgroundResource(0);
        binding.iconProfile.setColorFilter(ContextCompat.getColor(this, R.color.black));
        binding.tvProfile.setTextColor(ContextCompat.getColor(this, R.color.black));
        binding.tvProfile.setVisibility(View.GONE);

        binding.flChat.setBackgroundResource(0);
        binding.iconChat.setColorFilter(ContextCompat.getColor(this, R.color.black));
        binding.tvChat.setTextColor(ContextCompat.getColor(this, R.color.black));
        binding.tvChat.setVisibility(View.GONE);


    }

    private void updateOfferUi() {

        binding.flOffer.setBackgroundResource(R.drawable.small_rounded_btn_primary);
        binding.iconOffer.setColorFilter(ContextCompat.getColor(this, R.color.white));
        binding.tvOffer.setTextColor(ContextCompat.getColor(this, R.color.white));
        binding.tvOffer.setVisibility(View.VISIBLE);

        binding.flHome.setBackgroundResource(0);
        binding.iconHome.setColorFilter(ContextCompat.getColor(this, R.color.black));
        binding.tvHome.setTextColor(ContextCompat.getColor(this, R.color.black));
        binding.tvHome.setVisibility(View.GONE);


        binding.flProfile.setBackgroundResource(0);
        binding.iconProfile.setColorFilter(ContextCompat.getColor(this, R.color.black));
        binding.tvProfile.setTextColor(ContextCompat.getColor(this, R.color.black));
        binding.tvProfile.setVisibility(View.GONE);

        binding.flChat.setBackgroundResource(0);
        binding.iconChat.setColorFilter(ContextCompat.getColor(this, R.color.black));
        binding.tvChat.setTextColor(ContextCompat.getColor(this, R.color.black));
        binding.tvChat.setVisibility(View.GONE);

    }


    private void updateProfileUi() {

        binding.flHome.setBackgroundResource(0);
        binding.iconHome.setColorFilter(ContextCompat.getColor(this, R.color.black));
        binding.tvHome.setTextColor(ContextCompat.getColor(this, R.color.black));
        binding.tvHome.setVisibility(View.GONE);


        binding.flOffer.setBackgroundResource(0);
        binding.iconOffer.setColorFilter(ContextCompat.getColor(this, R.color.black));
        binding.tvOffer.setTextColor(ContextCompat.getColor(this, R.color.black));
        binding.tvOffer.setVisibility(View.GONE);

        binding.flChat.setBackgroundResource(0);
        binding.iconChat.setColorFilter(ContextCompat.getColor(this, R.color.black));
        binding.tvChat.setTextColor(ContextCompat.getColor(this, R.color.black));
        binding.tvChat.setVisibility(View.GONE);

        binding.flProfile.setBackgroundResource(R.drawable.small_rounded_btn_primary);
        binding.iconProfile.setColorFilter(ContextCompat.getColor(this, R.color.white));
        binding.tvProfile.setTextColor(ContextCompat.getColor(this, R.color.white));
        binding.tvProfile.setVisibility(View.VISIBLE);

    }

    private void updateChatUi() {

        binding.flHome.setBackgroundResource(0);
        binding.iconHome.setColorFilter(ContextCompat.getColor(this, R.color.black));
        binding.tvHome.setTextColor(ContextCompat.getColor(this, R.color.black));
        binding.tvHome.setVisibility(View.GONE);


        binding.flOffer.setBackgroundResource(0);
        binding.iconOffer.setColorFilter(ContextCompat.getColor(this, R.color.black));
        binding.tvOffer.setTextColor(ContextCompat.getColor(this, R.color.black));
        binding.tvOffer.setVisibility(View.GONE);


        binding.flChat.setBackgroundResource(R.drawable.small_rounded_btn_primary);
        binding.iconChat.setColorFilter(ContextCompat.getColor(this, R.color.white));
        binding.tvChat.setTextColor(ContextCompat.getColor(this, R.color.white));
        binding.tvChat.setVisibility(View.VISIBLE);

        binding.flProfile.setBackgroundResource(0);
        binding.iconProfile.setColorFilter(ContextCompat.getColor(this, R.color.black));
        binding.tvProfile.setTextColor(ContextCompat.getColor(this, R.color.black));
        binding.tvProfile.setVisibility(View.GONE);

    }

    private void updateTokenFireBase() {

        FirebaseInstanceId.getInstance()
                .getInstanceId().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String token = task.getResult().getToken();

                try {
                    Api.getService(Tags.base_url)
                            .updateFirebaseToken(token, userModel.getData().getId(), "android")
                            .enqueue(new Callback<StatusResponse>() {
                                @Override
                                public void onResponse(Call<StatusResponse> call, Response<StatusResponse> response) {
                                    if (response.isSuccessful() && response.body() != null) {

                                        if (response.body().getStatus() == 200) {
                                            userModel.getData().setFirebaseToken(token);
                                            preferences.create_update_userdata(HomeActivity.this, userModel);
                                            Log.e("token", "updated successfully");

                                        }
                                    } else {
                                        try {

                                            Log.e("errorToken", response.code() + "_" + response.errorBody().string());
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<StatusResponse> call, Throwable t) {
                                    try {

                                        if (t.getMessage() != null) {
                                            Log.e("errorToken2", t.getMessage());
                                            if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                                Toast.makeText(HomeActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(HomeActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                    } catch (Exception e) {
                                    }
                                }
                            });
                } catch (Exception e) {


                }

            }
        });
    }

    public void deleteFirebaseToken() {
        if (userModel == null) {
            navigateToSignInActivity();

        } else {
            ProgressDialog dialog = Common.createProgressDialog(this, getString(R.string.wait));
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(true);
            dialog.show();

            Api.getService(Tags.base_url)
                    .deleteFirebaseToken(userModel.getData().getFirebaseToken(), userModel.getData().getId())
                    .enqueue(new Callback<StatusResponse>() {
                        @Override
                        public void onResponse(Call<StatusResponse> call, Response<StatusResponse> response) {
                            if (response.isSuccessful() && response.body() != null) {

                                if (response.body().getStatus() == 200) {
                                    logout(dialog);
                                } else {
                                    Toast.makeText(HomeActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                }

                            } else {
                                dialog.dismiss();
                                try {
                                    Log.e("error", response.code() + "__" + response.errorBody().string());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                if (response.code() == 500) {
                                    Toast.makeText(HomeActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(HomeActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<StatusResponse> call, Throwable t) {
                            try {
                                dialog.dismiss();
                                if (t.getMessage() != null) {
                                    Log.e("error", t.getMessage() + "__");

                                    if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                        Toast.makeText(HomeActivity.this, getString(R.string.something), Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(HomeActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            } catch (Exception e) {
                                Log.e("Error", e.getMessage() + "__");
                            }
                        }
                    });
        }

    }

    private void logout(ProgressDialog dialog) {
        Api.getService(Tags.base_url)
                .logout("Bearer " + userModel.getData().getToken())
                .enqueue(new Callback<StatusResponse>() {
                    @Override
                    public void onResponse(Call<StatusResponse> call, Response<StatusResponse> response) {
                        dialog.dismiss();
                        if (response.isSuccessful()) {
                            if (response.body() != null && response.body().getStatus() == 200) {
                                preferences.clear(HomeActivity.this);
                                NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                                if (manager != null) {
                                    manager.cancel(Tags.not_tag, Tags.not_id);
                                }
                                navigateToSignInActivity();
                            } else {
                                Toast.makeText(HomeActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

                            }


                        } else {
                            dialog.dismiss();
                            try {
                                Log.e("error", response.code() + "__" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            if (response.code() == 500) {
                                Toast.makeText(HomeActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(HomeActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<StatusResponse> call, Throwable t) {
                        try {
                            dialog.dismiss();
                            if (t.getMessage() != null) {
                                Log.e("error", t.getMessage() + "__");

                                if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                    Toast.makeText(HomeActivity.this, getString(R.string.something), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(HomeActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception e) {
                            Log.e("Error", e.getMessage() + "__");
                        }
                    }
                });
    }

    public void refreshActivity(String lang) {
        Paper.book().write("lang", lang);
        Language.setNewLocale(this, lang);
        new Handler()
                .postDelayed(() -> {

                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                }, 500);


    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void listenToNotifications(NotFireModel notFireModel) {
        if (userModel != null) {
            getNotificationCount();
            if (fragment_chat!=null&&fragment_chat.isAdded()){
                fragment_chat.getRooms();
            }

        }
    }



    private void getNotificationCount() {

    }

    @Override
    public void onBackPressed() {

        if (fragment_home != null && fragment_home.isAdded() && fragment_home.isVisible()) {
            if (userModel != null) {
                finish();
            } else {
                navigateToSignInActivity();
            }
        } else {
            displayFragmentMain();
        }
    }


    private void navigateToSignInActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        List<Fragment> fragmentList = fragmentManager.getFragments();
        for (Fragment fragment : fragmentList) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        List<Fragment> fragmentList = fragmentManager.getFragments();
        for (Fragment fragment : fragmentList) {
            fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    public void setItemSubDepartmentData(DepartmentModel.SubCategory subCategory, int parent_pos, int adapterPosition) {
        DepartmentModel departmentModel = departmentModelList.get(parent_pos);
        Intent intent = new Intent(this, DepartmentDetailsActivity.class);
        intent.putExtra("data", departmentModel);
        intent.putExtra("child_pos", adapterPosition);
        startActivity(intent);

        this.parent_pos = parent_pos;
        this.child_pos = adapterPosition;
    }

    public void refreshFragmentOffers() {
        if (fragment_offer != null && fragment_offer.isAdded()) {
            fragment_offer.getData();
        }
    }


}
