package com.reyansh.audio.audioplayer.free.NowPlaying;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.reyansh.audio.audioplayer.free.Common.CommonClass;
import com.reyansh.audio.audioplayer.free.R;
import com.reyansh.audio.audioplayer.free.Utils.MetaRetriever;
import com.reyansh.audio.audioplayer.free.Utils.MusicUtils;
import com.reyansh.audio.audioplayer.free.Views.MusicVisualizer;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by REYANSH on 11/06/2016.
 */
public class Queue_Adapter extends RecyclerView.Adapter<Queue_Adapter.Item_holder> implements MusicUtils.names, ItemTouchHelperAdapter {
    ArrayList<HashMap<String, String>> data = new ArrayList<>();
    Context mContext;
    private OnStartDragListener mDragStartListener;
    CommonClass commonClass;
    String mSongName;

    public Queue_Adapter(Context context, ArrayList<HashMap<String, String>> data, OnStartDragListener dragLlistener, CommonClass cc) {
        this.data = data;
        mDragStartListener = dragLlistener;
        this.mContext = context;
        this.commonClass = cc;
    }

    @Override
    public Item_holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.queue_drawer_list_layout, parent, false);
        return new Item_holder(itemView);
    }

    @Override
    public void onBindViewHolder(final Item_holder holder, int position) {
        mSongName = data.get(position).get(SONG_NAME);
        if (MetaRetriever.getsInstance().getSongName() == mSongName) {
            holder.musicVisualizer.setVisibility(View.VISIBLE);
            holder.mainbg.setBackgroundColor(mContext.getResources().getColor(R.color.transparent_bg));
        } else {
            holder.musicVisualizer.setVisibility(View.INVISIBLE);
            holder.mainbg.setBackgroundColor(mContext.getResources().getColor(android.R.color.transparent));
        }

        holder.title.setText(data.get(position).get(SONG_NAME));
        holder.artist.setText(data.get(position).get(SONG_ARTIST));
        if (data.get(position).get(SONG_ALBUM_ID) != null)
            ImageLoader.getInstance().displayImage(MusicUtils.getAlbumArtUri(Long.parseLong(data.get(position).get(SONG_ALBUM_ID))).toString(),
                    holder.albumart, new DisplayImageOptions.Builder().cacheInMemory(true).showImageOnFail(R.drawable.default_art)
                            .showImageOnLoading(R.drawable.default_art)
                            .resetViewBeforeLoading(true)
                            .build());
        holder.dragger.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                    mDragStartListener.onStartDrag(holder);
                }
                return false;
            }
        });

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public void onItemMove(int from, int to) {
        HashMap<String, String> item = commonClass.getService().getSongList().get(from);
        commonClass.getService().getSongList().remove(from);
        commonClass.getService().getSongList().add(to, item);
        if (from == commonClass.getService().getCurrentSongIndex()) {
            commonClass.getService().setCurrentSongIndex(to);
        } else if (from > commonClass.getService().getCurrentSongIndex() && to <= commonClass.getService().getCurrentSongIndex()) {
            int i = commonClass.getService().getCurrentSongIndex();
            commonClass.getService().setCurrentSongIndex(i + 1);
        } else if (from < commonClass.getService().getCurrentSongIndex() && to >= commonClass.getService().getCurrentSongIndex()) {
            int i = commonClass.getService().getCurrentSongIndex();
            commonClass.getService().setCurrentSongIndex(i - 1);
        }
        notifyItemMoved(from, to);
    }

    @Override
    public void onItemDismiss(int position) {

    }

    class Item_holder extends RecyclerView.ViewHolder {
        public TextView title, artist;
        public ImageView dragger;
        public ImageView albumart;
        public MusicVisualizer musicVisualizer;
        public RelativeLayout mainbg;

        public Item_holder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.queue_song_title);
            artist = (TextView) itemView.findViewById(R.id.song_artist);
            dragger = (ImageView) itemView.findViewById(R.id.dragger);
            albumart = (ImageView) itemView.findViewById(R.id.image_view_album_art);
            musicVisualizer = (MusicVisualizer) itemView.findViewById(R.id.visualizer);
            mainbg = (RelativeLayout) itemView.findViewById(R.id.mainbg);

        }
    }
}
