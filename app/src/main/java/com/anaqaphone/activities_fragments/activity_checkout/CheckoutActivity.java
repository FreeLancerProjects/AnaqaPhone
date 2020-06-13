package com.anaqaphone.activities_fragments.activity_checkout;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.anaqaphone.R;
import com.anaqaphone.activities_fragments.activity_checkout.fragments.Fragment_Address;
import com.anaqaphone.activities_fragments.activity_checkout.fragments.Fragment_Date;
import com.anaqaphone.activities_fragments.activity_checkout.fragments.Fragment_Payment_Type;
import com.anaqaphone.databinding.ActivityCheckoutBinding;
import com.anaqaphone.interfaces.Listeners;
import com.anaqaphone.language.Language;
import com.anaqaphone.models.AddOrderModel;
import com.anaqaphone.models.ItemCartUploadModel;
import com.anaqaphone.models.OrderModel;
import com.anaqaphone.models.UserModel;
import com.anaqaphone.preferences.Preferences;
import com.anaqaphone.remote.Api;
import com.anaqaphone.share.Common;
import com.anaqaphone.singleton.CartSingleton;
import com.anaqaphone.tags.Tags;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckoutActivity extends AppCompatActivity implements Listeners.BackListener{
    private ActivityCheckoutBinding binding;
    private String lang;
    private FragmentManager fragmentManager;
    private Fragment_Address fragment_address;
    private Fragment_Date fragment_date;
    private Fragment_Payment_Type fragment_payment_type;
    private AddOrderModel addOrderModel;
    private CartSingleton singleton;
    private ItemCartUploadModel itemCartUploadModel;
    private UserModel userModel;
    private Preferences preferences;
    private double total_cost=0.0;
    private int tax;
    private OrderModel orderModel;


    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang", "ar")));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_checkout);
        fragmentManager = getSupportFragmentManager();
        getDataFromIntent();
        initView();
        if (savedInstanceState == null) {
            displayFragmentAddress();
        }
    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        total_cost = intent.getDoubleExtra("total_cost",0.0);
        tax = intent.getIntExtra("coupun",0);

    }


    private void initView()
    {
        preferences = Preferences.getInstance();
        userModel = preferences.getUserData(this);
        singleton = CartSingleton.newInstance();
        addOrderModel = new AddOrderModel();
        addOrderModel.setTotal_price(total_cost);
        addOrderModel.setCoupon_id(tax+"");
        Paper.init(this);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        binding.setBackListener(this);
        binding.setLang(lang);



    }


    public void displayFragmentAddress()
    {
        try {
            if (fragment_address == null) {
                fragment_address = Fragment_Address.newInstance(addOrderModel);
            }else {
                fragment_address.setModel(addOrderModel);
            }

            if (fragment_date != null && fragment_date.isAdded()) {
                fragmentManager.beginTransaction().hide(fragment_date).commit();
            }
            if (fragment_payment_type != null && fragment_payment_type.isAdded()) {
                fragmentManager.beginTransaction().hide(fragment_payment_type).commit();
            }

            if (fragment_address.isAdded()) {
                fragmentManager.beginTransaction().show(fragment_address).commit();

            } else {
                fragmentManager.beginTransaction().add(R.id.fragment_container, fragment_address, "fragment_address").addToBackStack("fragment_address").commit();

            }

            binding.tvAddress.setTextColor(ContextCompat.getColor(this,R.color.white));
            binding.tvAddress.setBackgroundColor(ContextCompat.getColor(this,R.color.colorPrimaryDark));


            binding.tvDate.setTextColor(ContextCompat.getColor(this,R.color.colorPrimaryDark));
            binding.tvDate.setBackgroundColor(ContextCompat.getColor(this,R.color.transparent));


            binding.tvPayment.setTextColor(ContextCompat.getColor(this,R.color.colorPrimaryDark));
            binding.tvPayment.setBackgroundColor(ContextCompat.getColor(this,R.color.transparent));

        } catch (Exception e) {
        }

    }
