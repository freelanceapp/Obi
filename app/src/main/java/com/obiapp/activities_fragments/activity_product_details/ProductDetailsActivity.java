package com.obiapp.activities_fragments.activity_product_details;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;


import com.obiapp.R;
import com.obiapp.activities_fragments.activity_chat.ChatActivity;
import com.obiapp.activities_fragments.activity_chat_admin.ChatAdminActivity;
import com.obiapp.activities_fragments.activity_profile_products.ProfileProductsActivity;
import com.obiapp.adapters.ProductDetailsAdapter;
import com.obiapp.adapters.SliderAdapter;
import com.obiapp.databinding.ActivityProductDetailsBinding;
import com.obiapp.language.Language;
import com.obiapp.models.AdminMessageDataModel;
import com.obiapp.models.ChatUserModel;
import com.obiapp.models.MessageModel;
import com.obiapp.models.ProductImageModel;
import com.obiapp.models.ProductModel;
import com.obiapp.models.RoomDataModel2;
import com.obiapp.models.SingleProductDataModel;
import com.obiapp.models.StatusResponse;
import com.obiapp.models.UserModel;
import com.obiapp.preferences.Preferences;
import com.obiapp.remote.Api;
import com.obiapp.share.Common;
import com.obiapp.tags.Tags;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetailsActivity extends AppCompatActivity {
    private ActivityProductDetailsBinding binding;
    private String lang;
    private Preferences preferences;
    private UserModel userModel;
    private int product_id;
    private ProductModel productModel;
    private ProductDetailsAdapter adapter;
    private List<ProductModel.ProductDetail> productDetailsModelList;
    private SliderAdapter sliderAdapter;
    private List<ProductImageModel> productImageModelList;
    private boolean isDataChanged = false;

    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang", "ar")));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_product_details);
        getDataFromIntent();

        initView();

    }


    private void getDataFromIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            product_id = intent.getIntExtra("product_id", 0);

        }

    }


    private void initView() {


        productImageModelList = new ArrayList<>();
        productDetailsModelList = new ArrayList<>();
        Paper.init(this);
        preferences = Preferences.getInstance();
        userModel = preferences.getUserData(this);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        binding.setLang(lang);
        binding.setUserModel(userModel);
        binding.tab.setupWithViewPager(binding.pager);
        binding.recView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ProductDetailsAdapter(productDetailsModelList, this);
        binding.recView.setAdapter(adapter);

        sliderAdapter = new SliderAdapter(productImageModelList, this);
        binding.pager.setAdapter(sliderAdapter);

        binding.checkFavorite.setOnClickListener(view -> {
            if (binding.checkFavorite.isChecked()) {
                like_dislike(true);
            } else {
                like_dislike(false);

            }
        });

        binding.iconCopy.setOnClickListener(view -> {

            ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("label", productModel.getUser().getPhone_code() + productModel.getUser().getPhone());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, R.string.copied, Toast.LENGTH_SHORT).show();
        });

        binding.flCall.setOnClickListener(view -> {
            String phone = productModel.getUser().getPhone_code() + productModel.getUser().getPhone();
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
            startActivity(intent);
        });

        binding.iconWhatsApp.setOnClickListener(view -> {
            String phone = productModel.getUser().getPhone_code() + productModel.getUser().getPhone();
            String url = "https://api.whatsapp.com/send?phone=" + phone;
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        });
        binding.imgWarning.setOnClickListener(view -> {
           /* if (productModel.getIs_report().equals("yes")) {
                addReport(true);
            } else {
                addReport(false);

            }*/
        });


        /*binding.image.setOnClickListener(v -> {
            Intent intent = new Intent(this, ProfileProductsActivity.class);
            intent.putExtra("user_id", productModel.getUser().getId());
            startActivity(intent);
        });*/

        binding.llBack.setOnClickListener(view -> {
            onBackPressed();
        });

        binding.llChat.setOnClickListener(view -> {
            createChat();
        });

        getProductById();
    }

    private void createChat() {
        try {
            ProgressDialog dialog = Common.createProgressDialog(this, getString(R.string.wait));
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
            Api.getService(Tags.base_url)
                    .getAdminChatMessage("Bearer " + userModel.getData().getToken(),productModel.getUser().getId())
                    .enqueue(new Callback<AdminMessageDataModel>() {
                        @Override
                        public void onResponse(Call<AdminMessageDataModel> call, Response<AdminMessageDataModel> response) {
                            dialog.dismiss();
                            if (response.isSuccessful()) {

                                if (response.body() != null && response.body().getStatus() == 200) {
                                    navigateToChatActivity(response.body().getData().getRoom());
                                } else {
                                    Toast.makeText(ProductDetailsActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

                                }

                            } else {
                                dialog.dismiss();
                                if (response.code() == 500) {
                                    Toast.makeText(ProductDetailsActivity.this, "Server Error", Toast.LENGTH_SHORT).show();


                                } else {
                                    Toast.makeText(ProductDetailsActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

                                    try {

                                        Log.e("error", response.code() + "_" + response.errorBody().string());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<AdminMessageDataModel> call, Throwable t) {
                            try {
                                dialog.dismiss();
                                if (t.getMessage() != null) {
                                    Log.e("error", t.getMessage());
                                    if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                        Toast.makeText(ProductDetailsActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(ProductDetailsActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                            } catch (Exception e) {
                            }
                        }
                    });
        } catch (Exception e) {

        }
    }

    private void navigateToChatActivity(MessageModel.RoomModel data) {
        ChatUserModel chatUserModel = new ChatUserModel(data.getAdmin_id(), getString(R.string.admin),"", data.getId(),product_id);
        Intent intent = new Intent(this, ChatAdminActivity.class);
        intent.putExtra("data", chatUserModel);
        startActivity(intent);
    }

    private void getProductById() {

        try {
            binding.scrollView.setVisibility(View.GONE);
            binding.progBar.setVisibility(View.VISIBLE);
            String user_id = "";
            if (userModel != null) {
                user_id = String.valueOf(userModel.getData().getId());
            }

            Api.getService(Tags.base_url)
                    .getProductById(product_id, user_id)
                    .enqueue(new Callback<SingleProductDataModel>() {
                        @Override
                        public void onResponse(Call<SingleProductDataModel> call, Response<SingleProductDataModel> response) {
                            binding.progBar.setVisibility(View.GONE);
                            if (response.isSuccessful()) {

                                if (response.body().getStatus() == 200) {
                                    if (response.body().getData() != null) {
                                        productModel = response.body().getData();
                                        if (productModel != null) {
                                            updateUi();
                                        }
                                    }

                                } else {
                                    Toast.makeText(ProductDetailsActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                }


                            } else {
                                binding.progBar.setVisibility(View.GONE);

                                if (response.code() == 500) {
                                    Toast.makeText(ProductDetailsActivity.this, "Server Error", Toast.LENGTH_SHORT).show();


                                } else {
                                    Toast.makeText(ProductDetailsActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

                                    try {

                                        Log.e("error", response.code() + "_" + response.errorBody().string());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<SingleProductDataModel> call, Throwable t) {
                            try {
                                binding.progBar.setVisibility(View.GONE);

                                if (t.getMessage() != null) {
                                    Log.e("error", t.getMessage());
                                    if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                        Toast.makeText(ProductDetailsActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(ProductDetailsActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                            } catch (Exception e) {
                            }
                        }
                    });
        } catch (
                Exception e) {

        }

    }

    private void updateUi() {

        /*if (productModel.getIs_report().equals("yes")) {
            binding.imgWarning.setColorFilter(ContextCompat.getColor(ProductDetailsActivity.this, R.color.colorPrimary));
        } else {
            binding.imgWarning.setColorFilter(ContextCompat.getColor(ProductDetailsActivity.this, R.color.gray4));

        }
*/

        if (productModel.getProduct_details() != null && productModel.getProduct_details().size() > 0) {
            productDetailsModelList.addAll(productModel.getProduct_details());
            adapter.notifyDataSetChanged();
        }

        if (productModel.getIs_favorite().equals("yes")) {
            binding.checkFavorite.setChecked(true);
        } else {
            binding.checkFavorite.setChecked(false);

        }

       /* if (productModel.getIs_report().equals("yes")) {
            binding.imgWarning.setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary));
        } else {
            binding.imgWarning.setColorFilter(ContextCompat.getColor(this, R.color.gray4));

        }*/

        if (productModel.getVideo() != null) {
            productImageModelList.add(new ProductImageModel(0, productModel.getVideo(), "video"));
        }
        if (productModel.getProduct_images() != null && productModel.getProduct_images().size() > 0) {
            binding.flNoSlider.setVisibility(View.GONE);
            binding.flSlider.setVisibility(View.VISIBLE);
            productImageModelList.addAll(productModel.getProduct_images());

            sliderAdapter.notifyDataSetChanged();
        } else {
            binding.flNoSlider.setVisibility(View.VISIBLE);
            binding.flSlider.setVisibility(View.GONE);
        }


        binding.setModel(productModel);

        binding.scrollView.setVisibility(View.VISIBLE);


    }

    public void like_dislike(boolean isChecked) {
        if (userModel == null) {
            if (isChecked) {
                binding.checkFavorite.setChecked(false);
                productModel.setIs_favorite("no");

            } else {
                binding.checkFavorite.setChecked(true);
                productModel.setIs_favorite("yes");


            }
            Toast.makeText(this, getString(R.string.please_sign_in_or_sign_up), Toast.LENGTH_SHORT).show();

            return;
        }

        try {
            Api.getService(Tags.base_url)
                    .like_disliked("Bearer " + userModel.getData().getToken(), product_id)
                    .enqueue(new Callback<StatusResponse>() {
                        @Override
                        public void onResponse(Call<StatusResponse> call, Response<StatusResponse> response) {
                            if (response.isSuccessful()) {
                                if (response.body().getStatus() == 200) {
                                    if (isChecked) {
                                        productModel.setIs_favorite("yes");
                                        binding.checkFavorite.setChecked(true);
                                        isDataChanged = true;

                                    } else {
                                        productModel.setIs_favorite("no");

                                        binding.checkFavorite.setChecked(false);

                                    }
                                } else {

                                    if (isChecked) {
                                        binding.checkFavorite.setChecked(false);
                                        productModel.setIs_favorite("no");


                                    } else {
                                        binding.checkFavorite.setChecked(true);
                                        productModel.setIs_favorite("yes");


                                    }

                                    Toast.makeText(ProductDetailsActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

                                }
                            } else {

                                if (isChecked) {
                                    binding.checkFavorite.setChecked(false);
                                    productModel.setIs_favorite("no");

                                } else {
                                    binding.checkFavorite.setChecked(true);
                                    productModel.setIs_favorite("yes");


                                }
                                if (response.code() == 500) {
                                    Toast.makeText(ProductDetailsActivity.this, "Server Error", Toast.LENGTH_SHORT).show();


                                } else {
                                    Toast.makeText(ProductDetailsActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

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
                                if (isChecked) {
                                    binding.checkFavorite.setChecked(false);

                                } else {
                                    binding.checkFavorite.setChecked(true);

                                }
                                if (t.getMessage() != null) {
                                    Log.e("error", t.getMessage());
                                    if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                        Toast.makeText(ProductDetailsActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(ProductDetailsActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                            } catch (Exception e) {
                            }
                        }
                    });
        } catch (Exception e) {
        }

    }

    public void addReport(boolean isReport) {

        if (userModel == null) {
            if (isReport) {
                productModel.setIs_report("yes");


            } else {
                productModel.setIs_report("no");

            }
            Toast.makeText(this, getString(R.string.please_sign_in_or_sign_up), Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            Api.getService(Tags.base_url)
                    .report("Bearer " + userModel.getData().getToken(), product_id, "0")
                    .enqueue(new Callback<StatusResponse>() {
                        @Override
                        public void onResponse(Call<StatusResponse> call, Response<StatusResponse> response) {
                            if (response.isSuccessful()) {
                                if (response.body().getStatus() == 200) {
                                    if (isReport) {
                                        productModel.setIs_report("no");
                                        binding.imgWarning.setColorFilter(ContextCompat.getColor(ProductDetailsActivity.this, R.color.gray4));


                                    } else {
                                        productModel.setIs_report("yes");
                                        binding.imgWarning.setColorFilter(ContextCompat.getColor(ProductDetailsActivity.this, R.color.colorPrimary));

                                    }
                                } else {

                                    if (isReport) {
                                        productModel.setIs_report("yes");


                                    } else {
                                        productModel.setIs_report("no");

                                    }

                                    Toast.makeText(ProductDetailsActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

                                }
                            } else {

                                if (isReport) {
                                    productModel.setIs_report("yes");
                                    binding.imgWarning.setColorFilter(ContextCompat.getColor(ProductDetailsActivity.this, R.color.colorPrimary));


                                } else {
                                    productModel.setIs_report("no");
                                    binding.imgWarning.setColorFilter(ContextCompat.getColor(ProductDetailsActivity.this, R.color.gray4));


                                }
                                if (response.code() == 500) {
                                    Toast.makeText(ProductDetailsActivity.this, "Server Error", Toast.LENGTH_SHORT).show();


                                } else {
                                    Toast.makeText(ProductDetailsActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

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
                                if (isReport) {
                                    productModel.setIs_report("yes");
                                    binding.imgWarning.setColorFilter(ContextCompat.getColor(ProductDetailsActivity.this, R.color.colorPrimary));


                                } else {
                                    productModel.setIs_report("no");
                                    binding.imgWarning.setColorFilter(ContextCompat.getColor(ProductDetailsActivity.this, R.color.gray4));


                                }
                                if (t.getMessage() != null) {
                                    Log.e("error", t.getMessage());
                                    if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                        Toast.makeText(ProductDetailsActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(ProductDetailsActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
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
    public void onBackPressed() {
        if (isDataChanged) {
            setResult(RESULT_OK);
        }
        finish();
    }
}
