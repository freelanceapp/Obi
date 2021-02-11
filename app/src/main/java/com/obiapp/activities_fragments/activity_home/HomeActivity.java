package com.obiapp.activities_fragments.activity_home;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.os.Build;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.firebase.iid.FirebaseInstanceId;
import com.obiapp.R;
import com.obiapp.activities_fragments.activity_department_details.DepartmentDetailsActivity;
import com.obiapp.activities_fragments.activity_home.fragments.Fragment_News;
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

public class HomeActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, LocationListener {
    private ActivityHomeBinding binding;
    private Preferences preferences;
    private FragmentManager fragmentManager;
    private Fragment_Home fragment_home;
    private Fragment_Profile fragment_profile;
    private Fragment_News fragment_news;
    private Fragment_Offer fragment_country_guide;
    private UserModel userModel;
    private String lang;
    private ActionBarDrawerToggle toggle;
    private List<DepartmentModel> departmentModelList;
    private ExpandDepartmentAdapter expandDepartmentAdapter;
    private int parent_pos = -1, child_pos = -1;
    private float lastTranslate = 0.0f;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private final String gps_perm = Manifest.permission.ACCESS_FINE_LOCATION;
    private final int loc_req = 22;
    public double lat = 0.0, lng = 0.0;


    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang", "ar")));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home);
        CheckPermission();

        initView();


    }

    private void initView() {
        departmentModelList = new ArrayList<>();
        fragmentManager = getSupportFragmentManager();
        preferences = Preferences.getInstance();
        userModel = preferences.getUserData(this);
//        if(userModel==null){
//            binding.llprofile.setVisibility(View.GONE);
//        }
        Paper.init(this);
        lang = Paper.book().read("lang", "ar");
        binding.setLang(lang);
        toggle = new ActionBarDrawerToggle(this, binding.drawerLayout, binding.toolbar, R.string.open, R.string.close) {
            @SuppressLint("NewApi")
            public void onDrawerSlide(View drawerView, float slideOffset) {
                slide(slideOffset);

            }
        };
        //   toggle.setDrawerIndicatorEnabled(false);

        toggle.syncState();
        binding.toolbar.setNavigationIcon(R.drawable.ic_menu);

        binding.drawerLayout.setDrawerListener(toggle);

        binding.toolbar.setNavigationIcon(R.drawable.ic_menu);

        binding.toolbar.getNavigationIcon().setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        binding.flNotification.setOnClickListener(view -> {

            Intent intent = new Intent(HomeActivity.this, NotificationActivity.class);
            startActivity(intent);

        });
        binding.setModel(userModel);


//
//        binding.recViewNavigation.setLayoutManager(new LinearLayoutManager(this));
//        expandDepartmentAdapter = new ExpandDepartmentAdapter(departmentModelList, this);
//        binding.recViewNavigation.setAdapter(expandDepartmentAdapter);

        binding.flHome.setOnClickListener(v -> {
            displayFragmentMain();
        });


        binding.flProfile.setOnClickListener(v -> {
            displayFragmentProfile();

        });

        binding.flNews.setOnClickListener(v -> {
            displayFragmentNews();

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

                if (fragment_home != null) {
                    fragment_home.search(editable.toString());
                }

            }
        });
//        binding.llCoupon.setOnClickListener(view -> {
//            Intent intent = new Intent(this, MyCouponsActivity.class);
//            startActivity(intent);
//        });
        binding.flcountrguide.setOnClickListener(v -> {
            displayFragmentCountryGuide();

        });



        if (userModel != null) {
            EventBus.getDefault().register(this);
            updateTokenFireBase();

        }


//        binding.swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
//        binding.swipeRefresh.setOnRefreshListener(this::getDepartments);
        //  getDepartments();
        binding.llabout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.drawerLayout.closeDrawer(Gravity.RIGHT);
                binding.tv1.setTextColor(getResources().getColor(R.color.gray6));
                //  binding.tv2.setTextColor(getResources().getColor(R.color. gray6));
                binding.tv3.setTextColor(getResources().getColor(R.color.gray6));
                binding.tv4.setTextColor(getResources().getColor(R.color.colorAccent));
                binding.tv5.setTextColor(getResources().getColor(R.color.gray6));
                binding.image1.setColorFilter(R.color.gray6, android.graphics.PorterDuff.Mode.MULTIPLY);
                //  binding.image2.setColorFilter(R.color. gray6, android.graphics.PorterDuff.Mode.MULTIPLY);
                binding.image3.setColorFilter(R.color.gray6, android.graphics.PorterDuff.Mode.MULTIPLY);
                binding.image4.setColorFilter(R.color.colorAccent, android.graphics.PorterDuff.Mode.MULTIPLY);
                binding.image5.setColorFilter(R.color.gray6, android.graphics.PorterDuff.Mode.MULTIPLY);
                //  presenter.displayFragmentAboutus();
