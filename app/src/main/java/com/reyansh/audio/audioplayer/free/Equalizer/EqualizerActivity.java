package com.reyansh.audio.audioplayer.free.Equalizer;

import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.audiofx.PresetReverb;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.reyansh.audio.audioplayer.free.Common.CommonClass;
import com.reyansh.audio.audioplayer.free.R;
import com.reyansh.audio.audioplayer.free.Utils.MetaRetriever;
import com.reyansh.audio.audioplayer.free.Views.VerticalSeekBar;
import com.triggertrap.seekarc.SeekArc;

import java.util.ArrayList;

/**
 * Created by REYANSH on 23/04/2016.
 */
public class EqualizerActivity extends AppCompatActivity {
    private Context mContext;

    private VerticalSeekBar e50HzSeekBar;
    private TextView e50HzTextView;

    private VerticalSeekBar e130HzSeekBar;
    private TextView e130HzTextView;

    private VerticalSeekBar e320HzSeekBar;
    private TextView e320HzTextView;

    private VerticalSeekBar e800HzSeekBar;
    private TextView e800HzTextView;

    private VerticalSeekBar e2kHzSeekBar;
    private TextView e2kHzTextView;

    private VerticalSeekBar e5kHzSeekBar;
    private TextView e5kHzTextView;

    private VerticalSeekBar e12_5kHzSeekBar;
    private TextView e12_5HzTextView;

    private Button resetAllButton;
    private Button savePreset;
    //
    private SeekArc virtualizerSeekBar;
    private TextView virtualizervalue;
    private SeekArc bass_boost;
    private TextView bassboostvalue;
    private Spinner reverbSpinner;
    //
    private int fiftyHertzLevel = 16;
    private int oneThirtyHertzLevel = 16;
    private int threeTwentyHertzLevel = 16;
    private int eightHundredHertzLevel = 16;
    private int twoKilohertzLevel = 16;
    private int fiveKilohertzLevel = 16;
    private int twelvePointFiveKilohertzLevel = 16;

    // Temp variables that hold audio fx settings.
    private int virtualizerLevel;
    private int bassBoostLevel;
    private int reverbSetting;
    CommonClass mApp;
    LinearLayout bg;
    ArrayList<String> reverbPresets;
    boolean DEBUG = true;
    String TAG = "Equalizer";
    private Toolbar mToolbar;
    SwitchCompat switcha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();
        mApp = (CommonClass) mContext.getApplicationContext();
        if (mApp.getPreferencesUtility().getFullWindow()) {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        setContentView(R.layout.main_equalizer);
        final View view = this.getWindow().getDecorView();
        view.setBackgroundColor(Color.parseColor("#000000"));

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

        mContext = getApplicationContext();
        bg = (LinearLayout) findViewById(R.id.parentlinear);
        bg.setBackground(MetaRetriever.getsInstance().getBlurredArtWork(mContext));
        e50HzSeekBar = (VerticalSeekBar) findViewById(R.id.slider_1);
        e50HzSeekBar.setOnSeekBarChangeListener(equalizer50HzListener);
        e50HzTextView = (TextView) findViewById(R.id.e50hztxt);
        e130HzSeekBar = (VerticalSeekBar) findViewById(R.id.slider_2);
        e130HzSeekBar.setOnSeekBarChangeListener(equalizer130HzListener);
        e130HzTextView = (TextView) findViewById(R.id.e130hztxt);
        e320HzSeekBar = (VerticalSeekBar) findViewById(R.id.slider_3);
        e320HzSeekBar.setOnSeekBarChangeListener(equalizer320HzListener);
        e320HzTextView = (TextView) findViewById(R.id.e320hztxt);
        e800HzSeekBar = (VerticalSeekBar) findViewById(R.id.slider_4);
        e800HzSeekBar.setOnSeekBarChangeListener(equalizer800HzListener);
        e800HzTextView = (TextView) findViewById(R.id.e800hztxt);
        e2kHzSeekBar = (VerticalSeekBar) findViewById(R.id.slider_5);
        e2kHzSeekBar.setOnSeekBarChangeListener(equalizer2kHzListener);
        e2kHzTextView = (TextView) findViewById(R.id.e2khztxt);
        e5kHzSeekBar = (VerticalSeekBar) findViewById(R.id.slider_6);
        e5kHzSeekBar.setOnSeekBarChangeListener(equalizer5kHzListener);
        e5kHzTextView = (TextView) findViewById(R.id.e5khztxt);

        e12_5kHzSeekBar = (VerticalSeekBar) findViewById(R.id.slider_7);
        e12_5kHzSeekBar.setOnSeekBarChangeListener(equalizer12_5kHzListener);
        e12_5HzTextView = (TextView) findViewById(R.id.e12_5khztxt);


        virtualizerSeekBar = (SeekArc) findViewById(R.id.virtulizer);
        virtualizerSeekBar.setOnSeekArcChangeListener(virtualizerListener);
        virtualizervalue = (TextView) findViewById(R.id.virtualizervalue);

        bass_boost = (SeekArc) findViewById(R.id.bass_boost);
        bass_boost.setOnSeekArcChangeListener(bassBoostListener);
        bassboostvalue = (TextView) findViewById(R.id.bass_boostvalue);

        reverbSpinner = (Spinner) findViewById(R.id.reverb_spinner);


        if (mApp.getPreferencesUtility().isEqActive()) letsEnableControl();
        else letsDisableControl();

        reverbPresets = new ArrayList<String>();
        ArrayList<String> savedPresets = mApp.getDBAccessHelper().getAvailablePresets();
        String[] reverbs = mContext.getResources().getStringArray(R.array.presetarray);
        for (int i = 0; i < reverbs.length; i++) {
            reverbPresets.add(reverbs[i]);
        }
        for (int j = 0; j < savedPresets.size(); j++) {
            reverbPresets.add(savedPresets.get(j));
        }

        final ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.spinner_text, reverbPresets);
        dataAdapter.setDropDownViewResource(R.layout.spinner_dropdown);
        reverbSpinner.setAdapter(dataAdapter);
        reverbSpinner.setOnItemSelectedListener(reverbListener);
        updateCurrentPreset();

