package com.anaqaphone.activities_fragments.activity_home.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.anaqaphone.R;
import com.anaqaphone.activities_fragments.activity_about_app.AboutAppActivity;
import com.anaqaphone.activities_fragments.activity_checkout.CheckoutActivity;
import com.anaqaphone.activities_fragments.activity_home.HomeActivity;
import com.anaqaphone.activities_fragments.activity_order_details.OrderDetailsActivity;
import com.anaqaphone.adapters.CartAdapter;
import com.anaqaphone.adapters.Swipe;
import com.anaqaphone.databinding.FragmentCartBinding;
import com.anaqaphone.models.ItemCartModel;
import com.anaqaphone.models.ItemCartUploadModel;
import com.anaqaphone.models.OrderModel;
import com.anaqaphone.models.SettingModel;
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

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fragment_Cart extends Fragment implements Swipe.SwipeListener {

    private HomeActivity activity;
    private FragmentCartBinding binding;
    private Preferences preferences;
    private UserModel userModel;
    private String lang;
    private LinearLayoutManager manager;
    private CartSingleton singleton;
    private CartAdapter adapter;
    private List<ItemCartModel> itemCartModelList;
    private ItemCartUploadModel itemCartModel;
    private double total = 0.0;
    private double tax = 0.0;

    private SettingModel appDataModel;
    private int coupon_id;
    private SettingModel settingmodel;
    private boolean israaive = false;

    public static Fragment_Cart newInstance() {
        return new Fragment_Cart();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_cart, container, false);
        initView();

        getAppData();

        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    private void getAppData() {

        Api.getService(Tags.base_url)
                .getSetting(lang)
                .enqueue(new Callback<SettingModel>() {
                    @Override
                    public void onResponse(Call<SettingModel> call, Response<SettingModel> response) {
                        if (response.isSuccessful() && response.body() != null) {

                            settingmodel = response.body();
                            updateUI();

                        } else {
                            try {

                                Log.e("error", response.code() + "_" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            if (response.code() == 500) {
                                // Toast.makeText(AboutAppActivity.this, "Server Error", Toast.LENGTH_SHORT).show();

                            } else {
                                //Toast.makeText(AboutAppActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();


                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<SettingModel> call, Throwable t) {
                        try {
                            //binding.progBar.setVisibility(View.GONE);

                            if (t.getMessage() != null) {
                                Log.e("error", t.getMessage());
                                if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                    //      Toast.makeText(AboutAppActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                                } else {
                                    //    Toast.makeText(AboutAppActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }

                        } catch (Exception e) {
                        }
                    }
                });

    }

    private void initView() {
        itemCartModelList = new ArrayList<>();
        activity = (HomeActivity) getActivity();
        Paper.init(activity);
        lang = Paper.book().read("lang", "ar");
        binding.setLang(lang);
        preferences = Preferences.getInstance();
        userModel = preferences.getUserData(activity);
        singleton = CartSingleton.newInstance();
        itemCartModelList.addAll(singleton.getItemCartModelList());
        manager = new LinearLayoutManager(activity);
        binding.recView.setLayoutManager(manager);
        adapter = new CartAdapter(this, itemCartModelList, activity);
        binding.btnShop.setOnClickListener(view -> activity.displayFragmentMain());
        ItemTouchHelper.SimpleCallback simpleCallback = new Swipe(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT, this);
        ItemTouchHelper helper = new ItemTouchHelper(simpleCallback);
        helper.attachToRecyclerView(binding.recView);

        binding.flSearch.setOnClickListener(view -> {
            String coupon = binding.edtCopoun.getText().toString();
            Log.e("bbb", coupon);
            if (!coupon.isEmpty()) {
                confirmCoupon(coupon);
            } else {
                binding.edtCopoun.setError(activity.getResources().getString(R.string.field_required));
            }
        });
        binding.btnCheckout.setOnClickListener(view -> navigateToCheckoutActivity());
        binding.rbChoose1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    israaive = true;
                    binding.llarrive.setVisibility(View.VISIBLE);
                    binding.tvarrive.setText(settingmodel.getDelivery_value() + "");
                    total += settingmodel.getDelivery_value();
                    binding.tvTotal.setText(String.format(Locale.ENGLISH, "%s %s", String.valueOf(total), getString(R.string.sar)));
                }
            }
        });
        binding.rbChoose2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    binding.llarrive.setVisibility(View.GONE);
                    if (israaive == true) {
                        total -= settingmodel.getDelivery_value();
                        binding.tvTotal.setText(String.format(Locale.ENGLISH, "%s %s", String.valueOf(total), getString(R.string.sar)));
                    }
                    israaive = false;
                }
            }
        });
    }


    private void confirmCoupon(String coupon) {
        final ProgressDialog dialog = Common.createProgressDialog(activity, getString(R.string.wait));
        dialog.setCancelable(false);
        dialog.show();
        Api.getService(Tags.base_url)
                .getCouponValue(coupon)
                .enqueue(new Callback<SettingModel>() {
                    @Override
                    public void onResponse(Call<SettingModel> call, Response<SettingModel> response) {
                        dialog.dismiss();
                        if (response.isSuccessful()) {
                            binding.lldiscount.setVisibility(View.VISIBLE);
                            binding.tvdiscount.setText(response.body().getCoupon_value() + "%");
                            Toast.makeText(activity, activity.getResources().getString(R.string.suc), Toast.LENGTH_LONG).show();
                            updateUI(response.body());

                        } else {
                            try {
                                Log.e("error_code", response.code() + "_" + response.errorBody().string());
                            } catch (Exception e) {
                            }

                            if (response.code() == 404 || response.code() == 422) {
                                Toast.makeText(activity, getString(R.string.not_found), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(activity, getString(R.string.something), Toast.LENGTH_SHORT).show();


                            }


                        }
                    }

                    @Override
                    public void onFailure(Call<SettingModel> call, Throwable t) {

                        try {
                            dialog.dismiss();
                            Toast.makeText(activity, getString(R.string.something), Toast.LENGTH_SHORT).show();
                            Log.e("Error", t.getMessage());
                        } catch (Exception e) {
                        }


                    }
                });


    }

    private void updateUI(SettingModel body) {
        this.settingmodel = body;
        coupon_id = body.getCoupon_id();
        total = total - (total * body.getCoupon_value()) / 100;
        binding.tvTotal.setText(String.format(Locale.ENGLISH, "%s %s", String.valueOf(total), getString(R.string.sar)));

    }


    private void navigateToCheckoutActivity() {
        if (userModel != null) {
            Intent intent = new Intent(activity, CheckoutActivity.class);
            intent.putExtra("total_cost", total);
            intent.putExtra("coupun", coupon_id);
            intent.putExtra("tax", tax);
            intent.putExtra("isarrive", israaive);
            intent.putExtra("arrive", settingmodel.getDelivery_value());
            intent.putExtra("del", settingmodel.getPay_when_recieving());

            startActivityForResult(intent, 100);
        } else {
            Common.CreateDialogAlert(activity, getString(R.string.please_sign_in_or_sign_up));
        }
    }


    public void updateUI() {

        /*if (singleton==null){
            singleton = CartSingleton.newInstance();

        }*/

        itemCartModelList.clear();
        itemCartModelList.addAll(singleton.getItemCartModelList());

        Log.e("lllll", itemCartModelList.size() + "");
        binding.lldiscount.setVisibility(View.GONE);
        adapter.notifyDataSetChanged();
        if (itemCartModelList.size() == 0) {
            // Log.e("lllllss", itemCartModelList.size() + "");

            binding.btnCheckout.setVisibility(View.GONE);
            binding.llSearch.setVisibility(View.GONE);
            binding.llEmptyCart.setVisibility(View.VISIBLE);
            binding.llTotal.setVisibility(View.GONE);
            binding.llCheckout.setVisibility(View.GONE);
            binding.llarrive.setVisibility(View.GONE);
            binding.tvdiscount.setText("");
            binding.tvTotal.setText("");
            binding.edtCopoun.setText("");

        } else {
            binding.btnCheckout.setVisibility(View.VISIBLE);
            binding.llEmptyCart.setVisibility(View.GONE);
            binding.llSearch.setVisibility(View.VISIBLE);
            binding.llTotal.setVisibility(View.VISIBLE);
            binding.llCheckout.setVisibility(View.VISIBLE);


        }
        calculateTotal();
        activity.updateCartCount(itemCartModelList.size());

    }


    public void calculateTotal() {
        total = 0;
        tax = 0;
        for (ItemCartModel model : itemCartModelList) {
            total += model.getPrice() * model.getAmount();
        }
        tax = (total * settingmodel.getTax()) / 100;
        total += (total * settingmodel.getTax()) / 100;

        binding.tvTotal.setText(String.format(Locale.ENGLISH, "%s %s", String.valueOf(total), getString(R.string.sar)));

    }


    @Override
    public void onSwipe(int pos, int dir) {
        CreateCartDeleteDialog(pos);
    }

    private void CreateCartDeleteDialog(final int pos) {

        final AlertDialog dialog = new AlertDialog.Builder(activity)
                .setCancelable(true)
                .create();

        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_delete, null);
        TextView tvDelete = view.findViewById(R.id.tvDelete);
        TextView tvCancel = view.findViewById(R.id.tvCancel);
        tvCancel.setOnClickListener(v -> {
            adapter.notifyDataSetChanged();
            dialog.dismiss();

        });

        tvDelete.setOnClickListener(v -> {
            try {
                itemCartModelList.remove(pos);
                singleton.deleteItem(pos);
                adapter.notifyItemRemoved(pos);
                updateUI();
                //  calculateTotal();
                if (itemCartModelList.size() == 0) {
                    binding.llCheckout.setVisibility(View.GONE);
                    binding.llEmptyCart.setVisibility(View.VISIBLE);
                    binding.llTotal.setVisibility(View.GONE);

                }
            } catch (Exception e) {
            }

            dialog.dismiss();
        });

        dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_congratulation_animation;
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_window_bg);
        dialog.setView(view);
        dialog.show();

    }


    public void increase_decrease(ItemCartModel model2, int adapterPosition) {
        itemCartModelList.set(adapterPosition, model2);
        calculateTotal();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == Activity.RESULT_OK && data != null) {
            itemCartModelList.clear();
            adapter.notifyDataSetChanged();
            singleton.clear();
            binding.llCheckout.setVisibility(View.GONE);
            binding.llEmptyCart.setVisibility(View.VISIBLE);
            binding.llTotal.setVisibility(View.GONE);

            OrderModel orderModel = (OrderModel) data.getSerializableExtra("data");
            navigateToOrderDetailsActivity(orderModel);


        }
    }

    private void navigateToOrderDetailsActivity(OrderModel orderModel) {
        Intent intent = new Intent(activity, OrderDetailsActivity.class);
        intent.putExtra("data", orderModel);
        startActivity(intent);
    }
}
