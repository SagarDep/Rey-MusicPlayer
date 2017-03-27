package com.reyansh.audio.audioplayer.free.Album.AlbumTracks;


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
import android.provider.BaseColumns;
import android.provider.MediaStore;
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


public class AlbumTracksActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, MusicUtils.Defs, MusicUtils.names {
    private ArrayList<HashMap<String, String>> mAlbumTracksList;
    private Cursor mCursor;
    private ListView mListView;
    String TAG = "AlbumTracksActivity";
    private AlbumSongListAdapter mAdapter;
    private Context mContext;
    private String mAlbumName;
    private String mCoverPath;
    private ImageView mAlbumArt;
    private ImageButton mShuffleImageButton;
    private ImageButton mPlayImageButton;
    private ImageButton mAddToQueueButton;
    private TextView mAlbumNameTextView;
    private TextView mArtistNameTextView;
    private TextView mTotalSongsTextView;
    private TextView mReleaseYearTextView;
    private LinearLayout relativeLayout;
    private int mSelectedPosition;
    private long mSelectedId;
    private static Activity albumActivity;
    private CommonClass mApp;
    private WindowManager.LayoutParams mLayoutParams;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();
        mApp = (CommonClass) mContext.getApplicationContext();
        if (mApp.getPreferencesUtility().getFullWindow()) {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        mContext = getApplicationContext();
        mApp = (CommonClass) mContext.getApplicationContext();
        setContentView(R.layout.demo_details);
        Bundle bundle = getIntent().getExtras();

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

        mShuffleImageButton = (ImageButton) findViewById(R.id.shuffle);
        mPlayImageButton = (ImageButton) findViewById(R.id.play);
        mAddToQueueButton = (ImageButton) findViewById(R.id.addtoqueue);
        mAddToQueueButton.setOnClickListener(addtoqueuetoplay);
        mPlayImageButton.setOnClickListener(playit);
        mShuffleImageButton.setOnClickListener(shuffleit);
        albumActivity = this;
        mAlbumName = bundle.getString("AlbumNameForTracks");
        initviews();
        mReleaseYearTextView.setText("Year:" + bundle.getString("year"));
        mTotalSongsTextView.setText("Total Tracks:" + bundle.getString("NumberOfSongs"));
        mArtistNameTextView.setText("By:" + bundle.getString("ArtistNameForAlbum"));
        mCoverPath = bundle.getString("AlbumCoverPath");
        mListView.setOnItemClickListener(this);
        mListView.setOnCreateContextMenuListener(this);
        mListView.setDividerHeight(0);
        mListView.setDivider(null);
        mAlbumTracksList = loadAlbumTracks();
        background();
        mAdapter = new AlbumSongListAdapter(this, mAlbumTracksList);
        mListView.setAdapter(mAdapter);
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

    private View.OnClickListener shuffleit = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            shufflesongs(0);
        }
    };

    private View.OnClickListener playit = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mAlbumTracksList.size() != 0) {
                Toast.makeText(mContext,"Songs are being played!!",Toast.LENGTH_SHORT).show();
                mApp.getService().setSongList(mAlbumTracksList);
                mApp.getService().setSelectedSong(0, MusicService.NOTIFICATION_ID);
            }
        }
    };
    private View.OnClickListener addtoqueuetoplay = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mAlbumTracksList.size() != 0) {
                Toast.makeText(mContext,"Songs added to queue!",Toast.LENGTH_SHORT).show();
                mApp.getService().addtoqueue(mAlbumTracksList);
            }

        }
    };

    private void shufflesongs(int position) {
        if (mAlbumTracksList.size() != 0) {

            Toast.makeText(mContext,"Songs are shuffled and Being played!",Toast.LENGTH_SHORT).show();
            long seed = System.nanoTime();
            Collections.shuffle(mAlbumTracksList, new Random(seed));
            Collections.shuffle(mAlbumTracksList, new Random(seed));
            mApp.getService().setSongList(mAlbumTracksList);
            mApp.getService().setSelectedSong(position, MusicService.NOTIFICATION_ID);
        }
    }

    public void initviews() {
        mListView = (ListView) findViewById(R.id.album_tracks_list);
        relativeLayout = (LinearLayout) findViewById(R.id.albumtracksactivitylayout);
        mAlbumArt = (ImageView) findViewById(R.id.album_art);
        mAlbumArt.bringToFront();
        mAlbumNameTextView = (TextView) findViewById(R.id.Album_Name);
        mAlbumNameTextView.setText(mAlbumName);
        mArtistNameTextView = (TextView) findViewById(R.id.Artist_Name);
        mTotalSongsTextView = (TextView) findViewById(R.id.TotalSongs);
        mReleaseYearTextView = (TextView) findViewById(R.id.release_Year);
    }

    private void background() {
        if (mCoverPath != null) {
            try {
                Bitmap artwork = ImageLoader.getInstance().loadImageSync(MusicUtils.getAlbumArtUri(Long.parseLong(mAlbumTracksList.get(0)
                        .get(SONG_ALBUM_ID))).toString());
                relativeLayout.setBackground(MetaRetriever.createBlurredImageFromBitmap(artwork, mContext));
                mAlbumArt.setImageBitmap(artwork);
            } catch (Exception e) {
                nopath();
            }
        } else {
            nopath();
        }
    }

    void nopath() {
        Bitmap ok = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.album_art_7);
        mAlbumArt.setImageBitmap(ok);
        relativeLayout.setBackground(MetaRetriever.createBlurredImageFromBitmap(ok, mContext));
    }

    public ArrayList<HashMap<String, String>> loadAlbumTracks() {
//        try {
        ArrayList<HashMap<String, String>> mArtistList = new ArrayList<HashMap<String, String>>();
        String[] columns = {
                BaseColumns._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.ALBUM_ID,
        };
        String where = MediaStore.Audio.Media.ALBUM + "=?";
        String whereVal[] = {mAlbumName};
        mCursor = this.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                columns,
                where,
                whereVal,
                MediaStore.Audio.Media.TITLE);
        if (mCursor != null && mCursor.moveToFirst()) {
            do {
                HashMap<String, String> albumTracks = new HashMap<String, String>();
                albumTracks.put(SONG_ID, mCursor.getString(0));
                albumTracks.put(SONG_NAME, mCursor.getString(1));
                albumTracks.put(SONG_ARTIST, mCursor.getString(2));
                albumTracks.put(SONG_DURATION, mCursor.getString(3));
                albumTracks.put(SONG_PATH, mCursor.getString(4));
                albumTracks.put(SONG_ALBUM, mCursor.getString(5));
                albumTracks.put(SONG_ALBUM_ID, mCursor.getString(6));
                mArtistList.add(albumTracks);
            } while (mCursor.moveToNext());

        }
        if (mCursor != null) {
            mCursor.close();
            mCursor = null;
        }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        return mArtistList;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mApp.getService().setSongList(mAlbumTracksList);
        mApp.getService().setSelectedSong(position, MusicService.NOTIFICATION_ID);
        startActivity(new Intent(this, NowPlaying.class));
    }

    public static Activity getInstance() {
        return albumActivity;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAlbumTracksList = loadAlbumTracks();
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
        menu.add(0, USE_AS_RINGTONE, 0, R.string.ringtone_menu);
        menu.add(0, DELETE_ITEM, 0, R.string.delete_item);
        menu.add(0, SHARE_ITEM, 0, R.string.share_item);
        AdapterView.AdapterContextMenuInfo mi = (AdapterView.AdapterContextMenuInfo) menuInfoIn;
        mSelectedPosition = mi.position;
        mSelectedId = Long.parseLong(mAlbumTracksList.get(mSelectedPosition).get(SONG_ID));
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case NEW_PLAYLIST:
                PlaylistDialog b = new PlaylistDialog(this, mAlbumTracksList);
                b.show();
                WindowManager.LayoutParams lp = b.getWindow().getAttributes();
                lp.dimAmount = 0.5f;
                b.getWindow().setAttributes(lp);
                b.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
               /* Intent intent = new Intent();
                intent.setClass(this, CreatePlaylist.class);
                startActivityForResult(intent, NEW_PLAYLIST);*/
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
            case DELETE_ITEM:
                final File file = new File(mAlbumTracksList.get(mSelectedPosition).get(SONG_PATH));
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
                            MusicUtils.askForUriPermission(AlbumTracksActivity.this, MusicUtils.URI_REQUEST_CODE);
                        } else {
                            new AsyncDeleteTask(mContext, file).execute();
                            mAlbumTracksList.remove(mSelectedPosition);
                            mAdapter.setSongsList(mAlbumTracksList);
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

            case ADD_TO_QUEUE:
                HashMap<String, String> song = mAlbumTracksList.get(mSelectedPosition);
                mApp.getService().Add(song, 2);
                break;
            case PLAY_NEXT:
                HashMap<String, String> so = mAlbumTracksList.get(mSelectedPosition);
                mApp.getService().Add(so, 1);
                break;
            case PLAY_THIS:
                mApp.getService().setSongList(mAlbumTracksList);
                mApp.getService().setSelectedSong(mSelectedPosition, MusicService.NOTIFICATION_ID);
                startActivity(new Intent(this, NowPlaying.class));
                break;
            case SHARE_ITEM:
                MusicUtils.shareTheMusic(mAlbumTracksList, mSelectedPosition, mContext);
                break;
            case ADD_TO_FAVORITES:
                mApp.getDBAccessHelper().addTOFavorites(Integer.parseInt(mAlbumTracksList.get(mSelectedPosition).get(SONG_ID)));
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
                File file = new File(mAlbumTracksList.get(mSelectedPosition).get(SONG_PATH));
                mContext.getContentResolver().takePersistableUriPermission(treeUri, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                new AsyncDeleteTask(mContext, file).execute();
                mAlbumTracksList.remove(mSelectedPosition);
                mAdapter.setSongsList(mAlbumTracksList);
                mAdapter.notifyDataSetChanged();
                break;
        }
    }
}
