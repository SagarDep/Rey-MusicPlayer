package com.reyansh.audio.audioplayer.free.Artist;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.BaseColumns;
import android.provider.MediaStore;

import com.reyansh.audio.audioplayer.free.Common.CommonClass;
import com.reyansh.audio.audioplayer.free.Utils.MusicUtils;
import com.reyansh.audio.audioplayer.free.Utils.PreferencesUtility;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by REYANSH on 31/07/2016.
 */
public class AsyncFetchArtists extends AsyncTask<Void, Void, ArrayList<HashMap<String, String>>> {
    private static Context mContext;
    private FragmentArtist mFragmentArtist;
    private ArrayList<HashMap<String, String>> mArtistList;
    Cursor mCursor;

    public AsyncFetchArtists(Context context, FragmentArtist fragmentArtist) {
        mContext = context;
        mFragmentArtist = fragmentArtist;
        mArtistList = new ArrayList<>();
    }

    @Override
    protected ArrayList<HashMap<String, String>> doInBackground(Void... params) {
        try {
            mArtistList = new ArrayList<HashMap<String, String>>();
            String[] columns = {
                    BaseColumns._ID,
                    MediaStore.Audio.ArtistColumns.ARTIST,
                    MediaStore.Audio.ArtistColumns.NUMBER_OF_ALBUMS,
                    MediaStore.Audio.ArtistColumns.NUMBER_OF_TRACKS,

            };
            mCursor = mContext.getContentResolver().query(
                    MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI,
                    columns,
                    null,
                    null,
                    PreferencesUtility.getInstance(mContext).getArtistSortOrder());

            if (mCursor != null && mCursor.moveToFirst()) {
                do {
                    HashMap<String, String> song = new HashMap<String, String>();
                    song.put("artistID", mCursor.getString(0));
                    song.put("songArtist", mCursor.getString(1));
                    song.put("numberOfAlbums", mCursor.getString(2));
                    song.put("numberOfTracks", mCursor.getString(3));
                    mArtistList.add(song);
                } while (mCursor.moveToNext());
            }
            if (mCursor != null) {
                mCursor.close();
                mCursor = null;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return mArtistList;
    }

    @Override
    protected void onPostExecute(ArrayList<HashMap<String, String>> hashMaps) {
        super.onPostExecute(hashMaps);
        mFragmentArtist.updateData(hashMaps);
        mArtistList = hashMaps;
    }

    public static ArrayList<HashMap<String, String>> getArtistsForCursor(Cursor cursor) {
        ArrayList<HashMap<String, String>> arrayList = new ArrayList();
        if ((cursor != null) && (cursor.moveToFirst()))
            do {
                HashMap<String, String> artists = new HashMap<>();
                artists.put(MusicUtils.names.ARTISTS_ID, cursor.getString(0));
                artists.put(MusicUtils.names.ARTISTS, cursor.getString(1));
                artists.put(MusicUtils.names.ARTISTS_NO_OF_ALBUMS, cursor.getString(2));
                artists.put(MusicUtils.names.ARTISTS_NO_OF_TRACKS, cursor.getString(3));
                artists.put(MusicUtils.names.TYPE, "ARTISTS");
                arrayList.add(artists);
            }
            while (cursor.moveToNext());
        if (cursor != null)
            cursor.close();
        return arrayList;
    }


    public static ArrayList<HashMap<String, String>> getArtists(Context context, String paramString) {
        return getArtistsForCursor(makeArtistCursor(context, "artist LIKE ?", new String[]{"%" + paramString + "%"}));
    }

    public static Cursor makeArtistCursor(Context context, String selection, String[] paramArrayOfString) {
        final String artistSortOrder = ((CommonClass) context.getApplicationContext()).getPreferencesUtility().getArtistSortOrder();
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI, new String[]{
                "_id",
                "artist",
                "number_of_albums",
                "number_of_tracks"}, selection, paramArrayOfString, artistSortOrder);
        return cursor;
    }
}
