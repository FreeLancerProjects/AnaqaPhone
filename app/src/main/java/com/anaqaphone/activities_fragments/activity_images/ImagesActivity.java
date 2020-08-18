package com.anaqaphone.activities_fragments.activity_images;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.anaqaphone.R;
import com.anaqaphone.adapters.ProductDetialsSlidingImage_Adapter;
import com.anaqaphone.adapters.ProductImageAdapter;
import com.anaqaphone.databinding.ActivityImageBinding;
import com.anaqaphone.interfaces.Listeners;
import com.anaqaphone.language.Language;
import com.anaqaphone.models.SingleProductDataModel;
import com.anaqaphone.models.UserModel;
import com.anaqaphone.preferences.Preferences;
import com.anaqaphone.singleton.CartSingleton;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;


public class ImagesActivity extends AppCompatActivity implements Listeners.BackListener {
    private ActivityImageBinding binding;
    private String lang;
    private SingleProductDataModel productDataModel;
    private Preferences preferences;

    private UserModel userModel;
    private ProductDetialsSlidingImage_Adapter slidingImage__adapter;
    private ProductImageAdapter productImageAdapter;
    private List<SingleProductDataModel.ProductsImages> productsImagesList;
    private CartSingleton cartSingleton;
    private SingleProductDataModel singleProductDataModel;

    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang", "ar")));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_image);
        getDataFromIntent();
        initView();

    }


    private void getDataFromIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            singleProductDataModel = (SingleProductDataModel) intent.getSerializableExtra("data");

        }

        //    product_id=1;
    }


    private void initView() {
        productsImagesList = new ArrayList<>();
        Paper.init(this);
        cartSingleton = CartSingleton.newInstance();
        preferences = Preferences.getInstance();
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        binding.setBackListener(this);
        binding.setLang(lang);
        productImageAdapter = new ProductImageAdapter(productsImagesList, this);
        binding.recimage.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        binding.recimage.setAdapter(productImageAdapter);
        binding.setModel(productDataModel);
        binding.progBarSlider.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        UPDATEUI(singleProductDataModel);


    }


    private void UPDATEUI(SingleProductDataModel body) {

        binding.setModel(body);
        this.singleProductDataModel = body;
        binding.progBarSlider.setVisibility(View.GONE);
//        if (body.getColor() != null) {
//            binding.frame.setBackgroundColor(Color.parseColor(body.getColor()));
//        }

        slidingImage__adapter = new ProductDetialsSlidingImage_Adapter(this, body.getProducts_images());
        binding.pager.setAdapter(slidingImage__adapter);
        productsImagesList.addAll(body.getProducts_images());
        productImageAdapter.notifyDataSetChanged();
    }


    @Override
    public void back() {
//        if (isDataAdded) {
//            setResult(RESULT_OK);
//        }
        finish();
    }


    @Override
    public void onBackPressed() {
        back();
    }


    public void showimage(int layoutPosition) {
        binding.pager.setCurrentItem(layoutPosition);
    }
}
