package com.obiapp.activities_fragments.activity_department_details;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.obiapp.R;
import com.obiapp.activities_fragments.activity_product_details.ProductDetailsActivity;
import com.obiapp.adapters.ProductAdapter;
import com.obiapp.adapters.TypeAdapter;
import com.obiapp.databinding.ActivityDepartmentDetailsBinding;
import com.obiapp.language.Language;
import com.obiapp.models.DepartmentModel;
import com.obiapp.models.ProductModel;
import com.obiapp.models.ProductsDataModel;
import com.obiapp.models.TypeDataModel;
import com.obiapp.models.TypeModel;
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

public class DepartmentDetailsActivity extends AppCompatActivity {
    private ActivityDepartmentDetailsBinding binding;
    private String lang;
    private List<ProductModel> productModelList;
    private ProductAdapter adapter;
    private DepartmentModel departmentModel;
    private List<TypeModel> typeModelList;
    private TypeAdapter typeAdapter;
    private Preferences preferences;
    private UserModel userModel;
    private int child_pos = 0;

    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang", "ar")));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_department_details);
        getDataFromIntent();
        initView();

    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        departmentModel = (DepartmentModel) intent.getSerializableExtra("data");
        child_pos = intent.getIntExtra("child_pos", 0);
        DepartmentModel.SubCategory subCategory = departmentModel.getSub_categories().get(child_pos);
        subCategory.setSelected(true);


    }

    private void initView() {
        productModelList = new ArrayList<>();
        typeModelList = new ArrayList<>();

        preferences = Preferences.getInstance();
        userModel = preferences.getUserData(this);
        Paper.init(this);
        lang = Paper.book().read("lang", "ar");


        binding.setLang(lang);
        String title = departmentModel.getTitle()+" ("+departmentModel.getSub_categories().get(child_pos).getTitle()+")";
        binding.setTitle(title);


        binding.recViewSubDepartment.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        typeAdapter = new TypeAdapter(typeModelList, this);
        binding.recViewSubDepartment.setAdapter(typeAdapter);

        binding.recView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ProductAdapter(productModelList,this,null);
        binding.recView.setAdapter(adapter);
        binding.llBack.setOnClickListener(view -> onBackPressed());

        getTypes();
    }

    private void getTypes() {
        int category_id = departmentModel.getId();
        int suc_category_id = departmentModel.getSub_categories().get(child_pos).getId();
        try {
            Api.getService(Tags.base_url)
                    .getTypes(category_id,suc_category_id)
                    .enqueue(new Callback<TypeDataModel>() {
                        @Override
                        public void onResponse(Call<TypeDataModel> call, Response<TypeDataModel> response) {
                            binding.progBarType.setVisibility(View.GONE);
                            if (response.isSuccessful() && response.body() != null) {
                                if (response.body().getStatus() == 200) {
                                    if (response.body().getData().size() > 0) {
                                        typeModelList.clear();
                                        typeModelList.addAll(response.body().getData());
                                        TypeModel typeModel = typeModelList.get(0);
                                        typeModel.setSelected(true);
                                        typeModelList.set(0,typeModel);
                                        typeAdapter.notifyDataSetChanged();
                                        getProducts(typeModel.getId());
                                        binding.tvNoData.setVisibility(View.GONE);
                                    } else {
                                        binding.tvNoData.setVisibility(View.VISIBLE);

                                    }
                                } else {
                                    Toast.makeText(DepartmentDetailsActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                binding.progBarType.setVisibility(View.GONE);

                                if (response.code() == 500) {
                                    Toast.makeText(DepartmentDetailsActivity.this, "Server Error", Toast.LENGTH_SHORT).show();


                                } else {
                                    Toast.makeText(DepartmentDetailsActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

                                    try {

                                        Log.e("error", response.code() + "_" + response.errorBody().string());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<TypeDataModel> call, Throwable t) {
                            try {
                                binding.progBarType.setVisibility(View.GONE);

                                if (t.getMessage() != null) {
                                    Log.e("error", t.getMessage());
                                    if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                        Toast.makeText(DepartmentDetailsActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(DepartmentDetailsActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                            } catch (Exception e) {
                            }
                        }
                    });
        } catch (Exception e) {

        }
    }


    private void getProducts(int type_id) {
        productModelList.clear();
        adapter.notifyDataSetChanged();
        binding.progBar.setVisibility(View.VISIBLE);
        binding.tvNoData.setVisibility(View.GONE);
        int sub_category_id = departmentModel.getSub_categories().get(child_pos).getId();
        try {
            Api.getService(Tags.base_url)
                    .getFilteredProducts(departmentModel.getId(),sub_category_id, "no","desc",type_id)
                    .enqueue(new Callback<ProductsDataModel>() {
                        @Override
                        public void onResponse(Call<ProductsDataModel> call, Response<ProductsDataModel> response) {
                            binding.progBar.setVisibility(View.GONE);
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
                                    Toast.makeText(DepartmentDetailsActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                binding.progBar.setVisibility(View.GONE);

                                if (response.code() == 500) {
                                    Toast.makeText(DepartmentDetailsActivity.this, "Server Error", Toast.LENGTH_SHORT).show();


                                } else {
                                    Toast.makeText(DepartmentDetailsActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

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

                                if (t.getMessage() != null) {
                                    Log.e("error", t.getMessage());
                                    if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                        Toast.makeText(DepartmentDetailsActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(DepartmentDetailsActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                            } catch (Exception e) {
                            }
                        }
                    });
        } catch (Exception e) {

        }
    }

    public void setItemData(TypeModel model) {
        getProducts(model.getId());
    }


    public void setProductItemData(ProductModel productModel) {
        Intent intent = new Intent(this, ProductDetailsActivity.class);
        intent.putExtra("product_id",productModel.getId());
        startActivity(intent);
    }
}