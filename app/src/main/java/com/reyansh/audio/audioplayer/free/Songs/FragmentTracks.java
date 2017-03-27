/*
package com.reyansh.audio.audioplayer.free.Songs;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.view.ContextThemeWrapper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.nhaarman.listviewanimations.appearance.simple.ScaleInAnimationAdapter;
import com.reyansh.audio.audioplayer.free.AsyncTasks.AsyncDeleteTask;
import com.reyansh.audio.audioplayer.free.Common.CommonClass;
import com.reyansh.audio.audioplayer.free.Dialog.PlaylistDialog;
import com.reyansh.audio.audioplayer.free.Dialog.SortDialog;
import com.reyansh.audio.audioplayer.free.FileDirectory.FragmentFolder;
import com.reyansh.audio.audioplayer.free.Main;
import com.reyansh.audio.audioplayer.free.MusicService.MusicService;
import com.reyansh.audio.audioplayer.free.NowPlaying.NowPlaying;
import com.reyansh.audio.audioplayer.free.R;
import com.reyansh.audio.audioplayer.free.Setting.SettingsActivity;
import com.reyansh.audio.audioplayer.free.Task.MusicUtils;
import com.reyansh.audio.audioplayer.free.Task.PreferencesUtility;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

public class FragmentTracks extends Fragment implements AdapterView.OnItemClickListener, MusicUtils.Defs, MusicUtils.names,
        View.OnClickListener {

    private EditText mSearchView;
    private LinearLayout searchviewlayout;
    private ListView mListSongs;
    private LinearLayout lymenubar;
    private long mSelectedId;
    private InputMethodManager imm;
    private Context mContext;
    private int mPreviousVisibleItem;
    private int mSelectedPosition;
    private ImageView btnSearch, btnSort, btnShuffle, btnSettings;
    private LinearLayout btnSearchl, btnSortl, btnShufflel, btnSettingsl;
    private PreferencesUtility sInstance;
    private ArrayList<HashMap<String, String>> mSongsList;
    private RelativeLayout.LayoutParams params;
    private ImageButton searchclose;
    private CommonClass mApp;
    private WindowManager.LayoutParams mLayoutParams;

    public FragmentTracks() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tracks_layout, container, false);
        mSongsList = new ArrayList<>();
        //mListSongs = (ListView) view.findViewById(R.id.songlista);
//        lymenubar = (LinearLayout) view.findViewById(R.id.lymenubar);
//        btnSearch = (ImageView) view.findViewById(R.id.search);
//        btnSearchl = (LinearLayout) view.findViewById(R.id.searchl);
//        btnSort = (ImageView) view.findViewById(R.id.sort);
//        btnSortl = (LinearLayout) view.findViewById(R.id.sortl);
//        btnShuffle = (ImageView) view.findViewById(R.id.shuffle);
//        btnShufflel = (LinearLayout) view.findViewById(R.id.shufflel);
//        btnSettings = (ImageView) view.findViewById(R.id.settings);
//        btnSettingsl = (LinearLayout) view.findViewById(R.id.settingsl);
//        mSearchView = (EditText) view.findViewById(R.id.editsearch);
//        searchviewlayout = (LinearLayout) view.findViewById(R.id.searchlayout);
//        searchclose = (ImageButton) view.findViewById(R.id.closesearch);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
        new TrackLoader().execute();
        mAdapter = new SongAdapter(getActivity(), android.R.id.list);
        mListSongs.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        searchclose.setOnClickListener(search);
        mListSongs.setOnItemClickListener(this);
        mListSongs.setOnCreateContextMenuListener(this);
        btnSearch.setOnClickListener(this);
        btnSearchl.setOnClickListener(this);
        btnSort.setOnClickListener(this);
        btnSortl.setOnClickListener(this);
        btnShuffle.setOnClickListener(this);
        btnShufflel.setOnClickListener(this);
        btnSettings.setOnClickListener(this);
        btnSettingsl.setOnClickListener(this);
        mApp = (CommonClass) mContext.getApplicationContext();
        ScaleInAnimationAdapter animationAdapter = new ScaleInAnimationAdapter(mAdapter);
        animationAdapter.setAbsListView(mListSongs);
        mListSongs.setAdapter(animationAdapter);
        sInstance = new PreferencesUtility(mContext.getApplicationContext());
        params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.FILL_PARENT,
                RelativeLayout.LayoutParams.FILL_PARENT
        );
        mListSongs.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem > mPreviousVisibleItem) {
                    lymenubar.setVisibility(View.GONE);
                } else if (firstVisibleItem < mPreviousVisibleItem) {
                    if (lymenubar.getVisibility() != View.VISIBLE)
                        YoYo.with(Techniques.SlideInUp).duration(300).playOn(lymenubar);
                    lymenubar.setVisibility(View.VISIBLE);
                }
                mPreviousVisibleItem = firstVisibleItem;
            }
        });
    }

    View.OnClickListener search = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mSearchView.setText("");
            searchviewlayout.setVisibility(View.GONE);
            View view = getActivity().getCurrentFocus();
            if (view != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
            params.setMargins(0, 0, 0, 0);
            mListSongs.setLayoutParams(params);
        }
    };


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
        //i();
    }

    private void shufflesongs(int position) {
        long seed = System.nanoTime();
        Collections.shuffle(mSongsList, new Random(seed));
        Collections.shuffle(mSongsList, new Random(seed));
        mApp.getService().setSongList(mSongsList);
        mApp.getService().setSelectedSong(position, MusicService.NOTIFICATION_ID);
        startActivity(new Intent(getActivity(), NowPlaying.class));
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mAdapter.getFilteredData() != null) {
            mApp.getService().setSongList(mAdapter.getFilteredData());
            mApp.getService().setSelectedSong(position, MusicService.NOTIFICATION_ID);
        } else {
            mApp.getService().setSongList(mSongsList);
            mApp.getService().setSelectedSong(position, MusicService.NOTIFICATION_ID);
        }
        startActivity(new Intent(getContext(), NowPlaying.class));
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfoIn) {
        menu.add(0, PLAY_NEXT, 0, R.string.play_next);
        menu.add(0, ADD_TO_QUEUE, 0, R.string.add_to_queue);
        SubMenu sub = menu.addSubMenu(0, ADD_TO_PLAYLIST, 0, R.string.add_to_playlist);
        MusicUtils.makePlaylistMenu(getContext(), sub);
        menu.add(0, ADD_TO_FAVORITES, 0, R.string.add_to_favorites);
        menu.add(0, USE_AS_RINGTONE, 0, R.string.ringtone_menu);
        menu.add(0, DELETE_ITEM, 0, R.string.delete_item);
        menu.add(0, SHARE_ITEM, 0, R.string.share_item);
        AdapterView.AdapterContextMenuInfo mi = (AdapterView.AdapterContextMenuInfo) menuInfoIn;
        mSelectedPosition = mi.position;
        mSelectedId = Long.parseLong(mSongsList.get(mSelectedPosition).get(SONG_ID));
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getGroupId() == 0) {
            switch (item.getItemId()) {
                case NEW_PLAYLIST:
                    PlaylistDialog b = new PlaylistDialog(getActivity(), mSelectedId);
                    b.show();
                    mLayoutParams = b.getWindow().getAttributes();
                    mLayoutParams.dimAmount = 0.5f;
                    b.getWindow().setAttributes(mLayoutParams);
                    b.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
                    return true;
                case PLAYLIST_SELECTED:
                    long[] list = new long[]{mSelectedId};
                    long playlist = item.getIntent().getLongExtra("playlist", 0);
                    MusicUtils.addToPlaylist(getContext(), list, playlist);
                    return true;
                case USE_AS_RINGTONE:
                    MusicUtils.setRingtone(getActivity(), mSelectedId);
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
                                mAdapter.setData(mSongsList);
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
                    mLayoutParams = dialog.getWindow().getAttributes();
                    mLayoutParams.dimAmount = 0.5f;
                    dialog.getWindow().setAttributes(mLayoutParams);
                    dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
                    dialog.show();
                    return true;
                }
                case ADD_TO_QUEUE:
                    HashMap<String, String> song = mAdapter.getItem(mSelectedPosition);
                    mApp.getService().Add(song, 2);
                    break;
                case PLAY_NEXT:
                    HashMap<String, String> so = mAdapter.getItem(mSelectedPosition);
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
        } else {
            final int mSelectedPosition = FragmentFolder.mSelectedPosition;
            mSelectedId = Long.parseLong(FragmentFolder.fetchedFiles.get(mSelectedPosition).get(SONG_ID));
            switch (item.getItemId()) {
                case NEW_PLAYLIST:
                    PlaylistDialog b = new PlaylistDialog(getActivity(), mSelectedId);
                    b.show();
                    mLayoutParams = b.getWindow().getAttributes();
                    mLayoutParams.dimAmount = 0.5f;
                    b.getWindow().setAttributes(mLayoutParams);
                    b.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
                    return true;
                case PLAYLIST_SELECTED:
                    long[] list = new long[]{mSelectedId};
                    long playlist = item.getIntent().getLongExtra("playlist", 0);
                    MusicUtils.addToPlaylist(getContext(), list, playlist);
                    return true;
                case USE_AS_RINGTONE:
                    MusicUtils.setRingtone(getActivity(), mSelectedId);
                    return true;
                case DELETE_ITEM: {
                    final File file = new File(FragmentFolder.fetchedFiles.get(mSelectedPosition).get(SONG_PATH));
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

    @Override
    public void onResume() {
        super.onResume();
        if (searchviewlayout.getVisibility() != View.VISIBLE) {
            new TrackLoader().execute();
            mAdapter.setData(mSongsList);
            mAdapter.notifyDataSetChanged();
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case NEW_PLAYLIST:
                if (resultCode == -1) {
                    Uri uri = data.getData();
                    if (uri != null) {
                        long[] list = new long[]{mSelectedId};
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
                mAdapter.setData(mSongsList);
                mAdapter.notifyDataSetChanged();
                break;
        }
    }

    @Override
    public void onClick(View v) {
//        int a = v.getId();
//        if (R.id.search == a || R.id.searchl == a) {
//            searchsongs();
//        } else if (R.id.sort == a || R.id.sortl == a) {
//            sortSong();
//        } else if (R.id.shuffle == a || R.id.shufflel == a) {
//            shufflesongs(0);
//        } else if (R.id.settings == a || R.id.settingsl == a) {
//            startActivity(new Intent(mContext, SettingsActivity.class));
//        }
    }

    private void searchsongs() {
        YoYo.with(Techniques.FadeIn).duration(1600).playOn(searchviewlayout);
        searchviewlayout.setVisibility(View.VISIBLE);
        params.setMargins(0, 100, 0, 0);
        mListSongs.setLayoutParams(params);
        mSearchView.requestFocus();
        imm.showSoftInput(mSearchView, InputMethodManager.SHOW_IMPLICIT);
        mSearchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count < before) {
                    mAdapter.resetData();
                }
                mAdapter.getFilter().filter(s.toString());
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    public class TrackLoader extends AsyncTask<Void, Void, ArrayList<HashMap<String, String>>> {

        @Override
        protected ArrayList<HashMap<String, String>> doInBackground(Void... params) {
            Cursor cursor;
            try {
                mSongsList = new ArrayList<HashMap<String, String>>();
                String[] columns = {
                        MediaStore.Audio.Media._ID,
                        MediaStore.Audio.Media.TITLE,
                        MediaStore.Audio.Media.ARTIST,
                        MediaStore.Audio.Media.DURATION,
                        MediaStore.Audio.Media.DATA,
                        MediaStore.Audio.Media.ALBUM,
                        MediaStore.Audio.Media.ALBUM_ID,
                };
                cursor = mContext.getContentResolver().query(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        columns,
                        null,
                        null,
                        mApp.getPreferencesUtility().getSongSortOrder());
                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        if (cursor.getInt(3) > 6000) {
                            HashMap<String, String> song = new HashMap<String, String>();
                            song.put(SONG_ID, cursor.getString(0));
                            song.put(SONG_NAME, cursor.getString(1));
                            song.put(SONG_ARTIST, cursor.getString(2));
                            song.put(SONG_DURATION, cursor.getString(3));
                            song.put(SONG_PATH, cursor.getString(4));
                            song.put(SONG_ALBUM, cursor.getString(5));
                            song.put(SONG_ALBUM_ID, cursor.getString(6));
                            mSongsList.add(song);
                        }
                    } while (cursor.moveToNext());
                }
                if (cursor != null) {
                    cursor.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return mSongsList;
        }

        @Override
        protected void onPostExecute(ArrayList<HashMap<String, String>> hashMaps) {
            mAdapter.setData(hashMaps);
            mSongsList = hashMaps;
            super.onPostExecute(hashMaps);
        }
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

}
*/
