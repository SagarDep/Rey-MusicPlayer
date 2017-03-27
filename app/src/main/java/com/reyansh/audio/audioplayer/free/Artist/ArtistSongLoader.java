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

package com.reyansh.audio.audioplayer.free.Artist;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.reyansh.audio.audioplayer.free.Utils.MusicUtils;
import com.reyansh.audio.audioplayer.free.Utils.PreferencesUtility;

import java.util.ArrayList;
import java.util.HashMap;

public class ArtistSongLoader implements MusicUtils.names {

    public static ArrayList<HashMap<String, String>> getSongsForArtist(Context context, long artistID) {
        Cursor cursor = makeArtistSongCursor(context, artistID);
        ArrayList<HashMap<String, String>> mArtistList = new ArrayList<HashMap<String, String>>();
        if ((cursor != null) && (cursor.moveToFirst()))
            do {
                HashMap<String, String> song = new HashMap<String, String>();
                song.put(SONG_ID, cursor.getString(0));
                song.put(SONG_NAME, cursor.getString(1));
                song.put(SONG_ARTIST, cursor.getString(2));
                song.put(SONG_ALBUM, cursor.getString(3));
                song.put(SONG_DURATION, cursor.getString(4));
                song.put(SONG_ALBUM_ID, cursor.getString(6));
                song.put("tackno", cursor.getString(5));
                song.put(SONG_ARTIST_ID, String.valueOf(artistID));
                song.put(SONG_PATH, cursor.getString(7));
                mArtistList.add(song);
            }
            while (cursor.moveToNext());
        if (cursor != null)
            cursor.close();
        return mArtistList;
    }

    public static Cursor makeArtistSongCursor(Context context, long artistID) {
        ContentResolver contentResolver = context.getContentResolver();
        final String artistSongSortOrder = PreferencesUtility.getInstance(context).getArtistSongSortOrder();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String string = "is_music=1 AND title != '' AND artist_id=" + artistID;
        return contentResolver.query(uri, new String[]{"_id", "title", "artist", "album", "duration", "track", "album_id", "_data"}, string, null, artistSongSortOrder);
    }


    public static String getAlbumIdOfArtist(Context context, long artistId) {
        String albumId = null;
        ContentResolver contentResolver = context.getContentResolver();
        final String artistSongSortOrder = PreferencesUtility.getInstance(context).getArtistSongSortOrder();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String string = "is_music=1 AND title != '' AND artist_id=" + artistId;
        Cursor cursor = contentResolver.query(uri, new String[]{"album_id"}, string, null, artistSongSortOrder);
        if ((cursor != null) && (cursor.moveToFirst()))
            do {
                albumId = cursor.getString(0);
            }
            while (cursor.moveToNext());
        if (cursor != null)
            cursor.close();

        return albumId;
    }

}
