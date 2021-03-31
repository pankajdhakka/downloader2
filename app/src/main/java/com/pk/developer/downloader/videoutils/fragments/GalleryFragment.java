package com.pk.developer.downloader.videoutils.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pk.developer.downloader.R;
import com.pk.developer.downloader.videoutils.Adapters.GalleryAudioAdapter;
import com.pk.developer.downloader.videoutils.Adapters.GalleryImageAdapter;
import com.pk.developer.downloader.videoutils.Adapters.GalleryVideoAdapter;
import com.pk.developer.downloader.videoutils.MainActivity;
import com.pk.developer.downloader.videoutils.Pojo.VideoModel;
import com.pk.developer.downloader.videoutils.Utils.Constants;
import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class GalleryFragment extends Fragment {

    private final String TAG = "GalleryFragment";
    RecyclerView rclGif, rclImages, rclAudios, rclVideos;
    GalleryImageAdapter mGifGalleryImageAdapter, mJpgGalleryImageAdapter;
    GalleryAudioAdapter mGalleryAudioAdapter;
    GalleryVideoAdapter mGalleryVideoAdapter;
    ArrayList<VideoModel> videoModels;
    ArrayList<Integer> gifList, imageList, audioList;

    TextView txtNoData;
    FrameLayout frameLayout;
    ImageView ivPreview;
    FloatingActionButton fabCollapse;
    BottomNavigationView bottamView;
    Activity activity;
    boolean showProgress;
    ProgressDialog progressDialog;
    AdView bannerAdView;
    InterstitialAd mInterstitialAd;

    public GalleryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        if (bannerAdView != null) {
            bannerAdView.resume();
        }
        ((MainActivity) activity).setTitle(R.string.gallery);
        ((MainActivity) activity).setDrawerState(false);
    }

    @Override
    public void onPause() {
        if (bannerAdView != null) {
            bannerAdView.pause();
        }
        super.onPause();
    }

    @Override
    public void onDestroy() {
        if (bannerAdView != null) {
            bannerAdView.destroy();
        }
        super.onDestroy();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_gallery, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView(view);
        ((MainActivity) activity).setDrawerState(false);

        gifList = Constants.getGifList(activity);
        mGifGalleryImageAdapter = new GalleryImageAdapter(this, gifList, "gif");
        rclGif.setAdapter(mGifGalleryImageAdapter);

        imageList = Constants.getImageList(activity);
        mJpgGalleryImageAdapter = new GalleryImageAdapter(this, imageList, "jpg");
        rclImages.setAdapter(mJpgGalleryImageAdapter);

        audioList = Constants.getAudioList(activity);
        mGalleryAudioAdapter = new GalleryAudioAdapter(this, audioList);
        rclAudios.setAdapter(mGalleryAudioAdapter);

        videoModels = new ArrayList<>();
        mGalleryVideoAdapter = new GalleryVideoAdapter(this, videoModels);
        rclVideos.setAdapter(mGalleryVideoAdapter);
        setUpVideoList();

        fabCollapse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                frameLayout.setVisibility((View.GONE));
            }
        });

        bottamView.setSelectedItemId(R.id.action_gif);
        bottamView.setSelected(true);
        bottamView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                frameLayout.setVisibility(View.GONE);
                switch (item.getItemId()) {
                    case R.id.action_gif:
                        if (Constants.getGifList(activity).isEmpty())
                            setNoDataVisibility();
                        else {
                            rclGif.setVisibility(View.VISIBLE);
                            rclImages.setVisibility(View.GONE);
                            rclAudios.setVisibility(View.GONE);
                            rclVideos.setVisibility(View.GONE);
                            txtNoData.setVisibility(View.GONE);
                        }
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();
                        return true;
                    case R.id.action_jpg:
                        if (Constants.getImageList(activity).isEmpty())
                            setNoDataVisibility();
                        else {
                            rclGif.setVisibility(View.GONE);
                            rclImages.setVisibility(View.VISIBLE);
                            rclAudios.setVisibility(View.GONE);
                            rclVideos.setVisibility(View.GONE);
                            txtNoData.setVisibility(View.GONE);
                        }
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();
                        return true;
                    case R.id.action_audio:
                        if (Constants.getAudioList(activity).isEmpty())
                            setNoDataVisibility();
                        else {
                            rclGif.setVisibility(View.GONE);
                            rclImages.setVisibility(View.GONE);
                            rclAudios.setVisibility(View.VISIBLE);
                            rclVideos.setVisibility(View.GONE);
                            txtNoData.setVisibility(View.GONE);
                        }
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();
                        return true;
                    case R.id.action_video:
                        if (Constants.getVideoList(activity).isEmpty())
                            setNoDataVisibility();
                        else {
                            rclGif.setVisibility(View.GONE);
                            rclImages.setVisibility(View.GONE);
                            rclAudios.setVisibility(View.GONE);
                            rclVideos.setVisibility(View.VISIBLE);
                            txtNoData.setVisibility(View.GONE);
                            if (showProgress) {
                                progressDialog.show();
                            } else {
                                if (progressDialog.isShowing())
                                    progressDialog.dismiss();
                            }
                        }
                        return true;
                }
                return false;
            }
        });

        if (getTag() != null) {
            switch (getTag()) {
                case Constants.TYPE_GIF:
                    bottamView.setSelectedItemId(R.id.action_gif);
                    break;
                case Constants.TYPE_IMAGE:
                    bottamView.setSelectedItemId(R.id.action_jpg);
                    break;
                case Constants.TYPE_AUDIO:
                    bottamView.setSelectedItemId(R.id.action_audio);
                    break;
                case Constants.TYPE_VIDEO:
                    bottamView.setSelectedItemId(R.id.action_video);
                    break;
                default:
                    if (Constants.getGifList(activity).isEmpty())
                        setNoDataVisibility();
                    break;
            }
        }

        if (Constants.AD_COUNT >= 3) {

            AdRequest adRequest1 = new AdRequest.Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    // Check the LogCat to get your test device ID
                    .addTestDevice("5F338128595663F734E36512D2DF7D2F")
                    .build();

            // Load ads into Interstitial Ads
            mInterstitialAd.loadAd(adRequest1);

            mInterstitialAd.setAdListener(new AdListener(){
                @Override
                public void onAdClosed() {
                    super.onAdClosed();
                }

                @Override
                public void onAdFailedToLoad(int i) {
                    super.onAdFailedToLoad(i);
                }

                @Override
                public void onAdOpened() {
                    super.onAdOpened();
                }

                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    mInterstitialAd.show();
                    Constants.AD_COUNT = 1;
                }
            });

        } else {
            Constants.AD_COUNT++;
        }

        AdRequest adRequest2 = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                // Check the LogCat to get your test device ID
                .addTestDevice("5F338128595663F734E36512D2DF7D2F")
                .build();

        bannerAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                bannerAdView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAdClosed() {
                bannerAdView.setVisibility(View.GONE);
                Log.d(TAG, "onAdClosed: ");
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                bannerAdView.setVisibility(View.GONE);
                Log.d(TAG, "onAdFailedToLoad: errorCode: "+errorCode);
            }

            @Override
            public void onAdLeftApplication() {
                Log.d(TAG, "onAdLeftApplication: ");
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
            }
        });

        bannerAdView.loadAd(adRequest2);
