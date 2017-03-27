package com.reyansh.audio.audioplayer.free.Songs;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.MediaStore;

import com.reyansh.audio.audioplayer.free.Common.CommonClass;
import com.reyansh.audio.audioplayer.free.Utils.MusicUtils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by REYANSH on 30/07/2016.
 */
public class AsyncFetchSongs extends AsyncTask<Void, Void, ArrayList<HashMap<String, String>>> implements MusicUtils.names {
    private ArrayList<HashMap<String, String>> mFetchedSongs = new ArrayList<HashMap<String, String>>();
    private Context mContext;
    private CommonClass mApp;
    private Cursor cursor;
    private FragmentSongs mFragmentSongs;

    public AsyncFetchSongs(Context context, FragmentSongs fragmentSongs) {
        mContext = context;
        mApp = (CommonClass) mContext.getApplicationContext();
        mFragmentSongs = fragmentSongs;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected ArrayList<HashMap<String, String>> doInBackground(Void... params) {

        try {
            String[] columns = {
                    MediaStore.Audio.Media._ID,
                    MediaStore.Audio.Media.TITLE,
                    MediaStore.Audio.Media.ARTIST,
                    MediaStore.Audio.Media.DURATION,
                    MediaStore.Audio.Media.DATA,
                    MediaStore.Audio.Media.ALBUM,
                    MediaStore.Audio.Media.ALBUM_ID,
            };
            cursor = mContext.getContentResolver().query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    columns,
                    null,
                    null,
                    mApp.getPreferencesUtility().getSongSortOrder());
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    if (cursor.getInt(3) > 6000) {
                        HashMap<String, String> song = new HashMap<String, String>();
                        song.put(SONG_ID, cursor.getString(0));
                        song.put(SONG_NAME, cursor.getString(1));
                        song.put(SONG_ARTIST, cursor.getString(2));
                        song.put(SONG_DURATION, cursor.getString(3));
                        song.put(SONG_PATH, cursor.getString(4));
                        song.put(SONG_ALBUM, cursor.getString(5));
                        song.put(SONG_ALBUM_ID, cursor.getString(6));
                        mFetchedSongs.add(song);
                    }
                } while (cursor.moveToNext());
            }
            if (cursor != null) {
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mFetchedSongs;
    }

    @Override
    protected void onPostExecute(ArrayList<HashMap<String, String>> hashMaps) {
        super.onPostExecute(hashMaps);
        mFragmentSongs.updateSong(hashMaps);
    }



}
