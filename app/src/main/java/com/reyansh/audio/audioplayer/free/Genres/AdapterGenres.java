package com.reyansh.audio.audioplayer.free.Genres;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.reyansh.audio.audioplayer.free.Artist.FragmentArtist;
import com.reyansh.audio.audioplayer.free.Genres.GenresAlbum.GenresTracksActivity;
import com.reyansh.audio.audioplayer.free.R;
import com.reyansh.audio.audioplayer.free.Utils.BubbleTextGetter;
import com.reyansh.audio.audioplayer.free.Utils.MusicUtils;

import java.util.ArrayList;
import java.util.HashMap;


public class AdapterGenres extends RecyclerView.Adapter<AdapterGenres.ItemHolder> implements BubbleTextGetter, MusicUtils.names {
    private Context mContext;
    private ArrayList<HashMap<String, String>> data;
    private FragmentGenres mFragmentGenres;

    public AdapterGenres(Context context, FragmentGenres mFragmentGenres, ArrayList<HashMap<String, String>> data) {
        mContext = context;
        this.data = data;
        this.mFragmentGenres = mFragmentGenres;
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.artist_list_layout, parent, false);
        return new ItemHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ItemHolder holder, int position) {
        holder.artistName.setText(data.get(position).get(GENRES_NAME));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public String getTextToShowInBubble(int pos) {
        if (data.get(pos).get(GENRES_NAME).length() != 0) {
            return String.valueOf(data.get(pos).get(GENRES_NAME).charAt(0));
        } else {
            return String.valueOf("-");
        }
    }


    public void updateData(ArrayList<HashMap<String, String>> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView artistName;

        public ItemHolder(View itemView) {
            super(itemView);
            artistName = (TextView) itemView.findViewById(R.id.gridViewTitleText);
            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                @Override
                public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                    mFragmentGenres.createContextMenu(menu, v, getAdapterPosition());
                }
            });
        }

        @Override
        public void onClick(View v) {
            Intent in = new Intent(mContext, GenresTracksActivity.class);
            in.putExtra(GENRES_ID, data.get(getAdapterPosition()).get(GENRES_ID));
            in.putExtra(GENRES_NAME, data.get(getAdapterPosition()).get(GENRES_NAME));
            mContext.startActivity(in);
        }
    }
}