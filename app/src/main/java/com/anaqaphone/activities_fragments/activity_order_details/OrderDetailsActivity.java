package com.anaqaphone.activities_fragments.activity_order_details;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.anaqaphone.R;
import com.anaqaphone.adapters.ProductDetailsAdapter;
import com.anaqaphone.databinding.ActivityOrderDetailsBinding;
import com.anaqaphone.interfaces.Listeners;
import com.anaqaphone.language.Language;
import com.anaqaphone.models.OrderModel;
import com.anaqaphone.share.Common;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;

public class OrderDetailsActivity extends AppCompatActivity implements Listeners.BackListener{
    private ActivityOrderDetailsBinding binding;
    private String lang;
    private OrderModel orderModel;
    private List<OrderModel.OrdersDetails> orderDetailsList;
    private ProductDetailsAdapter adapter;

    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang","ar")));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_order_details);
        getDataFromIntent();
        initView();
    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        if (intent!=null)
        {
            orderModel = (OrderModel) intent.getSerializableExtra("data");

        }
    }


    private void initView()
    {
        orderDetailsList = new ArrayList<>();
        Paper.init(this);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        binding.setBackListener(this);
        binding.setLang(lang);
        binding.setModel(orderModel);
        orderDetailsList.addAll(orderModel.getOrders_details());
        adapter = new ProductDetailsAdapter(orderDetailsList,this);
        binding.recView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        binding.recView.setAdapter(adapter);


    }




    @Override
    public void back() {
        finish();
    }

}
