package com.anaqaphone.activities_fragments.activity_checkout.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.anaqaphone.R;
import com.anaqaphone.activities_fragments.activity_checkout.CheckoutActivity;
import com.anaqaphone.databinding.FragmentPaymentTypeBinding;
import com.anaqaphone.interfaces.Listeners;
import com.anaqaphone.models.AddOrderModel;
import com.anaqaphone.singleton.CartSingleton;

public class Fragment_Payment_Type extends Fragment implements Listeners.PaymentTypeAction {
    private static final String TAG = "data";
    private CheckoutActivity activity;
    private FragmentPaymentTypeBinding binding;
    private String payment_type;
    private AddOrderModel addOrderModel;
    private CartSingleton singleton;


    public static Fragment_Payment_Type newInstance(AddOrderModel addOrderModel) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(TAG, addOrderModel);
        Fragment_Payment_Type fragment_payment_type = new Fragment_Payment_Type();
        fragment_payment_type.setArguments(bundle);
        return fragment_payment_type;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_payment_type, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();


    }


    private void initView() {
        singleton = CartSingleton.newInstance();
        activity = (CheckoutActivity) getActivity();
        binding.setAction(this);
        binding.tvvat.setText(activity.tax + "");
        if (activity.isarrive) {
            binding.tvArriveprice.setText(activity.arrive + "");
        }
        binding.tvTotal.setText(activity.total_cost + "");
        binding.tvdelivry.setText(activity.recive + "");
        binding.tvProductprice.setText(activity.total_items + "");
        Bundle bundle = getArguments();
        if (bundle != null) {
            addOrderModel = (AddOrderModel) bundle.getSerializable(TAG);
        }
        onCash();

    }


    public void setModel(AddOrderModel model) {
        this.addOrderModel = model;
    }

    @Override
    public void onCredit() {
        payment_type = "card";
        binding.img1.setVisibility(View.VISIBLE);
        binding.img2.setVisibility(View.GONE);
        binding.img3.setVisibility(View.GONE);
        binding.lldel.setVisibility(View.GONE);
        addOrderModel.setPay_type(payment_type);
        addOrderModel.setCash_pay(0.0);
        activity.total_cost = activity.total_cost - activity.recive;
        binding.tvTotal.setText((activity.total_cost) + "");


    }

    @Override
    public void onPaypal() {
        payment_type = "paypal";
        binding.img1.setVisibility(View.GONE);
        binding.img2.setVisibility(View.VISIBLE);
        binding.img3.setVisibility(View.GONE);
        binding.lldel.setVisibility(View.GONE);
        addOrderModel.setPay_type(payment_type);


    }

    @Override
    public void onCash() {
        payment_type = "cash";
        binding.img1.setVisibility(View.GONE);
        binding.img2.setVisibility(View.GONE);
        binding.img3.setVisibility(View.VISIBLE);
        binding.lldel.setVisibility(View.VISIBLE);
        addOrderModel.setPay_type(payment_type);
        activity.total_cost = activity.total_cost + activity.recive;
        binding.tvTotal.setText((activity.total_cost) + "");
        addOrderModel.setCash_pay(activity.recive);

    }

    @Override
    public void onNext() {

        if (addOrderModel.isStep3Valid(activity)) {
            addOrderModel.setProducts(singleton.getItemCartModelList());
            activity.updateModel(addOrderModel);
            activity.createOrder();
        }


    }

    @Override
    public void onPrevious() {
        activity.displayFragmentAddress();
    }
}
