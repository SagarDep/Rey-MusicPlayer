package com.reyansh.audio.audioplayer.free.PlayList;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.BaseColumns;
import android.provider.MediaStore;

import com.reyansh.audio.audioplayer.free.Utils.MusicUtils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by REYANSH on 31/07/2016.
 */
public class AsyncFetchPlaylist extends AsyncTask<Void, Void, ArrayList<HashMap<String, String>>> implements MusicUtils.names {
    private ArrayList<HashMap<String, String>> playtList = new ArrayList<>();
    private Cursor mCursor;
    private Context mContext;
    private FragmentPlaylist mFragmentPlaylist;

    AsyncFetchPlaylist(Context context, FragmentPlaylist fragmentPlaylist) {
        mContext = context;
        mFragmentPlaylist = fragmentPlaylist;
    }

    @Override
    protected ArrayList<HashMap<String, String>> doInBackground(Void... params) {

        try {

            String[] columns = {
                    BaseColumns._ID,
                    MediaStore.Audio.Playlists._ID,
                    MediaStore.Audio.Playlists.NAME

            };
            mCursor = mContext.getContentResolver().query(
                    MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
                    columns,
                    null,
                    null,
                    MediaStore.Audio.Playlists.DEFAULT_SORT_ORDER);
            HashMap<String, String> recently = new HashMap<String, String>();
            recently.put(PLAYLIST_ID, "-1");
            recently.put(PLAYLIST_NAME, "Recently Added");
            HashMap<String, String> favorites = new HashMap<String, String>();
            favorites.put(PLAYLIST_ID, "-2");
            favorites.put(PLAYLIST_NAME, "Favorites");
            HashMap<String, String> toptracks = new HashMap<String, String>();
            toptracks.put(PLAYLIST_ID, "-3");
            toptracks.put(PLAYLIST_NAME, "Top Played");
            HashMap<String, String> recentlyplayed = new HashMap<String, String>();
            recentlyplayed.put(PLAYLIST_ID, "-4");
            recentlyplayed.put(PLAYLIST_NAME, "Recently Played");
            playtList.add(0, recently);
            playtList.add(1, favorites);
            playtList.add(2, toptracks);
            playtList.add(3, recentlyplayed);

            if (mCursor != null && mCursor.moveToFirst()) {
                do {
                    HashMap<String, String> song = new HashMap<String, String>();
                    song.put(PLAYLIST_ID, mCursor.getString(1));
                    song.put(PLAYLIST_NAME, mCursor.getString(2));
                    playtList.add(song);
                } while (mCursor.moveToNext());
            }
            if (mCursor != null) {
                mCursor.close();
                mCursor = null;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return playtList;
    }

    @Override
    protected void onPostExecute(ArrayList<HashMap<String, String>> hashMaps) {
        super.onPostExecute(hashMaps);
        mFragmentPlaylist.updateData(hashMaps);
    }
}
