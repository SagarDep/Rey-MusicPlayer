package com.reyansh.audio.audioplayer.free.PlayList;

import android.app.Dialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.reyansh.audio.audioplayer.free.Common.CommonClass;
import com.reyansh.audio.audioplayer.free.Dialog.RecentlyAddedDialog;
import com.reyansh.audio.audioplayer.free.Dialog.RenameDialog;
import com.reyansh.audio.audioplayer.free.MainActivity.Main;
import com.reyansh.audio.audioplayer.free.MusicService.MusicService;
import com.reyansh.audio.audioplayer.free.R;
import com.reyansh.audio.audioplayer.free.Views.FastScroller;
import com.reyansh.audio.audioplayer.free.Utils.MusicUtils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by REYANSH on 31/07/2016.
 */
public class FragmentPlaylist extends Fragment implements MusicUtils.Defs, MusicUtils.names {
    private RecyclerView mRecyclerView;
    private Context mContext;
    private CommonClass mApp;
    private FastScroller mFastScroller;
    private AdapterPlaylist mAdapter;
    private ArrayList<HashMap<String, String>> data;
    private int mSelectedPosition;
    private RelativeLayout mRecentlyAddedLayout;
    private RelativeLayout mFavoritesLayout;
    private RelativeLayout mTopPlayedLayout;
    private RelativeLayout mRecentlyPlayedLayout;
    View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_album_layout, container, false);
        mContext = getContext();
        mApp = (CommonClass) mContext.getApplicationContext();
        data = new ArrayList<>();
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mFastScroller = (FastScroller) view.findViewById(R.id.fastscroller);
        mFastScroller.setRecyclerView(mRecyclerView);
        mAdapter = new AdapterPlaylist(mContext, data, this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnScrollListener(onScrollChangeListener);

//        mRecentlyAddedLayout = (RelativeLayout) view.findViewById(R.id.relative_layout_recently_added);
//        mFavoritesLayout = (RelativeLayout) view.findViewById(R.id.relative_layout_favorites);
//        mTopPlayedLayout = (RelativeLayout) view.findViewById(R.id.relative_layout_top_played);
//        mRecentlyPlayedLayout = (RelativeLayout) view.findViewById(R.id.relative_layout_recently_played);

//        mRecentlyAddedLayout.setOnClickListener(this);
//        mFavoritesLayout.setOnClickListener(this);
//        mTopPlayedLayout.setOnClickListener(this);
//        mRecentlyPlayedLayout.setOnClickListener(this);


        new AsyncFetchPlaylist(mContext, this).execute();
        return view;
    }

    public void updateData(ArrayList<HashMap<String, String>> data) {
        this.data = data;
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
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        new AsyncFetchPlaylist(mContext, this).execute();
    }

    public void createContextMenu(Menu menu, View v, int position) {
        mSelectedPosition = position;
        switch (position) {
            case 0:
                menu.add(6, RECENTLY_ADDED_WEEK, 0, R.string.editweek);
                break;
            case 1:
                menu.add(6, CLEAR_FAVORITES, 0, R.string.clearfavorites);
                break;
            case 2:
                menu.add(6, CLEAR_TOPTRACKS, 0, R.string.cleartoptracks);
                break;
            case 3:
                menu.add(6, CLEAR_RECENTLY_PLAYED, 0, R.string.clearrecentlyplayed);
                break;
            default:
                menu.add(6, PLAY, 0, R.string.playit);
                menu.add(6, RENAME_PLAYLIST, 0, R.string.rename_playlist);
                menu.add(6, DELETE_PLAYLIST, 0, R.string.delete_playlist);
                break;
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getGroupId() == 6) {
            switch (item.getItemId()) {
                case PLAY:
                    mApp.getService().setSongList(MusicUtils.playPlaylist(getContext(), Long.parseLong(data.get(mSelectedPosition).get(MusicUtils.names.PLAYLIST_ID))));
                    mApp.getService().setSelectedSong(0, MusicService.NOTIFICATION_ID);
                    break;
                case RENAME_PLAYLIST:
                    RenameDialog d = new RenameDialog(mContext, Long.parseLong(data.get(mSelectedPosition).get(MusicUtils.names.PLAYLIST_ID)));
                    d.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(final DialogInterface arg0) {
                            setMenuVisibility(true);
                        }
                    });
                    d.show();
                    WindowManager.LayoutParams lp = d.getWindow().getAttributes();
                    lp.dimAmount = 0.5f;
                    d.getWindow().setAttributes(lp);
                    d.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
                    break;
                case DELETE_PLAYLIST:
                    if (mSelectedPosition != 0) {
                        Uri uri = ContentUris.withAppendedId(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
                                Long.parseLong(data.get(mSelectedPosition).get(MusicUtils.names.PLAYLIST_ID)));
                        mContext.getContentResolver().delete(uri, null, null);
                        Toast.makeText(mContext, R.string.playlist_deleted_message, Toast.LENGTH_SHORT).show();
                        data.remove(mSelectedPosition);
                        mAdapter.updateData(data);
                        mAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(mContext, R.string.Auto_playlist_deleted_message, Toast.LENGTH_SHORT).show();
                    }
                    break;
                case RECENTLY_ADDED_WEEK:
                    RecentlyAddedDialog f = new RecentlyAddedDialog(mContext);
                    f.show();
                    WindowManager.LayoutParams lpa = f.getWindow().getAttributes();
                    lpa.dimAmount = 0.5f;
                    f.getWindow().setAttributes(lpa);
                    f.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
                    break;
                case CLEAR_FAVORITES:
                    final Dialog da = new Dialog(getActivity());
                    da.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    da.setContentView(R.layout.unfavorites_dialog);
                    ((TextView) da.findViewById(R.id.playlist)).setText(R.string.unfavorite_long);
                    da.getWindow().getAttributes().windowAnimations = R.style.DialogAnim;
                    da.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                    da.show();
                    WindowManager.LayoutParams lap = da.getWindow().getAttributes();
                    lap.dimAmount = 0.5f;
                    da.getWindow().setAttributes(lap);
                    da.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
                    ((Button) da.findViewById(R.id.create)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mApp.getDBAccessHelper().clearFavorites();
                            Toast.makeText(mContext, R.string.favoritescleared, Toast.LENGTH_SHORT).show();
                            da.dismiss();
                        }
                    });
                    ((Button) da.findViewById(R.id.cancel)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            da.dismiss();
                        }
                    });
                    break;
                case CLEAR_TOPTRACKS:
                    final Dialog daa = new Dialog(getActivity());
                    daa.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    daa.setContentView(R.layout.unfavorites_dialog);
                    ((TextView) daa.findViewById(R.id.playlist)).setText(R.string.cleartoptracks_long);
                    daa.getWindow().getAttributes().windowAnimations = R.style.DialogAnim;
                    daa.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                    daa.show();
                    ((Button) daa.findViewById(R.id.create)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mApp.getDBAccessHelper().clearTopTracks();
                            Toast.makeText(mContext, R.string.favoritescleared, Toast.LENGTH_SHORT).show();
                            daa.dismiss();
                        }
                    });
                    ((Button) daa.findViewById(R.id.cancel)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            daa.dismiss();
                        }
                    });
                    break;
                case CLEAR_RECENTLY_PLAYED:
                    final Dialog daa1 = new Dialog(getActivity());
                    daa1.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    daa1.setContentView(R.layout.unfavorites_dialog);
                    ((TextView) daa1.findViewById(R.id.playlist)).setText(R.string.clearrecentlyplayed);
                    daa1.getWindow().getAttributes().windowAnimations = R.style.DialogAnim;
                    daa1.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                    daa1.show();
                    (daa1.findViewById(R.id.create)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mApp.getDBAccessHelper().clearRecentlyPlayed();
                            Toast.makeText(mContext, R.string.recentlyplayedcleared, Toast.LENGTH_SHORT).show();
                            daa1.dismiss();
                        }
                    });
                    (daa1.findViewById(R.id.cancel)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            daa1.dismiss();
                        }
                    });
                    break;
                default:
                    break;
            }
        }
        return super.onContextItemSelected(item);
    }
