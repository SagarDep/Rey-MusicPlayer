package com.reyansh.audio.audioplayer.free.MusicService;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RemoteControlClient;
import android.media.audiofx.PresetReverb;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.reyansh.audio.audioplayer.free.Album.AlbumTracks.AlbumTracksActivity;
import com.reyansh.audio.audioplayer.free.AppWidget.SmallWidgetProvider;
import com.reyansh.audio.audioplayer.free.Artist.ArtistAlbum.ArtistTracksActivity;
import com.reyansh.audio.audioplayer.free.BroadcastReceiver.HeadsetNotificationBroadcast;
import com.reyansh.audio.audioplayer.free.BroadcastReceiver.HeadsetPlugBroadcastReceiver;
import com.reyansh.audio.audioplayer.free.Common.CommonClass;
import com.reyansh.audio.audioplayer.free.Equalizer.EqualizerHelper;
import com.reyansh.audio.audioplayer.free.Genres.GenresAlbum.GenresTracksActivity;
import com.reyansh.audio.audioplayer.free.MainActivity.Main;
import com.reyansh.audio.audioplayer.free.NowPlaying.NowPlaying;
import com.reyansh.audio.audioplayer.free.PlayList.PlaylistTracks.PlaylistTracksActivity;
import com.reyansh.audio.audioplayer.free.R;
import com.reyansh.audio.audioplayer.free.Search.SearchActivity;
import com.reyansh.audio.audioplayer.free.Setting.SettingsActivity;
import com.reyansh.audio.audioplayer.free.Utils.AudioManagerHelper;
import com.reyansh.audio.audioplayer.free.Utils.MetaRetriever;
import com.reyansh.audio.audioplayer.free.Utils.MusicUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

public class MusicService extends Service implements SensorListener, MusicUtils.names, MediaPlayer.OnErrorListener {
    String TAG = "MusicService";
    boolean DEBUG = true;

    public static final String BROADCAST_SEEKBARPROGRESS = "com.example.reyansh.musicplayer.SEEK_PROGRESS";
    public static final String BROADCAST_SONGPATH = "com.example.reyansh.musicplayer.SONG_PATH";

    private static final int FORCE_THRESHOLD = 1000;
    private static final int TIME_THRESHOLD = 100;
    private static final int SHAKE_TIMEOUT = 500;
    private static final int SHAKE_DURATION = 1000;
    private static final int SHAKE_COUNT = 3;

    private static final String ACTION_STOP = "mediaplayer.com.Music_Service.STOP";
    private static final String ACTION_NEXT = "mediaplayer.com.Music_Service.NEXT";
    private static final String ACTION_PREVIOUS = "mediaplayer.com.Music_Service.PREVIOUS";
    private static final String ACTION_PAUSE = "mediaplayer.com.Music_Service.PAUSE";

    private ArrayList<HashMap<String, String>> mListSongs = new ArrayList<HashMap<String, String>>();
    private Uri mSongUri;
    private int SONG_POS = -1;
    public static int NOTIFICATION_ID = 11;
    private final Handler handler = new Handler();
    private Bundle mBundle;
    private Intent mSeekBarIntent;
    private Intent mSongsPathIntent;
    private Intent intent;
    private Context mContext;
    private MediaPlayer mMediaPlayer;
    private WakeLock mWakeLock;

    //  public static int mState = 0;
    private SensorManager sensorMgr;
    private float mLastX = -1.0f, mLastY = -1.0f, mLastZ = -1.0f;
    private long mLastTime;
    private int mShakeCount = 0;
    private long mLastShake;
    private EqualizerHelper mEqualizerHelper;
    private long mLastForce;
    private AudioManager mAudioManager;
    private AudioManagerHelper mAudioManagerHelper;
    private TelephonyManager mTelephoneManger;
    private Handler mHandler;
    private CommonClass mApp;
    private boolean notification = true;
    private Intent ooo;
    private NotificationCompat.Builder mNotificationBuilder;
    private ComponentName remoteComponentName;
    private RemoteControlClient mRemoteControlClient;
    private boolean first = true;
    private boolean mFirstCallIdle = true;
    HeadsetNotificationBroadcast mHeadsetNotificationBroadcast;

    //Headset plug receiver.
    private HeadsetPlugBroadcastReceiver mHeadsetPlugReceiver;
    Service mService;
    MediaSessionCompat mMediaSessionCompat;
    MediaControllerCompat.TransportControls mTransportController;

    //A-B Repeat variables.
    private int mRepeatSongRangePointA = 0;
    private int mRepeatSongRangePointB = 0;
    private MediaSessionCompat mediaSession;
    private ArrayList<HashMap<String, String>> shuffled = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        if (DEBUG)
            Log.d(TAG, "onCreate");
        mContext = this;
        mService = this;
        mApp = (CommonClass) getApplicationContext();
        mApp.setIsServiceRunning(true);
        if (mApp.getPreferencesUtility().getSongQueue() != null) {
            if (mApp.getPreferencesUtility().getSongQueue().size() != 0) {
                mListSongs = mApp.getPreferencesUtility().getSongQueue();
                SONG_POS = Integer.parseInt(mApp.getPreferencesUtility().getSongPos());
            }
        }

        mSongsPathIntent = new Intent(BROADCAST_SONGPATH);
        registerHeadsetPlugReceiver();
        initPlayer();
        initAudioFX();
        mWakeLock = ((PowerManager) getSystemService(Context.POWER_SERVICE)).newWakeLock(1, getClass().getName());
        mWakeLock.setReferenceCounted(false);
        ooo = new Intent("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        if (mApp.getPreferencesUtility().isEqActive()) applyMediaPlayerEQ();
        mBundle = new Bundle();
        intent = new Intent();
        intent.setAction("com.android.music.metachanged");
        registerReceiver(seekchageReceiver, new IntentFilter(NowPlaying.BROADCAST_SEEKBAR));
        mApp.setService(this);
        mSeekBarIntent = new Intent(BROADCAST_SEEKBARPROGRESS);
        mHandler = new Handler();
        mHeadsetNotificationBroadcast = new HeadsetNotificationBroadcast();
        registerReceiver(mHeadsetNotificationBroadcast, new IntentFilter(Intent.ACTION_MEDIA_BUTTON));
        mTelephoneManger = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        mAudioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        sensorMgr = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorMgr.registerListener(this, SensorManager.SENSOR_ACCELEROMETER, SensorManager.SENSOR_DELAY_GAME);
        mAudioManagerHelper = new AudioManagerHelper();
        registerRemoteClient();
        //setupMediaSession();
        mTelephoneManger.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        setupHandler();
        updateSongsInfo(SONG_POS);
        UpdateMetadata();
    }

