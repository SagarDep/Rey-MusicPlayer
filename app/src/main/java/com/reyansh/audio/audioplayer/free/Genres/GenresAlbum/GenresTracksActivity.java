package com.reyansh.audio.audioplayer.free.Genres.GenresAlbum;


import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

import com.reyansh.audio.audioplayer.free.AsyncTasks.AsyncDeleteTask;
import com.reyansh.audio.audioplayer.free.Common.CommonClass;
import com.reyansh.audio.audioplayer.free.Dialog.PlaylistDialog;
import com.reyansh.audio.audioplayer.free.MusicService.MusicService;
import com.reyansh.audio.audioplayer.free.NowPlaying.NowPlaying;

import com.reyansh.audio.audioplayer.free.R;
import com.reyansh.audio.audioplayer.free.Search.SearchActivity;
import com.reyansh.audio.audioplayer.free.Utils.MetaRetriever;
import com.reyansh.audio.audioplayer.free.Utils.MusicUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;


public class GenresTracksActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, MusicUtils.Defs, MusicUtils.names {
    private ArrayList<HashMap<String, String>> mGenresTracksList;
    private GenresAlbumListAdapter mAdapter;

    private ListView listView;
    private TextView mGenresNameTextView;
    private TextView mNoOfTracksTextView;
    private TextView mTotalSongTextView;

    private String mGenresId;
    private Cursor mCursor;
    private int mSelectedPosition;

    private ImageButton mShuffleImageButton;
    private ImageButton mPlayImageButton;
    private ImageButton addtoqueue;

    private long mSelectedId;
    private CommonClass mApp;
    private LinearLayout background;
    private ImageView Album_Art;
    private static Activity albumActivity;
    private Context mContext;
    private Long a = 0L;
    private WindowManager.LayoutParams mLayoutParams;

