package com.reyansh.audio.audioplayer.free.Equalizer;

import android.content.Context;
import android.media.audiofx.BassBoost;
import android.media.audiofx.Equalizer;
import android.media.audiofx.PresetReverb;
import android.media.audiofx.Virtualizer;
import android.util.Log;

import com.reyansh.audio.audioplayer.free.Common.CommonClass;

public class EqualizerHelper {
    String TAG = "EqualizerHelper";
    boolean DEBUG = true;
    CommonClass mApp;
    //Context and helper objects.
    Context mContext;

    //Equalizer objects.
    private Equalizer mEqualizer;
    private Virtualizer mVirtualizer;
    private BassBoost mBassBoost;
    private PresetReverb mReverb;
    private boolean mIsEqualizerSupported = true;

    //Equalizer setting values.
    private int m50HzLevel = 16;
    private int m130HzLevel = 16;
    private int m320HzLevel = 16;
    private int m800HzLevel = 16;
    private int m2kHzLevel = 16;
    private int m5kHzLevel = 16;
    private int m12kHzLevel = 16;
    private short mVirtualizerLevel = 0;
    private short mBassBoostLevel = 0;
    private short mReverbSetting = 0;

    public EqualizerHelper(Context context, int audioSessionId1, boolean equalizerEnabled) {
        context = context.getApplicationContext();
        mContext = context;
        mEqualizer = new Equalizer(0, audioSessionId1);
        mEqualizer.setEnabled(equalizerEnabled);
        mVirtualizer = new Virtualizer(0, audioSessionId1);
        mVirtualizer.setEnabled(equalizerEnabled);
        mBassBoost = new BassBoost(0, audioSessionId1);
        mBassBoost.setEnabled(equalizerEnabled);
        mReverb = new PresetReverb(0, audioSessionId1);
        mReverb.setEnabled(equalizerEnabled);
        mApp = (CommonClass) mContext.getApplicationContext();
        //if (equalizerEnabled) applyPreviousEqualizerSetting();
    }

