
package com.reyansh.audio.audioplayer.free.PlayList.PlaylistTracks;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.media.AudioManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.reyansh.audio.audioplayer.free.R;
import com.reyansh.audio.audioplayer.free.Utils.MusicUtils;

public class RenamePlaylist extends Activity {
    private EditText mPlaylist;
    private TextView mPrompt;
    private Button mSaveButton;
    private long mRenameId;
    private String mOriginalName;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        Bundle extras = getIntent().getExtras();
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.create_playlist);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);

        mPrompt = (TextView) findViewById(R.id.prompt);
        mPlaylist = (EditText) findViewById(R.id.playlist);
        mSaveButton = (Button) findViewById(R.id.create);
        mSaveButton.setOnClickListener(mOpenClicked);

        ((Button) findViewById(R.id.cancel)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        mRenameId = icicle != null ? icicle.getLong("rename") : Long.parseLong(getIntent().getStringExtra("rename"));
        mOriginalName = nameForId(mRenameId);
        String defaultname = icicle != null ? icicle.getString("defaultname") : mOriginalName;
        if (mRenameId < 0 || mOriginalName == null || defaultname == null) {
            Log.i("@@@@", "Rename failed: " + mRenameId + "/" + defaultname);
            Toast.makeText(getApplicationContext(), R.string.playlistcant_rename_message, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        String promptformat;
        if (mOriginalName.equals(defaultname)) {
            promptformat = getString(R.string.rename_playlist_diff_prompt);
        } else {
            promptformat = getString(R.string.rename_playlist_diff_prompt);
        }
        String prompt = String.format(promptformat, mOriginalName, defaultname);
        mPrompt.setText(prompt);
        mPlaylist.setText(defaultname);
        mPlaylist.setSelection(defaultname.length());
        mPlaylist.addTextChangedListener(mTextWatcher);
        setSaveButton();
    }

    TextWatcher mTextWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // check if playlist with current name exists already, and warn the user if so.
            setSaveButton();
        }

        ;

        public void afterTextChanged(Editable s) {

        }
    };

    private void setSaveButton() {
        String typedname = mPlaylist.getText().toString();
        if (typedname.trim().length() == 0) {
            mSaveButton.setEnabled(false);
        } else {
            mSaveButton.setEnabled(true);
            if (idForplaylist(typedname) >= 0
                    && !mOriginalName.equals(typedname)) {
                mSaveButton.setText(R.string.create_playlist_overwrite_text);
            } else {
                mSaveButton.setText(R.string.create_playlist_create_text);
            }
        }

    }

    private int idForplaylist(String name) {
        Cursor c = MusicUtils.query(this, MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Audio.Playlists._ID},
                MediaStore.Audio.Playlists.NAME + "=?",
                new String[]{name},
                MediaStore.Audio.Playlists.NAME);
        int id = -1;
        if (c != null) {
            c.moveToFirst();
            if (!c.isAfterLast()) {
                id = c.getInt(0);
            }
        }
        c.close();
        return id;
    }

    private String nameForId(long id) {
        Cursor c = MusicUtils.query(this, MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Audio.Playlists.NAME},
                MediaStore.Audio.Playlists._ID + "=?",
                new String[]{Long.valueOf(id).toString()},
                MediaStore.Audio.Playlists.NAME);
        String name = null;
        if (c != null) {
            c.moveToFirst();
            if (!c.isAfterLast()) {
                name = c.getString(0);
            }
        }
        c.close();
        return name;
    }


    @Override
    public void onSaveInstanceState(Bundle outcicle) {
        outcicle.putString("defaultname", mPlaylist.getText().toString());
        outcicle.putLong("rename", mRenameId);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private View.OnClickListener mOpenClicked = new View.OnClickListener() {
        public void onClick(View v) {
            String name = mPlaylist.getText().toString();
            if (name != null && name.length() > 0) {
                ContentResolver resolver = getContentResolver();
                ContentValues values = new ContentValues(1);
                values.put(MediaStore.Audio.Playlists.NAME, name);
                resolver.update(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
                        values,
                        MediaStore.Audio.Playlists._ID + "=?",
                        new String[]{Long.valueOf(mRenameId).toString()});

                setResult(RESULT_OK);
                Toast.makeText(RenamePlaylist.this, R.string.playlist_renamed_message, Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    };
}