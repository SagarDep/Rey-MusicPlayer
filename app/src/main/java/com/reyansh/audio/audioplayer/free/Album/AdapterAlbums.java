package com.reyansh.audio.audioplayer.free.Album;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.reyansh.audio.audioplayer.free.Album.AlbumTracks.AlbumParallexActivity;
import com.reyansh.audio.audioplayer.free.Album.AlbumTracks.AlbumTracksActivity;
import com.reyansh.audio.audioplayer.free.R;
import com.reyansh.audio.audioplayer.free.Utils.BubbleTextGetter;
import com.reyansh.audio.audioplayer.free.Utils.MusicUtils;
import com.reyansh.audio.audioplayer.free.Utils.PreferencesUtility;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by REYANSH on 31/07/2016.
 */
public class AdapterAlbums extends RecyclerView.Adapter<AdapterAlbums.ItemHolder> implements MusicUtils.names, BubbleTextGetter {

    private Context mContext;
    private ArrayList<HashMap<String, String>> data;
    private FragmentAlbum mFragmentAlbum;

    public AdapterAlbums(Context context, ArrayList<HashMap<String, String>> data, FragmentAlbum fragmentAlbum) {
        mContext = context;
        this.data = data;
        mFragmentAlbum = fragmentAlbum;
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.albumlist, parent, false);
        return new ItemHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ItemHolder holder, int position) {
        holder.albumName.setText(data.get(position).get(SONG_ALBUM));
        holder.artistName.setText(data.get(position).get(SONG_ARTIST));

        ImageLoader.getInstance().displayImage(
                MusicUtils.getAlbumArtUri(Long.parseLong(data.get(position).get(SONG_ALBUM_ID))).toString(), holder.albumart,
                new DisplayImageOptions.Builder().cacheInMemory(true).showImageOnLoading(R.drawable.default_art)
                        .showImageOnFail(R.drawable.default_art)
                        .resetViewBeforeLoading(true)
                        .build(), new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        new Palette.Builder(loadedImage).generate(new Palette.PaletteAsyncListener() {
                            @Override
                            public void onGenerated(Palette palette) {
                                if (PreferencesUtility.getInstance(mContext).getAlbumViewAs()) {
                                    Palette.Swatch swatch = palette.getVibrantSwatch();
                                    if (swatch != null) {
                                        int color = swatch.getRgb();
                                        holder.mFooter.setBackgroundColor(color);
                                    } else {
                                        Palette.Swatch mutedSwatch = palette.getMutedSwatch();
                                        if (mutedSwatch != null) {
                                            int color = mutedSwatch.getRgb();
                                            holder.mFooter.setBackgroundColor(color);
                                        }
                                    }
                                } else {
                                    holder.mFooter.setBackgroundColor(Color.parseColor("#55000000"));
                                    holder.mFooter.getBackground().setAlpha(100);
                                }
                            }
                        });
                    }
                });

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void updateData(ArrayList<HashMap<String, String>> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @Override
    public String getTextToShowInBubble(int pos) {
        return String.valueOf(data.get(pos).get(SONG_ALBUM).charAt(0));
    }

    class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView albumName;
        private TextView artistName;
        private ImageView albumart;
        private LinearLayout mFooter;

        public ItemHolder(View itemView) {
            super(itemView);
            albumName = (TextView) itemView.findViewById(R.id.gridViewTitleText);
            artistName = (TextView) itemView.findViewById(R.id.gridViewSubText);
            albumart = (ImageView) itemView.findViewById(R.id.gridViewImage);
            mFooter = (LinearLayout) itemView.findViewById(R.id.linear_layout_footer);
            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                @Override
                public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                    mFragmentAlbum.createContextMenu(menu, v, getAdapterPosition());
                }
            });
        }

        @Override
        public void onClick(View v) {
            Bundle bundle = new Bundle();
            bundle.putString("AlbumNameForTracks", data.get(getAdapterPosition()).get(SONG_ALBUM));
            bundle.putString("ArtistNameForAlbum", data.get(getAdapterPosition()).get(SONG_ARTIST));
            bundle.putString("AlbumCoverPath", data.get(getAdapterPosition()).get(ALBUM_COVER_PATH));
            bundle.putString("NumberOfSongs", data.get(getAdapterPosition()).get(ALBUM_NUMBER_OF_SONGS));
            bundle.putString("year", data.get(getAdapterPosition()).get(ALBUM_YEAR));
            Intent in = new Intent(mContext, AlbumTracksActivity.class);
            in.putExtras(bundle);
            mContext.startActivity(in);
        }
    }
}
