package com.pk.developer.downloader.fbdownload;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JavascriptInterface;
import android.webkit.URLUtil;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.pk.developer.downloader.R;

import java.io.File;

public class FBHomeFragment extends Fragment {

    private String title;
    private int colorId;

    public FBHomeFragment(String title, int colorId) {
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

    private static final String target_url = "https://m.facebook.com";
    private static WebView webView;
    View rootView;
    private LinearLayout lay_main;
    private LinearLayout adLinLay;

    class mClient extends WebViewClient {
        mClient() {
        }

        public void onPageFinished(WebView view, String url) {
            FBHomeFragment.webView.loadUrl("javascript:(function() { var el = document.querySelectorAll('div[data-sigil]');for(var i=0;i<el.length; i++){var sigil = el[i].dataset.sigil;if(sigil.indexOf('inlineVideo') > -1){delete el[i].dataset.sigil;var jsonData = JSON.parse(el[i].dataset.store);el[i].setAttribute('onClick', 'DownloaderInterface.processVideoToDownload(\"'+jsonData['src']+'\");');}}})()");
        }

        public void onLoadResource(WebView view, String url) {
            FBHomeFragment.webView.loadUrl("javascript:(function prepareVideo() { var el = document.querySelectorAll('div[data-sigil]');for(var i=0;i<el.length; i++){var sigil = el[i].dataset.sigil;if(sigil.indexOf('inlineVideo') > -1){delete el[i].dataset.sigil;console.log(i);var jsonData = JSON.parse(el[i].dataset.store);el[i].setAttribute('onClick', 'DownloaderInterface.processVideoToDownload(\"'+jsonData['src']+'\",\"'+jsonData['videoID']+'\");');}}})()");
            FBHomeFragment.webView.loadUrl("javascript:( window.onload=prepareVideo;)()");
        }
    }

    SwipeRefreshLayout mySwipeRefreshLayout;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case 1:{
                    webViewGoBack();
                }
                break;
            }
        }
    };

    private void webViewGoBack(){
        webView.goBack();
    }


    @SuppressLint({"SetJavaScriptEnabled"})
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.rootView = inflater.inflate(R.layout.fbhome_fragment, container, false);
        webView = (WebView) rootView.findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(this, "DownloaderInterface");
        webView.setWebViewClient(new mClient());
        CookieSyncManager.createInstance(getActivity());
        CookieManager.getInstance().setAcceptCookie(true);
        CookieSyncManager.getInstance().startSync();
        webView.loadUrl(target_url);
        webView.setOnKeyListener(new View.OnKeyListener(){
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == MotionEvent.ACTION_UP && webView.canGoBack()) {
                    handler.sendEmptyMessage(1);
                    return true;
                }
                return false;
            }
        });
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if( URLUtil.isNetworkUrl(url) ) {
                    return false;
                }

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity( intent );
                return true;
            }

        });
        mySwipeRefreshLayout = (SwipeRefreshLayout)rootView.findViewById(R.id.container);
        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        webView.reload();
                        mySwipeRefreshLayout.setRefreshing(false);
                    }
                }
        );



        return rootView;
    }

    @JavascriptInterface
    public void processVideoToDownload(String vidData, String vidID) {
        openMagicBox(vidData, vidID);
    }

    public static boolean Back() {
        if (!webView.canGoBack()) {
            return false;
        }
        webView.goBack();
        return true;
    }

    public void openMagicBox(final String vidData, final String vidID) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.download_live_dialog, null);
        LinearLayout mLeanerLive = (LinearLayout) view.findViewById(R.id.lay_live);
        LinearLayout mLeanerDownload = (LinearLayout) view.findViewById(R.id.lay_download);
        final Dialog mBottomSheetDialog = new Dialog(getActivity(), R.style.MaterialDialogSheet);
        mBottomSheetDialog.setContentView(view);
        mBottomSheetDialog.setCancelable(true);
        mBottomSheetDialog.getWindow().setLayout(-1, -2);
        mBottomSheetDialog.getWindow().setGravity(Gravity.CENTER);
        mBottomSheetDialog.show();
        mLeanerLive.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mBottomSheetDialog.dismiss();
                streamFB(vidData);
            }
        });
        mLeanerDownload.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mBottomSheetDialog.dismiss();
                downloadFB(vidData, vidID);
            }
        });
    }

    public void streamFB(String vid_url) {
        try {
            Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(vid_url));
            intent.setDataAndType(Uri.parse(vid_url), "video/mp4");
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Network Error", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint({"NewApi"})
    public void downloadFB(String vid_url, String vid_id) {
        try {

            DownloadManager dm = (DownloadManager) getContext().getSystemService(Context.DOWNLOAD_SERVICE);
            Uri uri = Uri.parse(vid_url);
            DownloadManager.Request req = new DownloadManager.Request(uri);
            req.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            String filename = "fb";
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
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}

