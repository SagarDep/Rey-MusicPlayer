package com.reyansh.audio.audioplayer.free.MainActivity;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.PagerTitleStrip;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.reyansh.audio.audioplayer.free.Album.FragmentAlbum;
import com.reyansh.audio.audioplayer.free.Artist.FragmentArtist;
import com.reyansh.audio.audioplayer.free.Common.CommonClass;
import com.reyansh.audio.audioplayer.free.Dialog.RateMe;
import com.reyansh.audio.audioplayer.free.FileDirectory.FragmentFolder;
import com.reyansh.audio.audioplayer.free.Genres.FragmentGenres;
import com.reyansh.audio.audioplayer.free.MusicService.MusicService;
import com.reyansh.audio.audioplayer.free.NowPlaying.NowPlaying;
import com.reyansh.audio.audioplayer.free.PlayList.FragmentPlaylist;
import com.reyansh.audio.audioplayer.free.R;
import com.reyansh.audio.audioplayer.free.Search.SearchActivity;
import com.reyansh.audio.audioplayer.free.Setting.SettingsActivity;
import com.reyansh.audio.audioplayer.free.Songs.FragmentSongs;
import com.reyansh.audio.audioplayer.free.Utils.MetaRetriever;
import com.reyansh.audio.audioplayer.free.Utils.MusicUtils;
import com.reyansh.audio.audioplayer.free.Utils.PreferencesUtility;
import com.reyansh.audio.audioplayer.free.Views.HSVColorWheel;
import com.reyansh.audio.audioplayer.free.Views.SeekArc;

public class Main extends FragmentActivity implements MusicUtils.names {

    public ViewPager mViewPager;
    private TextView mSongName;
    private TextView mArtistName;
    private RelativeLayout mTabLayout;
    private ImageView mMainBg;
    private ImageView mTagLayoutBackground;
    private CommonClass mApp;
    private CharSequence mTitle;
    private SwipeAdapter mAdapter;
    private TextView av;
    private Context mContext;
    private static Main main;
    private boolean mUpdate = false;
    private ImageButton mSearchImageButton;
    private ImageButton mOverflowImageButton;
    private SeekArc mSeekArc;
    private TextView mCurrentDuration;
    private TextView mTotalDuration;
    private ImageButton mPlayPauseImageButton;
    private FrameLayout leftLayout;
    private WindowManager.LayoutParams mLayoutParams;
    final int color = 0;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        mApp = (CommonClass) mContext.getApplicationContext();
        if (mApp.getPreferencesUtility().getFullWindow()) {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        setContentView(R.layout.activity_main);
        registerReceiver(dd, new IntentFilter(MusicService.BROADCAST_SONGPATH));
        registerReceiver(mPlaypauseReceiver, new IntentFilter(MusicService.BROADCAST_SEEKBARPROGRESS));
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mAdapter = new SwipeAdapter(getSupportFragmentManager(), mContext);
        mViewPager.setPageTransformer(true, mApp.getTransformer());
        if (!mApp.isServiceRunning()) startService(new Intent(mContext, MusicService.class));
        mViewPager.setAdapter(mAdapter);
        mViewPager.setCurrentItem(2);
        mViewPager.setOffscreenPageLimit(5);
        mSongName = (TextView) findViewById(R.id.text_view_song_name);
        mArtistName = (TextView) findViewById(R.id.text_view_artist_name);
        mTabLayout = (RelativeLayout) findViewById(R.id.tab_layout);
        mTabLayout.setOnClickListener(tabLayoutlistener);
        mMainBg = (ImageView) findViewById(R.id.mainbg);
        mTagLayoutBackground = (ImageView) findViewById(R.id.tab_layout_background);
        mSeekArc = (com.reyansh.audio.audioplayer.free.Views.SeekArc) findViewById(R.id.seekBar);
        leftLayout = (FrameLayout) findViewById(R.id.left_layout);
        leftLayout.setOnClickListener(playpause);
        mSearchImageButton = (ImageButton) findViewById(R.id.image_button_search);
        mOverflowImageButton = (ImageButton) findViewById(R.id.image_button_overflow);
        mOverflowImageButton.setOnClickListener(overFlowListener);
        mSearchImageButton.setOnClickListener(searchListerner);
        mCurrentDuration = (TextView) findViewById(R.id.text_view_current_duration);
        mTotalDuration = (TextView) findViewById(R.id.text_view_duration);
        mPlayPauseImageButton = (ImageButton) findViewById(R.id.image_button_play);
        mPlayPauseImageButton.setOnClickListener(playpause);
        changePagerTitleStripFont();
        main = this;
        mUpdate = true;
        if (mApp.getService() != null) updateUI(0, mContext);
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        showRateUS();
        (findViewById(R.id.pagertitlestrip)).getBackground().setAlpha(PreferencesUtility.getInstance(mContext).getBgAlpha());
        (findViewById(R.id.tab_layout_2)).getBackground().setAlpha(PreferencesUtility.getInstance(mContext).getBgAlpha());
    }

