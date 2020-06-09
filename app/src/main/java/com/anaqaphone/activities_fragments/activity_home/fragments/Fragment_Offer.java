package com.anaqaphone.activities_fragments.activity_home.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.anaqaphone.R;
import com.anaqaphone.activities_fragments.activity_home.HomeActivity;
import com.anaqaphone.adapters.MainCategoryAdapter;
import com.anaqaphone.adapters.OffersAdapter;
import com.anaqaphone.databinding.FragmentCartBinding;
import com.anaqaphone.databinding.FragmentOfferBinding;
import com.anaqaphone.models.MainCategoryDataModel;
import com.anaqaphone.models.ProductDataModel;
import com.anaqaphone.models.UserModel;
import com.anaqaphone.preferences.Preferences;
import com.anaqaphone.remote.Api;
import com.anaqaphone.tags.Tags;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fragment_Offer extends Fragment {

    private HomeActivity activity;
    private FragmentOfferBinding binding;
    private Preferences preferences;
    private UserModel userModel;
    private String lang;
    private LinearLayoutManager manager;
    private GridLayoutManager manager2;
    private MainCategoryAdapter adapter;
    private List<MainCategoryDataModel.Data> mainDepartmentsList;
    private int square = 1, list = 2;
    private int displayType = square;
    private List<ProductDataModel.Data> offersDataList;
    private OffersAdapter offersAdapter;

    public static Fragment_Offer newInstance() {
        return new Fragment_Offer();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_offer, container, false);
        initView();
        getMainCategory();
        getOffersProducts();
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    private void initView() {
        mainDepartmentsList = new ArrayList<>();
        offersDataList=new ArrayList<>();
        activity = (HomeActivity) getActivity();
        Paper.init(activity);
        lang = Paper.book().read("lang", "ar");
        binding.setLang(lang);
        preferences = Preferences.getInstance();
        userModel = preferences.getUserData(activity);

        manager = new LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false);
        manager2 = new GridLayoutManager(activity, 1);

        binding.recViewCaregory.setLayoutManager(manager);

        adapter = new MainCategoryAdapter(mainDepartmentsList, activity, this);
        binding.progBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(activity, R.color.colorPrimary), PorterDuff.Mode.SRC_IN);

        binding.recViewCaregory.setAdapter(adapter);
        binding.recViewOffer.setLayoutManager(new GridLayoutManager(activity, 2));
        offersAdapter = new OffersAdapter(offersDataList, activity, this,displayType);
        binding.recViewOffer.setAdapter(offersAdapter);
        binding.llType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (displayType == square) {
                    displayType = list;
                    offersAdapter.setType(displayType);

                    binding.imageType.setImageResource(R.drawable.ic_list2);
                    binding.tvType.setText(getString(R.string.list));
                    binding.recViewOffer.setLayoutManager(manager2);

                    offersAdapter.notifyDataSetChanged();


                } else {
                    displayType = square;
                    binding.imageType.setImageResource(R.drawable.ic_squares);
                    binding.tvType.setText(getString(R.string.normal));

                    offersAdapter.setType(displayType);
                    binding.recViewOffer.setLayoutManager(new GridLayoutManager(activity, 2));

                    offersAdapter.notifyDataSetChanged();


                }
            }
        });

    }

    private void getMainCategory() {

        Api.getService(Tags.base_url)
                .getMainCategory("off")
                .enqueue(new Callback<MainCategoryDataModel>() {
                    @Override
                    public void onResponse(Call<MainCategoryDataModel> call, Response<MainCategoryDataModel> response) {
                        binding.progBar.setVisibility(View.GONE);
                        if (response.isSuccessful()) {
                            mainDepartmentsList.clear();
                            mainDepartmentsList.addAll(response.body().getData());

                            if (mainDepartmentsList.size() > 0) {
                                adapter.notifyDataSetChanged();
                                binding.tvNoData.setVisibility(View.GONE);
                            } else {
                                binding.tvNoData.setVisibility(View.VISIBLE);

                            }


                        } else {
                            binding.progBar.setVisibility(View.GONE);

                            try {
                                Log.e("errorNotCode", response.code() + "__" + response.errorBody().string());
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
                    public void onFailure(Call<MainCategoryDataModel> call, Throwable t) {
                        try {
                            binding.progBar.setVisibility(View.GONE);

                            if (t.getMessage() != null) {
                                Log.e("error_not_code", t.getMessage() + "__");

                                if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                    Toast.makeText(activity, getString(R.string.something), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(activity, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception e) {
                            Log.e("Error", e.getMessage() + "__");
                        }
                    }
                });
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
                            binding.progBar.setVisibility(View.GONE);

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
                            binding.progBar.setVisibility(View.GONE);
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

}
