package com.pk.developer.downloader.tiktokdownload;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.pk.developer.downloader.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static android.content.Context.CLIPBOARD_SERVICE;

public class TikTokWithoutFragment extends Fragment {


    EditText inputURl;
    Button BtnDownload;
    Button withwatermark;
    private String VideoTitle;

    private String title;
    private int colorId;

    public TikTokWithoutFragment(String title, int colorId) {
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
        withwatermark = rootView.findViewById(R.id.withwatermark);
        BtnDownload = rootView.findViewById(R.id.btn_downloadTiktokVideo);
//        AdView mAdView = (AdView)rootView.findViewById(R.id.adView);
//        AdRequest adRequest = new AdRequest.Builder().build();
//        mAdView.loadAd(adRequest);
//        mInterstitialAd = new InterstitialAd(getContext());
//        mInterstitialAd.setAdUnitId(getString(R.string.interstitial));
//        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        BtnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (mInterstitialAd.isLoaded()) {
//                    mInterstitialAd.show();
//                } else {
//                    Log.d("TAG", "The interstitial wasn't loaded yet.");
//                }
                final String URL = inputURl.getText().toString();
                Toast.makeText(getContext(), "Checking URL", Toast.LENGTH_SHORT).show();
//                    TiktokVideoDownloader downloader = new TiktokVideoDownloader(Tiktok.this, URL);
//                    downloader.DownloadVideo();
                getWebsite(URL);
            }
        });

        withwatermark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String URL = inputURl.getText().toString();
                Toast.makeText(getActivity(), "Checking URL", Toast.LENGTH_SHORT).show();
                try {
                    //    TiktokVideoDownloader downloader = new TiktokVideoDownloader(getActivity(), URL);
                    //  downloader.DownloadVideo();

                    try {
                        new Data().execute(getVideoId(URL));
                    }catch (Exception e){
                        Toast.makeText(getContext(), "Try other Url", Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    Toast.makeText(getContext(), "Try other Url", Toast.LENGTH_SHORT).show();
                }
            }
        });

        final Button btnPaste = (Button)rootView.findViewById(R.id.btnPaste);
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

    public String getVideoId(String link) {
        if(!link.contains("https"))
        {
            link = link.replace("http","https");
        }
        return link;
    }

    @SuppressLint("StaticFieldLeak")
    private class Data extends AsyncTask<String, String,String> {

        @Override
        protected String doInBackground(String... strings) {
            HttpURLConnection connection;
            BufferedReader reader;
            try {
                URL url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuilder buffer =new StringBuilder();
                String Line;
                while ((Line = reader.readLine()) != null)
                {
                    try{
                        //Log.e("Hello", Line);
                        if(Line.contains("videoData"))
                        {
                            Line = Line.substring(Line.indexOf("videoData"));
                            //Log.e("Hello",Line);
                            Line = Line.substring(Line.indexOf("urls"));
                            //Log.e("Hello",Line);
                            VideoTitle = Line.substring(Line.indexOf("text"));
                            if(VideoTitle.contains("#")) {
                                VideoTitle = VideoTitle.substring(ordinalIndexOf(VideoTitle, "\"", 1) + 1, ordinalIndexOf(VideoTitle, "#", 0));
                            }
                            else {
                                VideoTitle = VideoTitle.substring(ordinalIndexOf(VideoTitle, "\"", 1) + 1, ordinalIndexOf(VideoTitle, "\"", 2));
                            }
                            //Log.e("HelloTitle",VideoTitle);
                            Line = Line.substring(ordinalIndexOf(Line,"\"",1)+1,ordinalIndexOf(Line,"\"",2));
                            //Log.e("HelloURL",Line);
                            if(!Line.contains("https"))
                            {
                                Line = Line.replace("http","https");
                            }
                            buffer.append(Line);
                            break;
                        }
                    }catch (Exception e){
                        Toast.makeText(getContext(), "Try other Url", Toast.LENGTH_SHORT).show();
                    }
                }
                return buffer.toString();
            } catch (Exception e) {
                return "Try other Url";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(URLUtil.isValidUrl(s))
            {
//                String path = createDirectory();
//                if(VideoTitle == null || VideoTitle.equals(""))
//                {
//                    VideoTitle = "TiktokVideo"+".mp4";
//                }
//                else {
//                    VideoTitle = VideoTitle + ".mp4";
//                }
//                File newFile = new File(path, VideoTitle);
                try {
//                    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(s));
//                    request.allowScanningByMediaScanner();
//                    request.setDescription("Downloading")
//                            .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE)
//                            .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI)
//                            .setDestinationUri(Uri.fromFile(newFile))
//                            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
//                            .setVisibleInDownloadsUi(true)
//                            .setTitle(VideoTitle);
//                    DownloadManager manager = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
//                    assert manager != null;
//                    long downLoadID = manager.enqueue(request);
                    DownloadManager dm = (DownloadManager) getContext().getSystemService(Context.DOWNLOAD_SERVICE);
                    Uri uri = Uri.parse(s);
                    DownloadManager.Request req = new DownloadManager.Request(uri);
                    req.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    String filename = "tiktok";
                    filename += ".mp4";
                    File dir = Environment.getExternalStorageDirectory();
                    String path1 = dir.getAbsolutePath();
                    String downloadPath =  path1+"/"+Environment.DIRECTORY_PICTURES +"/VideoDownloader";
                    File fileDir = new File(downloadPath);
                    if (fileDir.isDirectory()) {
                        fileDir.mkdirs();
                        req.setDestinationInExternalPublicDir( Environment.DIRECTORY_PICTURES,"/VideoDownloader/"+filename);
                    } else {
                        fileDir.mkdirs();
                        req.setDestinationInExternalPublicDir( Environment.DIRECTORY_PICTURES,"/VideoDownloader/"+filename);
                    }
                    Long ref = dm.enqueue(req);
                    Toast.makeText(getContext(), "Download is in progress check download folder", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    if (Looper.myLooper()==null)
                        Looper.prepare();
                    Toast.makeText(getContext(), "Video Can't be downloaded! Try Again", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            }
            else {
                if (Looper.myLooper()==null)
                    Looper.prepare();
                Toast.makeText(getContext(), s, Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        }
    }


    private void getWebsite(final String urls) {
        Log.e("strrr", "https://downloadtiktokvideos.com/?url="+urls);
        new Thread(new Runnable() {
            @Override
            public void run() {
                final StringBuilder builder = new StringBuilder();
                try {
                    Document doc = Jsoup.connect("https://downloadtiktokvideos.com/?url="+urls).timeout(100 * 1000).get();
                    Log.e("strrr", "https://downloadtiktokvideos.com/?url="+urls);
                    String title = doc.title();
                    Elements links = doc.select("source[src]");
                    builder.append(links.attr("src"));
                    Log.e("strrrrr", ""+links.attr("src"));
                } catch (IOException e) {
                    builder.append("Error : ").append(e.getMessage()).append("\n");
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String urls=builder.toString();
                            Log.e("strrr", ""+urls);
                            DownloadManager dm = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
                            Uri uri = Uri.parse(urls);
                            DownloadManager.Request req = new DownloadManager.Request(uri);
                            req.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                            String filename = "tiktok";
                            filename += ".mp4";
                            File dir = Environment.getExternalStorageDirectory();
                            String path1 = dir.getAbsolutePath();
                            String downloadPath =  path1+"/"+Environment.DIRECTORY_PICTURES +"/VideoDownloader";
                            File fileDir = new File(downloadPath);
                            if (fileDir.isDirectory()) {
                                fileDir.mkdirs();
                                req.setDestinationInExternalPublicDir( Environment.DIRECTORY_PICTURES,"/VideoDownloader/"+filename);
                            } else {
                                fileDir.mkdirs();
                                req.setDestinationInExternalPublicDir( Environment.DIRECTORY_PICTURES,"/VideoDownloader/"+filename);
                            }
                            Long ref = dm.enqueue(req);
                            Toast.makeText(getContext(), "Download is in progress check download folder", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            if (Looper.myLooper()==null)
                                Looper.prepare();
                            Toast.makeText(getContext(), "Video Can't be downloaded! Try Again", Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }
                        Log.d("Result",builder.toString());
                    }
                });
            }
        }).start();
    }


    private static int ordinalIndexOf(String str, String substr, int n) {
        int pos = -1;
        do {
            pos = str.indexOf(substr, pos + 1);
        } while (n-- > 0 && pos != -1);
        return pos;
    }


}
