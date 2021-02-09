package com.obiapp.activities_fragments.activity_notification;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;


import com.obiapp.R;
import com.obiapp.adapters.NotificationAdapter;
import com.obiapp.databinding.ActivityNotificationBinding;
import com.obiapp.language.Language;
import com.obiapp.models.NotificationDataModel;
import com.obiapp.models.NotificationModel;
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

public class NotificationActivity extends AppCompatActivity {
    private ActivityNotificationBinding binding;
    private String lang;
    private List<NotificationModel> notificationModelList;
    private NotificationAdapter adapter;
    private Preferences preferences;
    private UserModel userModel;


    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang", "ar")));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(  this, R.layout.activity_notification);
        initView();
    }


    private void initView() {
        Paper.init(this);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        binding.setLang(lang);
        binding.progBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(   this, R.color.colorPrimary), PorterDuff.Mode.SRC_IN);

        preferences = Preferences.getInstance();
        userModel = preferences.getUserData(this);
        notificationModelList = new ArrayList<>();
        Paper.init(this);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        binding.setLang(lang);
        binding.swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        binding.progBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        binding.recView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NotificationAdapter(notificationModelList, this);
        binding.recView.setAdapter(adapter);
        binding.llBack.setOnClickListener(view -> finish());
        binding.swipeRefresh.setOnRefreshListener(this::getNotification);
        getNotification();

    }

    private void getNotification() {
        if (userModel == null) {
            binding.swipeRefresh.setRefreshing(false);
            binding.progBar.setVisibility(View.GONE);
            binding.tvNoData.setVisibility(View.VISIBLE);
            return;
        }
        try {
            Api.getService(Tags.base_url)
                    .getNotification("Bearer " + userModel.getData().getToken())
                    .enqueue(new Callback<NotificationDataModel>() {
                        @Override
                        public void onResponse(Call<NotificationDataModel> call, Response<NotificationDataModel> response) {
                            binding.swipeRefresh.setRefreshing(false);
                            binding.progBar.setVisibility(View.GONE);
                            if (response.isSuccessful() ) {

                                if (response.body()!=null&&response.body().getStatus()==200){
                                    notificationModelList.clear();
                                    notificationModelList.addAll(response.body().getData());
                                    if (notificationModelList.size() > 0) {

                                        adapter.notifyDataSetChanged();

                                        binding.tvNoData.setVisibility(View.GONE);
                                    } else {
                                        binding.tvNoData.setVisibility(View.VISIBLE);

                                    }

                                }else {
                                    Toast.makeText(NotificationActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

                                }

                            } else {
                                binding.swipeRefresh.setRefreshing(false);
                                binding.progBar.setVisibility(View.GONE);
                                if (response.code() == 500) {
                                    Toast.makeText(NotificationActivity.this, "Server Error", Toast.LENGTH_SHORT).show();


                                } else {
                                    Toast.makeText(NotificationActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

                                    try {

                                        Log.e("error", response.code() + "_" + response.errorBody().string());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<NotificationDataModel> call, Throwable t) {
                            try {
                                binding.swipeRefresh.setRefreshing(false);
                                binding.progBar.setVisibility(View.GONE);

                                if (t.getMessage() != null) {
                                    Log.e("error", t.getMessage());
                                    if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                        Toast.makeText(NotificationActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(NotificationActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                            } catch (Exception e) {
                            }
                        }
                    });
        } catch (Exception e) {

        }
    }


    public void setItemData(NotificationModel notificationModel, int adapterPosition) {


        try {
            ProgressDialog dialog = Common.createProgressDialog(this, getString(R.string.wait));
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
            Api.getService(Tags.base_url)
                    .deleteNotification("Bearer " + userModel.getData().getToken(), notificationModel.getId())
                    .enqueue(new Callback<StatusResponse>() {
                        @Override
                        public void onResponse(Call<StatusResponse> call, Response<StatusResponse> response) {
                            dialog.dismiss();
                            if (response.isSuccessful()) {

                                if (response.body() != null && response.body().getStatus() == 200) {
                                    notificationModelList.remove(adapterPosition);
                                    adapter.notifyItemRemoved(adapterPosition);

                                    if (notificationModelList.size() > 0) {

                                        binding.tvNoData.setVisibility(View.GONE);
                                    } else {
                                        binding.tvNoData.setVisibility(View.VISIBLE);

                                    }
                                } else {
                                    Toast.makeText(NotificationActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

                                }

                            } else {
                                dialog.dismiss();
                                if (response.code() == 500) {
                                    Toast.makeText(NotificationActivity.this, "Server Error", Toast.LENGTH_SHORT).show();


                                } else {
                                    Toast.makeText(NotificationActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

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
                                        Toast.makeText(NotificationActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(NotificationActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
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
