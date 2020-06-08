package com.anaqaphone.activities_fragments.activity_product_details;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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

//import com.anaqaphone.Animate.CircleAnimationUtil;
import com.anaqaphone.R;
import com.anaqaphone.adapters.ProductDetialsSlidingImage_Adapter;
import com.anaqaphone.databinding.ActivityProductDetailsBinding;
import com.anaqaphone.interfaces.Listeners;
import com.anaqaphone.language.Language;

import com.anaqaphone.models.ProductDataModel;
import com.anaqaphone.models.UserModel;
import com.anaqaphone.preferences.Preferences;
import com.anaqaphone.remote.Api;
import com.anaqaphone.share.Common;
import com.anaqaphone.tags.Tags;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetailsActivity extends AppCompatActivity implements Listeners.BackListener {
    private ActivityProductDetailsBinding binding;
    private String lang;
    private ProductDataModel.Data productDataModel;
    private int product_id;
    private Preferences preferences;
    private TimerTask timerTask;
    private Timer timer;
    private int current_page = 0, NUM_PAGES;
    private UserModel userModel;
    private ProductDetialsSlidingImage_Adapter slidingImage__adapter;
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
        product_id=1;
    }


    private void initView() {
        Paper.init(this);
        preferences = Preferences.getInstance();
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        binding.setBackListener(this);
        binding.setLang(lang);

        binding.setModel(productDataModel);
        binding.tab.setupWithViewPager(binding.pager);
        binding.progBarSlider.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary), PorterDuff.Mode.SRC_IN);

        binding.tvOldprice.setPaintFlags( binding.tvOldprice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);


        //  binding.flAddToCart.setOnClickListener(v -> addToCart(offerModel));

    }

    private void getOrder() {
        ProgressDialog dialog = Common.createProgressDialog(this, getString(R.string.wait));
        dialog.setCancelable(false);
        dialog.show();
        try {
            Api.getService(Tags.base_url)
                    .Product_detials(product_id)
                    .enqueue(new Callback<ProductDataModel>() {
                        @Override
                        public void onResponse(Call<ProductDataModel> call, Response<ProductDataModel> response) {
                            dialog.dismiss();
                            if (response.isSuccessful() && response.body() != null) {
                                UPDATEUI(response.body().getData());
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
                        public void onFailure(Call<ProductDataModel> call, Throwable t) {
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

    private void UPDATEUI(List<ProductDataModel.Data> body) {

        binding.setModel(body.get(product_id));
        binding.progBarSlider.setVisibility(View.GONE);


                NUM_PAGES =body.get(product_id).getProducts_images().size();
                slidingImage__adapter = new ProductDetialsSlidingImage_Adapter(this, body.get(product_id).getProducts_images());
                binding.pager.setAdapter(slidingImage__adapter);
    }


//    public void addToCart(OfferModel offerModel) {
//        if (market.getMarkter_status().equals("open")) {
//            if (market.getId() == createOrderModel.getMarkter_id()) {
//                ItemCartModel model = new ItemCartModel(offerModel.getId(), offerModel.getId(), offerModel.getImage(), offerModel.getTitle(), 1);
//                model.setPrice_before_discount(Double.parseDouble(offerModel.getPrice()));
//
//                if (offerModel.getOffer() == null) {
//                    model.setPrice(Double.parseDouble(offerModel.getPrice()));
//                    model.setOffer_id(0);
//                } else {
//
//                    if (offerModel.getOffer().getOffer_status().trim().equals("open")) {
//                        if (offerModel.getOffer().getOffer_type().trim().equals("per")) {
//                            double price_before_discount = Double.parseDouble(offerModel.getPrice());
//                            double price_after_discount = price_before_discount - (price_before_discount * (offerModel.getOffer().getOffer_value() / 100));
//                            model.setPrice(price_after_discount);
//                            model.setOffer_id(offerModel.getOffer().getId());
//                        } else {
//                            double price_before_discount = Double.parseDouble(offerModel.getPrice());
//                            double price_after_discount = price_before_discount - offerModel.getOffer().getOffer_value();
//                            model.setPrice(price_after_discount);
//                            model.setOffer_id(offerModel.getOffer().getId());
//                        }
//                    } else {
//                        model.setPrice(Double.parseDouble(offerModel.getPrice()));
//                        model.setOffer_id(0);
//                    }
//                }
//
//
//                createOrderModel.addNewProduct(model);
//                preferences.create_update_cart(this, createOrderModel);
//                // binding.setCartCount(createOrderModel.getProducts().size());
//                makeFlyAnimation(binding.image, createOrderModel.getProducts().size());
//                isDataAdded = true;
//                //Toast.makeText(this, getString(R.string.added_suc), Toast.LENGTH_SHORT).show();
//            } else {
//                Common.CreateDialogAlert(this, getString(R.string.diff_market));
//            }
//        } else {
//            Common.CreateDialogAlert(this, getString(R.string.market_not_available));
//
//        }
//
//    }

//    public void makeFlyAnimation(RoundedImageView targetView, int quantity) {
//
//
//        new CircleAnimationUtil().attachActivity(this).setTargetView(targetView, lang).setMoveDuration(1000).setDestView(binding.flCart).setAnimationListener(new Animator.AnimatorListener() {
//            @Override
//            public void onAnimationStart(Animator animation) {
//
//            }
//
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                //     addItemToCart();
//                binding.setCartCount(createOrderModel.getProducts().size());
//
//                //  Toast.makeText(homeActivity, "Continue Shopping...", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onAnimationCancel(Animator animation) {
//
//            }
//
//            @Override
//            public void onAnimationRepeat(Animator animation) {
//
//            }
//        }).startAnimation();
//
//
//    }


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
}
