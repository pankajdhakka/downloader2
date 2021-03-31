package com.pk.developer.downloader.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.pk.developer.downloader.BuildConfig;
import com.pk.developer.downloader.R;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class SavedFullImageActivity extends AppCompatActivity {

    String photouri;
    private PhotoView imageView;
    String from;
    LinearLayout saveimages;
    LinearLayout shareapp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_full_image);
        photouri=getIntent().getStringExtra("url");
        Intent intent = getIntent();
        imageView = findViewById(R.id.story_imageview);
        imageView.setImageURI(Uri.parse(photouri));
        saveimages=findViewById(R.id.saveimages);
        shareapp=findViewById(R.id.shareapp);
        if (intent.hasExtra("from")) {
            from = intent.getStringExtra("from");
            if(from.equals("previews")){
                saveimages.setVisibility(View.GONE);
            }

        } else {
        }

        saveimages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BitmapDrawable draw = (BitmapDrawable) imageView.getDrawable();
                Bitmap bitmap = draw.getBitmap();
                FileOutputStream outStream = null;
                File sdCard = Environment.getExternalStorageDirectory();
                File dir1 = Environment.getExternalStorageDirectory();
                String path = dir1.getAbsolutePath();

                File dir = new File(  path+"/"+Environment.DIRECTORY_PICTURES + "/VideoDownloader");
                dir.mkdirs();
                String fileName = String.format("%d.jpg", System.currentTimeMillis());
                File outFile = new File(dir, fileName);
                try {
                    outStream = new FileOutputStream(outFile);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
                    outStream.flush();
                    outStream.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Snackbar snackbar;
                snackbar = Snackbar.make(view, "Picture Saved Successfully", Snackbar.LENGTH_LONG);
                View snackBarView = snackbar.getView();
                snackBarView.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                TextView textView = (TextView) snackBarView.findViewById(R.id.snackbar_text);
                textView.setTextColor(getResources().getColor(R.color.white));
                textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_done_black_24dp, 0);
                snackbar.show();
            }
        });

        shareapp.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("image/*");
                sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                Uri photoURI = FileProvider.getUriForFile(SavedFullImageActivity.this,
                        BuildConfig.APPLICATION_ID + ".provider",
                        new File(photouri));
                sharingIntent.putExtra(Intent.EXTRA_STREAM,photoURI);
                startActivity(Intent.createChooser(sharingIntent, "Share Image to"));
            }
        });
    }
}

