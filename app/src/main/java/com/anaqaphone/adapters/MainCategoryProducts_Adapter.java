package com.anaqaphone.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.anaqaphone.R;
import com.anaqaphone.databinding.CategoryproductRowBinding;
import com.anaqaphone.models.CategoryProductDataModel;

import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;

public class MainCategoryProducts_Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<CategoryProductDataModel.Data> list;
    private Context context;
    private LayoutInflater inflater;
    private String lang;
    private int i = 0;
    private Fragment fragment;

    public MainCategoryProducts_Adapter(List<CategoryProductDataModel.Data> list, Context context, Fragment fragment) {
        this.list = list;
        this.context = context;
        inflater = LayoutInflater.from(context);
        Paper.init(context);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        CategoryproductRowBinding binding = DataBindingUtil.inflate(inflater, R.layout.categoryproduct_row, parent, false);
        return new EventHolder(binding);


    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        EventHolder eventHolder = (EventHolder) holder;
        eventHolder.binding.setModel(list.get(position));
        if (list.get(position).getProducts() == null || list.get(position).getProducts().size() == 0) {
            eventHolder.itemView.setVisibility(View.GONE);
        }
        CategoryProduct_Adapter explore_product_adapter = new CategoryProduct_Adapter(list.get(position).getProducts(), context, fragment);
        ((EventHolder) eventHolder).binding.recview1.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
        ((EventHolder) eventHolder).binding.recview1.setAdapter(explore_product_adapter);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class EventHolder extends RecyclerView.ViewHolder {
        public CategoryproductRowBinding binding;

        public EventHolder(@NonNull CategoryproductRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
    }


}
