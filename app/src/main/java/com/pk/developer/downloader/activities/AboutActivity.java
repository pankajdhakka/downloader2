package com.pk.developer.downloader.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.pk.developer.downloader.BuildConfig;
import com.pk.developer.downloader.R;

import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

public class AboutActivity extends Fragment {

    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.aboutactivity, container, false);
        Element versionElement = new Element();
        versionElement.setTitle("Version 1.0");
        Element adsElement = new Element();
        adsElement.setTitle("Advertise with us");
        View aboutPage = new AboutPage(getContext())
                .setImage(R.drawable.about_appicon)
                .setDescription("Essential application in every phone that makes Social media downloads easier")
                .addItem(new Element().setTitle("Version: " + BuildConfig.VERSION_NAME))
                .addGroup("Connect with us")
                .addEmail("")
                .addPlayStore("")
                .create();
        viewGroup.addView(aboutPage);
        aboutPage.setBackgroundColor(getResources().getColor(R.color.colorBackground));
        return viewGroup;
    }
}
