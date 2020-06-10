package com.anaqaphone.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.anaqaphone.R;
import com.anaqaphone.activities_fragments.activity_home.fragments.Fragment_Main;
import com.anaqaphone.activities_fragments.activity_home.fragments.Fragment_Offer;
import com.anaqaphone.databinding.OfferRowBinding;
import com.anaqaphone.models.ItemCartModel;
import com.anaqaphone.models.SingleProductDataModel;
import com.anaqaphone.singleton.CartSingleton;


import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;

public class CategoryProduct_Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<SingleProductDataModel> list;
    private Context context;
    private LayoutInflater inflater;
    private String lang;
    private int i = 0;
    private Fragment fragment;
    private CartSingleton cartSingleton;

    public CategoryProduct_Adapter(List<SingleProductDataModel> list, Context context, Fragment fragment) {
        this.list = list;
        this.context = context;
        inflater = LayoutInflater.from(context);
        Paper.init(context);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        this.fragment = fragment;
        cartSingleton = CartSingleton.newInstance();

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        OfferRowBinding binding = DataBindingUtil.inflate(inflater, R.layout.offer_row, parent, false);
        return new EventHolder(binding);


    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder eventHohlder, int position) {

        EventHolder myHolder = (EventHolder) eventHohlder;
        myHolder.binding.tvOldprice.setPaintFlags(myHolder.binding.tvOldprice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        myHolder.binding.setModel(list.get(position));
        myHolder.binding.setLang(lang);

        myHolder.binding.llAddToCart.setOnClickListener(v -> {
            int count = Integer.parseInt(myHolder.binding.tvCounter.getText().toString());
            ItemCartModel itemCartModel = new ItemCartModel(list.get(position).getId(), list.get(position).getTitle(), list.get(position).getPrice(), count, list.get(position).getImage());
            cartSingleton.addItem(itemCartModel);
            if (fragment instanceof Fragment_Main) {
                Fragment_Main fragment_main = (Fragment_Main) fragment;
                fragment_main.updateCartCount(cartSingleton.getItemCount());
            }
            Toast.makeText(context, R.string.added_suc, Toast.LENGTH_SHORT).show();
        });
        myHolder.itemView.setOnClickListener(view -> {
            if (fragment instanceof Fragment_Main) {

                Fragment_Main fragment_main = (Fragment_Main) fragment;
                fragment_main.setItemDataOffers(list.get(myHolder.getAdapterPosition()));
            }


        });

        myHolder.binding.checkbox.setOnClickListener(v -> {
            if (fragment instanceof Fragment_Main) {

                Fragment_Main fragment_main = (Fragment_Main) fragment;

                if (myHolder.binding.checkbox.isChecked()) {
                    fragment_main.like_dislike( list.get(myHolder.getAdapterPosition()), myHolder.getAdapterPosition(),1);
                } else {
                    fragment_main.like_dislike( list.get(myHolder.getAdapterPosition()), myHolder.getAdapterPosition(),1);

                }
            }


        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class EventHolder extends RecyclerView.ViewHolder {
        public OfferRowBinding binding;

        public EventHolder(@NonNull OfferRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.imgIncrease.setOnClickListener(v -> {
                        int count = Integer.parseInt(binding.tvCounter.getText().toString()) + 1;
                        binding.tvCounter.setText(String.valueOf(count));

                    }

            );
            binding.imgDecrease.setOnClickListener(v -> {
                        int count = Integer.parseInt(binding.tvCounter.getText().toString());
                        if (count > 1) {
                            count = count - 1;

                            binding.tvCounter.setText(String.valueOf(count));

                        }
                    }

            );

        }
    }


}