//    public void displayFragmentDate()
//    {
//        try {
//            if (fragment_date == null) {
//                fragment_date = Fragment_Date.newInstance(addOrderModel);
//            }else {
//                fragment_date.setModel(addOrderModel);
//            }
//
//            if (fragment_address != null && fragment_address.isAdded()) {
//                fragmentManager.beginTransaction().hide(fragment_address).commit();
//            }
//            if (fragment_payment_type != null && fragment_payment_type.isAdded()) {
//                fragmentManager.beginTransaction().hide(fragment_payment_type).commit();
//            }
//
//            if (fragment_date.isAdded()) {
//                fragmentManager.beginTransaction().show(fragment_date).commit();
//
//            } else {
//                fragmentManager.beginTransaction().add(R.id.fragment_container, fragment_date, "fragment_date").addToBackStack("fragment_date").commit();
//
//            }
//
//            binding.tvDate.setTextColor(ContextCompat.getColor(this,R.color.white));
//            binding.tvDate.setBackgroundColor(ContextCompat.getColor(this,R.color.colorPrimaryDark));
//
//
//            binding.tvAddress.setTextColor(ContextCompat.getColor(this,R.color.colorPrimaryDark));
//            binding.tvAddress.setBackgroundColor(ContextCompat.getColor(this,R.color.transparent));
//
//
//            binding.tvPayment.setTextColor(ContextCompat.getColor(this,R.color.colorPrimaryDark));
//            binding.tvPayment.setBackgroundColor(ContextCompat.getColor(this,R.color.transparent));
//
//
//
//        } catch (Exception e) {
//        }
//
//    }
    public void displayFragmentPaymentType()
    {
        try {
            if (fragment_payment_type == null) {
                fragment_payment_type = Fragment_Payment_Type.newInstance(addOrderModel);
            }else {
                fragment_payment_type.setModel(addOrderModel);
            }

            if (fragment_date != null && fragment_date.isAdded()) {
                fragmentManager.beginTransaction().hide(fragment_date).commit();
            }
            if (fragment_address != null && fragment_address.isAdded()) {
                fragmentManager.beginTransaction().hide(fragment_address).commit();
            }

            if (fragment_payment_type.isAdded()) {
                fragmentManager.beginTransaction().show(fragment_payment_type).commit();

            } else {
                fragmentManager.beginTransaction().add(R.id.fragment_container, fragment_payment_type, "fragment_payment_type").addToBackStack("fragment_payment_type").commit();

            }

            binding.tvPayment.setTextColor(ContextCompat.getColor(this,R.color.white));
            binding.tvPayment.setBackgroundColor(ContextCompat.getColor(this,R.color.colorPrimaryDark));


            binding.tvAddress.setTextColor(ContextCompat.getColor(this,R.color.colorPrimaryDark));
            binding.tvAddress.setBackgroundColor(ContextCompat.getColor(this,R.color.transparent));


            binding.tvDate.setTextColor(ContextCompat.getColor(this,R.color.colorPrimaryDark));
            binding.tvDate.setBackgroundColor(ContextCompat.getColor(this,R.color.transparent));


        } catch (Exception e) {
        }

    }


    public void updateModel(AddOrderModel addOrderModel)
    {
        this.addOrderModel = addOrderModel;
    }


    public void createOrder() {
        try {
            ProgressDialog dialog = Common.createProgressDialog(this,getString(R.string.wait));
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
          //  String date = new SimpleDateFormat("dd-MM-yyyy",Locale.ENGLISH).format(new Date(addOrderModel.getDate()));
          //  itemCartUploadModel = new ItemCartUploadModel(userModel.getUser().getUser_id(),addOrderModel.getAddress(),String.valueOf(addOrderModel.getLat()),String.valueOf(addOrderModel.getLng()),date,String.valueOf(addOrderModel.getTime()/1000),addOrderModel.getPayment_type(),singleton.getItemCartModelList(),String.valueOf(tax),String.valueOf(total_cost));
            Api.getService(Tags.base_url)
                    .createOrder(userModel.getUser().getToken(),addOrderModel)
                    .enqueue(new Callback<OrderModel>() {
                        @Override
                        public void onResponse(Call<OrderModel> call, Response<OrderModel> response) {
                            dialog.dismiss();
                            if (response.isSuccessful() ) {


                                if (response.body() != null ) {

                                    orderModel = response.body();
                                    if (addOrderModel.getPayment_type().equals("cash")){
                                        Toast.makeText(CheckoutActivity.this, getString(R.string.suc), Toast.LENGTH_SHORT).show();
                                        Intent intent = getIntent();
                                        intent.putExtra("data", orderModel);
                                        setResult(RESULT_OK, intent);
                                        finish();}
//                                    }else {
//                                        Intent intent = new Intent(CheckoutActivity.this, OnlinePaymentActivity.class);
//                                        intent.putExtra("data", response.body());
//                                        startActivityForResult(intent, 100);
//                                    }


                                }

                            } else {
                                dialog.dismiss();
                                if (response.code() == 500) {
                                    Toast.makeText(CheckoutActivity.this, "Server Error", Toast.LENGTH_SHORT).show();


                                } else {
                                    Toast.makeText(CheckoutActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

                                    try {

                                        Log.e("errorsss", response.code() + "_" + response.errorBody().string());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<OrderModel> call, Throwable t) {
                            try {
                                dialog.dismiss();
                                if (t.getMessage() != null) {
                                    Log.e("error", t.getMessage());
                                    if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                        Toast.makeText(CheckoutActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(CheckoutActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
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
    public void onBackPressed() {
        back();
    }

    @Override
    public void back() {
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        List<Fragment> fragmentList = fragmentManager.getFragments();
        for (Fragment fragment :fragmentList)
        {
            fragment.onActivityResult(requestCode, resultCode, data);
        }

        if (requestCode == 100) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(CheckoutActivity.this, getString(R.string.suc), Toast.LENGTH_SHORT).show();
                Intent intent = getIntent();
                intent.putExtra("data", orderModel);
                setResult(RESULT_OK, intent);
                finish();
            }else if (resultCode==RESULT_CANCELED){
                Toast.makeText(this, "بلح", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        List<Fragment> fragmentList = fragmentManager.getFragments();
        for (Fragment fragment :fragmentList)
        {
            fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


}
