package com.reyansh.audio.audioplayer.free.Utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.provider.DocumentFile;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.Toast;


import com.reyansh.audio.audioplayer.free.Common.CommonClass;
import com.reyansh.audio.audioplayer.free.Database.DataBaseHelper;
import com.reyansh.audio.audioplayer.free.R;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by REYANSH on 03/01/2016.
 */
public class MusicUtils {
    public final static int URI_REQUEST_CODE = 29;

    public interface Defs {
        public final static int ADD_TO_PLAYLIST = 1;
        public final static int USE_AS_RINGTONE = 2;
        public final static int PLAYLIST_SELECTED = 3;
        public final static int NEW_PLAYLIST = 4;
        public final static int SET_TIMER = 5;
        public final static int AB_REPEAT = 6;
        public final static int GOTO = 7;
        public final static int GOTO_SETTINGS = 8;
        public final static int ALBUM = 9;
        public final static int DELETE_ITEM = 10;
        public final static int GERNE = 11;
        public final static int ARTIST = 12;
        public final static int ADD_TO_QUEUE = 15;
        public final static int PLAY_NEXT = 16;
        public final static int PLAY_THIS = 17;
        public final static int RENAME_PLAYLIST = 19;
        public final static int DELETE_PLAYLIST = 20;
        public final static int PLAY = 21;
        public final static int RECENTLY_ADDED_WEEK = 22;
        public final static int SHARE_ITEM = 23;
        public final static int CLEAR_FAVORITES = 24;
        public final static int ADD_TO_FAVORITES = 25;
        public final static int CLEAR_TOPTRACKS = 26;
        public final static int RECENTLY_PLAYED = 27;
        public final static int CLEAR_RECENTLY_PLAYED = 28;
        public final static int A_B_REPEAT = 29;
        public final static int EDIT_TAGS = 32;

    }

    public interface names {
        public final static String TYPE = "type";
        public final static String SONG_ID = "songId";
        public final static String SONG_NAME = "songName";
        public final static String SONG_ALBUM = "songALBUM";
        public final static String SONG_ARTIST = "songArtist";
        public final static String SONG_ARTIST_ID = "songArtistid";
        public final static String SONG_DURATION = "songDuration";
        public final static String TOTAL_SONG_DURATION = "totalsongDuration";
        public final static String TOTAL_TRACKS = "totaltracks";
        public final static String SONG_YEAR = "songYear";
        public final static String SONG_PATH = "songUri";
        public final static String SONG_ALBUM_ID = "album_id";
        public final static String ALBUM_ARTIST_NAME = "albumartistname";
        public final static String ALBUM_COVER_PATH = "albumcovrepath";
        public final static String ALBUM_NUMBER_OF_SONGS = "albumnoofsongs";
        public final static String ALBUM_YEAR = "albumyear";
        public final static String GENRES_ID = "genresID";
        public final static String GENRES_NAME = "genresName";
        public final static String PLAYLIST_NAME = "playlistName";
        public final static String PLAYLIST_ID = "playlistId";


        public final static String ALBUM_ID = "album_id";
        public final static String ALBUM = "album";
        public final static String ALBUM_ARTIST = "album_artist";
        public final static String ALBUM_ARTIST_ID = "album_artist_id";
        public final static String ALBUM_NO_OF_SONGS = "album_no_of_songs";
        public final static String ALBUM_MIN_YEAR = "album_min_year";


        public final static String ARTISTS_ID = "artistsId";
        public final static String ARTISTS = "artists";
        public final static String ARTISTS_NO_OF_ALBUMS = "artists_no_of_albums";
        public final static String ARTISTS_NO_OF_TRACKS = "artists_no_of_tracks";
    }

