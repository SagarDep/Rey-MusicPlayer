package com.reyansh.audio.audioplayer.free.Songs;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.view.ContextThemeWrapper;
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

import com.reyansh.audio.audioplayer.free.AsyncTasks.AsyncDeleteTask;
import com.reyansh.audio.audioplayer.free.Common.CommonClass;
import com.reyansh.audio.audioplayer.free.Dialog.PlaylistDialog;
import com.reyansh.audio.audioplayer.free.Dialog.SortDialog;
import com.reyansh.audio.audioplayer.free.FileDirectory.FragmentFolder;
import com.reyansh.audio.audioplayer.free.MainActivity.Main;
import com.reyansh.audio.audioplayer.free.MusicService.MusicService;
import com.reyansh.audio.audioplayer.free.NowPlaying.NowPlaying;
import com.reyansh.audio.audioplayer.free.R;
import com.reyansh.audio.audioplayer.free.Utils.MusicUtils;
import com.reyansh.audio.audioplayer.free.Views.FastScroller;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by REYANSH on 30/07/2016.
 */
public class FragmentSongs extends Fragment implements MusicUtils.Defs, MusicUtils.names {

    private ArrayList<HashMap<String, String>> mSongsList;
    private RecyclerView mRecyclerView;
    private AdapterSongs mAdapter;
    private Context mContext;
    private CommonClass mApp;
    private FastScroller mFastScroller;
    private WindowManager.LayoutParams mLayoutParams;
    private int mSelectedPosition;
    private int mSongId;
    View view;

