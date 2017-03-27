package com.reyansh.audio.audioplayer.free.FileDirectory;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by REYANSH on 24/08/2016.
 */
public class FileAdapter extends RecyclerView.Adapter<FileAdapter.ItemHolder> {
    private ArrayList<HashMap<String, String>> mData;
    private FragmentFolder mFragmentFolder;

    public FileAdapter(FragmentFolder mFragmentFolder, ArrayList<HashMap<String, String>> mData) {
        this.mData = mData;
        this.mFragmentFolder = mFragmentFolder;
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(ItemHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ItemHolder extends RecyclerView.ViewHolder {
        public ItemHolder(View itemView) {
            super(itemView);
        }
    }
}
