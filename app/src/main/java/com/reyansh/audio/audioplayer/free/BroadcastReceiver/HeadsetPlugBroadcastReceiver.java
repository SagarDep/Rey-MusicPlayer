package com.reyansh.audio.audioplayer.free.BroadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.reyansh.audio.audioplayer.free.Common.CommonClass;

public class HeadsetPlugBroadcastReceiver extends BroadcastReceiver {
    private CommonClass mApp;
    @Override
    public void onReceive(Context context, Intent intent) {
        mApp = (CommonClass) context.getApplicationContext();
        if (intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {
            int state = intent.getIntExtra("state", -1);
            Log.d("state",""+state);
            switch (state) {
                case 0:
                    mApp.getService().headsetDisconnected();
                    break;
                case 1:
                    mApp.getService().headsetisConnected();
                    break;
                default:
                    break;
            }

        }

    }
}