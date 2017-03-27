package com.reyansh.audio.audioplayer.free.Search;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.reyansh.audio.audioplayer.free.Album.AlbumTracks.AlbumTracksActivity;
import com.reyansh.audio.audioplayer.free.Artist.ArtistAlbum.ArtistTracksActivity;
import com.reyansh.audio.audioplayer.free.Common.CommonClass;
import com.reyansh.audio.audioplayer.free.NowPlaying.NowPlaying;
import com.reyansh.audio.audioplayer.free.R;
import com.reyansh.audio.audioplayer.free.Utils.MusicUtils;
import com.reyansh.audio.audioplayer.free.Utils.PreferencesUtility;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by REYANSH on 26/06/2016.
 */
public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.Item_holder> implements MusicUtils.names {
    ArrayList<HashMap<String, String>> data = new ArrayList<>();
    private Context mContext;
    private CommonClass mApp;
    private SearchActivity mSeachActivity;

    public SearchAdapter(Context context, SearchActivity searchActivity) {
        this.mContext = context;
        this.mApp = (CommonClass) context.getApplicationContext();
        mSeachActivity = searchActivity;
    }

    public void updateSearchResults(ArrayList<HashMap<String, String>> data) {
        this.data = data;
    }

    @Override
    public Item_holder onCreateViewHolder(ViewGroup parent, int viewType) {

        switch (viewType) {
            case 0:
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_layout_demo, parent, false);
                return new Item_holder(itemView);
            case 1:
                View itemView2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_layout_demo, parent, false);
                return new Item_holder(itemView2);
            case 2:
                View itemView3 = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_header, parent, false);
                return new Item_holder(itemView3);
            case 3:
                View itemView4 = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_layout, parent, false);
                return new Item_holder(itemView4);
        }

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_layout_demo, parent, false);
        return new Item_holder(itemView);
    }

    @Override
    public void onBindViewHolder(final Item_holder holder, int position) {
        switch (getItemViewType(position)) {
            case 0:
                holder.title.setText(data.get(position).get(SONG_NAME));
                holder.artist.setText(data.get(position).get(SONG_ARTIST));
                if (PreferencesUtility.getInstance(mContext).getSongsViewAs()) {
                    ImageLoader.getInstance().displayImage(MusicUtils.getAlbumArtUri(Long.parseLong(data.get(position).get(SONG_ALBUM_ID))).toString(),
                            holder.albumart,
                            new DisplayImageOptions.Builder().
                                    cacheInMemory(true).showImageOnFail(R.drawable.default_art)
                                    .showImageOnLoading(R.drawable.default_art)
                                    .resetViewBeforeLoading(true)
                                    .displayer(new FadeInBitmapDisplayer(400))
                                    .build());
                } else {
                    holder.albumart.setVisibility(View.GONE);
                }
                try {
                    holder.songDuration.setText(MusicUtils.makeShortTimeString(mContext, (Integer.parseInt(data.get(position).get(SONG_DURATION)) / 1000)));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

                break;
            case 1:
                holder.title.setText(data.get(position).get(ALBUM));
                holder.artist.setText(data.get(position).get(ALBUM_ARTIST));
                ImageLoader.getInstance().displayImage(MusicUtils.getAlbumArtUri(Long.parseLong(data.get(position).get(ALBUM_ID))).toString(),
                        holder.albumart,
                        new DisplayImageOptions.Builder().
                                cacheInMemory(true).showImageOnFail(R.drawable.default_art)
                                .showImageOnLoading(R.drawable.default_art)
                                .resetViewBeforeLoading(true)
                                .displayer(new FadeInBitmapDisplayer(400))
                                .build());
                break;
            case 2:
                holder.header.setText(data.get(position).get("HEADERS TYPE"));
                break;
            case 3:
                holder.title.setText(data.get(position).get(ARTISTS));
                holder.artist.setText(data.get(position).get(ARTISTS_NO_OF_TRACKS) + " Tracks " +
                        "| " + data.get(position).get(ARTISTS_NO_OF_ALBUMS) + " Albums");

                break;

        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (data.get(position).get(TYPE).equalsIgnoreCase("SONGS"))
            return 0;
        if (data.get(position).get(TYPE).equalsIgnoreCase("ALBUMS"))
            return 1;
        if (data.get(position).get(TYPE).equalsIgnoreCase("HEADER"))
            return 2;
        if (data.get(position).get(TYPE).equalsIgnoreCase("ARTISTS"))
            return 3;
        return 5;
    }

    public void update(ArrayList<HashMap<String, String>> mSongsList) {
        data = mSongsList;
        notifyDataSetChanged();
    }


    class Item_holder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView title, artist, songDuration, header;
        public ImageView albumart;

        public Item_holder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.song_name);
            artist = (TextView) itemView.findViewById(R.id.artist_name);
            albumart = (ImageView) itemView.findViewById(R.id.songimage);
            songDuration = (TextView) itemView.findViewById(R.id.list_songduration);
            header = (TextView) itemView.findViewById(R.id.section_header);
            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                @Override
                public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                    if (data.get(getAdapterPosition()).get(TYPE).equalsIgnoreCase("SONGS")) {
                        mSeachActivity.createContextMenu(menu, v, getAdapterPosition(), data);
                    }
                }
            });
        }

        @Override
        public void onClick(View v) {
            if (data.get(getAdapterPosition()).get(TYPE).equalsIgnoreCase("SONGS")) {
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ArrayList<HashMap<String, String>> dfa = new ArrayList<HashMap<String, String>>();
                        dfa.add(data.get(getAdapterPosition()));
                        mApp.getService().setSongList(dfa);
                        mApp.getService().setSelectedSong(0, 11);
                    }
                }, 100);
            } else if (data.get(getAdapterPosition()).get(TYPE).equalsIgnoreCase("ALBUMS")) {
                Bundle bundle = new Bundle();
                bundle.putString("AlbumNameForTracks", data.get(getAdapterPosition()).get(ALBUM));
                bundle.putString("ArtistNameForAlbum", data.get(getAdapterPosition()).get(ALBUM_ARTIST));
                bundle.putString("AlbumCoverPath", data.get(getAdapterPosition()).get(ALBUM_COVER_PATH));
                bundle.putString("NumberOfSongs", data.get(getAdapterPosition()).get(ALBUM_NUMBER_OF_SONGS));
                bundle.putString("year", data.get(getAdapterPosition()).get(ALBUM_MIN_YEAR));
                Intent in = new Intent(mContext, AlbumTracksActivity.class);
                in.putExtras(bundle);
                mContext.startActivity(in);
            } else if (data.get(getAdapterPosition()).get(TYPE).equalsIgnoreCase("HEADER")) {

            } else if (data.get(getAdapterPosition()).get(TYPE).equalsIgnoreCase("ARTISTS")) {
                Bundle bundle = new Bundle();
                bundle.putString("ArtistName", data.get(getAdapterPosition()).get(ARTISTS));
                bundle.putString("numberOfTracks", data.get(getAdapterPosition()).get(ARTISTS_NO_OF_TRACKS));
                bundle.putString("numberOfAlbums", data.get(getAdapterPosition()).get(ARTISTS_NO_OF_ALBUMS));
                bundle.putString("artistID", data.get(getAdapterPosition()).get(ARTISTS_ID));
                Intent in = new Intent(mContext, ArtistTracksActivity.class);
                in.putExtras(bundle);
                mContext.startActivity(in);
            }
        }
    }
}
