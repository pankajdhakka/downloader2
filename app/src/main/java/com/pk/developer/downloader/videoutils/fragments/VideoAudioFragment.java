package com.pk.developer.downloader.videoutils.fragments;


import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.appcompat.widget.AppCompatSpinner;
import androidx.fragment.app.Fragment;

import com.pk.developer.downloader.R;
import com.pk.developer.downloader.videoutils.MainActivity;
import com.pk.developer.downloader.videoutils.Utils.Constants;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;

/**
 * A simple {@link Fragment} subclass.
 */
public class VideoAudioFragment extends Fragment {

    public final String TAG = "VideoAudioFragment";
    public Activity activity;
    public int m_nAngle = 0;
    public int m_nSeekStart = 0, startTime = 0;
    public int m_nSeekEnd = 0, endTime = 0;
    RelativeLayout relativeVideoGif;
    ImageView ivPreview;
    int MAX_SEEK = 100;
    LinearLayout m_bottomBar;
    Button m_btnPlay;
    FloatingActionButton fabExpand, fabCollapse, fabDone;
    TextView m_txtPlayTime;
    TextView m_txtTotalTime;
    int m_nDuration = 0;
    VideoView m_video;
    boolean m_bShowVideoController;
    boolean isFirstTime = true;
    boolean m_running = false;
    boolean m_runningReverse = false;
    ProgressDialog mlDialog;
    int nProgress;
    String videoPath = "";
    AppCompatSpinner spinnerFormat, spinnerQuality;
    String[] formats = new String[]{"mp3", "wav"};
    String[] qualitys = new String[]{"64k", "128k", "192k", "256k", "320k"};


