package com.anaqaphone.adapters;


import android.content.Context;
import android.net.Uri;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.anaqaphone.R;
import com.anaqaphone.models.OfferModel;
import com.anaqaphone.models.Slider_Model;
import com.anaqaphone.tags.Tags;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.List;


public class ProductDetialsSlidingImage_Adapter extends PagerAdapter {
    List<OfferModel.ProductsImages> IMAGES;
    private LayoutInflater inflater;
    Context context;
    public ProductDetialsSlidingImage_Adapter(Context context, List<OfferModel.ProductsImages> IMAGES) {
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
        OfferModel.ProductsImages slider = IMAGES.get(position);
        Picasso.get().load(Uri.parse(Tags.IMAGE_URL + slider.getImage())).fit().into(imageView);
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