//    @Override
//    public void onClick(View v) {
//        Bundle bundle = new Bundle();
//        Intent in = new Intent(mContext, PlaylistTracksActivity.class);
//        switch (v.getId()) {
//            case R.id.relative_layout_favorites:
//                in.putExtra(PLAYLIST_ID, "-2");
//                in.putExtra(PLAYLIST_NAME, "Favorites");
//                mContext.startActivity(in);
//                break;
//            case R.id.relative_layout_top_played:
//                in.putExtra(PLAYLIST_ID, "-3");
//                in.putExtra(PLAYLIST_NAME, "Top Played");
//                mContext.startActivity(in);
//
//                break;
//            case R.id.relative_layout_recently_played:
//                in.putExtra(PLAYLIST_ID, "-4");
//                in.putExtra(PLAYLIST_NAME, "Recently Played");
//                mContext.startActivity(in);
//
//                break;
//            case R.id.relative_layout_recently_added:
//                in.putExtra(PLAYLIST_ID, "-1");
//                in.putExtra(PLAYLIST_NAME, "Recently Added");
//                mContext.startActivity(in);
//
//                break;
//        }
//    }


    public Drawable getDrawable() {
        return (view.findViewById(R.id.firstbg)).getBackground();
    }
    public void setViewColor(int color) {
        view.findViewById(R.id.firstbg).setBackgroundColor(color);
    }

}
