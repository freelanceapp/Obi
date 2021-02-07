package com.obiapp.activities_fragments.activity_commission;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;

import com.obiapp.R;
import com.obiapp.databinding.ActivityCommissionBinding;
import com.obiapp.databinding.ActivitySplashBinding;
import com.obiapp.language.Language;
import com.obiapp.models.UserModel;
import com.obiapp.preferences.Preferences;

import io.paperdb.Paper;

public class CommissionActivity extends AppCompatActivity {
    private ActivityCommissionBinding binding;
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
        binding = DataBindingUtil.setContentView(this, R.layout.activity_commission);
        initView();
    }

    private void initView() {
        preference = Preferences.getInstance();
        userModel = preference.getUserData(this);
        Paper.init(this);
        lang = Paper.book().read("lang","ar");
        binding.setLang(lang);
        binding.llBack.setOnClickListener(view -> finish());
        binding.edtPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!editable.toString().isEmpty()){
                    double commission = Double.parseDouble(editable.toString().trim());
                    double result = commission/100;
                    String resultData = getString(R.string.if_it_is_sold_at_a_price_of)+" "+commission+" "+getString(R.string.sar)+" "+getString(R.string.the_amount_owed_to_payment_is)+" "+result+" "+getString(R.string.sar);
                    binding.tvResult.setText(resultData);
                }else {
                    binding.tvResult.setText(null);
                }
            }
        });
    }
}