//        getVideoList();
    }

    private void setUpVideoList() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                showProgress = true;
                ArrayList<Integer> indexList = Constants.getVideoList(activity);
                for (int i = 0; i < indexList.size(); i++) {
                    String strPath = Constants.VIDEO_FOLDER_PATH + String.format("/%s%d.mp4", Constants.MY_VIDEO, indexList.get(i));
                    File file = new File(strPath);
                    if (file.exists()) {
                        Bitmap bMap = ThumbnailUtils
                                .createVideoThumbnail(file.getAbsolutePath(), MediaStore.Video.Thumbnails.MICRO_KIND);
                        VideoModel videoModel = new VideoModel();
                        videoModel.setIndex(indexList.get(i));
                        videoModel.setStr_path(strPath);
                        videoModel.setStr_thumb(bMap);
                        videoModels.add(videoModel);
                    }
                }
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showProgress = false;
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();

                        mGalleryVideoAdapter.notifyDataSetChanged();
                    }
                });
            }
        }).start();

    }

    private void setNoDataVisibility() {

        txtNoData.setVisibility(View.VISIBLE);
        rclGif.setVisibility(View.GONE);
        rclImages.setVisibility(View.GONE);
        rclAudios.setVisibility(View.GONE);
        rclVideos.setVisibility(View.GONE);
    }

    public void view(int nIndex, String type) {
        if (type.equals("gif")) {
            String gifPath = Constants.GIF_FOLDER_PATH + String.format("/%s%d.gif", Constants.MY_GIF, nIndex);
            Glide.with(this).asGif().load(new File(gifPath)).into(ivPreview);
        } else {
            String jpgPath = Constants.IMAGE_FOLDER_PATH + String.format("/%s%d.jpg", Constants.MY_IMAGE, nIndex);
            Glide.with(this).asBitmap().load(new File(jpgPath)).into(ivPreview);
        }
        frameLayout.setVisibility(View.VISIBLE);
    }

    public void share(int nIndex, String type) {

        String sharePath;
        Intent shareIntent;
        Uri shareUri;

        switch (type) {
            case "gif":
                sharePath = Constants.GIF_FOLDER_PATH + String.format("/%s%d.gif", Constants.MY_GIF, nIndex);
                shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("image/gif");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "GIF");
                shareIntent.putExtra(Intent.EXTRA_TITLE, "GIF");
                shareUri = Uri.parse("file://" + sharePath);
                shareIntent.putExtra(Intent.EXTRA_STREAM, shareUri);
                startActivity(Intent.createChooser(shareIntent, "Share GIF"));
                break;
            case "jpg":
                sharePath = Constants.IMAGE_FOLDER_PATH + String.format("/%s%d.jpg", Constants.MY_IMAGE, nIndex);
                shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("image/jpg");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Image");
                shareIntent.putExtra(Intent.EXTRA_TITLE, "Image");
                shareUri = Uri.parse("file://" + sharePath);
                shareIntent.putExtra(Intent.EXTRA_STREAM, shareUri);
                startActivity(Intent.createChooser(shareIntent, "Share Image"));
                break;
            case "wav":
                sharePath = Constants.AUDIO_FOLDER_PATH + String.format("/%s%d.wav", Constants.MY_AUDIO, nIndex);
                shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("audio/wav");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Audio");
                shareIntent.putExtra(Intent.EXTRA_TITLE, "Audio");
                shareUri = Uri.parse("file://" + sharePath);
                shareIntent.putExtra(Intent.EXTRA_STREAM, shareUri);
                startActivity(Intent.createChooser(shareIntent, "Share Audio"));
                break;
            case "mp3":
                sharePath = Constants.AUDIO_FOLDER_PATH + String.format("/%s%d.mp3", Constants.MY_AUDIO, nIndex);
                shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("audio/mp3");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Audio");
                shareIntent.putExtra(Intent.EXTRA_TITLE, "Audio");
                shareUri = Uri.parse("file://" + sharePath);
                shareIntent.putExtra(Intent.EXTRA_STREAM, shareUri);
                startActivity(Intent.createChooser(shareIntent, "Share Audio"));
                break;
            case "mp4":
                sharePath = Constants.VIDEO_FOLDER_PATH + String.format("/%s%d.mp4", Constants.MY_VIDEO, nIndex);
                shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("audio/mp4");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Video");
                shareIntent.putExtra(Intent.EXTRA_TITLE, "Video");
                shareUri = Uri.parse("file://" + sharePath);
                shareIntent.putExtra(Intent.EXTRA_STREAM, shareUri);
                startActivity(Intent.createChooser(shareIntent, "Share Video"));
                break;
        }
    }

    public void remove(int nIndex, String type) {

        final int nSelIndex = nIndex;
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(R.string.confirm);

        switch (type) {
            case "gif":
                builder.setMessage("Do you want to remove this gif file?");
                builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing but close the dialog
                        Constants.removeGif(activity, gifList.get(nSelIndex));
                        gifList.remove(nSelIndex);
                        mGifGalleryImageAdapter.notifyItemRemoved(nSelIndex);
                        dialog.dismiss();
                    }
                });
                break;
            case "jpg":
                builder.setMessage("Do you want to remove this jpg file?");
                builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing but close the dialog
                        Constants.removeImage(activity, imageList.get(nSelIndex));
                        imageList.remove(nSelIndex);
                        mJpgGalleryImageAdapter.notifyItemRemoved(nSelIndex);
                        dialog.dismiss();
                    }
                });
                break;
            case "mp4":
                builder.setMessage("Do you want to remove this video file?");
                builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing but close the dialog
                        Constants.removeVideo(activity, videoModels.get(nSelIndex).getIndex());
                        videoModels.remove(nSelIndex);
                        mGalleryVideoAdapter.notifyItemRemoved(nSelIndex);
                        dialog.dismiss();
                    }
                });
                break;
            default:
                builder.setMessage("Do you want to remove this audio file?");
                builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing but close the dialog
                        Constants.removeAudio(activity, audioList.get(nSelIndex));
                        audioList.remove(nSelIndex);
                        mGalleryAudioAdapter.notifyItemRemoved(nSelIndex);
                        dialog.dismiss();
                    }
                });
                break;
        }

        builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    public void initView(View view) {

        activity = getActivity();

        rclGif = (RecyclerView) view.findViewById(R.id.rclGif);
        rclGif.setLayoutManager(new GridLayoutManager(activity, 2));
        rclGif.setItemAnimator(new DefaultItemAnimator());

        rclImages = (RecyclerView) view.findViewById(R.id.rclJpg);
        rclImages.setLayoutManager(new GridLayoutManager(activity, 2));
        rclImages.setItemAnimator(new DefaultItemAnimator());

        rclAudios = (RecyclerView) view.findViewById(R.id.rclAudio);
        rclAudios.setLayoutManager(new LinearLayoutManager(activity));
        rclAudios.setItemAnimator(new DefaultItemAnimator());

        rclVideos = (RecyclerView) view.findViewById(R.id.rclVideo);
        rclVideos.setLayoutManager(new GridLayoutManager(activity, 2));
        rclVideos.setItemAnimator(new DefaultItemAnimator());

        txtNoData = view.findViewById(R.id.txtNoData);
        bottamView = view.findViewById(R.id.bottamView);
        frameLayout = (FrameLayout) view.findViewById(R.id.video);
        fabCollapse = (FloatingActionButton) view.findViewById(R.id.fabCollapse);
        ivPreview = (ImageView) view.findViewById(R.id.ivPreview);

        progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        bannerAdView = view.findViewById(R.id.bannerAdView);
        mInterstitialAd = new InterstitialAd(activity);

        // set the ad unit ID
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial_ad_id));

    }
}