//        binding.swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
//        binding.swipeRefresh.setOnRefreshListener(this::getDepartments);
      //  getDepartments();

            }
        });
        binding.llterms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  binding.drawar.closeDrawer(Gravity.RIGHT);
                binding.drawerLayout.closeDrawer(Gravity.RIGHT);
                binding.tv1.setTextColor(getResources().getColor(R.color.gray6));
                //binding.tv2.setTextColor(getResources().getColor(R.color. gray6));
                binding.tv3.setTextColor(getResources().getColor(R.color.gray6));
                binding.tv4.setTextColor(getResources().getColor(R.color.gray6));
                binding.tv5.setTextColor(getResources().getColor(R.color.colorAccent));
                binding.image1.setColorFilter(R.color.gray6, android.graphics.PorterDuff.Mode.MULTIPLY);
                //binding.image2.setColorFilter(R.color. gray6, android.graphics.PorterDuff.Mode.MULTIPLY);
                binding.image3.setColorFilter(R.color.gray6, android.graphics.PorterDuff.Mode.MULTIPLY);
                binding.image4.setColorFilter(R.color.gray6, android.graphics.PorterDuff.Mode.MULTIPLY);
                binding.image5.setColorFilter(R.color.colorAccent, android.graphics.PorterDuff.Mode.MULTIPLY);
                //  presenter.displayFragmentTerms();
            }
        });
        binding.llcontactus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.drawerLayout.closeDrawer(Gravity.RIGHT);
                binding.tv1.setTextColor(getResources().getColor(R.color.gray6));
                //binding.tv2.setTextColor(getResources().getColor(R.color. gray6));
                binding.tv3.setTextColor(getResources().getColor(R.color.colorAccent));
                binding.tv4.setTextColor(getResources().getColor(R.color.gray6));
                binding.tv5.setTextColor(getResources().getColor(R.color.gray6));
                binding.image1.setColorFilter(R.color.gray6, android.graphics.PorterDuff.Mode.MULTIPLY);
                // binding.image2.setColorFilter(R.color. gray6, android.graphics.PorterDuff.Mode.MULTIPLY);
                binding.image3.setColorFilter(R.color.colorAccent, android.graphics.PorterDuff.Mode.MULTIPLY);
                binding.image4.setColorFilter(R.color.gray6, android.graphics.PorterDuff.Mode.MULTIPLY);
                binding.image5.setColorFilter(R.color.gray6, android.graphics.PorterDuff.Mode.MULTIPLY);

                // presenter.displayFragmentContactus();

            }
        });
        binding.llhome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.drawerLayout.closeDrawer(Gravity.RIGHT);
                binding.tv1.setTextColor(getResources().getColor(R.color.colorAccent));
                binding.tv3.setTextColor(getResources().getColor(R.color.gray6));
                binding.tv4.setTextColor(getResources().getColor(R.color.gray6));
                binding.tv5.setTextColor(getResources().getColor(R.color.gray6));
                binding.image1.setColorFilter(R.color.colorAccent, android.graphics.PorterDuff.Mode.MULTIPLY);
                binding.image3.setColorFilter(R.color.gray6, android.graphics.PorterDuff.Mode.MULTIPLY);
                binding.image4.setColorFilter(R.color.gray6, android.graphics.PorterDuff.Mode.MULTIPLY);
                binding.image5.setColorFilter(R.color.gray6, android.graphics.PorterDuff.Mode.MULTIPLY);

            }
        });

        binding.lllogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userModel != null) {
                    preferences.clear(HomeActivity.this);
                    Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(HomeActivity.this, getResources().getString(R.string.please_sign_in_or_sign_up), Toast.LENGTH_LONG).show();

                }

            }

        });
    }


