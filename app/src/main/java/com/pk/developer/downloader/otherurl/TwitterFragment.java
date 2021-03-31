package com.pk.developer.downloader.otherurl;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.pk.developer.downloader.R;
import com.pk.developer.downloader.activities.TwitterVideoDownloader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import static android.content.Context.CLIPBOARD_SERVICE;

public class TwitterFragment extends Fragment {

    private String title;
    private int colorId;
    EditText inputURl;
    Button BtnDownload;
    InterstitialAd mInterstitialAd;

    public TwitterFragment(String title, int colorId) {
        this.title = title;
        this.colorId = colorId;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        TextView textView = getView().findViewById(R.id.tab_title);
//        textView.setText(title);
//        textView.setTextColor(ContextCompat.getColor(getContext(), colorId));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.activity_twitter, container, false);
        AdView mAdView = (AdView)v.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        inputURl = v.findViewById(R.id.inputURl);

        mInterstitialAd = new InterstitialAd(getContext());
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        BtnDownload = v.findViewById(R.id.btn_downloadTiktokVideo);
        BtnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (mInterstitialAd.isLoaded()) {
                        mInterstitialAd.show();
                    } else {
                        Log.d("TAG", "The interstitial wasn't loaded yet.");
                    }

                    final String URL = inputURl.getText().toString();
                    TwitterVideoDownloader downloader = new TwitterVideoDownloader(getContext(), URL);
                    downloader.DownloadVideo();
                }catch (Exception e){
                }
            }
        });
        final Button btnPaste = (Button) v.findViewById(R.id.btnPaste);
        btnPaste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (inputURl.getText().toString().length()!=0){
                        inputURl.getText().clear();
                    }else {
                        ClipboardManager myClipboard = (ClipboardManager)getContext().getSystemService(CLIPBOARD_SERVICE);
                        ClipData abc = myClipboard.getPrimaryClip();
                        if (abc !=null){
                            ClipData.Item item = abc.getItemAt(0);
                            String text = item.getText().toString();
                            inputURl.setText(text);
                        }
                    }
                }catch (Exception e){
                }
            }
        });
        Button open_tiktok=v.findViewById(R.id.open_tiktok);
        open_tiktok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = getActivity().getPackageManager().getLaunchIntentForPackage("com.twitter.android");
                    if (intent != null) {
                        // We found the activity now start the activity
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    } else {
                        // Bring user to the market or let them choose an app?
                        intent = new Intent(Intent.ACTION_VIEW);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setData(Uri.parse("market://details?id=" + "com.twitter.android"));
                        startActivity(intent);
                    }
                }catch (Exception e){
                }
            }
        });
        return v;
    }

}
