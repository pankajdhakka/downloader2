package com.pk.developer.downloader.tiktokdownload;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.pk.developer.downloader.R;
import com.pk.developer.downloader.activities.TiktokVideoDownloader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import static android.content.Context.CLIPBOARD_SERVICE;

public class Tiktokwithwatermark extends Fragment {


    EditText inputURl;
    Button BtnDownload;
//    InterstitialAd mInterstitialAd;

    private String title;
    private int colorId;

    public Tiktokwithwatermark(String title, int colorId) {
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
        View rootView = inflater.inflate(R.layout.activity_tiktok, container, false);
        inputURl = rootView.findViewById(R.id.input_TiktokURL);
        BtnDownload = rootView.findViewById(R.id.btn_downloadTiktokVideo);
        AdView mAdView = (AdView)rootView.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        BtnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String URL = inputURl.getText().toString();
                Toast.makeText(getActivity(), "Checking URL", Toast.LENGTH_SHORT).show();
                try {
                    TiktokVideoDownloader downloader = new TiktokVideoDownloader(getActivity(), URL);
                    downloader.DownloadVideo();
                }catch (Exception e){
                    Toast.makeText(getContext(), "Try other Url", Toast.LENGTH_SHORT).show();
                }




            }
        });

        final Button btnPaste = (Button) rootView.findViewById(R.id.btnPaste);
        btnPaste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (inputURl.getText().toString().length()!=0){
                        inputURl.getText().clear();
                    }else {
                        ClipboardManager myClipboard = (ClipboardManager)getActivity().getSystemService(CLIPBOARD_SERVICE);
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

        Button open_tiktok=rootView.findViewById(R.id.open_tiktok);
        open_tiktok.setText("Open Tiktok App");
        open_tiktok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = getActivity().getPackageManager().getLaunchIntentForPackage("com.zhiliaoapp.musically");
                    if (intent != null) {
                        // We found the activity now start the activity
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    } else {
                        // Bring user to the market or let them choose an app?
                        intent = new Intent(Intent.ACTION_VIEW);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setData(Uri.parse("market://details?id=" + "com.zhiliaoapp.musically"));
                        startActivity(intent);
                    }
                }catch (Exception e){

                }
            }
        });
        return rootView;
    }
}

