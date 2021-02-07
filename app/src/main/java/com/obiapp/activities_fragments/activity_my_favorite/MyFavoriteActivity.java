package com.obiapp.activities_fragments.activity_my_favorite;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.obiapp.R;
import com.obiapp.activities_fragments.activity_product_details.ProductDetailsActivity;
import com.obiapp.adapters.MyFavoriteAdapter;
import com.obiapp.databinding.ActivityMyFavoriteBinding;
import com.obiapp.language.Language;
import com.obiapp.models.ProductModel;
import com.obiapp.models.ProductsDataModel;
import com.obiapp.models.StatusResponse;
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

public class MyFavoriteActivity extends AppCompatActivity {
    private ActivityMyFavoriteBinding binding;
    private Preferences preference;
    private UserModel userModel;
    private MyFavoriteAdapter adapter;
    private List<ProductModel> productModelList;
    private String lang;
    private int selectedPos=-1;

    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang", "ar")));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_favorite);
        initView();
    }

    private void initView() {
        productModelList = new ArrayList<>();
        Paper.init(this);
        lang = Paper.book().read("lang", "ar");
        binding.setLang(lang);
        preference = Preferences.getInstance();
        userModel = preference.getUserData(this);
        binding.recView.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new MyFavoriteAdapter(productModelList, this);
        binding.recView.setAdapter(adapter);
        binding.swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        binding.swipeRefresh.setOnRefreshListener(this::getMyFavorite);
        binding.llBack.setOnClickListener(view -> onBackPressed());

        getMyFavorite();
    }

    private void getMyFavorite() {

        try {

            Api.getService(Tags.base_url)
                    .getMyFavorite("Bearer " + userModel.getData().getToken())
                    .enqueue(new Callback<ProductsDataModel>() {
                        @Override
                        public void onResponse(Call<ProductsDataModel> call, Response<ProductsDataModel> response) {
                            binding.progBar.setVisibility(View.GONE);
                            binding.swipeRefresh.setRefreshing(false);
                            if (response.isSuccessful() && response.body() != null) {
                                if (response.body().getStatus() == 200) {
                                    if (response.body().getData().size() > 0) {
                                        productModelList.clear();
                                        productModelList.addAll(response.body().getData());
                                        adapter.notifyDataSetChanged();
                                        binding.tvNoData.setVisibility(View.GONE);
                                    } else {
                                        binding.tvNoData.setVisibility(View.VISIBLE);

                                    }
                                } else {
                                    Toast.makeText(MyFavoriteActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                binding.progBar.setVisibility(View.GONE);
                                binding.swipeRefresh.setRefreshing(false);
                                if (response.code() == 500) {
                                    Toast.makeText(MyFavoriteActivity.this, "Server Error", Toast.LENGTH_SHORT).show();


                                } else {
                                    Toast.makeText(MyFavoriteActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

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
                                binding.progBar.setVisibility(View.GONE);
                                binding.swipeRefresh.setRefreshing(false);

                                if (t.getMessage() != null) {
                                    Log.e("error", t.getMessage());
                                    if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                        Toast.makeText(MyFavoriteActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(MyFavoriteActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                            } catch (Exception e) {
                            }
                        }
                    });
        } catch (Exception e) {

        }

    }

    public void setProductItemData(ProductModel productModel, int adapterPosition) {
        this.selectedPos = adapterPosition;
        Intent intent = new Intent(this, ProductDetailsActivity.class);
        intent.putExtra("product_id", productModel.getId());
        startActivityForResult(intent,100);
    }

    public void disLike(ProductModel productModel, int adapterPosition) {
        try {
            ProgressDialog dialog = Common.createProgressDialog(this, getString(R.string.wait));
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(true);
            dialog.show();

            Api.getService(Tags.base_url)
                    .like_disliked("Bearer " + userModel.getData().getToken(), productModel.getId())
                    .enqueue(new Callback<StatusResponse>() {
                        @Override
                        public void onResponse(Call<StatusResponse> call, Response<StatusResponse> response) {
                            if (response.isSuccessful()) {
                                dialog.dismiss();
                                if (response.body().getStatus() == 200) {
                                    productModelList.remove(adapterPosition);
                                    adapter.notifyItemRemoved(adapterPosition);
                                    if (productModelList.size() > 0) {
                                        binding.tvNoData.setVisibility(View.GONE);
                                    } else {
                                        binding.tvNoData.setVisibility(View.VISIBLE);

                                    }
                                } else {


                                    Toast.makeText(MyFavoriteActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

                                }
                            } else {

                                dialog.dismiss();
                                if (response.code() == 500) {
                                    Toast.makeText(MyFavoriteActivity.this, "Server Error", Toast.LENGTH_SHORT).show();


                                } else {
                                    Toast.makeText(MyFavoriteActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

                                    try {

                                        Log.e("error", response.code() + "_" + response.errorBody().string());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<StatusResponse> call, Throwable t) {
                            try {
                                dialog.dismiss();
                                if (t.getMessage() != null) {
                                    Log.e("error", t.getMessage());
                                    if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                        Toast.makeText(MyFavoriteActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(MyFavoriteActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                            } catch (Exception e) {
                            }
                        }
                    });
        } catch (Exception e) {
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==100&&resultCode==RESULT_OK){
            if (selectedPos!=-1){
                productModelList.remove(selectedPos);
                adapter.notifyItemRemoved(selectedPos);
                if (productModelList.size()>0){
                    binding.tvNoData.setVisibility(View.GONE);
                }else {
                    binding.tvNoData.setVisibility(View.VISIBLE);

                }
                selectedPos=-1;
            }
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }


}