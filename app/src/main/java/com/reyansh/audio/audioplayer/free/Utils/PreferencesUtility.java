/*
 * Copyright (C) 2015 Naman Dwivedi
 *
 * Licensed under the GNU General Public License v3
 *
 * This is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 */

package com.reyansh.audio.audioplayer.free.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

public final class PreferencesUtility extends PreferenceActivity {

    public static final String ARTIST_SORT_ORDER = "artist_sort_order";
    public static final String ARTIST_SONG_SORT_ORDER = "artist_song_sort_order";
    public static final String ARTIST_ALBUM_SORT_ORDER = "artist_album_sort_order";
    public static final String ALBUM_SORT_ORDER = "album_sort_order";
    public static final String ALBUM_SONG_SORT_ORDER = "album_song_sort_order";
    public static final String SONG_SORT_ORDER = "song_sort_order";
    public static final String REPEAT_ALL = "repeate_all";
    public static final String NO_REPEAT = "no_repeate";
    public static final String REPEAT_MODE = "repeate_mode";
    public static final String SONG_QUEUE = "song_queue";
    public static final String SHUFFLE_MODE = "shuffle_mode";
    public static final String SONG_POS = "song_position";
    public static final String SONG_SEEK_POS = "song_seek_position";
    public static final String EQ_SETTINGS = "eq_settings";
    Gson gson = new Gson();
    private static PreferencesUtility sInstance;
    private boolean stripviewCheck;
    private boolean rateUs;
    private long rateUseCount;
    private boolean playOnDisconnect;


    public static final PreferencesUtility getInstance(final Context context) {
        if (sInstance == null) {
            sInstance = new PreferencesUtility(context.getApplicationContext());
        }
        return sInstance;
    }

    private static SharedPreferences mPreferences;

    public PreferencesUtility(final Context context) {
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void setOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
        mPreferences.registerOnSharedPreferenceChangeListener(listener);
    }


