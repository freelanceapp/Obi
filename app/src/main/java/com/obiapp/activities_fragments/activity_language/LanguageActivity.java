package com.obiapp.activities_fragments.activity_language;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.obiapp.R;
import com.obiapp.databinding.ActivityLanguageBinding;
import com.obiapp.language.Language;

import io.paperdb.Paper;

public class LanguageActivity extends AppCompatActivity {
    private ActivityLanguageBinding binding;
    private String lang = "";
    private String selectedLang;


    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang", "ar")));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_language);
        initView();
    }

    private void initView() {
        Paper.init(this);
        lang = Paper.book().read("lang", "ar");
        selectedLang = lang;

        if (lang.equals("ar")) {
            binding.flAr.setBackgroundResource(R.drawable.small_stroke_primary);
            binding.flEn.setBackgroundResource(0);

        } else {
            binding.flAr.setBackgroundResource(0);
            binding.flEn.setBackgroundResource(R.drawable.small_stroke_primary);

        }

        binding.cardAr.setOnClickListener(view -> {
            selectedLang = "ar";

            if (!selectedLang.equals(lang)) {
                binding.btnNext.setVisibility(View.VISIBLE);

            } else {
                binding.btnNext.setVisibility(View.INVISIBLE);

            }
            binding.flAr.setBackgroundResource(R.drawable.small_stroke_primary);
            binding.flEn.setBackgroundResource(0);

        });

        binding.cardEn.setOnClickListener(view -> {
            selectedLang = "en";

            Log.e("lang",selectedLang+"__"+lang);
            if (!selectedLang.equals(lang)) {
                binding.btnNext.setVisibility(View.VISIBLE);

            } else {
                binding.btnNext.setVisibility(View.INVISIBLE);

            }
            binding.flAr.setBackgroundResource(0);
            binding.flEn.setBackgroundResource(R.drawable.small_stroke_primary);
        });


        binding.btnNext.setOnClickListener(view -> {

            Intent intent = getIntent();
            intent.putExtra("lang", selectedLang);
            setResult(RESULT_OK, intent);
            finish();
        });
    }
}