package com.anaqaphone.activities_fragments.activity_search;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.anaqaphone.R;
import com.anaqaphone.databinding.ActivitySearchBinding;
import com.anaqaphone.interfaces.Listeners;
import com.anaqaphone.language.Language;
import com.anaqaphone.models.UserModel;
import com.anaqaphone.preferences.Preferences;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity implements Listeners.BackListener{
    private ActivitySearchBinding binding;
    private String lang;
    /*private List<ProductDataModel.Data> productModelList;
    private SearchAdapter searchAdapter;*/
    private String query="";
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
    }



    private void initView()
    {
        Paper.init(this);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        binding.setBackListener(this);
        binding.setLang(lang);
        binding.progBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary), PorterDuff.Mode.SRC_IN);

        //productModelList = new ArrayList<>();
        manager = new GridLayoutManager(this,2);
        binding.recView.setLayoutManager(manager);
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

        binding.llType.setOnClickListener(v -> {

            if (displayType==square)
            {
                displayType = list;
                binding.imageType.setImageResource(R.drawable.ic_list2);
                binding.tvType.setText(getString(R.string.list));
            }else {
                displayType = square;
                binding.imageType.setImageResource(R.drawable.ic_squares);
                binding.tvType.setText(getString(R.string.normal));
            }
        });

    }


    private void search()
    {

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

    @Override
    public void onBackPressed() {
        back();
    }
}
