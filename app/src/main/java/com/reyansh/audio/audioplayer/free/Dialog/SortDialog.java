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
import android.widget.RadioGroup;

import com.reyansh.audio.audioplayer.free.Common.CommonClass;
import com.reyansh.audio.audioplayer.free.R;
import com.reyansh.audio.audioplayer.free.Utils.PreferencesUtility;
import com.reyansh.audio.audioplayer.free.Utils.SortOrder;

/**
 * Created by REYANSH on 04/02/2016.
 */
public class SortDialog extends Dialog implements RadioGroup.OnCheckedChangeListener {
    public Dialog d;
    private PreferencesUtility mPreferences;
    RadioGroup radioGroup;
    CheckBox ignoreaandthe;
    Button ascending, descending;

    public SortDialog(Context context) {
        super(context);
        mApp=(CommonClass)context.getApplicationContext();
    }

    LinearLayout dailogbg;
    CommonClass mApp;

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
        radioGroup.setOnCheckedChangeListener(this);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        getWindow().getAttributes().windowAnimations = R.style.DialogAnim;

        mPreferences = PreferencesUtility.getInstance(getContext());
        check();
        descending.setTypeface(mApp.getStripTitleTypeFace());
        ascending.setTypeface(mApp.getStripTitleTypeFace());
    }

    View.OnClickListener ascending1 = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int checkedId = radioGroup.getCheckedRadioButtonId();
            switch (checkedId) {
                case R.id.sortbytitle:
                    mPreferences.setSongSortOrder(SortOrder.SongSortOrder.SONG_A_Z);
                    break;
                case R.id.sortbyartist:
                    mPreferences.setSongSortOrder(SortOrder.SongSortOrder.SONG_ARTIST);
                    break;
                case R.id.sortbyalbum:
                    mPreferences.setSongSortOrder(SortOrder.SongSortOrder.SONG_ALBUM);
                    break;
                case R.id.sortbyduration:
                    mPreferences.setSongSortOrder(SortOrder.SongSortOrder.SONG_DURATION + " ASC");
                    break;
                case R.id.sortbydateadded:
                    mPreferences.setSongSortOrder(SortOrder.SongSortOrder.SONG_DATE + " ASC");
                    break;
                case R.id.sortbyyear:
                    mPreferences.setSongSortOrder(SortOrder.SongSortOrder.SONG_YEAR + " ASC");
                    break;
                case R.id.sortbyfilename:
                    mPreferences.setSongSortOrder(SortOrder.SongSortOrder.SONG_FILENAME+ " ASC");
                    break;
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
                    mPreferences.setSongSortOrder(SortOrder.SongSortOrder.SONG_Z_A);
                    break;
                case R.id.sortbyartist:
                    mPreferences.setSongSortOrder(SortOrder.SongSortOrder.SONG_ARTIST + " DESC");
                    break;
                case R.id.sortbyalbum:
                    mPreferences.setSongSortOrder(SortOrder.SongSortOrder.SONG_ALBUM + " DESC");
                    break;
                case R.id.sortbyduration:
                    mPreferences.setSongSortOrder(SortOrder.SongSortOrder.SONG_DURATION + " DESC");
                    break;
                case R.id.sortbydateadded:
                    mPreferences.setSongSortOrder(SortOrder.SongSortOrder.SONG_DATE+ " DESC");
                    break;
                case R.id.sortbyyear:
                    mPreferences.setSongSortOrder(SortOrder.SongSortOrder.SONG_YEAR + " DESC");
                    break;
                case R.id.sortbyfilename:
                    mPreferences.setSongSortOrder(SortOrder.SongSortOrder.SONG_FILENAME+" DESC");
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
                mPreferences.setSongSortOrder(SortOrder.SongSortOrder.SONG_A_Z);
                break;
            case R.id.sortbyartist:
                mPreferences.setSongSortOrder(SortOrder.SongSortOrder.SONG_ARTIST);
                break;
            case R.id.sortbyalbum:
                mPreferences.setSongSortOrder(SortOrder.SongSortOrder.SONG_ALBUM);
                break;
            case R.id.sortbyduration:
                mPreferences.setSongSortOrder(SortOrder.SongSortOrder.SONG_DURATION);
                break;
            case R.id.sortbydateadded:
                mPreferences.setSongSortOrder(SortOrder.SongSortOrder.SONG_DATE);
                break;
            case R.id.sortbyfilename:
                mPreferences.setSongSortOrder(SortOrder.SongSortOrder.SONG_FILENAME);
                break;
            default:
                break;
        }
    }
}