    private void setSortOrder(final String key, final String value) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(final Void... unused) {
                final SharedPreferences.Editor editor = mPreferences.edit();
                editor.putString(key, value);
                editor.apply();

                return null;
            }
        }.execute();
    }

    private void setRepeatMode(final String key, final int value) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(final Void... unused) {
                final SharedPreferences.Editor editor = mPreferences.edit();
                editor.putInt(key, value);
                editor.apply();

                return null;
            }
        }.execute();
    }

    private void setSongQueue(final String key, final ArrayList<HashMap<String, String>> value) {
        final String inputString = gson.toJson(value);
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(final Void... unused) {
                final SharedPreferences.Editor editor = mPreferences.edit();
                editor.putString(key, inputString);
                editor.apply();
                return null;
            }
        }.execute();
    }

    private void setSongPos(final String key, final int value) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(final Void... unused) {
                final SharedPreferences.Editor editor = mPreferences.edit();
                editor.putString(key, String.valueOf(value));
                editor.apply();
                return null;
            }
        }.execute();
    }

    public boolean isItFirstLaunch() {
        return mPreferences.getBoolean("firstLaunchs", true);
    }

    public void setIsItFirstLaunch(final boolean value) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(final Void... unused) {
                final SharedPreferences.Editor editor = mPreferences.edit();
                editor.putBoolean("firstLaunchs", value);
                editor.apply();
                return null;
            }
        }.execute();
    }

    private void setSongSeekPos(final String key, final long value) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(final Void... unused) {
                final SharedPreferences.Editor editor = mPreferences.edit();
                editor.putLong(key, value);
                editor.apply();
                return null;
            }
        }.execute();
    }

    private void setShuffleMode(final String key, final boolean value) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(final Void... unused) {
                final SharedPreferences.Editor editor = mPreferences.edit();
                editor.putBoolean(key, value);
                editor.apply();

                return null;
            }
        }.execute();
    }

    public void setSongsViewAs(final boolean value) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(final Void... unused) {
                final SharedPreferences.Editor editor = mPreferences.edit();
                editor.putBoolean("SONGS_VIEW_AS", value);
                editor.apply();

                return null;
            }
        }.execute();
    }


    public void setAlbumViewAs(final boolean value) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(final Void... unused) {
                final SharedPreferences.Editor editor = mPreferences.edit();
                editor.putBoolean("ALBUM_VIEW_AS", value);
                editor.apply();
                return null;
            }
        }.execute();
    }

    public void setArtistsViewAs(final boolean value) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(final Void... unused) {
                final SharedPreferences.Editor editor = mPreferences.edit();
                editor.putBoolean("ARTISTS_VIEW_AS", value);
                editor.apply();
                return null;
            }
        }.execute();
    }

    public boolean getArtistsViewAs() {
        return mPreferences.getBoolean("ARTISTS_VIEW_AS", false);
    }

    public void setBgAlpha(final int value) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(final Void... unused) {
                final SharedPreferences.Editor editor = mPreferences.edit();
                editor.putInt("ALPHA", value);
                editor.apply();

                return null;
            }
        }.execute();
    }


    public int getBgAlpha() {
        return mPreferences.getInt("ALPHA", 150);
    }

    public boolean getAlbumViewAs() {
        return mPreferences.getBoolean("ALBUM_VIEW_AS", false);
    }

    public boolean getSongsViewAs() {
        return mPreferences.getBoolean("SONGS_VIEW_AS", false);
    }

    private void setStrippedLayout(final boolean f) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(final Void... unused) {
                final SharedPreferences.Editor editor = mPreferences.edit();
                editor.putBoolean("stripped", f);
                editor.apply();

                return null;
            }
        }.execute();
    }


    public final String getArtistSortOrder() {
        return mPreferences.getString(ARTIST_SORT_ORDER, SortOrder.ArtistSortOrder.ARTIST_A_Z);
    }

    public void setArtistSortOrder(final String value) {
        setSortOrder(ARTIST_SORT_ORDER, value);
    }

    public final String getArtistSongSortOrder() {
        return mPreferences.getString(ARTIST_SONG_SORT_ORDER,
                SortOrder.ArtistSongSortOrder.SONG_A_Z);
    }

    public void setArtistSongSortOrder(final String value) {
        setSortOrder(ARTIST_SONG_SORT_ORDER, value);
    }

    public final String getArtistAlbumSortOrder() {
        return mPreferences.getString(ARTIST_ALBUM_SORT_ORDER,
                SortOrder.ArtistAlbumSortOrder.ALBUM_A_Z);
    }

    public void setArtistAlbumSortOrder(final String value) {
        setSortOrder(ARTIST_ALBUM_SORT_ORDER, value);
    }

    public final String getAlbumSortOrder() {
        return mPreferences.getString(ALBUM_SORT_ORDER, SortOrder.AlbumSortOrder.ALBUM_A_Z);
    }

    public void setAlbumSortOrder(final String value) {
        setSortOrder(ALBUM_SORT_ORDER, value);
    }

    public final String getAlbumSongSortOrder() {
        return mPreferences.getString(ALBUM_SONG_SORT_ORDER,
                SortOrder.AlbumSongSortOrder.SONG_TRACK_LIST);
    }

    public void setAlbumSongSortOrder(final String value) {
        setSortOrder(ALBUM_SONG_SORT_ORDER, value);
    }

    public final String getSongSortOrder() {
        return mPreferences.getString(SONG_SORT_ORDER, SortOrder.SongSortOrder.SONG_A_Z);
    }

    public void setSongSortOrder(final String value) {
        setSortOrder(SONG_SORT_ORDER, value);
    }

    public void setRepeateMode(final int value) {
        setRepeatMode(REPEAT_MODE, value);
    }

    public void setSongQueue(final ArrayList<HashMap<String, String>> value) {
        setSongQueue(SONG_QUEUE, value);
    }

    public void setSongPos(final int value) {
        setSongPos(SONG_POS, value);
    }

    public void setSongSeekPos(final long value) {
        setSongSeekPos(SONG_SEEK_POS, value);
    }

    public long getSongSeekPos() {

        return mPreferences.getLong(SONG_SEEK_POS, 0L);
    }

    public String getSongPos() {
        return mPreferences.getString(SONG_POS, "0");
    }

    public ArrayList<HashMap<String, String>> getSongQueue() {
        ArrayList<HashMap<String, String>> finalOutputString = null;
        Type type = new TypeToken<ArrayList<HashMap<String, String>>>() {
        }.getType();
        try {
            finalOutputString = gson.fromJson(mPreferences.getString(SONG_QUEUE, "0"), type);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (finalOutputString != null)
            if (finalOutputString.size() != 0 && finalOutputString != null)
                return finalOutputString;
            else
                return null;
        return finalOutputString;
    }

    public void setShuffleMode(final boolean value) {
        setShuffleMode(SHUFFLE_MODE, value);
    }

    public boolean getShuffleMode() {
        return mPreferences.getBoolean(SHUFFLE_MODE, false);
    }

    public int getReateMode() {
        return mPreferences.getInt(REPEAT_MODE, 0);
    }

    public void setLayoutStyle(boolean stripped) {
        setStrippedLayout(stripped);
    }

    public void setShakeCheck(boolean value) {
        final SharedPreferences.Editor editor = mPreferences.edit();
        editor.putBoolean("shakeit", value);
        editor.apply();
    }

    public boolean getShakeCheck() {
        return mPreferences.getBoolean("shakeit", false);
    }

    public String getChangedFont() {
        return null;
    }

    public void setStripviewCheck(boolean stripviewCheck) {
        final SharedPreferences.Editor editor = mPreferences.edit();
        editor.putBoolean("stripit", stripviewCheck);
        editor.apply();

    }

    public boolean getStripviewCheck() {
        return mPreferences.getBoolean("stripit", false);
    }


    public void setFullWindow(boolean stripviewCheck) {
        final SharedPreferences.Editor editor = mPreferences.edit();
        editor.putBoolean("fullit", stripviewCheck);
        editor.apply();

    }

    public boolean getFullWindow() {
        return mPreferences.getBoolean("fullit", false);
    }

    public void setFont(String stripviewCheck) {
        final SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString("fontit", stripviewCheck);
        editor.apply();
    }

    public String getFont() {
        return mPreferences.getString("fontit", "NULL");
    }

    public void setScrollView(String stripviewCheck) {
        final SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString("scrollit", stripviewCheck);
        editor.apply();
    }

    public String getScrollView() {
        return mPreferences.getString("scrollit", "NULL");
    }

    public void setWeeks(int weeks) {
        final SharedPreferences.Editor editor = mPreferences.edit();
        editor.putInt("weekit", weeks);
        editor.apply();
    }

    public int getWeeks() {
        return mPreferences.getInt("weekit", 1);
    }


    public String getLastPreset() {
        return mPreferences.getString("lastPreset", "None");
    }

    public int getFiftyHertzLevel() {
        return mPreferences.getInt("50Hz", 16);
    }

    public int getOneThirtyHertzLevel() {
        return mPreferences.getInt("130Hz", 16);
    }

    public int getThreeTwentyHertzLevel() {
        return mPreferences.getInt("320Hz", 16);
    }

    public int getEightHundredHertzLevel() {
        return mPreferences.getInt("800Hz", 16);
    }

    public int getTwoKilohertzLevel() {
        return mPreferences.getInt("2kHz", 16);
    }

    public int getFiveKilohertzLevel() {
        return mPreferences.getInt("5kHz", 16);
    }

    public int getTwelvePointFiveKilohertzLevel() {
        return mPreferences.getInt("12.5kHz", 16);
    }

    public int getVirtualizerLevel() {
        return mPreferences.getInt("virtualizerLevel", 0);
    }

    public int getBassBoostLevel() {
        return mPreferences.getInt("bassBoostLever", 0);
    }

    /**
     * Setter methods.
     */

    public void setFiftyHertzLevel(final int fiftyHertzLevel) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(final Void... unused) {
                final SharedPreferences.Editor editor = mPreferences.edit();
                editor.putInt("50Hz", fiftyHertzLevel);
                editor.apply();

                return null;
            }
        }.execute();
    }

    public void setLastPreset(final String presetName) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                final SharedPreferences.Editor editor = mPreferences.edit();
                editor.putString("lastPreset", presetName);
                editor.apply();
                return null;
            }
        }.execute();
    }

    public void setOneThirtyHertzLevel(final int oneThirtyHertzLevel) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(final Void... unused) {
                final SharedPreferences.Editor editor = mPreferences.edit();
                editor.putInt("130Hz", oneThirtyHertzLevel);
                editor.apply();
                return null;
            }
        }.execute();
    }

    public void setThreeTwentyHertzLevel(final int threeTwentyHertzLevel) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(final Void... unused) {
                final SharedPreferences.Editor editor = mPreferences.edit();
                editor.putInt("320Hz", threeTwentyHertzLevel);
                editor.apply();
                return null;
            }
        }.execute();
    }

    public void setEightHundredHertzLevel(final int eightHundredHertzLevel) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(final Void... unused) {
                final SharedPreferences.Editor editor = mPreferences.edit();
                editor.putInt("800Hz", eightHundredHertzLevel);
                editor.apply();

                return null;
            }
        }.execute();
    }

    public void setTwoKilohertzLevel(final int twoKilohertzLevel) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(final Void... unused) {
                final SharedPreferences.Editor editor = mPreferences.edit();
                editor.putInt("2kHz", twoKilohertzLevel);
                editor.apply();

                return null;
            }
        }.execute();
    }

    public void setFiveKilohertzLevel(final int fiveKilohertzLevel) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(final Void... unused) {
                final SharedPreferences.Editor editor = mPreferences.edit();
                editor.putInt("5kHz", fiveKilohertzLevel);
                editor.apply();

                return null;
            }
        }.execute();
    }

    public void setTwelvePointFiveKilohertzLevel(final int twelvePointFiveKilohertzLevel) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(final Void... unused) {
                final SharedPreferences.Editor editor = mPreferences.edit();
                editor.putInt("12.5kHz", twelvePointFiveKilohertzLevel);
                editor.apply();

                return null;
            }
        }.execute();
    }

    public void setVirtualizerLevel(final int virtualizerLevel) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(final Void... unused) {
                final SharedPreferences.Editor editor = mPreferences.edit();
                editor.putInt("virtualizerLevel", virtualizerLevel);
                editor.apply();

                return null;
            }
        }.execute();
    }

    public void setBassBoostLevel(final int bassBoostLevel) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(final Void... unused) {
                final SharedPreferences.Editor editor = mPreferences.edit();
                editor.putInt("bassBoostLever", bassBoostLevel);
                editor.apply();

                return null;
            }
        }.execute();
    }


    public void setEqState(final boolean state) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(final Void... unused) {
                final SharedPreferences.Editor editor = mPreferences.edit();
                editor.putBoolean("eqState", state);
                editor.apply();
                return null;
            }
        }.execute();

    }

    public boolean isEqActive() {
        return mPreferences.getBoolean("eqState", false);
    }


    public boolean getLockScreenoption() {
        return mPreferences.getBoolean("lockscreen", false);
    }

    public void setLockScreenoption(final boolean lockScreenoption) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(final Void... unused) {
                final SharedPreferences.Editor editor = mPreferences.edit();
                editor.putBoolean("lockscreen", lockScreenoption);
                editor.apply();
                return null;
            }
        }.execute();

    }

    public void setSeekBarPosition(final int seekBarPosition) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(final Void... unused) {
                final SharedPreferences.Editor editor = mPreferences.edit();
                editor.putInt("seekbarPosition", seekBarPosition);
                editor.apply();
                return null;
            }
        }.execute();
    }

    public int getSeekBarPosition() {
        return mPreferences.getInt("seekbarPosition", 0);
    }

    public boolean getUserBg() {
        return mPreferences.getBoolean("userBg", false);
    }

    public String getUserBgUri() {
        return mPreferences.getString("userBgUri", "null");
    }

    public void setUserBg(final boolean userBg) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(final Void... unused) {
                final SharedPreferences.Editor editor = mPreferences.edit();
                editor.putBoolean("userBg", userBg);
                editor.apply();
                return null;
            }
        }.execute();
    }

    public void setUserBgUri(final String userBgUri) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(final Void... unused) {
                final SharedPreferences.Editor editor = mPreferences.edit();
                editor.putString("userBgUri", userBgUri);
                editor.apply();
                return null;
            }
        }.execute();
    }

    public boolean getRateUs() {
        return mPreferences.getBoolean("dontshowagain", false);
    }

    public int getRateUsCount() {
        return mPreferences.getInt("rateuscount", 0);
    }

    public void setRateUseCount(final int rateUseCount) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(final Void... unused) {
                final SharedPreferences.Editor editor = mPreferences.edit();
                editor.putInt("rateuscount", rateUseCount);
                editor.apply();
                return null;
            }
        }.execute();
    }

    public long getFirstLaunch() {
        Long a = mPreferences.getLong("firstLaunch", 0L);
        return a;
    }

    public void setFirstLaunch(final Long firstLaunch) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(final Void... unused) {
                final SharedPreferences.Editor editor = mPreferences.edit();
                editor.putLong("firstLaunch", firstLaunch);
                editor.apply();
                return null;
            }
        }.execute();
    }

    public void setRateUs(final boolean rate) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(final Void... unused) {
                final SharedPreferences.Editor editor = mPreferences.edit();
                editor.putBoolean("dontshowagain", rate);
                editor.apply();
                return null;
            }
        }.execute();
    }

    public void setSharedPreferenceUri(final String uri) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(final Void... unused) {
                final SharedPreferences.Editor editor = mPreferences.edit();
                editor.putString("sdcardpermission", uri);
                editor.apply();
                return null;
            }
        }.execute();
    }

    public void clearSharedPreference(int version) {
        if (version < 33) {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(final Void... unused) {
                    final SharedPreferences.Editor editor = mPreferences.edit();
                    editor.clear().commit();
                    return null;
                }
            }.execute();
        }
    }

    public String getSharedPreferenceUri() {
        return mPreferences.getString("sdcardpermission", null);
    }

    public boolean getPlayOnDisconnect() {
        return mPreferences.getBoolean("playondisconnect", false);
    }

    public void setPlayOnDisconnect(final boolean value) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                final SharedPreferences.Editor editor = mPreferences.edit();
                editor.putBoolean("playondisconnect", value);
                editor.apply();
                return null;
            }
        }.execute();
    }

    public boolean getPauseOnDisconnect() {
        return mPreferences.getBoolean("pauseondisconnect", false);
    }

    public void setPauseOnDisconnect(final boolean value) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                final SharedPreferences.Editor editor = mPreferences.edit();
                editor.putBoolean("pauseondisconnect", value);
                editor.apply();
                return null;
            }
        }.execute();
    }
}