    View.OnClickListener searchListerner = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(mContext, SearchActivity.class));
        }
    };
    View.OnClickListener overFlowListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            PopupMenu popupMenu = new PopupMenu(mContext, v);
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.item_sort_by:
                            switch (mViewPager.getCurrentItem()) {
                                case 0:
                                    ((FragmentAlbum) mAdapter.getFragment(0)).sortAlbum();
                                    break;
                                case 1:
                                    ((FragmentArtist) mAdapter.getFragment(1)).sortArtist();
                                    break;
                                case 2:
                                    ((FragmentSongs) mAdapter.getFragment(2)).sortSong();
                                    break;
                                case 3:

                            }
                            return true;
                        case R.id.item_shuffle:
                            ((FragmentSongs) mAdapter.getFragment(2)).shufflesongs();
                            return true;
                        case R.id.item_settings:
                            startActivity(new Intent(mContext, SettingsActivity.class));
                            return true;
//                        case R.id.item_change_theme:
//                            changeTheme();
//                            return true;
                        case R.id.item_view:
                            changeViewAs();
                            return true;
                    }
                    return true;
                }
            });
            popupMenu.inflate(R.menu.menu_main);
            if (mViewPager.getCurrentItem() == 2) {
                if (PreferencesUtility.getInstance(mContext).getSongsViewAs()) {
                    popupMenu.getMenu().findItem(R.id.item_view).setTitle("View:Without Art");
                } else {
                    popupMenu.getMenu().findItem(R.id.item_view).setTitle("View:With Art");
                }
            } else if (mViewPager.getCurrentItem() == 0) {
                if (PreferencesUtility.getInstance(mContext).getAlbumViewAs()) {
                    popupMenu.getMenu().findItem(R.id.item_view).setTitle("View:Without Pallete");
                } else {
                    popupMenu.getMenu().findItem(R.id.item_view).setTitle("View:With Pallete");
                }
            } else if (mViewPager.getCurrentItem() == 1) {
                if (PreferencesUtility.getInstance(mContext).getArtistsViewAs()) {
                    popupMenu.getMenu().findItem(R.id.item_view).setTitle("View:As Grid");
                } else {
                    popupMenu.getMenu().findItem(R.id.item_view).setTitle("View:As List");
                }
            }
            int cur = mViewPager.getCurrentItem();
            if (cur == 1 || cur == 3 || cur == 4 || cur == 5) {
                (popupMenu.getMenu()).removeItem(R.id.item_sort_by);
                (popupMenu.getMenu()).removeItem(R.id.item_view);
            }
            popupMenu.show();
        }
    };


    View.OnLongClickListener openNowPlaying = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            startActivity(new Intent(mContext, NowPlaying.class));
            return true;
        }
    };
    View.OnClickListener playpause = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!mApp.isServiceRunning()) {
                startService(new Intent(mContext, MusicService.class));
            } else {
                mApp.getService().playPauseSong();
            }
            if (mApp.getService().getMediaPlayer().isPlaying()) {
                mPlayPauseImageButton.setImageResource(R.drawable.ic_av_pause);
            } else {
                mPlayPauseImageButton.setImageResource(R.drawable.ic_av_play_arrow);
            }
        }
    };

    View.OnClickListener tabLayoutlistener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mContext.startActivity(new Intent(mContext, NowPlaying.class));
        }
    };

    public static Main getInstance() {
        return main;
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }


    public FragmentFolder getFragment() {
        return (FragmentFolder) mAdapter.getFragment(5);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mViewPager.setPageTransformer(true, mApp.getTransformer());
        if (mApp.getPreferencesUtility().getSongQueue() == null) {
            mTabLayout.setVisibility(View.INVISIBLE);
        } else {
            mTabLayout.setVisibility(View.VISIBLE);
        }
        changePagerTitleStripFont();
        LocalBroadcastManager.getInstance(mContext).registerReceiver(changeBgBroadcast, new IntentFilter("changeBg"));

    }


    @Override
    protected void onPause() {

        super.onPause();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    BroadcastReceiver dd = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, Intent intent) {
            final int i = intent.getIntExtra("posss", 0);
            runOnUiThread(new Runnable() {
                public void run() {
                    updateUI(i, context);
                    if (mApp.getService().getMediaPlayer().isPlaying()) {
                        mPlayPauseImageButton.setImageResource(R.drawable.ic_av_pause);
                    } else {
                        mPlayPauseImageButton.setImageResource(R.drawable.ic_av_play_arrow);
                    }
                }
            });
        }
    };

    void updateUI(int i, Context context) {
        if (mApp.getService().getSongList().size() != 0) {
            if (i != -2)
                mTagLayoutBackground.setImageBitmap(MetaRetriever.getsInstance().getBlurredArtWorkA(mContext));
            mMainBg.setBackground(MetaRetriever.getsInstance().getBlurredArtWork(mContext));
            //    mSongImage.setImageBitmap(MetaRetriever.getsInstance().getArtWork());
            mSongName.setText(MetaRetriever.getsInstance().getSongName());
            mArtistName.setText(MetaRetriever.getsInstance().getSongArtist());
        } else {
            mMainBg.setBackground(MetaRetriever.getsInstance().getBlurredArtWork(mContext));
            mTagLayoutBackground.setBackground(MetaRetriever.getsInstance().getBlurredArtWork(mContext));
        }


    }

    public void putRotateAnim(ImageView imageView) {
        long a = 0;
        Animation firstAnimation = AnimationUtils.loadAnimation(this, R.anim.imageinout);
        firstAnimation.setRepeatCount(Animation.INFINITE);
        firstAnimation.setRepeatMode(Animation.INFINITE);
        imageView.startAnimation(firstAnimation);
    }

    public void changePagerTitleStripFont() {
        PagerTitleStrip titleStrip = (PagerTitleStrip) findViewById(R.id.pagertitlestrip);
        for (int counter = 0; counter < titleStrip.getChildCount(); counter++) {
            if (titleStrip.getChildAt(counter) instanceof TextView) {
                ((TextView) titleStrip.getChildAt(counter)).setTypeface(mApp.getStripTitleTypeFace());
            }
        }
    }


    private BroadcastReceiver mPlaypauseReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String s = intent.getStringExtra("counter");
            String mediamax = intent.getStringExtra("mediamax");
            mCurrentDuration.setText("" + MusicUtils.makeShortTimeString(mContext, (Long.parseLong(s)) / 1000));
            mTotalDuration.setText("" + MusicUtils.makeShortTimeString(mContext, (Long.parseLong(mediamax)) / 1000));
            mSeekArc.setMax(Integer.parseInt(mediamax));
            mSeekArc.setProgress(Integer.parseInt(s));
            if (mUpdate) {
                updateUI(0, context);
                mUpdate = false;
            }
        }
    };


    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        setTitle(mTitle);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mPlaypauseReceiver);
        unregisterReceiver(dd);
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(changeBgBroadcast);
    }

    @Override
    public void onBackPressed() {
        if (mViewPager.getCurrentItem() == 5) {
            FragmentFolder fragmentFolder = (FragmentFolder) mAdapter.getFragment(5);
            if (fragmentFolder.getCurrentDir().equals("/")) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                startActivity(intent);
            } else {
                fragmentFolder.getParentDir();
            }
        } else {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
        }
    }

    public void hideTabLayout(int dy) {
        if (dy > 0) {
            mAdView.animate().translationY(mAdView.getHeight()).setInterpolator(new AccelerateInterpolator(2)).start();
            mTabLayout.animate().translationY(mTabLayout.getHeight()).setInterpolator(new AccelerateInterpolator(2)).start();
        } else {
            mAdView.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
            mTabLayout.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
        }
    }

    private void showRateUS() {
        if (mApp.getPreferencesUtility().getRateUs()) {
            return;
        }
        int launch_count = mApp.getPreferencesUtility().getRateUsCount() + 1;
        mApp.getPreferencesUtility().setRateUseCount(launch_count);
        Long date_firstLaunch = mApp.getPreferencesUtility().getFirstLaunch();
        if (date_firstLaunch == 0) {
            date_firstLaunch = System.currentTimeMillis();
            mApp.getPreferencesUtility().setFirstLaunch(date_firstLaunch);
        }
        if (launch_count >= 10) {
            if (System.currentTimeMillis() >= date_firstLaunch +
                    (3 * 24 * 60 * 60 * 1000)) {
                RateMe d = new RateMe(mContext);
                d.show();
                WindowManager.LayoutParams lp = d.getWindow().getAttributes();
                lp.dimAmount = 0.5f;
                d.getWindow().setAttributes(lp);
                d.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
            }
        }
    }


    private BroadcastReceiver changeBgBroadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mMainBg.setBackground(MetaRetriever.getsInstance().getBlurredArtWork(mContext));
        }
    };

    private void changeTheme() {
        final Dialog dialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_drawable_color);
        ((HSVColorWheel) dialog.findViewById(R.id.colorWheel)).setListener(new HSVColorWheel.OnColorChange() {
            @Override
            public void selectedColor(int color) {
                getCurrentFragment(color);
            }
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnim;
        SeekBar mAlphaSeekBar = (SeekBar) dialog.findViewById(R.id.color_seekbar);
        mAlphaSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean fromUser) {
                int progress = i;

                dialog.findViewById(R.id.dialogbg).getBackground().setAlpha(progress);
                PreferencesUtility.getInstance(mContext).setBgAlpha(progress);

                (findViewById(R.id.pagertitlestrip)).getBackground().setAlpha(progress);
                (findViewById(R.id.tab_layout_2)).getBackground().setAlpha(progress);
                ((FragmentAlbum) mAdapter.getFragment(0)).getDrawable().setAlpha(progress);
                ((FragmentArtist) mAdapter.getFragment(1)).getDrawable().setAlpha(progress);
                ((FragmentSongs) mAdapter.getFragment(2)).getDrawable().setAlpha(progress);
                ((FragmentGenres) mAdapter.getFragment(3)).getDrawable().setAlpha(progress);
                ((FragmentPlaylist) mAdapter.getFragment(4)).getDrawable().setAlpha(progress);
                ((FragmentFolder) mAdapter.getFragment(5)).getDrawable().setAlpha(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        Button mDismissButton = (Button) dialog.findViewById(R.id.dismiss);
        mDismissButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
        mLayoutParams = dialog.getWindow().getAttributes();
        mLayoutParams.dimAmount = 0.0f;
        dialog.getWindow().setAttributes(mLayoutParams);
    }

    public void getCurrentFragment(int color) {
        (findViewById(R.id.pagertitlestrip)).setBackgroundColor(color);
        (findViewById(R.id.tab_layout_2)).setBackgroundColor(color);
        ((FragmentAlbum) mAdapter.getFragment(0)).setViewColor(color);
        ((FragmentArtist) mAdapter.getFragment(1)).setViewColor(color);
        ((FragmentSongs) mAdapter.getFragment(2)).setViewColor(color);
        ((FragmentGenres) mAdapter.getFragment(3)).setViewColor(color);
        ((FragmentPlaylist) mAdapter.getFragment(4)).setViewColor(color);
        ((FragmentFolder) mAdapter.getFragment(5)).setViewColor(color);
    }


    public void changeViewAs() {
        switch (mViewPager.getCurrentItem()) {
            case 2:
                if (PreferencesUtility.getInstance(mContext).getSongsViewAs()) {
                    PreferencesUtility.getInstance(mContext).setSongsViewAs(false);
                } else {
                    PreferencesUtility.getInstance(mContext).setSongsViewAs(true);
                }
                ((FragmentSongs) mAdapter.getFragment(2)).onResume();
                break;
            case 0:
                if (PreferencesUtility.getInstance(mContext).getAlbumViewAs()) {
                    PreferencesUtility.getInstance(mContext).setAlbumViewAs(false);
                } else {
                    PreferencesUtility.getInstance(mContext).setAlbumViewAs(true);
                }
                ((FragmentAlbum) mAdapter.getFragment(0)).onResume();
                break;

//            case 1:
//                if (PreferencesUtility.getInstance(mContext).getArtistsViewAs()) {
//                    PreferencesUtility.getInstance(mContext).setArtistsViewAs(false);
//                } else {
//                    PreferencesUtility.getInstance(mContext).setArtistsViewAs(true);
//                }
//                ((FragmentArtist) mAdapter.getFragment(1)).setLayoutMangeer();

        }

    }










    /*public void laftfm() {
        LastFmClient.getInstance(mContext).getAlbumInfo(new AlbumQuery("Believe", "Justin Bieber"), new AlbuminfoListener() {
            @Override
            public void albumInfoSucess(LastfmAlbum album) {
                Log.d("lastfm", album.mArtwork.get(2).mUrl);
            }

            @Override
            public void albumInfoFailed() {

            }
        });
    }*/
}





