package com.reyansh.audio.audioplayer.free.Artist.ArtistAlbum;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.graphics.ColorUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.reyansh.audio.audioplayer.free.Common.CommonClass;
import com.reyansh.audio.audioplayer.free.R;
import com.reyansh.audio.audioplayer.free.Utils.MusicUtils;

import java.util.ArrayList;
import java.util.HashMap;

public class ArtistAlbumListAdapter extends BaseAdapter implements MusicUtils.names {
    private Context mContext;
    private ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();
    CommonClass mApp;

    public ArtistAlbumListAdapter(Context context, ArrayList<HashMap<String, String>> list) {
        mContext = context;
        mApp = (CommonClass) mContext.getApplicationContext();
        this.songsList = list;
    }

    @Override
    public int getCount() {
        return songsList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.song_layout, null);
        }
        TextView mtxtSongName = (TextView) convertView.findViewById(R.id.song_name);
        TextView mtxtArtistName = (TextView) convertView.findViewById(R.id.artist_name);
        TextView mtxtDuration = (TextView) convertView.findViewById(R.id.song_duration);
        RelativeLayout bg = (RelativeLayout) convertView.findViewById(R.id.song_layout);
        mtxtSongName.setText(songsList.get(position).get(SONG_NAME));
        mtxtArtistName.setText(songsList.get(position).get(SONG_ARTIST));
        try {
            mtxtDuration.setText(MusicUtils.makeShortTimeString(mContext, (Integer.parseInt(songsList.get(position).get(SONG_DURATION)) / 1000)));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        if (mApp.getPreferencesUtility().getStripviewCheck()) {
            if (position % 2 == 0) {
                bg.setBackgroundColor(ColorUtils.setAlphaComponent(Color.parseColor("#ffffff"), 21));
            } else {
                bg.setBackgroundColor(ColorUtils.setAlphaComponent(Color.parseColor("#F19001"), 15));
            }
        }



      /*  if (songsList.get(position).get("coverPath") != null) {
            Picasso.with(mContext).load(new File(songsList.get(position).get("coverPath"))).into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    mTxtSongDuration.setImageBitmap(bitmap);
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {
                    Log.d("okok", "failed");
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            });
        } else {
            Picasso.with(mContext).load(R.drawable.album_art_5).into(mTxtSongDuration);
        }*/
        return convertView;
    }

    public void setSongsList(ArrayList<HashMap<String, String>> list) {
        songsList = list;
        this.notifyDataSetChanged();
    }
}
