package com.obiapp.activities_fragments.activity_home.fragments;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;


import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.SkeletonScreen;
import com.obiapp.R;
import com.obiapp.activities_fragments.activity_add_ads.AddAdsActivity;
import com.obiapp.activities_fragments.activity_google.activity_shop_query.ShopsQueryActivity;
import com.obiapp.activities_fragments.activity_home.HomeActivity;
import com.obiapp.activities_fragments.activity_product_details.ProductDetailsActivity;
import com.obiapp.adapters.LatestOfferAdapter;
import com.obiapp.adapters.OfferAdapter;
import com.obiapp.adapters.google_adapters.PlaceCategoryAdapter;
import com.obiapp.databinding.FragmentOfferBinding;
import com.obiapp.models.ProductModel;
import com.obiapp.models.ProductsDataModel;
import com.obiapp.models.UserModel;
import com.obiapp.models.google_models.CategoryDataModel;
import com.obiapp.models.google_models.CategoryModel;
import com.obiapp.preferences.Preferences;
import com.obiapp.remote.Api;
import com.obiapp.share.Common;
import com.obiapp.tags.Tags;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fragment_Offer extends Fragment {

    private HomeActivity activity;
    private FragmentOfferBinding binding;
    private Preferences preferences;
    private UserModel userModel;
    private String lang;
    private PlaceCategoryAdapter categoryAdapter;
    private List<CategoryModel> categoryModelList;
    private SkeletonScreen skeletonCategory;
    private double user_lat = 0.0;
    private double user_lng = 0.0;


    public static Fragment_Offer newInstance() {
        return new Fragment_Offer();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_offer, container, false);
        initView();
        return binding.getRoot();
    }


    private void initView() {
        categoryModelList = new ArrayList<>();
        activity = (HomeActivity) getActivity();
        Paper.init(activity);
        lang = Paper.book().read("lang", "ar");
        binding.setLang(lang);
        preferences = Preferences.getInstance();
        userModel = preferences.getUserData(activity);




        binding.recView.setLayoutManager(new GridLayoutManager(activity, 2));
        categoryAdapter = new PlaceCategoryAdapter(categoryModelList, activity,this);
        binding.recView.setAdapter(categoryAdapter);
        skeletonCategory = Skeleton.bind(binding.recView)
                .frozen(false)
                .duration(1000)
                .shimmer(true)
                .count(96)
                .adapter(categoryAdapter)
                .load(R.layout.category_row)
                .show();
        getData();

    }

    public void getData() {
        getCategory();
    }

    private void getCategory() {


        categoryModelList.clear();
        Api.getService(Tags.base_url)
                .getGoogleCategory()
                .enqueue(new Callback<CategoryDataModel>() {
                    @Override
                    public void onResponse(Call<CategoryDataModel> call, Response<CategoryDataModel> response) {
                        skeletonCategory.hide();
                        if (response.isSuccessful() && response.body() != null) {
                            categoryModelList.addAll(response.body().getGoogle_categories());
                            categoryAdapter.notifyDataSetChanged();
                            Log.e("size",categoryModelList.size()+"");
                            if (categoryModelList.size()>0){
                                binding.tvNoData.setVisibility(View.GONE);
                            }else {
                                binding.tvNoData.setVisibility(View.VISIBLE);

                            }

                        } else {
                            skeletonCategory.hide();

                            try {
                                Log.e("error_code", response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }


                    }


                    @Override
                    public void onFailure(Call<CategoryDataModel> call, Throwable t) {
                        try {
                            Log.e("3", "3");

                            skeletonCategory.hide();
                            if (t.getMessage() != null) {
                                Log.e("error", t.getMessage());
                                if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                    Toast.makeText(activity,getString(R.string.something), Toast.LENGTH_LONG).show();
                                } else if (t.getMessage().toLowerCase().contains("socket") || t.getMessage().toLowerCase().contains("canceled")) {
                                } else {
                                    Toast.makeText(activity, t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception e) {
                            Log.e("error", e.toString());
                        }
                    }
                });


    }


    public void setCategoryData(CategoryModel categoryModel) {
        user_lat = activity.lat;
        user_lng = activity.lng;
        Intent intent = new Intent(activity, ShopsQueryActivity.class);
        intent.putExtra("lat",user_lat);
        intent.putExtra("lng",user_lng);
        intent.putExtra("data",categoryModel);
        startActivity(intent);
    }
}
