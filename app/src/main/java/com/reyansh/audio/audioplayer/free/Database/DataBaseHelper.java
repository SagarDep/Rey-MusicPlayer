package com.reyansh.audio.audioplayer.free.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import com.reyansh.audio.audioplayer.free.Utils.MusicUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class DataBaseHelper extends SQLiteOpenHelper {
    private SQLiteDatabase mDatabase;
    static DataBaseHelper mDatabaseHelper;
    Context mContext;
    public static final String DATABASE_NAME = "MyDBName.db";
    public static final int DATABASE_VERSION = 8;
    public static final String FAVORITES_TABLE = "FAVORITES";
    public static final String TOPTRACKS_TABLE = "toptracks";
    public static final String RECENTLY_PLAYED_TABLE = "recentlyplayed";
    public static final String EQUALIZER_TABLE = "equalizer";
    public static final String SONG_ID = "songId";
    public static final String SONG_COUNT = "songCount";
    public static final String E50HZ = "e50hz";
    public static final String E130HZ = "e130hz";
    public static final String E320HZ = "e320hz";
    public static final String E800HZ = "e800hz";
    public static final String E2KHZ = "e2khz";
    public static final String E5KHZ = "e5khz";
    public static final String E125KHZ = "e125khz";
    public static final String VIRTUALIZER = "virtualizer";
    public static final String BASS_BOOST = "bassboost";
    public static final String REVERB = "reverb";
    public static final String PRESET_TABLE = "preset_table";
    public static final String CURRENT_PRESET_TABLE = "current_preset_table";
    public static final String PRESET_NAME = "preset_name";

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + FAVORITES_TABLE + "(" + SONG_ID + " INTEGER PRIMARY KEY)");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + CURRENT_PRESET_TABLE + "(" + PRESET_NAME + " TEXT PRIMARY KEY)");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + RECENTLY_PLAYED_TABLE + "(" + SONG_ID + " INTEGER PRIMARY KEY,date TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TOPTRACKS_TABLE + "(" + SONG_ID + " INTEGER primary KEY," + SONG_COUNT + " INTEGER)");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + EQUALIZER_TABLE + "(" + SONG_ID + " INTEGER primary KEY," + E50HZ + " INTEGER," + E130HZ + " INTEGER," + E320HZ + " INTEGER," + E800HZ + " INTEGER," + E2KHZ + " INTEGER," + E5KHZ + " INTEGER," + E125KHZ + " INTEGER," + BASS_BOOST + " INTEGER," + VIRTUALIZER + " INTEGER," + REVERB + " INTEGER)");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + PRESET_TABLE + "(" + PRESET_NAME + " TEXT primary KEY," + E50HZ + " INTEGER," + E130HZ + " INTEGER," + E320HZ + " INTEGER," + E800HZ + " INTEGER," + E2KHZ + " INTEGER," + E5KHZ + " INTEGER," + E125KHZ + " INTEGER," + BASS_BOOST + " INTEGER," + VIRTUALIZER + " INTEGER," + REVERB + " INTEGER)");
    }


    public void addEQForSong(int songId,
                             int e50Hz,
                             int e130Hz,
                             int e320Hz,
                             int e800Hz,
                             int e2KHz,
                             int e5KHz,
                             int e125KHz,
                             int virtualizer,
                             int bassboost,
                             int reverb) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(SONG_ID, songId);
        contentValues.put(E50HZ, e50Hz);
        contentValues.put(E130HZ, e130Hz);
        contentValues.put(E320HZ, e320Hz);
        contentValues.put(E800HZ, e800Hz);
        contentValues.put(E2KHZ, e2KHz);
        contentValues.put(E5KHZ, e5KHz);
        contentValues.put(E125KHZ, e125KHz);
        contentValues.put(VIRTUALIZER, virtualizer);
        contentValues.put(BASS_BOOST, bassboost);
        contentValues.put(REVERB, reverb);
        try {
            getDatabase().insertOrThrow(EQUALIZER_TABLE, null, contentValues);
        } catch (Exception e) {
            getDatabase().update(EQUALIZER_TABLE, contentValues, "" + SONG_ID + "= " + songId, null);
        }
    }

    public void addEQForPreset(String presetName, int e50Hz,
                               int e130Hz,
                               int e320Hz,
                               int e800Hz,
                               int e2KHz,
                               int e5KHz,
                               int e125KHz,
                               int virtualizer,
                               int bassboost,
                               int reverb) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(PRESET_NAME, presetName);
        contentValues.put(E50HZ, e50Hz);
        contentValues.put(E130HZ, e130Hz);
        contentValues.put(E320HZ, e320Hz);
        contentValues.put(E800HZ, e800Hz);
        contentValues.put(E2KHZ, e2KHz);
        contentValues.put(E5KHZ, e5KHz);
        contentValues.put(E125KHZ, e125KHz);
        contentValues.put(VIRTUALIZER, virtualizer);
        contentValues.put(BASS_BOOST, bassboost);
        contentValues.put(REVERB, reverb);
        try {
            getDatabase().insertOrThrow(PRESET_TABLE, null, contentValues);
        } catch (Exception e) {
            getDatabase().update(PRESET_TABLE, contentValues, PRESET_NAME + "= " + "'" + presetName + "'", null);
        }
    }


    public int[] getEQForPreset(String presetName) {
        String where = PRESET_NAME + "=" + "'" + presetName.replace("'", "''") + "'";
        String columnsToReturn[] = {PRESET_NAME, E50HZ, E130HZ, E320HZ, E800HZ, E5KHZ, E2KHZ, E125KHZ, BASS_BOOST, VIRTUALIZER, REVERB};
        Cursor cursor = getDatabase().query(PRESET_TABLE, columnsToReturn, where, null, null, null, null);
        int[] eqValues = new int[11];
        if (cursor != null && cursor.getCount() != 0) {
            cursor.moveToFirst();
            eqValues[0] = cursor.getInt(cursor.getColumnIndex(E50HZ));
            eqValues[1] = cursor.getInt(cursor.getColumnIndex(E130HZ));
            eqValues[2] = cursor.getInt(cursor.getColumnIndex(E320HZ));
            eqValues[3] = cursor.getInt(cursor.getColumnIndex(E800HZ));
            eqValues[4] = cursor.getInt(cursor.getColumnIndex(E2KHZ));
            eqValues[5] = cursor.getInt(cursor.getColumnIndex(E5KHZ));
            eqValues[6] = cursor.getInt(cursor.getColumnIndex(E125KHZ));
            eqValues[7] = cursor.getInt(cursor.getColumnIndex(VIRTUALIZER));
            eqValues[8] = cursor.getInt(cursor.getColumnIndex(BASS_BOOST));
            eqValues[9] = cursor.getInt(cursor.getColumnIndex(REVERB));
            eqValues[10] = 1; //The song id exists in the EQ table.
            cursor.close();
        }
        return eqValues;
    }

    public ArrayList<String> getAvailablePresets() {
        String[] columnsToReturn = {PRESET_NAME};
        ArrayList<String> presets = new ArrayList<>();
        Cursor c = getDatabase().query(PRESET_TABLE, columnsToReturn, null, null, null, null, null);
        if (c.moveToFirst() && c != null) {
            do {
                presets.add(c.getString(c.getColumnIndex(PRESET_NAME)));
            } while (c.moveToNext());
        }
        return presets;
    }

    public void saveCurrentPreset(String presetName) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(PRESET_NAME, presetName);
        try {
            getDatabase().insertOrThrow(CURRENT_PRESET_TABLE, null, contentValues);
        } catch (Exception e) {
            getDatabase().update(CURRENT_PRESET_TABLE, contentValues, "" + PRESET_NAME + "= " + "'" + presetName + "'", null);
        }
    }

    public String getLastPresetName() {
        String presetsName = "";
        String[] columnsToReturn = {PRESET_NAME};
        Cursor c = getDatabase().query(CURRENT_PRESET_TABLE, columnsToReturn, null, null, null, null, null);
        if (c.moveToFirst() && c != null) {
            do {
                presetsName = c.getString(c.getColumnIndex(PRESET_NAME));
            } while (c.moveToNext());
        }
        return presetsName;
    }

    public String getPresetByName(String presetName) {
        String where = PRESET_NAME + "=" + "'" + presetName.replace("'", "''") + "'";
        String[] columnsToReturn = {PRESET_NAME};
        String presets = "";
        Cursor c = getDatabase().query(PRESET_TABLE, columnsToReturn, where, null, null, null, null);
        if (c.moveToFirst() && c != null) {
            do {
                presets = c.getString(c.getColumnIndex(PRESET_NAME));
            } while (c.moveToNext());
        }
        return presets;
    }


    public int[] getEQForSong(String songId) {
        String where = SONG_ID + "= " + songId;
        String columnsToReturn[] = {SONG_ID, E50HZ, E130HZ, E320HZ, E800HZ, E5KHZ, E2KHZ, E125KHZ, BASS_BOOST, VIRTUALIZER, REVERB};
        Cursor cursor = getDatabase().query(EQUALIZER_TABLE, columnsToReturn, where, null, null, null, null);
        int[] eqValues = new int[11];
        if (cursor != null && cursor.getCount() != 0) {
            cursor.moveToFirst();
            eqValues[0] = cursor.getInt(cursor.getColumnIndex(E50HZ));
            eqValues[1] = cursor.getInt(cursor.getColumnIndex(E130HZ));
            eqValues[2] = cursor.getInt(cursor.getColumnIndex(E320HZ));
            eqValues[3] = cursor.getInt(cursor.getColumnIndex(E800HZ));
            eqValues[4] = cursor.getInt(cursor.getColumnIndex(E2KHZ));
            eqValues[5] = cursor.getInt(cursor.getColumnIndex(E5KHZ));
            eqValues[6] = cursor.getInt(cursor.getColumnIndex(E125KHZ));
            eqValues[7] = cursor.getInt(cursor.getColumnIndex(VIRTUALIZER));
            eqValues[8] = cursor.getInt(cursor.getColumnIndex(BASS_BOOST));
            eqValues[9] = cursor.getInt(cursor.getColumnIndex(REVERB));
            eqValues[10] = 1; //The song id exists in the EQ table.
            cursor.close();
        } else {
            eqValues[0] = 16;
            eqValues[1] = 16;
            eqValues[2] = 16;
            eqValues[3] = 16;
            eqValues[4] = 16;
            eqValues[5] = 16;
            eqValues[6] = 16;
            eqValues[7] = 0;
            eqValues[8] = 0;
            eqValues[9] = 0;
            eqValues[10] = 0; //The song id doesn't exist in the EQ table.
        }
        return eqValues;
    }

    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    public static synchronized DataBaseHelper getDatabaseHelper(Context context) {
        if (mDatabaseHelper == null)
            mDatabaseHelper = new DataBaseHelper(context.getApplicationContext());
        return mDatabaseHelper;
    }

    public synchronized SQLiteDatabase getDatabase() {
        if (mDatabase == null)
            mDatabase = getWritableDatabase();
        return mDatabase;
    }

    public void addTOFavorites(int songId) {
        ContentValues values = new ContentValues();
        values.put("songId", songId);
        try {
            getDatabase().insertOrThrow(FAVORITES_TABLE, null, values);
            Toast.makeText(mContext,"Added to favorites",Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            getDatabase().delete(FAVORITES_TABLE, "songId= " + songId, null);
            Toast.makeText(mContext,"Removed from favorites",Toast.LENGTH_SHORT).show();

        }

    }

    public void addEQSettingForSong() {

    }

    public void removeFromFavorites(Long songId) {
        getDatabase().delete(FAVORITES_TABLE, "songId= " + songId, null);
    }


    public void removeRecentlyPlayed(Long songId) {
        getDatabase().delete(RECENTLY_PLAYED_TABLE, "songId= " + songId, null);
    }


    public void removeFromTopTracks(Long songId) {
        getDatabase().delete(TOPTRACKS_TABLE, "songId= " + songId, null);
    }

    public void insertSongCount(int songId) {
        ContentValues values = new ContentValues();
        Cursor c = getDatabase().rawQuery("SELECT * FROM " + TOPTRACKS_TABLE + " WHERE songId= " + songId, null);
        if (c != null && c.moveToFirst()) {
            if (c.getString(1) != null) {
                int s = c.getInt(1) + 1;
                Log.d("count", String.valueOf(s));
                values.put("songId", songId);
                values.put("songCount", s);
                getDatabase().update(TOPTRACKS_TABLE, values, "songId= " + songId, null);
            }
        } else {
            values.put("songId", songId);
            values.put("songCount", 0);
            try {
                getDatabase().insertOrThrow(TOPTRACKS_TABLE, null, values);
            } catch (Exception e) {
            }
        }
    }

    public void insertRecentlyPlayer(int songId) {
        ContentValues values = new ContentValues();
        values.put("songId", songId);
        values.put("date", new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new java.util.Date()));
        try {
            getDatabase().insertOrThrow(RECENTLY_PLAYED_TABLE, null, values);
        } catch (Exception e) {
            getDatabase().update(RECENTLY_PLAYED_TABLE, values, "songId= " + songId, null);
        }
    }

    public ArrayList<HashMap<String, String>> getRecentlyPlayed() {
        ArrayList<HashMap<String, String>> data = new ArrayList<>();
        Cursor c = getDatabase().query(RECENTLY_PLAYED_TABLE, new String[]{"*"}, null, null, null, null, "datetime(date) DESC");
        if (c.moveToFirst()) {
            do {
                int column2 = c.getInt(0);
                Cursor cursor = MusicUtils.getSongForID(mContext, column2);
                if (cursor.moveToFirst()) {
                    do {
                        HashMap<String, String> ok = new HashMap<>();
                        ok.put(MusicUtils.names.SONG_ID, cursor.getString(0));
                        ok.put(MusicUtils.names.SONG_NAME, cursor.getString(1));
                        ok.put(MusicUtils.names.SONG_ARTIST, cursor.getString(2));
                        ok.put(MusicUtils.names.SONG_DURATION, cursor.getString(3));
                        ok.put(MusicUtils.names.SONG_PATH, cursor.getString(4));
                        ok.put(MusicUtils.names.SONG_ALBUM, cursor.getString(5));
                        ok.put(MusicUtils.names.SONG_ALBUM_ID, cursor.getString(6));
                        data.add(ok);
                    } while (cursor.moveToNext());
                    cursor.close();
                }
            } while (c.moveToNext());
        }
        c.close();
        return data;
    }

    public boolean checkFavorites(int songId) {
        boolean ok = false;
        Cursor c = getDatabase().rawQuery("SELECT * FROM " + FAVORITES_TABLE + " WHERE songId= " + songId, null);
        if (c != null && c.moveToFirst()) {
            if (c.getString(0) != null) {
                ok = true;
            } else {
                ok = false;
            }
        }
        return ok;
    }


    public void clearFavorites() {
        getDatabase().execSQL("DELETE FROM " + FAVORITES_TABLE);
    }

    public void clearTopTracks() {
        getDatabase().execSQL("DELETE FROM " + TOPTRACKS_TABLE);
    }

    public void clearRecentlyPlayed() {
        getDatabase().execSQL("DELETE FROM " + RECENTLY_PLAYED_TABLE);
    }

    public ArrayList<HashMap<String, String>> getTopTracks() {
        ArrayList<HashMap<String, String>> data = new ArrayList<>();
        Cursor c = getDatabase().query(TOPTRACKS_TABLE, new String[]{"*"}, null, null, null, null, "songCount" + " DESC");
        if (c.moveToFirst()) {
            do {
                int column2 = c.getInt(0);
                Cursor cursor = MusicUtils.getSongForID(mContext, column2);
                if (cursor.moveToFirst()) {
                    do {
                        HashMap<String, String> ok = new HashMap<>();
                        ok.put(MusicUtils.names.SONG_ID, cursor.getString(0));
                        ok.put(MusicUtils.names.SONG_NAME, cursor.getString(1));
                        ok.put(MusicUtils.names.SONG_ARTIST, cursor.getString(2));
                        ok.put(MusicUtils.names.SONG_DURATION, cursor.getString(3));
                        ok.put(MusicUtils.names.SONG_PATH, cursor.getString(4));
                        ok.put(MusicUtils.names.SONG_ALBUM, cursor.getString(5));
                        ok.put(MusicUtils.names.SONG_ALBUM_ID, cursor.getString(6));
                        data.add(ok);
                    } while (cursor.moveToNext());
                    cursor.close();
                }
            } while (c.moveToNext());
        }
        c.close();
        return data;
    }

    public ArrayList<HashMap<String, String>> getFavorites() {
        ArrayList<HashMap<String, String>> data = new ArrayList<>();
        Cursor c = getDatabase().rawQuery("SELECT * FROM " + FAVORITES_TABLE +" limit 40", null);
        if (c.moveToFirst()) {
            do {
                int column2 = c.getInt(0);
                Cursor cursor = MusicUtils.getSongForID(mContext, column2);
                if (cursor.moveToFirst()) {
                    do {
                        HashMap<String, String> ok = new HashMap<>();
                        ok.put(MusicUtils.names.SONG_ID, cursor.getString(0));
                        ok.put(MusicUtils.names.SONG_NAME, cursor.getString(1));
                        ok.put(MusicUtils.names.SONG_ARTIST, cursor.getString(2));
                        ok.put(MusicUtils.names.SONG_DURATION, cursor.getString(3));
                        ok.put(MusicUtils.names.SONG_PATH, cursor.getString(4));
                        ok.put(MusicUtils.names.SONG_ALBUM, cursor.getString(5));
                        ok.put(MusicUtils.names.SONG_ALBUM_ID, cursor.getString(6));
                        // ok.put("ooo", cursor.getString(7));
                        data.add(ok);
                    } while (cursor.moveToNext());
                    cursor.close();
                }
            } while (c.moveToNext());
        }
        c.close();
        return data;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + FAVORITES_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + RECENTLY_PLAYED_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + TOPTRACKS_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + EQUALIZER_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + PRESET_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + CURRENT_PRESET_TABLE);
        onCreate(db);
    }

    @Override
    protected void finalize() {
        try {
            getDatabase().close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
