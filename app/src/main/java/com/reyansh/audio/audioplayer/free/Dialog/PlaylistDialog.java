package com.reyansh.audio.audioplayer.free.Dialog;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.reyansh.audio.audioplayer.free.Common.CommonClass;
import com.reyansh.audio.audioplayer.free.R;
import com.reyansh.audio.audioplayer.free.Utils.MusicUtils;

import java.util.ArrayList;
import java.util.HashMap;

public class PlaylistDialog extends Dialog implements MusicUtils.names {
    private EditText mPlaylist;
    private TextView mPrompt;
    long[] mSelectedId;
    TextView title;
    ArrayList<HashMap<String, String>> data;
    private Context mContext;
    CommonClass mApp;

    public PlaylistDialog(Context context, long[] mSelectedId) {
        super(context);
        this.mSelectedId = mSelectedId;
        mContext = context;
        mApp = (CommonClass) mContext.getApplicationContext();
    }

    public PlaylistDialog(Context context, ArrayList<HashMap<String, String>> data) {
        super(context);
        this.data = data;
        mContext = context;
    }

    private Button mSaveButton;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.create_playlist);
        mPrompt = (TextView) findViewById(R.id.prompt);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        getWindow().getAttributes().windowAnimations = R.style.DialogAnim;
        mPlaylist = (EditText) findViewById(R.id.playlist);
        mPlaylist.addTextChangedListener(mTextWatcher);
        mSaveButton = (Button) findViewById(R.id.create);
        title = (TextView) findViewById(R.id.titles);
        title.setText("Create Playlist");
        mSaveButton.setOnClickListener(mOpenClicked);
        ((Button) findViewById(R.id.cancel)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dismiss();
            }
        });

    }

    private int idForplaylist(String name) {
        Cursor c = getContext().getContentResolver().query(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
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
            c.close();
        }
        return id;
    }

    TextWatcher mTextWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String newText = mPlaylist.getText().toString();
            if (newText.trim().length() == 0) {
                mSaveButton.setEnabled(false);
            } else {
                mSaveButton.setEnabled(true);
                if (idForplaylist(newText) >= 0) {
                    mSaveButton.setText(R.string.create_playlist_overwrite_text);
                } else {
                    mSaveButton.setText(R.string.create_playlist_create_text);
                }
            }
        }

        public void afterTextChanged(Editable s) {
        }
    };

    public static void cleatePlaylist(Context context, int plid) {
        Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", plid);
        context.getContentResolver().delete(uri, null, null);
        return;
    }

    private View.OnClickListener mOpenClicked = new View.OnClickListener() {
        public void onClick(View v) {
            String name = mPlaylist.getText().toString();
            if (name != null && name.length() > 0) {
                ContentResolver resolver = getContext().getContentResolver();
                int id = idForplaylist(name);
                final Uri uri;
                if (id >= 0) {
                    uri = ContentUris.withAppendedId(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, id);
                    cleatePlaylist(getContext(), id);
                } else {
                    ContentValues values = new ContentValues(1);
                    values.put(MediaStore.Audio.Playlists.NAME, name);
                    uri = resolver.insert(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, values);
                }
                if (mSelectedId != null) {
                    MusicUtils.addToPlaylist(getContext(), mSelectedId, Integer.valueOf(uri.getLastPathSegment()));
                    Toast.makeText(mContext, MusicUtils.makeLabel(mContext, R.plurals.Nsongs, mSelectedId.length) + " Added to playlist", Toast.LENGTH_SHORT).show();
                    dismiss();
                } else {
                    dismiss();
                    new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... params) {
                            for (int i = 0; i < data.size(); i++) {
                                long[] list = new long[]{Long.valueOf(data.get(i).get(SONG_ID))};
                                MusicUtils.addToPlaylist(getContext(), list, Integer.valueOf(uri.getLastPathSegment()));
                            }
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void aVoid) {
                            super.onPostExecute(aVoid);
                            Toast.makeText(mContext, MusicUtils.makeLabel(mContext, R.plurals.Nsongs, data.size()) + " Added to playlist", Toast.LENGTH_SHORT).show();
                        }
                    }.execute();
                }
            }
        }
    };
}
