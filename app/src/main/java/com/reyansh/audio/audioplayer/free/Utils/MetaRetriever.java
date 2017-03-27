package com.reyansh.audio.audioplayer.free.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.graphics.Palette;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.reyansh.audio.audioplayer.free.R;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class MetaRetriever implements MusicUtils.names {
    String songId = "";
    String songName = "";
    String songPath = "";
    String albumName = "";
    String songArtist;
    String album_id;
    String song_Duration;
    private Bitmap artWork;

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getSongId() {
        return songId;
    }

    public void setsongId(String mSongId) {
        this.songId = mSongId;
    }

    public String getSongPath() {
        return songPath;
    }

    public void setSongPath(String songPath) {
        this.songPath = songPath;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getSongArtist() {
        return songArtist;
    }

    public void setSongArtist(String songArtist) {
        this.songArtist = songArtist;
    }

    public String getAlbum_id() {

        return album_id;
    }

    public static MetaRetriever getsInstance() {
        if (sInstance == null) {
            sInstance = new MetaRetriever();
        }
        return sInstance;
    }

    public void setAlbum_id(String album_id) {

        this.album_id = album_id;
    }

    public String getSong_Duration() {
        return song_Duration;
    }

    public void setSong_Duration(String song_Duration) {
        this.song_Duration = song_Duration;
    }

    public Drawable getBlurredArtWork(Context context){
        if (PreferencesUtility.getInstance(context).getUserBg()) {
            Bitmap artWork = ImageLoader.getInstance().loadImageSync(PreferencesUtility.getInstance(context).getUserBgUri());
            return new BitmapDrawable(context.getResources(), artWork);
        } else {
            if (artWork != null) {
                return createBlurredImageFromBitmap(artWork, context);
            } else {
                artWork = ImageLoader.getInstance().loadImageSync("drawable://" + R.drawable.main_ic);
            }
            return createBlurredImageFromBitmap(artWork, context);
        }
    }

    public Bitmap getBlurredArtWorkA(Context context) {
        Bitmap d = null;
        if (artWork != null) {
            int inSampleSize = 9;
            android.support.v8.renderscript.RenderScript rs = android.support.v8.renderscript.RenderScript.create(context);
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = inSampleSize;

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            artWork.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] imageInByte = stream.toByteArray();
            ByteArrayInputStream bis = new ByteArrayInputStream(imageInByte);
            Bitmap blurTemplate = BitmapFactory.decodeStream(bis, null, options);
            final android.support.v8.renderscript.Allocation input = android.support.v8.renderscript.Allocation.createFromBitmap(rs, blurTemplate);
            final android.support.v8.renderscript.Allocation output = android.support.v8.renderscript.Allocation.createTyped(rs, input.getType());
            final android.support.v8.renderscript.ScriptIntrinsicBlur script = android.support.v8.renderscript.ScriptIntrinsicBlur.create(rs, android.support.v8.renderscript.Element.U8_4(rs));
            script.setRadius(8f);
            script.setInput(input);
            script.forEach(output);
            output.copyTo(blurTemplate);
            d = blurTemplate;
        }
        return d;
    }

    public Bitmap getArtWork() {
        return artWork;
    }

    public void setArtWork(Bitmap artWork) {
        this.artWork = artWork;
    }

    public static Drawable createBlurredImageFromBitmap(Bitmap bitmap, Context context) {

        int inSampleSize = 9;
        android.support.v8.renderscript.RenderScript rs = android.support.v8.renderscript.RenderScript.create(context);
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = inSampleSize;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] imageInByte = stream.toByteArray();
        ByteArrayInputStream bis = new ByteArrayInputStream(imageInByte);
        Bitmap blurTemplate = BitmapFactory.decodeStream(bis, null, options);
        final android.support.v8.renderscript.Allocation input = android.support.v8.renderscript.Allocation.createFromBitmap(rs, blurTemplate);
        final android.support.v8.renderscript.Allocation output = android.support.v8.renderscript.Allocation.createTyped(rs, input.getType());
        final android.support.v8.renderscript.ScriptIntrinsicBlur script = android.support.v8.renderscript.ScriptIntrinsicBlur.create(rs, android.support.v8.renderscript.Element.U8_4(rs));
        script.setRadius(8f);
        script.setInput(input);
        script.forEach(output);
        output.copyTo(blurTemplate);
        return new BitmapDrawable(context.getResources(), blurTemplate);
    }

    public static MetaRetriever sInstance;

    public int getColor() {
        return Palette.from(artWork).generate().getVibrantColor(Color.parseColor("#403f4d"));
    }


}
