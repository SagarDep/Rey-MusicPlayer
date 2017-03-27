package com.reyansh.audio.audioplayer.free.Search;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SubMenu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.reyansh.audio.audioplayer.free.Album.AsyncFetchAlbums;
import com.reyansh.audio.audioplayer.free.Artist.AsyncFetchArtists;
import com.reyansh.audio.audioplayer.free.AsyncTasks.AsyncDeleteTask;
import com.reyansh.audio.audioplayer.free.Common.CommonClass;
import com.reyansh.audio.audioplayer.free.Dialog.PlaylistDialog;
import com.reyansh.audio.audioplayer.free.R;
import com.reyansh.audio.audioplayer.free.Utils.MetaRetriever;
import com.reyansh.audio.audioplayer.free.Utils.MusicUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class SearchActivity extends AppCompatActivity implements MusicUtils.names, MusicUtils.Defs {
    private SearchView mSearchView;
    private InputMethodManager mImm;
    private String queryString;
    CommonClass mApp;
    Context mContext;
    private SearchAdapter mAdapter;
    private RecyclerView recyclerView;
    private ArrayList<HashMap<String, String>> searchResults = new ArrayList<>();
    private ImageView mainbg;
    private int mSelectedPosition;
    private int mSongId;
    private WindowManager.LayoutParams mLayoutParams;
    private HashMap<String, String> hashMap;
    private ArrayList<HashMap<String, String>> mSongsList;
    private EditText mSearchEditText;
    private static SearchActivity searchActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        mContext = getApplicationContext();
        mApp = (CommonClass) mContext.getApplicationContext();
        mImm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mainbg = (ImageView) findViewById(R.id.mainbg);
        mainbg.setImageBitmap(MetaRetriever.getsInstance().getBlurredArtWorkA(mContext));
        this.getWindow().getDecorView().setBackgroundColor(Color.parseColor("#000000"));
        mSearchEditText = (EditText) findViewById(R.id.edit_text_search);
        mSearchEditText.addTextChangedListener(textWatcher);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new SearchAdapter(this, this);
        recyclerView.setAdapter(mAdapter);
        searchActivity = this;
    }

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            onQueryTextChange(s.toString());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };


    public boolean onQueryTextChange(final String newText) {
        if (newText.equals(queryString)) {
            return true;
        }
        queryString = newText;
        if (!queryString.trim().equals("")) {
            this.searchResults = new ArrayList();

            ArrayList<HashMap<String, String>> songHeader = new ArrayList<>();
            ArrayList<HashMap<String, String>> songList = MusicUtils.searchSongs(this, queryString);
            ArrayList<HashMap<String, String>> albumList = AsyncFetchAlbums.getAlbums(mContext, queryString);
            ArrayList<HashMap<String, String>> artistsList = AsyncFetchArtists.getArtists(mContext, queryString);
            if (!songList.isEmpty()) {
                HashMap<String, String> header = new HashMap<>();
                header.put(MusicUtils.names.TYPE, "HEADER");
                header.put("HEADERS TYPE", "Songs");
                songHeader.add(header);
                searchResults.addAll((Collection) songHeader);
                searchResults.addAll((Collection) (songList));
            }
            if (!albumList.isEmpty()) {
                ArrayList<HashMap<String, String>> albumHeader = new ArrayList<>();
                HashMap<String, String> header = new HashMap<>();
                header.put(MusicUtils.names.TYPE, "HEADER");
                header.put("HEADERS TYPE", "Albums");
                albumHeader.add(header);
                searchResults.addAll((Collection) albumHeader);
                searchResults.addAll((Collection) (albumList));
            }

            if (!artistsList.isEmpty()) {
                ArrayList<HashMap<String, String>> artistHeader = new ArrayList<>();
                HashMap<String, String> header = new HashMap<>();
                header.put(MusicUtils.names.TYPE, "HEADER");
                header.put("HEADERS TYPE", "Artists");
                artistHeader.add(header);
                searchResults.addAll((Collection) artistHeader);
                searchResults.addAll((Collection) (artistsList));
            }
        } else {
            searchResults.clear();
            mAdapter.update(searchResults);
            mAdapter.notifyDataSetChanged();
        }
        mAdapter.updateSearchResults(searchResults);
        mAdapter.notifyDataSetChanged();
        return true;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        hideInputManager();
        return super.dispatchTouchEvent(ev);
    }

    public void hideInputManager() {
        if (mSearchEditText != null) {
            if (mImm != null) {
                mImm.hideSoftInputFromWindow(mSearchEditText.getWindowToken(), 0);
            }
            mSearchEditText.clearFocus();
        }
    }


    public static Activity getInstance() {
        return searchActivity;
    }

    public void createContextMenu(Menu menu, View v, int position, ArrayList<HashMap<String, String>> hashMap) {
        menu.add(0, PLAY_NEXT, 0, R.string.play_next);
        menu.add(0, ADD_TO_QUEUE, 0, R.string.add_to_queue);
        SubMenu sub = menu.addSubMenu(0, ADD_TO_PLAYLIST, 0, R.string.add_to_playlist);
        MusicUtils.makePlaylistMenu(this, sub, 0);
        menu.add(0, ADD_TO_FAVORITES, 0, R.string.add_to_favorites);
        menu.add(0, USE_AS_RINGTONE, 0, R.string.ringtone_menu);
        menu.add(0, DELETE_ITEM, 0, R.string.delete_item);
        menu.add(0, SHARE_ITEM, 0, R.string.share_item);
        this.hashMap = hashMap.get(position);
        mSelectedPosition = position;
        mSongsList = hashMap;
        mSongId = Integer.parseInt(hashMap.get(position).get(SONG_ID));
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getGroupId() == 0) {
            switch (item.getItemId()) {
                case NEW_PLAYLIST:
                    PlaylistDialog b = new PlaylistDialog(mContext, new long[]{mSongId});
                    b.show();
                    mLayoutParams = b.getWindow().getAttributes();
                    mLayoutParams.dimAmount = 0.5f;
                    b.getWindow().setAttributes(mLayoutParams);
                    b.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
                    return true;
                case PLAYLIST_SELECTED:
                    long[] list = new long[]{mSongId};
                    long playlist = item.getIntent().getLongExtra("playlist", 0);
                    MusicUtils.addToPlaylist(mContext, list, playlist);
                    return true;
                case USE_AS_RINGTONE:
                    MusicUtils.setRingtone(mContext, mSongId);
                    return true;
                case ADD_TO_QUEUE:
                    mApp.getService().Add(hashMap, 2);
                    break;
                case PLAY_NEXT:
                    mApp.getService().Add(hashMap, 1);
                    break;
                case SHARE_ITEM:
                    MusicUtils.shareTheMusic(hashMap, SearchActivity.this);
                    break;
                case ADD_TO_FAVORITES:
                    mApp.getDBAccessHelper().addTOFavorites(Integer.parseInt(hashMap.get(SONG_ID)));
                    break;
                case DELETE_ITEM: {
                    final File file = new File(mSongsList.get(mSelectedPosition).get(SONG_PATH));
                    final Dialog dialog = new Dialog(new ContextThemeWrapper(SearchActivity.this, R.style.myDialog));
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
                    mLayoutParams = dialog.getWindow().getAttributes();
                    mLayoutParams.dimAmount = 0.5f;
                    dialog.getWindow().setAttributes(mLayoutParams);
                    dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
                    dialog.show();
                    return true;
                }
                default:
                    break;
            }
        }
        return true;
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
                        MusicUtils.addToPlaylist(mContext, list, Integer.valueOf(uri.getLastPathSegment()));
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
}