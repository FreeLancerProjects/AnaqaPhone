package com.anaqaphone.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.anaqaphone.R;
import com.anaqaphone.activities_fragments.activity_images.ImagesActivity;
import com.anaqaphone.activities_fragments.activity_product_details.ProductDetailsActivity;
import com.anaqaphone.databinding.ImageRowBinding;
import com.anaqaphone.databinding.MemoryRowBinding;
import com.anaqaphone.models.SingleProductDataModel;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ProductSizesAdapter extends RecyclerView.Adapter<ProductSizesAdapter.MyHolder> {

    private List<SingleProductDataModel.Sizes> sizesList;
    private Context context;
    private int i = 0;

    public ProductSizesAdapter(List<SingleProductDataModel.Sizes> sizesList, Context context) {
        this.sizesList = sizesList;
        this.context = context;


    }


    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MemoryRowBinding imageRowBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.memory_row, parent, false);
        return new MyHolder(imageRowBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyHolder holder, int position) {

        holder.binding.setModel(sizesList.get(position).getSize());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                i = holder.getLayoutPosition();
                notifyDataSetChanged();
            }
        });
        if (i == position) {
            if (context instanceof ProductDetailsActivity) {
                ProductDetailsActivity productDetailsActivity = (ProductDetailsActivity) context;
                productDetailsActivity.setsizeid(sizesList.get(i));
            }
            holder.binding.cons.setBackground(context.getResources().getDrawable(R.drawable.small_rounded_btn_memory_selected));
        } else {
            holder.binding.cons.setBackground(context.getResources().getDrawable(R.drawable.small_rounded_memory_unselected));

        }
    }

    @Override
    public int getItemCount() {
        return sizesList.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        private MemoryRowBinding binding;

        public MyHolder(MemoryRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;


        }


    }
}
