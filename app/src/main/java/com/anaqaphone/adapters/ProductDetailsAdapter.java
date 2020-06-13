package com.anaqaphone.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.anaqaphone.R;
import com.anaqaphone.databinding.ProductDetailsRowBinding;
import com.anaqaphone.models.OrderModel;

import java.util.List;

public class ProductDetailsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<OrderModel.OrdersDetails> list;
    private Context context;
    private LayoutInflater inflater;

    public ProductDetailsAdapter(List<OrderModel.OrdersDetails> list, Context context) {
        this.list = list;
        this.context = context;
        inflater = LayoutInflater.from(context);


    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        ProductDetailsRowBinding binding = DataBindingUtil.inflate(inflater, R.layout.product_details_row, parent, false);
        return new MyHolder(binding);


    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        MyHolder myHolder = (MyHolder) holder;
        OrderModel.OrdersDetails  model = list.get(position);

        myHolder.binding.setModel(model);



    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        public ProductDetailsRowBinding binding;

        public MyHolder(@NonNull ProductDetailsRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
    }


}