    public VideoAudioFragment() {

    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) activity).setTitle(R.string.video_to_audio);
        ((MainActivity) activity).setDrawerState(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_video_audio, container, false);

        activity = getActivity();

        if (getArguments() != null) {
            videoPath = getArguments().getString("video");
        }

        init(v);

        return v;
    }

    public void init(View view) {

        m_bottomBar = (LinearLayout) view.findViewById(R.id.bottom_bar);
        m_btnPlay = (Button) view.findViewById(R.id.tutorialplay);
        fabExpand = (FloatingActionButton) view.findViewById(R.id.fabExpand);
        fabCollapse = (FloatingActionButton) view.findViewById(R.id.fabCollapse);
        fabDone = (FloatingActionButton) view.findViewById(R.id.fabDone);
        m_txtPlayTime = (TextView) view.findViewById(R.id.txt_playtime);
        m_txtTotalTime = (TextView) view.findViewById(R.id.txt_totaltime);
        m_video = (VideoView) view.findViewById(R.id.videoView);
        relativeVideoGif = view.findViewById(R.id.relativeVideoGif);
        ivPreview = view.findViewById(R.id.ivPreview);
        spinnerFormat = view.findViewById(R.id.spinnerFormat);
        spinnerQuality = view.findViewById(R.id.spinnerQuality);

        setSpinnerFormat();
        setSpinnerQuality();

        ivPreview.setVisibility(View.GONE);

        m_btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playVideo();
                m_video.seekTo(m_nDuration * m_nSeekStart / MAX_SEEK);
            }
        });

        fabExpand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m_bottomBar.setVisibility(View.GONE);
                m_bShowVideoController = false;
            }
        });

        fabCollapse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m_bottomBar.setVisibility(View.VISIBLE);
                m_bShowVideoController = true;
            }
        });

        fabDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!videoPath.equals("")) {

                    completeVideo();
                    m_nAngle = 0;
                    m_runningReverse = true;

//                    m_btnBack.setEnabled(false);
//                    fabDone.setEnabled(false);
//                    m_btnPlay.setEnabled(false);
//                    fabExpand.setEnabled(false);

                    if (Build.VERSION.SDK_INT >= 17) {
                        MediaMetadataRetriever m = new MediaMetadataRetriever();
                        m.setDataSource(videoPath);
                        Bitmap thumbnail = m.getFrameAtTime();
                        String s = m.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION).trim();
                        m_nAngle = Integer.parseInt(s);
                        Log.e("Rotation", s);
                    }

                    if (endTime == 0) endTime = m_nDuration;
                    int start = startTime / 1000;
                    int total = (endTime - startTime) / 1000;

                    convertToAudio(start, total);
                }
            }
        });

        m_nSeekStart = 0;
        m_nSeekEnd = MAX_SEEK;
        prepareVideo();
    }

    private void setSpinnerFormat() {

        ArrayAdapter<String> aa = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_dropdown_item, formats);
        spinnerFormat.setAdapter(aa);
        spinnerFormat.setSelected(true);
        spinnerFormat.setSelection(0);
    }

    private void setSpinnerQuality() {
        ArrayAdapter<String> aa = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_dropdown_item, qualitys);
        spinnerQuality.setAdapter(aa);
        spinnerQuality.setSelected(true);
        spinnerQuality.setSelection(3);
    }

    void convertToAudio(int start, final int total) {



    }

    void playVideo() {
        if (m_video.isPlaying()) {
            m_video.pause();
            m_btnPlay.setBackgroundResource(R.drawable.ic_play);
        } else {
            m_video.start();
            m_btnPlay.setBackgroundResource(R.drawable.ic_pause);
        }
    }

    void prepareVideo() {
        if (m_video.isPlaying())
            m_video.seekTo(0);

        m_video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            public void onPrepared(MediaPlayer mp) {
                m_running = true;
                final int duration = m_video.getDuration();
                m_nDuration = mp.getDuration();
                m_txtTotalTime.setText(Constants.getFomattedTime(duration));
                new Thread(new Runnable() {
                    public void run() {
                        do {
                            m_txtPlayTime.post(new Runnable() {
                                public void run() {
                                    //duration -
                                    if (m_video.isPlaying() && m_video.getCurrentPosition() >= m_nDuration * m_nSeekEnd / MAX_SEEK)
                                        completeVideo();
                                    if (isFirstTime) {
                                        m_video.seekTo(0);
                                        isFirstTime = false;
                                    }
                                }
                            });
                            try {
                                Thread.sleep(30);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            if (!m_running) break;
                        }
                        while (m_video.getCurrentPosition() < duration);
                    }
                }).start();
            }
        });

        m_video.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                completeVideo();
            }
        });

        m_video.setVideoPath(videoPath);
        m_txtTotalTime.setText(Constants.getFomattedTime(m_video.getDuration()));

    }

    void completeVideo() {
        m_video.pause();
        m_btnPlay.setBackgroundResource(R.drawable.ic_play);
        m_bottomBar.setVisibility(View.VISIBLE);
        m_bShowVideoController = true;
    }

    public void finishedVideoConvet(String format) {

        int nSaveNumber = Constants.getIntValue(activity, Constants.TOTAL_COUNT_AUDIO);

        String strAudioOldName = Constants.AUDIO_FOLDER_PATH + String.format("/%s.%s", Constants.MY_AUDIO, format);
        String strAudioNewName = Constants.AUDIO_FOLDER_PATH + String.format("/%s%d.%s", Constants.MY_AUDIO, nSaveNumber, format);
        File file = new File(strAudioOldName);

        if (file.exists())
            file.renameTo(new File(strAudioNewName));

        Constants.putIntValue(activity, Constants.SAVE_AUDIO + nSaveNumber, 1);
        nSaveNumber++;
        Constants.putIntValue(activity, Constants.TOTAL_COUNT_AUDIO, nSaveNumber);

        activity.onBackPressed();
        Constants.SELECTED = Constants.GALLERY_FRAGMENT;
        ((MainActivity) activity).loadFragment(Constants.TYPE_AUDIO);
    }
}