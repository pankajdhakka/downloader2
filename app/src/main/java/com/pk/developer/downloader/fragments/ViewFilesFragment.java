package com.pk.developer.downloader.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pk.developer.downloader.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class ViewFilesFragment extends Fragment {

    private SavedImageAdapter recyclerViewAdapter;
    private RecyclerView recyclerView;
    private File[] files;
    RecyclerView.LayoutManager layoutManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_viewfiles, container, false);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new GridLayoutManager(getContext(),2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerViewAdapter = new SavedImageAdapter(getContext(), getData());
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerViewAdapter.notifyDataSetChanged();
        return rootView;
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
