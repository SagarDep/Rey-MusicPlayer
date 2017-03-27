package com.reyansh.audio.audioplayer.free.BroadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.reyansh.audio.audioplayer.free.Common.CommonClass;
import com.reyansh.audio.audioplayer.free.MusicService.MusicService;

public class NotificationBroadcastReceiver extends BroadcastReceiver {
    CommonClass commonClass;
    Context mContext;
    String TAG = "NotificationBroadcastReceiver";
    boolean DEBUG = true;
    @Override
    public void onReceive(Context context, Intent intent) {
        commonClass = (CommonClass) context.getApplicationContext();
        mContext = context;
        if (!commonClass.isServiceRunning()) {
            mContext.startService(new Intent(context, MusicService.class));
        }
        try {
            String action = intent.getAction();
            if (action.equalsIgnoreCase("mediaplayer.com.Music_Service.NEXT")) {
                commonClass.getService().nextSong();
                if (DEBUG) Log.d(TAG, "nextSong");
            } else if (action.equalsIgnoreCase("mediaplayer.com.Music_Service.PAUSE")) {
                commonClass.getService().playPauseSong();
                commonClass.getService().updateNotification();
                if (DEBUG) Log.d(TAG, "playPauseSong");
            } else if (action.equalsIgnoreCase("mediaplayer.com.Music_Service.PREVIOUS")) {
                commonClass.getService().previousSong();
                if (DEBUG) Log.d(TAG, "previousSong");
            } else if (action.equalsIgnoreCase("mediaplayer.com.Music_Service.STOP")) {
                commonClass.getService().stopSong();
                if (DEBUG) Log.d(TAG, "stopSelf");
            }
        } catch (Exception e) {
        }

    }
}
