package com.pk.developer.downloader.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.pk.developer.downloader.BuildConfig;
import com.pk.developer.downloader.R;
import com.pk.developer.downloader.WaStatus.WaActivity;
import com.pk.developer.downloader.activities.ViewFilesActivity;
import com.pk.developer.downloader.fbdownload.FBHOMENew;
import com.pk.developer.downloader.insta.IntaMainActivity;
import com.pk.developer.downloader.otherurl.OtherURLActivity;
import com.pk.developer.downloader.tiktokdownload.TikTokDownloadActivity;
import com.pk.developer.downloader.videoutils.MainActivity;

public class HomeFragment extends Fragment {

    public HomeFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.new_home_fragment , container, false);

        LinearLayout insta = rootView.findViewById(R.id.btnInsta);
        insta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), IntaMainActivity.class);
                startActivity(intent);
            }
        });

        LinearLayout facebook = rootView.findViewById(R.id.btnFacebook);
        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), FBHOMENew.class);
                startActivity(intent);
            }
        });

        LinearLayout btnwhatsapp = rootView.findViewById(R.id.btnwhatsapp);
        btnwhatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), WaActivity.class);
                startActivity(intent);
            }
        });

        LinearLayout tiktok = rootView.findViewById(R.id.btnTiktok);
        tiktok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), TikTokDownloadActivity.class);
                startActivity(intent);
            }
        });

        LinearLayout twitter = rootView.findViewById(R.id.btnTwitter);
        twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), OtherURLActivity.class);
                startActivity(intent);
            }
        });

        LinearLayout vimeo = rootView.findViewById(R.id.btnVimeo);
        vimeo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MainActivity.class);
                startActivity(intent);

            }
        });

        LinearLayout share = rootView.findViewById(R.id.btnShare);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent sendIntent = new Intent();
//                sendIntent.setAction(Intent.ACTION_SEND);
//                sendIntent.putExtra(Intent.EXTRA_TEXT,
//                        "Hey check out my app at: https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID);
//                sendIntent.setType("text/plain");
//                startActivity(sendIntent);
//                ViewFilesFragment viewFilesFragment = new ViewFilesFragment();
//                loadFragment(viewFilesFragment);

                Intent intent = new Intent(getContext(), ViewFilesActivity.class);
                startActivity(intent);

            }
        });

        LinearLayout rate = rootView.findViewById(R.id.btnRate);
        rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://play.google.com/store/apps/details?id=" +getActivity().getPackageName())));

            }
        });
        return rootView;
    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            FragmentTransaction ft =
                    getFragmentManager().beginTransaction();
            ft.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            ft.replace(R.id.fragment_container, fragment).commit();
            return true;
        }
        return false;
    }

}