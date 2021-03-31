package com.pk.developer.downloader.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.pk.developer.downloader.BuildConfig;
import com.pk.developer.downloader.R;
import com.pk.developer.downloader.activities.SavedFullImageActivity;
import com.pk.developer.downloader.activities.SavedFullVideoActivity;
import com.pk.developer.downloader.activities.ViewFilesActivity;
import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;

public class SavedImageAdapter extends RecyclerView.Adapter<SavedImageAdapter.ViewHolder> {
    private Context context;
    private ArrayList<SavedImageModel> filesList;

    public SavedImageAdapter(Context context, ArrayList<SavedImageModel> filesList) {
        this.context = context;
        this.filesList = filesList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_card_row,null,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final SavedImageModel files = filesList.get(position);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        final Uri uri = Uri.parse(files.getUri().toString());
        final File file = new File(uri.getPath());
        if(files.getUri().toString().endsWith(".mp4"))
        {
            holder.playIcon.setVisibility(View.VISIBLE);
        }else{
            holder.playIcon.setVisibility(View.INVISIBLE);
        }
        Glide.with(context)
                .load(files.getUri())
                .into(holder.savedImage);
        holder.reltivie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri mainUri = Uri.fromFile(file);
                if(files.getUri().toString().endsWith(".jpg")){
//                    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
//                    sharingIntent.setType("image/*");
//                    sharingIntent.putExtra(Intent.EXTRA_STREAM, mainUri);
//                    sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                    try {
//                        context.startActivity(Intent.createChooser(sharingIntent, "Share Image using"));
//                    } catch (ActivityNotFoundException e) {
//                        Toast.makeText(context, "No application found to open this file.", Toast.LENGTH_LONG).show();
//                    }
                    Intent i=new Intent(context, SavedFullImageActivity.class);
                    i.putExtra("url",file.getAbsolutePath());
                    context.startActivity(i);

                }else if(files.getUri().toString().endsWith(".mp4")){
//                    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
//                    sharingIntent.setType("video/*");
//                    sharingIntent.putExtra(Intent.EXTRA_STREAM, mainUri);
//                    sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                    try {
//                        context.startActivity(Intent.createChooser(sharingIntent, "Share Video using"));
//                    } catch (ActivityNotFoundException e) {
//                        Toast.makeText(context, "No application found to open this file.", Toast.LENGTH_LONG).show();
//                    }

                    Intent i=new Intent(context, SavedFullVideoActivity.class);
                    i.putExtra("urls",file.getAbsolutePath());
                    context.startActivity(i);

                }
            }
        });

        holder.shareImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("video/*");
                sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                Uri photoURI = FileProvider.getUriForFile(context,
                        BuildConfig.APPLICATION_ID + ".provider",
                        new File(file.getAbsolutePath()));
                sharingIntent.putExtra(Intent.EXTRA_STREAM,photoURI);
                context.startActivity(Intent.createChooser(sharingIntent, "Share to"));
            }
        });
        holder.deleteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File fdelete = new File(file.getAbsolutePath());
                if (fdelete.exists()) {
                    if (fdelete.delete()) {
//                        System.out.println("file Deleted :" + uri.getPath());
//                        context.startActivity(, ViewFilesActivity.class);
                        Intent i=new Intent(context,ViewFilesActivity.class);
                        context.startActivity(i);
                        ((Activity)context).finish();
                    } else {
//                        System.out.println("file not Deleted :" + uri.getPath());
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return filesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView savedImage;
        ImageView playIcon, shareImage, deleteImage;
        TextView shareID;
        RelativeLayout reltivie;
        public ViewHolder(View itemView) {
            super(itemView);
            savedImage = (ImageView) itemView.findViewById(R.id.mainImageView);
            playIcon = (ImageView) itemView.findViewById(R.id.playButtonImage);
            shareID = (TextView) itemView.findViewById(R.id.shareID);
            reltivie = (RelativeLayout) itemView.findViewById(R.id.reltivie);
            deleteImage = (ImageView) itemView.findViewById(R.id.deleteImage);
            shareImage = (ImageView) itemView.findViewById(R.id.shareImage);
        }
    }
}
