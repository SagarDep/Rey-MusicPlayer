package com.reyansh.audio.audioplayer.free.AsyncTasks;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.reyansh.audio.audioplayer.free.R;
import com.reyansh.audio.audioplayer.free.Utils.MetaRetriever;
import com.reyansh.audio.audioplayer.free.Utils.MusicUtils;

/**
 * Created by REYANSH on 15/07/2016.
 */
public class AsyncTaskAddToFavorites extends AsyncTask<String, Void, Bitmap> {
    Context mContext;

    AsyncTaskAddToFavorites() {

    }

    @Override
    protected Bitmap doInBackground(String... params) {
        Bitmap artwork = null;

        final BitmapFactory.Options optionsa = new BitmapFactory.Options();
        optionsa.inJustDecodeBounds = true;
        optionsa.inJustDecodeBounds = false;
        optionsa.inPurgeable = true;
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .decodingOptions(optionsa)
                .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2).bitmapConfig(Bitmap.Config.ARGB_8888).build();

        artwork = ImageLoader.getInstance().loadImageSync(params[0], options);
        if (artwork == null)
            artwork = ImageLoader.getInstance().loadImageSync("drawable://" + R.drawable.album_art_6);
        MetaRetriever.getsInstance().setArtWork(artwork);
        return artwork;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        MetaRetriever.getsInstance().setArtWork(bitmap);
        super.onPostExecute(bitmap);
    }
}
