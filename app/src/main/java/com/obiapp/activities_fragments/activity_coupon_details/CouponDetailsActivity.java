package com.obiapp.activities_fragments.activity_coupon_details;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.obiapp.R;
import com.obiapp.databinding.ActivityCouponDetailsBinding;
import com.obiapp.databinding.ActivityMyCouponsBinding;
import com.obiapp.language.Language;
import com.obiapp.models.CouponModel;
import com.obiapp.models.SingleCouponModel;
import com.obiapp.models.StatusResponse;
import com.obiapp.models.UserModel;
import com.obiapp.preferences.Preferences;
import com.obiapp.remote.Api;
import com.obiapp.tags.Tags;

import java.io.IOException;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CouponDetailsActivity extends AppCompatActivity {
    private ActivityCouponDetailsBinding binding;
    private String lang;
    private Preferences preferences;
    private UserModel userModel;
    private CouponModel couponModel;
    private int coupon_id = 0;


    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang", "ar")));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_coupon_details);
        getDataFromIntent();
        initView();
    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        coupon_id = intent.getIntExtra("data", 0);
    }

    private void initView() {
        preferences = Preferences.getInstance();
        userModel = preferences.getUserData(this);
        Paper.init(this);
        lang = Paper.book().read("lang", "ar");
        binding.setLang(lang);
        binding.llBack.setOnClickListener(view -> finish());
        binding.imageLike.setOnClickListener(view -> {
            if (couponModel.getLike_action() == null) {
                int newLikeCount = couponModel.getLikes_count() + 1;
                couponModel.setLikes_count(newLikeCount);

                CouponModel.LikeAction likeAction = new CouponModel.LikeAction();
                likeAction.setType("like");
                couponModel.setLike_action(likeAction);
                binding.setModel(couponModel);
                couponAction("like");

            } else {
                if (couponModel.getLike_action().getType().equals("like")) {


                    int newLikeCount = couponModel.getLikes_count() - 1;
                    couponModel.setLikes_count(newLikeCount);


                    couponModel.setLike_action(null);
                    binding.setModel(couponModel);
                    couponAction("deleteAction");

                } else {


                    int newLikeCount = couponModel.getLikes_count() + 1;
                    int newDisLike = couponModel.getDislikes_count() - 1;
                    couponModel.setLikes_count(newLikeCount);
                    couponModel.setDislikes_count(newDisLike);

                    CouponModel.LikeAction likeAction = new CouponModel.LikeAction();
                    likeAction.setType("like");
                    couponModel.setLike_action(likeAction);
                    binding.setModel(couponModel);
                    couponAction("like");
                }

            }

        });
        binding.imageDisLike.setOnClickListener(view -> {
            if (couponModel.getLike_action() == null) {
                int newDisLikeCount = couponModel.getDislikes_count() + 1;
                couponModel.setDislikes_count(newDisLikeCount);

                CouponModel.LikeAction likeAction = new CouponModel.LikeAction();
                likeAction.setType("dislike");
                couponModel.setLike_action(likeAction);
                binding.setModel(couponModel);
                couponAction("dislike");

            } else {

                if (couponModel.getLike_action().getType().equals("dislike")) {

                    int newDisLikeCount = couponModel.getDislikes_count() - 1;
                    couponModel.setDislikes_count(newDisLikeCount);

                    couponModel.setLike_action(null);
                    binding.setModel(couponModel);
                    couponAction("deleteAction");

                } else {

                    int newLikeCount = couponModel.getLikes_count() - 1;
                    int newDisLike = couponModel.getDislikes_count() + 1;
                    couponModel.setLikes_count(newLikeCount);
                    couponModel.setDislikes_count(newDisLike);

                    CouponModel.LikeAction likeAction = new CouponModel.LikeAction();
                    likeAction.setType("dislike");
                    couponModel.setLike_action(likeAction);
                    binding.setModel(couponModel);
                    couponAction("dislike");
                }

            }

        });
        binding.imageCopy.setOnClickListener(view -> {
            ClipboardManager manager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            ClipData data = ClipData.newPlainText("label", couponModel.getCoupon_code());
            manager.setPrimaryClip(data);
            Toast.makeText(this, getString(R.string.copied), Toast.LENGTH_SHORT).show();
        });
        getCoupon();
    }

    private void getCoupon() {
        if (userModel == null) {
            return;
        }
        try {

            Api.getService(Tags.base_url)
                    .getSingleCoupon(coupon_id, userModel.getData().getId())
                    .enqueue(new Callback<SingleCouponModel>() {
                        @Override
                        public void onResponse(Call<SingleCouponModel> call, Response<SingleCouponModel> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                if (response.body().getStatus() == 200) {
                                    binding.progBar.setVisibility(View.GONE);
                                    couponModel = response.body().getData();
                                    binding.setModel(couponModel);
                                    binding.consData.setVisibility(View.VISIBLE);

                                } else {
                                    Toast.makeText(CouponDetailsActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                binding.progBar.setVisibility(View.GONE);

                                if (response.code() == 500) {
                                    Toast.makeText(CouponDetailsActivity.this, "Server Error", Toast.LENGTH_SHORT).show();


                                } else {
                                    Toast.makeText(CouponDetailsActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

                                    try {

                                        Log.e("error", response.code() + "_" + response.errorBody().string());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<SingleCouponModel> call, Throwable t) {
                            try {
                                binding.progBar.setVisibility(View.GONE);

                                if (t.getMessage() != null) {
                                    Log.e("error", t.getMessage());
                                    if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                        Toast.makeText(CouponDetailsActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(CouponDetailsActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                            } catch (Exception e) {
                            }
                        }
                    });
        } catch (Exception e) {

        }

    }

    private void couponAction(String action) {

        if (userModel == null) {
            return;
        }
        try {

            Api.getService(Tags.base_url)
                    .couponAction("Bearer " + userModel.getData().getToken(), coupon_id, action)
                    .enqueue(new Callback<StatusResponse>() {
                        @Override
                        public void onResponse(Call<StatusResponse> call, Response<StatusResponse> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                if (response.body().getStatus() == 200) {
                                    getCoupon();

                                } else {
                                    Toast.makeText(CouponDetailsActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                if (action.equals("like")) {
                                    int newLikeCount = couponModel.getLikes_count() - 1;
                                    couponModel.setLikes_count(newLikeCount);
                                    binding.setModel(couponModel);
                                } else {
                                    int newDisLikeCount = couponModel.getDislikes_count() - 1;
                                    couponModel.setLikes_count(newDisLikeCount);
                                    binding.setModel(couponModel);
                                }
                                getCoupon();
                                binding.progBar.setVisibility(View.GONE);

                                if (response.code() == 500) {
                                    Toast.makeText(CouponDetailsActivity.this, "Server Error", Toast.LENGTH_SHORT).show();


                                } else {
                                    Toast.makeText(CouponDetailsActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

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
                                if (action.equals("like")) {
                                    int newLikeCount = couponModel.getLikes_count() - 1;
                                    couponModel.setLikes_count(newLikeCount);
                                    binding.setModel(couponModel);
                                } else {
                                    int newDisLikeCount = couponModel.getDislikes_count() - 1;
                                    couponModel.setLikes_count(newDisLikeCount);
                                    binding.setModel(couponModel);
                                }

                                getCoupon();
                                binding.progBar.setVisibility(View.GONE);

                                if (t.getMessage() != null) {
                                    Log.e("error", t.getMessage());
                                    if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                        Toast.makeText(CouponDetailsActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(CouponDetailsActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
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