    public void applyPreviousEqualizerSetting() {
        if (DEBUG) Log.d(TAG, "applyequalizer");
        short fiftyHertzBand = mEqualizer.getBand(50000);
        short oneThirtyHertzBand = mEqualizer.getBand(130000);
        short threeTwentyHertzBand = mEqualizer.getBand(320000);
        short eightHundredHertzBand = mEqualizer.getBand(800000);
        short twoKilohertzBand = mEqualizer.getBand(2000000);
        short fiveKilohertzBand = mEqualizer.getBand(5000000);
        short twelvePointFiveKilohertzBand = mEqualizer.getBand(9000000);

        //50Hz Band.
        if (mApp.getPreferencesUtility().getFiftyHertzLevel() == 16) {
            mEqualizer.setBandLevel(fiftyHertzBand, (short) 0);
        } else if (mApp.getPreferencesUtility().getFiftyHertzLevel() < 16) {
            if (mApp.getPreferencesUtility().getFiftyHertzLevel() == 0) {
                mEqualizer.setBandLevel(fiftyHertzBand, (short) -1500);
            } else {
                mEqualizer.setBandLevel(fiftyHertzBand, (short) (-(16 - mApp.getPreferencesUtility().getFiftyHertzLevel()) * 100));
            }

        } else if (mApp.getPreferencesUtility().getFiftyHertzLevel() > 16) {
            mEqualizer.setBandLevel(fiftyHertzBand, (short) ((mApp.getPreferencesUtility().getFiftyHertzLevel() - 16) * 100));
        }

        //130Hz Band.
        if (mApp.getPreferencesUtility().getOneThirtyHertzLevel() == 16) {
            mEqualizer.setBandLevel(oneThirtyHertzBand, (short) 0);
        } else if (mApp.getPreferencesUtility().getOneThirtyHertzLevel() < 16) {

            if (mApp.getPreferencesUtility().getOneThirtyHertzLevel() == 0) {
                mEqualizer.setBandLevel(oneThirtyHertzBand, (short) -1500);
            } else {
                mEqualizer.setBandLevel(oneThirtyHertzBand, (short) (-(16 - mApp.getPreferencesUtility().getOneThirtyHertzLevel()) * 100));
            }

        } else if (mApp.getPreferencesUtility().getOneThirtyHertzLevel() > 16) {
            mEqualizer.setBandLevel(oneThirtyHertzBand, (short) ((mApp.getPreferencesUtility().getOneThirtyHertzLevel() - 16) * 100));
        }

        //320Hz Band.
        if (mApp.getPreferencesUtility().getThreeTwentyHertzLevel() == 16) {
            mEqualizer.setBandLevel(threeTwentyHertzBand, (short) 0);
        } else if (mApp.getPreferencesUtility().getThreeTwentyHertzLevel() < 16) {

            if (mApp.getPreferencesUtility().getThreeTwentyHertzLevel() == 0) {
                mEqualizer.setBandLevel(threeTwentyHertzBand, (short) -1500);
            } else {
                mEqualizer.setBandLevel(threeTwentyHertzBand, (short) (-(16 - mApp.getPreferencesUtility().getThreeTwentyHertzLevel()) * 100));
            }

        } else if (mApp.getPreferencesUtility().getThreeTwentyHertzLevel() > 16) {
            mEqualizer.setBandLevel(threeTwentyHertzBand, (short) ((mApp.getPreferencesUtility().getThreeTwentyHertzLevel() - 16) * 100));
        }


        //800Hz Band.
        if (mApp.getPreferencesUtility().getEightHundredHertzLevel() == 16) {
            mEqualizer.setBandLevel(eightHundredHertzBand, (short) 0);
        } else if (mApp.getPreferencesUtility().getEightHundredHertzLevel() < 16) {

            if (mApp.getPreferencesUtility().getEightHundredHertzLevel() == 0) {
                mEqualizer.setBandLevel(eightHundredHertzBand, (short) -1500);
            } else {
                mEqualizer.setBandLevel(eightHundredHertzBand, (short) (-(16 - mApp.getPreferencesUtility().getEightHundredHertzLevel()) * 100));
            }

        } else if (mApp.getPreferencesUtility().getEightHundredHertzLevel() > 16) {
            mEqualizer.setBandLevel(eightHundredHertzBand, (short) ((mApp.getPreferencesUtility().getEightHundredHertzLevel() - 16) * 100));
        }
        //2kHz Band.
        if (mApp.getPreferencesUtility().getTwoKilohertzLevel() == 16) {
            mEqualizer.setBandLevel(twoKilohertzBand, (short) 0);
        } else if (mApp.getPreferencesUtility().getTwoKilohertzLevel() < 16) {

            if (mApp.getPreferencesUtility().getTwoKilohertzLevel() == 0) {
                mEqualizer.setBandLevel(twoKilohertzBand, (short) -1500);
            } else {
                mEqualizer.setBandLevel(twoKilohertzBand, (short) (-(16 - mApp.getPreferencesUtility().getTwoKilohertzLevel()) * 100));
            }

        } else if (mApp.getPreferencesUtility().getTwoKilohertzLevel() > 16) {
            mEqualizer.setBandLevel(twoKilohertzBand, (short) ((mApp.getPreferencesUtility().getTwoKilohertzLevel() - 16) * 100));
        }

        //5kHz Band.
        if (mApp.getPreferencesUtility().getFiveKilohertzLevel() == 16) {
            mEqualizer.setBandLevel(fiveKilohertzBand, (short) 0);
        } else if (mApp.getPreferencesUtility().getFiveKilohertzLevel() < 16) {

            if (mApp.getPreferencesUtility().getFiveKilohertzLevel() == 0) {
                mEqualizer.setBandLevel(fiveKilohertzBand, (short) -1500);
            } else {
                mEqualizer.setBandLevel(fiveKilohertzBand, (short) (-(16 - mApp.getPreferencesUtility().getFiveKilohertzLevel()) * 100));
            }

        } else if (mApp.getPreferencesUtility().getFiveKilohertzLevel() > 16) {
            mEqualizer.setBandLevel(fiveKilohertzBand, (short) ((mApp.getPreferencesUtility().getFiveKilohertzLevel() - 16) * 100));
        }
        //12.5kHz Band.
        if (mApp.getPreferencesUtility().getTwelvePointFiveKilohertzLevel() == 16) {
            mEqualizer.setBandLevel(twelvePointFiveKilohertzBand, (short) 0);
        } else if (mApp.getPreferencesUtility().getTwelvePointFiveKilohertzLevel() < 16) {
            if (mApp.getPreferencesUtility().getTwelvePointFiveKilohertzLevel() == 0) {
                mEqualizer.setBandLevel(twelvePointFiveKilohertzBand, (short) -1500);
            } else {
                mEqualizer.setBandLevel(twelvePointFiveKilohertzBand, (short) (-(16 - mApp.getPreferencesUtility().getTwelvePointFiveKilohertzLevel()) * 100));
            }
        } else if (mApp.getPreferencesUtility().getTwelvePointFiveKilohertzLevel() > 16) {
            mEqualizer.setBandLevel(twelvePointFiveKilohertzBand, (short) ((mApp.getPreferencesUtility().getTwelvePointFiveKilohertzLevel() - 16) * 100));
        }
        mBassBoost.setStrength((short)mApp.getPreferencesUtility().getBassBoostLevel());
    }

