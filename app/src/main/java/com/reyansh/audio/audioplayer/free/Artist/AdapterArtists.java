package com.reyansh.audio.audioplayer.free.Artist;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.codetroopers.betterpickers.Utils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.reyansh.audio.audioplayer.free.Artist.ArtistAlbum.ArtistTracksActivity;
import com.reyansh.audio.audioplayer.free.Lastfmapi.LastFmClient;
import com.reyansh.audio.audioplayer.free.Lastfmapi.callbacks.ArtistInfoListener;
import com.reyansh.audio.audioplayer.free.Lastfmapi.models.ArtistQuery;
import com.reyansh.audio.audioplayer.free.Lastfmapi.models.LastfmArtist;
import com.reyansh.audio.audioplayer.free.R;
import com.reyansh.audio.audioplayer.free.Utils.BubbleTextGetter;
import com.reyansh.audio.audioplayer.free.Utils.MusicUtils;
import com.reyansh.audio.audioplayer.free.Utils.PreferencesUtility;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by REYANSH on 31/07/2016.
 */
public class AdapterArtists extends RecyclerView.Adapter<AdapterArtists.ItemHolder> implements BubbleTextGetter, MusicUtils.names {
    private Context mContext;
    private ArrayList<HashMap<String, String>> data;
    private FragmentArtist mFragmentArtist;

    public AdapterArtists(Context context, FragmentArtist mFragmentArtist, ArrayList<HashMap<String, String>> data) {
        mContext = context;
        this.data = data;
        this.mFragmentArtist = mFragmentArtist;
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        if (viewType == 0) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_layout, parent, false);
        return new ItemHolder(itemView);
//        } else {
//            View itemView1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.artist_list_layout, parent, false);
//            return new ItemHolder(itemView1);
//        }
    }

    @Override
    public void onBindViewHolder(final ItemHolder holder, int position) {
        try {
//            String nooftracks = MusicUtils.makeLabel(mContext, R.plurals.Nsongs, Integer.parseInt(data.get(position).get("numberOfTracks")));
  //          String noofalbums = MusicUtils.makeLabel(mContext, R.plurals.Nalbums, Integer.parseInt(data.get(position).get("numberOfAlbums")));
    //        holder.mDetails.setText(nooftracks + " | " + noofalbums);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        holder.mArtistName.setText(data.get(position).get(SONG_ARTIST));
//        if (PreferencesUtility.getInstance(mContext).getArtistsViewAs())
//            LastFmClient.getInstance(mContext).getArtistInfo(
//                    new ArtistQuery(data.get(position).get(SONG_ARTIST)),
//                    new ArtistInfoListener() {
//                        @Override
//                        public void artistInfoSucess(LastfmArtist artist) {
//                            if (artist != null && artist.mArtwork != null) {
//                                ImageLoader.getInstance().displayImage(artist.mArtwork.get(2).mUrl, holder.mArtWork,
//                                        new DisplayImageOptions.Builder().cacheInMemory(true)
//                                                .cacheOnDisk(true)
//                                                .showImageOnLoading(R.drawable.default_art)
//                                                .showImageOnFail(R.drawable.default_art)
//                                                .resetViewBeforeLoading(true)
//                                                .displayer(new FadeInBitmapDisplayer(400))
//                                                .build(), new SimpleImageLoadingListener() {
//                                            @Override
//                                            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//                                                if (loadedImage != null) {
//                                                    new Palette.Builder(loadedImage).generate(new Palette.PaletteAsyncListener() {
//                                                        @Override
//                                                        public void onGenerated(Palette palette) {
//                                                            int color = palette.getVibrantColor(Color.parseColor("#66000000"));
//                                                            Palette.Swatch swatch = palette.getVibrantSwatch();
//                                                            int textColor;
//                                                            if (swatch != null) {
//                                                            } else
//                                                                textColor = Color.parseColor("#ffffff");
//                                                        }
//                                                    });
//                                                }
//                                            }
//
//                                            @Override
//                                            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
//                                            }
//                                        });
//                            }
//                        }
//
//                        @Override
//                        public void artistInfoFailed() {
//
//                        }
//                    });

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public String getTextToShowInBubble(int pos) {
        return String.valueOf(data.get(pos).get(SONG_ARTIST).charAt(0));
    }
//
//    @Override
//    public int getItemViewType(int position) {
//        int i = 0;
//        if (PreferencesUtility.getInstance(mContext).getArtistsViewAs()) {
//            i = 0;
//        } else {
//            i = 1;
//        }
//        switch (i) {
//            case 0:
//                return 0;
//            case 1:
//                return 1;
//            default:
//                return 2;
//        }
//    }

    public void updateData(ArrayList<HashMap<String, String>> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mArtistName;
        private TextView mAlbumName;
        private ImageView mArtWork;
        private LinearLayout mFooter;
        private TextView mDetails;

        public ItemHolder(View itemView) {
            super(itemView);
            mArtistName = (TextView) itemView.findViewById(R.id.song_name);
            mDetails = (TextView) itemView.findViewById(R.id.artist_name);
            mArtWork = (ImageView) itemView.findViewById(R.id.gridViewImage);
            mFooter = (LinearLayout) itemView.findViewById(R.id.linear_layout_footer);
            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                @Override
                public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                    mFragmentArtist.createContextMenu(menu, v, getAdapterPosition());
                }
            });
        }

        @Override
        public void onClick(View v) {
            Bundle bundle = new Bundle();
            bundle.putString("ArtistName", data.get(getAdapterPosition()).get("songArtist"));
            bundle.putString("numberOfTracks", data.get(getAdapterPosition()).get("numberOfTracks"));
            bundle.putString("numberOfAlbums", data.get(getAdapterPosition()).get("numberOfAlbums"));
            bundle.putString("artistID", data.get(getAdapterPosition()).get("artistID"));
            Intent in = new Intent(mContext, ArtistTracksActivity.class);
            in.putExtras(bundle);
            mContext.startActivity(in);
        }
    }
}
