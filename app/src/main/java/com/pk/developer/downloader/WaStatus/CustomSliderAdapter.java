package com.pk.developer.downloader.WaStatus;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.viewpager.widget.PagerAdapter;

import com.pk.developer.downloader.R;
import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;

import java.io.File;
import java.util.List;

public class CustomSliderAdapter extends PagerAdapter {

    Context context;
    File images[];
    List<File> list;

    LayoutInflater layoutInflater;


    public CustomSliderAdapter(Context context, List<File> list) {
        this.context = context;
        this.list = list;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((LinearLayout) object);
    }




    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View itemView = layoutInflater.inflate(R.layout.item, container, false);
        PhotoView imageView =  itemView.findViewById(R.id.picDetImg);
//        Picasso.with(context).load(list.get(position)).networkPolicy(NetworkPolicy.OFFLINE).into(imageView);
        Glide.with(context).load(list.get(position)).error(R.drawable.error).into(imageView);
        // imageView.setImageResource(images[position]);
        container.addView(itemView);
        //listening to image click
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toast.makeText(context, "you clicked image " + (position + 1), Toast.LENGTH_LONG).show();
            }
        });

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }



}