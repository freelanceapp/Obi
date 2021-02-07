package com.obiapp.activities_fragments.activity_home.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.obiapp.R;
import com.obiapp.activities_fragments.activity_home.HomeActivity;
import com.obiapp.activities_fragments.activity_product_details.ProductDetailsActivity;
import com.obiapp.adapters.ProductAdapter;
import com.obiapp.databinding.FragmentHomeBinding;
import com.obiapp.models.ProductModel;
import com.obiapp.models.ProductsDataModel;
import com.obiapp.models.UserModel;
import com.obiapp.preferences.Preferences;
import com.obiapp.remote.Api;
import com.obiapp.tags.Tags;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fragment_Home extends Fragment {

    private HomeActivity activity;
    private FragmentHomeBinding binding;
    private Preferences preferences;
    private String lang;
    private UserModel userModel;
    private ProductAdapter productAdapter;
    private List<ProductModel> productModelList;
    private LinearLayoutManager manager;
    private Call<ProductsDataModel> call;


    public static Fragment_Home newInstance() {
        return new Fragment_Home();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        initView();
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    private void initView() {

        activity = (HomeActivity) getActivity();
        productModelList = new ArrayList<>();
        preferences = Preferences.getInstance();
        userModel = preferences.getUserData(activity);
        Paper.init(activity);
        lang = Paper.book().read("lang", "ar");
        binding.swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        manager = new LinearLayoutManager(activity);
        productAdapter = new ProductAdapter(productModelList, activity, this);
        binding.recView.setLayoutManager(manager);
        binding.recView.setAdapter(productAdapter);
        binding.edtSearch.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_SEARCH) {
                String query = binding.edtSearch.getText().toString();
                if (!query.isEmpty()) {
                    productModelList.clear();
                    productAdapter.notifyDataSetChanged();
                    binding.progBar.setVisibility(View.VISIBLE);
                    getProducts(query);
                }
            }
            return false;
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
                if (editable.toString().isEmpty()){
                    productModelList.clear();
                    productAdapter.notifyDataSetChanged();
                    binding.swipeRefresh.setRefreshing(false);
                    binding.progBar.setVisibility(View.VISIBLE);
                    binding.tvNoData.setVisibility(View.GONE);
                    getProducts(null);
                }
            }
        });
        getProducts(null);

        binding.swipeRefresh.setOnRefreshListener(() -> {
            String query = binding.edtSearch.getText().toString();
            if (query.isEmpty()) {
                query = null;
            }
            getProducts(query);

        });


    }

    private void getProducts(String query) {
        try {

            if (call!=null){
                call.cancel();
            }

            call = Api.getService(Tags.base_url)
                    .getProducts(query, "no");
            call.enqueue(new Callback<ProductsDataModel>() {
                @Override
                public void onResponse(Call<ProductsDataModel> call, Response<ProductsDataModel> response) {
                    binding.progBar.setVisibility(View.GONE);
                    binding.swipeRefresh.setRefreshing(false);
                    if (response.isSuccessful() && response.body() != null) {
                        if (response.body().getStatus() == 200) {
                            if (response.body().getData().size() > 0) {
                                productModelList.clear();
                                productModelList.addAll(response.body().getData());
                                productAdapter.notifyDataSetChanged();
                                binding.tvNoData.setVisibility(View.GONE);
                            } else {
                                binding.tvNoData.setVisibility(View.VISIBLE);

                            }
                        } else {
                            Toast.makeText(activity, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        binding.progBar.setVisibility(View.GONE);
                        binding.swipeRefresh.setRefreshing(false);

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
                        binding.progBar.setVisibility(View.GONE);
                        binding.swipeRefresh.setRefreshing(false);

                        if (t.getMessage() != null) {
                            Log.e("error", t.getMessage());
                            if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                Toast.makeText(activity, R.string.something, Toast.LENGTH_SHORT).show();
                            } else if (t.getMessage().toLowerCase().contains("socket")||t.getMessage().toLowerCase().contains("canceled")){

                            }else {
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


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


    }


    public void setProductItemData(ProductModel productModel) {
        Intent intent = new Intent(activity, ProductDetailsActivity.class);
        intent.putExtra("product_id",productModel.getId());
        startActivity(intent);
    }
}
