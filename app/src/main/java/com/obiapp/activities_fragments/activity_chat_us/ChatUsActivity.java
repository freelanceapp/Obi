package com.obiapp.activities_fragments.activity_chat_us;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.obiapp.BuildConfig;
import com.obiapp.R;
import com.obiapp.activities_fragments.activity_chat_admin.ChatAdminActivity;
import com.obiapp.databinding.ActivityChatUsBinding;
import com.obiapp.databinding.ActivitySettingBinding;
import com.obiapp.language.Language;
import com.obiapp.models.AdminRoomDataModel;
import com.obiapp.models.ChatUserModel;
import com.obiapp.models.SettingDataModel;
import com.obiapp.models.UserModel;
import com.obiapp.preferences.Preferences;
import com.obiapp.remote.Api;
import com.obiapp.share.Common;
import com.obiapp.tags.Tags;

import java.io.IOException;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatUsActivity extends AppCompatActivity {
    private ActivityChatUsBinding binding;
    private Preferences preference;
    private UserModel userModel;
    private String lang;
    private SettingDataModel.Setting setting;
    private final String regex = "https?:\\/\\/(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_\\+.~#?&//=]*)";

    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang", "ar")));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat_us);
        initView();
    }

    private void initView() {
        preference = Preferences.getInstance();
        userModel = preference.getUserData(this);
        Paper.init(this);
        lang = Paper.book().read("lang", "ar");
        binding.setLang(lang);
        binding.llBack.setOnClickListener(view -> finish());

        binding.imageCall.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel: " + setting.getPhone1()));
            startActivity(intent);
        });

        binding.imageWhats.setOnClickListener(view -> {
            String url = "https://api.whatsapp.com/send?phone=" + setting.getWhatsapp();
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        });
        binding.imageFacebook.setOnClickListener(view -> {
            if (setting.getFacebook().matches(regex)) {
                openSocial(setting.getFacebook());
            } else {
                Toast.makeText(this, R.string.inv_url, Toast.LENGTH_SHORT).show();
            }
        });

        binding.imageInstagram.setOnClickListener(view -> {
            if (setting.getInstagram().matches(regex)) {
                openSocial(setting.getInstagram());
            } else {
                Toast.makeText(this, R.string.inv_url, Toast.LENGTH_SHORT).show();
            }
        });

        binding.imageTwitter.setOnClickListener(view -> {
            if (setting.getTwitter().matches(regex)) {
                openSocial(setting.getTwitter());
            } else {
                Toast.makeText(this, R.string.inv_url, Toast.LENGTH_SHORT).show();
            }
        });
        binding.tvChat.setOnClickListener(view -> {
            if (userModel==null){
                Toast.makeText(this, getString(R.string.please_sign_in_or_sign_up), Toast.LENGTH_SHORT).show();
            }else {
                createAdmin();

            }
        });
        getSetting();
    }

    private void createAdmin() {
        try {
            ProgressDialog dialog = Common.createProgressDialog(this, getString(R.string.wait));
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
            Api.getService(Tags.base_url)
                    .createAdminRoom("Bearer " + userModel.getData().getToken())
                    .enqueue(new Callback<AdminRoomDataModel>() {
                        @Override
                        public void onResponse(Call<AdminRoomDataModel> call, Response<AdminRoomDataModel> response) {
                            dialog.dismiss();
                            if (response.isSuccessful()) {

                                if (response.body() != null && response.body().getStatus() == 200) {
                                    ChatUserModel chatUserModel = new ChatUserModel();
                                    chatUserModel.setRoom_id(response.body().getData().getId());
                                    Intent intent = new Intent(ChatUsActivity.this, ChatAdminActivity.class);
                                    intent.putExtra("data", chatUserModel);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(ChatUsActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

                                }

                            } else {
                                dialog.dismiss();
                                if (response.code() == 500) {
                                    Toast.makeText(ChatUsActivity.this, "Server Error", Toast.LENGTH_SHORT).show();


                                } else {
                                    Toast.makeText(ChatUsActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

                                    try {

                                        Log.e("error", response.code() + "_" + response.errorBody().string());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<AdminRoomDataModel> call, Throwable t) {
                            try {
                                dialog.dismiss();
                                if (t.getMessage() != null) {
                                    Log.e("error", t.getMessage());
                                    if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                        Toast.makeText(ChatUsActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(ChatUsActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                            } catch (Exception e) {
                            }
                        }
                    });
        } catch (Exception e) {

        }
    }

    private void openSocial(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);

    }

    private void getSetting() {
        try {
            Api.getService(Tags.base_url)
                    .getSettings()
                    .enqueue(new Callback<SettingDataModel>() {
                        @Override
                        public void onResponse(Call<SettingDataModel> call, Response<SettingDataModel> response) {
                            binding.progBar.setVisibility(View.GONE);
                            if (response.isSuccessful()) {

                                if (response.body() != null && response.body().getStatus() == 200) {
                                    setting = response.body().getData();
                                    binding.setModel(setting);
                                    binding.scrollView.setVisibility(View.VISIBLE);

                                } else {
                                    Toast.makeText(ChatUsActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

                                }

                            } else {
                                if (response.code() == 500) {
                                    Toast.makeText(ChatUsActivity.this, "Server Error", Toast.LENGTH_SHORT).show();


                                } else {
                                    Toast.makeText(ChatUsActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

                                    try {

                                        Log.e("error", response.code() + "_" + response.errorBody().string());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<SettingDataModel> call, Throwable t) {
                            try {

                                if (t.getMessage() != null) {
                                    Log.e("error", t.getMessage());
                                    if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                        Toast.makeText(ChatUsActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(ChatUsActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
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