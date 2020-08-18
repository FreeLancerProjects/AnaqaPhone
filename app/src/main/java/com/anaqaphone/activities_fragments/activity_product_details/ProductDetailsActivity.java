package com.anaqaphone.activities_fragments.activity_product_details;

import android.animation.Animator;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

//import com.anaqaphone.Animate.CircleAnimationUtil;
import com.anaqaphone.R;
import com.anaqaphone.activities_fragments.activity_images.ImagesActivity;
import com.anaqaphone.activities_fragments.activity_verification_code.VerificationCodeActivity;
import com.anaqaphone.adapters.ProductColorsAdapter;
import com.anaqaphone.adapters.ProductDetialsSlidingImage_Adapter;
import com.anaqaphone.adapters.ProductSizesAdapter;
import com.anaqaphone.databinding.ActivityProductDetailsBinding;
import com.anaqaphone.interfaces.Listeners;
import com.anaqaphone.language.Language;

import com.anaqaphone.models.ItemCartModel;
import com.anaqaphone.models.ProductDataModel;
import com.anaqaphone.models.SingleProductDataModel;
import com.anaqaphone.models.UserModel;
import com.anaqaphone.preferences.Preferences;
import com.anaqaphone.remote.Api;
import com.anaqaphone.share.Common;
import com.anaqaphone.singleton.CartSingleton;
import com.anaqaphone.tags.Tags;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import io.paperdb.Paper;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetailsActivity extends AppCompatActivity implements Listeners.BackListener {
    private ActivityProductDetailsBinding binding;
    private String lang;
    private SingleProductDataModel productDataModel;
    private int product_id;
    private Preferences preferences;
    private TimerTask timerTask;
    private Timer timer;
    private int current_page = 0, NUM_PAGES;
    private UserModel userModel;
    private ProductDetialsSlidingImage_Adapter slidingImage__adapter;
    private CartSingleton cartSingleton;
    private SingleProductDataModel singleProductDataModel;
    private List<SingleProductDataModel.Sizes> sizesList;
    private ProductSizesAdapter productSizesAdapter;
    private List<SingleProductDataModel.Sizes.Colors> colorsList;
    private ProductColorsAdapter productColorsAdapter;
    private double price;
    private String selected_product_id, color_id = null, size_id = null, image;

    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang", "ar")));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_product_details);
        getDataFromIntent();
        initView();
        getOrder();
        change_slide_image();

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


    private void getDataFromIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            product_id = intent.getIntExtra("product_id", 0);

        }

        //    product_id=1;
    }


    private void initView() {
        sizesList = new ArrayList<>();
        colorsList = new ArrayList<>();
        Paper.init(this);
        cartSingleton = CartSingleton.newInstance();
        preferences = Preferences.getInstance();
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        binding.setBackListener(this);
        binding.setLang(lang);

        binding.setModel(productDataModel);
        binding.tab.setupWithViewPager(binding.pager);
        binding.progBarSlider.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary), PorterDuff.Mode.SRC_IN);

        binding.tvOldprice.setPaintFlags(binding.tvOldprice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);


        binding.flAddToCart.setOnClickListener(v -> addToCart(singleProductDataModel));
        productSizesAdapter = new ProductSizesAdapter(sizesList, this);
        productColorsAdapter = new ProductColorsAdapter(colorsList, this);
        binding.recsize.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        binding.recsize.setAdapter(productSizesAdapter);
        binding.reccolor.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        binding.reccolor.setAdapter(productColorsAdapter);
        binding.tvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.expandLayout.isExpanded()) {
                    binding.expandLayout.collapse(true);
                } else {
                    binding.expandLayout.expand(true);
                }
            }
        });
    }

    private void getOrder() {
        ProgressDialog dialog = Common.createProgressDialog(this, getString(R.string.wait));
        dialog.setCancelable(false);
        dialog.show();
        try {
            Api.getService(Tags.base_url)
                    .Product_detials(product_id)
                    .enqueue(new Callback<SingleProductDataModel>() {
                        @Override
                        public void onResponse(Call<SingleProductDataModel> call, Response<SingleProductDataModel> response) {
                            dialog.dismiss();
                            if (response.isSuccessful() && response.body() != null) {
                                UPDATEUI(response.body());
                            } else {
                                if (response.code() == 500) {
                                    Toast.makeText(ProductDetailsActivity.this, "Server Error", Toast.LENGTH_SHORT).show();


                                } else {
                                    Toast.makeText(ProductDetailsActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

                                    try {

                                        Log.e("error", response.code() + "_" + response.errorBody().string());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<SingleProductDataModel> call, Throwable t) {
                            try {
                                dialog.dismiss();
                                if (t.getMessage() != null) {
                                    Log.e("error", t.getMessage());
                                    if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                        Toast.makeText(ProductDetailsActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(ProductDetailsActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                            } catch (Exception e) {
                            }
                        }
                    });
        } catch (Exception e) {

        }
    }

    private void UPDATEUI(SingleProductDataModel body) {

        binding.setModel(body);
        this.singleProductDataModel = body;

        binding.progBarSlider.setVisibility(View.GONE);
//        if (body.getColor() != null) {
//            binding.frame.setBackgroundColor(Color.parseColor(body.getColor()));
//        }

        NUM_PAGES = body.getProducts_images().size();
        slidingImage__adapter = new ProductDetialsSlidingImage_Adapter(this, body.getProducts_images());
        binding.pager.setAdapter(slidingImage__adapter);
        if (body.getSizes() != null && body.getSizes().size() > 0) {
            sizesList.addAll(body.getSizes());
            binding.tvOldprice.setText("0");
            binding.tvprice.setText("0");

        } else {
            selected_product_id = singleProductDataModel.getId() + "";
            price = singleProductDataModel.getPrice();
            color_id = singleProductDataModel.getPrice_id() + "";
            size_id = "0";
            image = singleProductDataModel.getImage();

        }
    }


    public void addToCart(SingleProductDataModel singleProductDataModel) {
        if ((singleProductDataModel.getSizes() != null && singleProductDataModel.getSizes().size() > 0 && !color_id.equals(null) && !size_id.equals(null)) || (singleProductDataModel.getSizes().size() == 0)) {
            if (cartSingleton.getItemCartModelList() != null && cartSingleton.getItemCartModelList().size() > 0) {
                int postion = -1;
                for (int i = 0; i < cartSingleton.getItemCartModelList().size(); i++) {
                    ItemCartModel itemCartModel = cartSingleton.getItemCartModelList().get(i);
                    if (selected_product_id.equals(itemCartModel.getProduct_id() + "") && color_id.equals(itemCartModel.getPrice_id())) {
                        postion = i;
                        break;
                    }
                }
                if (postion > -1) {
                    ItemCartModel itemCartModel = cartSingleton.getItemCartModelList().get(postion);
                    itemCartModel.setAmount(itemCartModel.getAmount() + 1);
                    itemCartModel.setPrice(itemCartModel.getAmount() * price);
                    cartSingleton.deleteItem(postion);
                    cartSingleton.addItem(itemCartModel);
                    if (binding.expandLayout.isExpanded()) {
                        binding.expandLayout.collapse(true);
                    } else {
                        binding.expandLayout.expand(true);
                    }
                    Toast.makeText(this, getResources().getString(R.string.add_to_cart), Toast.LENGTH_SHORT).show();
                } else {
                    ItemCartModel itemCartModel = new ItemCartModel(selected_product_id, singleProductDataModel.getTitle(), singleProductDataModel.getPrice(), 1, image, color_id, size_id);
                    cartSingleton.addItem(itemCartModel);
                    if (binding.expandLayout.isExpanded()) {
                        binding.expandLayout.collapse(true);
                    } else {
                        binding.expandLayout.expand(true);
                    }
                    Toast.makeText(this, getResources().getString(R.string.add_to_cart), Toast.LENGTH_SHORT).show();
                }
            } else {
                ItemCartModel itemCartModel = new ItemCartModel(selected_product_id, singleProductDataModel.getTitle(), singleProductDataModel.getPrice(), 1, image, color_id, size_id);
                cartSingleton.addItem(itemCartModel);
                if (binding.expandLayout.isExpanded()) {
                    binding.expandLayout.collapse(true);
                } else {
                    binding.expandLayout.expand(true);
                }
                Toast.makeText(this, getResources().getString(R.string.add_to_cart), Toast.LENGTH_SHORT).show();
            }
        } else {
            if (color_id == null) {
                Toast.makeText(ProductDetailsActivity.this, getResources().getString(R.string.select_color), Toast.LENGTH_LONG).show();
            }
            if (size_id == null) {
                Toast.makeText(ProductDetailsActivity.this, getResources().getString(R.string.select_size), Toast.LENGTH_LONG).show();

            }
        }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
//            createOrderModel = preferences.getCartData(this);
//            if (createOrderModel == null) {
//                createOrderModel = new CreateOrderModel();
//                createOrderModel.setMarkter_id(market.getId());
//                binding.setCartCount(0);
//                isDataAdded = true;
//
//            } else {
//
//                binding.setCartCount(createOrderModel.getProducts().size());
//            }
//        }
        }
    }

    public void show() {
        Intent intent = new Intent(this, ImagesActivity.class);
        intent.putExtra("data", singleProductDataModel);
        startActivityForResult(intent, 100);
    }

    public void setsizeid(SingleProductDataModel.Sizes sizes) {
        colorsList.clear();
        colorsList.addAll(sizes.getColors());
        productColorsAdapter.notifyDataSetChanged();
        size_id = sizes.getSize_id() + "";

    }

    public void setselectcolors(SingleProductDataModel.Sizes.Colors colors) {
        selected_product_id = singleProductDataModel.getId() + "";
        color_id = colors.getId() + "";
        image = colors.getImage();
        if (singleProductDataModel.getHave_offer().equals("no")) {
            price = colors.getPrice();

            binding.tvprice.setText(price + getResources().getString(R.string.ryal));
            binding.tvOldprice.setText(price + getResources().getString(R.string.ryal));
        } else {
            if (singleProductDataModel.getOffer_type().equals("per")) {
                price = colors.getPrice() - ((colors.getPrice() * singleProductDataModel.getOffer_value()) / 100);
            } else {
                price = colors.getPrice() - singleProductDataModel.getOffer_value();
            }
            binding.tvprice.setText(price + getResources().getString(R.string.ryal));
            binding.tvOldprice.setText(colors.getPrice() + getResources().getString(R.string.ryal));
        }
    }
}
