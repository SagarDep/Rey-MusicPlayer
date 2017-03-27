package com.reyansh.audio.audioplayer.free.PlayList;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.graphics.ColorUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.reyansh.audio.audioplayer.free.R;
import com.reyansh.audio.audioplayer.free.Utils.PreferencesUtility;

import java.util.ArrayList;
import java.util.HashMap;



public class PlaylistAdapter extends ArrayAdapter<HashMap<String, String>> {
    Context context;

    public PlaylistAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        this.context = context;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        AlbumViewHolder viewHolder;
        HashMap<String, String> a;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.artist_list_layout, null);
            viewHolder = new AlbumViewHolder();
            viewHolder.artist = (TextView) convertView.findViewById(R.id.gridViewTitleText);
            viewHolder.song_layout=(LinearLayout)convertView.findViewById(R.id.okok);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (AlbumViewHolder) convertView.getTag();
        }
        a = getItem(position);
        viewHolder.artist.setText(a.get("playListName"));
        if (PreferencesUtility.getInstance(getContext()).getStripviewCheck()) {
            if (position % 2 == 0) {
                viewHolder.song_layout.setBackgroundColor(ColorUtils.setAlphaComponent(Color.parseColor("#ffffff"), 21));
            } else {
                viewHolder.song_layout.setBackgroundColor(ColorUtils.setAlphaComponent(Color.parseColor("#F19001"), 15));
            }
        }
        return convertView;
    }



    static class AlbumViewHolder {
        TextView artist;
        LinearLayout song_layout;
      /*  TextView numberofalbums;
        TextView songDuration;*/
    }

    public void setData(ArrayList<HashMap<String, String>> arg1) {
        clear();
        if (arg1 != null) {
            for (int i = 0; i < arg1.size(); i++) {
                add(arg1.get(i));
            }
        }
    }
}
