package com.reyansh.audio.audioplayer.free.Genres;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;

import com.reyansh.audio.audioplayer.free.Common.CommonClass;
import com.reyansh.audio.audioplayer.free.Dialog.PlaylistDialog;
import com.reyansh.audio.audioplayer.free.MainActivity.Main;
import com.reyansh.audio.audioplayer.free.MusicService.MusicService;
import com.reyansh.audio.audioplayer.free.R;
import com.reyansh.audio.audioplayer.free.Utils.MusicUtils;
import com.reyansh.audio.audioplayer.free.Views.FastScroller;

import java.util.ArrayList;
import java.util.HashMap;


public class FragmentGenres extends Fragment implements AdapterView.OnItemClickListener, MusicUtils.names, MusicUtils.Defs {
    ArrayList<HashMap<String, String>> data;
    private RecyclerView mRecyclerView;
    private FastScroller mFastScroller;
    private AdapterGenres mAdapter;
    private Context mContext;
    private int mSelectedPosition;
    private int mSelectedId;
    private CommonClass mApp;

    private WindowManager.LayoutParams mLayoutParams;

    public FragmentGenres() {
    }

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_album_layout, container, false);
        mContext = getContext();
        mApp = (CommonClass) mContext.getApplicationContext();
        mFastScroller = (FastScroller) view.findViewById(R.id.fastscroller);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        new AsyncFetchGenres(mContext, this).execute();
        data = new ArrayList<>();
        mAdapter = new AdapterGenres(mContext, this, data);
        mFastScroller.setRecyclerView(mRecyclerView);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnScrollListener(onScrollChangeListener);
        return view;
    }


    public void createContextMenu(ContextMenu menu, View view, int position) {
        menu.add(7, PLAY, 0, R.string.play);
        menu.add(7, PLAY_NEXT, 0, R.string.play_next);
        menu.add(7, ADD_TO_QUEUE, 0, R.string.add_to_queue);
        SubMenu sub = menu.addSubMenu(0, ADD_TO_PLAYLIST, 0, R.string.add_to_playlist);
        MusicUtils.makePlaylistMenu(getContext(), sub, 7);
        // menu.add(7, DELETE_ITEM, 0, R.string.delete_item);
        mSelectedPosition = position;
        mSelectedId = Integer.parseInt(data.get(mSelectedPosition).get(GENRES_ID));
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getGroupId() == 7) {
            switch (item.getItemId()) {
                case PLAY:
                    mApp.getService().setSongList(loadGenresTracks(String.valueOf(mSelectedId)));
                    mApp.getService().setSelectedSong(0, MusicService.NOTIFICATION_ID);
                    break;
                case PLAY_NEXT:
                    mApp.getService().playNext(loadGenresTracks(String.valueOf(mSelectedId)));
                    break;
                case ADD_TO_QUEUE:
                    mApp.getService().addtoqueue(loadGenresTracks(String.valueOf(mSelectedId)));
                    break;
                case NEW_PLAYLIST:
                    PlaylistDialog b = new PlaylistDialog(getActivity(), loadGenresTracks(String.valueOf(mSelectedId)));
                    b.show();
                    mLayoutParams = b.getWindow().getAttributes();
                    mLayoutParams.dimAmount = 0.5f;
                    b.getWindow().setAttributes(mLayoutParams);
                    b.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
                    break;
                case PLAYLIST_SELECTED:
                    MusicUtils.insertIntoPlayList(mContext, item, loadGenresTracks(String.valueOf(mSelectedId)));
                    break;
            }
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        new AsyncFetchGenres(mContext, this).execute();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
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

    public void updateData(ArrayList<HashMap<String, String>> data) {
        mAdapter.updateData(data);
        mAdapter.notifyDataSetChanged();
        this.data = data;
    }


    public Drawable getDrawable() {
        return (view.findViewById(R.id.firstbg)).getBackground();
    }

    public void setViewColor(int color) {
        view.findViewById(R.id.firstbg).setBackgroundColor(color);
    }

    public ArrayList<HashMap<String, String>> loadGenresTracks(String mGenresId) {
        ArrayList<HashMap<String, String>> data = new ArrayList<>();
        try {
            Uri uri = MediaStore.Audio.Genres.Members.getContentUri("external", Long.parseLong(mGenresId));
            String[] projection = new String[]{
                    MediaStore.Audio.Media._ID,
                    MediaStore.Audio.Media.TITLE,
                    MediaStore.Audio.Media.ARTIST,
                    MediaStore.Audio.Media.DATA,
                    MediaStore.Audio.Media.DURATION,
                    MediaStore.Audio.Media.ALBUM,
                    MediaStore.Audio.Media.ALBUM_ID
            };
            Cursor mCursor = mContext.getContentResolver().query(uri, projection, null, null, null);
            if (mCursor != null && mCursor.moveToFirst()) {
                do {
                    HashMap<String, String> album = new HashMap<String, String>();
                    album.put(SONG_ID, mCursor.getString(0));
                    album.put(SONG_NAME, mCursor.getString(1));
                    album.put(SONG_ARTIST, mCursor.getString(2));
                    album.put(SONG_PATH, mCursor.getString(3));
                    album.put(SONG_DURATION, mCursor.getString(4));
                    album.put(SONG_ALBUM, mCursor.getString(5));
                    album.put(SONG_ALBUM_ID, mCursor.getString(6));
                    data.add(album);
                } while (mCursor.moveToNext());
            }
            if (mCursor != null) {
                mCursor.close();
            }
        } catch (Exception e) {

        }
        return data;
    }

}
