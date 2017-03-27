package com.reyansh.audio.audioplayer.free.Setting;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.reyansh.audio.audioplayer.free.Common.CommonClass;
import com.reyansh.audio.audioplayer.free.Dialog.AboutDialog;
import com.reyansh.audio.audioplayer.free.Dialog.FontDialog;
import com.reyansh.audio.audioplayer.free.Dialog.ScrollViewDialog;
import com.reyansh.audio.audioplayer.free.MainActivity.Main;
import com.reyansh.audio.audioplayer.free.R;
import com.reyansh.audio.audioplayer.free.Utils.MetaRetriever;
import com.reyansh.audio.audioplayer.free.Utils.PreferencesUtility;

public class SettingsActivity extends AppCompatActivity {
    private LinearLayout changefont, fullwindow, stripviewll, viewpager, shakeitll, blurlocksreenl, background, playll, pausell, about;
    private Context mContext;
    private CommonClass mApp;
    private CheckBox shakeit, fullwindowcheck, stripcheck, blurlocksreenit, backgroundcheck, playcheck, pausecheck;
    private static SettingsActivity instance;
    RelativeLayout mainbg;
    InterstitialAd mInterstitialAd;
    private Toolbar mToolbar;
    AdView mAdView;

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
        setContentView(R.layout.settings);
        final View view = this.getWindow().getDecorView();
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
        view.setBackgroundColor(Color.parseColor("#000000"));
        mainbg = (RelativeLayout) findViewById(R.id.mainbg);
        changefont = (LinearLayout) findViewById(R.id.changefont);
        stripcheck = (CheckBox) findViewById(R.id.checkstripview);
        stripviewll = (LinearLayout) findViewById(R.id.stripview);
        viewpager = (LinearLayout) findViewById(R.id.scrollview);
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        mAdView.bringToFront();

        mInterstitialAd = new InterstitialAd(this);

