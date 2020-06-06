package com.anaqaphone.activities_fragments.activity_home.fragments;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.anaqaphone.R;
import com.anaqaphone.activities_fragments.activity_home.HomeActivity;
import com.anaqaphone.adapters.SlidingImage_Adapter;
import com.anaqaphone.databinding.FragmentMainBinding;
import com.anaqaphone.models.Slider_Model;
import com.anaqaphone.models.UserModel;
import com.anaqaphone.preferences.Preferences;
import com.anaqaphone.remote.Api;
import com.anaqaphone.tags.Tags;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import io.paperdb.Paper;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

public class Fragment_Main extends Fragment {
    private HomeActivity activity;
    private FragmentMainBinding binding;
    private Preferences preferences;
    private String lang;
    private TimerTask timerTask;
    private Timer timer;
    private int current_page = 0,NUM_PAGES;
    private LinearLayoutManager manager;
    private boolean isLoading = false;
    private UserModel userModel;
    private SlidingImage_Adapter slidingImage__adapter;
    public static Fragment_Main newInstance() {
        return new Fragment_Main();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
get_slider();
change_slide_image();
    }

    private void initView() {

        activity = (HomeActivity) getActivity();
        preferences = Preferences.getInstance();
        userModel = preferences.getUserData(activity);

        Paper.init(activity);
        lang = Paper.book().read("lang", "ar");

        binding.tab.setupWithViewPager(binding.pager);
        binding.progBarSlider.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(activity, R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        binding.progBarOffer.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(activity, R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        binding.progBarAccessories.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(activity, R.color.colorPrimary), PorterDuff.Mode.SRC_IN);





    }
    private void get_slider() {

        Api.getService(Tags.base_url).get_slider().enqueue(new Callback<Slider_Model>() {
            @Override
            public void onResponse(Call<Slider_Model> call, Response<Slider_Model> response) {
                binding.progBarSlider.setVisibility(View.GONE);

                if (response.isSuccessful()&&response.body()!=null&&response.body().getData()!=null) {
                    if (response.body().getData().size() > 0) {
                        NUM_PAGES = response.body().getData().size();
                        slidingImage__adapter = new SlidingImage_Adapter(activity, response.body().getData());
                        binding.pager.setAdapter(slidingImage__adapter);

                    } else {

                        binding.pager.setVisibility(View.GONE);
                    }
                } else if (response.code() == 404) {
                    binding.pager.setVisibility(View.GONE);
                } else {
                    binding.pager.setVisibility(View.GONE);
                    try {
                        Log.e("Error_code", response.code() + "_" + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onFailure(Call<Slider_Model> call, Throwable t) {
                try {
                    binding.progBarSlider.setVisibility(View.GONE);
                    binding.pager.setVisibility(View.GONE);

                    Log.e("Error", t.getMessage());

                } catch (Exception e) {

                }

            }
        });

    }
    private void change_slide_image() {
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (current_page == NUM_PAGES) {
                    current_page = 0;
                }
                binding.pager.setCurrentItem(current_page++, true);
            }
        };
        Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, 3000, 3000);
    }
}
