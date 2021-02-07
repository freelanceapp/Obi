package com.obiapp.activities_fragments.activity_login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;

import com.obiapp.R;
import com.obiapp.activities_fragments.activity_home.HomeActivity;
import com.obiapp.activities_fragments.activity_verification_code.VerificationCodeActivity;
import com.obiapp.databinding.ActivityLoginBinding;
import com.obiapp.language.Language;
import com.obiapp.models.LoginModel;
import com.obiapp.share.Common;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private String lang;
    private LoginModel loginModel;


    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang", "ar")));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_login);
        initView();
    }

    private void initView() {
        Paper.init(this);
        lang = Paper.book().read("lang","ar");
        binding.setLang(lang);
        loginModel = new LoginModel();
        binding.setLoginModel(loginModel);
        binding.edtPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().startsWith("0")) {
                    binding.edtPhone.setText("");
                }
            }
        });

        binding.btnLogin.setOnClickListener(view -> {
            if (loginModel.isDataValid(this)) {
                Common.CloseKeyBoard(this, binding.edtPhone);
                navigateToVerificationCodeActivity();
            }
        });

        binding.btnSkip.setOnClickListener(view -> {
            navigateToHomeActivity();
        });

    }

    private void navigateToHomeActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void navigateToVerificationCodeActivity() {

        Intent intent = new Intent(this, VerificationCodeActivity.class);
        intent.putExtra("phone_code", loginModel.getPhone_code());
        intent.putExtra("phone", loginModel.getPhone());
        startActivity(intent);
        finish();

    }




}