    public void releaseEQObjects() throws Exception {
        mEqualizer.release();
        mVirtualizer.release();
        mBassBoost.release();
        mReverb.release();
        mEqualizer = null;
        mVirtualizer = null;
        mBassBoost = null;
        mReverb = null;

    }

    public Equalizer getEqualizer() {
        return mEqualizer;
    }


    public BassBoost getBassBoost() {
        return mBassBoost;
    }


    public Virtualizer getVirtualizer() {
        return mVirtualizer;
    }

    public PresetReverb getReverb() {
        return mReverb;
    }


    public int get50HzLevel() {
        return m50HzLevel;
    }

    public int get130HzLevel() {
        return m130HzLevel;
    }

    public int get320HzLevel() {
        return m320HzLevel;
    }

    public int get800HzLevel() {
        return m800HzLevel;
    }

    public int get2kHzLevel() {
        return m2kHzLevel;
    }

    public int get5kHzLevel() {
        return m5kHzLevel;
    }

    public int get12kHzLevel() {
        return m12kHzLevel;
    }

    public short getVirtualizerLevel() {
        return mVirtualizerLevel;
    }

    public short getBassBoostLevel() {
        return mBassBoostLevel;
    }

    public short getReverbSetting() {
        return mReverbSetting;
    }

    public boolean isEqualizerSupported() {
        return mIsEqualizerSupported;
    }


    public void setEqualizer(Equalizer equalizer) {
        mEqualizer = equalizer;
    }

    public void setVirtualizer(Virtualizer virtualizer) {
        mVirtualizer = virtualizer;
    }


    public void setBassBoost(BassBoost bassBoost) {
        mBassBoost = bassBoost;
    }

    public void setReverb(PresetReverb reverb) {
        mReverb = reverb;
    }

    public void set50HzLevel(int l50HzLevel) {
        m50HzLevel = l50HzLevel;
    }

    public void set130HzLevel(int l130HzLevel) {
        m130HzLevel = l130HzLevel;
    }

    public void set320HzLevel(int l320HzLevel) {
        m320HzLevel = l320HzLevel;
    }

    public void set800HzLevel(int l800HzLevel) {
        m800HzLevel = l800HzLevel;
    }

    public void set2kHzLevel(int l2kHzLevel) {
        m2kHzLevel = l2kHzLevel;
    }

    public void set5kHzLevel(int l5kHzLevel) {
        m5kHzLevel = l5kHzLevel;
    }

    public void set12kHzLevel(int l12kHzLevel) {
        m12kHzLevel = l12kHzLevel;
    }

    public void setVirtualizerLevel(short virtualizerLevel) {
        mVirtualizerLevel = virtualizerLevel;
    }

    public void setBassBoostLevel(short bassBoostLevel) {
        mBassBoostLevel = bassBoostLevel;
    }

    public void setReverbSetting(short reverbSetting) {
        mReverbSetting = reverbSetting;
    }

    public void setIsEqualizerSupported(boolean isSupported) {
        mIsEqualizerSupported = isSupported;
    }

}
