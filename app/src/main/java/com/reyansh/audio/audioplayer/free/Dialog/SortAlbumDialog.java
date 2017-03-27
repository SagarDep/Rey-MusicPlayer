package com.reyansh.audio.audioplayer.free.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.reyansh.audio.audioplayer.free.Common.CommonClass;
import com.reyansh.audio.audioplayer.free.R;
import com.reyansh.audio.audioplayer.free.Utils.PreferencesUtility;
import com.reyansh.audio.audioplayer.free.Utils.SortOrder;

/**
 * Created by REYANSH on 31/07/2016.
 */
public class SortAlbumDialog extends Dialog implements RadioGroup.OnCheckedChangeListener {
    public Dialog d;
    private PreferencesUtility mPreferences;
    RadioGroup radioGroup;
    CheckBox ignoreaandthe;
    Button ascending, descending;
CommonClass mApp;
    public SortAlbumDialog(Context context) {
        super(context);
        mApp=(CommonClass)context.getApplicationContext();
    }

    LinearLayout dailogbg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dailog_sort);
        radioGroup = (RadioGroup) findViewById(R.id.sortradiogroup);
        ignoreaandthe = (CheckBox) findViewById(R.id.theanda);
        dailogbg = (LinearLayout) findViewById(R.id.dailogbg);
        ascending = (Button) findViewById(R.id.ascending);
        descending = (Button) findViewById(R.id.descending);
        ascending.setOnClickListener(ascending1);
        ascending.setTypeface(mApp.getStripTitleTypeFace());
        descending.setTypeface(mApp.getStripTitleTypeFace());
        descending.setOnClickListener(descending1);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        getWindow().getAttributes().windowAnimations = R.style.DialogAnim;

        mPreferences = PreferencesUtility.getInstance(getContext());
        ((RadioButton) radioGroup.getChildAt(0)).setText("Album");
        ((RadioButton) radioGroup.getChildAt(1)).setText("Year");
        ((RadioButton) radioGroup.getChildAt(2)).setText("Artist");
        ((RadioButton) radioGroup.getChildAt(3)).setText("Number of songs");
        (radioGroup.getChildAt(4)).setVisibility(View.GONE);
        (radioGroup.getChildAt(5)).setVisibility(View.GONE);
    }

    View.OnClickListener ascending1 = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int checkedId = radioGroup.getCheckedRadioButtonId();
            switch (checkedId) {
                case R.id.sortbytitle:
                    mPreferences.setAlbumSortOrder(SortOrder.AlbumSortOrder.ALBUM_A_Z);
                    break;
                case R.id.sortbyartist:
                    mPreferences.setAlbumSortOrder(SortOrder.AlbumSortOrder.ALBUM_YEAR_ASC);
                    break;
                case R.id.sortbyalbum:
                    mPreferences.setAlbumSortOrder(SortOrder.AlbumSortOrder.ALBUM_ARTIST_ASC);
                case R.id.sortbyduration:
                    mPreferences.setAlbumSortOrder(SortOrder.AlbumSortOrder.ALBUM_NUMBER_OF_SONGS_ASC);
                default:
                    break;
            }
            dismiss();
        }
    };
    View.OnClickListener descending1 = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int checkedId = radioGroup.getCheckedRadioButtonId();
            switch (checkedId) {
                case R.id.sortbytitle:
                    mPreferences.setAlbumSortOrder(SortOrder.AlbumSortOrder.ALBUM_Z_A);
                    break;
                case R.id.sortbyartist:
                    mPreferences.setAlbumSortOrder(SortOrder.AlbumSortOrder.ALBUM_YEAR_DESC);
                    break;
                case R.id.sortbyalbum:
                    mPreferences.setAlbumSortOrder(SortOrder.AlbumSortOrder.ALBUM_ARTIST_DESC);
                case R.id.sortbyduration:
                    mPreferences.setAlbumSortOrder(SortOrder.AlbumSortOrder.ALBUM_NUMBER_OF_SONGS_DESC);
                default:
                    break;
            }
            dismiss();
        }
    };

    public void check() {
        String s = PreferencesUtility.getInstance(getContext()).getSongSortOrder();
        if (s == SortOrder.SongSortOrder.SONG_A_Z) {
            radioGroup.check(R.id.sortbytitle);
        } else if (s == SortOrder.SongSortOrder.SONG_ARTIST) {
            radioGroup.check(R.id.sortbyartist);
        } else if (s == SortOrder.SongSortOrder.SONG_ALBUM) {
            radioGroup.check(R.id.sortbyalbum);
        } else if (s == SortOrder.SongSortOrder.SONG_DURATION) {
            radioGroup.check(R.id.sortbyduration);
        } else if (s == SortOrder.SongSortOrder.SONG_DATE) {
            radioGroup.check(R.id.sortbydateadded);
        } else if (s == SortOrder.SongSortOrder.SONG_YEAR) {
            radioGroup.check(R.id.sortbyyear);
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.sortbytitle:
             //  mPreferences.setAlbumSortOrder(SortOrder.AlbumSortOrder.ALBUM_A_Z);
                break;
            case R.id.sortbyartist:
            //   mPreferences.setAlbumSortOrder(SortOrder.AlbumSortOrder.ALBUM_NUMBER_OF_SONGS_ASC);
                break;
            case R.id.sortbyalbum:
           //   mPreferences.setAlbumSortOrder(SortOrder.AlbumSortOrder.ALBUM_ARTIST_DESC);
            default:
                break;
        }
    }
}