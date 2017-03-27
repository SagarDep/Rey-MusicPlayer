package com.reyansh.audio.audioplayer.free.AppWidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.RemoteViews;

import com.reyansh.audio.audioplayer.free.Common.CommonClass;
import com.reyansh.audio.audioplayer.free.MusicService.MusicService;
import com.reyansh.audio.audioplayer.free.R;
import com.reyansh.audio.audioplayer.free.Utils.MetaRetriever;

/**
 * Created by REYANSH on 22/05/2016.
 */
public class SmallWidgetProvider extends AppWidgetProvider {
    boolean DEBUG = true;
    String TAG = "SmallWidgetProvider";
    CommonClass mApp;
    private Context mContext;
    final Handler handler = new Handler();

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        mContext = context;
        mApp = (CommonClass) context.getApplicationContext();

        for (int i = 0; i < appWidgetIds.length; i++) {
            int currentAppWidgetId = appWidgetIds[i];
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);

            Intent previousIntent = new Intent();
            previousIntent.setAction("mediaplayer.com.Music_Service.PREVIOUS");

            PendingIntent previousPendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), 0, previousIntent, 0);
            views.setOnClickPendingIntent(R.id.notification_expanded_base_previous, previousPendingIntent);

            Intent playpauseIntent = new Intent();
            playpauseIntent.setAction("mediaplayer.com.Music_Service.PAUSE");
            playpauseIntent.putExtra("isfromSmallWidget", true);
            PendingIntent playpausePendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), 0, playpauseIntent, 0);
            views.setOnClickPendingIntent(R.id.notification_expanded_base_play, playpausePendingIntent);

            Intent intent = new Intent(context, SmallWidgetProvider.class);
            intent.setAction("use_custom_class");
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setOnClickPendingIntent(R.id.notification_expanded_base_play, pendingIntent);

            Intent nextIntent = new Intent();
            nextIntent.setAction("mediaplayer.com.Music_Service.NEXT");
            PendingIntent nextPendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), 0, nextIntent, 0);
            views.setOnClickPendingIntent(R.id.notification_expanded_base_next, nextPendingIntent);

            views.setTextViewText(R.id.notification_expanded_base_line_one, MetaRetriever.getsInstance().getSongName());
            views.setTextViewText(R.id.notification_expanded_base_line_two, MetaRetriever.getsInstance().getAlbumName());
            views.setTextViewText(R.id.notification_expanded_base_line_three, MetaRetriever.getsInstance().getSongArtist());
            if (mApp.isServiceRunning()) {
                if (mApp.getService().getMediaPlayer().isPlaying()) {
                    views.setImageViewResource(R.id.notification_expanded_base_play, R.drawable.btn_playback_pause_light);
                } else {
                    views.setImageViewResource(R.id.notification_expanded_base_play, R.drawable.btn_playback_play_light);
                }
            } else {
                views.setImageViewResource(R.id.notification_expanded_base_play, R.drawable.btn_playback_play_light);
            }
            views.setImageViewBitmap(R.id.notification_expanded_base_image, MetaRetriever.getsInstance().getArtWork());
            views.setImageViewBitmap(R.id.expnotifbg, MetaRetriever.getsInstance().getBlurredArtWorkA(context));
            try {
                appWidgetManager.updateAppWidget(currentAppWidgetId, views);
            } catch (Exception e) {
                continue;
            }
        }
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        String action = intent.getAction();
        String actionName = "use_custom_class";
        if (actionName.equals(action)) {
            if (DEBUG) Log.d(TAG, "yasdfup");
            mContext = context;
            mApp = (CommonClass) context.getApplicationContext();
            if (!mApp.isServiceRunning()) {
                if (DEBUG) Log.d(TAG, "onUpdasdfate");
                mContext.startService(new Intent(mContext, MusicService.class));
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mApp.getService().playPauseSong();
                    }
                }, 625);
            } else {
                mApp.getService().playPauseSong();
            }
        }
    }
}
