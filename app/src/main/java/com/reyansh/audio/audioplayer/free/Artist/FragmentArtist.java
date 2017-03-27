package com.reyansh.audio.audioplayer.free.Artist;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.reyansh.audio.audioplayer.free.Common.CommonClass;
import com.reyansh.audio.audioplayer.free.Dialog.PlaylistDialog;
import com.reyansh.audio.audioplayer.free.Dialog.SortArtistDialog;
import com.reyansh.audio.audioplayer.free.MainActivity.Main;
import com.reyansh.audio.audioplayer.free.MusicService.MusicService;
import com.reyansh.audio.audioplayer.free.R;
import com.reyansh.audio.audioplayer.free.Utils.MusicUtils;
import com.reyansh.audio.audioplayer.free.Utils.PreferencesUtility;
import com.reyansh.audio.audioplayer.free.Views.FastScroller;

import java.util.ArrayList;
import java.util.HashMap;

public class FragmentArtist extends Fragment implements MusicUtils.names, MusicUtils.Defs {

    private ArrayList<HashMap<String, String>> arg1;
    private ArrayList<HashMap<String, String>> mArtistList;
    private Context mContext;
    private RecyclerView mRecyclerView;
    private AdapterArtists mAdapter;
    private FastScroller mFastScroller;
    private WindowManager.LayoutParams mLayoutParams;

    private int mSelectedPosition;
    private long mSelectedId;
    private View view;
    private CommonClass mApp;

    public FragmentArtist() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_album_layout, container, false);
        mContext = getContext();
        mApp=(CommonClass)mContext.getApplicationContext();
        mArtistList = new ArrayList<>();
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mFastScroller = (FastScroller) view.findViewById(R.id.fastscroller);
        mFastScroller.setRecyclerView(mRecyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new AdapterArtists(mContext, this, mArtistList);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnScrollListener(onScrollChangeListener);
        new AsyncFetchArtists(mContext, this).execute();
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    RecyclerView.OnScrollListener onScrollChangeListener = new RecyclerView.OnScrollListener() {
        private static final int HIDE_THRESHOLD = 20;
        private int scrolledDistance = 0;
        private boolean controlsVisible = true;

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if (scrolledDistance > HIDE_THRESHOLD && controlsVisible) {
                ((Main) getActivity()).hideTabLayout(1);
                controlsVisible = false;
                scrolledDistance = 0;
            } else if (scrolledDistance < -HIDE_THRESHOLD && !controlsVisible) {
                ((Main) getActivity()).hideTabLayout(0);
                controlsVisible = true;
                scrolledDistance = 0;
            }
            if ((controlsVisible && dy > 0) || (!controlsVisible && dy < 0)) {
                scrolledDistance += dy;
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        new AsyncFetchArtists(mContext, this).execute();
    }

    public void updateData(ArrayList<HashMap<String, String>> data) {
        mAdapter.updateData(data);
        mAdapter.notifyDataSetChanged();
        mArtistList = data;
    }

    public void setLayoutMangeer() {
        new AsyncFetchArtists(mContext, this).execute();
        if (PreferencesUtility.getInstance(mContext).getArtistsViewAs()) {
            mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        } else {
            mRecyclerView.setLayoutManager(new GridLayoutManager(mContext, 2));

        }
    }


    public void createContextMenu(ContextMenu menu, View view, int position) {
        mSelectedPosition = position;
        menu.add(4, PLAY, 0, R.string.play);
        menu.add(4, PLAY_NEXT, 0, R.string.play_next);
        menu.add(4, ADD_TO_QUEUE, 0, R.string.add_to_queue);
        SubMenu sub = menu.addSubMenu(0, ADD_TO_PLAYLIST, 0, R.string.add_to_playlist);
        MusicUtils.makePlaylistMenu(getContext(), sub, 4);
        menu.add(4, DELETE_ITEM, 0, R.string.delete_item);
        mSelectedId = Integer.parseInt(mArtistList.get(mSelectedPosition).get("artistID"));
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getGroupId() == 4) {
            switch (item.getItemId()) {
                case PLAY:
                    mApp.getService().setSongList(ArtistSongLoader.getSongsForArtist(mContext, mSelectedId));
                    mApp.getService().setSelectedSong(0, MusicService.NOTIFICATION_ID);
                    break;
                case PLAY_NEXT:
                    mApp.getService().playNext(ArtistSongLoader.getSongsForArtist(mContext, mSelectedId));
                    break;
                case ADD_TO_QUEUE:
                    mApp.getService().addtoqueue(ArtistSongLoader.getSongsForArtist(mContext, mSelectedId));
                    break;
                case NEW_PLAYLIST:
                    PlaylistDialog b = new PlaylistDialog(getActivity(),ArtistSongLoader.getSongsForArtist(mContext, mSelectedId));
                    b.show();
                    mLayoutParams = b.getWindow().getAttributes();
                    mLayoutParams.dimAmount = 0.5f;
                    b.getWindow().setAttributes(mLayoutParams);
                    b.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
                    break;
                case PLAYLIST_SELECTED:
                    MusicUtils.insertIntoPlayList(mContext, item,ArtistSongLoader.getSongsForArtist(mContext, mSelectedId));
                    return true;
                case DELETE_ITEM:
                    deleteArtist(getContext(), mSelectedId);
                    break;
            }
        }
        return super.onContextItemSelected(item);
    }

    public void deleteArtist(final Context context, final long _id) {

        final Dialog dialog = new Dialog(new android.support.v7.view.ContextThemeWrapper(getActivity(), R.style.myDialog));
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.unfavorites_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnim;
        ((TextView) dialog.findViewById(R.id.playlist)).setText("All songs associated with '" + mArtistList.get(mSelectedPosition).get(SONG_ARTIST) + "' artist will be deleted!!");
        (dialog.findViewById(R.id.create)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                        ContentResolver resolver = context.getContentResolver();
                        resolver.delete(uri, MediaStore.Audio.Media.ARTIST_ID + " = " + _id, null);
                        onResume();
                        return null;
                    }
                }.execute();
                Toast.makeText(mContext, mArtistList.get(mSelectedPosition).get(SONG_ARTIST) + " Deleted!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        ((Button) dialog.findViewById(R.id.cancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        ((Button) dialog.findViewById(R.id.create)).setTypeface(mApp.getStripTitleTypeFace());
        ((Button) dialog.findViewById(R.id.cancel)).setTypeface(mApp.getStripTitleTypeFace());
        mLayoutParams = dialog.getWindow().getAttributes();
        mLayoutParams.dimAmount = 0.5f;
        dialog.getWindow().setAttributes(mLayoutParams);
        dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
        dialog.show();
    }

    public void sortArtist() {
        SortArtistDialog sort = new SortArtistDialog(getContext());
        sort.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(final DialogInterface arg0) {
                onResume();
            }
        });
        sort.show();
        mLayoutParams = sort.getWindow().getAttributes();
        mLayoutParams.dimAmount = 0.5f;
        sort.getWindow().setAttributes(mLayoutParams);
        sort.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
    }

    public Drawable getDrawable() {
        return (view.findViewById(R.id.firstbg)).getBackground();
    }

    public void setViewColor(int color) {
        view.findViewById(R.id.firstbg).setBackgroundColor(color);
    }
}