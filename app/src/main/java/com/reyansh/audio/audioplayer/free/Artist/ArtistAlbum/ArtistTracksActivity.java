package com.reyansh.audio.audioplayer.free.Artist.ArtistAlbum;

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

import com.reyansh.audio.audioplayer.free.Artist.ArtistSongLoader;
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


public class ArtistTracksActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, MusicUtils.names, MusicUtils.Defs {
    ArrayList<HashMap<String, String>> mArtistTracksList;
    Cursor cursor;
    String TAG = "AlbumTracksActivity";
    private WindowManager.LayoutParams mLayoutParams;
    private Context mContext;
    ArtistAlbumListAdapter mAdapter;
    private int mSelectedPosition;
    String mArtistId;
    private long mSelectedId;
    LinearLayout mRelativeLayout;
    ImageButton mShuffleImageButton, mPlayImageButton, mAddToQueueImageButton;
    TextView mArtistHeaderTextView, mNoOfTracksTextView, mNoOfAlbumsTextView, mYearTextView;
    ListView gridView;
    CommonClass mApp;
    ImageView albumart;
    static Activity albumActivity;
    Bundle bundle;
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
        setContentView(R.layout.demo_details);
        gridView = (ListView) findViewById(R.id.album_tracks_list);
        gridView.setOnCreateContextMenuListener(this);

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
        gridView.setDividerHeight(0);
        albumActivity = this;
        mRelativeLayout = (LinearLayout) findViewById(R.id.albumtracksactivitylayout);
        albumart = (ImageView) findViewById(R.id.album_art);
        albumart.bringToFront();
        bundle = getIntent().getExtras();
        mShuffleImageButton = (ImageButton) findViewById(R.id.shuffle);
        mPlayImageButton = (ImageButton) findViewById(R.id.play);
        mAddToQueueImageButton = (ImageButton) findViewById(R.id.addtoqueue);
        mArtistId = bundle.getString("artistID");
        mYearTextView = (TextView) findViewById(R.id.release_Year);
        albumdetails();
        mArtistHeaderTextView = (TextView) findViewById(R.id.Album_Name);
        mArtistHeaderTextView.setText(bundle.getString("ArtistName"));
        mAddToQueueImageButton.setOnClickListener(addtoqueuetoplay);
        mPlayImageButton.setOnClickListener(playit);
        mShuffleImageButton.setOnClickListener(shuffleit);
        mArtistTracksList = ArtistSongLoader.getSongsForArtist(this, Long.parseLong(mArtistId));
        gridView.setOnItemClickListener(this);
        mAdapter = new ArtistAlbumListAdapter(this, mArtistTracksList);
        gridView.setAdapter(mAdapter);
        View view = this.getWindow().getDecorView();
        view.setBackgroundColor(Color.parseColor("#000000"));
        background();
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

    private void albumdetails() {
        mNoOfTracksTextView = (TextView) findViewById(R.id.Artist_Name);
        mNoOfTracksTextView.setText("Total Tracks: " + bundle.getString("numberOfTracks"));
        mNoOfAlbumsTextView = (TextView) findViewById(R.id.TotalSongs);
        mNoOfAlbumsTextView.setText("Total Albums: " + bundle.getString("numberOfAlbums"));

    }

    private void background() {
        if (mArtistTracksList.size() != 0) {
            Bitmap artwork = ImageLoader.getInstance().loadImageSync(MusicUtils.getAlbumArtUri(Long.parseLong(mArtistTracksList.get(0)
                    .get(SONG_ALBUM_ID))).toString());
            if (artwork != null) {
                albumart.setImageBitmap(artwork);
                mRelativeLayout.setBackground(MetaRetriever.createBlurredImageFromBitmap(artwork, getApplicationContext()));
            } else {
                nopath();
            }
        } else {
            nopath();
        }
    }

