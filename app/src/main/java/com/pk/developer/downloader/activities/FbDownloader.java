package com.pk.developer.downloader.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.pk.developer.downloader.R;
import com.pk.developer.downloader.helpers.checkNetwork;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.muddzdev.styleabletoast.StyleableToast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;

public class FbDownloader extends AppCompatActivity {

    ProgressDialog progressDialog;
    EditText editText;
    String downlink;
    String matag;
    TextView t;
    Button b;
    AlertDialog.Builder builder;
    InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_fb);
        Button button = findViewById(R.id.download);
        progressDialog = new ProgressDialog(FbDownloader.this);
        editText = findViewById(R.id.input_TiktokURL);
        t = findViewById(R.id.genlink);
        b = findViewById(R.id.fbdload);
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
                        Toast.makeText(getApplicationContext(), "Please enter link", Toast.LENGTH_SHORT).show();
                    }
                    else if (!checkNetwork.isConnected(getApplicationContext())) {
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
                    } else {
                        new Facebook().execute();
                    }
                }catch (Exception e){
                }
            }
        });
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
                    Intent intent = getPackageManager().getLaunchIntentForPackage("com.facebook.katana");
                    if (intent != null) {
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    } else {
                        intent = new Intent(Intent.ACTION_VIEW);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setData(Uri.parse("market://details?id=" + "com.facebook.katana"));
                        startActivity(intent);
                    }

                }catch (Exception e){

                }



            }
        });

    }
    private class Facebook extends AsyncTask<Void, Void, Void> {

        String title;

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
                Document doc = Jsoup.connect("https://www.getfvid.com/downloader")
                        .data("url", downlink)
                        .post();
                Element p = doc.select("p.card-text").first();
                title = p.text();
                Log.e("Main", title);
                Element link = doc.select("p#sdlink").first();
                String atag = link.select("a").first().attr("href");
                matag = atag;
                Log.e("Main", matag);
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
            progressDialog.dismiss();
            if (title == null) {
                b.setVisibility(View.INVISIBLE);
            } else {
                t.setText(title);
                b.setVisibility(View.VISIBLE);
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DownloadManager dm = (DownloadManager) getApplicationContext().getSystemService(Context.DOWNLOAD_SERVICE);
                        Uri uri = Uri.parse(matag);
                        DownloadManager.Request req = new DownloadManager.Request(uri);
                        req.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                        String filename = title;
                        filename += ".mp4";
                        File dir = Environment.getExternalStorageDirectory();
                        String path = dir.getAbsolutePath();
                        String downloadPath =  path+"/"+Environment.DIRECTORY_PICTURES +"/VideoDownloader";
                        File fileDir = new File(downloadPath);
                        if (fileDir.isDirectory()) {
                            fileDir.mkdirs();
                            req.setDestinationInExternalPublicDir( Environment.DIRECTORY_PICTURES,"/VideoDownloader/"+filename);
                        } else {
                            fileDir.mkdirs();
                            req.setDestinationInExternalPublicDir( Environment.DIRECTORY_PICTURES,"/VideoDownloader/"+filename);
                        }
                        StyleableToast.makeText(getApplicationContext(), "Download Started", Toast.LENGTH_SHORT, R.style.mytoast).show();
                        Long ref = dm.enqueue(req);
                    }
                });
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

}
