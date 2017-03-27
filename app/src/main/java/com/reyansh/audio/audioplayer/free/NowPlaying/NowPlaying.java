package com.reyansh.audio.audioplayer.free.NowPlaying;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.codetroopers.betterpickers.hmspicker.HmsPickerBuilder;
import com.codetroopers.betterpickers.hmspicker.HmsPickerDialogFragment;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.reyansh.audio.audioplayer.free.Common.CommonClass;
import com.reyansh.audio.audioplayer.free.Dialog.ABRepeatDialog;
import com.reyansh.audio.audioplayer.free.Dialog.PlaylistDialog;
import com.reyansh.audio.audioplayer.free.Equalizer.EqualizerActivity;
import com.reyansh.audio.audioplayer.free.MusicService.MusicService;
import com.reyansh.audio.audioplayer.free.R;
import com.reyansh.audio.audioplayer.free.Setting.SettingsActivity;
import com.reyansh.audio.audioplayer.free.Utils.MetaRetriever;
import com.reyansh.audio.audioplayer.free.Utils.MusicUtils;
import com.reyansh.audio.audioplayer.free.Utils.onSwipeGestureListener;

import java.util.Timer;
import java.util.TimerTask;

public class NowPlaying extends AppCompatActivity implements
        View.OnClickListener, HmsPickerDialogFragment.HmsPickerDialogHandler,
        SeekBar.OnSeekBarChangeListener, MusicUtils.names, MusicUtils.Defs {

    private TextView mTimerDialogTextView;
    private CommonClass mApp;

    private ImageButton mPlayPuaseImageButton;
    private ImageButton mNextImageButton;
    private ImageButton mPreviousImageButton;
    private ImageButton mEqualizerImageButton;
    private ImageButton mContextMenuImageButton;
    private ImageButton mFavoritesImageButton;

    private ImageButton mRepeatButton;
    private ImageButton mShuffleButton;
    private ImageButton mQueueButton;

    private TextView songName;
    private TextView albumName;
    private TextView artistName;
    private TextView songCurrentDurationLabel;
    private TextView songTotalDurationLabel;

    private SeekBar seekBar;
    private TextView duration;
    private int s = 0;

    private ImageView albumArt;
    private LinearLayout rL;
    private String TAG = "Now_Playing";
    private Intent intent;
    private static NowPlaying now_playing;

    public static final String BROADCAST_SEEKBAR = "com.example.reyansh.musicplayer.SEND_SEEKBAR";

    private Context mContext;
    private Handler mTimerHandler;
    private Timer mTimer;
    boolean mTimerIsSet = false;
    private Dialog mTimerDialog;

    private ImageView mBackgroundImageView;
    AdView mAdView;
    private Toolbar mToolbar;
    private TextView mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();
        mApp = (CommonClass) mContext.getApplicationContext();
        if (mApp.getPreferencesUtility().getFullWindow()) {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        setContentView(R.layout.nowplaying);
        now_playing = this;
        mTitle = (TextView) findViewById(R.id.text_view_title);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        if (!mApp.isServiceRunning()) startService(new Intent(mContext, MusicService.class));
        initViews();
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        mTimerDialog = new Dialog(NowPlaying.this);
        mTimerDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mTimerDialog.setContentView(R.layout.dialog_timer);
        mTimerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        mTimerDialogTextView = (TextView) mTimerDialog.findViewById(R.id.text_view_timerdialog);
        intent = new Intent(BROADCAST_SEEKBAR);
        registerReceiver(updateUIBroadcastReceiver, new IntentFilter(MusicService.BROADCAST_SEEKBARPROGRESS));
        registerReceiver(updateSongDetails, new IntentFilter(MusicService.BROADCAST_SONGPATH));
        mTimerHandler = new Handler();
        View view = this.getWindow().getDecorView();
        view.setBackgroundColor(Color.parseColor("#000000"));
        populateUI();
    }

    public static Activity getInstance() {
        return now_playing;
    }

    private void initViews() {
        mPlayPuaseImageButton = (ImageButton) findViewById(R.id.btnPlay);
        mPlayPuaseImageButton.setOnClickListener(this);

        mNextImageButton = (ImageButton) findViewById(R.id.btnForward);
        mNextImageButton.setOnClickListener(this);

        mPreviousImageButton = (ImageButton) findViewById(R.id.btnBackward);
        mPreviousImageButton.setOnClickListener(this);

        seekBar = (SeekBar) findViewById(R.id.seekbar);
        seekBar.setOnSeekBarChangeListener(this);

        duration = (TextView) findViewById(R.id.duration);

        songCurrentDurationLabel = (TextView) findViewById(R.id.songCurrentDurationLabel);
        songTotalDurationLabel = (TextView) findViewById(R.id.songTotalDurationLabel);

        songName = (TextView) findViewById(R.id.text_view_title);
        albumName = (TextView) findViewById(R.id.albumname);

        artistName = (TextView) findViewById(R.id.artistname);
        albumArt = (ImageView) findViewById(R.id.songart);

        rL = (LinearLayout) findViewById(R.id.nowPlaying_layout);
        mBackgroundImageView = (ImageView) findViewById(R.id.image_view_background);
        rL.setOnTouchListener(new onSwipeGestureListener(this) {
            public void onSwipeLeft() {
                mApp.getService().nextSong();
            }

            public void onSwipeRight() {
                mApp.getService().previousSong();
            }
        });
        mRepeatButton = (ImageButton) findViewById(R.id.btnrepeat);
        mRepeatButton.setOnClickListener(this);
        mShuffleButton = (ImageButton) findViewById(R.id.btnshuffle);
        mShuffleButton.setOnClickListener(this);
        mQueueButton = (ImageButton) findViewById(R.id.btnqueue);
        mQueueButton.setOnClickListener(this);
        mEqualizerImageButton = (ImageButton) findViewById(R.id.equalizer);
        mContextMenuImageButton = (ImageButton) findViewById(R.id.menu);
        mContextMenuImageButton.setOnClickListener(menus);
        mFavoritesImageButton = (ImageButton) findViewById(R.id.favorites);
        mFavoritesImageButton.setOnClickListener(this);
        mEqualizerImageButton.setOnClickListener(this);

    }

    View.OnClickListener menus = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            registerForContextMenu(mContextMenuImageButton);
            openContextMenu(mContextMenuImageButton);
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.now_menu, menu);
        SubMenu fileMenu = menu.addSubMenu("Add to playlist");
        MusicUtils.overflowsubmenu(getApplication(), fileMenu);
        menu.add(Menu.NONE, 6, Menu.NONE, "Settings");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_set_timer:
                setTmer();
                return true;
            case R.id.item_share_item:
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                Uri uri = Uri.parse(MetaRetriever.getsInstance().getSongPath());
                sharingIntent.setType("*/*");
                sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);
                startActivity(Intent.createChooser(sharingIntent, "Share Track Using"));
                return true;
            case R.id.item_a_b_repeat:
                ABRepeatDialog dialog = new ABRepeatDialog(NowPlaying.this);
                dialog.show();
                WindowManager.LayoutParams lap = dialog.getWindow().getAttributes();
                lap.dimAmount = 0.5f;
                dialog.getWindow().setAttributes(lap);
                dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
                return true;
            case 6:
                startActivity(new Intent(mContext, SettingsActivity.class));
                return true;
            case NEW_PLAYLIST:
                PlaylistDialog b = new PlaylistDialog(this, new long[]{Long.parseLong(MetaRetriever.getsInstance().getSongId())});
                b.show();
                WindowManager.LayoutParams lp = b.getWindow().getAttributes();
                ((Button) b.findViewById(R.id.create)).setTypeface(mApp.getStripTitleTypeFace());
                ((Button) b.findViewById(R.id.cancel)).setTypeface(mApp.getStripTitleTypeFace());
                lp.dimAmount = 0.5f;
                b.getWindow().setAttributes(lp);
                b.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
                return true;
            case PLAYLIST_SELECTED:
                long[] list = new long[]{Long.parseLong(MetaRetriever.getsInstance().getSongId())};
                long playlist = item.getIntent().getLongExtra("playlist", 0);
                MusicUtils.addToPlaylist(this, list, playlist);
                return true;

        }
        return super.onOptionsItemSelected(item);

    }

    public void setTmer() {
        if (mTimerIsSet) {
            mTimerDialog.show();
            ((Button) mTimerDialog.findViewById(R.id.btn_ok_timerdialog)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mTimerDialog.dismiss();
                }
            });
            ((Button) mTimerDialog.findViewById(R.id.btn_cancel_timerdialog)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTimer != null) {
                        mTimer.cancel();
                        mTimer = null;
                        mHandler.removeCallbacks(pausetimer);
                        mTimerIsSet = false;
                        mTimerDialog.dismiss();
                    }
                }
            });
        } else {
            HmsPickerBuilder hpb = new HmsPickerBuilder()
                    .setFragmentManager(getSupportFragmentManager())
                    .setStyleResId(R.style.MyCustomBetterPickerTheme);
            hpb.show();
        }

    }

    //    public void putRotateAnim(ImageView imageView) {
    //     Animation firstAnimation = AnimationUtils.loadAnimation(this, R.anim.imageinout);
    //        firstAnimation.setRepeatCount(Animation.INFINITE);
