package com.obiapp.activities_fragments.activity_setting;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.obiapp.BuildConfig;
import com.obiapp.R;
import com.obiapp.activities_fragments.activity_language.LanguageActivity;
import com.obiapp.activities_fragments.activity_web_view.WebViewActivity;
import com.obiapp.databinding.ActivitySettingBinding;
import com.obiapp.databinding.ActivitySplashBinding;
import com.obiapp.language.Language;
import com.obiapp.models.UserModel;
import com.obiapp.preferences.Preferences;
import com.obiapp.tags.Tags;

import io.paperdb.Paper;

public class SettingActivity extends AppCompatActivity {
    private ActivitySettingBinding binding;
    private Preferences preference;
    private UserModel userModel;
    private String lang;

    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang", "ar")));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_setting);
        initView();
    }

    private void initView() {
        preference = Preferences.getInstance();
        userModel = preference.getUserData(this);
        Paper.init(this);
        lang = Paper.book().read("lang","ar");
        binding.setLang(lang);
        binding.llBack.setOnClickListener(view -> finish());
        String version = "Version: "+ BuildConfig.VERSION_NAME;
        binding.tvVersion.setText(version);

        binding.llLanguage.setOnClickListener(view -> {
            Intent intent = new Intent(this, LanguageActivity.class);
            startActivityForResult(intent,100);
        });

        binding.llTerms.setOnClickListener(view -> {
            String url = Tags.base_url+"app-setting#2";
            navigateToWebViewActivity(url);
        });

        binding.llAboutApp.setOnClickListener(view -> {
            String url = Tags.base_url+"app-setting#1";
            navigateToWebViewActivity(url);
        });






    }

    private void navigateToWebViewActivity(String url){
        Intent intent = new Intent(this, WebViewActivity.class);
        intent.putExtra("url",url);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==100&&resultCode==RESULT_OK&&data!=null){
            String lang = data.getStringExtra("lang");
            Intent intent = getIntent();
            intent.putExtra("lang",lang);
            setResult(RESULT_OK,intent);
            finish();
        }
    }
}