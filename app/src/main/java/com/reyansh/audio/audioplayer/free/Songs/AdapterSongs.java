package com.reyansh.audio.audioplayer.free.Songs;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.reyansh.audio.audioplayer.free.Common.CommonClass;
import com.reyansh.audio.audioplayer.free.MusicService.MusicService;
import com.reyansh.audio.audioplayer.free.NowPlaying.NowPlaying;
import com.reyansh.audio.audioplayer.free.R;
import com.reyansh.audio.audioplayer.free.Utils.BubbleTextGetter;
import com.reyansh.audio.audioplayer.free.Utils.MusicUtils;
import com.reyansh.audio.audioplayer.free.Utils.PreferencesUtility;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by REYANSH on 30/07/2016.
 */
public class AdapterSongs extends RecyclerView.Adapter<AdapterSongs.Item_holder> implements MusicUtils.names, BubbleTextGetter {

    private Context mContext;
    private CommonClass mApp;
    private ArrayList<HashMap<String, String>> data;
    private FragmentSongs mFragmentSongs;

    public AdapterSongs(Context context, ArrayList<HashMap<String, String>> data, FragmentSongs fragmentSongs) {
        mContext = context;
        mApp = (CommonClass) mContext.getApplicationContext();
        this.data = data;
        mFragmentSongs = fragmentSongs;
    }

    @Override
    public Item_holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_layout_demo, parent, false);
        return new Item_holder(itemView);
    }

    @Override
    public void onBindViewHolder(Item_holder holder, int position) {
        holder.title.setText(data.get(position).get(SONG_NAME));
        holder.artist.setText(data.get(position).get(SONG_ARTIST));
        try {
            holder.duration.setText(MusicUtils.makeShortTimeString(mContext,
                    Integer.parseInt(data.get(position).get(SONG_DURATION)) / 1000));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        if (PreferencesUtility.getInstance(mContext).getSongsViewAs()) {
            ImageLoader.getInstance().displayImage(MusicUtils.getAlbumArtUri(Long.parseLong(data.get(position).get(SONG_ALBUM_ID))).toString(),
                    holder.albumart,
                    new DisplayImageOptions.Builder().cacheInMemory(true)
                            .showImageOnFail(R.drawable.default_art)
                            .showImageOnLoading(R.drawable.default_art)
                            .resetViewBeforeLoading(true)
                            .build());
            holder.albumart.setVisibility(View.VISIBLE);
        } else {
            holder.albumart.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void update(ArrayList<HashMap<String, String>> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @Override
    public String getTextToShowInBubble(int pos) {
        return String.valueOf(data.get(pos).get(SONG_NAME).charAt(0));
    }

    class Item_holder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView title;
        private TextView artist;
        private ImageView albumart;
        private TextView duration;

        public Item_holder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.song_name);
            artist = (TextView) itemView.findViewById(R.id.artist_name);
            albumart = (ImageView) itemView.findViewById(R.id.songimage);
            duration = (TextView) itemView.findViewById(R.id.list_songduration);
            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                @Override
                public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                    mFragmentSongs.createContextMenu(menu, v, getAdapterPosition());
                }
            });
        }

        @Override
        public void onClick(View v) {
            mApp.getService().setSongList(data);
            mApp.getService().setSelectedSong(getAdapterPosition(), MusicService.NOTIFICATION_ID);
            mContext.startActivity(new Intent(mContext, NowPlaying.class));
        }
    }
}