    public FragmentSongs() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_album_layout, container, false);
        mContext = getContext();
        mApp = (CommonClass) mContext.getApplicationContext();
        mSongsList = new ArrayList<>();
        mFastScroller = (FastScroller) view.findViewById(R.id.fastscroller);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.addOnScrollListener(onScrollChangeListener);
        mFastScroller.setRecyclerView(mRecyclerView);
        mAdapter = new AdapterSongs(mContext, mSongsList, this);
        mRecyclerView.setAdapter(mAdapter);
        new AsyncFetchSongs(mContext, this).execute();
        return view;
    }

    public void updateSong(ArrayList<HashMap<String, String>> data) {
        this.mSongsList = data;
        mAdapter.update(data);
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


    public void shufflesongs() {
        long seed = System.nanoTime();
        Collections.shuffle(mSongsList, new Random(seed));
        Collections.shuffle(mSongsList, new Random(seed));
        mApp.getService().setSongList(mSongsList);
        mApp.getService().setSelectedSong(0, MusicService.NOTIFICATION_ID);
        startActivity(new Intent(getActivity(), NowPlaying.class));
    }


    public void sortSong() {
        SortDialog sort = new SortDialog(getContext());
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


    @Override
    public void onResume() {
        super.onResume();
        new AsyncFetchSongs(mContext, this).execute();
        mAdapter.notifyDataSetChanged();
    }

    public void createContextMenu(ContextMenu menu, View view, int position) {
        menu.add(0, PLAY_NEXT, 0, R.string.play_next);
        menu.add(0, ADD_TO_QUEUE, 0, R.string.add_to_queue);
        SubMenu sub = menu.addSubMenu(0, ADD_TO_PLAYLIST, 0, R.string.add_to_playlist);
        MusicUtils.makePlaylistMenu(getContext(), sub, 0);
        menu.add(0, ADD_TO_FAVORITES, 0, R.string.add_to_favorites);
        menu.add(0, USE_AS_RINGTONE, 0, R.string.ringtone_menu);
        menu.add(0, DELETE_ITEM, 0, R.string.delete_item);
        menu.add(0, SHARE_ITEM, 0, R.string.share_item);
        mSelectedPosition = position;
        mSongId = Integer.parseInt(mSongsList.get(mSelectedPosition).get(SONG_ID));
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getGroupId() == 0) {
            switch (item.getItemId()) {
                case NEW_PLAYLIST:
                    PlaylistDialog b = new PlaylistDialog(getActivity(), new long[]{mSongId});
                    b.show();
                    mLayoutParams = b.getWindow().getAttributes();
                    mLayoutParams.dimAmount = 0.5f;
                    b.getWindow().setAttributes(mLayoutParams);
                    b.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
                    ((Button) b.findViewById(R.id.create)).setTypeface(mApp.getStripTitleTypeFace());
                    ((Button) b.findViewById(R.id.cancel)).setTypeface(mApp.getStripTitleTypeFace());
                    return true;
                case PLAYLIST_SELECTED:
                    long[] list = new long[]{mSongId};
                    long playlist = item.getIntent().getLongExtra("playlist", 0);
                    MusicUtils.addToPlaylist(getContext(), list, playlist);
                    return true;
                case USE_AS_RINGTONE:
                    MusicUtils.setRingtone(getActivity(), mSongId);
                    return true;
                case DELETE_ITEM: {
                    final File file = new File(mSongsList.get(mSelectedPosition).get(SONG_PATH));
                    final Dialog dialog = new Dialog(new ContextThemeWrapper(getActivity(), R.style.myDialog));
                    dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.unfavorites_dialog);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                    dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnim;
                    ((TextView) dialog.findViewById(R.id.playlist)).setText(file.getName() + " Song will be deleted!!");
                    ((Button) dialog.findViewById(R.id.create)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mApp.getPreferencesUtility().getSharedPreferenceUri() == null && !MusicUtils.isKitkat()) {
                                askForUriPermission();
                            } else {
                                new AsyncDeleteTask(mContext, file).execute();
                                mSongsList.remove(mSelectedPosition);
                                mAdapter.update(mSongsList);
                                mAdapter.notifyDataSetChanged();
                            }
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
                    return true;
                }
                case ADD_TO_QUEUE:
                    HashMap<String, String> song = mSongsList.get(mSelectedPosition);
                    mApp.getService().Add(song, 2);
                    break;
                case PLAY_NEXT:
                    HashMap<String, String> so = mSongsList.get(mSelectedPosition);
                    mApp.getService().Add(so, 1);
                    break;
                case SHARE_ITEM:
                    MusicUtils.shareTheMusic(mSongsList, mSelectedPosition, mContext);
                    break;
                case ADD_TO_FAVORITES:
                    mApp.getDBAccessHelper().addTOFavorites(Integer.parseInt(mSongsList.get(mSelectedPosition).get(SONG_ID)));
                    break;
                default:
                    break;
            }
        } else if (item.getGroupId() == 1) {
            final int mSelectedPosition = FragmentFolder.mSelectedPosition;
            mSongId = (int) Long.parseLong(FragmentFolder.fetchedFiles.get(mSelectedPosition).get(SONG_ID));
            switch (item.getItemId()) {
                case NEW_PLAYLIST:
                    PlaylistDialog b = new PlaylistDialog(getActivity(),new long[]{mSongId});
                    b.show();
                    mLayoutParams = b.getWindow().getAttributes();
                    mLayoutParams.dimAmount = 0.5f;
                    b.getWindow().setAttributes(mLayoutParams);
                    b.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
                    return true;
                case PLAYLIST_SELECTED:
                    long[] list = new long[]{mSongId};
                    long playlist = item.getIntent().getLongExtra("playlist", 0);
                    MusicUtils.addToPlaylist(getContext(), list, playlist);
                    return true;
                case USE_AS_RINGTONE:
                    MusicUtils.setRingtone(getActivity(), mSongId);
                    return true;
                case DELETE_ITEM: {
                    final File file = new File(FragmentFolder.fetchedFiles.get(mSelectedPosition).get(SONG_PATH));
                    final Dialog dialog = new Dialog(new ContextThemeWrapper(getActivity(), R.style.myDialog));
                    dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.unfavorites_dialog);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                    dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnim;
                    ((TextView) dialog.findViewById(R.id.playlist)).setText(file.getName() + " Song will be deleted!!");
                    (dialog.findViewById(R.id.create)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mApp.getPreferencesUtility().getSharedPreferenceUri() == null && !MusicUtils.isKitkat()) {
                                askForUriPermission();
                            } else {
                                new AsyncDeleteTask(mContext, file).execute();
                                ((Main) getActivity()).getFragment().refreshListView();
                            }
                            dialog.dismiss();
                        }
                    });
                    ((Button) dialog.findViewById(R.id.cancel)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                    return true;
                }
                case ADD_TO_QUEUE:
                    HashMap<String, String> song = FragmentFolder.fetchedFiles.get(mSelectedPosition);
                    mApp.getService().Add(song, 2);
                    break;
                case PLAY_NEXT:
                    HashMap<String, String> so = FragmentFolder.fetchedFiles.get(mSelectedPosition);
                    mApp.getService().Add(so, 1);
                    break;
                case SHARE_ITEM:
                    MusicUtils.shareTheMusic(FragmentFolder.fetchedFiles, mSelectedPosition, mContext);
                    break;
                case ADD_TO_FAVORITES:
                    mApp.getDBAccessHelper().addTOFavorites(Integer.parseInt(FragmentFolder.fetchedFiles.get(mSelectedPosition).get(SONG_ID)));
                    break;
                default:
                    break;
            }
        }
        return super.onContextItemSelected(item);
    }


    public void askForUriPermission() {
        final Dialog dialog = new Dialog(mContext);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_sd_access);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnim;

        (dialog.findViewById(R.id.ascending)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                startActivityForResult(intent, MusicUtils.URI_REQUEST_CODE);
                dialog.dismiss();
            }
        });
        (dialog.findViewById(R.id.descending)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case NEW_PLAYLIST:
                if (resultCode == -1) {
                    Uri uri = data.getData();
                    if (uri != null) {
                        long[] list = new long[]{Long.parseLong(mSongsList.get(mSelectedPosition).get(SONG_ID))};
                        MusicUtils.addToPlaylist(getContext(), list, Integer.valueOf(uri.getLastPathSegment()));
                    }
                }
                break;
            case MusicUtils.URI_REQUEST_CODE:
                Uri treeUri = null;
                if (resultCode == Activity.RESULT_OK) treeUri = data.getData();
                mApp.getPreferencesUtility().setSharedPreferenceUri(treeUri.toString());
                File file = new File(mSongsList.get(mSelectedPosition).get(SONG_PATH));
                mContext.getContentResolver().takePersistableUriPermission(treeUri, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                new AsyncDeleteTask(mContext, file).execute();
                mSongsList.remove(mSelectedPosition);
                mAdapter.update(mSongsList);
                mAdapter.notifyDataSetChanged();
                break;
        }
    }

    public Drawable getDrawable() {
        return (view.findViewById(R.id.firstbg)).getBackground();
    }

    public void setViewColor(int color) {
        view.findViewById(R.id.firstbg).setBackgroundColor(color);
    }

}
