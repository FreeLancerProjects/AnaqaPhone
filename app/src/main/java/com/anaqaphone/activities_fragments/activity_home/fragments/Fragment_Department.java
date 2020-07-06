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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.anaqaphone.R;
import com.anaqaphone.activities_fragments.activity_home.HomeActivity;
import com.anaqaphone.activities_fragments.activity_product_details.ProductDetailsActivity;
import com.anaqaphone.adapters.MainCategoryAdapter;
import com.anaqaphone.adapters.OffersAdapter;
import com.anaqaphone.databinding.FragmentDepartmentBinding;
import com.anaqaphone.models.MainCategoryDataModel;
import com.anaqaphone.models.ProductDataModel;
import com.anaqaphone.models.SingleProductDataModel;
import com.anaqaphone.models.UserModel;
import com.anaqaphone.preferences.Preferences;
import com.anaqaphone.remote.Api;
import com.anaqaphone.share.Common;
import com.anaqaphone.tags.Tags;
import com.google.android.material.tabs.TabLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fragment_Department extends Fragment {

    private HomeActivity activity;
    private FragmentDepartmentBinding binding;
    private Preferences preferences;
    private UserModel userModel;
    private String lang;
    private LinearLayoutManager manager;
    private GridLayoutManager manager2;
    private MainCategoryAdapter adapter;
    private List<MainCategoryDataModel.Data> mainDepartmentsList, getMainDepartmentsList;
    private int square = 1, list = 2;
    private int displayType = square;
    private List<SingleProductDataModel> offersDataList;
    private OffersAdapter offersAdapter;
    private String brand_id = "all", department_id = "all";

    public static Fragment_Department newInstance() {
        return new Fragment_Department();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_department, container, false);
        initView();
        getCategory();
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    private void initView() {
        mainDepartmentsList = new ArrayList<>();
        getMainDepartmentsList = new ArrayList<>();
        offersDataList = new ArrayList<>();
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
        offersAdapter = new OffersAdapter(offersDataList, activity, this, displayType);
        binding.recViewOffer.setAdapter(offersAdapter);
        binding.tab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int pos = tab.getPosition();
//                if (pos==0)
//                {
//                    getAds("all");
//
//                }else
//                {
                MainCategoryDataModel.Data categoryModel = getMainDepartmentsList.get(pos);
                department_id = categoryModel.getId() + "";
                getOffersProducts();
//                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    private void getBrands() {

        Api.getService(Tags.base_url)
                .getBrands("off")
                .enqueue(new Callback<MainCategoryDataModel>() {
                    @Override
                    public void onResponse(Call<MainCategoryDataModel> call, Response<MainCategoryDataModel> response) {
                        binding.progBar.setVisibility(View.GONE);
                        if (response.isSuccessful()) {
                            mainDepartmentsList.clear();
                            mainDepartmentsList.addAll(response.body().getData());

                            if (mainDepartmentsList.size() > 0) {
                                adapter.notifyDataSetChanged();
                               // binding.tvNoData.setVisibility(View.GONE);
                            } else {
                               // binding.tvNoData.setVisibility(View.VISIBLE);

                            }
                            getOffersProducts();


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

    private void getCategory() {

        Api.getService(Tags.base_url)
                .getCategory("off")
                .enqueue(new Callback<MainCategoryDataModel>() {
                    @Override
                    public void onResponse(Call<MainCategoryDataModel> call, Response<MainCategoryDataModel> response) {
                        binding.progBar.setVisibility(View.GONE);
                        if (response.isSuccessful()) {
                            updateTabUI(response.body());

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
        offersDataList.clear();
        offersAdapter.notifyDataSetChanged();
        binding.progBar.setVisibility(View.VISIBLE);
        binding.tvNoData.setVisibility(View.GONE);


        try {
            int uid;

            if (userModel != null) {
                uid = userModel.getUser().getId();
            } else {
                uid = 0;
            }
            Api.getService(Tags.base_url).
                    getOffersProducts("off", uid, department_id, brand_id, "all").
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
                                    binding.tvNoData.setVisibility(View.VISIBLE);

                                }

                            } else {
                                binding.tvNoData.setVisibility(View.VISIBLE);

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
                            binding.tvNoData.setVisibility(View.VISIBLE);
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

    public void setDepartment(String s) {
        brand_id = s;
        getOffersProducts();
    }

    public void updateCartCount(int itemCount) {
        activity.updateCartCount(itemCount);
    }

    public void setItemDataOffers(SingleProductDataModel model) {

        Intent intent = new Intent(activity, ProductDetailsActivity.class);
        intent.putExtra("product_id", model.getId());
        startActivityForResult(intent, 100);
    }

    public int like_dislike(SingleProductDataModel productModel, int pos) {
        if (userModel != null) {
            try {
                Api.getService(Tags.base_url)
                        .addFavoriteProduct(userModel.getUser().getToken(), productModel.getId() + "")
                        .enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                if (response.isSuccessful()) {

                                    getOffersProducts();
                                } else {


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
            return 1;

        } else {

            Common.CreateDialogAlert(activity, getString(R.string.please_sign_in_or_sign_up));
            return 0;

        }
    }

    private void updateTabUI(MainCategoryDataModel data) {
//        CategoryDataModel.CategoryModel categoryModel = new CategoryDataModel.CategoryModel(0,getString(R.string.show_all));
//       data.add(0,categoryModel);
        getMainDepartmentsList.clear();
        getMainDepartmentsList.addAll(data.getData());

        for (MainCategoryDataModel.Data categoryModel1 : data.getData()) {

            binding.tab.addTab(binding.tab.newTab().setText(categoryModel1.getTitle()));


        }

        new Handler().postDelayed(
                () -> binding.tab.getTabAt(0).select(), 100);
        getBrands();


    }
}
