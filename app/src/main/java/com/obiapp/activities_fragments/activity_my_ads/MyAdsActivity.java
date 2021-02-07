package com.obiapp.activities_fragments.activity_my_ads;

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
import com.obiapp.adapters.MyProductAdapter;
import com.obiapp.databinding.ActivityMyAdsBinding;
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

public class MyAdsActivity extends AppCompatActivity {
    private ActivityMyAdsBinding binding;
    private Preferences preference;
    private UserModel userModel;
    private MyProductAdapter adapter;
    private List<ProductModel> productModelList;
    private boolean isDataChanged = false;
    private String lang;

    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang", "ar")));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_my_ads);
        initView();
    }

    private void initView() {
        productModelList = new ArrayList<>();
        Paper.init(this);
        lang = Paper.book().read("lang","ar");
        binding.setLang(lang);
        preference = Preferences.getInstance();
        userModel = preference.getUserData(this);
        binding.recView.setLayoutManager(new GridLayoutManager(this,2));
        adapter = new MyProductAdapter(productModelList,this);
        binding.recView.setAdapter(adapter);
        binding.swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        binding.swipeRefresh.setOnRefreshListener(this::getMyProducts);
        binding.llBack.setOnClickListener(view -> onBackPressed());

        getMyProducts();
    }

    private void getMyProducts()
    {

        try {

            Api.getService(Tags.base_url)
                    .getMyProducts("Bearer "+userModel.getData().getToken())
                    .enqueue(new Callback<ProductsDataModel>() {
                        @Override
                        public void onResponse(Call<ProductsDataModel> call, Response<ProductsDataModel> response) {
                            binding.progBar.setVisibility(View.GONE);
                            binding.swipeRefresh.setRefreshing(false);
                            if (response.isSuccessful() && response.body() != null ) {
                                if (response.body().getStatus()==200){
                                    if (response.body().getData().size()>0){
                                        productModelList.clear();
                                        productModelList.addAll(response.body().getData());
                                        adapter.notifyDataSetChanged();
                                        binding.tvNoData.setVisibility(View.GONE);
                                    }else {
                                        binding.tvNoData.setVisibility(View.VISIBLE);

                                    }
                                }else {
                                    Toast.makeText(MyAdsActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                binding.progBar.setVisibility(View.GONE);
                                binding.swipeRefresh.setRefreshing(false);
                                if (response.code() == 500) {
                                    Toast.makeText(MyAdsActivity.this, "Server Error", Toast.LENGTH_SHORT).show();


                                } else {
                                    Toast.makeText(MyAdsActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

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
                                        Toast.makeText(MyAdsActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(MyAdsActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
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
        Intent intent = new Intent(this, ProductDetailsActivity.class);
        intent.putExtra("product_id",productModel.getId());
        startActivity(intent);
    }

    public void deleteAd(ProductModel productModel,int pos){

        ProgressDialog dialog = Common.createProgressDialog(this, getString(R.string.wait));
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(true);
        dialog.show();

        Api.getService(Tags.base_url)
                .deleteProduct("Bearer "+userModel.getData().getToken(),productModel.getId())
                .enqueue(new Callback<StatusResponse>() {
                    @Override
                    public void onResponse(Call<StatusResponse> call, Response<StatusResponse> response) {
                        dialog.dismiss();
                        if (response.isSuccessful()) {
                            if (response.body()!=null&&response.body().getStatus()==200){
                                isDataChanged = true;
                                productModelList.remove(pos);
                                adapter.notifyItemRemoved(pos);
                                if (productModelList.size()>0){
                                    binding.tvNoData.setVisibility(View.GONE);
                                }else {
                                    binding.tvNoData.setVisibility(View.VISIBLE);

                                }
                            }else {
                                Toast.makeText(MyAdsActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

                            }
                        } else {
                            dialog.dismiss();
                            try {
                                Log.e("error", response.code() + "__" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            if (response.code() == 500) {
                                Toast.makeText(MyAdsActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(MyAdsActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
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
                                    Toast.makeText(MyAdsActivity.this, getString(R.string.something), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(MyAdsActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception e) {
                            Log.e("Error", e.getMessage() + "__");
                        }
                    }
                });
    }

    @Override
    public void onBackPressed() {
        if (isDataChanged){
            setResult(RESULT_OK);
        }
        finish();
    }
}