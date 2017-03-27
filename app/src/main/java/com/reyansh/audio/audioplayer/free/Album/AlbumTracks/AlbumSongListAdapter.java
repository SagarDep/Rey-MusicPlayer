package com.reyansh.audio.audioplayer.free.Album.AlbumTracks;

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


public class AlbumSongListAdapter extends BaseAdapter implements MusicUtils.names {

    private Context mContext;
    CommonClass mApp;
    private ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();

    public AlbumSongListAdapter(Context context, ArrayList<HashMap<String, String>> list) {
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
        RelativeLayout bg = (RelativeLayout) convertView.findViewById(R.id.song_layout);
        TextView mtxtSongName = (TextView) convertView.findViewById(R.id.song_name);
        TextView mTxtSongAlbumName = (TextView) convertView.findViewById(R.id.artist_name);
        TextView mTxtSongDuration = (TextView) convertView.findViewById(R.id.song_duration);
        mtxtSongName.setText(songsList.get(position).get(SONG_NAME));
        mTxtSongAlbumName.setText(songsList.get(position).get(SONG_ARTIST));
        try {
            mTxtSongDuration.setText(MusicUtils.makeShortTimeString(mContext, Long.parseLong(songsList.get(position).get(SONG_DURATION)) / 1000));
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
        return convertView;
    }

    public void setSongsList(ArrayList<HashMap<String, String>> list) {
        songsList = list;
        this.notifyDataSetChanged();
    }


}
