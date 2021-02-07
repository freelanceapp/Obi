package com.obiapp.activities_fragments.activity_my_coupon;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.obiapp.R;
import com.obiapp.activities_fragments.activity_add_coupon.AddCouponActivity;
import com.obiapp.activities_fragments.activity_coupon_details.CouponDetailsActivity;
import com.obiapp.adapters.CouponAdapter;
import com.obiapp.databinding.ActivityLoginBinding;
import com.obiapp.databinding.ActivityMyCouponsBinding;
import com.obiapp.language.Language;
import com.obiapp.models.CouponDataModel;
import com.obiapp.models.CouponModel;
import com.obiapp.models.StatusResponse;
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

public class MyCouponsActivity extends AppCompatActivity {

    private ActivityMyCouponsBinding binding;
    private String lang;
    private Preferences preferences;
    private UserModel userModel;
    private List<CouponModel> couponModelList;
    private CouponAdapter adapter;


    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang", "ar")));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_coupons);
        initView();
    }

    private void initView() {
        couponModelList = new ArrayList<>();
        preferences = Preferences.getInstance();
        userModel = preferences.getUserData(this);
        Paper.init(this);
        lang = Paper.book().read("lang", "ar");
        binding.setLang(lang);
        binding.swipeRefresh.setColorSchemeResources(R.color.colorPrimary);

        binding.recView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CouponAdapter(couponModelList, this);
        binding.recView.setAdapter(adapter);
        binding.llBack.setOnClickListener(view -> finish());
        binding.fabAdd.setOnClickListener(view -> {
            if (userModel != null) {
                Intent intent = new Intent(this, AddCouponActivity.class);
                startActivityForResult(intent, 100);
            } else {
                Toast.makeText(this, getString(R.string.please_sign_in_or_sign_up), Toast.LENGTH_SHORT).show();
            }

        });
        binding.swipeRefresh.setOnRefreshListener(this::getCoupons);


        getCoupons();

    }

    private void getCoupons() {
        if (userModel == null) {
            binding.progBar.setVisibility(View.GONE);
            binding.swipeRefresh.setRefreshing(false);
            binding.tvNoData.setVisibility(View.VISIBLE);
            return;
        }
        try {

            Api.getService(Tags.base_url)
                    .getMyCoupon("Bearer " + userModel.getData().getToken())
                    .enqueue(new Callback<CouponDataModel>() {
                        @Override
                        public void onResponse(Call<CouponDataModel> call, Response<CouponDataModel> response) {
                            binding.progBar.setVisibility(View.GONE);
                            binding.swipeRefresh.setRefreshing(false);

                            if (response.isSuccessful() && response.body() != null) {
                                if (response.body().getStatus() == 200) {
                                    if (response.body().getData().size() > 0) {
                                        couponModelList.clear();
                                        couponModelList.addAll(response.body().getData());
                                        adapter.notifyDataSetChanged();
                                        binding.tvNoData.setVisibility(View.GONE);
                                    } else {
                                        binding.tvNoData.setVisibility(View.VISIBLE);

                                    }
                                } else {
                                    Toast.makeText(MyCouponsActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                binding.progBar.setVisibility(View.GONE);
                                binding.swipeRefresh.setRefreshing(false);

                                if (response.code() == 500) {
                                    Toast.makeText(MyCouponsActivity.this, "Server Error", Toast.LENGTH_SHORT).show();


                                } else {
                                    Toast.makeText(MyCouponsActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

                                    try {

                                        Log.e("error", response.code() + "_" + response.errorBody().string());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<CouponDataModel> call, Throwable t) {
                            try {
                                binding.progBar.setVisibility(View.GONE);
                                binding.swipeRefresh.setRefreshing(false);

                                if (t.getMessage() != null) {
                                    Log.e("error", t.getMessage());
                                    if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                        Toast.makeText(MyCouponsActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(MyCouponsActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
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
        if (requestCode == 100 && resultCode == RESULT_OK) {
            getCoupons();
        }
    }

    public void copy(String couponCode) {
        ClipboardManager manager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData data = ClipData.newPlainText("label", couponCode);
        manager.setPrimaryClip(data);
        Toast.makeText(this, getString(R.string.copied), Toast.LENGTH_SHORT).show();
    }

    public void setItemData(CouponModel couponModel) {
        Intent intent = new Intent(this, CouponDetailsActivity.class);
        intent.putExtra("data", couponModel.getId());
        startActivity(intent);
    }

    public void deleteCoupon(int coupon_id, int pos) {

        if (userModel == null) {
            return;
        }
        try {

            Api.getService(Tags.base_url)
                    .deleteCoupon("Bearer " + userModel.getData().getToken(), coupon_id)
                    .enqueue(new Callback<StatusResponse>() {
                        @Override
                        public void onResponse(Call<StatusResponse> call, Response<StatusResponse> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                if (response.body().getStatus() == 200) {
                                    couponModelList.remove(pos);
                                    adapter.notifyItemRemoved(pos);
                                    if (couponModelList.size() > 0) {
                                        binding.tvNoData.setVisibility(View.GONE);
                                    } else {
                                        binding.tvNoData.setVisibility(View.VISIBLE);
                                    }
                                } else {
                                    Toast.makeText(MyCouponsActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                binding.progBar.setVisibility(View.GONE);

                                if (response.code() == 500) {
                                    Toast.makeText(MyCouponsActivity.this, "Server Error", Toast.LENGTH_SHORT).show();


                                } else {
                                    Toast.makeText(MyCouponsActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

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
                                binding.progBar.setVisibility(View.GONE);

                                if (t.getMessage() != null) {
                                    Log.e("error", t.getMessage());
                                    if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                        Toast.makeText(MyCouponsActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(MyCouponsActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                            } catch (Exception e) {
                            }
                        }
                    });
        } catch (Exception e) {

        }

    }
}