package com.pk.developer.downloader.activities;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.pk.developer.downloader.BuildConfig;
import com.pk.developer.downloader.R;
import com.google.android.material.snackbar.Snackbar;
import com.universalvideoview.UniversalMediaController;
import com.universalvideoview.UniversalVideoView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class SavedFullVideoActivity extends AppCompatActivity {

    String urls;
    View mBottomLayout;
    View mVideoLayout;
    UniversalVideoView mVideoView;
    UniversalMediaController mMediaController;
    String from;
    LinearLayout shareapp,saveimages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_full_video);
        urls=getIntent().getStringExtra("urls");
        mVideoView = (UniversalVideoView) findViewById(R.id.video_view);
        mMediaController = (UniversalMediaController) findViewById(R.id.media_controller);
        mVideoView.setMediaController(mMediaController);
        mVideoView.setVideoURI(Uri.parse(urls));
        Intent intent = getIntent();
        final ImageButton story_video=findViewById(R.id.story_video);
        shareapp=findViewById(R.id.shareapp);
        saveimages=findViewById(R.id.saveimages);
        if (intent.hasExtra("from")) {
            from = intent.getStringExtra("from");
            if(from.equals("previews")){
                saveimages.setVisibility(View.GONE);
            }
        } else {
        }
        mVideoView.setVideoViewCallback(new UniversalVideoView.VideoViewCallback() {
            @Override
            public void onScaleChange(boolean isFullscreen) {

            }

            @Override
            public void onPause(MediaPlayer mediaPlayer) { // Video pause
            }

            @Override
            public void onStart(MediaPlayer mediaPlayer) { // Video start/resume to play
            }

            @Override
            public void onBufferingStart(MediaPlayer mediaPlayer) {// steam start loading
            }

            @Override
            public void onBufferingEnd(MediaPlayer mediaPlayer) {// steam end loading
            }

        });

        story_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                story_video.setVisibility(View.GONE);
                mVideoView.start();
            }
        });

        shareapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("video/*");
                sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                Uri photoURI = FileProvider.getUriForFile(SavedFullVideoActivity.this,
                        BuildConfig.APPLICATION_ID + ".provider",
                        new File(urls));
                sharingIntent.putExtra(Intent.EXTRA_STREAM,photoURI);
                startActivity(Intent.createChooser(sharingIntent, "Share to"));

            }
        });

        saveimages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File sdCard = Environment.getExternalStorageDirectory();
                File dir1 = Environment.getExternalStorageDirectory();
                String path = dir1.getAbsolutePath();

                File dir = new File( path+"/"+Environment.DIRECTORY_PICTURES  + "/VideoDownloader");
                dir.mkdirs();
                String fileName = String.format("%d.mp4", System.currentTimeMillis());
                File outFile = new File(dir, fileName);
                if (!outFile.getParentFile().exists())
                    outFile.getParentFile().mkdirs();

                if (!outFile.exists()) {
                    try {
                        outFile.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                FileChannel source = null;
                FileChannel destination = null;
                try {
                    source = new FileInputStream(new File(urls)).getChannel();
                    destination = new FileOutputStream(outFile).getChannel();
                    destination.transferFrom(source, 0, source.size());
                    Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    Uri contentUri = Uri.fromFile(outFile);
                    mediaScanIntent.setData(contentUri);
                    sendBroadcast(mediaScanIntent);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (source != null) {
                        try {
                            source.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (destination != null) {
                        try {
                            destination.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                Snackbar snackbar;
                snackbar = Snackbar.make(view, "Video Saved Successfully", Snackbar.LENGTH_LONG);
                View snackBarView = snackbar.getView();
                snackBarView.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                TextView textView = (TextView) snackBarView.findViewById(R.id.snackbar_text);
                textView.setTextColor(getResources().getColor(R.color.white));
                textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_done_black_24dp, 0);
                snackbar.show();
            }
        });
    }
}
