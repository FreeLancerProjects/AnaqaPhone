package com.anaqaphone.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.anaqaphone.R;
import com.anaqaphone.activities_fragments.activity_home.fragments.Fragment_Main;
import com.anaqaphone.activities_fragments.activity_my_favorite.MyFavoriteActivity;
import com.anaqaphone.databinding.FavouriteRowBinding;
import com.anaqaphone.databinding.OfferRowBinding;
import com.anaqaphone.models.FavouriteDataModel;
import com.anaqaphone.models.ItemCartModel;
import com.anaqaphone.models.SingleProductDataModel;
import com.anaqaphone.singleton.CartSingleton;

import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;

public class FavouriteProduct_Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<FavouriteDataModel.FavouriteData> list;
    private Context context;
    private LayoutInflater inflater;
    private String lang;
    private int i = 0;
    private CartSingleton cartSingleton;

    public FavouriteProduct_Adapter(List<FavouriteDataModel.FavouriteData> list, Context context) {
        this.list = list;
        this.context = context;
        inflater = LayoutInflater.from(context);
        Paper.init(context);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        cartSingleton = CartSingleton.newInstance();

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        FavouriteRowBinding binding = DataBindingUtil.inflate(inflater, R.layout.favourite_row, parent, false);
        return new EventHolder(binding);


    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder eventHohlder, int position) {

        EventHolder myHolder = (EventHolder) eventHohlder;
        myHolder.binding.tvOldprice.setPaintFlags(myHolder.binding.tvOldprice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        myHolder.binding.setModel(list.get(position).getProduct());
        if(list.get(position).getProduct().getStock()<=0){
            myHolder.binding.llAddToCart.setVisibility(View.GONE);
            myHolder.binding.tvStock.setVisibility(View.VISIBLE);
            myHolder.binding.ll.setVisibility(View.GONE);
        }
        myHolder.binding.llAddToCart.setOnClickListener(v -> {
            int count = Integer.parseInt(myHolder.binding.tvCounter.getText().toString());
            ItemCartModel itemCartModel = new ItemCartModel(list.get(position).getProduct().getId(), list.get(position).getProduct().getTitle(), list.get(position).getProduct().getPrice(), count, list.get(position).getProduct().getImage());
            cartSingleton.addItem(itemCartModel);
            if (context instanceof MyFavoriteActivity) {
                MyFavoriteActivity myFavoriteActivity = (MyFavoriteActivity) context;
               // myFavoriteActivity.updateCartCount(cartSingleton.getItemCount());
            }
            Toast.makeText(context, R.string.added_suc, Toast.LENGTH_SHORT).show();
        });
        myHolder.itemView.setOnClickListener(view -> {
            if (context instanceof MyFavoriteActivity) {

                MyFavoriteActivity myFavoriteActivity = (MyFavoriteActivity) context;
                myFavoriteActivity.setItemDataOffers(list.get(myHolder.getAdapterPosition()).getProduct());
            }


        });

        myHolder.binding.checkbox.setOnClickListener(v -> {
            if (context instanceof MyFavoriteActivity) {

                MyFavoriteActivity myFavoriteActivity = (MyFavoriteActivity) context;

                myFavoriteActivity.like_dislike( list.get(myHolder.getAdapterPosition()).getProduct(), myHolder.getAdapterPosition(),1);

            }


        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class EventHolder extends RecyclerView.ViewHolder {
        public FavouriteRowBinding binding;

        public EventHolder(@NonNull FavouriteRowBinding binding) {
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
