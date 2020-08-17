package com.anaqaphone.adapters;


import android.content.Context;
import android.net.Uri;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.anaqaphone.R;
import com.anaqaphone.activities_fragments.activity_product_details.ProductDetailsActivity;
import com.anaqaphone.models.ProductDataModel;
import com.anaqaphone.models.SingleProductDataModel;
import com.anaqaphone.tags.Tags;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.List;


public class ProductDetialsSlidingImage_Adapter extends PagerAdapter {
    List<SingleProductDataModel.ProductsImages> IMAGES;
    private LayoutInflater inflater;
    Context context;

    public ProductDetialsSlidingImage_Adapter(Context context, List<SingleProductDataModel.ProductsImages> IMAGES) {
        this.context = context;
        this.IMAGES = IMAGES;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return IMAGES.size();
    }

    @Override
    public Object instantiateItem(ViewGroup view, int position) {
        View imageLayout = inflater.inflate(R.layout.slider, view, false);

        assert imageLayout != null;
        final RoundedImageView imageView = imageLayout
                .findViewById(R.id.image);
        SingleProductDataModel.ProductsImages slider = IMAGES.get(position);
        Picasso.get().load(Uri.parse(Tags.IMAGE_URL + slider.getFull_file())).fit().into(imageView);
        imageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("l;lll",";llll");
                if (context instanceof ProductDetailsActivity) {
                    ProductDetailsActivity productDetailsActivity = (ProductDetailsActivity) context;
                    productDetailsActivity.show();
                }
            }
        });
        view.addView(imageLayout, 0);

        return imageLayout;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
    }

    @Override
    public Parcelable saveState() {
        return null;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }
}
