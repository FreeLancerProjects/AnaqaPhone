package com.anaqaphone.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.anaqaphone.R;
import com.anaqaphone.activities_fragments.activity_home.fragments.Fragment_Main;
import com.anaqaphone.activities_fragments.activity_home.fragments.Fragment_Offer;
import com.anaqaphone.databinding.OfferListRowBinding;
import com.anaqaphone.databinding.OfferRowBinding;
import com.anaqaphone.models.ItemCartModel;
import com.anaqaphone.models.ProductDataModel;
import com.anaqaphone.models.SingleProductDataModel;
import com.anaqaphone.singleton.CartSingleton;

import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;

public class OffersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final String lang;
    private List<SingleProductDataModel> list;
    private Context context;
    private LayoutInflater inflater;
    private Fragment fragment;
    private CartSingleton cartSingleton;
    private int type;

    public OffersAdapter(List<SingleProductDataModel> list, Context context, Fragment fragment, int type) {
        this.list = list;
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.fragment = fragment;
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());

        cartSingleton = CartSingleton.newInstance();
        this.type = type;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (type == 1) {
            OfferRowBinding binding = DataBindingUtil.inflate(inflater, R.layout.offer_row, parent, false);
            return new MyHolder(binding);
        } else {
            OfferListRowBinding binding = DataBindingUtil.inflate(inflater, R.layout.offer_list_row, parent, false);
            return new MyHolderList(binding);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MyHolder) {
            MyHolder myHolder = (MyHolder) holder;
            myHolder.binding.tvOldprice.setPaintFlags(myHolder.binding.tvOldprice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            myHolder.binding.setModel(list.get(position));
            myHolder.binding.setLang(lang);
            if(list.get(position).getUser_like()!=null){
              ((MyHolder) holder).binding.checkbox.setChecked(true);
            }

            myHolder.binding.llAddToCart.setOnClickListener(v -> {
                int count = Integer.parseInt(myHolder.binding.tvCounter.getText().toString());
                ItemCartModel itemCartModel = new ItemCartModel(list.get(position).getId(), list.get(position).getTitle(), list.get(position).getPrice(), count, list.get(position).getImage());
                cartSingleton.addItem(itemCartModel);
                if (fragment instanceof Fragment_Main) {
                    Fragment_Main fragment_main = (Fragment_Main) fragment;
                    fragment_main.updateCartCount(cartSingleton.getItemCount());
                } else if (fragment instanceof Fragment_Offer) {
                    Fragment_Offer fragment_offer = (Fragment_Offer) fragment;
                    fragment_offer.updateCartCount(cartSingleton.getItemCount());
                }
                Toast.makeText(context, R.string.added_suc, Toast.LENGTH_SHORT).show();
            });
            myHolder.itemView.setOnClickListener(view -> {
                if (fragment instanceof Fragment_Main) {

                    Fragment_Main fragment_main = (Fragment_Main) fragment;
                    fragment_main.setItemDataOffers(list.get(myHolder.getAdapterPosition()));
                } else if (fragment instanceof Fragment_Offer) {
                    Fragment_Offer fragment_offer = (Fragment_Offer) fragment;
                    fragment_offer.setItemDataOffers(list.get(myHolder.getAdapterPosition()));
                }

            });

            myHolder.binding.checkbox.setOnClickListener(v -> {
                if (fragment instanceof Fragment_Main) {

                    Fragment_Main fragment_main = (Fragment_Main) fragment;

                    if (myHolder.binding.checkbox.isChecked()) {
                        fragment_main.like_dislike( list.get(myHolder.getAdapterPosition()), myHolder.getAdapterPosition(),0);
                    } else {
                        fragment_main.like_dislike( list.get(myHolder.getAdapterPosition()), myHolder.getAdapterPosition(),0);

                    }
                } else if (fragment instanceof Fragment_Offer) {
                    Fragment_Offer fragment_offer = (Fragment_Offer) fragment;
                    if (myHolder.binding.checkbox.isChecked()) {
                        fragment_offer.like_dislike(list.get(myHolder.getAdapterPosition()), myHolder.getAdapterPosition());
                    } else {
                        fragment_offer.like_dislike( list.get(myHolder.getAdapterPosition()),  myHolder.getAdapterPosition());

                    }
                }

            });
        } else {

            MyHolderList myHolder = (MyHolderList) holder;
            myHolder.binding.tvOldprice.setPaintFlags(myHolder.binding.tvOldprice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            Log.e("llldll", list.get(position).getHave_offer());
            myHolder.binding.setModel(list.get(position));
            if(list.get(position).getUser_like()!=null){
                ((MyHolder) holder).binding.checkbox.setChecked(true);
            }
            myHolder.binding.llAddToCart.setOnClickListener(v -> {
                int count = Integer.parseInt(myHolder.binding.tvCounter.getText().toString());
                ItemCartModel itemCartModel = new ItemCartModel(list.get(position).getId(), list.get(position).getTitle(), list.get(position).getPrice(), count, list.get(position).getImage());
                cartSingleton.addItem(itemCartModel);
                if (fragment instanceof Fragment_Main) {
                    Fragment_Main fragment_main = (Fragment_Main) fragment;
                    fragment_main.updateCartCount(cartSingleton.getItemCount());
                } else if (fragment instanceof Fragment_Offer) {
                    Fragment_Offer fragment_offer = (Fragment_Offer) fragment;
                    fragment_offer.updateCartCount(cartSingleton.getItemCount());
                }
                Toast.makeText(context, R.string.added_suc, Toast.LENGTH_SHORT).show();
            });
            myHolder.itemView.setOnClickListener(view -> {
                if (fragment instanceof Fragment_Main) {

                    Fragment_Main fragment_main = (Fragment_Main) fragment;
                    fragment_main.setItemDataOffers(list.get(myHolder.getAdapterPosition()));
                } else if (fragment instanceof Fragment_Offer) {
                    Fragment_Offer fragment_offer = (Fragment_Offer) fragment;
                    fragment_offer.setItemDataOffers(list.get(myHolder.getAdapterPosition()));
                }

            });

            myHolder.binding.checkbox.setOnClickListener(v -> {
                if (fragment instanceof Fragment_Main) {

                    Fragment_Main fragment_main = (Fragment_Main) fragment;

                    if (myHolder.binding.checkbox.isChecked()) {
                        fragment_main.like_dislike( list.get(myHolder.getAdapterPosition()),  myHolder.getAdapterPosition(),0);
                    } else {
                        fragment_main.like_dislike( list.get(myHolder.getAdapterPosition()), myHolder.getAdapterPosition(),0);

                    }
                } else if (fragment instanceof Fragment_Offer) {
                    Fragment_Offer fragment_offer = (Fragment_Offer) fragment;
                    if (myHolder.binding.checkbox.isChecked()) {
                        fragment_offer.like_dislike(list.get(myHolder.getAdapterPosition()), myHolder.getAdapterPosition());
                    } else {
                        fragment_offer.like_dislike( list.get(myHolder.getAdapterPosition()), myHolder.getAdapterPosition());

                    }
                }

            });
        }
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

    public class MyHolderList extends RecyclerView.ViewHolder {
        public OfferListRowBinding binding;

        public MyHolderList(@NonNull OfferListRowBinding binding) {
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

    @Override
    public int getItemViewType(int position) {

        if (type == 1) {

            return type;
        } else {
            return type;
        }
    }

    public void setType(int type) {
        this.type = type;
    }
}
