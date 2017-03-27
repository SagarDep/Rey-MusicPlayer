package com.reyansh.audio.audioplayer.free.Genres;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.MediaStore;

import com.reyansh.audio.audioplayer.free.Utils.MusicUtils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by REYANSH on 31/07/2016.
 */
public class AsyncFetchGenres extends AsyncTask<Void, Void, ArrayList<HashMap<String, String>>> implements MusicUtils.names {
    private Context mContext;
    private FragmentGenres mFragmentGenres;
    private ArrayList<HashMap<String, String>> mGenresList;
    private Cursor mCursor;

    public AsyncFetchGenres(Context context, FragmentGenres fragmentGenres) {
        mContext = context;
        mFragmentGenres = fragmentGenres;
        mGenresList = new ArrayList<>();
    }

    @Override
    protected ArrayList<HashMap<String, String>> doInBackground(Void... params) {
        try {
            String[] columns = {
                    MediaStore.Audio.Genres._ID,
                    MediaStore.Audio.Genres.NAME

            };
            mCursor = mContext.getContentResolver().query(
                    MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI,
                    columns,
                    null,
                    null,
                    MediaStore.Audio.Genres.NAME);

            if (mCursor != null && mCursor.moveToFirst()) {
                do {
                    HashMap<String, String> song = new HashMap<String, String>();
                    song.put(GENRES_ID, mCursor.getString(0));
                    song.put(GENRES_NAME, mCursor.getString(1));
                    mGenresList.add(song);
                } while (mCursor.moveToNext());
            }
            if (mCursor != null) {
                mCursor.close();
                mCursor = null;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return mGenresList;
    }

    @Override
    protected void onPostExecute(ArrayList<HashMap<String, String>> hashMaps) {
        super.onPostExecute(hashMaps);
        mFragmentGenres.updateData(hashMaps);

    }
}
