package com.anaqaphone.activities_fragments.activity_search;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.anaqaphone.R;
import com.anaqaphone.activities_fragments.activity_product_details.ProductDetailsActivity;
import com.anaqaphone.adapters.OffersAdapter;
import com.anaqaphone.databinding.ActivitySearchBinding;
import com.anaqaphone.interfaces.Listeners;
import com.anaqaphone.language.Language;
import com.anaqaphone.models.ProductDataModel;
import com.anaqaphone.models.SingleProductDataModel;
import com.anaqaphone.models.UserModel;
import com.anaqaphone.preferences.Preferences;
import com.anaqaphone.remote.Api;
import com.anaqaphone.share.Common;
import com.anaqaphone.tags.Tags;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity implements Listeners.BackListener{
    private ActivitySearchBinding binding;
    private String lang;
    private List<SingleProductDataModel> offersDataList;
    private OffersAdapter offersAdapter;
    private String query = "all", department_id = "all";
    private UserModel userModel;
    private Preferences preferences;
    private boolean isLoading = false;
    private int current_page = 1;
    private LinearLayoutManager manager;
    private boolean isFavoriteChange = false;
    private int square = 1,list=2;
    private int displayType=square;

    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang","ar")));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search);
        initView();
        search();

    }



    private void initView()
    {
        offersDataList = new ArrayList<>();
        Paper.init(this);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        binding.setBackListener(this);
        binding.setLang(lang);
        binding.progBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary), PorterDuff.Mode.SRC_IN);

        //productModelList = new ArrayList<>();
        manager = new GridLayoutManager(this,2);
        binding.recView.setLayoutManager(manager);
        offersAdapter = new OffersAdapter(offersDataList, this, null, displayType);
        binding.recView.setAdapter(offersAdapter);
       /* searchAdapter = new SearchAdapter(this,productModelList,this);
        binding.recView.setAdapter(searchAdapter);
        binding.recView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    int total = binding.recView.getAdapter().getItemCount();

                    int lastVisibleItem = manager.findLastCompletelyVisibleItemPosition();


                    if (total > 6 && (total - lastVisibleItem) == 2 && !isLoading) {
                        isLoading = true;
                        int page = current_page + 1;
                        productModelList.add(null);
                        searchAdapter.notifyDataSetChanged();
                        loadMore(page);
                    }
                }
            }
        });*/


        //productModelList = new ArrayList<>();

        preferences = Preferences.getInstance();
        userModel = preferences.getUserData(this);


        binding.edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().length()>0)
                {
                    query = editable.toString();

                    search();
                    binding.progBar.setVisibility(View.GONE);
                    binding.recView.setVisibility(View.VISIBLE);
                }else
                {
                    query ="";
                    //productModelList.clear();
                    //searchAdapter.notifyDataSetChanged();


                }
            }
        });

//        binding.llType.setOnClickListener(v -> {
//
//            if (displayType==square)
//            {
//                displayType = list;
//                binding.imageType.setImageResource(R.drawable.ic_list2);
//                binding.tvType.setText(getString(R.string.list));
//            }else {
//                displayType = square;
//                binding.imageType.setImageResource(R.drawable.ic_squares);
//                binding.tvType.setText(getString(R.string.normal));
//            }
//        });

    }



    private void loadMore(int page) {
       /* try {
            String token;
            if (userModel==null)
            {
                token = "";
            }else
            {
                token = userModel.getUser().getToken();
            }

            Api.getService(Tags.base_url)
                    .getProductsByName(lang,"on",page,query)
                    .enqueue(new Callback<ProductDataModel>() {
                        @Override
                        public void onResponse(Call<ProductDataModel> call, Response<ProductDataModel> response) {
                            isLoading = false;
                            productModelList.remove(productModelList.size() - 1);
                            searchAdapter.notifyItemRemoved(productModelList.size() - 1);

                            if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                                productModelList.addAll(response.body().getData());
                                searchAdapter.notifyDataSetChanged();
                                if (response.body().getData().size() > 0) {
                                    current_page = response.body().getMeta().getCurrent_page();

                                }
                            } else {
                                if (response.code() == 500) {
                                    Toast.makeText(SearchActivity.this, "Server Error", Toast.LENGTH_SHORT).show();


                                } else {
                                    Toast.makeText(SearchActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

                                    try {

                                        Log.e("error", response.code() + "_" + response.errorBody().string());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ProductDataModel> call, Throwable t) {
                            try {
                                if (productModelList.get(productModelList.size() - 1) == null) {
                                    isLoading = false;
                                    productModelList.remove(productModelList.size() - 1);
                                    searchAdapter.notifyItemRemoved(productModelList.size() - 1);

                                }
                                binding.progBar.setVisibility(View.GONE);

                                if (t.getMessage() != null) {
                                    Log.e("error", t.getMessage());
                                    if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                        Toast.makeText(SearchActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(SearchActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                            } catch (Exception e) {
                            }
                        }
                    });
        } catch (Exception e) {

        }*/
    }
    public void search() {
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
                    Search("off", uid, query, department_id).
                    enqueue(new Callback<ProductDataModel>() {
                        @Override
                        public void onResponse(Call<ProductDataModel> call, Response<ProductDataModel> response) {
                            binding.progBar.setVisibility(View.GONE);

                            if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {

                                offersDataList.clear();
                                offersDataList.addAll(response.body().getData());
                                if (offersDataList.size() > 0) {
                                    offersAdapter.notifyDataSetChanged();
                                }
                                else {
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
                                    Toast.makeText(SearchActivity.this, "Server Error", Toast.LENGTH_SHORT).show();


                                } else {
                                    Toast.makeText(SearchActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();


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
                                        Toast.makeText(SearchActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(SearchActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
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
    public void back() {
        if (isFavoriteChange)
        {
            setResult(RESULT_OK);
        }
        finish();
    }

   /* public void setItemData(ProductDataModel.Data model) {
        Intent intent = new Intent(this, ProductDetailsActivity.class);
        intent.putExtra("data",model);
        startActivityForResult(intent,100);
    }*/


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==100&&resultCode==RESULT_OK)
        {
            isFavoriteChange = true;
        }
    }

    public void setItemDataOffers(SingleProductDataModel model) {

        Intent intent = new Intent(this, ProductDetailsActivity.class);
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

                                    search();
                                } else {


                                    if (response.code() == 500) {
                                        Toast.makeText(SearchActivity.this, "Server Error", Toast.LENGTH_SHORT).show();


                                    } else {
                                        Toast.makeText(SearchActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

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
                                            Toast.makeText(SearchActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(SearchActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                } catch (Exception e) {
                                }
                            }
                        });
            } catch (Exception e) {

            }
            return 1;

        }
        else {

            Common.CreateDialogAlert(SearchActivity.this, getString(R.string.please_sign_in_or_sign_up));
            return 0;

        }
    }


    @Override
    public void onBackPressed() {
        back();
    }
}