    private Toolbar mToolbar;

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
        setContentView(R.layout.demo_details);
        listView = (ListView) findViewById(R.id.album_tracks_list);
        mGenresNameTextView = (TextView) findViewById(R.id.Album_Name);
        Bundle bundle = getIntent().getExtras();
        albumActivity = this;
        mGenresId = bundle.getString(GENRES_ID);
        Album_Art = (ImageView) findViewById(R.id.album_art);
        mShuffleImageButton = (ImageButton) findViewById(R.id.shuffle);
        mPlayImageButton = (ImageButton) findViewById(R.id.play);
        addtoqueue = (ImageButton) findViewById(R.id.addtoqueue);
        addtoqueue.setOnClickListener(addtoqueuetoplay);
        mNoOfTracksTextView = (TextView) findViewById(R.id.TotalSongs);
        mTotalSongTextView = (TextView) findViewById(R.id.Artist_Name);
        mPlayImageButton.setOnClickListener(playit);
        mShuffleImageButton.setOnClickListener(shuffleit);
        mGenresTracksList = loadGenresTracks();
        mTotalSongTextView.setText("Total Tracks:" + mGenresTracksList.size());
        try {
            mNoOfTracksTextView.setText("Total Time:" + MusicUtils.makeShortTimeString(this, a / 1000));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
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

        mGenresNameTextView.setText(bundle.getString(GENRES_NAME));
        background = (LinearLayout) findViewById(R.id.albumtracksactivitylayout);

        mApp = (CommonClass) mContext.getApplicationContext();
        background();
        Album_Art.bringToFront();
        listView.setOnItemClickListener(this);
        listView.setDividerHeight(0);
        listView.setDivider(null);
        listView.setOnCreateContextMenuListener(this);
        mAdapter = new GenresAlbumListAdapter(this, mGenresTracksList);
        listView.setAdapter(mAdapter);
        View view = this.getWindow().getDecorView();
        view.setBackgroundColor(Color.parseColor("#000000"));
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

    private void background() {
        int randomInt = 0;
        final Random random = new Random();
        if (mGenresTracksList.size() != 0) {
            randomInt = random.nextInt(mGenresTracksList.size());
            Bitmap artwork = ImageLoader.getInstance().loadImageSync(MusicUtils.getAlbumArtUri(Long.parseLong(mGenresTracksList.get(randomInt)
                    .get(SONG_ALBUM_ID))).toString());
            if (artwork != null) {
                Album_Art.setImageBitmap(artwork);
                background.setBackground(MetaRetriever.createBlurredImageFromBitmap(artwork, getApplicationContext()));
            } else {
                nopath();
            }
        } else {
            nopath();
        }
    }

    public static Activity getInstance() {
        return albumActivity;
    }

    void nopath() {
        Bitmap ok = BitmapFactory.decodeResource(this.getResources(), R.drawable.main_ic);
        Album_Art.setImageBitmap(ok);
        background.setBackground(MetaRetriever.createBlurredImageFromBitmap(ok, this));
    }

    /*

    private void populateTable() {
        runOnUiThread(new Runnable(){
            public void run() {
                Bitmap ok = ImageLoader.getInstance().loadImageSync("drawable://" + R.drawable.album_art_1);
                background.setBackground(MetaRetriever.createBlurredImageFromBitmap(ok, getApplication()));
            }
        });
    }*/


    private View.OnClickListener shuffleit = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mGenresTracksList.size() != 0) {
                shufflesongs(0);
            }
        }
    };

    private View.OnClickListener playit = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mGenresTracksList.size() != 0) {
                Toast.makeText(mContext, R.string.songsbeingplayed, Toast.LENGTH_SHORT).show();
                mApp.getService().setSongList(mGenresTracksList);
                mApp.getService().setSelectedSong(0, MusicService.NOTIFICATION_ID);
            }
        }
    };
    private View.OnClickListener addtoqueuetoplay = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast.makeText(mContext, R.string.songsaddedtothequeue, Toast.LENGTH_SHORT).show();
            mApp.getService().addtoqueue(mGenresTracksList);

        }
    };

    private void shufflesongs(int position) {
        Toast.makeText(mContext, R.string.songshuffledplayed, Toast.LENGTH_SHORT).show();
        long seed = System.nanoTime();
        Collections.shuffle(mGenresTracksList, new Random(seed));
        Collections.shuffle(mGenresTracksList, new Random(seed));
        mApp.getService().setSongList(mGenresTracksList);
        mApp.getService().setSelectedSong(position, MusicService.NOTIFICATION_ID);
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    public ArrayList<HashMap<String, String>> loadGenresTracks() {
        try {
            mGenresTracksList = new ArrayList<HashMap<String, String>>();
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
            mCursor = getContentResolver().query(uri, projection, null, null, null);
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
                    a = a + mCursor.getLong(4);
                    mGenresTracksList.add(album);
                } while (mCursor.moveToNext());
            }
            if (mCursor != null) {
                mCursor.close();
                mCursor = null;
            }
            //   Fragment_GenresAlbum.setGenresAlbumList(mArtistList);
            //AlbumTracksActivity.setAlbumSongList(mArtistList);
        } catch (Exception e) {

        }
        return mGenresTracksList;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mApp.getService().setSongList(mGenresTracksList);
        mApp.getService().setSelectedSong(position, MusicService.NOTIFICATION_ID);
        startActivity(new Intent(this, NowPlaying.class));
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfoIn) {
        menu.add(0, PLAY_NEXT, 0, R.string.play_next);
        menu.add(0, ADD_TO_QUEUE, 0, R.string.add_to_queue);
        SubMenu sub = menu.addSubMenu(0, ADD_TO_PLAYLIST, 0, R.string.add_to_playlist);
        MusicUtils.makePlaylistMenu(this, sub, 0);
        menu.add(0, ADD_TO_FAVORITES, 0, R.string.add_to_favorites);
        menu.add(0, USE_AS_RINGTONE, 0, R.string.ringtone_menu);
        menu.add(0, DELETE_ITEM, 0, R.string.delete_item);
        menu.add(0, SHARE_ITEM, 0, R.string.share_item);
        AdapterView.AdapterContextMenuInfo mi = (AdapterView.AdapterContextMenuInfo) menuInfoIn;
        mSelectedPosition = mi.position;
        mSelectedId = Long.parseLong(mGenresTracksList.get(mSelectedPosition).get(SONG_ID));
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
                long[] list = new long[]{mSelectedId};
                long playlist = item.getIntent().getLongExtra("playlist", 0);
                MusicUtils.addToPlaylist(this, list, playlist);
                return true;
            }
            case USE_AS_RINGTONE:
                MusicUtils.setRingtone(this, mSelectedId);
                return true;
            case DELETE_ITEM: {
                final File file = new File(mGenresTracksList.get(mSelectedPosition).get(SONG_PATH));
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
                            MusicUtils.askForUriPermission(GenresTracksActivity.this, MusicUtils.URI_REQUEST_CODE);
                        } else {
                            new AsyncDeleteTask(mContext, file).execute();
                            mGenresTracksList.remove(mSelectedPosition);
                            mAdapter.setSongsList(mGenresTracksList);
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
                HashMap<String, String> song = mGenresTracksList.get(mSelectedPosition);
                mApp.getService().Add(song, 2);
                break;
            case PLAY_NEXT:
                HashMap<String, String> so = mGenresTracksList.get(mSelectedPosition);
                mApp.getService().Add(so, 1);
                break;
            case PLAY_THIS:
                mApp.getService().setSongList(mGenresTracksList);
                mApp.getService().setSelectedSong(mSelectedPosition, MusicService.NOTIFICATION_ID);
                startActivity(new Intent(this, NowPlaying.class));
                break;
            case SHARE_ITEM:
                MusicUtils.shareTheMusic(mGenresTracksList, mSelectedPosition, mContext);
                break;
            case ADD_TO_FAVORITES:
                mApp.getDBAccessHelper().addTOFavorites(Integer.parseInt(mGenresTracksList.get(mSelectedPosition).get(SONG_ID)));
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
                File file = new File(mGenresTracksList.get(mSelectedPosition).get(SONG_PATH));
                mContext.getContentResolver().takePersistableUriPermission(treeUri, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                new AsyncDeleteTask(mContext, file).execute();
                mGenresTracksList.remove(mSelectedPosition);
                mAdapter.setSongsList(mGenresTracksList);
                mAdapter.notifyDataSetChanged();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGenresTracksList = loadGenresTracks();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }


}
