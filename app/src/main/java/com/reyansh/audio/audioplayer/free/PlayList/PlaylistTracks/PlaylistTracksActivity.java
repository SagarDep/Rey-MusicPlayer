package com.reyansh.audio.audioplayer.free.PlayList.PlaylistTracks;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.reyansh.audio.audioplayer.free.AsyncTasks.AsyncDeleteTask;
import com.reyansh.audio.audioplayer.free.Common.CommonClass;
import com.reyansh.audio.audioplayer.free.Dialog.PlaylistDialog;
import com.reyansh.audio.audioplayer.free.MusicService.MusicService;
import com.reyansh.audio.audioplayer.free.NowPlaying.NowPlaying;
import com.reyansh.audio.audioplayer.free.R;
import com.reyansh.audio.audioplayer.free.Search.SearchActivity;
import com.reyansh.audio.audioplayer.free.Utils.MetaRetriever;
import com.reyansh.audio.audioplayer.free.Utils.MusicUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

public class PlaylistTracksActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, MusicUtils.Defs, MusicUtils.names {
    private Cursor cursor;
    private ListView listView;
    private int mSelectedPosition;
    private long mSelectedId;
    private long mSelectedIda;
    private String songPath = "";
    private PlaylistTracksAdapter adapter;
    private ImageButton shuffle, play, addtoqueue;
    private Long playListID;
    private TextView PlaylistHeader, nooftracks, totalsongs;
    private ImageView playlistart;
    private Long a = 0L;
    private static Activity now_playing;
    private Context mContext;
    private TextView emptylist;
    private CommonClass mApp;
    private LinearLayout frameLayout;
    private WindowManager.LayoutParams mLayoutParams;
    private Toolbar mToolbar;
    private ArrayList<HashMap<String, String>> mSongsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        mApp = (CommonClass) mContext.getApplicationContext();
        if (mApp.getPreferencesUtility().getFullWindow()) {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        mSongsList = new ArrayList<>();
        setContentView(R.layout.demo_details);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        emptylist = (TextView) findViewById(R.id.emptylist);
        playlistart = (ImageView) findViewById(R.id.album_art);
        playlistart.bringToFront();
        listView = (ListView) findViewById(R.id.album_tracks_list);
        Bundle bundle = getIntent().getExtras();
        playListID = Long.valueOf(bundle.getString(PLAYLIST_ID));
        PlaylistHeader = (TextView) findViewById(R.id.Album_Name);
        nooftracks = (TextView) findViewById(R.id.Artist_Name);
        totalsongs = (TextView) findViewById(R.id.TotalSongs);
        nooftracks = (TextView) findViewById(R.id.Artist_Name);
        mSongsList = loadAlbumTracks(playListID);
        PlaylistHeader.setText(bundle.getString(PLAYLIST_NAME));
        now_playing = this;
        nooftracks.setText("Total Tracks: " + mSongsList.size());
        shuffle = (ImageButton) findViewById(R.id.shuffle);
        play = (ImageButton) findViewById(R.id.play);
        addtoqueue = (ImageButton) findViewById(R.id.addtoqueue);
        try {
            totalsongs.setText("Total Time :" + MusicUtils.makeShortTimeString(this, a / 1000));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        listView.setOnItemClickListener(this);
        addtoqueue.setOnClickListener(addtoqueuetoplay);
        play.setOnClickListener(playit);
        shuffle.setOnClickListener(shuffleit);
        listView.setOnCreateContextMenuListener(this);
        listView.setDividerHeight(0);
        listView.setDivider(null);
        adapter = new PlaylistTracksAdapter(this, mSongsList);
        listView.setAdapter(adapter);
        View view = this.getWindow().getDecorView();
        view.setBackgroundColor(Color.parseColor("#000000"));
        frameLayout = (LinearLayout) findViewById(R.id.albumtracksactivitylayout);
        background();
    }

    public static Activity getInstance() {
        return now_playing;
    }

    private void background() {
        int randomInt = 0;
        final Random random = new Random();
        if (mSongsList.size() != 0) {
            randomInt = random.nextInt(mSongsList.size());
            Bitmap artwork = ImageLoader.getInstance().loadImageSync(MusicUtils.getAlbumArtUri(Long.parseLong(mSongsList.get(randomInt)
                    .get(SONG_ALBUM_ID))).toString());
            if (artwork != null) {
                playlistart.setImageBitmap(artwork);
                frameLayout.setBackground(MetaRetriever.createBlurredImageFromBitmap(artwork, getApplicationContext()));
            } else {
                nopath();
            }
        } else {
            emptylist.setVisibility(View.VISIBLE);
            nopath();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_overflow, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_search) {
            startActivity(new Intent(this, SearchActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    void nopath() {
        Bitmap ok = ImageLoader.getInstance().loadImageSync("drawable://" + R.drawable.album_art_6);
        playlistart.setImageBitmap(ok);
        frameLayout.setBackground(MetaRetriever.createBlurredImageFromBitmap(ok, this));
    }


    public ArrayList<HashMap<String, String>> loadAlbumTracks(Long playListID) {
        ArrayList<HashMap<String, String>> mArtistList = new ArrayList<HashMap<String, String>>();

        try {
            String[] columns = {
                    MediaStore.Audio.Playlists.Members.AUDIO_ID,
                    MediaStore.Audio.Playlists.Members.TITLE,
                    MediaStore.Audio.Playlists.Members.ARTIST,
                    MediaStore.Audio.Playlists.Members.DURATION,
                    MediaStore.Audio.Playlists.Members.DATA,
                    MediaStore.Audio.Playlists.Members.ALBUM,
                    MediaStore.Audio.Playlists.Members.ALBUM_ID,
                    MediaStore.Audio.Playlists.Members._ID,
            };
            String[] column = {
                    MediaStore.Audio.Media._ID,
                    MediaStore.Audio.Media.TITLE,
                    MediaStore.Audio.Media.ARTIST,
                    MediaStore.Audio.Media.DURATION,
                    MediaStore.Audio.Media.DATA,
                    MediaStore.Audio.Media.ALBUM,
                    MediaStore.Audio.Media.ALBUM_ID,
                    MediaStore.Audio.Media._ID,
            };
            if (playListID == -1) {
                int X = mApp.getPreferencesUtility().getWeeks() * (3600 * 24 * 7);
                String where = MediaStore.MediaColumns.DATE_ADDED + ">" + (System.currentTimeMillis() / 1000 - X);
                cursor = this.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        column, where, null, MediaStore.Audio.Media.DATE_ADDED + " DESC");
            } else if (playListID == -2) {
                totalsongs.setVisibility(View.INVISIBLE);
                return mApp.getDBAccessHelper().getFavorites();
            } else if (playListID == -3) {
                totalsongs.setVisibility(View.INVISIBLE);
                return mApp.getDBAccessHelper().getTopTracks();
            } else if (playListID == -4) {
                totalsongs.setVisibility(View.INVISIBLE);
                return mApp.getDBAccessHelper().getRecentlyPlayed();
            } else {
                cursor = this.getContentResolver().query(
                        MediaStore.Audio.Playlists.Members.getContentUri("external", playListID),
                        columns,
                        null,
                        null,
                        MediaStore.Audio.Media.TITLE);
            }
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    if (cursor.getInt(3) > 6000) {
                        HashMap<String, String> albumTracks = new HashMap<String, String>();
                        albumTracks.put(SONG_ID, cursor.getString(0));
                        albumTracks.put(SONG_NAME, cursor.getString(1));
                        albumTracks.put(SONG_ARTIST, cursor.getString(2));
                        albumTracks.put(SONG_DURATION, cursor.getString(3));
                        a = a + cursor.getLong(3);
                        albumTracks.put(SONG_PATH, cursor.getString(4));
                        albumTracks.put(SONG_ALBUM, cursor.getString(5));
                        albumTracks.put(SONG_ALBUM_ID, cursor.getString(6));
                        albumTracks.put("ooo", cursor.getString(7));
                        mArtistList.add(albumTracks);
                    }
                } while (cursor.moveToNext());
            }
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mArtistList;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mApp.getService().setSongList(mSongsList);
        mApp.getService().setSelectedSong(position, MusicService.NOTIFICATION_ID);
        startActivity(new Intent(this, NowPlaying.class));
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfoIn) {
        menu.add(0, PLAY_NEXT, 0, R.string.play_next);
        menu.add(0, ADD_TO_QUEUE, 0, R.string.add_to_queue);
        SubMenu sub = menu.addSubMenu(0, ADD_TO_PLAYLIST, 0, R.string.add_to_playlist);
        MusicUtils.makePlaylistMenu(this, sub, 0);
        menu.add(0, ADD_TO_FAVORITES, 0, R.string.add_to_favorites);
        menu.add(0, DELETE_ITEM, 0, R.string.remove_from_playlist);
        menu.add(0, SHARE_ITEM, 0, R.string.share_item);
        AdapterView.AdapterContextMenuInfo mi = (AdapterView.AdapterContextMenuInfo) menuInfoIn;
        mSelectedPosition = mi.position;
        mSelectedId = Long.parseLong(mSongsList.get(mSelectedPosition).get(SONG_ID));
        mSelectedIda = Long.parseLong(mSongsList.get(mSelectedPosition).get(SONG_ID));
        songPath = mSongsList.get(mSelectedPosition).get(SONG_PATH);

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case NEW_PLAYLIST:
                PlaylistDialog b = new PlaylistDialog(this, new long[]{mSelectedId});
                b.show();
                WindowManager.LayoutParams lp = b.getWindow().getAttributes();
                lp.dimAmount = 0.5f;
                b.getWindow().setAttributes(lp);
                b.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
                return true;
            case PLAYLIST_SELECTED: {
                long[] list = new long[]{mSelectedIda};
                long playlist = item.getIntent().getLongExtra("playlist", 0);
                MusicUtils.addToPlaylist(this, list, playlist);
                return true;
            }
            case DELETE_ITEM: {
                if (playListID == -1) {
                    final File file = new File(mSongsList.get(mSelectedPosition).get(SONG_PATH));
                    final Dialog dialog = new Dialog(new android.support.v7.view.ContextThemeWrapper(this, R.style.myDialog));
                    dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.unfavorites_dialog);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                    dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnim;
                    ((TextView) dialog.findViewById(R.id.playlist)).setText(file.getName() + " Song will be deleted!!");
                    ((Button) dialog.findViewById(R.id.create)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mApp.getPreferencesUtility().getSharedPreferenceUri() == null && !MusicUtils.isKitkat()) {
                                MusicUtils.askForUriPermission(PlaylistTracksActivity.this, MusicUtils.URI_REQUEST_CODE);
                            } else {
                                new AsyncDeleteTask(mContext, file).execute();
                                mSongsList.remove(mSelectedPosition);
                                adapter.setSongsList(mSongsList);
                                adapter.notifyDataSetChanged();
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
                } else {
                    MusicUtils.removeFromPlaylist(this, mSelectedId, playListID, songPath);
                    Log.d("playlist", mSelectedId + "\n" + playListID + "\n" + songPath);
                    mSongsList.remove(mSelectedPosition);
                    adapter.setSongsList(mSongsList);
                    adapter.notifyDataSetChanged();
                }
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
            case PLAY_THIS:
                mApp.getService().setSongList(mSongsList);
                mApp.getService().setSelectedSong(mSelectedPosition, MusicService.NOTIFICATION_ID);
                startActivity(new Intent(this, NowPlaying.class));
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
        return super.onContextItemSelected(item);
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
                        MusicUtils.addToPlaylist(this, list, Integer.valueOf(uri.getLastPathSegment()));
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
                adapter.setSongsList(mSongsList);
                adapter.notifyDataSetChanged();
                break;
        }
    }

    private View.OnClickListener shuffleit = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            shufflesongs(0);
        }
    };

    private View.OnClickListener playit = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast.makeText(mContext, R.string.songsbeingplayed, Toast.LENGTH_SHORT).show();
            mApp.getService().setSongList(mSongsList);
            mApp.getService().setSelectedSong(0, MusicService.NOTIFICATION_ID);
        }
    };
    private View.OnClickListener addtoqueuetoplay = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast.makeText(mContext, R.string.songsaddedtothequeue, Toast.LENGTH_SHORT).show();
            mApp.getService().addtoqueue(mSongsList);

        }
    };

    private void shufflesongs(int position) {
        Toast.makeText(mContext, R.string.songshuffledplayed, Toast.LENGTH_SHORT).show();
        long seed = System.nanoTime();
        Collections.shuffle(mSongsList, new Random(seed));
        Collections.shuffle(mSongsList, new Random(seed));
        mApp.getService().setSongList(mSongsList);
        mApp.getService().setSelectedSong(position, MusicService.NOTIFICATION_ID);
    }

}