        resetAllButton = (Button) findViewById(R.id.resetAllButton);
        savePreset = (Button) findViewById(R.id.savePreset);


        resetAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetAll();
                reverbSpinner.setSelection(0, true);
                Toast.makeText(mContext,"Reset.",Toast.LENGTH_SHORT).show();
            }

        });

        savePreset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(EqualizerActivity.this);
                dialog.setContentView(R.layout.create_playlist);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                TextView title = (TextView) dialog.findViewById(R.id.titles);
                title.setText("Enter Preset Name");
                final EditText enteredName = (EditText) dialog.findViewById(R.id.playlist);
                Button positive = (Button) dialog.findViewById(R.id.create);
                positive.setTypeface(mApp.getStripTitleTypeFace());
                positive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String presetName = enteredName.getText().toString();
                        if (presetName.equalsIgnoreCase("")) {
                            Toast.makeText(mContext,"Please enter preset name.",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        mApp.getDBAccessHelper().addEQForPreset(presetName,
                                fiftyHertzLevel,
                                oneThirtyHertzLevel,
                                threeTwentyHertzLevel,
                                eightHundredHertzLevel, twoKilohertzLevel,
                                fiveKilohertzLevel,
                                twelvePointFiveKilohertzLevel,
                                virtualizerLevel,
                                bassBoostLevel, reverbSetting);
                        reverbPresets.add(mApp.getDBAccessHelper().getPresetByName(presetName));
                        dataAdapter.notifyDataSetChanged();
                        reverbSpinner.setSelection(reverbPresets.size(), true);
                        Toast.makeText(mContext,"Preset saved!",Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
                Button negative = (Button) dialog.findViewById(R.id.cancel);
                negative.setTypeface(mApp.getStripTitleTypeFace());
                negative.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
                WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
                lp.dimAmount = 0.5f;
                dialog.getWindow().setAttributes(lp);
                dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.switch_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.myswitch);
        View view = MenuItemCompat.getActionView(menuItem);
        switcha = (SwitchCompat) view.findViewById(R.id.switchButton);
        switcha.setChecked(mApp.getPreferencesUtility().isEqActive());
        switcha.setOnCheckedChangeListener(equalizerActive);
        return true;
    }


    public void updateCurrentPreset() {
        final String lastPresetName = mApp.getPreferencesUtility().getLastPreset();
        if (lastPresetName.equalsIgnoreCase("None")) {
            applyDefault("default");
            mApp.getService().getEqualizerHelper().getReverb().setPreset(PresetReverb.PRESET_NONE);
            reverbSpinner.setSelection(reverbPresets.indexOf(lastPresetName), false);
            reverbSetting = 0;
        } else if (lastPresetName.equalsIgnoreCase("Large Hall")) {
            mApp.getService().getEqualizerHelper().getReverb().setPreset(PresetReverb.PRESET_LARGEHALL);
            reverbSpinner.setSelection(reverbPresets.indexOf(lastPresetName), false);
            reverbSetting = 1;
        } else if (lastPresetName.equalsIgnoreCase("Large Room")) {
            mApp.getService().getEqualizerHelper().getReverb().setPreset(PresetReverb.PRESET_LARGEROOM);
            reverbSpinner.setSelection(reverbPresets.indexOf(lastPresetName), false);
            reverbSetting = 2;
        } else if (lastPresetName.equalsIgnoreCase("Medium Hall")) {
            mApp.getService().getEqualizerHelper().getReverb().setPreset(PresetReverb.PRESET_MEDIUMHALL);
            reverbSpinner.setSelection(reverbPresets.indexOf(lastPresetName), false);
            reverbSetting = 3;
        } else if (lastPresetName.equalsIgnoreCase("Medium Room")) {
            mApp.getService().getEqualizerHelper().getReverb().setPreset(PresetReverb.PRESET_MEDIUMROOM);
            reverbSpinner.setSelection(reverbPresets.indexOf(lastPresetName), false);
            reverbSetting = 4;
        } else if (lastPresetName.equalsIgnoreCase("Small Room")) {
            mApp.getService().getEqualizerHelper().getReverb().setPreset(PresetReverb.PRESET_SMALLROOM);
            reverbSpinner.setSelection(reverbPresets.indexOf(lastPresetName), false);
            reverbSetting = 5;
        } else if (lastPresetName.equalsIgnoreCase("Plate")) {
            mApp.getService().getEqualizerHelper().getReverb().setPreset(PresetReverb.PRESET_PLATE);
            reverbSpinner.setSelection(reverbPresets.indexOf(lastPresetName), false);
            reverbSetting = 6;
        } else if (lastPresetName.equalsIgnoreCase("Normal")) {
            mApp.getService().getEqualizerHelper().getReverb().setPreset(PresetReverb.PRESET_NONE);
            mApp.getService().getEqualizerHelper().getEqualizer().usePreset((short) 0);
            reverbSpinner.setSelection(reverbPresets.indexOf(lastPresetName), false);
            reverbSetting = 7;
        } else if (lastPresetName.equalsIgnoreCase("Classical")) {
            mApp.getService().getEqualizerHelper().getReverb().setPreset(PresetReverb.PRESET_NONE);
            mApp.getService().getEqualizerHelper().getEqualizer().usePreset((short) 1);
            reverbSpinner.setSelection(reverbPresets.indexOf(lastPresetName), false);
            reverbSetting = 8;
        } else if (lastPresetName.equalsIgnoreCase("Dance")) {
            mApp.getService().getEqualizerHelper().getReverb().setPreset(PresetReverb.PRESET_NONE);
            mApp.getService().getEqualizerHelper().getEqualizer().usePreset((short) 2);
            reverbSpinner.setSelection(reverbPresets.indexOf(lastPresetName), false);
            reverbSetting = 9;
        } else if (lastPresetName.equalsIgnoreCase("Folk")) {
            mApp.getService().getEqualizerHelper().getReverb().setPreset(PresetReverb.PRESET_NONE);
            mApp.getService().getEqualizerHelper().getEqualizer().usePreset((short) 3);
            reverbSpinner.setSelection(reverbPresets.indexOf(lastPresetName), false);
            reverbSetting = 10;
        } else if (lastPresetName.equalsIgnoreCase("Heavy Metal")) {
            mApp.getService().getEqualizerHelper().getReverb().setPreset(PresetReverb.PRESET_NONE);
            mApp.getService().getEqualizerHelper().getEqualizer().usePreset((short) 4);
            reverbSpinner.setSelection(reverbPresets.indexOf(lastPresetName), false);
            reverbSetting = 11;
        } else if (lastPresetName.equalsIgnoreCase("Hip Hop")) {
            mApp.getService().getEqualizerHelper().getReverb().setPreset(PresetReverb.PRESET_NONE);
            mApp.getService().getEqualizerHelper().getEqualizer().usePreset((short) 5);
            reverbSpinner.setSelection(reverbPresets.indexOf(lastPresetName), false);
            reverbSetting = 12;
        } else if (lastPresetName.equalsIgnoreCase("Jazz")) {
            mApp.getService().getEqualizerHelper().getReverb().setPreset(PresetReverb.PRESET_NONE);
            mApp.getService().getEqualizerHelper().getEqualizer().usePreset((short) 6);
            reverbSpinner.setSelection(reverbPresets.indexOf(lastPresetName), false);
            reverbSetting = 13;
        } else if (lastPresetName.equalsIgnoreCase("Pop")) {
            mApp.getService().getEqualizerHelper().getReverb().setPreset(PresetReverb.PRESET_NONE);
            mApp.getService().getEqualizerHelper().getEqualizer().usePreset((short) 7);
            reverbSpinner.setSelection(reverbPresets.indexOf(lastPresetName), false);
            reverbSetting = 14;
        } else if (lastPresetName.equalsIgnoreCase("Rock")) {
            mApp.getService().getEqualizerHelper().getReverb().setPreset(PresetReverb.PRESET_NONE);
            mApp.getService().getEqualizerHelper().getEqualizer().usePreset((short) 8);
            reverbSpinner.setSelection(reverbPresets.indexOf(lastPresetName), false);
            reverbSetting = 15;
        } else {
            applyDefault(lastPresetName);
//            int[] eqValues = mApp.getDBAccessHelper().getEQForPreset(lastPresetName);
//            e50HzSeekBar.setProgressAndThumb(eqValues[0]);
//            e130HzSeekBar.setProgressAndThumb(eqValues[1]);
//            e320HzSeekBar.setProgressAndThumb(eqValues[2]);
//            e800HzSeekBar.setProgressAndThumb(eqValues[3]);
//            e2kHzSeekBar.setProgressAndThumb(eqValues[4]);
//            e5kHzSeekBar.setProgressAndThumb(eqValues[5]);
//            e12_5kHzSeekBar.setProgressAndThumb(eqValues[6]);
//            virtualizerSeekBar.setProgress(eqValues[7]);
//            bass_boost.setProgress(eqValues[8]);
        }

    }

    public void applyDefault(final String lastPresetName) {
        new AsyncTask<Void, Void, Void>() {
            int[] eqValues;

            @Override
            protected Void doInBackground(Void... params) {
                eqValues = mApp.getDBAccessHelper().getEQForPreset(lastPresetName);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                fiftyHertzLevel = eqValues[0];
                oneThirtyHertzLevel = eqValues[1];
                threeTwentyHertzLevel = eqValues[2];
                eightHundredHertzLevel = eqValues[3];
                twoKilohertzLevel = eqValues[4];
                fiveKilohertzLevel = eqValues[5];
                twelvePointFiveKilohertzLevel = eqValues[6];
                virtualizerLevel = eqValues[7];
                bassBoostLevel = eqValues[8];
                reverbSetting = eqValues[9];
                //Move the sliders to the equalizer settings.
                e50HzSeekBar.setProgressAndThumb(fiftyHertzLevel);
                e130HzSeekBar.setProgressAndThumb(oneThirtyHertzLevel);
                e320HzSeekBar.setProgressAndThumb(threeTwentyHertzLevel);
                e800HzSeekBar.setProgressAndThumb(eightHundredHertzLevel);
                e2kHzSeekBar.setProgressAndThumb(twoKilohertzLevel);
                e5kHzSeekBar.setProgressAndThumb(fiveKilohertzLevel);
                e12_5kHzSeekBar.setProgressAndThumb(twelvePointFiveKilohertzLevel);
                virtualizerSeekBar.setProgress(virtualizerLevel);
                bass_boost.setProgress(bassBoostLevel);
                reverbSpinner.setSelection(reverbPresets.indexOf(lastPresetName), false);
            }
        }.execute();
    }

    void resetAll() {
        e50HzSeekBar.setProgressAndThumb(16);
        e130HzSeekBar.setProgressAndThumb(16);
        e320HzSeekBar.setProgressAndThumb(16);
        e800HzSeekBar.setProgressAndThumb(16);
        e2kHzSeekBar.setProgressAndThumb(16);
        e5kHzSeekBar.setProgressAndThumb(16);
        e12_5kHzSeekBar.setProgressAndThumb(16);
        virtualizerSeekBar.setProgress(0);
        bass_boost.setProgress(0);


    }


    CompoundButton.OnCheckedChangeListener equalizerActive = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (switcha.isChecked()) {
                mApp.getPreferencesUtility().setEqState(true);
                mApp.getService().getEqualizerHelper().getBassBoost().setEnabled(true);
                mApp.getService().getEqualizerHelper().getVirtualizer().setEnabled(true);
                mApp.getService().getEqualizerHelper().getEqualizer().setEnabled(true);
                letsEnableControl();
            } else {
                mApp.getPreferencesUtility().setEqState(false);
                mApp.getService().getEqualizerHelper().getBassBoost().setEnabled(false);
                mApp.getService().getEqualizerHelper().getVirtualizer().setEnabled(false);
                mApp.getService().getEqualizerHelper().getEqualizer().setEnabled(false);
                letsDisableControl();
            }
        }
    };

    void letsEnableControl() {
        e12_5kHzSeekBar.setEnabled(true);
        e320HzSeekBar.setEnabled(true);
        e800HzSeekBar.setEnabled(true);
        e50HzSeekBar.setEnabled(true);
        e130HzSeekBar.setEnabled(true);
        e5kHzSeekBar.setEnabled(true);
        e2kHzSeekBar.setEnabled(true);
        reverbSpinner.setEnabled(true);
        bass_boost.setEnabled(true);
        virtualizerSeekBar.setEnabled(true);
    }

    void letsDisableControl() {
        e12_5kHzSeekBar.setEnabled(false);
        e320HzSeekBar.setEnabled(false);
        e800HzSeekBar.setEnabled(false);
        e50HzSeekBar.setEnabled(false);
        e130HzSeekBar.setEnabled(false);
        e5kHzSeekBar.setEnabled(false);
        e2kHzSeekBar.setEnabled(false);
        reverbSpinner.setEnabled(false);
        bass_boost.setEnabled(false);
        virtualizerSeekBar.setEnabled(false);
    }

    private SeekBar.OnSeekBarChangeListener equalizer50HzListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar arg0, int seekBarLevel, boolean changedByUser) {
            try {
                short sixtyHertzBand = mApp.getService().getEqualizerHelper().getEqualizer().getBand(50000);
                if (seekBarLevel == 16) {
                    e50HzTextView.setText("0");
                    mApp.getService().getEqualizerHelper().getEqualizer().setBandLevel(sixtyHertzBand, (short) 0);
                } else if (seekBarLevel < 16) {
                    if (seekBarLevel == 0) {
                        e50HzTextView.setText("-" + "15 ");
                        mApp.getService().getEqualizerHelper().getEqualizer().setBandLevel(sixtyHertzBand, (short) (-1500));
                    } else {
                        e50HzTextView.setText("-" + (16 - seekBarLevel));
                        mApp.getService().getEqualizerHelper().getEqualizer().setBandLevel(sixtyHertzBand, (short) -((16 - seekBarLevel) * 100));
                    }
                } else if (seekBarLevel > 16) {
                    e50HzTextView.setText("+" + (seekBarLevel - 16));
                    mApp.getService().getEqualizerHelper().getEqualizer().setBandLevel(sixtyHertzBand, (short) ((seekBarLevel - 16) * 100));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            mApp.getPreferencesUtility().setFiftyHertzLevel(seekBarLevel);
            fiftyHertzLevel = seekBarLevel;

        }

        @Override
        public void onStartTrackingTouch(SeekBar arg0) {
            reverbSpinner.setSelection(0, true);
        }

        @Override
        public void onStopTrackingTouch(SeekBar arg0) {

        }

    };

    /**
     * 130 Hz equalizer seekbar listener.
     */
    private SeekBar.OnSeekBarChangeListener equalizer130HzListener = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onProgressChanged(SeekBar arg0, int seekBarLevel, boolean changedByUser) {

            try {
                //Get the appropriate equalizer band.
                short twoThirtyHertzBand = mApp.getService().getEqualizerHelper().getEqualizer().getBand(130000);

                //Set the gain level text based on the slider position.
                if (seekBarLevel == 16) {
                    e130HzTextView.setText("0");
                    mApp.getService().getEqualizerHelper().getEqualizer().setBandLevel(twoThirtyHertzBand, (short) 0);
                } else if (seekBarLevel < 16) {
                    if (seekBarLevel == 0) {
                        e130HzTextView.setText("-" + "15");
                        mApp.getService().getEqualizerHelper().getEqualizer().setBandLevel(twoThirtyHertzBand, (short) (-1500));
                    } else {
                        e130HzTextView.setText("-" + (16 - seekBarLevel));
                        mApp.getService().getEqualizerHelper().getEqualizer().setBandLevel(twoThirtyHertzBand, (short) -((16 - seekBarLevel) * 100));
                    }

                } else if (seekBarLevel > 16) {
                    e130HzTextView.setText("+" + (seekBarLevel - 16));
                    mApp.getService().getEqualizerHelper().getEqualizer().setBandLevel(twoThirtyHertzBand, (short) ((seekBarLevel - 16) * 100));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            mApp.getPreferencesUtility().setOneThirtyHertzLevel(seekBarLevel);
            oneThirtyHertzLevel = seekBarLevel;
        }

        @Override
        public void onStartTrackingTouch(SeekBar arg0) {
            reverbSpinner.setSelection(0, true);

        }

        @Override
        public void onStopTrackingTouch(SeekBar arg0) {
            // TODO Auto-generated method stub

        }

    };
    /**
     * 320 Hz equalizer seekbar listener.
     */
    private SeekBar.OnSeekBarChangeListener equalizer320HzListener = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onProgressChanged(SeekBar arg0, int seekBarLevel, boolean changedByUser) {
            try {
                short nineTenHertzBand = mApp.getService().getEqualizerHelper().getEqualizer().getBand(320000);
                if (seekBarLevel == 16) {
                    e320HzTextView.setText("0");
                    mApp.getService().getEqualizerHelper().getEqualizer().setBandLevel(nineTenHertzBand, (short) 0);
                } else if (seekBarLevel < 16) {

                    if (seekBarLevel == 0) {
                        e320HzTextView.setText("-" + "15");
                        mApp.getService().getEqualizerHelper().getEqualizer().setBandLevel(nineTenHertzBand, (short) (-1500));
                    } else {
                        e320HzTextView.setText("-" + (16 - seekBarLevel));
                        mApp.getService().getEqualizerHelper().getEqualizer().setBandLevel(nineTenHertzBand, (short) -((16 - seekBarLevel) * 100));
                    }

                } else if (seekBarLevel > 16) {
                    e320HzTextView.setText("+" + (seekBarLevel - 16));
                    mApp.getService().getEqualizerHelper().getEqualizer().setBandLevel(nineTenHertzBand, (short) ((seekBarLevel - 16) * 100));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            mApp.getPreferencesUtility().setThreeTwentyHertzLevel(seekBarLevel);
            threeTwentyHertzLevel = seekBarLevel;
        }

        @Override
        public void onStartTrackingTouch(SeekBar arg0) {
            reverbSpinner.setSelection(0, true);
        }

        @Override
        public void onStopTrackingTouch(SeekBar arg0) {

        }

    };
    /**
     * 800 Hz equalizer seekbar listener.
     */
    private SeekBar.OnSeekBarChangeListener equalizer800HzListener = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onProgressChanged(SeekBar arg0, int seekBarLevel, boolean changedByUser) {
            try {
                //Get the appropriate equalizer band.
                short threeKiloHertzBand = mApp.getService().getEqualizerHelper().getEqualizer().getBand(800000);
                //Set the gain level text based on the slider position.
                if (seekBarLevel == 16) {
                    e800HzTextView.setText("0");
                    mApp.getService().getEqualizerHelper().getEqualizer().setBandLevel(threeKiloHertzBand, (short) 0);
                } else if (seekBarLevel < 16) {
                    if (seekBarLevel == 0) {
                        e800HzTextView.setText("-" + "15 ");
                        mApp.getService().getEqualizerHelper().getEqualizer().setBandLevel(threeKiloHertzBand, (short) (-1500));
                    } else {
                        e800HzTextView.setText("-" + (16 - seekBarLevel));
                        mApp.getService().getEqualizerHelper().getEqualizer().setBandLevel(threeKiloHertzBand, (short) -((16 - seekBarLevel) * 100));
                    }

                } else if (seekBarLevel > 16) {
                    e800HzTextView.setText("+" + (seekBarLevel - 16));
                    mApp.getService().getEqualizerHelper().getEqualizer().setBandLevel(threeKiloHertzBand, (short) ((seekBarLevel - 16) * 100));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            mApp.getPreferencesUtility().setEightHundredHertzLevel(seekBarLevel);
            eightHundredHertzLevel = seekBarLevel;
        }

        @Override
        public void onStartTrackingTouch(SeekBar arg0) {
            reverbSpinner.setSelection(0, true);
        }

        @Override
        public void onStopTrackingTouch(SeekBar arg0) {

        }

    };
    /**
     * 2 kHz equalizer seekbar listener.
     */
    private SeekBar.OnSeekBarChangeListener equalizer2kHzListener = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onProgressChanged(SeekBar arg0, int seekBarLevel, boolean changedByUser) {
            try {
                short fourteenKiloHertzBand = mApp.getService().getEqualizerHelper().getEqualizer().getBand(2000000);
                if (seekBarLevel == 16) {
                    e2kHzTextView.setText("0");
                    mApp.getService().getEqualizerHelper().getEqualizer().setBandLevel(fourteenKiloHertzBand, (short) 0);
                } else if (seekBarLevel < 16) {

                    if (seekBarLevel == 0) {
                        e2kHzTextView.setText("-" + "15 ");
                        mApp.getService().getEqualizerHelper().getEqualizer().setBandLevel(fourteenKiloHertzBand, (short) (-1500));
                    } else {
                        e2kHzTextView.setText("-" + (16 - seekBarLevel));
                        mApp.getService().getEqualizerHelper().getEqualizer().setBandLevel(fourteenKiloHertzBand, (short) -((16 - seekBarLevel) * 100));
                    }

                } else if (seekBarLevel > 16) {
                    e2kHzTextView.setText("+" + (seekBarLevel - 16));
                    mApp.getService().getEqualizerHelper().getEqualizer().setBandLevel(fourteenKiloHertzBand, (short) ((seekBarLevel - 16) * 100));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            mApp.getPreferencesUtility().setTwoKilohertzLevel(seekBarLevel);
            twoKilohertzLevel = seekBarLevel;
        }

        @Override
        public void onStartTrackingTouch(SeekBar arg0) {
            reverbSpinner.setSelection(0, true);
        }

        @Override
        public void onStopTrackingTouch(SeekBar arg0) {
        }

    };


    /**
     * 5 kHz equalizer seekbar listener.
     */
    private SeekBar.OnSeekBarChangeListener equalizer5kHzListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar arg0, int seekBarLevel, boolean changedByUser) {
            try {
                short fiveKiloHertzBand = mApp.getService().getEqualizerHelper().getEqualizer().getBand(5000000);
                if (seekBarLevel == 16) {
                    e5kHzTextView.setText("0");
                    mApp.getService().getEqualizerHelper().getEqualizer().setBandLevel(fiveKiloHertzBand, (short) 0);
                } else if (seekBarLevel < 16) {
                    if (seekBarLevel == 0) {
                        e5kHzTextView.setText("-" + "15");
                        mApp.getService().getEqualizerHelper().getEqualizer().setBandLevel(fiveKiloHertzBand, (short) (-1500));
                    } else {
                        e5kHzTextView.setText("-" + (16 - seekBarLevel));
                        mApp.getService().getEqualizerHelper().getEqualizer().setBandLevel(fiveKiloHertzBand, (short) -((16 - seekBarLevel) * 100));
                    }

                } else if (seekBarLevel > 16) {
                    e5kHzTextView.setText("+" + (seekBarLevel - 16));
                    mApp.getService().getEqualizerHelper().getEqualizer().setBandLevel(fiveKiloHertzBand, (short) ((seekBarLevel - 16) * 100));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            mApp.getPreferencesUtility().setFiveKilohertzLevel(seekBarLevel);
            fiveKilohertzLevel = seekBarLevel;
        }

        @Override
        public void onStartTrackingTouch(SeekBar arg0) {
            reverbSpinner.setSelection(0, true);
        }

        @Override
        public void onStopTrackingTouch(SeekBar arg0) {

        }

    };


    /**
     * 12.5 kHz equalizer seekbar listener.
     */
    private SeekBar.OnSeekBarChangeListener equalizer12_5kHzListener = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onProgressChanged(SeekBar arg0, int seekBarLevel, boolean changedByUser) {
            try {
                short twelvePointFiveKiloHertzBand = mApp.getService().getEqualizerHelper().getEqualizer().getBand(9000000);
                if (seekBarLevel == 16) {
                    e12_5HzTextView.setText("0");
                    mApp.getService().getEqualizerHelper().getEqualizer().setBandLevel(twelvePointFiveKiloHertzBand, (short) 0);
                } else if (seekBarLevel < 16) {
                    if (seekBarLevel == 0) {
                        e12_5HzTextView.setText("-" + "15");
                        mApp.getService().getEqualizerHelper().getEqualizer().setBandLevel(twelvePointFiveKiloHertzBand, (short) (-1500));
                    } else {
                        e12_5HzTextView.setText("-" + (16 - seekBarLevel));
                        mApp.getService().getEqualizerHelper().getEqualizer().setBandLevel(twelvePointFiveKiloHertzBand, (short) -((16 - seekBarLevel) * 100));
                    }

                } else if (seekBarLevel > 16) {
                    e12_5HzTextView.setText("+" + (seekBarLevel - 16));
                    mApp.getService().getEqualizerHelper().getEqualizer().setBandLevel(twelvePointFiveKiloHertzBand, (short) ((seekBarLevel - 16) * 100));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            mApp.getPreferencesUtility().setTwelvePointFiveKilohertzLevel(seekBarLevel);
            twelvePointFiveKilohertzLevel = seekBarLevel;
        }

        @Override
        public void onStartTrackingTouch(SeekBar arg0) {
            reverbSpinner.setSelection(0, true);
        }

        @Override
        public void onStopTrackingTouch(SeekBar arg0) {

        }

    };


    private SeekArc.OnSeekArcChangeListener virtualizerListener = new SeekArc.OnSeekArcChangeListener() {

        @Override
        public void onProgressChanged(SeekArc arg0, int arg1, boolean arg2) {
            mApp.getService().getEqualizerHelper().getVirtualizer().setStrength((short) arg1);
            float f = ((float) arg1 / 1000) * 100;
            int a = Math.round(f);
            virtualizervalue.setText(a + "%");
            mApp.getPreferencesUtility().setVirtualizerLevel(virtualizerLevel);
            virtualizerLevel = (short) arg1;
        }

        @Override
        public void onStartTrackingTouch(SeekArc seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekArc seekBar) {
        }

    };


    private SeekArc.OnSeekArcChangeListener bassBoostListener = new SeekArc.OnSeekArcChangeListener() {
        @Override
        public void onProgressChanged(SeekArc seekArc, int i, boolean b) {
            mApp.getService().getEqualizerHelper().getBassBoost().setStrength((short) i);
            Log.d("values", String.valueOf(i));
            bassBoostLevel = (short) i;
            float f = ((float) i / 1000) * 100;
            int a = Math.round(f);
            bassboostvalue.setText(a + "%");
        }

        @Override
        public void onStartTrackingTouch(SeekArc seekArc) {

        }

        @Override
        public void onStopTrackingTouch(SeekArc seekArc) {

        }


    };

    private AdapterView.OnItemSelectedListener reverbListener = new AdapterView.OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> arg0, View arg1, int index, long arg3) {
            mApp.getPreferencesUtility().setLastPreset(reverbSpinner.getSelectedItem().toString());
            if (index == 0) {
                mApp.getService().getEqualizerHelper().getReverb().setPreset(PresetReverb.PRESET_NONE);
                reverbSpinner.setSelection(reverbSpinner.getSelectedItemPosition(), false);
                reverbSetting = 0;
            } else if (index == 1) {
                mApp.getService().getEqualizerHelper().getReverb().setPreset(PresetReverb.PRESET_LARGEHALL);
                reverbSpinner.setSelection(reverbSpinner.getSelectedItemPosition(), false);
                reverbSetting = 1;
            } else if (index == 2) {
                mApp.getService().getEqualizerHelper().getReverb().setPreset(PresetReverb.PRESET_LARGEROOM);
                reverbSpinner.setSelection(reverbSpinner.getSelectedItemPosition(), false);
                reverbSetting = 2;
            } else if (index == 3) {
                mApp.getService().getEqualizerHelper().getReverb().setPreset(PresetReverb.PRESET_MEDIUMHALL);
                reverbSpinner.setSelection(reverbSpinner.getSelectedItemPosition(), false);
                reverbSetting = 3;
            } else if (index == 4) {
                mApp.getService().getEqualizerHelper().getReverb().setPreset(PresetReverb.PRESET_MEDIUMROOM);
                reverbSpinner.setSelection(reverbSpinner.getSelectedItemPosition(), false);
                reverbSetting = 4;
            } else if (index == 5) {
                mApp.getService().getEqualizerHelper().getReverb().setPreset(PresetReverb.PRESET_SMALLROOM);
                reverbSpinner.setSelection(reverbSpinner.getSelectedItemPosition(), false);
                reverbSetting = 5;
            } else if (index == 6) {
                mApp.getService().getEqualizerHelper().getReverb().setPreset(PresetReverb.PRESET_PLATE);
                reverbSpinner.setSelection(reverbSpinner.getSelectedItemPosition(), false);
                reverbSetting = 6;
            } else if (index == 7) {
                mApp.getService().getEqualizerHelper().getReverb().setPreset(PresetReverb.PRESET_NONE);
                mApp.getService().getEqualizerHelper().getEqualizer().usePreset((short) 0);
                reverbSpinner.setSelection(reverbSpinner.getSelectedItemPosition(), false);
                reverbSetting = 7;
            } else if (index == 8) {
                mApp.getService().getEqualizerHelper().getReverb().setPreset(PresetReverb.PRESET_NONE);
                mApp.getService().getEqualizerHelper().getEqualizer().usePreset((short) 1);
                reverbSpinner.setSelection(reverbSpinner.getSelectedItemPosition(), false);
                reverbSetting = 8;
            } else if (index == 9) {
                mApp.getService().getEqualizerHelper().getReverb().setPreset(PresetReverb.PRESET_NONE);
                mApp.getService().getEqualizerHelper().getEqualizer().usePreset((short) 2);
                reverbSpinner.setSelection(reverbSpinner.getSelectedItemPosition(), false);
                reverbSetting = 9;
            } else if (index == 10) {
                mApp.getService().getEqualizerHelper().getReverb().setPreset(PresetReverb.PRESET_NONE);
                mApp.getService().getEqualizerHelper().getEqualizer().usePreset((short) 3);
                reverbSpinner.setSelection(reverbSpinner.getSelectedItemPosition(), false);
                reverbSetting = 10;
            } else if (index == 11) {
                mApp.getService().getEqualizerHelper().getReverb().setPreset(PresetReverb.PRESET_NONE);
                mApp.getService().getEqualizerHelper().getEqualizer().usePreset((short) 4);
                reverbSpinner.setSelection(reverbSpinner.getSelectedItemPosition(), false);
                reverbSetting = 11;
            } else if (index == 12) {
                mApp.getService().getEqualizerHelper().getReverb().setPreset(PresetReverb.PRESET_NONE);
                mApp.getService().getEqualizerHelper().getEqualizer().usePreset((short) 5);
                reverbSpinner.setSelection(reverbSpinner.getSelectedItemPosition(), false);
                reverbSetting = 12;
            } else if (index == 13) {
                mApp.getService().getEqualizerHelper().getReverb().setPreset(PresetReverb.PRESET_NONE);
                mApp.getService().getEqualizerHelper().getEqualizer().usePreset((short) 6);
                reverbSpinner.setSelection(reverbSpinner.getSelectedItemPosition(), false);
                reverbSetting = 13;
            } else if (index == 14) {
                mApp.getService().getEqualizerHelper().getReverb().setPreset(PresetReverb.PRESET_NONE);
                mApp.getService().getEqualizerHelper().getEqualizer().usePreset((short) 7);
                reverbSpinner.setSelection(reverbSpinner.getSelectedItemPosition(), false);
                reverbSetting = 14;
            } else if (index == 15) {
                mApp.getService().getEqualizerHelper().getReverb().setPreset(PresetReverb.PRESET_NONE);
                mApp.getService().getEqualizerHelper().getEqualizer().usePreset((short) 8);
                reverbSpinner.setSelection(reverbSpinner.getSelectedItemPosition(), false);
                reverbSetting = 15;
            } else if (index == 16) {
                mApp.getService().getEqualizerHelper().getReverb().setPreset(PresetReverb.PRESET_NONE);
                mApp.getService().getEqualizerHelper().getEqualizer().usePreset((short) 9);
                reverbSpinner.setSelection(reverbSpinner.getSelectedItemPosition(), false);
                reverbSetting = 16;
            } else {
                final String presetName = (String) reverbSpinner.getSelectedItem();
                new AsyncTask<Void, Void, Void>() {
                    int[] eqValues;

                    @Override
                    protected Void doInBackground(Void... params) {
                        eqValues = mApp.getDBAccessHelper().getEQForPreset(presetName);
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);
                        fiftyHertzLevel = eqValues[0];
                        oneThirtyHertzLevel = eqValues[1];
                        threeTwentyHertzLevel = eqValues[2];
                        eightHundredHertzLevel = eqValues[3];
                        twoKilohertzLevel = eqValues[4];
                        fiveKilohertzLevel = eqValues[5];
                        twelvePointFiveKilohertzLevel = eqValues[6];
                        virtualizerLevel = eqValues[7];
                        bassBoostLevel = eqValues[8];
                        reverbSetting = eqValues[9];
                        //Move the sliders to the equalizer settings.
                        ValueAnimator fiftyHzAnim = ValueAnimator.ofInt(e50HzSeekBar.getProgress(), fiftyHertzLevel);
                        fiftyHzAnim.setDuration(500);
                        fiftyHzAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animation) {
                                int animProgress = (Integer) animation.getAnimatedValue();
                                e50HzSeekBar.setProgressAndThumb(animProgress);
                            }
                        });
                        fiftyHzAnim.start();
                        ValueAnimator oneThirtyHzAnim = ValueAnimator.ofInt(e130HzSeekBar.getProgress(), oneThirtyHertzLevel);
                        oneThirtyHzAnim.setDuration(500);
                        oneThirtyHzAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animation) {
                                int animProgress = (Integer) animation.getAnimatedValue();
                                e130HzSeekBar.setProgressAndThumb(animProgress);
                            }
                        });
                        oneThirtyHzAnim.start();

                        ValueAnimator threeTwentyHzAnim = ValueAnimator.ofInt(e320HzSeekBar.getProgress(), threeTwentyHertzLevel);
                        threeTwentyHzAnim.setDuration(500);
                        threeTwentyHzAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animation) {
                                int animProgress = (Integer) animation.getAnimatedValue();
                                e320HzSeekBar.setProgressAndThumb(animProgress);
                            }
                        });
                        threeTwentyHzAnim.start();


                        ValueAnimator eightHundresHzAnim = ValueAnimator.ofInt(e800HzSeekBar.getProgress(), eightHundredHertzLevel);
                        eightHundresHzAnim.setDuration(500);
                        eightHundresHzAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animation) {
                                int animProgress = (Integer) animation.getAnimatedValue();
                                e800HzSeekBar.setProgressAndThumb(animProgress);
                            }
                        });
                        eightHundresHzAnim.start();
                        ValueAnimator twoKHzAnim = ValueAnimator.ofInt(e2kHzSeekBar.getProgress(), twoKilohertzLevel);
                        twoKHzAnim.setDuration(500);
                        twoKHzAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animation) {
                                int animProgress = (Integer) animation.getAnimatedValue();
                                e2kHzSeekBar.setProgressAndThumb(animProgress);
                            }
                        });
                        twoKHzAnim.start();

                        ValueAnimator fiveKHzAnim = ValueAnimator.ofInt(e5kHzSeekBar.getProgress(), fiveKilohertzLevel);
                        fiveKHzAnim.setDuration(500);
                        fiveKHzAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animation) {
                                int animProgress = (Integer) animation.getAnimatedValue();
                                e5kHzSeekBar.setProgressAndThumb(animProgress);
                            }
                        });
                        fiveKHzAnim.start();

                        ValueAnimator twelvePointfiveKHzAnim = ValueAnimator.ofInt(e12_5kHzSeekBar.getProgress(), twelvePointFiveKilohertzLevel);
                        twelvePointfiveKHzAnim.setDuration(500);
                        twelvePointfiveKHzAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animation) {
                                int animProgress = (Integer) animation.getAnimatedValue();
                                e12_5kHzSeekBar.setProgressAndThumb(animProgress);
                            }
                        });
                        twelvePointfiveKHzAnim.start();


                        ValueAnimator virtualizerAnim = ValueAnimator.ofInt(virtualizerSeekBar.getProgress(), virtualizerLevel);
                        virtualizerAnim.setDuration(500);
                        virtualizerAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animation) {
                                int animProgress = (Integer) animation.getAnimatedValue();
                                virtualizerSeekBar.setProgress(animProgress);
                            }
                        });
                        virtualizerAnim.start();

                        ValueAnimator bassboostAnim = ValueAnimator.ofInt(bass_boost.getProgress(), bassBoostLevel);
                        bassboostAnim.setDuration(500);
                        bassboostAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animation) {
                                int animProgress = (Integer) animation.getAnimatedValue();
                                bass_boost.setProgress(animProgress);
                            }
                        });
                        bassboostAnim.start();

                        reverbSpinner.setSelection(reverbSpinner.getSelectedItemPosition(), true);
                    }
                }.execute();
            }
        }

        public void onNothingSelected(AdapterView<?> arg0) {

        }
    };

    public int getVirtualizerLevel() {
        return virtualizerLevel;
    }

    @Override
    protected void onStop() {
        super.onStop();


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        mApp.getPreferencesUtility().setLastPreset(reverbSpinner.getSelectedItem().toString());

        mApp.getDBAccessHelper().addEQForPreset(
                "default",
                fiftyHertzLevel,
                oneThirtyHertzLevel,
                threeTwentyHertzLevel,
                eightHundredHertzLevel, twoKilohertzLevel,
                fiveKilohertzLevel,
                twelvePointFiveKilohertzLevel,
                virtualizerLevel,
                bassBoostLevel,
                reverbSetting);
        Log.d("aaaa", "" + fiftyHertzLevel);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