        // set the ad unit ID
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial_full_screen));

        AdRequest adRequestInterstitial = new AdRequest.Builder()
                .build();
        mInterstitialAd.loadAd(adRequestInterstitial);
        mInterstitialAd.setAdListener(new AdListener() {
            public void onAdLoaded() {
                showInterstitial();
            }
        });

        instance = this;
        fullwindowcheck = (CheckBox) findViewById(R.id.fullwindowcheck);
        fullwindow = (LinearLayout) findViewById(R.id.fullwindow);
        about = (LinearLayout) findViewById(R.id.about);
        backgroundcheck = (CheckBox) findViewById(R.id.changebgcheck);
        background = (LinearLayout) findViewById(R.id.changebg);

        shakeit = (CheckBox) findViewById(R.id.shakeitcheck);
        shakeitll = (LinearLayout) findViewById(R.id.shakeit);

        blurlocksreenit = (CheckBox) findViewById(R.id.artwork);
        blurlocksreenl = (LinearLayout) findViewById(R.id.artworkl);

        playll = (LinearLayout) findViewById(R.id.playondisconnect);
        playcheck = (CheckBox) findViewById(R.id.playondisconnectcheck);

        pausell = (LinearLayout) findViewById(R.id.pauseondisconnect);
        pausecheck = (CheckBox) findViewById(R.id.pauseondisconnectcheck);

        mainbg.setBackground(MetaRetriever.getsInstance().getBlurredArtWork(mContext));

        if (mApp.getPreferencesUtility().getShakeCheck()) {
            shakeit.setChecked(true);
        } else {
            shakeit.setChecked(false);
        }

        if (mApp.getPreferencesUtility().getLockScreenoption()) {
            blurlocksreenit.setChecked(true);
        } else {
            blurlocksreenit.setChecked(false);
        }

        if (PreferencesUtility.getInstance(mContext).getUserBg()) {
            backgroundcheck.setChecked(true);
        } else {
            backgroundcheck.setChecked(false);
        }

        if (mApp.getPreferencesUtility().getStripviewCheck()) {
            stripcheck.setChecked(true);
        } else {
            stripcheck.setChecked(false);
        }


        if (mApp.getPreferencesUtility().getFullWindow()) {
            fullwindowcheck.setChecked(true);
        } else {
            fullwindowcheck.setChecked(false);
        }

        if (mApp.getPreferencesUtility().getPlayOnDisconnect()) {
            playcheck.setChecked(true);
        } else {
            playcheck.setChecked(false);
        }

        if (mApp.getPreferencesUtility().getPauseOnDisconnect()) {
            pausecheck.setChecked(true);
        } else {
            pausecheck.setChecked(false);
        }


        shakeit.setOnClickListener(shakeits);
        shakeitll.setOnClickListener(shakeits);


        stripcheck.setOnClickListener(stripview);
        stripviewll.setOnClickListener(stripview);


        blurlocksreenit.setOnClickListener(blurlockit);
        blurlocksreenl.setOnClickListener(blurlockit);

        playcheck.setOnClickListener(playOnDisconnect);
        playll.setOnClickListener(playOnDisconnect);

        pausecheck.setOnClickListener(pauseOnDisconnect);
        pausell.setOnClickListener(pauseOnDisconnect);

        changefont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FontDialog f = new FontDialog(mContext);
                f.show();
                WindowManager.LayoutParams lp = f.getWindow().getAttributes();
                lp.dimAmount = 0.5f;
                f.getWindow().setAttributes(lp);
                f.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
            }
        });
        fullwindowcheck.setOnClickListener(fullwindowS);
        fullwindow.setOnClickListener(fullwindowS);
        viewpager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScrollViewDialog s = new ScrollViewDialog(mContext);
                s.show();
                WindowManager.LayoutParams lp = s.getWindow().getAttributes();
                lp.dimAmount = 0.5f;
                s.getWindow().setAttributes(lp);
                s.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
            }
        });
        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AboutDialog f = new AboutDialog(mContext);
                f.show();
            }
        });
        background.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 0);
            }
        });

        backgroundcheck.setOnClickListener(userBg);

    }

    private void showInterstitial() {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }
    }

    View.OnClickListener userBg = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (PreferencesUtility.getInstance(mContext).getUserBg()) {
                PreferencesUtility.getInstance(mContext).setUserBg(false);
                backgroundcheck.setChecked(false);
                mainbg.setBackground(MetaRetriever.getsInstance().getBlurredArtWork(mContext));
            } else {
                backgroundcheck.setChecked(true);
                PreferencesUtility.getInstance(mContext).setUserBg(true);
                mainbg.setBackground(MetaRetriever.getsInstance().getBlurredArtWork(mContext));
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Uri targetUri = data.getData();
            PreferencesUtility.getInstance(mContext).setUserBgUri(String.valueOf(targetUri));
            Intent intent = new Intent("changeBg");
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
            mainbg.setBackground(MetaRetriever.getsInstance().getBlurredArtWork(mContext));
        }
    }

    public static SettingsActivity getInstance() {
        return instance;
    }


    View.OnClickListener fullwindowS = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mApp.getPreferencesUtility().getFullWindow()) {
                mApp.getPreferencesUtility().setFullWindow(false);
                fullwindowcheck.setChecked(false);
            } else {
                mApp.getPreferencesUtility().setFullWindow(true);
                fullwindowcheck.setChecked(true);
            }
            mContext.startActivity(new Intent(mContext, Main.class));
        }
    };
    View.OnClickListener shakeits = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mApp.getPreferencesUtility().getShakeCheck()) {
                mApp.getPreferencesUtility().setShakeCheck(false);
                shakeit.setChecked(false);
            } else {
                mApp.getPreferencesUtility().setShakeCheck(true);
                shakeit.setChecked(true);
            }
        }
    };
    View.OnClickListener stripview = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mApp.getPreferencesUtility().getStripviewCheck()) {
                mApp.getPreferencesUtility().setStripviewCheck(false);
                stripcheck.setChecked(false);
                mContext.startActivity(new Intent(mContext, Main.class));
            } else {
                mApp.getPreferencesUtility().setStripviewCheck(true);
                stripcheck.setChecked(true);
            }
        }
    };
    View.OnClickListener blurlockit = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mApp.getPreferencesUtility().getLockScreenoption()) {
                mApp.getPreferencesUtility().setLockScreenoption(false);
                blurlocksreenit.setChecked(false);
            } else {
                mApp.getPreferencesUtility().setLockScreenoption(true);
                blurlocksreenit.setChecked(true);
            }
        }
    };

    View.OnClickListener playOnDisconnect = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mApp.getPreferencesUtility().getPlayOnDisconnect()) {
                mApp.getPreferencesUtility().setPlayOnDisconnect(false);
                playcheck.setChecked(false);
            } else {
                mApp.getPreferencesUtility().setPlayOnDisconnect(true);
                playcheck.setChecked(true);
            }
        }
    };
    View.OnClickListener pauseOnDisconnect = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mApp.getPreferencesUtility().getPauseOnDisconnect()) {
                mApp.getPreferencesUtility().setPauseOnDisconnect(false);
                pausecheck.setChecked(false);
            } else {
                mApp.getPreferencesUtility().setPauseOnDisconnect(true);
                pausecheck.setChecked(true);
            }

        }
    };


}
