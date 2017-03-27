package com.reyansh.audio.audioplayer.free.Dialog;

/**
 * Created by REYANSH on 31/07/2016.
 */

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
 * Created by REYANSH on 04/02/2016.
 */
public class SortArtistDialog extends Dialog implements RadioGroup.OnCheckedChangeListener {
    public Dialog d;
    private PreferencesUtility mPreferences;
    RadioGroup radioGroup;
    CheckBox ignoreaandthe;
    Button ascending, descending;
    CommonClass mApp;

    public SortArtistDialog(Context context) {
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
        descending.setOnClickListener(descending1);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        getWindow().getAttributes().windowAnimations = R.style.DialogAnim;

        mPreferences = PreferencesUtility.getInstance(getContext());
        ((RadioButton) radioGroup.getChildAt(0)).setText("Artist");
        ((RadioButton) radioGroup.getChildAt(1)).setText("Number of Songs");
        ((RadioButton) radioGroup.getChildAt(2)).setText("Number of Albums");
        (radioGroup.getChildAt(3)).setVisibility(View.GONE);
        (radioGroup.getChildAt(4)).setVisibility(View.GONE);
        (radioGroup.getChildAt(5)).setVisibility(View.GONE);
        descending.setTypeface(mApp.getStripTitleTypeFace());
        ascending.setTypeface(mApp.getStripTitleTypeFace());
    }

    View.OnClickListener ascending1 = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int checkedId = radioGroup.getCheckedRadioButtonId();
            switch (checkedId) {
                case R.id.sortbytitle:
                    mPreferences.setArtistSortOrder(SortOrder.ArtistSortOrder.ARTIST_A_Z);
                    break;
                case R.id.sortbyartist:
                    mPreferences.setArtistSortOrder(SortOrder.ArtistSortOrder.ARTIST_NUMBER_OF_SONGS_ASC);
                    break;
                case R.id.sortbyalbum:
                    mPreferences.setArtistSortOrder(SortOrder.ArtistSortOrder.ARTIST_NUMBER_OF_ALBUMS_ASC);
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
                    mPreferences.setArtistSortOrder(SortOrder.ArtistSortOrder.ARTIST_Z_A);
                    break;
                case R.id.sortbyartist:
                    mPreferences.setArtistSortOrder(SortOrder.ArtistSortOrder.ARTIST_NUMBER_OF_SONGS);
                    break;
                case R.id.sortbyalbum:
                    mPreferences.setArtistSortOrder(SortOrder.ArtistSortOrder.ARTIST_NUMBER_OF_ALBUMS);
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
                mPreferences.setSongSortOrder(SortOrder.ArtistSortOrder.ARTIST_A_Z);
                break;
            case R.id.sortbyartist:
                mPreferences.setSongSortOrder(SortOrder.ArtistSortOrder.ARTIST_NUMBER_OF_SONGS);
                break;
            case R.id.sortbyalbum:
                mPreferences.setSongSortOrder(SortOrder.ArtistSortOrder.ARTIST_NUMBER_OF_ALBUMS);
            default:
                break;
        }
    }
}