    public static void addToPlaylist(Context context, long[] ids, long playlistid) {
        if (ids == null) {
            Log.e("MusicBase", "ListSelection null");
        } else {
            int size = ids.length;
            ContentResolver resolver = context.getContentResolver();
            String[] cols = new String[]{"count(*)"};
            Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlistid);
            Cursor cur = resolver.query(uri, cols, null, null, null);
            cur.moveToFirst();
            int base = cur.getInt(0);
            cur.close();
            int numinserted = 0;
            for (int i = 0; i < size; i += 1000) {
                makeInsertItems(ids, i, 1000, base);
                numinserted += resolver.bulkInsert(uri, sContentValuesCache);
            }
        }
    }

    private static ContentValues[] sContentValuesCache = null;

    private static void makeInsertItems(long[] ids, int offset, int len, int base) {
        if (offset + len > ids.length) {
            len = ids.length - offset;
        }
        if (sContentValuesCache == null || sContentValuesCache.length != len) {
            sContentValuesCache = new ContentValues[len];
        }
        for (int i = 0; i < len; i++) {
            if (sContentValuesCache[i] == null) {
                sContentValuesCache[i] = new ContentValues();
            }
            sContentValuesCache[i].put(MediaStore.Audio.Playlists.Members.PLAY_ORDER, base + offset + i);
            sContentValuesCache[i].put(MediaStore.Audio.Playlists.Members.AUDIO_ID, ids[offset + i]);
        }
    }

    public static Cursor query(Context context, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return query(context, uri, projection, selection, selectionArgs, sortOrder, 0);
    }


    public static Cursor query(Context context, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder, int limit) {
        try {
            ContentResolver resolver = context.getContentResolver();
            if (resolver == null) {
                return null;
            }
            if (limit > 0) {
                uri = uri.buildUpon().appendQueryParameter("limit", "" + limit).build();
            }
            return resolver.query(uri, projection, selection, selectionArgs, sortOrder);
        } catch (UnsupportedOperationException ex) {
            return null;
        }

    }


    public static final String makeShortTimeString(final Context context, long secs) {
        long hours, mins;

        hours = secs / 3600;
        secs %= 3600;
        mins = secs / 60;
        secs %= 60;

        final String durationFormat = context.getResources().getString(
                hours == 0 ? R.string.durationformatshort : R.string.durationformatlong);
        return String.format(durationFormat, hours, mins, secs);
    }


    public static void setRingtone(Context context, long id) {
        ContentResolver resolver = context.getContentResolver();
        Uri ringUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);
        try {
            ContentValues values = new ContentValues(2);
            values.put(MediaStore.Audio.Media.IS_RINGTONE, "1");
            values.put(MediaStore.Audio.Media.IS_ALARM, "1");
            resolver.update(ringUri, values, null, null);
        } catch (UnsupportedOperationException ex) {
            // most likely the card just got unmounted
            Log.e("Notset", "couldn't set ringtone flag for id " + id);
            return;
        }

        String[] cols = new String[]{
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.TITLE
        };

        String where = MediaStore.Audio.Media._ID + "=" + id;
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                cols, where, null, null);
        try {
            if (cursor != null && cursor.getCount() == 1) {
                cursor.moveToFirst();
                Settings.System.putString(resolver, Settings.System.RINGTONE, ringUri.toString());
                String message = context.getString(R.string.ringtone_set, cursor.getString(2));
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public static void removeFromPlaylist(Context resolver, Long audioId, Long plid, String songPath) {
        if (plid != -1 && plid != -2 && plid != -3 && plid != -4) {
            String[] cols = new String[]{"count(*)"};
            Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", plid);
            Cursor cur = resolver.getContentResolver().query(uri, cols, null, null, null);
            cur.moveToFirst();
            cur.close();
            resolver.getContentResolver().delete(uri, MediaStore.Audio.Playlists.Members._ID + "=" + audioId, null);

        } else if (plid == -2) {
            DataBaseHelper.getDatabaseHelper(resolver).removeFromFavorites(audioId);
        } else if (plid == -3) {
            DataBaseHelper.getDatabaseHelper(resolver).removeFromTopTracks(audioId);
        } else if (plid == -4) {
            DataBaseHelper.getDatabaseHelper(resolver).removeRecentlyPlayed(audioId);
        } else {
            deleteSongs(songPath, resolver);
        }
    }

    public static void makePlaylistMenu(Context context, SubMenu sub, int groupdId) {
        String[] cols = new String[]{
                MediaStore.Audio.Playlists._ID,
                MediaStore.Audio.Playlists.NAME
        };
        ContentResolver resolver = context.getContentResolver();
        if (resolver == null) {
            System.out.println("resolver = null");
        } else {
            String whereclause = MediaStore.Audio.Playlists.NAME + " != ''";
            Cursor cur = resolver.query(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
                    cols, whereclause, null,
                    MediaStore.Audio.Playlists.NAME);
            sub.clear();
            sub.add(groupdId, Defs.NEW_PLAYLIST, 0, R.string.new_playlist);
            if (cur != null && cur.getCount() > 0) {
                //sub.addSeparator(1, 0);
                cur.moveToFirst();
                while (!cur.isAfterLast()) {
                    Intent intent = new Intent();
                    intent.putExtra("playlist", cur.getLong(0));
                    sub.add(groupdId, Defs.PLAYLIST_SELECTED, 0, cur.getString(1)).setIntent(intent);
                    cur.moveToNext();
                }
            }
            if (cur != null) {
                cur.close();
            }
        }
    }

    public static void overflowsubmenu(Context context,SubMenu subMenu) {
        String[] cols = new String[]{
                MediaStore.Audio.Playlists._ID,
                MediaStore.Audio.Playlists.NAME
        };
        ContentResolver resolver = context.getContentResolver();
        if (resolver == null) {
            System.out.println("resolver = null");
        } else {
            String whereclause = MediaStore.Audio.Playlists.NAME + " != ''";
            Cursor cur = resolver.query(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
                    cols, whereclause, null,
                    MediaStore.Audio.Playlists.NAME);
            subMenu.clear();
            subMenu.add(0, Defs.NEW_PLAYLIST, 0, R.string.new_playlist);
            if (cur != null && cur.getCount() > 0) {
                //sub.addSeparator(1, 0);
                cur.moveToFirst();
                while (!cur.isAfterLast()) {
                    Intent intent = new Intent();
                    intent.putExtra("playlist", cur.getLong(0));
                    subMenu.add(0,Defs.PLAYLIST_SELECTED, 0, cur.getString(1)).setIntent(intent);
                    cur.moveToNext();
                }
            }
            if (cur != null) {
                cur.close();
            }
        }
    }


    public static void deleteSongs(String path, final Context context) {

        try {
            MediaScannerConnection.scanFile(context, new String[]{path},
                    null, new MediaScannerConnection.OnScanCompletedListener() {

                        public void onScanCompleted(String path, final Uri uri) {
                            context.getContentResolver().delete(uri, null, null);
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
        Toast.makeText(context, "Song Deleted", Toast.LENGTH_SHORT).show();

    }


    public static boolean dailog(Activity activity, final Context context) {
        activity.runOnUiThread(new Runnable() {
            public void run() {

            }
        });
        return false;
    }


    public static ArrayList<HashMap<String, String>> searchSongs(Context context, String searchString) {
        return getSongsForCursor(makeSongCursor(context, "title LIKE ?", new String[]{"%" + searchString + "%"}));
    }

    public static Cursor makeSongCursor(Context context, String selection, String[] paramArrayOfString) {
        String selectionStatement = "is_music=1 AND title != ''";
        final String songSortOrder = PreferencesUtility.getInstance(context).getSongSortOrder();

        if (!TextUtils.isEmpty(selection)) {
            selectionStatement = selectionStatement + " AND " + selection;
        }
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, new String[]{
                        MediaStore.Audio.Media._ID,
                        MediaStore.Audio.Media.TITLE,
                        MediaStore.Audio.Media.ARTIST,
                        MediaStore.Audio.Media.DURATION,
                        MediaStore.Audio.Media.DATA,
                        MediaStore.Audio.Media.ALBUM,
                        MediaStore.Audio.Media.ALBUM_ID}
                , selectionStatement, paramArrayOfString, songSortOrder);

        return cursor;
    }

    public static Cursor getSongForID(Context context, long id) {
        return makeSongCursor(context, "_id=" + String.valueOf(id), null);
    }

    public static ArrayList<HashMap<String, String>> getSongsForCursor(Cursor cursor) {
        ArrayList<HashMap<String, String>> arrayList = new ArrayList<HashMap<String, String>>();
        if ((cursor != null) && (cursor.moveToFirst()))
            do {
                if (cursor.getInt(3) > 6000) {
                    HashMap<String, String> song = new HashMap<String, String>();

                    song.put(names.SONG_ID, cursor.getString(0));
                    song.put(names.SONG_NAME, cursor.getString(1));
                    song.put(names.SONG_ARTIST, cursor.getString(2));
                    song.put(names.SONG_DURATION, cursor.getString(3));
                    song.put(names.SONG_PATH, cursor.getString(4));
                    song.put(names.SONG_ALBUM, cursor.getString(5));
                    song.put(names.SONG_ALBUM_ID, cursor.getString(6));
                    song.put(names.TYPE, "SONGS");
                    if (cursor.getString(0) != null)
                        arrayList.add(song);
                }
            }
            while (cursor.moveToNext());
        if (cursor != null)
            cursor.close();
        return arrayList;
    }

    public static int getIntPref(Context context, String name, int def) {
        SharedPreferences prefs = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        return prefs.getInt(name, def);
    }


    public static void animate(final ImageView imageView, final int images[], final int imageIndex, final boolean forever) {

        //imageView <-- The View which displays the images
        //images[] <-- Holds R references to the images to display
        //imageIndex <-- index of the first image to show in images[]
        //forever <-- If equals true then after the last image it starts all over again with the first image resulting in an infinite loop. You have been warned.

        int fadeInDuration = 5000; // Configure time values here
        int timeBetween = 3000;
        int fadeOutDuration = 5000;

        imageView.setVisibility(View.INVISIBLE);    //Visible or invisible by default - this will apply when the animation ends
        imageView.setImageResource(images[imageIndex]);

        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new DecelerateInterpolator()); // add this
        fadeIn.setDuration(fadeInDuration);

        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator()); // and this
        fadeOut.setStartOffset(fadeInDuration + timeBetween);
        fadeOut.setDuration(fadeOutDuration);

        AnimationSet animation = new AnimationSet(false); // change to false
        animation.addAnimation(fadeIn);
        animation.addAnimation(fadeOut);
        animation.setRepeatCount(1);
        imageView.setAnimation(animation);

        animation.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationEnd(Animation animation) {
                if (images.length - 1 > imageIndex) {
                    animate(imageView, images, imageIndex + 1, forever); //Calls itself until it gets to the end of the array
                } else {
                    if (forever == true) {
                        animate(imageView, images, 0, forever);  //Calls itself to start the animation all over again in a loop if forever = true
                    }
                }
            }

            public void onAnimationRepeat(Animation animation) {
                // TODO Auto-generated method stub
            }

            public void onAnimationStart(Animation animation) {
                // TODO Auto-generated method stub
            }
        });
    }

    public static void deletePlaylist(Context context, String playlistid) {
        ContentResolver resolver = context.getContentResolver();
        String where = MediaStore.Audio.Playlists._ID + "=?";
        String[] whereVal = {playlistid};
        resolver.delete(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, where, whereVal);
        return;
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inDither = true;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static void shareTheMusic(ArrayList<HashMap<String, String>> mSongtList, int pos, Context context) {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        Uri uri = Uri.parse("file://" + mSongtList.get(pos).get(names.SONG_PATH));
        sharingIntent.setType("audio/*");
        sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);
        context.startActivity(Intent.createChooser(sharingIntent, "Share Track Using"));
    }

    public static void shareTheMusic(HashMap<String, String> mSongtList, Context context) {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        Uri uri = Uri.parse("file://" + mSongtList.get(names.SONG_PATH));
        sharingIntent.setType("audio/*");
        sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);
        sharingIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(Intent.createChooser(sharingIntent, "Share Track Using"));
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }


    public static Bitmap Shrinkmethod(String file, int width, int height) {
        BitmapFactory.Options bitopt = new BitmapFactory.Options();
        bitopt.inJustDecodeBounds = true;
        Bitmap bit = BitmapFactory.decodeFile(file, bitopt);
        int h = (int) Math.ceil(bitopt.outHeight / (float) height);
        int w = (int) Math.ceil(bitopt.outWidth / (float) width);

        if (h > 1 || w > 1) {
            if (h > w) {
                bitopt.inSampleSize = h;

            } else {
                bitopt.inSampleSize = w;
            }
        }
        bitopt.inJustDecodeBounds = false;
        bit = BitmapFactory.decodeFile(file, bitopt);
        return bit;
    }


    public static Uri getAlbumArtUri(long paramInt) {
        return ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), paramInt);
    }


    public static ArrayList<HashMap<String, String>> playPlaylist(Context context, long plid) {
        ArrayList<HashMap<String, String>> list = getSongListForPlaylist(context, plid);
        return list;
    }

    public static void deleteTracks(Context context, long[] list) {

        String[] cols = new String[]{MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.ALBUM_ID};
        StringBuilder where = new StringBuilder();
        where.append(MediaStore.Audio.Media._ID + " IN (");
        for (int i = 0; i < list.length; i++) {
            where.append(list[i]);
            if (i < list.length - 1) {
                where.append(",");
            }
        }
        where.append(")");
        Cursor c = query(context, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, cols,
                where.toString(), null, null);

        if (c != null) {

            // step 1: remove selected tracks from the current playlist, as well
            // as from the album art cache
       /*     try {
                c.moveToFirst();
                while (! c.isAfterLast()) {
                    // remove from current playlist
                    long id = c.getLong(0);
                    sService.removeTrack(id);
                    // remove from album art cache
                    long artIndex = c.getLong(2);
                    synchronized(sArtCache) {
                        sArtCache.remove(artIndex);
                    }
                    c.moveToNext();
                }
            } catch (RemoteException ex) {
            }*/

            // step 2: remove selected tracks from the database
            context.getContentResolver().delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, where.toString(), null);

            // step 3: remove files from card
            c.moveToFirst();
            while (!c.isAfterLast()) {
                String name = c.getString(1);
                File f = new File(name);
                try {  // File.delete can throw a security exception
                    if (!f.delete()) {
                        // I'm not sure if we'd ever get here (deletion would
                        // have to fail, but no exception thrown)
                        Log.e("MusicUtils", "Failed to delete file " + name);
                    }
                    c.moveToNext();
                } catch (SecurityException ex) {
                    c.moveToNext();
                }
            }
            c.close();
        }

      /*  String message = context.getResources().getQuantityString(
             //   R.plurals.NNNtracksdeleted, list.length, Integer.valueOf(list.length));*/

        //Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        // We deleted a number of tracks, which could affect any number of things
        // in the media content domain, so update everything.
        context.getContentResolver().notifyChange(Uri.parse("content://media"), null);
    }

    public static ArrayList<HashMap<String, String>> getSongListForPlaylist(Context context, long plid) {
        final String[] ccols = new String[]{
                MediaStore.Audio.Playlists.Members.AUDIO_ID,
                MediaStore.Audio.Playlists.Members.TITLE,
                MediaStore.Audio.Playlists.Members.ARTIST,
                MediaStore.Audio.Playlists.Members.DURATION,
                MediaStore.Audio.Playlists.Members.DATA,
                MediaStore.Audio.Playlists.Members.ALBUM,
                MediaStore.Audio.Playlists.Members.ALBUM_ID,
        };
        Cursor cursor = query(context, MediaStore.Audio.Playlists.Members.getContentUri("external", plid),
                ccols, null, null, MediaStore.Audio.Playlists.Members.DEFAULT_SORT_ORDER);

        ArrayList<HashMap<String, String>> list = getSongListForCursor(cursor);
        cursor.close();
        return list;
    }

    static ArrayList<HashMap<String, String>> ok = new ArrayList<HashMap<String, String>>();

    public static ArrayList<HashMap<String, String>> getSongListForCursor(Cursor cursor) {
        if (cursor == null) {
            return ok;
        }
        if (cursor != null && cursor.moveToFirst()) {
            do {
                HashMap<String, String> oka = new HashMap<String, String>();
                oka.put(names.SONG_ID, cursor.getString(0));
                oka.put(names.SONG_NAME, cursor.getString(1));
                oka.put(names.SONG_ARTIST, cursor.getString(2));
                oka.put(names.SONG_DURATION, cursor.getString(3));
                oka.put(names.SONG_PATH, cursor.getString(4));
                oka.put(names.SONG_ALBUM, cursor.getString(5));
                oka.put(names.SONG_ALBUM_ID, cursor.getString(6));
                ok.add(oka);
            } while (cursor.moveToNext());

        }
        return ok;
    }


    public static void askForUriPermission(final Activity context, int requestcode) {
        final Dialog dialog = new Dialog(context);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_sd_access);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnim;

        (dialog.findViewById(R.id.ascending)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                context.startActivityForResult(intent, MusicUtils.URI_REQUEST_CODE);
                dialog.dismiss();
            }
        });
        (dialog.findViewById(R.id.descending)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static boolean deleteViaContentProvider(Context context, String fullname) {
        Uri uri = getFileUri(context, fullname);
        if (uri == null) {
            return false;
        }
        try {
            ContentResolver resolver = context.getContentResolver();
            // change type to image, otherwise nothing will be deleted
            ContentValues contentValues = new ContentValues();
            int media_type = 1;
            contentValues.put("media_type", media_type);
            resolver.update(uri, contentValues, null, null);

            return resolver.delete(uri, null, null) > 0;
        } catch (Throwable e) {
            return false;
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private static Uri getFileUri(Context context, String fullname) {
        Uri uri = null;
        Cursor cursor = null;
        ContentResolver contentResolver = null;
        try {
            contentResolver = context.getContentResolver();
            if (contentResolver == null)
                return null;
            uri = MediaStore.Files.getContentUri("external");
            String[] projection = new String[2];
            projection[0] = "_id";
            projection[1] = "_data";
            String selection = "_data = ? ";    // this avoids SQL injection
            String[] selectionParams = new String[1];
            selectionParams[0] = fullname;
            String sortOrder = "_id";
            cursor = contentResolver.query(uri, projection, selection, selectionParams, sortOrder);
            if (cursor != null) {
                try {
                    if (cursor.getCount() > 0) // file present!
                    {
                        cursor.moveToFirst();
                        int dataColumn = cursor.getColumnIndex("_data");
                        String s = cursor.getString(dataColumn);
                        if (!s.equals(fullname))
                            return null;
                        int idColumn = cursor.getColumnIndex("_id");
                        long id = cursor.getLong(idColumn);
                        uri = MediaStore.Files.getContentUri("external", id);
                    } else // file isn't in the media database!
                    {
                        ContentValues contentValues = new ContentValues();
                        contentValues.put("_data", fullname);
                        uri = MediaStore.Files.getContentUri("external");
                        uri = contentResolver.insert(uri, contentValues);
                    }
                } catch (Throwable e) {
                    uri = null;
                } finally {
                    cursor.close();
                }
            }
        } catch (Throwable e) {
            uri = null;
        }
        return uri;
    }

    public static Uri getDocumentUri(File file, boolean isDirectory, Context mContext) {
        try {
            return getDocumentFile(file, isDirectory, mContext).getUri();
        } catch (NullPointerException e) {

            return null;
        }
    }

    public static DocumentFile getDocumentFile(final File file, final boolean isDirectory, Context mContext) {
        CommonClass mApp = (CommonClass) mContext.getApplicationContext();
        String baseFolder = getExtSdCardFolder(file, mContext);
        if (baseFolder == null) {
            boolean f = file.delete();
            if (f) {
                MusicUtils.deleteViaContentProvider(mContext, file.getAbsolutePath());
            }
            return null;
        }
        String relativePath = null;
        try {
            String fullPath = file.getCanonicalPath();
            relativePath = fullPath.substring(baseFolder.length() + 1);
        } catch (IOException e) {
            return null;
        }

        Uri treeUri = Uri.parse(mApp.getPreferencesUtility().getSharedPreferenceUri());

        if (treeUri == null) {
            return null;
        }

        // start with root of SD card and then parse through document tree.
        DocumentFile document = DocumentFile.fromTreeUri(mContext, treeUri);

        String[] parts = relativePath.split("\\/");
        for (int i = 0; i < parts.length; i++) {
            DocumentFile nextDocument = document.findFile(parts[i]);

            if (nextDocument == null) {
                if ((i < parts.length - 1) || isDirectory) {
                    nextDocument = document.createDirectory(parts[i]);
                } else {
                    nextDocument = document.createFile("image", parts[i]);
                }
            }
            document = nextDocument;
        }
        return document;
    }


    public static String getExtSdCardFolder(final File file, Context mContext) {
        String[] extSdPaths = getExtSdCardPaths(mContext);
        try {
            for (int i = 0; i < extSdPaths.length; i++) {
                if (file.getCanonicalPath().startsWith(extSdPaths[i])) {
                    return extSdPaths[i];
                }
            }
        } catch (IOException e) {
            return null;
        }
        return null;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private static String[] getExtSdCardPaths(Context mContext) {
        List<String> paths = new ArrayList<>();
        for (File file : mContext.getExternalFilesDirs("external")) {
            if (file != null && !file.equals(mContext.getExternalFilesDir("external"))) {
                int index = file.getAbsolutePath().lastIndexOf("/Android/data");
                if (index < 0) {
                    Log.w("asd", "Unexpected external file dir: " + file.getAbsolutePath());
                } else {
                    String path = file.getAbsolutePath().substring(0, index);
                    try {
                        path = new File(path).getCanonicalPath();
                    } catch (IOException e) {
                        // Keep non-canonical path.
                    }
                    paths.add(path);
                }
            }
        }
        return paths.toArray(new String[paths.size()]);
    }


    public static void insertIntoPlayList(final Context context, final MenuItem item, final ArrayList<HashMap<String, String>> data) {
        final long[] list = new long[data.size()];
        new AsyncTask<Void, Void, long[]>() {
            @Override
            protected long[] doInBackground(Void... params) {
                for (int i = 0; i < data.size(); i++) {
                    list[i] = Long.parseLong(data.get(i).get(names.SONG_ID));
                }
                return list;
            }

            @Override
            protected void onPostExecute(long[] longs) {
                super.onPostExecute(longs);
                long playlist = item.getIntent().getLongExtra("playlist", 0);
                MusicUtils.addToPlaylist(context, longs, playlist);
                Toast.makeText(context, MusicUtils.makeLabel(context, R.plurals.Nsongs, longs.length) + " Added to playlist", Toast.LENGTH_SHORT).show();
            }
        }.execute();
    }


    public static boolean isKitkat() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            return true;
        } else {
            return false;
        }
    }

    public static final String makeLabel(final Context context, final int pluralInt,
                                         final int number) {
        return context.getResources().getQuantityString(pluralInt, number, number);
    }

}