//        firstAnimation.setRepeatMode(Animation.INFINITE);
//        imageView.startAnimation(firstAnimation);
//    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.add(0, SET_TIMER, 0, R.string.setimer);
        menu.add(0, SHARE_ITEM, 0, R.string.share_item);
        SubMenu sub = menu.addSubMenu(0, ADD_TO_PLAYLIST, 0, R.string.add_to_playlist);
        MusicUtils.makePlaylistMenu(this, sub, 0);
        menu.add(0, A_B_REPEAT, 0, R.string.abrepeat);
        menu.add(0, GOTO_SETTINGS, 0, R.string.settings);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case NEW_PLAYLIST:
                PlaylistDialog b = new PlaylistDialog(this, new long[]{Long.parseLong(MetaRetriever.getsInstance().getSongId())});
                b.show();
                WindowManager.LayoutParams lp = b.getWindow().getAttributes();
                lp.dimAmount = 0.5f;
                b.getWindow().setAttributes(lp);
                b.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
                return true;
            case PLAYLIST_SELECTED:
                long[] list = new long[]{Long.parseLong(MetaRetriever.getsInstance().getSongId())};
                long playlist = item.getIntent().getLongExtra("playlist", 0);
                MusicUtils.addToPlaylist(this, list, playlist);
                return true;
            case GOTO_SETTINGS:
                startActivity(new Intent(mContext, SettingsActivity.class));
                break;
            case SHARE_ITEM:
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                Uri uri = Uri.parse(MetaRetriever.getsInstance().getSongPath());
                sharingIntent.setType("*/*");
                sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);
                startActivity(Intent.createChooser(sharingIntent, "Share Track Using"));
                break;
            case SET_TIMER:
                if (mTimerIsSet) {
                    mTimerDialog.show();
                    ((Button) mTimerDialog.findViewById(R.id.btn_ok_timerdialog)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mTimerDialog.dismiss();
                        }
                    });
                    ((Button) mTimerDialog.findViewById(R.id.btn_cancel_timerdialog)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mTimer != null) {
                                mTimer.cancel();
                                mTimer = null;
                                mHandler.removeCallbacks(pausetimer);
                                mTimerIsSet = false;
                                mTimerDialog.dismiss();
                            }
                        }
                    });
                } else {
                    HmsPickerBuilder hpb = new HmsPickerBuilder()
                            .setFragmentManager(getSupportFragmentManager())
                            .setStyleResId(R.style.MyCustomBetterPickerTheme);
                    hpb.show();
                }
                break;
            case A_B_REPEAT:
                ABRepeatDialog dialog = new ABRepeatDialog(NowPlaying.this);
                dialog.show();
                WindowManager.LayoutParams lap = dialog.getWindow().getAttributes();
                lap.dimAmount = 0.5f;
                dialog.getWindow().setAttributes(lap);
                dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);

                break;
            default:
                break;
        }
        return super.onContextItemSelected(item);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 4:
                if (resultCode == -1) {
                    Uri uri = data.getData();
                    if (uri != null) {
                        long[] list = new long[]{Long.parseLong(MetaRetriever.getsInstance().getSongId())};
                        MusicUtils.addToPlaylist(mContext, list, Integer.valueOf(uri.getLastPathSegment()));
                    }
                }
                break;
        }
    }

    public void showplaylist() {
        this.getFragmentManager().beginTransaction().add(R.id.mainnowplaying, new Queue_Fragment(), "reyansh").addToBackStack("ok")
                .commit();
 /*
        NowplayingActivity f = new NowplayingActivity();
        this.getFragmentManager().beginTransaction().add(R.id.mainnowplaying, f,"reyansh").addToBackStack("ok")
                .commit();
 */
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(updateUIBroadcastReceiver);
        unregisterReceiver(updateSongDetails);
        mApp = null;
    }

    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btnPlay:
                mApp.getService().playPauseSong();
                setPauseButtonImage();
                break;
            case R.id.btnForward:
                mApp.getService().nextSong();
                break;
            case R.id.btnBackward:
                mApp.getService().previousSong();
                break;
            case R.id.btnshuffle:
                shuffleSongs();
                break;
            case R.id.btnrepeat:
                repeatSong();
                break;
            case R.id.btnqueue:
                showplaylist();
                break;
            case R.id.equalizer:
                startActivity(new Intent(mContext, EqualizerActivity.class));
                break;
            case R.id.favorites:
                mApp.getDBAccessHelper().addTOFavorites(Integer.parseInt(MetaRetriever.getsInstance().getSongId()));
                YoYo.with(Techniques.Tada).playOn(mFavoritesImageButton);
                updateFavIcon();
                break;
            default:
                break;
        }
    }

    private Runnable pausetimer = new Runnable() {
        public void run() {
            timer();
        }
    };

    public void timer() {
        if (mTimer != null) {
            mApp.getService().playPauseSong();
            mTimer.cancel();
            if (mTimerDialog.isShowing()) mTimerDialog.dismiss();
            mTimerIsSet = false;
            mTimer = null;
            Toast.makeText(mContext, R.string.overTimerMsg, Toast.LENGTH_SHORT).show();
            setPauseButtonImage();
        }
    }

    private void shuffleSongs() {
        YoYo.with(Techniques.Tada)
                .duration(500)
                .playOn(albumArt);
        if (mApp.getPreferencesUtility().getShuffleMode()) {
            mApp.getPreferencesUtility().setShuffleMode(false);
            mShuffleButton.setImageResource(R.drawable.ic_action_playback_schuffle);
        } else {
            mApp.getPreferencesUtility().setShuffleMode(true);
            mApp.getPreferencesUtility().setRepeateMode(0);
            mRepeatButton.setImageResource(R.drawable.ic_action_playback_repeat);
            mShuffleButton.setImageResource(R.drawable.ic_action_playback_schuffle_blue);
        }
    }

    public void repeatSong() {
        mApp.getService().clearABRepeatRange();
        mApp.getPreferencesUtility().setShuffleMode(false);
        mShuffleButton.setImageResource(R.drawable.ic_action_playback_schuffle);
        if (mApp.getPreferencesUtility().getReateMode() == 0) {
            mApp.getPreferencesUtility().setRepeateMode(1);
            mRepeatButton.setImageResource(R.drawable.ic_action_playback_repeat_1_blue);
        } else if (mApp.getPreferencesUtility().getReateMode() == 1) {
            mApp.getPreferencesUtility().setRepeateMode(2);
            mRepeatButton.setImageResource(R.drawable.ic_action_playback_repeat_blue);
        } else if (mApp.getPreferencesUtility().getReateMode() == 2) {
            mApp.getPreferencesUtility().setRepeateMode(0);
            mRepeatButton.setImageResource(R.drawable.ic_action_playback_repeat);
        } else if (mApp.getPreferencesUtility().getReateMode() == 3) {
            mApp.getPreferencesUtility().setRepeateMode(0);
            mRepeatButton.setImageResource(R.drawable.ic_action_playback_repeat);
        }
        Log.d("asdfasdf", String.valueOf(mApp.getPreferencesUtility().getReateMode()));
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setPauseButtonImage() {

        if (mApp != null && mApp.getService().getMediaPlayer().isPlaying()) {
            mPlayPuaseImageButton.setBackground(ContextCompat.getDrawable(this, R.drawable.pause));
            seekBar.clearAnimation();
        } else {
            mPlayPuaseImageButton.setBackground(ContextCompat.getDrawable(this, R.drawable.play2));
            seekBarBlinking(seekBar);
        }
        if (mApp.getPreferencesUtility().getReateMode() == 0) {
            mRepeatButton.setImageResource(R.drawable.ic_action_playback_repeat);
        } else if (mApp.getPreferencesUtility().getReateMode() == 1) {
            mRepeatButton.setImageResource(R.drawable.ic_action_playback_repeat_1_blue);
        } else if (mApp.getPreferencesUtility().getReateMode() == 2) {
            mRepeatButton.setImageResource(R.drawable.ic_action_playback_repeat_blue);
        } else if (mApp.getPreferencesUtility().getReateMode() == 3) {
            mRepeatButton.setImageResource(R.drawable.repeat_song_range);
        }
        if (mApp.getPreferencesUtility().getShuffleMode()) {
            mShuffleButton.setImageResource(R.drawable.ic_action_playback_schuffle_blue);
        }
    }

    public void seekBarBlinking(SeekBar seekBar) {
        Animation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(900);
        anim.setStartOffset(20);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
        seekBar.startAnimation(anim);

    }


    public BroadcastReceiver updateUIBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateUI(intent);
            updateFragmentUI();
            setPauseButtonImage();
        }
    };

    public void updateFragmentUI() {
        Queue_Fragment fragment = (Queue_Fragment) getFragmentManager().findFragmentByTag("reyansh");
        if (fragment != null)
            fragment.updateUI();
    }

    public BroadcastReceiver updateSongDetails = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int i = intent.getIntExtra("posss", 0);
            if (i == -2) {
                setPauseButtonImage();
            } else {
                populateUI();
            }
        }
    };


    @Override
    public void onDialogHmsSet(int reference, int hours, int minutes, int seconds) {
        int h = hours * 60 * 60;
        int m = minutes * 60;
        Toast.makeText(mContext, R.string.setTimerMsg, Toast.LENGTH_SHORT).show();
        s = h + m + seconds;
        setTimer(s * 1000);
        timerDialog();
    }

    public Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            s = s - 1;
            mTimerDialogTextView.setText(MusicUtils.makeShortTimeString(mContext, s));
        }
    };

    private void timerDialog() {
        int delay = 1000;
        int period = 1000;
        mTimer = new Timer();
        mTimer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                mHandler.obtainMessage(1).sendToTarget();
            }
        }, delay, period);

    }

    private void setTimer(long aftertime) {
        mTimerIsSet = true;
        mTimerHandler.removeCallbacks(pausetimer);
        mTimerHandler.postDelayed(pausetimer, aftertime);
    }

    private void populateUI() {
        runOnUiThread(new Runnable() {
            public void run() {
                YoYo.with(Techniques.BounceInRight).duration(750).playOn(songName);
                songName.setText(MetaRetriever.getsInstance().getSongName());
                YoYo.with(Techniques.BounceInLeft).duration(750).playOn(artistName);
                artistName.setText(MetaRetriever.getsInstance().getSongArtist());
                YoYo.with(Techniques.BounceInLeft).duration(750).playOn(albumName);
                albumName.setText(MetaRetriever.getsInstance().getAlbumName());
                albumArt.setImageBitmap(MetaRetriever.getsInstance().getArtWork());
                YoYo.with(Techniques.BounceInDown).duration(850).playOn(albumArt);
                mBackgroundImageView.setImageBitmap(MetaRetriever.getsInstance().getBlurredArtWorkA(mContext));
                updateFavIcon();
            }
        });
    }

    public void updateFavIcon() {
        if (!MetaRetriever.getsInstance().getSongId().equalsIgnoreCase(""))
            if (mApp.getDBAccessHelper().checkFavorites(Integer.parseInt(MetaRetriever.getsInstance().getSongId()))) {
                mFavoritesImageButton.setImageResource(R.drawable.ic_action_filled_heart);
            } else {
                mFavoritesImageButton.setImageResource(R.drawable.ic_action_sallow_heart);
            }
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void updateUI(Intent intent) {
        String counter = intent.getStringExtra("counter");
        String mediamax = intent.getStringExtra("mediamax");
        int seekProgress = Integer.parseInt(counter);
        int seekMax = Integer.parseInt(mediamax);
        seekBar.setMax(seekMax);
        seekBar.setProgress(seekProgress);
        long currentDuration = Long.parseLong(counter);
        long totalDuration = Long.parseLong(mediamax);
        try {
            songCurrentDurationLabel.setText("" + MusicUtils.makeShortTimeString(mContext, (currentDuration) / 1000));
            songTotalDurationLabel.setText("" + MusicUtils.makeShortTimeString(mContext, (totalDuration) / 1000));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onResume() {
        super.onResume();
        setPauseButtonImage();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onProgressChanged(SeekBar sb, int progress, boolean fromUser) {
        if (fromUser) {
            final int seekPos = sb.getProgress();
            intent.putExtra("seekpos", seekPos);
            mAdView.setVisibility(View.INVISIBLE);
            duration.setVisibility(View.VISIBLE);
            try {
                duration.setText(MusicUtils.makeShortTimeString(mContext, (seekPos) / 1000));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            sendBroadcast(intent);
        }
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        final Animation animationFadeOut = AnimationUtils.loadAnimation(this, R.anim.fadeout);
        duration.startAnimation(animationFadeOut);
        duration.setVisibility(View.INVISIBLE);
        mAdView.setVisibility(View.VISIBLE);

    }

}

