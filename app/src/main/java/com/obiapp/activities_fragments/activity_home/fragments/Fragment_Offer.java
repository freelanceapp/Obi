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


import com.obiapp.R;
import com.obiapp.activities_fragments.activity_add_ads.AddAdsActivity;
import com.obiapp.activities_fragments.activity_home.HomeActivity;
import com.obiapp.activities_fragments.activity_product_details.ProductDetailsActivity;
import com.obiapp.adapters.LatestOfferAdapter;
import com.obiapp.adapters.OfferAdapter;
import com.obiapp.databinding.FragmentOfferBinding;
import com.obiapp.models.ProductModel;
import com.obiapp.models.ProductsDataModel;
import com.obiapp.models.UserModel;
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
    private List<ProductModel> latestOfferModelList, offerModelList;
    private LatestOfferAdapter latestOfferAdapter;
    private OfferAdapter offerAdapter;


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
        latestOfferModelList = new ArrayList<>();
        offerModelList = new ArrayList<>();
        activity = (HomeActivity) getActivity();
        Paper.init(activity);
        lang = Paper.book().read("lang", "ar");
        binding.setLang(lang);
        preferences = Preferences.getInstance();
        userModel = preferences.getUserData(activity);
        latestOfferAdapter = new LatestOfferAdapter(latestOfferModelList, activity, this);
        binding.progBarOffer.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(activity, R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        binding.progBarLatestOffer.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(activity, R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        binding.recViewLatestOffer.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
        binding.recViewLatestOffer.setAdapter(latestOfferAdapter);

        binding.recViewOffer.setLayoutManager(new GridLayoutManager(activity, 2));
        offerAdapter = new OfferAdapter(offerModelList, activity, this);
        binding.recViewOffer.setAdapter(offerAdapter);

        binding.fab.setOnClickListener(v -> {
            if (userModel==null){
                Common.CreateDialogAlert(activity,getString(R.string.please_sign_in_or_sign_up));
            }else {
                Intent intent = new Intent(activity, AddAdsActivity.class);
                intent.putExtra("offer",true);
                startActivity(intent);
            }
        });
        getData();

    }

    public void getData() {
        getLatestOffer();
        getOffer();
    }

    private void getOffer() {
        offerModelList.clear();
        offerAdapter.notifyDataSetChanged();
        binding.progBarOffer.setVisibility(View.VISIBLE);
        binding.tvNoDataOffer.setVisibility(View.GONE);

        try {

            Api.getService(Tags.base_url)
                    .getOffer()
                    .enqueue(new Callback<ProductsDataModel>() {
                        @Override
                        public void onResponse(Call<ProductsDataModel> call, Response<ProductsDataModel> response) {
                            binding.progBarOffer.setVisibility(View.GONE);
                            if (response.isSuccessful() && response.body() != null) {
                                if (response.body().getStatus() == 200) {
                                    if (response.body().getData().size() > 0) {
                                        offerModelList.clear();
                                        offerModelList.addAll(response.body().getData());
                                        offerAdapter.notifyDataSetChanged();
                                        binding.tvNoDataOffer.setVisibility(View.GONE);
                                    } else {
                                        binding.tvNoDataOffer.setVisibility(View.VISIBLE);

                                    }
                                } else {
                                    Toast.makeText(activity, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                binding.progBarOffer.setVisibility(View.GONE);

                                if (response.code() == 500) {
                                    Toast.makeText(activity, "Server Error", Toast.LENGTH_SHORT).show();


                                } else {
                                    Toast.makeText(activity, getString(R.string.failed), Toast.LENGTH_SHORT).show();

                                    try {

                                        Log.e("error", response.code() + "_" + response.errorBody().string());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ProductsDataModel> call, Throwable t) {
                            try {
                                binding.progBarOffer.setVisibility(View.GONE);

                                if (t.getMessage() != null) {
                                    Log.e("error", t.getMessage());
                                    if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                        Toast.makeText(activity, R.string.something, Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(activity, t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                            } catch (Exception e) {
                            }
                        }
                    });
        } catch (Exception e) {

        }
    }

    private void getLatestOffer() {

        latestOfferModelList.clear();
        latestOfferAdapter.notifyDataSetChanged();
        binding.progBarLatestOffer.setVisibility(View.VISIBLE);
        binding.tvNoDataLatestOffer.setVisibility(View.GONE);
        try {

            Api.getService(Tags.base_url)
                    .getLatestOffer()
                    .enqueue(new Callback<ProductsDataModel>() {
                        @Override
                        public void onResponse(Call<ProductsDataModel> call, Response<ProductsDataModel> response) {
                            binding.progBarLatestOffer.setVisibility(View.GONE);
                            if (response.isSuccessful() && response.body() != null) {
                                if (response.body().getStatus() == 200) {
                                    if (response.body().getData().size() > 0) {
                                        latestOfferModelList.clear();
                                        latestOfferModelList.addAll(response.body().getData());
                                        latestOfferAdapter.notifyDataSetChanged();
                                        binding.tvNoDataLatestOffer.setVisibility(View.GONE);
                                    } else {
                                        binding.tvNoDataLatestOffer.setVisibility(View.VISIBLE);

                                    }
                                } else {
                                    Toast.makeText(activity, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                binding.progBarLatestOffer.setVisibility(View.GONE);

                                if (response.code() == 500) {
                                    Toast.makeText(activity, "Server Error", Toast.LENGTH_SHORT).show();


                                } else {
                                    Toast.makeText(activity, getString(R.string.failed), Toast.LENGTH_SHORT).show();

                                    try {

                                        Log.e("error", response.code() + "_" + response.errorBody().string());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ProductsDataModel> call, Throwable t) {
                            try {
                                binding.progBarLatestOffer.setVisibility(View.GONE);

                                if (t.getMessage() != null) {
                                    Log.e("error", t.getMessage());
                                    if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                        Toast.makeText(activity, R.string.something, Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(activity, t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                            } catch (Exception e) {
                            }
                        }
                    });
        } catch (Exception e) {

        }

    }

    public void setProductItemData(ProductModel productModel) {
        Intent intent = new Intent(activity, ProductDetailsActivity.class);
        intent.putExtra("product_id", productModel.getId());
        startActivity(intent);
    }
}
