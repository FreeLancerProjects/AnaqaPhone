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
import com.anaqaphone.activities_fragments.activity_product_details.ProductDetailsActivity;
import com.anaqaphone.adapters.OffersAdapter;
import com.anaqaphone.adapters.SlidingImage_Adapter;
import com.anaqaphone.databinding.FragmentMainBinding;
import com.anaqaphone.models.ProductDataModel;
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
    private int current_page = 0, NUM_PAGES;
    private LinearLayoutManager manager;
    private boolean isLoading = false;
    private UserModel userModel;
    private SlidingImage_Adapter slidingImage__adapter;
    private List<ProductDataModel.Data> offersDataList;
    private OffersAdapter offersAdapter;

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
        getOffersProducts();
    }

    private void initView() {
        offersDataList = new ArrayList<>();
        activity = (HomeActivity) getActivity();
        preferences = Preferences.getInstance();
        userModel = preferences.getUserData(activity);
        offersDataList=new ArrayList<>();
        Paper.init(activity);
        lang = Paper.book().read("lang", "ar");

        binding.tab.setupWithViewPager(binding.pager);
        binding.progBarSlider.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(activity, R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        binding.progBarOffer.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(activity, R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        binding.progBarAccessories.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(activity, R.color.colorPrimary), PorterDuff.Mode.SRC_IN);


        binding.recViewFavoriteOffers.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
        offersAdapter = new OffersAdapter(offersDataList, activity, this);
        binding.recViewFavoriteOffers.setAdapter(offersAdapter);

    }

    private void get_slider() {

        Api.getService(Tags.base_url).get_slider().enqueue(new Callback<Slider_Model>() {
            @Override
            public void onResponse(Call<Slider_Model> call, Response<Slider_Model> response) {
                binding.progBarSlider.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
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

    public void setItemDataOffers(ProductDataModel.Data model) {

        Intent intent = new Intent(activity, ProductDetailsActivity.class);
        intent.putExtra("id", model.getId());
        startActivityForResult(intent, 100);
    }

    public void like_dislike(int type, ProductDataModel.Data productModel, String action, int pos) {

       /* try {
            Api.getService(Tags.base_url)
                    .addFavoriteProduct(lang,userModel.getUser().getToken(),action,productModel.getId())
                    .enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if (response.isSuccessful()) {
                                if (action.equals("favourite"))
                                {
                                    productModel.setIs_favourite(true);

                                }else {
                                    productModel.setIs_favourite(false);

                                }
                                if (type==1)
                                {
                                    mostSellerDataList.set(pos,productModel);
                                    mostSellerAdapter.notifyItemChanged(pos);
                                }else {
                                    mostRateDataList.set(pos,productModel);
                                    mostRateAdapter.notifyItemChanged(pos);
                                }
                            } else {

                                if (action.equals("favourite"))
                                {
                                    productModel.setIs_favourite(false);

                                }else {
                                    productModel.setIs_favourite(true);

                                }
                                if (type==1)
                                {
                                    mostSellerDataList.set(pos,productModel);
                                    mostSellerAdapter.notifyItemChanged(pos);
                                }else {
                                    mostRateDataList.set(pos,productModel);
                                    mostRateAdapter.notifyItemChanged(pos);
                                }

                                if (response.code() == 500) {
                                    Toast.makeText(activity, "Server Error", Toast.LENGTH_SHORT).show();


                                } else {
                                    Toast.makeText(activity, getString(R.string.failed), Toast.LENGTH_SHORT).show();

                                    try {

                                        Log.e("error", response.code() + "_" + response.errorBody().string());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            try {
                                if (action.equals("favourite"))
                                {
                                    productModel.setIs_favourite(false);

                                }else {
                                    productModel.setIs_favourite(true);

                                }
                                if (type==1)
                                {
                                    mostSellerDataList.set(pos,productModel);
                                    mostSellerAdapter.notifyItemChanged(pos);
                                }else {
                                    mostRateDataList.set(pos,productModel);
                                    mostRateAdapter.notifyItemChanged(pos);
                                }
                                if (t.getMessage() != null) {
                                    Log.e("error", t.getMessage());
                                    if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                        Toast.makeText(activity, R.string.something, Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(activity, t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                            } catch (Exception e) {
                            }
                        }
                    });
        } catch (Exception e) {

        }*/
    }

    public void getOffersProducts() {

        try {
            int uid;

            if (userModel != null) {
                uid = userModel.getUser().getId();
            } else {
                uid = 0;
            }
            Api.getService(Tags.base_url).
                    getOffersProducts("off", uid).
                    enqueue(new Callback<ProductDataModel>() {
                        @Override
                        public void onResponse(Call<ProductDataModel> call, Response<ProductDataModel> response) {
                            binding.progBarOffer.setVisibility(View.GONE);

                            if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {

                                offersDataList.clear();
                                offersDataList.addAll(response.body().getData());
                                if (offersDataList.size() > 0) {
                                    offersAdapter.notifyDataSetChanged();
                                } else {

                                }

                            } else {
                                try {

                                    Log.e("error", response.code() + "_" + response.errorBody().string());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                if (response.code() == 500) {
                                    Toast.makeText(activity, "Server Error", Toast.LENGTH_SHORT).show();


                                } else {
                                    Toast.makeText(activity, getString(R.string.failed), Toast.LENGTH_SHORT).show();


                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ProductDataModel> call, Throwable t) {
                            binding.progBarOffer.setVisibility(View.GONE);
                            try {
                                if (t.getMessage() != null) {
                                    Log.e("error", t.getMessage());
                                    if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                        Toast.makeText(activity, R.string.something, Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(activity, t.getMessage(), Toast.LENGTH_SHORT).show();
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
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            getOffersProducts();
        }
    }

    public void updateCartCount(int itemCount) {
        activity.updateCartCount(itemCount);
    }
}