    public void mediaSession() {
        super.onCreate();
        ComponentName receiver = new ComponentName(getPackageName(), MusicService.class.getName());
        mediaSession = new MediaSessionCompat(this, "PlayerService", receiver, null);
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        mediaSession.setPlaybackState(new PlaybackStateCompat.Builder()
                .setState(PlaybackStateCompat.STATE_PAUSED, 0, 0)
                .setActions(PlaybackStateCompat.ACTION_PLAY_PAUSE)
                .build());
        mediaSession.setMetadata(new MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, "Test Artist")
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, "Test Album")
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, "Test Track Name")
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, 10000)
                .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART,
                        BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .build());

        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.requestAudioFocus(new AudioManager.OnAudioFocusChangeListener() {
            @Override
            public void onAudioFocusChange(int focusChange) {
                // Ignore
            }
        }, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        mediaSession.setActive(true);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (DEBUG) Log.d(TAG, "onStartCommand");
        return START_NOT_STICKY;
    }

    private PhoneStateListener phoneStateListener = new PhoneStateListener() {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    if (mMediaPlayer.isPlaying())
                        mMediaPlayer.pause();
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    if (mMediaPlayer.isPlaying())
                        mMediaPlayer.pause();
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    if (!mMediaPlayer.isPlaying()) {
                        if (mFirstCallIdle) {
                            mFirstCallIdle = false;
                        } else {
                            mMediaPlayer.start();
                            updateNotifs();
                        }
                    }
                    break;
            }
            super.onCallStateChanged(state, incomingNumber);
        }
    };

    public MediaPlayer getMediaPlayer() {
        return mMediaPlayer;
    }

    private boolean requestAudioFocus() {
        int result = mAudioManager.requestAudioFocus(audioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        if (result != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            Toast.makeText(getApplicationContext(), R.string.unable_to_get_audio_focus, Toast.LENGTH_LONG).show();
            return false;
        } else {
            return true;
        }
    }


    private void setupHandler() {
        handler.removeCallbacks(sendUpdatesToUI);
        handler.postDelayed(sendUpdatesToUI, 500);
    }


    public void Add(HashMap<String, String> f, int ok) {
        switch (ok) {
            case 1:
                if (SONG_POS != -1)
                    mListSongs.add(SONG_POS + 1, f);
                break;
            case 2:
                if (mListSongs.size() != -1)
                    mListSongs.add(f);
                break;
            default:
                break;
        }
    }

    public void addtoqueue(ArrayList<HashMap<String, String>> f) {
        if (mListSongs.size() != -1)
            mListSongs.addAll(f);
    }

    public void playNext(ArrayList<HashMap<String, String>> f) {
        if (mListSongs.size() != -1)
            mListSongs.addAll(SONG_POS + 1, f);
    }

    public void headsetDisconnected() {
        if (mApp.getPreferencesUtility().getPauseOnDisconnect())
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.pause();
                updateNotification();
            }
    }

    public void headsetisConnected() {
        if (mApp.getPreferencesUtility().getPlayOnDisconnect())
            if (!mMediaPlayer.isPlaying() && !first) {
                mMediaPlayer.start();
                updateNotification();
            }
    }

    private void logMediaPosition() {
        if (mMediaPlayer.isPlaying()) {
            long mediaPosition = mMediaPlayer.getCurrentPosition();
            long mediaMax = mMediaPlayer.getDuration();
            mSeekBarIntent.putExtra("counter", String.valueOf(mediaPosition));
            mSeekBarIntent.putExtra("mediamax", String.valueOf(mediaMax));
            mBundle.putLong("position", mMediaPlayer.getCurrentPosition());
           /* intent.putExtras(mBundle);
            sendBroadcast(intent);*/
            sendBroadcast(mSeekBarIntent);
        }
    }


    MediaPlayer.OnCompletionListener onComplete = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            if (DEBUG) Log.d(TAG, "onCompletion");
            mMediaPlayer.reset();
            mWakeLock.acquire(30000L);
            oneSongdone();
        }
    };

    private void oneSongdone() {
        if (mApp.getPreferencesUtility().getShuffleMode() == false) {
            if (mApp.getPreferencesUtility().getReateMode() == 0) {
                if (SONG_POS < mListSongs.size() - 1) {
                    SONG_POS++;
                    startSong(getUri(Long.parseLong(mListSongs.get(SONG_POS).get(SONG_ID))));
                } else {
                    mMediaPlayer.seekTo(0);
                }
            } else if (mApp.getPreferencesUtility().getReateMode() == 1) {
                startSong(getUri(Long.parseLong(mListSongs.get(SONG_POS).get(SONG_ID))));
            } else if (mApp.getPreferencesUtility().getReateMode() == 2) {
                if (SONG_POS < mListSongs.size() - 1) {
                    SONG_POS++;
                    startSong(getUri(Long.parseLong(mListSongs.get(SONG_POS).get(SONG_ID))));
                } else {
                    SONG_POS = 0;
                    startSong(getUri(Long.parseLong(mListSongs.get(SONG_POS).get(SONG_ID))));
                }
            }
        } else {
            Random rand = new Random();
            SONG_POS = rand.nextInt((mListSongs.size() - 1) - 0 + 1) + 0;
            startSong(getUri(Long.parseLong(mListSongs.get(SONG_POS).get(SONG_ID))));
        }
    }

    public void setShuffled() {
        if (mApp.getPreferencesUtility().getShuffleMode() == false) {
            mListSongs.clear();
            if (mListSongs.size() == 0) {
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mListSongs.addAll(shuffled);
                    }
                }, 550);
            }
        } else {
            Collections.shuffle(mListSongs, new Random(System.nanoTime()));
            Collections.shuffle(mListSongs, new Random(System.nanoTime()));
        }
    }

    private Uri getUri(long AudioID) {
        return ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, AudioID);
    }

    public EqualizerHelper getEqualizerHelper() {
        return mEqualizerHelper;
    }

    private void initAudioFX() {
        try {
            mEqualizerHelper = new EqualizerHelper(mContext, mMediaPlayer.getAudioSessionId(), mApp.getPreferencesUtility().isEqActive());
        } catch (UnsupportedOperationException e) {
            e.printStackTrace();
            //    mEqualizerHelper.setIsEqualizerSupported(false);
        } catch (Exception e) {
            e.printStackTrace();
            //  mEqualizerHelper.setIsEqualizerSupported(false);
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        switch (what) {
            case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
                Toast.makeText(this, "MEDIA ERROR NOT VALID FOR PROGRESSIVE PLAYBACK " + extra, Toast.LENGTH_SHORT).show();
                break;
            case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                Toast.makeText(this, "MEDIA ERROR SERVER DIED " + extra, Toast.LENGTH_SHORT).show();
                break;
            case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                Toast.makeText(this, "MEDIA ERROR UNKNOWN " + extra, Toast.LENGTH_SHORT).show();
                break;
            case MediaPlayer.MEDIA_ERROR_IO:
                Toast.makeText(this, "MEDIA_ERROR_IO " + extra, Toast.LENGTH_SHORT).show();
                break;
            case MediaPlayer.MEDIA_ERROR_UNSUPPORTED:
                Toast.makeText(this, "MEDIA ERROR UNKNOWN " + extra, Toast.LENGTH_SHORT).show();
                break;
            case MediaPlayer.MEDIA_ERROR_MALFORMED:
                Toast.makeText(this, "MEDIA ERROR UNKNOWN " + extra, Toast.LENGTH_SHORT).show();
                break;
            case MediaPlayer.MEDIA_INFO_UNSUPPORTED_SUBTITLE:
                Toast.makeText(this, "MEDIA ERROR UNKNOWN " + extra, Toast.LENGTH_SHORT).show();
                break;
            default:
                Log.d("MultiPlayer", "Error: " + what + "," + extra);
                break;
        }
        return false;
    }

    MediaPlayer.OnPreparedListener onPrepared = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            Log.d(TAG, "onPrepared");
            mMediaPlayer.setOnCompletionListener(onComplete);
            if (first) {
                mMediaPlayer.seekTo(mApp.getPreferencesUtility().getSeekBarPosition());
                first = false;
            } else {
                mMediaPlayer.start();
                updateNotifs();
            }
//            ooo.putExtra("playpause", "as");
//            sendBroadcast(ooo);
        }
    };

    private void initPlayer() {
        if (DEBUG) Log.d(TAG, "initPlayer");
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setWakeMode(this, 1);
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        if (mListSongs.size() == 0) return;
        startSong(getUri(Long.parseLong(mListSongs.get(SONG_POS).get(SONG_ID))));
    }

    private void applyMediaPlayerEQ() {
        if (mEqualizerHelper == null) return;

        short fiftyHertzBand = mEqualizerHelper.getEqualizer().getBand(50000);
        short oneThirtyHertzBand = mEqualizerHelper.getEqualizer().getBand(130000);
        short threeTwentyHertzBand = mEqualizerHelper.getEqualizer().getBand(320000);
        short eightHundredHertzBand = mEqualizerHelper.getEqualizer().getBand(800000);
        short twoKilohertzBand = mEqualizerHelper.getEqualizer().getBand(2000000);
        short fiveKilohertzBand = mEqualizerHelper.getEqualizer().getBand(5000000);
        short twelvePointFiveKilohertzBand = mEqualizerHelper.getEqualizer().getBand(9000000);

        int[] eqValues = mApp.getDBAccessHelper().getEQForPreset("default");


        for (int d : eqValues) {
            Log.d(TAG, "" + d);

        }

        //50Hz Band.
        if (eqValues[0] == 16) {
            mEqualizerHelper.getEqualizer().setBandLevel(fiftyHertzBand, (short) 0);
        } else if (eqValues[0] < 16) {
            if (eqValues[0] == 0) {
                mEqualizerHelper.getEqualizer().setBandLevel(fiftyHertzBand, (short) -1500);
            } else {
                mEqualizerHelper.getEqualizer().setBandLevel(fiftyHertzBand, (short) (-(16 - eqValues[0]) * 100));
            }

        } else if (eqValues[0] > 16) {
            mEqualizerHelper.getEqualizer().setBandLevel(fiftyHertzBand, (short) ((eqValues[0] - 16) * 100));
        }

        //130Hz Band.
        if (eqValues[1] == 16) {
            mEqualizerHelper.getEqualizer().setBandLevel(oneThirtyHertzBand, (short) 0);
        } else if (eqValues[1] < 16) {

            if (eqValues[1] == 0) {
                mEqualizerHelper.getEqualizer().setBandLevel(oneThirtyHertzBand, (short) -1500);
            } else {
                mEqualizerHelper.getEqualizer().setBandLevel(oneThirtyHertzBand, (short) (-(16 - eqValues[1]) * 100));
            }

        } else if (eqValues[1] > 16) {
            mEqualizerHelper.getEqualizer().setBandLevel(oneThirtyHertzBand, (short) ((eqValues[1] - 16) * 100));
        }

        //320Hz Band.
        if (eqValues[2] == 16) {
            mEqualizerHelper.getEqualizer().setBandLevel(threeTwentyHertzBand, (short) 0);
        } else if (eqValues[2] < 16) {

            if (eqValues[2] == 0) {
                mEqualizerHelper.getEqualizer().setBandLevel(threeTwentyHertzBand, (short) -1500);
            } else {
                mEqualizerHelper.getEqualizer().setBandLevel(threeTwentyHertzBand, (short) (-(16 - eqValues[2]) * 100));
            }

        } else if (eqValues[2] > 16) {
            mEqualizerHelper.getEqualizer().setBandLevel(threeTwentyHertzBand, (short) ((eqValues[2] - 16) * 100));
        }

        //800Hz Band.
        if (eqValues[3] == 16) {
            mEqualizerHelper.getEqualizer().setBandLevel(eightHundredHertzBand, (short) 0);
        } else if (eqValues[3] < 16) {

            if (eqValues[3] == 0) {
                mEqualizerHelper.getEqualizer().setBandLevel(eightHundredHertzBand, (short) -1500);
            } else {
                mEqualizerHelper.getEqualizer().setBandLevel(eightHundredHertzBand, (short) (-(16 - eqValues[3]) * 100));
            }

        } else if (eqValues[3] > 16) {
            mEqualizerHelper.getEqualizer().setBandLevel(eightHundredHertzBand, (short) ((eqValues[3] - 16) * 100));
        }

        //2kHz Band.
        if (eqValues[4] == 16) {
            mEqualizerHelper.getEqualizer().setBandLevel(twoKilohertzBand, (short) 0);
        } else if (eqValues[4] < 16) {

            if (eqValues[4] == 0) {
                mEqualizerHelper.getEqualizer().setBandLevel(twoKilohertzBand, (short) -1500);
            } else {
                mEqualizerHelper.getEqualizer().setBandLevel(twoKilohertzBand, (short) (-(16 - eqValues[4]) * 100));
            }

        } else if (eqValues[4] > 16) {
            mEqualizerHelper.getEqualizer().setBandLevel(twoKilohertzBand, (short) ((eqValues[4] - 16) * 100));
        }

        //5kHz Band.
        if (eqValues[5] == 16) {
            mEqualizerHelper.getEqualizer().setBandLevel(fiveKilohertzBand, (short) 0);
        } else if (eqValues[5] < 16) {

            if (eqValues[5] == 0) {
                mEqualizerHelper.getEqualizer().setBandLevel(fiveKilohertzBand, (short) -1500);
            } else {
                mEqualizerHelper.getEqualizer().setBandLevel(fiveKilohertzBand, (short) (-(16 - eqValues[5]) * 100));
            }

        } else if (eqValues[5] > 16) {
            mEqualizerHelper.getEqualizer().setBandLevel(fiveKilohertzBand, (short) ((eqValues[5] - 16) * 100));
        }

        //12.5kHz Band.
        if (eqValues[6] == 16) {
            mEqualizerHelper.getEqualizer().setBandLevel(twelvePointFiveKilohertzBand, (short) 0);
        } else if (eqValues[6] < 16) {

            if (eqValues[6] == 0) {
                mEqualizerHelper.getEqualizer().setBandLevel(twelvePointFiveKilohertzBand, (short) -1500);
            } else {
                mEqualizerHelper.getEqualizer().setBandLevel(twelvePointFiveKilohertzBand, (short) (-(16 - eqValues[6]) * 100));
            }

        } else if (eqValues[6] > 16) {
            mEqualizerHelper.getEqualizer().setBandLevel(twelvePointFiveKilohertzBand, (short) ((eqValues[6] - 16) * 100));
        }

        //Set the audioFX values.
        mEqualizerHelper.getVirtualizer().setStrength((short) eqValues[7]);
        mEqualizerHelper.getBassBoost().setStrength((short) eqValues[8]);

        if (eqValues[9] == 0) {
            mEqualizerHelper.getReverb().setPreset(PresetReverb.PRESET_NONE);
        } else if (eqValues[9] == 1) {
            mEqualizerHelper.getReverb().setPreset(PresetReverb.PRESET_LARGEHALL);
        } else if (eqValues[9] == 2) {
            mEqualizerHelper.getReverb().setPreset(PresetReverb.PRESET_LARGEROOM);
        } else if (eqValues[9] == 3) {
            mEqualizerHelper.getReverb().setPreset(PresetReverb.PRESET_MEDIUMHALL);
        } else if (eqValues[9] == 4) {
            mEqualizerHelper.getReverb().setPreset(PresetReverb.PRESET_MEDIUMROOM);
        } else if (eqValues[9] == 5) {
            mEqualizerHelper.getReverb().setPreset(PresetReverb.PRESET_SMALLROOM);
        } else if (eqValues[9] == 6) {
            mEqualizerHelper.getReverb().setPreset(PresetReverb.PRESET_PLATE);
        } else {
            return;
        }
    }

    private void startSong(final Uri songuri) {
        saveQueue();
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(final Void... unused) {
                mMediaPlayer.reset();
                mSongUri = songuri;
                try {
                    mMediaPlayer.setDataSource(mContext, mSongUri);
                    mMediaPlayer.setOnPreparedListener(onPrepared);
                    mMediaPlayer.prepareAsync();
                    mApp.getDBAccessHelper().insertSongCount(Integer.parseInt(MetaRetriever.getsInstance().getSongId()));
                    mApp.getDBAccessHelper().insertRecentlyPlayer(Integer.parseInt(MetaRetriever.getsInstance().getSongId()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();

        updateSongsInfo(SONG_POS);
        UpdateMetadata();
        //updateWidgest();
    }

    private void updateNotifs() {
        if (notification) {
            startForeground(NOTIFICATION_ID, buildJBNotification());
            notification = false;
        } else {
            updateNotification();
        }
        //updateWidgest();
    }

    private void updateSongsInfo(int pos) {
        if (SONG_POS != -1) mSongsPathIntent.putExtra("posss", SONG_POS);
        sendBroadcast(mSongsPathIntent);
        populateSongs(pos);
    }

    private void populateSongs(int songPos) {
        Bitmap artwork;
        if (songPos != -1) {
            if (songPos != -2) {
                MetaRetriever.getsInstance().setsongId(String.valueOf(Long.valueOf(mListSongs.get(SONG_POS).get(SONG_ID))));
                MetaRetriever.getsInstance().setSongName(mListSongs.get(SONG_POS).get(SONG_NAME));
                MetaRetriever.getsInstance().setAlbumName(mListSongs.get(SONG_POS).get(SONG_ALBUM));
                MetaRetriever.getsInstance().setSongArtist(mListSongs.get(SONG_POS).get(SONG_ARTIST));
                MetaRetriever.getsInstance().setAlbum_id(mListSongs.get(SONG_POS).get(SONG_ALBUM_ID));
                MetaRetriever.getsInstance().setSong_Duration(mListSongs.get(SONG_POS).get(SONG_DURATION));
                MetaRetriever.getsInstance().setSongPath(mListSongs.get(SONG_POS).get(SONG_PATH));
                artwork = ImageLoader.getInstance(). loadImageSync(MusicUtils.getAlbumArtUri(Long.parseLong(mListSongs.get(SONG_POS).get(SONG_ALBUM_ID))).toString());
                if (artwork == null)
                    artwork = ImageLoader.getInstance().loadImageSync("drawable://" + R.drawable.album_art_6);
                MetaRetriever.getsInstance().setArtWork(artwork);
            }
        }
    }

    public void updateWidgest() {
        Intent smallWidgetIntent = new Intent(mContext, SmallWidgetProvider.class);
        smallWidgetIntent.setAction("android.appwidget.action.APPWIDGET_UPDATE");
        int smallWidgetIds[] = AppWidgetManager.getInstance(mContext).getAppWidgetIds(new ComponentName(mContext, SmallWidgetProvider.class));
        smallWidgetIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, smallWidgetIds);
        mContext.sendBroadcast(smallWidgetIntent);
    }

    public void stopNotify() {
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(NOTIFICATION_ID);
        stopForeground(true);
    }

    public void playPauseSong() {
        if (DEBUG) Log.d(TAG, "playPauseSong");
        mAudioManagerHelper.setHasAudioFocus(requestAudioFocus());
        if (mMediaPlayer.isPlaying() && requestAudioFocus()) {
            mMediaPlayer.pause();
        } else {
            mMediaPlayer.start();
        }
        updateNotifs();
    }


    public void saveQueue() {
        if (SONG_POS == -1)
            return;
        mApp.getPreferencesUtility().setSongQueue(mListSongs);
        mApp.getPreferencesUtility().setSongPos(SONG_POS);
    }

    public void syncLyrics() {
        mBundle.putString("track", MetaRetriever.getsInstance().getSongName());
        mBundle.putString("artist", MetaRetriever.getsInstance().getSongArtist());
        mBundle.putString("album", MetaRetriever.getsInstance().getAlbumName());
        mBundle.putLong("duration", Long.parseLong(MetaRetriever.getsInstance().getSong_Duration()));
        mBundle.putLong("position", mMediaPlayer.getCurrentPosition());
        mBundle.putBoolean("playing", true);
        mBundle.putString("scrobbling_source", "com.reyansh.audio.audioplayer.free");
        intent.putExtras(mBundle);
        sendBroadcast(intent);
    }

    @SuppressLint("NewApi")
    private void registerRemoteClient() {
        remoteComponentName = new ComponentName(getApplicationContext(), new HeadsetNotificationBroadcast().ComponentName());
        try {
            if (mRemoteControlClient == null) {
                mAudioManager.registerMediaButtonEventReceiver(remoteComponentName);
                Intent mediaButtonIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
                mediaButtonIntent.setComponent(remoteComponentName);
                PendingIntent mediaPendingIntent = PendingIntent.getBroadcast(this, 0, mediaButtonIntent, 0);
                mRemoteControlClient = new RemoteControlClient(mediaPendingIntent);
                mAudioManager.registerRemoteControlClient(mRemoteControlClient);
            }
            mRemoteControlClient.setTransportControlFlags(mRemoteControlClient.FLAG_KEY_MEDIA_PLAY | mRemoteControlClient.FLAG_KEY_MEDIA_PAUSE |
                    mRemoteControlClient.FLAG_KEY_MEDIA_PLAY_PAUSE |
                    mRemoteControlClient.FLAG_KEY_MEDIA_STOP |
                    mRemoteControlClient.FLAG_KEY_MEDIA_PREVIOUS |
                    mRemoteControlClient.FLAG_KEY_MEDIA_NEXT);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void setupMediaSession() {
    /* Activate Audio Manager */
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mAudioManager.requestAudioFocus(audioFocusChangeListener, AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN);

        ComponentName mRemoteControlResponder = new ComponentName(getPackageName(),
                MediaButtonReceiver.class.getName());
        final Intent mediaButtonIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
        mediaButtonIntent.setComponent(mRemoteControlResponder);
        mMediaSessionCompat = new MediaSessionCompat(getApplication(), "JairSession", mRemoteControlResponder, null);
        mMediaSessionCompat.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        PlaybackStateCompat playbackStateCompat = new PlaybackStateCompat.Builder()
                .setActions(
                        PlaybackStateCompat.ACTION_SEEK_TO |
                                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                                PlaybackStateCompat.ACTION_SKIP_TO_NEXT |
                                PlaybackStateCompat.ACTION_PLAY |
                                PlaybackStateCompat.ACTION_PAUSE |
                                PlaybackStateCompat.ACTION_STOP
                )
                .setState(mMediaPlayer.isPlaying() ? PlaybackStateCompat.STATE_PLAYING : PlaybackStateCompat.STATE_PAUSED,
                        getCurrentSongIndex(),
                        1.0f)
                .build();
        mMediaSessionCompat.setPlaybackState(playbackStateCompat);
        mMediaSessionCompat.setCallback(mMediaSessionCallback);
        // mMediaSessionCompat.setSessionActivity(retrievePlaybackActions(5));
        mMediaSessionCompat.setActive(true);
        updateMediaSessionMetaData();
        mTransportController = mMediaSessionCompat.getController().getTransportControls();
    }

    private final MediaSessionCompat.Callback mMediaSessionCallback = new MediaSessionCompat.Callback() {
        MediaControllerCompat.TransportControls mTransportController;

        @Override
        public boolean onMediaButtonEvent(Intent mediaButtonEvent) {
            final String intentAction = mediaButtonEvent.getAction();
            if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(intentAction)) {
//                if (PrefUtils.isHeadsetPause(getBaseContext())) {
//                    Log.d(LOG_TAG, "Headset disconnected");
//                    pause();
//                }
            } else if (Intent.ACTION_MEDIA_BUTTON.equals(intentAction)) {
                final KeyEvent event = mediaButtonEvent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
                if (event == null) return super.onMediaButtonEvent(mediaButtonEvent);
                final int keycode = event.getKeyCode();
                final int action = event.getAction();
                final long eventTime = event.getEventTime();
                if (event.getRepeatCount() == 0 && action == KeyEvent.ACTION_DOWN) {
                    switch (keycode) {
                        case KeyEvent.KEYCODE_HEADSETHOOK:
//                            if (eventTime - mLastClickTime < DOUBLE_CLICK) {
//                                playNext(mSongNumber);
//                                mLastClickTime = 0;
//                            } else {
//                                if (isPlaying())
//                                    pause();
//                                else resume();
//                                mLastClickTime = eventTime;
//                            }
                            break;
                        case KeyEvent.KEYCODE_MEDIA_STOP:
                            //    mTransportController.stop();
                            break;
                        case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
//                            if (isMediaPlayerActive()) {
//                                if (isPlaying()) mTransportController.pause();
//                                else mTransportController.play();
//                            }
                            break;
                        case KeyEvent.KEYCODE_MEDIA_NEXT:
                            // mTransportController.skipToNext();
                            break;
                        case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                            //mTransportController.skipToPrevious();
                            break;
                        case KeyEvent.KEYCODE_MEDIA_PAUSE:
                            //mTransportController.pause();
                            break;
                        case KeyEvent.KEYCODE_MEDIA_PLAY:
                            //mTransportController.play();
                            break;
                    }
                }
            }
            return super.onMediaButtonEvent(mediaButtonEvent);
        }

        @Override
        public void onPlay() {
            super.onPlay();
            mMediaPlayer.start();
        }

        @Override
        public void onPause() {
            super.onPause();
            mMediaPlayer.pause();
        }

        @Override
        public void onSkipToNext() {
            super.onSkipToNext();
            nextSong();
        }

        @Override
        public void onSkipToPrevious() {
            super.onSkipToPrevious();
            previousSong();
        }

        @Override
        public void onSeekTo(long pos) {
            super.onSeekTo(pos);
            mMediaPlayer.seekTo((int) pos);
        }

        @Override
        public void onStop() {
            super.onStop();
            mMediaPlayer.pause();
            //commitMusicData();
            //updatePlayingUI(STOP_ACTION);
            stopSelf();
        }
    };

    private void updateMediaSessionMetaData() {
        MediaMetadataCompat.Builder builder = new MediaMetadataCompat.Builder();
        builder.putString(MediaMetadataCompat.METADATA_KEY_ARTIST, MetaRetriever.getsInstance().getSongArtist());
        builder.putString(MediaMetadataCompat.METADATA_KEY_ALBUM, MetaRetriever.getsInstance().getAlbumName());
        builder.putString(MediaMetadataCompat.METADATA_KEY_TITLE, MetaRetriever.getsInstance().getSongName());
        builder.putLong(MediaMetadataCompat.METADATA_KEY_DURATION, mMediaPlayer.getDuration());
        builder.putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, MetaRetriever.getsInstance().getArtWork());
        mMediaSessionCompat.setMetadata(builder.build());
    }

    private void UpdateMetadata() {
        if (mRemoteControlClient == null) return;
        RemoteControlClient.MetadataEditor metadataEditor = mRemoteControlClient.editMetadata(true);
        if (mApp.getPreferencesUtility().getLockScreenoption())
            metadataEditor.putBitmap(RemoteControlClient.MetadataEditor.BITMAP_KEY_ARTWORK, MetaRetriever.getsInstance().getBlurredArtWorkA(mContext));
        else
            metadataEditor.putBitmap(RemoteControlClient.MetadataEditor.BITMAP_KEY_ARTWORK, MetaRetriever.getsInstance().getArtWork());
        metadataEditor.apply();
    }

    public void stopSong() {
      updateWidgest();
        mApp.getPreferencesUtility().setSeekBarPosition(mMediaPlayer.getCurrentPosition());
        saveQueue();
        stopNotify();
        mMediaPlayer.stop();
        unregisterReceiver(seekchageReceiver);
        unregisterReceiver(mHeadsetNotificationBroadcast);
        mWakeLock.release();
        if (sensorMgr != null) {
            sensorMgr.unregisterListener(this, SensorManager.SENSOR_ACCELEROMETER);
            sensorMgr = null;
        }
        if (mMediaPlayer != null)
            mMediaPlayer.release();
        mMediaPlayer = null;
        try {
            mEqualizerHelper.releaseEQObjects();
            mEqualizerHelper = null;
        } catch (Exception e1) {
            e1.printStackTrace();
            mEqualizerHelper = null;
        }
        mAudioManagerHelper.setHasAudioFocus(false);
        mAudioManager.abandonAudioFocus(audioFocusChangeListener);
        mApp.setIsServiceRunning(false);
        stopSelf();
        if (NowPlaying.getInstance() != null)
            NowPlaying.getInstance().finish();
        if (Main.getInstance() != null)
            Main.getInstance().finish();
        if (AlbumTracksActivity.getInstance() != null)
            AlbumTracksActivity.getInstance().finish();
        if (PlaylistTracksActivity.getInstance() != null)
            PlaylistTracksActivity.getInstance().finish();
        if (ArtistTracksActivity.getInstance() != null)
            ArtistTracksActivity.getInstance().finish();
        if (GenresTracksActivity.getInstance() != null)
            GenresTracksActivity.getInstance().finish();
        if (SettingsActivity.getInstance() != null)
            SettingsActivity.getInstance().finish();
        if (SearchActivity.getInstance() != null) {
            SearchActivity.getInstance().finish();
        }
        System.exit(0);
    }

    public void nextSong() {
        if (mListSongs.size() != 1) {
            if (SONG_POS < mListSongs.size() - 1) {
                SONG_POS = SONG_POS + 1;
                startSong(getUri(Long.parseLong(mListSongs.get(SONG_POS).get(SONG_ID))));
            } else {
                SONG_POS = 0;
                startSong(getUri(Long.parseLong(mListSongs.get(SONG_POS).get(SONG_ID))));
            }
        }

    }


    private BroadcastReceiver seekchageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateSeekPos(intent);
        }

        private void updateSeekPos(Intent intent) {
            int seekPos = intent.getIntExtra("seekpos", 0);
            mMediaPlayer.seekTo(seekPos);

        }
    };

    public void previousSong() {
        if (mMediaPlayer.getCurrentPosition() >= 5000) {
            mMediaPlayer.seekTo(0);
        } else {
            if (mListSongs.size() != 1) {
                if (SONG_POS > 0) {
                    SONG_POS--;
                    startSong(getUri(Long.parseLong(mListSongs.get(SONG_POS).get(SONG_ID))));
                } else {
                    SONG_POS = mListSongs.size() - 1;
                    startSong(getUri(Long.parseLong(mListSongs.get(SONG_POS).get(SONG_ID))));
                }
            }

        }
    }

    private Runnable duckUpVolumeRunnable = new Runnable() {

        @Override
        public void run() {
            if (mAudioManagerHelper.getCurrentVolume() < mAudioManagerHelper.getTargetVolume()) {
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                        (mAudioManagerHelper.getCurrentVolume() + mAudioManagerHelper.getStepUpIncrement()),
                        0);

                mAudioManagerHelper.setCurrentVolume(mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
                mHandler.postDelayed(this, 50);
            }
        }

    };
    private Runnable duckDownVolumeRunnable = new Runnable() {

        @Override
        public void run() {
            if (mAudioManagerHelper.getCurrentVolume() > mAudioManagerHelper.getTargetVolume()) {
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                        (mAudioManagerHelper.getCurrentVolume() - mAudioManagerHelper.getStepDownIncrement()),
                        0);

                mAudioManagerHelper.setCurrentVolume(mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
                mHandler.postDelayed(this, 50);
            }

        }

    };

    @Override
    public void onSensorChanged(int sensor, float[] values) {
        if (sensor != SensorManager.SENSOR_ACCELEROMETER) return;
        long now = System.currentTimeMillis();

        if ((now - mLastForce) > SHAKE_TIMEOUT) {
            mShakeCount = 0;
        }

        if ((now - mLastTime) > TIME_THRESHOLD) {
            long diff = now - mLastTime;
            float speed = Math.abs(values[SensorManager.DATA_X] + values[SensorManager.DATA_Y] + values[SensorManager.DATA_Z] - mLastX - mLastY - mLastZ) / diff * 10000;
            if (speed > FORCE_THRESHOLD) {
                if ((++mShakeCount >= SHAKE_COUNT) && (now - mLastShake > SHAKE_DURATION)) {
                    mLastShake = now;
                    mShakeCount = 0;
                    if (mApp.getPreferencesUtility().getShakeCheck() && mListSongs.size() != 0) {
                        nextSong();
                    }
                }
                mLastForce = now;
            }
            mLastTime = now;
            mLastX = values[SensorManager.DATA_X];
            mLastY = values[SensorManager.DATA_Y];
            mLastZ = values[SensorManager.DATA_Z];
        }
    }


    private AudioManager.OnAudioFocusChangeListener audioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
                try {
                    mMediaPlayer.pause();
                    updateNotification();
                    updateWidgest();
                    mAudioManagerHelper.setHasAudioFocus(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
                mAudioManagerHelper.setAudioDucked(true);
                mAudioManagerHelper.setTargetVolume(5);
                mAudioManagerHelper.setStepDownIncrement(1);
                mAudioManagerHelper.setCurrentVolume(mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
                mAudioManagerHelper.setOriginalVolume(mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
                mHandler.post(duckDownVolumeRunnable);

            } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                if (mAudioManagerHelper.isAudioDucked()) {
                    mAudioManagerHelper.setTargetVolume(mAudioManagerHelper.getOriginalVolume());
                    mAudioManagerHelper.setStepUpIncrement(1);
                    mAudioManagerHelper.setCurrentVolume(mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
                    mHandler.post(duckUpVolumeRunnable);
                    mAudioManagerHelper.setAudioDucked(false);
                } else {
                    mAudioManagerHelper.setHasAudioFocus(true);
                }
            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                if (mMediaPlayer != null) {
                    mMediaPlayer.pause();
                    updateNotifs();
                }
                mAudioManagerHelper.setHasAudioFocus(false);
            }

        }

    };

    public void registerHeadsetPlugReceiver() {
        //Register the headset plug receiver.
        IntentFilter filter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
        mHeadsetPlugReceiver = new HeadsetPlugBroadcastReceiver();
        mService.registerReceiver(mHeadsetPlugReceiver, filter);
    }

    /*   private BroadcastReceiver headsetReceiver = new BroadcastReceiver() {
           private boolean headsetConnected = false;
           @Override
           public void onReceive(Context context, Intent intent) {
               if (intent.hasExtra("state")) {
                   if (headsetConnected && intent.getIntExtra("state", 0) == 0) {
                       headsetConnected = false;
                       headsetSwitch = 0;
                   } else if (!headsetConnected && intent.getIntExtra("state", 0) == 1) {
                       headsetConnected = true;
                       headsetSwitch = 1;
                   }
               }
               switch (headsetSwitch) {
                   case (0):
                       headsetDisconnected();
                       break;
                   case (1):
                       headsetisConnected();
                       break;
               }
           }
       };
   */
    private Runnable sendUpdatesToUI = new Runnable() {
        public void run() {
            logMediaPosition();
            handler.postDelayed(this, 500);
        }
    };

    public ArrayList<HashMap<String, String>> getSongList() {
        return mListSongs;
    }

    public void setSongList(ArrayList<HashMap<String, String>> listSong) {
        mListSongs.clear();
        mListSongs.addAll(listSong);
    }

    public void setSelectedSong(int pos, int notification_id) {
        SONG_POS = pos;
        NOTIFICATION_ID = notification_id;
        if (mListSongs.size() != 0)
            startSong(getUri(Long.parseLong(mListSongs.get(SONG_POS).get(SONG_ID))));
        updateNotifs();
    }

    public int getCurrentSongIndex() {
        return SONG_POS;
    }

    public void setCurrentSongIndex(int currentSongIndex) {
        SONG_POS = currentSongIndex;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        mApp.setIsServiceRunning(false);
        handler.removeCallbacks(sendUpdatesToUI);
        //stopNotify();
        unregisterReceiver(mHeadsetPlugReceiver);
        //updateWidgest();
        mApp.getPreferencesUtility().setSeekBarPosition(mMediaPlayer.getCurrentPosition());
        saveQueue();
        mMediaPlayer.stop();
        unregisterReceiver(seekchageReceiver);
        unregisterReceiver(mHeadsetNotificationBroadcast);
        mWakeLock.release();
        if (sensorMgr != null) {
            sensorMgr.unregisterListener(this, SensorManager.SENSOR_ACCELEROMETER);
            sensorMgr = null;
        }
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        try {
            mEqualizerHelper.releaseEQObjects();
            mEqualizerHelper = null;
        } catch (Exception e1) {
            e1.printStackTrace();
            mEqualizerHelper = null;
        }
        mAudioManagerHelper.setHasAudioFocus(false);
        mAudioManager.abandonAudioFocus(audioFocusChangeListener);

        //stopSong();
    }


    @SuppressLint("NewApi")
    private Notification buildJBNotification() {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext());
        Intent intent;
        PendingIntent pendingIntent;
        mNotificationBuilder = new NotificationCompat.Builder(mContext);
        mNotificationBuilder.setOngoing(true);
        mNotificationBuilder.setAutoCancel(false);
        mNotificationBuilder.setSmallIcon(R.mipmap.small_launcher);
        intent = new Intent();

        pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);
        notificationBuilder.setContentIntent(pendingIntent);
        final RemoteViews notificationView = new RemoteViews(mContext.getPackageName(), R.layout.notification_custom_layout);
        final RemoteViews expNotificationView = new RemoteViews(mContext.getPackageName(), R.layout.notification_custom_expanded_layout);

        Intent intentprevious = new Intent(ACTION_PREVIOUS);
        PendingIntent previousTrackPendingIntent = PendingIntent.getBroadcast(mContext, 0, intentprevious, 0);
        notificationView.setOnClickPendingIntent(R.id.notification_base_previous, previousTrackPendingIntent);
        expNotificationView.setOnClickPendingIntent(R.id.notification_base_previous, previousTrackPendingIntent);


        intent = new Intent(ACTION_PAUSE);
        PendingIntent playPauseTrackPendingIntent = PendingIntent.getBroadcast(mContext, 0, intent, 0);
        notificationView.setOnClickPendingIntent(R.id.notification_base_play, playPauseTrackPendingIntent);
        expNotificationView.setOnClickPendingIntent(R.id.notification_base_play, playPauseTrackPendingIntent);

        intent = new Intent(ACTION_NEXT);
        PendingIntent nextTrackPendingIntent = PendingIntent.getBroadcast(mContext, 0, intent, 0);
        notificationView.setOnClickPendingIntent(R.id.notification_base_next, nextTrackPendingIntent);
        expNotificationView.setOnClickPendingIntent(R.id.notification_base_next, nextTrackPendingIntent);

        intent = new Intent(ACTION_STOP);
        PendingIntent stopServicePendingIntent = PendingIntent.getBroadcast(mContext, 0, intent, 0);
        notificationView.setOnClickPendingIntent(R.id.notification_base_collapse, pendingIntent);
        expNotificationView.setOnClickPendingIntent(R.id.notification_base_collapse, pendingIntent);
        intent = new Intent(mContext, NowPlaying.class);
        pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);
        expNotificationView.setOnClickPendingIntent(R.id.notification_base_image, pendingIntent);
        notificationView.setOnClickPendingIntent(R.id.notification_base_image, pendingIntent);
        mNotificationBuilder.setContentIntent(pendingIntent);
        expNotificationView.setTextViewText(R.id.notification_expanded_base_line_one, MetaRetriever.getsInstance().getSongName());
        expNotificationView.setTextViewText(R.id.notification_expanded_base_line_two, MetaRetriever.getsInstance().getSongArtist());
        expNotificationView.setTextViewText(R.id.notification_expanded_base_line_three, MetaRetriever.getsInstance().getAlbumName());
        if (mMediaPlayer.isPlaying()) {
            notificationView.setImageViewResource(R.id.notification_base_play, R.drawable.btn_playback_pause_light);
            expNotificationView.setImageViewResource(R.id.notification_expanded_base_play, R.drawable.btn_playback_pause_light);
        } else {
            notificationView.setImageViewResource(R.id.notification_base_play, R.drawable.btn_playback_play_light);
            expNotificationView.setImageViewResource(R.id.notification_expanded_base_play, R.drawable.btn_playback_play_light);
        }
        expNotificationView.setImageViewBitmap(R.id.notification_expanded_base_image, MetaRetriever.getsInstance().getArtWork());
        notificationView.setImageViewBitmap(R.id.notification_base_image, MetaRetriever.getsInstance().getArtWork());

        expNotificationView.setImageViewBitmap(R.id.notification_bg_image_view, MetaRetriever.getsInstance().getBlurredArtWorkA(mContext));
        notificationView.setImageViewBitmap(R.id.mainbg, MetaRetriever.getsInstance().getBlurredArtWorkA(mContext));

        notificationView.setTextViewText(R.id.notification_base_line_one, MetaRetriever.getsInstance().getSongName());
        notificationView.setTextViewText(R.id.notification_base_line_two, MetaRetriever.getsInstance().getSongArtist());

        expNotificationView.setOnClickPendingIntent(R.id.notification_expanded_base_collapse, stopServicePendingIntent);
        notificationView.setOnClickPendingIntent(R.id.notification_base_collapse, stopServicePendingIntent);

        expNotificationView.setViewVisibility(R.id.notification_expanded_base_previous, View.VISIBLE);
        expNotificationView.setViewVisibility(R.id.notification_expanded_base_next, View.VISIBLE);
        expNotificationView.setOnClickPendingIntent(R.id.notification_expanded_base_play, playPauseTrackPendingIntent);
        expNotificationView.setOnClickPendingIntent(R.id.notification_expanded_base_next, nextTrackPendingIntent);
        expNotificationView.setOnClickPendingIntent(R.id.notification_expanded_base_previous, previousTrackPendingIntent);

        notificationView.setViewVisibility(R.id.notification_base_previous, View.VISIBLE);
        notificationView.setViewVisibility(R.id.notification_base_next, View.VISIBLE);
        notificationView.setOnClickPendingIntent(R.id.notification_base_play, playPauseTrackPendingIntent);
        notificationView.setOnClickPendingIntent(R.id.notification_base_next, nextTrackPendingIntent);
        notificationView.setOnClickPendingIntent(R.id.notification_base_previous, previousTrackPendingIntent);

        mNotificationBuilder.setContent(notificationView);
        Notification notification = mNotificationBuilder.build();
        notification.bigContentView = expNotificationView;
        notification.flags = Notification.FLAG_FOREGROUND_SERVICE | Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;
        return notification;
    }

    public void updateNotification() {
        ooo.putExtra("playpause", "as");
        sendBroadcast(ooo);
        Notification notification = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            notification = buildJBNotification();
        else
            return;
        NotificationManager notifManager = (NotificationManager) mApp.getSystemService(Context.NOTIFICATION_SERVICE);
        notifManager.notify(NOTIFICATION_ID, notification);
    }

    @Override
    public void onAccuracyChanged(int sensor, int accuracy) {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public class PlayerBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    public int getRepeatSongRangePointA() {
        return mRepeatSongRangePointA;
    }

    /**
     * Returns point B in milliseconds for A-B repeat.
     */
    public int getRepeatSongRangePointB() {
        return mRepeatSongRangePointB;
    }

    /**
     * Sets the A-B Repeat song markers.
     *
     * @param pointA The duration to repeat from (in millis).
     * @param pointB The duration to repeat until (in millis).
     */
    public void setRepeatSongRange(int pointA, int pointB) {
        mRepeatSongRangePointA = pointA;
        mRepeatSongRangePointB = pointB;
        getMediaPlayer().seekTo(pointA);
        mHandler.postDelayed(checkABRepeatRange, 100);
    }

    /**
     * Clears the A-B Repeat song markers.
     */
    public void clearABRepeatRange() {
        mHandler.removeCallbacks(checkABRepeatRange);
        mRepeatSongRangePointA = 0;
        mRepeatSongRangePointB = 0;
        mApp.getPreferencesUtility().setRepeateMode(0);
    }

    /**
     * Called repetitively to check for A-B repeat markers.
     */
    private Runnable checkABRepeatRange = new Runnable() {

        @Override
        public void run() {
            try {
                if (getMediaPlayer().isPlaying()) {

                    if (getMediaPlayer().getCurrentPosition() >= (mRepeatSongRangePointB)) {
                        getMediaPlayer().seekTo(mRepeatSongRangePointA);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (mApp.getPreferencesUtility().getReateMode() == 3) {
                mHandler.postDelayed(checkABRepeatRange, 100);
            }

        }

    };
}
