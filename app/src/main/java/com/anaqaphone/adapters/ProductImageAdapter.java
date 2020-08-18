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
import com.anaqaphone.databinding.BankRowBinding;
import com.anaqaphone.databinding.ImageRowBinding;
import com.anaqaphone.models.BankDataModel;
import com.anaqaphone.models.SingleProductDataModel;
import com.anaqaphone.tags.Tags;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ProductImageAdapter extends RecyclerView.Adapter<ProductImageAdapter.MyHolder> {

    private List<SingleProductDataModel.ProductsImages> productsImages;
    private Context context;

    public ProductImageAdapter(List<SingleProductDataModel.ProductsImages> productsImages, Context context) {
        this.productsImages = productsImages;
        this.context = context;


    }


    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ImageRowBinding imageRowBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.image_row, parent, false);
        return new MyHolder(imageRowBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyHolder holder, int position) {

        Picasso.get().load(Uri.parse(Tags.IMAGE_URL+productsImages.get(position).getFull_file())).into(holder.binding.image);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (context instanceof ImagesActivity) {
                    ImagesActivity imagesActivity = (ImagesActivity) context;
                    imagesActivity.showimage(holder.getLayoutPosition());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return productsImages.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        private ImageRowBinding binding;

        public MyHolder(ImageRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;


        }


    }
}