    private View.OnClickListener shuffleit = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mArtistTracksList.size() != 0) {
                shufflesongs(0);
            }
        }
    };

    private View.OnClickListener playit = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mArtistTracksList.size() != 0) {
                Toast.makeText(mContext,"Songs are being played!",Toast.LENGTH_SHORT).show();
                mApp.getService().setSongList(mArtistTracksList);
                mApp.getService().setSelectedSong(0, MusicService.NOTIFICATION_ID);
            }
        }
    };
    private View.OnClickListener addtoqueuetoplay = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mArtistTracksList.size() != 0) {
                Toast.makeText(mContext,"Songs added to queue!!",Toast.LENGTH_SHORT).show();
                mApp.getService().addtoqueue(mArtistTracksList);
            }
        }
    };

    private void shufflesongs(int position) {
        Toast.makeText(mContext,"Songs are shuffled and being played!!",Toast.LENGTH_SHORT).show();
        long seed = System.nanoTime();
        Collections.shuffle(mArtistTracksList, new Random(seed));
        Collections.shuffle(mArtistTracksList, new Random(seed));
        mApp.getService().setSongList(mArtistTracksList);
        mApp.getService().setSelectedSong(position, MusicService.NOTIFICATION_ID);

    }

    public static Activity getInstance() {
        return albumActivity;
    }

    void nopath() {
        Bitmap ok = BitmapFactory.decodeResource(this.getResources(), R.drawable.album_art_7);
        albumart.setImageBitmap(ok);
        mRelativeLayout.setBackground(MetaRetriever.createBlurredImageFromBitmap(ok, this));
    }

    @Override
    protected void onResume() {
        super.onResume();
        mArtistTracksList = ArtistSongLoader.getSongsForArtist(this, Long.parseLong(mArtistId));
        mAdapter.setSongsList(mArtistTracksList);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfoIn) {
        menu.add(0, PLAY_NEXT, 0, R.string.play_next);
        menu.add(0, ADD_TO_QUEUE, 0, R.string.add_to_queue);
        SubMenu sub = menu.addSubMenu(0, ADD_TO_PLAYLIST, 0, R.string.add_to_playlist);
        MusicUtils.makePlaylistMenu(this, sub,0);
        menu.add(0, ADD_TO_FAVORITES, 0, R.string.add_to_favorites);
        menu.add(0, USE_AS_RINGTONE, 0, R.string.ringtone_menu);
        menu.add(0, DELETE_ITEM, 0, R.string.delete_item);
        menu.add(0, SHARE_ITEM, 0, R.string.share_item);
        AdapterView.AdapterContextMenuInfo mi = (AdapterView.AdapterContextMenuInfo) menuInfoIn;
        mSelectedPosition = mi.position;
        mSelectedId = Long.parseLong(mArtistTracksList.get(mSelectedPosition).get(SONG_ID));
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case NEW_PLAYLIST:
                PlaylistDialog b = new PlaylistDialog(this,new  long[]{ mSelectedId});
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
                final File file = new File(mArtistTracksList.get(mSelectedPosition).get(SONG_PATH));
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
                            MusicUtils.askForUriPermission(ArtistTracksActivity.this, MusicUtils.URI_REQUEST_CODE);
                        } else {
                            new AsyncDeleteTask(mContext, file).execute();
                            mArtistTracksList.remove(mSelectedPosition);
                            mAdapter.setSongsList(mArtistTracksList);
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
                HashMap<String, String> song = mArtistTracksList.get(mSelectedPosition);
                mApp.getService().Add(song, 2);
                break;
            case PLAY_NEXT:
                HashMap<String, String> so = mArtistTracksList.get(mSelectedPosition);
                mApp.getService().Add(so, 1);
                break;
            case PLAY_THIS:
                mApp.getService().setSongList(mArtistTracksList);
                mApp.getService().setSelectedSong(mSelectedPosition, MusicService.NOTIFICATION_ID);
                startActivity(new Intent(this, NowPlaying.class));
                break;
            case SHARE_ITEM:
                MusicUtils.shareTheMusic(mArtistTracksList, mSelectedPosition, mContext);
                break;
            case ADD_TO_FAVORITES:
                mApp.getDBAccessHelper().addTOFavorites(Integer.parseInt(mArtistTracksList.get(mSelectedPosition).get(SONG_ID)));
                break;
            default:

                break;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mApp.getService().setSongList(mArtistTracksList);
        mApp.getService().setSelectedSong(position, MusicService.NOTIFICATION_ID);
        startActivity(new Intent(this, NowPlaying.class));

    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case MusicUtils.URI_REQUEST_CODE:
                Uri treeUri = null;
                if (resultCode == Activity.RESULT_OK) treeUri = data.getData();
                mApp.getPreferencesUtility().setSharedPreferenceUri(treeUri.toString());
                File file = new File(mArtistTracksList.get(mSelectedPosition).get(SONG_PATH));
                mContext.getContentResolver().takePersistableUriPermission(treeUri, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                new AsyncDeleteTask(mContext, file).execute();
                mArtistTracksList.remove(mSelectedPosition);
                mAdapter.setSongsList(mArtistTracksList);
                mAdapter.notifyDataSetChanged();
                break;
        }
    }
}
