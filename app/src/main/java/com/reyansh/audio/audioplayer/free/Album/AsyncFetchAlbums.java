package com.reyansh.audio.audioplayer.free.Album;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.MediaStore;

import com.reyansh.audio.audioplayer.free.Common.CommonClass;
import com.reyansh.audio.audioplayer.free.Utils.MusicUtils;
import com.reyansh.audio.audioplayer.free.Utils.PreferencesUtility;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by REYANSH on 31/07/2016.
 */
public class AsyncFetchAlbums extends AsyncTask<Void, Void, ArrayList<HashMap<String, String>>> implements MusicUtils.names {
    private Context mContext;
    private FragmentAlbum mFragmentAlbum;
    private Cursor mCursor;
    ArrayList<HashMap<String, String>> mAlbumList;

    public AsyncFetchAlbums(Context context, FragmentAlbum fragmentAlbum) {
        mContext = context;
        mFragmentAlbum = fragmentAlbum;
        mAlbumList = new ArrayList<>();
    }

    @Override
    protected ArrayList<HashMap<String, String>> doInBackground(Void... params) {

        try {
            String[] columns = {
                    "_id",
                    MediaStore.Audio.AlbumColumns.ALBUM,
                    MediaStore.Audio.AlbumColumns.ARTIST,
                    MediaStore.Audio.AlbumColumns.ALBUM_ART,
                    MediaStore.Audio.AlbumColumns.NUMBER_OF_SONGS,
                    MediaStore.Audio.AlbumColumns.FIRST_YEAR,
                    MediaStore.Audio.AlbumColumns.LAST_YEAR,
            };
            mCursor = mContext.getContentResolver().query(
                    MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                    columns,
                    null,
                    null,
                    PreferencesUtility.getInstance(mContext).getAlbumSortOrder());
            if (mCursor != null && mCursor.moveToFirst()) {
                do {
                    HashMap<String, String> album = new HashMap<String, String>();
                    album.put(SONG_ALBUM_ID, mCursor.getString(0));
                    album.put(SONG_ALBUM, mCursor.getString(1));
                    album.put(SONG_ARTIST, mCursor.getString(2));
                    album.put(ALBUM_COVER_PATH, mCursor.getString(3));
                    album.put(ALBUM_NUMBER_OF_SONGS, mCursor.getString(4));
                    album.put(ALBUM_YEAR, mCursor.getString(5));
                    mAlbumList.add(album);
                } while (mCursor.moveToNext());
            }
            if (mCursor != null) {
                mCursor.close();
                mCursor = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mAlbumList;
    }

    @Override
    protected void onPostExecute(ArrayList<HashMap<String, String>> hashMaps) {
        super.onPostExecute(hashMaps);
        mFragmentAlbum.updateData(hashMaps);
    }

    public static ArrayList<HashMap<String, String>> getAlbumsForCursor(Cursor cursor) {
        ArrayList<HashMap<String, String>> arrayList = new ArrayList();
        if ((cursor != null) && (cursor.moveToFirst()))
            do {
                HashMap<String, String> albums = new HashMap<String, String>();
                albums.put(ALBUM_ID, cursor.getString(0));
                albums.put(ALBUM, cursor.getString(1));
                albums.put(ALBUM_ARTIST, cursor.getString(2));
                albums.put(ALBUM_ARTIST_ID, cursor.getString(3));
                albums.put(ALBUM_NUMBER_OF_SONGS, cursor.getString(4));
                albums.put(ALBUM_MIN_YEAR, cursor.getString(5));
                albums.put(ALBUM_COVER_PATH, cursor.getString(6));
                albums.put(TYPE, "ALBUMS");
                arrayList.add(albums);
            }
            while (cursor.moveToNext());
        if (cursor != null)
            cursor.close();
        return arrayList;
    }

    public static ArrayList<HashMap<String, String>> getAlbums(Context context, String paramString) {
        return getAlbumsForCursor(makeAlbumCursor(context, "album LIKE ?", new String[]{"%" + paramString + "%"}));
    }

    public static Cursor makeAlbumCursor(Context context, String selection, String[] paramArrayOfString) {
        final String albumSortOrder = ((CommonClass) context.getApplicationContext()).getPreferencesUtility().getAlbumSortOrder();
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, new String[]{
                "_id", "album", "artist", "artist_id", "numsongs", "minyear", "album_art"}, selection, paramArrayOfString, null);
        return cursor;
    }


}
