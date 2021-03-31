package com.pk.developer.downloader.WaStatus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.pk.developer.downloader.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.comparator.LastModifiedFileComparator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PicDetail extends AppCompatActivity {

    ImageView backArrow;
    Uri iri2;
    List<File> imagesList;
    CustomSliderAdapter myCustomPagerAdapter;
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pic_detail);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        imagesList = new ArrayList<>();
        imagesList = fetchImages();
        int pos = 0;
        String posString = getIntent().getStringExtra("pos").toString();
        try {
            pos = Integer.parseInt(posString);
        } catch (Exception e) {
            Log.d("Error", "not parceable Int");
        }
        BottomNavigationView bottomNavigationView = findViewById(R.id.picNavigation);
        backArrow = findViewById(R.id.backArrow);
        DisplayMetrics display = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(display);
        myCustomPagerAdapter = new CustomSliderAdapter(this, imagesList);
        viewPager.setAdapter(myCustomPagerAdapter);
        viewPager.setCurrentItem(pos);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.SetStatus:
                        setStatus();
                        break;
                    case R.id.Download:
                        downloadImage();
                        break;
                    case R.id.Share:
                        shareImage();
                        break;
                }
                return true;
            }
        });
    }

    private void setStatus() {
        Uri imageUri = Uri.parse(imagesList.get(viewPager.getCurrentItem()).getAbsolutePath());
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        //Target whatsapp:
        shareIntent.setPackage("com.whatsapp");
        //Add text and then Image URI
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
        shareIntent.setType("image/jpeg");
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try {
            startActivity(shareIntent);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "Whatsapp have not been installed.", Toast.LENGTH_SHORT).show();
        }
    }

    private void shareImage() {
        iri2 = Uri.parse(imagesList.get(viewPager.getCurrentItem()).getAbsolutePath());
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("image/jpeg");
        i.putExtra(Intent.EXTRA_STREAM, iri2);
        startActivity(Intent.createChooser(i, "Share Image Using"));
    }

    private void downloadImage() {
        File f1, f2;
        f1 = imagesList.get(viewPager.getCurrentItem());
        String fname = f1.getName();
        f2 = new File(Environment.getExternalStorageDirectory() + "/WASaver/Photo/");
        f2.mkdirs();
        try {
            FileUtils.copyFileToDirectory(f1, f2);
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            values.put(MediaStore.MediaColumns.DATA, f2.toString() + "/" + fname);
            getApplicationContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_SHORT).show();
        }
    }

    List fetchImages() {
        List<File> muList = new ArrayList<File>();
        try {
            String path = Environment.getExternalStorageDirectory().toString() + "/WhatsApp/Media/.Statuses";
            Log.d("test", "onStart: " + path);
            File dir = new File(path);
            File[] files = dir.listFiles();
            Arrays.sort(files, LastModifiedFileComparator.LASTMODIFIED_REVERSE);
            for (int i = 0; i < files.length; i++) {
                if (files[i].getName().endsWith(".jpg") || files[i].getName().endsWith(".png")) {
                    muList.add(files[i]);
                }
            }
        } catch (Exception ex) {
            Toast.makeText(this, ex.getMessage().toString(), Toast.LENGTH_LONG).show();
        }
        return muList;
    }

}

