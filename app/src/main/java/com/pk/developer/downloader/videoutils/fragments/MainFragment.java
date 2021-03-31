package com.pk.developer.downloader.videoutils.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.pk.developer.downloader.R;
import com.pk.developer.downloader.videoutils.MainActivity;
import com.pk.developer.downloader.videoutils.Utils.Constants;
import com.pk.developer.downloader.videoutils.Utils.MyApplication;
import com.pk.developer.downloader.videoutils.Utils.PathUtil;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {

    public MainFragment() {
        // Required empty public constructor
    }

    private final String TAG = "MainFragment";
    Activity activity;
    AdView bannerAdView;
    boolean isAdLoaded;
    CardView cardVideoToGIF, cardImagesToGIF, cardCaptureImage, cardVideoToAudio, cardVideoCutter, cardGallery;
    LinearLayout linearRow2;

    private String SELECTED_TYPE = Constants.TYPE_GIF;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (bannerAdView != null) {
            bannerAdView.resume();
        }
        ((MainActivity) activity).setTitle("");
        ((MainActivity) activity).setDrawerState(true);

        if (!MyApplication.isFFmpegSupports) {
            linearRow2.setVisibility(View.GONE);
        }
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
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);

        cardVideoToGIF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(cardVideoToGIF);
                SELECTED_TYPE = Constants.TYPE_GIF;
            }
        });

        cardImagesToGIF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(cardImagesToGIF);
                SELECTED_TYPE = Constants.TYPE_GIF;
            }
        });

        cardCaptureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getVideoFromGallery();
                SELECTED_TYPE = Constants.TYPE_IMAGE;
            }
        });

        cardVideoToAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getVideoFromGallery();
                SELECTED_TYPE = Constants.TYPE_AUDIO;
            }
        });

        cardVideoCutter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getVideoFromGallery();
                SELECTED_TYPE = Constants.TYPE_VIDEO;
            }
        });

        cardGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Constants.SELECTED = Constants.GALLERY_FRAGMENT;
                ((MainActivity) activity).loadFragment("");
            }
        });

        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                // Check the LogCat to get your test device ID
                .addTestDevice("5F338128595663F734E36512D2DF7D2F")
                .build();

        bannerAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                isAdLoaded = true;
                bannerAdView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAdClosed() {
                isAdLoaded = false;
                bannerAdView.setVisibility(View.GONE);
                Log.d(TAG, "onAdClosed: ");
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                isAdLoaded = false;
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

        bannerAdView.loadAd(adRequest);
    }

    private void showPopupMenu(final CardView card) {

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(activity);
        bottomSheetDialog.setContentView(R.layout.dialog_chooser);
        CardView ivCamera = bottomSheetDialog.findViewById(R.id.cardCamera);
        CardView ivGallery = bottomSheetDialog.findViewById(R.id.cardGallery);

        ivCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)) {
                    ActivityCompat.requestPermissions(activity, Constants.PERMISSIONS_CAMERA, Constants.REQUEST_CAMERA);
                } else {
                    openCamera(card);
                }
                bottomSheetDialog.dismiss();
            }
        });

        ivGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    ActivityCompat.requestPermissions(activity, Constants.PERMISSIONS_STORAGE, Constants.REQUEST_EXTERNAL_STORAGE);
                } else {
                    openGallery(card);
                }
                bottomSheetDialog.dismiss();
            }
        });

        bottomSheetDialog.show();
    }

    private void openGallery(CardView card) {

        if (card.getId() == R.id.cardVideoToGIF) {
            getVideoFromGallery();
        } else {
            getImagesFromGallery();
        }
    }

    private void openCamera(CardView card) {

        if (card.getId() == R.id.cardVideoToGIF) {
            getVideoFromCamera();
        } else {
            getImagesFromCamera();
        }
    }

    private void getVideoFromCamera() {

        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        takeVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 30);
        startActivityForResult(takeVideoIntent, Constants.REQUEST_TAKE_GALLERY_VIDEO);
    }

    private void getImagesFromCamera() {

    }

    private void getVideoFromGallery() {

        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        intent.setType("video/*");
        startActivityForResult(Intent.createChooser(intent, "Select Video"), Constants.REQUEST_TAKE_GALLERY_VIDEO);
    }

    private void getImagesFromGallery() {



    }

    private void initViews(View view) {

        activity = getActivity();
        linearRow2 = view.findViewById(R.id.linearRow2);
        cardVideoToGIF = view.findViewById(R.id.cardVideoToGIF);
        cardImagesToGIF = view.findViewById(R.id.cardImagesToGIF);
        cardCaptureImage = view.findViewById(R.id.cardCaptureImage);
        cardVideoToAudio = view.findViewById(R.id.cardVideoToAudio);
        cardVideoCutter = view.findViewById(R.id.cardVideoCutter);
        cardGallery = view.findViewById(R.id.cardGallery);

        bannerAdView = view.findViewById(R.id.bannerAdView);;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {

            switch (requestCode) {
                case Constants.REQUEST_TAKE_GALLERY_VIDEO:
                    Uri selectedImageUri = data.getData();

                    // MEDIA GALLERY
                    String videoPath = PathUtil.getPath(activity, selectedImageUri);
                    if (videoPath != null) {
                        Bundle bundle = new Bundle();
                        bundle.putString("video", videoPath);

                        switch (SELECTED_TYPE) {
                            case Constants.TYPE_GIF:
                                Constants.SELECTED = Constants.VIDEO_GIF_FRAGMENT;
                                break;
                            case Constants.TYPE_IMAGE:
                                Constants.SELECTED = Constants.CAPTURE_IMAGE_FRAGMENT;
                                break;
                            case Constants.TYPE_AUDIO:
                                Constants.SELECTED = Constants.VIDEO_AUDIO_FRAGMENT;
                                break;
                            case Constants.TYPE_VIDEO:
                                Constants.SELECTED = Constants.VIDEO_CUTTER_FRAGMENT;
                                break;
                        }

                        ((MainActivity) activity).loadFragment(bundle);
                    }
                    break;
                case Constants.REQUEST_TAKE_MULTI_IMAGES:
                    break;
                case Constants.REQUEST_GALLERY_MULTI_IMAGES:
                    break;
            }
        }
    }


}
