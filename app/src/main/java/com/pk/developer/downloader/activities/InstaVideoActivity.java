package com.pk.developer.downloader.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.pk.developer.downloader.R;
import com.pk.developer.downloader.helpers.checkNetwork;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.muddzdev.styleabletoast.StyleableToast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;

public class InstaVideoActivity extends AppCompatActivity {

    ProgressDialog progressDialog;
    EditText editText;
    String downlink;
    TextView t;
    ImageView img;
    Button b;
    Spinner spinner;
    AlertDialog.Builder builder;
    InterstitialAd mInterstitialAd;

    private RequestListener<Drawable> requestListener = new RequestListener<Drawable>() {
        @Override
        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Please provide the correct link", Toast.LENGTH_SHORT).show();
            t.setVisibility(View.GONE);
            b.setVisibility(View.GONE);
            return false;
        }

        @Override
        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
            return false;
        }

    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_insta_image);
        Button button = findViewById(R.id.download);
        t = (TextView) findViewById(R.id.imgtxt);
        t.setTextColor(getResources().getColor(R.color.primary_text));
        b = (Button) findViewById(R.id.instadownload);
        img = (ImageView) findViewById(R.id.instaimg);
        spinner = findViewById(R.id.dtypespinner);
        String[] options = {"Image", "Video"};
        ArrayAdapter aa = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_spinner_item, options);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(aa);
        editText = findViewById(R.id.input_TiktokURL);
        AdView mAdView = (AdView)findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                    if (mInterstitialAd.isLoaded()) {
                        mInterstitialAd.show();
                    } else {
                        Log.d("TAG", "The interstitial wasn't loaded yet.");
                    }

                    downlink = editText.getText().toString();
                    if (downlink.isEmpty()) {
                        Toast.makeText(getApplicationContext(), "Please provide the link", Toast.LENGTH_SHORT).show();
                    } else if ((spinner.getSelectedItem() == null) || (spinner == null)) {
                        Toast.makeText(getApplicationContext(), "Please select the type", Toast.LENGTH_SHORT).show();
                    } else if (!checkNetwork.isConnected(getApplicationContext())) {
                        builder = new AlertDialog.Builder(getApplicationContext());
                        builder.setMessage("Please Connect to the Internet")
                                .setCancelable(false)
                                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.dismiss();
                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.setTitle("Internet Required");
                        alert.show();
                    } else if (spinner.getSelectedItemPosition() == 0) {
                        new  Insta().execute();
                    } else {
                        new  InstaVideo().execute();
                    }
                }catch (Exception e){

                }

            }
        });
        progressDialog = new ProgressDialog(InstaVideoActivity.this);
        final Button btnPaste = (Button) findViewById(R.id.btnPaste);
        btnPaste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    if (editText.getText().toString().length()!=0){
                        editText.getText().clear();
                    }else {
                        ClipboardManager myClipboard = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
                        ClipData abc = myClipboard.getPrimaryClip();
                        if (abc !=null){
                            ClipData.Item item = abc.getItemAt(0);
                            String text = item.getText().toString();
                            editText.setText(text);
                        }

                    }
                }catch (Exception e){

                }

            }
        });


        Button open_tiktok=findViewById(R.id.open_tiktok);
        open_tiktok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = getPackageManager().getLaunchIntentForPackage("com.instagram.android");
                    if (intent != null) {
                        // We found the activity now start the activity
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    } else {
                        // Bring user to the market or let them choose an app?
                        intent = new Intent(Intent.ACTION_VIEW);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setData(Uri.parse("market://details?id=" + "com.instagram.android"));
                        startActivity(intent);
                    }
                }catch (Exception e){

                }
            }
        });

    }


    private class Insta extends AsyncTask<Void, Void, Void> {
        String dlink;
        String imglink;

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Please Wait");
            progressDialog.setCancelable(false);
            progressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Document doc = Jsoup.connect("https://www.dinsta.com/photos/")
                        .data("url", downlink)
                        .post();
                Log.v("Hello", doc.title());
                Element imgtag = doc.select("img").first();
                dlink = imgtag.attr("src");
                imglink = imgtag.attr("src");
            } catch (Exception e) {
                 runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Invalid Link", Toast.LENGTH_SHORT).show();
                    }
                });
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            t.setText("Image Preview");
            if (dlink == null) {
                t.setVisibility(View.GONE);
                b.setVisibility(View.GONE);
                img.setVisibility(View.GONE);
            } else {
                t.setVisibility(View.VISIBLE);
                b.setVisibility(View.VISIBLE);
                img.setVisibility(View.VISIBLE);
                Glide.with(getApplicationContext()).load(imglink).error(R.drawable.error).listener(requestListener).into(img);
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DownloadManager dm = (DownloadManager) getApplicationContext().getSystemService(Context.DOWNLOAD_SERVICE);
                        Uri uri = Uri.parse(dlink);
                        DownloadManager.Request req = new DownloadManager.Request(uri);
                        req.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                        File dir = Environment.getExternalStorageDirectory();
                        String path = dir.getAbsolutePath();
                        String downloadPath =  path+"/"+Environment.DIRECTORY_PICTURES +"/VideoDownloader";
                        File fileDir = new File(downloadPath);
                        if (fileDir.isDirectory()) {
                            fileDir.mkdirs();
                            req.setDestinationInExternalPublicDir( Environment.DIRECTORY_PICTURES,"/VideoDownloader/insta.jpg");
                        } else {
                            fileDir.mkdirs();
                            req.setDestinationInExternalPublicDir( Environment.DIRECTORY_PICTURES,"/VideoDownloader/insta.jpg");
                        }
                        StyleableToast.makeText(getApplicationContext(), "Download Started", Toast.LENGTH_SHORT, R.style.mytoast).show();
                        Long ref = dm.enqueue(req);
                    }
                });
                progressDialog.dismiss();
            }
        }
    }

    private class InstaVideo extends AsyncTask<Void, Void, Void> {
        String dlink, imglink;

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Please Wait");
            progressDialog.setCancelable(false);
            progressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Document doc = Jsoup.connect("https://www.10insta.net/#grid-gallery")
                        .data("url", downlink)
                        .post();
                Log.v("Hello", doc.title());
                Element srctag = doc.select("img.card-img-top").first();
                Element ptag = doc.select("p.card-text").first();
                Element atag = ptag.select("a").first();
                imglink = srctag.attr("src");
                dlink = "https://www.10insta.net/";
                dlink += atag.attr("href");
            } catch (Exception e) {
                 runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Invalid Link", Toast.LENGTH_SHORT).show();
                    }
                });
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            t.setText("Video Preview");
            if (dlink == null) {
                t.setVisibility(View.GONE);
                b.setVisibility(View.GONE);
                img.setVisibility(View.GONE);
            } else {
                t.setVisibility(View.VISIBLE);
                b.setVisibility(View.VISIBLE);
                img.setVisibility(View.VISIBLE);
                Glide.with(getApplicationContext()).load(imglink).error(R.drawable.error).listener(requestListener).into(img);
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DownloadManager dm = (DownloadManager) getApplicationContext().getSystemService(Context.DOWNLOAD_SERVICE);
                        Uri uri = Uri.parse(dlink);
                        DownloadManager.Request req = new DownloadManager.Request(uri);
                        req.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                        File dir = Environment.getExternalStorageDirectory();
                        String path = dir.getAbsolutePath();
                        String downloadPath =  path+"/"+Environment.DIRECTORY_PICTURES +"/VideoDownloader";
                        File fileDir = new File(downloadPath);
                        if (fileDir.isDirectory()) {
                            fileDir.mkdirs();
                            req.setDestinationInExternalPublicDir( Environment.DIRECTORY_PICTURES,"/VideoDownloader/insta.mp4");
                        } else {
                            fileDir.mkdirs();
                            req.setDestinationInExternalPublicDir( Environment.DIRECTORY_PICTURES,"/VideoDownloader/insta.mp4");
                        }
                        StyleableToast.makeText(getApplicationContext(), "Download Started", Toast.LENGTH_SHORT, R.style.mytoast).show();
                        Long ref = dm.enqueue(req);
                    }
                });
                progressDialog.dismiss();
            }
        }
    }
}
