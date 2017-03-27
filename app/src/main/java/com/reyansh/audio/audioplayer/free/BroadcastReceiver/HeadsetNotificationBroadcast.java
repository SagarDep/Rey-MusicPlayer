package com.reyansh.audio.audioplayer.free.BroadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;

import com.reyansh.audio.audioplayer.free.Common.CommonClass;

public class HeadsetNotificationBroadcast extends BroadcastReceiver {
    CommonClass commonClass;
    @Override
    public void onReceive(Context context, Intent intent) {
        commonClass = (CommonClass) context.getApplicationContext();
        if (intent.getAction().equals(Intent.ACTION_MEDIA_BUTTON)) {
            KeyEvent keyEvent = (KeyEvent) intent.getExtras().get(Intent.EXTRA_KEY_EVENT);
            if (keyEvent.getAction() != KeyEvent.ACTION_DOWN)
                return;
            switch (keyEvent.getKeyCode()) {
                case KeyEvent.KEYCODE_HEADSETHOOK:
                    commonClass.getService().playPauseSong();
                    Log.d("TAG", "TAG: KEYCODE_HEADSETHOOK");
                    break;
                case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                    commonClass.getService().playPauseSong();
                    Log.d("TAG", "TAG: KEYCODE_MEDIA_PLAY_PAUSE");
                    break;
                case KeyEvent.KEYCODE_MEDIA_PLAY:
                    commonClass.getService().playPauseSong();
                    Log.d("TAG", "TAG: KEYCODE_MEDIA_PLAY");
                    commonClass.getService().playPauseSong();
                    break;
                case KeyEvent.KEYCODE_MEDIA_PAUSE:

                    Log.d("TAG", "TAG: KEYCODE_MEDIA_PAUSE");
                    commonClass.getService().playPauseSong();
                    break;
                case KeyEvent.KEYCODE_MEDIA_STOP:
                    commonClass.getService().playPauseSong();
                    Log.d("TAG", "TAG: KEYCODE_MEDIA_STOP");
                    break;
                case KeyEvent.KEYCODE_MEDIA_NEXT:
                    Log.d("TAG", "TAG: KEYCODE_MEDIA_NEXT");
                    commonClass.getService().nextSong();
                    break;
                case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                    Log.d("TAG", "TAG: KEYCODE_MEDIA_PREVIOUS");
                    commonClass.getService().previousSong();
                    break;
                default:
                    break;
            }
        }
    }

    public String ComponentName() {
        return this.getClass().getName();
    }
}