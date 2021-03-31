package com.pk.developer.downloader.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.view.View;
import android.widget.ImageView;

import com.pk.developer.downloader.R;
import com.pk.developer.downloader.fragments.SavedImageAdapter;
import com.pk.developer.downloader.fragments.SavedImageModel;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class ViewFilesActivity extends AppCompatActivity {


    private SavedImageAdapter recyclerViewAdapter;
    private RecyclerView recyclerView;
    private File[] files;
    RecyclerView.LayoutManager layoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_viewfiles);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new GridLayoutManager(this,2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerViewAdapter = new SavedImageAdapter(this, getData());
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerViewAdapter.notifyDataSetChanged();


        ImageView backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


    }

    private ArrayList<SavedImageModel> getData() {
        ArrayList<SavedImageModel> filesList = new ArrayList<>();
        SavedImageModel f;
        File dir = Environment.getExternalStorageDirectory();
        String path = dir.getAbsolutePath();
        String targetPath =  path+"/"+Environment.DIRECTORY_PICTURES +"/VideoDownloader";
        File targetDirector = new File(targetPath);
        files = targetDirector.listFiles();
        if (files == null) {
        }
        try {
            Arrays.sort(files, new Comparator() {
                public int compare(Object o1, Object o2) {
                    if (((File) o1).lastModified() > ((File) o2).lastModified()) {
                        return -1;
                    } else if (((File) o1).lastModified() < ((File)o2).lastModified()) {
                        return +1;
                    } else {
                        return 0;
                    }
                }
            });
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                f = new SavedImageModel();
                f.setName("Saved Stories: "+(i+1));
                f.setFilename(file.getName());
                f.setUri(Uri.fromFile(file));
                f.setPath(files[i].getAbsolutePath());
                filesList.add(f);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return filesList;
    }
}
