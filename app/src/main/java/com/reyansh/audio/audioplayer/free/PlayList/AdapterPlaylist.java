package com.reyansh.audio.audioplayer.free.PlayList;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.reyansh.audio.audioplayer.free.PlayList.PlaylistTracks.PlaylistTracksActivity;
import com.reyansh.audio.audioplayer.free.R;
import com.reyansh.audio.audioplayer.free.Utils.BubbleTextGetter;
import com.reyansh.audio.audioplayer.free.Utils.MusicUtils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by REYANSH on 31/07/2016.
 */
public class AdapterPlaylist extends RecyclerView.Adapter<AdapterPlaylist.ItemHolder> implements BubbleTextGetter, MusicUtils.names {
    ArrayList<HashMap<String, String>> data;
    private Context mContext;
    private FragmentPlaylist mFragmentPlaylist;

    AdapterPlaylist(Context context, ArrayList<HashMap<String, String>> data, FragmentPlaylist fragmentPlaylist) {
        mContext = context;
        this.data = data;
        mFragmentPlaylist = fragmentPlaylist;

    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.artist_list_layout, parent, false);
        return new ItemHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ItemHolder holder, int position) {
        holder.mPlaylistName.setText(data.get(position).get(PLAYLIST_NAME));
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
        if (data.size() > 0)
            return String.valueOf(data.get(pos).get(PLAYLIST_NAME).charAt(0));
        else {
            return "";
        }
    }

    class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener {
        private TextView mPlaylistName;

        public ItemHolder(View itemView) {
            super(itemView);
            mPlaylistName = (TextView) itemView.findViewById(R.id.gridViewTitleText);
            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent in = new Intent(mContext, PlaylistTracksActivity.class);
            in.putExtra(PLAYLIST_ID, data.get(getAdapterPosition()).get(PLAYLIST_ID));
            in.putExtra(PLAYLIST_NAME, data.get(getAdapterPosition()).get(PLAYLIST_NAME));
            mContext.startActivity(in);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            mFragmentPlaylist.createContextMenu(menu, v, getAdapterPosition());
        }
    }
}
