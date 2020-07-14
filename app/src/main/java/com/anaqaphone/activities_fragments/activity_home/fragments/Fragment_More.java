package com.anaqaphone.activities_fragments.activity_home.fragments;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.anaqaphone.BuildConfig;
import com.anaqaphone.R;
import com.anaqaphone.activities_fragments.activity_about_app.AboutAppActivity;
import com.anaqaphone.activities_fragments.activity_client_profile.ClientProfileActivity;
import com.anaqaphone.activities_fragments.activity_home.HomeActivity;
import com.anaqaphone.activities_fragments.activity_my_favorite.MyFavoriteActivity;
import com.anaqaphone.activities_fragments.activity_order.OrderActivity;
import com.anaqaphone.activities_fragments.bank_activity.BanksActivity;
import com.anaqaphone.databinding.FragmentMoreBinding;
import com.anaqaphone.interfaces.Listeners;
import com.anaqaphone.models.SettingModel;
import com.anaqaphone.models.UserModel;
import com.anaqaphone.preferences.Preferences;
import com.anaqaphone.remote.Api;
import com.anaqaphone.share.Common;
import com.anaqaphone.tags.Tags;


import java.io.IOException;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fragment_More extends Fragment implements Listeners.SettingActions {

    private HomeActivity activity;
    private FragmentMoreBinding binding;
    private Preferences preferences;
    private String lang;
    private UserModel userModel;
    private SettingModel settingmodel;

    public static Fragment_More newInstance() {

        return new Fragment_More();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_more, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        getAppData();
    }

    private void initView() {
        activity = (HomeActivity) getActivity();
        preferences = Preferences.getInstance();
        userModel = preferences.getUserData(activity);
        Paper.init(activity);
        lang = Paper.book().read("lang", "ar");
        binding.setLang(lang);
        binding.setAction(this);
        binding.tvVersion.setText(String.format("%s%s", "Version : ", BuildConfig.VERSION_NAME));

        if (lang.equals("ar")) {
            binding.btnAr.setBackgroundResource(R.drawable.small_rounded_btn_primary);
            binding.btnEn.setBackgroundResource(R.drawable.small_rounded_btn_second);
        } else {
            binding.btnAr.setBackgroundResource(R.drawable.small_rounded_btn_second);
            binding.btnEn.setBackgroundResource(R.drawable.small_rounded_btn_primary);
        }

    }


    @Override
    public void order() {
        if (userModel != null) {
            Intent intent = new Intent(activity, OrderActivity.class);
            startActivity(intent);
        } else {
            Common.CreateDialogAlert(activity, getString(R.string.please_sign_in_or_sign_up));
        }
    }

    @Override
    public void charge() {

    }

    @Override
    public void returns() {

    }

    @Override
    public void terms() {
        Intent intent = new Intent(activity, AboutAppActivity.class);
        intent.putExtra("type", 1);
        startActivity(intent);
    }

    @Override
    public void aboutApp() {
        Intent intent = new Intent(activity, AboutAppActivity.class);
        intent.putExtra("type", 2);
        startActivity(intent);
    }


    @Override
    public void logout() {
        if (userModel != null) {
            activity.logout();
        } else {
            Common.CreateDialogAlert(activity, getString(R.string.please_sign_in_or_sign_up));
        }
    }


    @Override
    public void favorite() {
        if (userModel != null) {
            Intent intent = new Intent(activity, MyFavoriteActivity.class);
            startActivityForResult(intent, 100);
        } else {
            Common.CreateDialogAlert(activity, getString(R.string.please_sign_in_or_sign_up));
        }
    }

    @Override
    public void bankAccount() {
        Intent intent = new Intent(activity, BanksActivity.class);
        startActivity(intent);
    }

    @Override
    public void share() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=" + activity.getPackageName());
        startActivity(intent);
    }

    @Override
    public void rateApp() {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + activity.getPackageName())));
        } catch (ActivityNotFoundException e1) {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + activity.getPackageName())));
            } catch (ActivityNotFoundException e2) {
                Toast.makeText(activity, "You don't have any app that can open this link", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void arLang() {

        if (!lang.equals("ar")) {
            activity.refreshActivity("ar");
            binding.btnAr.setBackgroundResource(R.drawable.small_rounded_btn_primary);
            binding.btnEn.setBackgroundResource(R.drawable.small_rounded_btn_second);
        }

    }

    @Override
    public void enLang() {

        if (!lang.equals("en")) {
            activity.refreshActivity("en");
            binding.btnAr.setBackgroundResource(R.drawable.small_rounded_btn_second);
            binding.btnEn.setBackgroundResource(R.drawable.small_rounded_btn_primary);
        }

    }

    @Override
    public void profile() {
        if (userModel != null) {
            Intent intent = new Intent(activity, ClientProfileActivity.class);
            startActivity(intent);
        } else {
            Common.CreateDialogAlert(activity, getString(R.string.please_sign_in_or_sign_up));
        }
    }

    @Override
    public void whatsapp() {
if(settingmodel!=null&&settingmodel.getSettings().getWhatsapp()!=null){
    ViewSocial("https://api.whatsapp.com/send?phone=" +settingmodel.getSettings().getWhatsapp());
}
    }

    private void getAppData()
    {

        Api.getService(Tags.base_url)
                .getSetting(lang)
                .enqueue(new Callback<SettingModel>() {
                    @Override
                    public void onResponse(Call<SettingModel> call, Response<SettingModel> response) {
                        if (response.isSuccessful() && response.body() != null) {

                          settingmodel=response.body();

                        } else {
                            try {

                                Log.e("error", response.code() + "_" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            if (response.code() == 500) {

                            } else {


                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<SettingModel> call, Throwable t) {
                        try {

                            if (t.getMessage() != null) {
                                Log.e("error", t.getMessage());
                                if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                } else {
                                }
                            }

                        } catch (Exception e) {
                        }
                    }
                });

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }
    private void ViewSocial(String path) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(path));
        startActivity(intent);

    }
}