//    private void getDepartments() {
//        try {
//
//            Api.getService(Tags.base_url)
//                    .getDepartment()
//                    .enqueue(new Callback<DepartmentDataModel>() {
//                        @Override
//                        public void onResponse(Call<DepartmentDataModel> call, Response<DepartmentDataModel> response) {
//                            binding.progBarNavigation.setVisibility(View.GONE);
//                            binding.swipeRefresh.setRefreshing(false);
//                            if (response.isSuccessful() && response.body() != null) {
//                                if (response.body().getStatus() == 200) {
//                                    if (response.body().getData().size() > 0) {
//                                        departmentModelList.clear();
//                                        departmentModelList.addAll(response.body().getData());
//                                        expandDepartmentAdapter.notifyDataSetChanged();
//                                        binding.tvNoDataNavigation.setVisibility(View.GONE);
//                                    } else {
//                                        binding.tvNoDataNavigation.setVisibility(View.VISIBLE);
//
//                                    }
//                                } else {
//                                    Toast.makeText(HomeActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
//                                }
//                            } else {
//                                binding.swipeRefresh.setRefreshing(false);
//
//                                binding.progBarNavigation.setVisibility(View.GONE);
//
//                                if (response.code() == 500) {
//                                    Toast.makeText(HomeActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
//
//
//                                } else {
//                                    Toast.makeText(HomeActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
//
//                                    try {
//
//                                        Log.e("error", response.code() + "_" + response.errorBody().string());
//                                    } catch (IOException e) {
//                                        e.printStackTrace();
//                                    }
//                                }
//                            }
//                        }
//
//                        @Override
//                        public void onFailure(Call<DepartmentDataModel> call, Throwable t) {
//                            try {
//                                binding.swipeRefresh.setRefreshing(false);
//
//                                binding.progBarNavigation.setVisibility(View.GONE);
//
//                                if (t.getMessage() != null) {
//                                    Log.e("error", t.getMessage());
//                                    if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
//                                        Toast.makeText(HomeActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
//                                    } else {
//                                        Toast.makeText(HomeActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
//                                    }
//                                }
//
//                            } catch (Exception e) {
//                            }
//                        }
//                    });
//        } catch (Exception e) {
//
//        }
//    }


    private void readNotificationCount() {
        binding.setNotCount(0);
    }


    public void displayFragmentMain() {
        try {
            updateHomUi();
            if (fragment_home == null) {
                fragment_home = Fragment_Home.newInstance();
            }


            if (fragment_country_guide != null && fragment_country_guide.isAdded()) {
                fragmentManager.beginTransaction().hide(fragment_country_guide).commit();
            }
            if (fragment_profile != null && fragment_profile.isAdded()) {
                fragmentManager.beginTransaction().hide(fragment_profile).commit();
            }

            if (fragment_news != null && fragment_news.isAdded()) {
                fragmentManager.beginTransaction().hide(fragment_news).commit();
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

    public void displayFragmentCountryGuide() {

        try {

            updateCountryGuideUi();
            if (fragment_country_guide == null) {
                fragment_country_guide = Fragment_Offer.newInstance();
            }


            if (fragment_home != null && fragment_home.isAdded()) {
                fragmentManager.beginTransaction().hide(fragment_home).commit();
            }
            if (fragment_news != null && fragment_news.isAdded()) {
                fragmentManager.beginTransaction().hide(fragment_news).commit();
            }

            if (fragment_profile != null && fragment_profile.isAdded()) {
                fragmentManager.beginTransaction().hide(fragment_profile).commit();
            }


            if (fragment_country_guide.isAdded()) {
                fragmentManager.beginTransaction().show(fragment_country_guide).commit();

            } else {
                fragmentManager.beginTransaction().add(R.id.fragment_app_container, fragment_country_guide, "fragment_offer").commit();

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
            if (fragment_country_guide != null && fragment_country_guide.isAdded()) {
                fragmentManager.beginTransaction().hide(fragment_country_guide).commit();
            }

            if (fragment_news != null && fragment_news.isAdded()) {
                fragmentManager.beginTransaction().hide(fragment_news).commit();
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

    public void displayFragmentNews() {

        try {
            updateNewsUi();
            if (fragment_news == null) {
                fragment_news = Fragment_News.newInstance();
            }


            if (fragment_home != null && fragment_home.isAdded()) {
                fragmentManager.beginTransaction().hide(fragment_home).commit();
            }
            if (fragment_country_guide != null && fragment_country_guide.isAdded()) {
                fragmentManager.beginTransaction().hide(fragment_country_guide).commit();
            }

            if (fragment_profile != null && fragment_profile.isAdded()) {
                fragmentManager.beginTransaction().hide(fragment_profile).commit();
            }


            if (fragment_news.isAdded()) {
                fragmentManager.beginTransaction().show(fragment_news).commit();

            } else {
                fragmentManager.beginTransaction().add(R.id.fragment_app_container, fragment_news, "fragment_profile").addToBackStack("fragment_profile").commit();

            }
            binding.setTitle(getString(R.string.chats));
        } catch (Exception e) {
        }
    }


    private void updateHomUi() {
        binding.flsearch.setVisibility(View.VISIBLE);
        binding.tv.setVisibility(View.GONE);
        binding.flcountrguide.setBackgroundResource(0);
        binding.iconcountrguide.setColorFilter(ContextCompat.getColor(this, R.color.
                gray6));
        binding.tvcountrguide.setTextColor(ContextCompat.getColor(this, R.color.
                gray6));
        // binding.tvOffer.setVisibility(View.GONE);

        binding.iconHome.setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary));
        binding.tvHome.setTextColor(ContextCompat.getColor(this, R.color.
                colorPrimary));
        binding.tvHome.setVisibility(View.VISIBLE);

        binding.flProfile.setBackgroundResource(0);
        binding.iconProfile.setColorFilter(ContextCompat.getColor(this, R.color.
                gray6));
        binding.tvProfile.setTextColor(ContextCompat.getColor(this, R.color.
                gray6));
        //  binding.tvProfile.setVisibility(View.GONE);

        binding.flNews.setBackgroundResource(0);
        binding.iconNews.setColorFilter(ContextCompat.getColor(this, R.color.gray6));
        binding.tvNews.setTextColor(ContextCompat.getColor(this, R.color.
                gray6));
        // binding.tvNews.setVisibility(View.GONE);


    }

    private void updateCountryGuideUi() {
        binding.flsearch.setVisibility(View.GONE);
        binding.tv.setVisibility(View.VISIBLE);

        binding.iconcountrguide.setColorFilter(ContextCompat.getColor(this, R.color.
                colorPrimary));
        binding.tvcountrguide.setTextColor(ContextCompat.getColor(this, R.color.
                colorPrimary));
        //binding.tvOffer.setVisibility(View.VISIBLE);

        binding.flHome.setBackgroundResource(0);
        binding.iconHome.setColorFilter(ContextCompat.getColor(this, R.color.
                gray6));
        binding.tvHome.setTextColor(ContextCompat.getColor(this, R.color.
                gray6));
        //  binding.tvHome.setVisibility(View.GONE);


        binding.flProfile.setBackgroundResource(0);
        binding.iconProfile.setColorFilter(ContextCompat.getColor(this, R.color.
                gray6));
        binding.tvProfile.setTextColor(ContextCompat.getColor(this, R.color.
                gray6));
        // binding.tvProfile.setVisibility(View.GONE);

        binding.flNews.setBackgroundResource(0);
        binding.iconNews.setColorFilter(ContextCompat.getColor(this, R.color.
                gray6));
        binding.tvNews.setTextColor(ContextCompat.getColor(this, R.color.gray6));
        //  binding.       tvNews.setVisibility(View.GONE);

    }


    private void updateProfileUi() {
        binding.flsearch.setVisibility(View.GONE);
        binding.tv.setVisibility(View.VISIBLE);

        binding.flHome.setBackgroundResource(0);
        binding.iconHome.setColorFilter(ContextCompat.getColor(this, R.color.
                gray6));
        binding.tvHome.setTextColor(ContextCompat.getColor(this, R.color.
                gray6));
        // binding.tvHome.setVisibility(View.GONE);


        binding.flcountrguide.setBackgroundResource(0);
        binding.iconcountrguide.setColorFilter(ContextCompat.getColor(this, R.color.
                gray6));
        binding.tvcountrguide.setTextColor(ContextCompat.getColor(this, R.color.
                gray6));
        // binding.tvOffer.setVisibility(View.GONE);

        binding.flNews.setBackgroundResource(0);
        binding.iconNews.setColorFilter(ContextCompat.getColor(this, R.color.
                gray6));
        binding.tvNews.setTextColor(ContextCompat.getColor(this, R.color.
                gray6));
        // binding.       tvNews.setVisibility(View.GONE);

        binding.iconProfile.setColorFilter(ContextCompat.getColor(this, R.color.
                colorPrimary));
        binding.tvProfile.setTextColor(ContextCompat.getColor(this, R.color.
                colorPrimary));
        binding.tvProfile.setVisibility(View.VISIBLE);

    }

    private void updateNewsUi() {
        binding.flsearch.setVisibility(View.GONE);
        binding.tv.setVisibility(View.VISIBLE);

        binding.flHome.setBackgroundResource(0);
        binding.iconHome.setColorFilter(ContextCompat.getColor(this, R.color.
                gray6));
        binding.tvHome.setTextColor(ContextCompat.getColor(this, R.color.
                gray6));
        // binding.tvHome.setVisibility(View.GONE);


        binding.flcountrguide.setBackgroundResource(0);
        binding.iconcountrguide.setColorFilter(ContextCompat.getColor(this, R.color.
                gray6));
        binding.tvcountrguide.setTextColor(ContextCompat.getColor(this, R.color.
                gray6));
        // binding.tvOffer.setVisibility(View.GONE);


        binding.iconNews.setColorFilter(ContextCompat.getColor(this, R.color.
                colorPrimary));
        binding.tvNews.setTextColor(ContextCompat.getColor(this, R.color.
                colorPrimary));
        binding.tvNews.setVisibility(View.VISIBLE);

        binding.flProfile.setBackgroundResource(0);
        binding.iconProfile.setColorFilter(ContextCompat.getColor(this, R.color.
                gray6));
        binding.tvProfile.setTextColor(ContextCompat.getColor(this, R.color.
                gray6));
        //  binding.tvProfile.setVisibility(View.GONE);

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
            if (fragment_news != null && fragment_news.isAdded()) {
                fragment_news.getRooms();
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


    private void initGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addOnConnectionFailedListener(this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }

    private void CheckPermission() {
        if (ActivityCompat.checkSelfPermission(this, gps_perm) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{gps_perm}, loc_req);
        } else {

            initGoogleApiClient();

        }
    }


    private void initLocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setFastestInterval(1000);
        locationRequest.setInterval(60000);
        LocationSettingsRequest.Builder request = new LocationSettingsRequest.Builder();
        request.addLocationRequest(locationRequest);
        request.setAlwaysShow(false);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, request.build());

        result.setResultCallback(result1 -> {

            Status status = result1.getStatus();
            switch (status.getStatusCode()) {
                case LocationSettingsStatusCodes.SUCCESS:
                    startLocationUpdate();
                    break;
                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                    try {
                        status.startResolutionForResult(HomeActivity.this, 1255);
                    } catch (Exception e) {
                    }
                    break;
                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                    Log.e("not available", "not available");
                    break;
            }
        });

    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdate() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                onLocationChanged(locationResult.getLastLocation());
            }
        };
        LocationServices.getFusedLocationProviderClient(this)
                .requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        initLocationRequest();
    }

    @Override
    public void onConnectionSuspended(int i) {
        if (googleApiClient != null) {
            googleApiClient.connect();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        lat = location.getLatitude();
        lng = location.getLongitude();
        displayFragmentMain();
        binding.tvLocation.setVisibility(View.GONE);
        binding.coordData.setVisibility(View.VISIBLE);
        if (googleApiClient != null) {
            googleApiClient.disconnect();
        }
        if (locationCallback != null) {
            LocationServices.getFusedLocationProviderClient(this).removeLocationUpdates(locationCallback);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }

        if (googleApiClient!=null){
            googleApiClient.disconnect();
            googleApiClient=null;
        }

        if (locationCallback!=null){
            LocationServices.getFusedLocationProviderClient(this).removeLocationUpdates(locationCallback);

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        List<Fragment> fragmentList = fragmentManager.getFragments();
        for (Fragment fragment : fragmentList) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
        if (requestCode == 1255 && resultCode == RESULT_OK) {
            startLocationUpdate();

        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        List<Fragment> fragmentList = fragmentManager.getFragments();
        for (Fragment fragment : fragmentList) {
            fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

        if (requestCode == loc_req) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initGoogleApiClient();

            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
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
        if (fragment_country_guide != null && fragment_country_guide.isAdded()) {
            fragment_country_guide.getData();
        }
    }

    public void slide(float slideOffset) {
        float moveFactor = (float) ((binding.cardview.getWidth() * slideOffset) / 1.5);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            binding.cardview.setTranslationX(-moveFactor);
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) binding.cardview.getLayoutParams();
            if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                params.setMargins(0, 0, 0, 0);
            } else {
                params.setMargins(0, 200, 0, 200);
            }
            binding.cardview.setLayoutParams(params);

        } else {
            TranslateAnimation anim = new TranslateAnimation(lastTranslate, -moveFactor, 0.0f, 0.0f);
            anim.setDuration(0);
            anim.setFillAfter(true);

            lastTranslate = -moveFactor;
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) binding.cardview.getLayoutParams();
            if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                params.setMargins(0, 0, 0, 0);
            } else {
                params.setMargins(0, 200, 0, 200);
            }
            binding.cardview.setLayoutParams(params);
            binding.cardview.startAnimation(anim);

        }

    }
}
