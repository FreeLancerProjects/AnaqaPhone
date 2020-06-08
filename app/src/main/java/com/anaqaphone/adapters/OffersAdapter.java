package com.anaqaphone.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import com.anaqaphone.R;
import com.anaqaphone.activities_fragments.activity_home.fragments.Fragment_Main;
import com.anaqaphone.databinding.OfferRowBinding;
import com.anaqaphone.models.ItemCartModel;
import com.anaqaphone.models.ProductDataModel;
import com.anaqaphone.singleton.CartSingleton;

import java.util.List;

public class OffersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ProductDataModel.Data> list;
    private Context context;
    private LayoutInflater inflater;
    private Fragment fragment;
    private CartSingleton cartSingleton;

    public OffersAdapter(List<ProductDataModel.Data> list, Context context, Fragment fragment) {
        this.list = list;
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.fragment = fragment;
        cartSingleton = CartSingleton.newInstance();



    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        OfferRowBinding binding = DataBindingUtil.inflate(inflater, R.layout.offer_row, parent, false);
        return new MyHolder(binding);


    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        MyHolder myHolder = (MyHolder) holder;

        myHolder.binding.setModel(list.get(position));
        myHolder.binding.llAddToCart.setOnClickListener(v -> {
            int count = Integer.parseInt(myHolder.binding.tvCounter.getText().toString());
            ItemCartModel itemCartModel = new ItemCartModel(list.get(position).getId(), list.get(position).getTitle(), list.get(position).getPrice(), count, list.get(position).getImage());
            cartSingleton.addItem(itemCartModel);
            Fragment_Main fragment_main = (Fragment_Main) fragment;
            fragment_main.updateCartCount(cartSingleton.getItemCount());

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

                if (myHolder.binding.checkbox.isChecked())
                {
                    fragment_main.like_dislike(2,list.get(myHolder.getAdapterPosition()),"favourite",myHolder.getAdapterPosition());
                }else {
                    fragment_main.like_dislike(2,list.get(myHolder.getAdapterPosition()),"unfavourite",myHolder.getAdapterPosition());

                }
            }

        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        public OfferRowBinding binding;

        public MyHolder(@NonNull OfferRowBinding binding) {
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
