package com.pk.developer.downloader.fbdownload;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.pk.developer.downloader.R;
import com.pk.developer.downloader.helpers.checkNetwork;
import com.muddzdev.styleabletoast.StyleableToast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;

import static android.content.Context.CLIPBOARD_SERVICE;

public class fragment_fb extends Fragment {
    ProgressDialog progressDialog;
    EditText editText;
    String downlink;
    String matag;
    TextView t;
    Button b;
    AlertDialog.Builder builder;

    public fragment_fb() {
    }

    private String title;
    private int colorId;

    public fragment_fb(String title, int colorId) {
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_fb, container, false);
        Button button = rootView.findViewById(R.id.download);
        progressDialog = new ProgressDialog(getContext());
        editText = rootView.findViewById(R.id.input_TiktokURL);
        t = rootView.findViewById(R.id.genlink);
        b = rootView.findViewById(R.id.fbdload);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downlink = editText.getText().toString();
                if (downlink.isEmpty()) {
                    Toast.makeText(getActivity(), "Please enter link", Toast.LENGTH_SHORT).show();
                }
                else if (!checkNetwork.isConnected(getActivity())) {
                    builder = new AlertDialog.Builder(getActivity());
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
            }
        });

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
                    Intent intent = getActivity().getPackageManager().getLaunchIntentForPackage("com.facebook.katana");
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
        return rootView;
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
//                        .data("url", "https://www.facebook.com/102988293558/posts/10158244539143559/")
                        .data("url", downlink)
                        .timeout(100*1000)
                        .post();
                Element p = doc.select("p.card-text").first();
                title = p.text();
                Log.e("Main", title);
                Element link = doc.select("div.card").first();
                Element link1 = link.select("div.btns-download").first();
//                if (link.attr("class").equalsIgnoreCase("col-md-4 btns-download")){

                String atag = link1.select("a").first().attr("href");
                Log.d("TextLink", atag);

                matag = atag;
//                }
                Log.e("Main12346", matag);
            } catch (Exception e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "Invalid Link", Toast.LENGTH_SHORT).show();
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

                DownloadManager dm = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
                Uri uri = Uri.parse(matag);
                DownloadManager.Request req = new DownloadManager.Request(uri);
                req.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                String filename = title;
                filename += ".mp4";
                String downloadPath = Environment.DIRECTORY_PICTURES + "/VideoDownloader";
                File fileDir = new File(downloadPath);
                if (fileDir.isDirectory()) {
                    fileDir.mkdirs();
                    req.setDestinationInExternalPublicDir( Environment.DIRECTORY_PICTURES,"/VideoDownloader/"+filename);
                } else {
                    fileDir.mkdirs();
                    req.setDestinationInExternalPublicDir( Environment.DIRECTORY_PICTURES,"/VideoDownloader/"+filename);
                }
                StyleableToast.makeText(getActivity(), "Download Started", Toast.LENGTH_SHORT, R.style.mytoast).show();
                Long ref = dm.enqueue(req);
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