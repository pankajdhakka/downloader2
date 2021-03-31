package com.pk.developer.downloader.videoutils.Utils;

import android.app.Application;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.StrictMode;
import android.util.Log;

import com.pk.developer.downloader.R;
import com.pk.developer.downloader.activities.MainActivity;
import com.google.android.gms.ads.MobileAds;
import com.onesignal.OSNotificationAction;
import com.onesignal.OneSignal;

import org.json.JSONObject;

import java.lang.reflect.Method;

public class MyApplication extends Application {

    public static boolean isFFmpegSupports;
    @Override
    public void onCreate() {
        super.onCreate();
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .setNotificationOpenedHandler(result -> {
                    OSNotificationAction.ActionType actionType = result.action.type;
                    JSONObject data = result.notification.payload.additionalData;
                    String type;

                    if (data != null) {
                        type = data.optString("type");
                        switch (type) {
                            case "post": {
                                int postId = data.optInt("value");
                                String postTitle = data.optString("title");

                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                intent.putExtra("postId", postId);
                                intent.putExtra("title", postTitle);
                                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                break;
                            }
                            case "web": {
                                String url = data.optString("value");
//                                Intent intent = new Intent(getApplicationContext(), ContainerActivity.class);
//                                intent.putExtra("screen","webview");
//                                intent.putExtra("url",url);
//                                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
//                                startActivity(intent);

                                Intent i = new Intent(Intent.ACTION_VIEW);
                                i.setData(Uri.parse(url));
                                i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(i);

                                break;
                            }
                            case "message": {
                                String title, message;
                                title = data.optString("title");
                                message = data.optString("message");
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                intent.putExtra("msgTitle",title);
                                intent.putExtra("msgBody",message);
                                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                break;
                            }
                        }
                    }

                    if (actionType == OSNotificationAction.ActionType.ActionTaken)
                        Log.i("OneSignalExample", "Button pressed with id: " + result.action.actionID);
                })
                .init();
        // initialize the AdMob app
        MobileAds.initialize(this, getString(R.string.adid));


        if (Build.VERSION.SDK_INT >= 24) {
            try {
                Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                m.invoke(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
