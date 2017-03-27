package com.reyansh.audio.audioplayer.free.Album.AlbumTracks;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.reyansh.audio.audioplayer.free.R;
import com.reyansh.audio.audioplayer.free.Songs.AdapterSongs;
import com.reyansh.audio.audioplayer.free.Utils.MetaRetriever;
import com.reyansh.audio.audioplayer.free.Utils.MusicUtils;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;


public class AlbumParallexActivity extends AppCompatActivity implements MusicUtils.Defs, MusicUtils.names {
    private RecyclerView recyclerView;
    private ImageView mCoordinatorLayout;
    private String mCoverPath;
    private ArrayList<HashMap<String, String>> mAlbumTracksList;
    private Toolbar mToolbar;
    private TextView mAlbumName;
    private TextView mArtistName;
    private TextView mNoofTracks;
    private ImageView mAlbumArt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_details);

//        mAlbumName = (TextView) findViewById(R.id.text_view_album_name);
//        mAlbumName.setText(getIntent().getExtras().getString("AlbumNameForTracks"));
//
//        mArtistName = (TextView) findViewById(R.id.text_view_artist_name);
//        mArtistName.setText("By:" + getIntent().getExtras().getString("ArtistNameForAlbum"));
//
//        mNoofTracks = (TextView) findViewById(R.id.text_view_no_of_tracks);
//        mNoofTracks.setText("Total Tracks:" + getIntent().getExtras().getString("NumberOfSongs"));
//        mCoverPath = getIntent().getExtras().getString("AlbumCoverPath");
//
//        mAlbumArt = (ImageView) findViewById(R.id.album_art);
//
//        mAlbumTracksList = loadAlbumTracks();
//        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
//        mCoordinatorLayout = (ImageView) findViewById(R.id.mainbg);
//        recyclerView.setLayoutManager(new LinearLayoutManager(getApplication()));
//        recyclerView.setAdapter(new AdapterSongs(getApplication(), mAlbumTracksList, null));
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
       // background();
    }
    


    private void background() {
        if (mCoverPath != null) {
            try {
                Bitmap artwork = ImageLoader.getInstance().loadImageSync(MusicUtils.getAlbumArtUri(Long.parseLong(mAlbumTracksList.get(0)
                        .get(SONG_ALBUM_ID))).toString());
                mAlbumArt.setImageBitmap(artwork);
                mCoordinatorLayout.setImageBitmap(((BitmapDrawable) MetaRetriever.createBlurredImageFromBitmap(artwork, getApplication())).getBitmap());
            } catch (Exception e) {
                nopath();
            }
        } else {
            nopath();
        }
    }

    void nopath() {
        Bitmap ok = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.album_art_7);
        mCoordinatorLayout.setBackground(MetaRetriever.createBlurredImageFromBitmap(ok, getApplication()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public ArrayList<HashMap<String, String>> loadAlbumTracks() {
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
        String whereVal[] = {getIntent().getExtras().getString("AlbumNameForTracks")};
        Cursor mCursor = this.getContentResolver().query(
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
        }
        return mArtistList;
    }
}