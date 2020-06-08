package com.anaqaphone.activities_fragments.activity_client_profile;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.anaqaphone.R;
import com.anaqaphone.databinding.ActivityClientProfileBinding;
import com.anaqaphone.interfaces.Listeners;
import com.anaqaphone.language.Language;
import com.anaqaphone.models.UserModel;
import com.anaqaphone.preferences.Preferences;

import java.util.Locale;
import java.util.Random;

import io.paperdb.Paper;

public class ClientProfileActivity extends AppCompatActivity implements Listeners.BackListener {
    private ActivityClientProfileBinding binding;
    private String lang;
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
        binding = DataBindingUtil.setContentView(this, R.layout.activity_client_profile);
        initView();
    }


    private void initView() {
        Paper.init(this);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        binding.setLang(lang);
        preferences = Preferences.getInstance();
        userModel = preferences.getUserData(this);
        Random random = new Random();

        binding.setRandom(random);
        binding.setBackListener(this);

        binding.setModel(userModel);


    }


    @Override
    public void back() {
        finish();
    }



}
