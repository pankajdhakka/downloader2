package com.pk.developer.downloader.insta;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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

import static android.content.Context.CLIPBOARD_SERVICE;

public class InstaImageFragment extends Fragment {


    EditText inputURl;
    Button BtnDownload;
    //    InterstitialAd mInterstitialAd;

    ProgressDialog progressDialog;
    EditText editText;
    String downlink;
    TextView t;
    ImageView img;
    Button b;
    Spinner spinner;
    AlertDialog.Builder builder;
    InterstitialAd mInterstitialAd;

    private String title;
    private int colorId;

    public InstaImageFragment(String title, int colorId) {
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

    private RequestListener<Drawable> requestListener = new RequestListener<Drawable>() {
        @Override
        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Please provide the correct link", Toast.LENGTH_SHORT).show();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_insta_image, container, false);

        Button button = rootView.findViewById(R.id.download);
        t = (TextView) rootView.findViewById(R.id.imgtxt);
        t.setTextColor(getResources().getColor(R.color.primary_text));
        b = (Button) rootView.findViewById(R.id.instadownload);
        img = (ImageView) rootView.findViewById(R.id.instaimg);
        spinner = rootView.findViewById(R.id.dtypespinner);
        String[] options = {"Image", "Video"};
        ArrayAdapter aa = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, options);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(aa);
        editText = rootView.findViewById(R.id.input_TiktokURL);
        AdView mAdView = (AdView)rootView.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        mInterstitialAd = new InterstitialAd(getActivity());
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
                        Toast.makeText(getContext(), "Please provide the link", Toast.LENGTH_SHORT).show();
                    }else if (!checkNetwork.isConnected(getContext())) {
                        builder = new AlertDialog.Builder(getContext());
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
                        new Insta().execute();

//                        new InstaVideo().execute();
                    }
                }catch (Exception e){

                }

            }
        });
        progressDialog = new ProgressDialog(getActivity());
        final Button btnPaste = (Button) rootView.findViewById(R.id.btnPaste);
        btnPaste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    if (editText.getText().toString().length()!=0){
                        editText.getText().clear();
                    }else {
                        ClipboardManager myClipboard = (ClipboardManager)getContext().getSystemService(CLIPBOARD_SERVICE);
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


        Button open_tiktok=rootView.findViewById(R.id.open_tiktok);
        open_tiktok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = getActivity().getPackageManager().getLaunchIntentForPackage("com.instagram.android");
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
        return rootView;

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

                Toast.makeText(getContext(), "Invalid Link", Toast.LENGTH_SHORT).show();
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
                Glide.with(getActivity()).load(imglink).error(R.drawable.error).listener(requestListener).into(img);
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DownloadManager dm = (DownloadManager) getContext().getSystemService(Context.DOWNLOAD_SERVICE);
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
                        StyleableToast.makeText(getContext(), "Download Started", Toast.LENGTH_SHORT, R.style.mytoast).show();
                        Long ref = dm.enqueue(req);
                    }
                });
                progressDialog.dismiss();
            }
        }
    }
}



