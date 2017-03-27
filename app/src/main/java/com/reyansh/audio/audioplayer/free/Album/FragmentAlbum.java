package com.reyansh.audio.audioplayer.free.Album;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
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
import com.reyansh.audio.audioplayer.free.Dialog.SortAlbumDialog;
import com.reyansh.audio.audioplayer.free.MainActivity.Main;
import com.reyansh.audio.audioplayer.free.MusicService.MusicService;
import com.reyansh.audio.audioplayer.free.R;
import com.reyansh.audio.audioplayer.free.Utils.MusicUtils;
import com.reyansh.audio.audioplayer.free.Utils.PreferencesUtility;
import com.reyansh.audio.audioplayer.free.Views.FastScroller;

import java.util.ArrayList;
import java.util.HashMap;


public class FragmentAlbum extends Fragment implements MusicUtils.names, MusicUtils.Defs {
    private Context mContext;
    private RecyclerView mRecyclerView;
    private ArrayList<HashMap<String, String>> mAlbumList;
    private ArrayList<HashMap<String, String>> mSelectedSongs;
    private AdapterAlbums mAdapter;
    private FastScroller mFastScroller;
    private WindowManager.LayoutParams mLayoutParams;
    private CommonClass mApp;
    private int mSelectedPosition;
    private long mSelectedId;
    private View view;

    public FragmentAlbum() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_album_layout, container, false);
        mContext = getContext();
        mAlbumList = new ArrayList<HashMap<String, String>>();
        mAdapter = new AdapterAlbums(mContext, mAlbumList, this);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mFastScroller = (FastScroller) view.findViewById(R.id.fastscroller);
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        mFastScroller.setRecyclerView(mRecyclerView);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnScrollListener(onScrollChangeListener);
        mApp = (CommonClass) mContext.getApplicationContext();
        new AsyncFetchAlbums(mContext, this).execute();
        return view;
    }


    public void updateData(ArrayList<HashMap<String, String>> data) {
        mAlbumList = data;
        mAdapter.updateData(data);
        mAdapter.notifyDataSetChanged();
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
        new AsyncFetchAlbums(mContext, this).execute();
    }

    public void createContextMenu(ContextMenu menu, View view, int position) {
        menu.add(3, PLAY, 0, R.string.play);
        menu.add(3, PLAY_NEXT, 0, R.string.play_next);
        menu.add(3, ADD_TO_QUEUE, 0, R.string.add_to_queue);
        SubMenu sub = menu.addSubMenu(0, ADD_TO_PLAYLIST, 0, R.string.add_to_playlist);
        MusicUtils.makePlaylistMenu(getContext(), sub, 3);
        menu.add(3, DELETE_ITEM, 0, R.string.delete_item);
        mSelectedPosition = position;
        mSelectedId = Integer.parseInt(mAlbumList.get(mSelectedPosition).get(SONG_ALBUM_ID));
    }

    @Override
    public boolean onContextItemSelected(final MenuItem item) {
        if (item.getGroupId() == 3) {
            switch (item.getItemId()) {
                case PLAY:
                    mApp.getService().setSongList(getSongsForAlbum(mContext, mSelectedId));
                    mApp.getService().setSelectedSong(0, MusicService.NOTIFICATION_ID);
                    return true;
                case PLAY_NEXT:
                    mApp.getService().playNext(getSongsForAlbum(mContext, mSelectedId));
                    return true;
                case ADD_TO_QUEUE:
                    mApp.getService().addtoqueue(getSongsForAlbum(mContext, mSelectedId));
                    return true;
                case NEW_PLAYLIST:
                    PlaylistDialog b = new PlaylistDialog(getActivity(), getSongsForAlbum(mContext, mSelectedId));
                    b.show();
                    mLayoutParams = b.getWindow().getAttributes();
                    mLayoutParams.dimAmount = 0.5f;
                    ((Button) b.findViewById(R.id.create)).setTypeface(mApp.getStripTitleTypeFace());
                    ((Button) b.findViewById(R.id.cancel)).setTypeface(mApp.getStripTitleTypeFace());
                    b.getWindow().setAttributes(mLayoutParams);
                    b.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
                    return true;
                case PLAYLIST_SELECTED:
                    MusicUtils.insertIntoPlayList(mContext, item, getSongsForAlbum(mContext, mSelectedId));
                    return true;
                case DELETE_ITEM:
                    deleteAlbum(mContext, mSelectedId);
                    return true;
                default:
                    break;
            }
        }
        return super.onContextItemSelected(item);
    }

    public void deleteAlbum(final Context context, final long _id) {

        final Dialog dialog = new Dialog(new android.support.v7.view.ContextThemeWrapper(getActivity(), R.style.myDialog));
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.unfavorites_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnim;
        ((TextView) dialog.findViewById(R.id.playlist)).setText("All songs associated with '" + mAlbumList.get(mSelectedPosition).get(SONG_ALBUM) + "' album will be deleted!!");
        (dialog.findViewById(R.id.create)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                        ContentResolver resolver = context.getContentResolver();
                        resolver.delete(uri, MediaStore.Audio.Media.ALBUM_ID + " = " + _id, null);
                        onResume();
                        return null;
                    }
                }.execute();
                Toast.makeText(mContext, mAlbumList.get(mSelectedPosition).get(SONG_ALBUM) + " Deleted!", Toast.LENGTH_SHORT).show();
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

    public void sortAlbum() {
        SortAlbumDialog sort = new SortAlbumDialog(getContext());
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


    public static ArrayList<HashMap<String, String>> getSongsForAlbum(Context context, long albumID) {
        ArrayList<HashMap<String, String>> songs = new ArrayList<>();
        Cursor cursor = makeAlbumSongCursor(context, albumID);
        if ((cursor != null) && (cursor.moveToFirst()))
            do {
                HashMap<String, String> song = new HashMap<String, String>();
                song.put(SONG_ID, cursor.getString(0));
                song.put(SONG_NAME, cursor.getString(1));
                song.put(SONG_ARTIST, cursor.getString(2));
                song.put(SONG_DURATION, cursor.getString(3));
                song.put(SONG_PATH, cursor.getString(4));
                song.put(SONG_ALBUM, cursor.getString(5));
                song.put(SONG_ALBUM_ID, cursor.getString(6));
                songs.add(song);
            }
            while (cursor.moveToNext());
        if (cursor != null)
            cursor.close();
        return songs;
    }

    public static Cursor makeAlbumSongCursor(Context context, long albumID) {
        ContentResolver contentResolver = context.getContentResolver();
        final String albumSongSortOrder = PreferencesUtility.getInstance(context).getAlbumSongSortOrder();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String string = "is_music=1 AND title != '' AND album_id=" + albumID;
        Cursor cursor = contentResolver.query(uri, new String[]{
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.ALBUM_ID}, string, null, albumSongSortOrder);
        return cursor;
    }

    public Drawable getDrawable() {
        return (view.findViewById(R.id.firstbg)).getBackground();
    }

    public void setViewColor(int color) {
        view.findViewById(R.id.firstbg).setBackgroundColor(color);
    }
}