package com.pk.developer.downloader.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.viewpager.widget.PagerAdapter;

import com.pk.developer.downloader.R;
import com.pk.developer.downloader.models.Image;

import java.util.List;

public class ImageAdapter extends PagerAdapter {



    private List<Image> images;
    private LayoutInflater layoutInflater;
    private Context context;

    public ImageAdapter(List<Image> images, Context context){
        this.images = images;
        this.context = context;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        int resImage1 = R.drawable.image1;
        int resImage2 = R.drawable.image2;
        layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.image_sliding_item, container, false);
        ImageView imageView = view.findViewById(R.id.imageSlider);
        imageView.setImageResource(images.get(position).getImage());

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uri="";
                if (images.get(position).getImage() == resImage1 ){
                    uri = "https://is.gd/winneerr";
                }
                else if (images.get(position).getImage() == resImage2) {
                    uri = "https://828.win.qureka.com";
                }
                else {
                    Toast.makeText(context, "Network Issue",Toast.LENGTH_LONG).show();
                }

                CustomTabsIntent.Builder customTabIntent = new CustomTabsIntent.Builder();
                customTabIntent.setToolbarColor(Color.parseColor("#2da639"));
                CustomTabsIntent customTab = customTabIntent.build();
                customTab.launchUrl(context,Uri.parse(uri));


            }
        });
        container.addView(view);
        return view;

    }



    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

}

