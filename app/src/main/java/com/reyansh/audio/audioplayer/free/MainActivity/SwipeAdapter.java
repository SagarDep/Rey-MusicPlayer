package com.reyansh.audio.audioplayer.free.MainActivity;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.reyansh.audio.audioplayer.free.Album.FragmentAlbum;
import com.reyansh.audio.audioplayer.free.Artist.FragmentArtist;
import com.reyansh.audio.audioplayer.free.FileDirectory.FragmentFolder;
import com.reyansh.audio.audioplayer.free.Genres.FragmentGenres;
import com.reyansh.audio.audioplayer.free.PlayList.FragmentPlaylist;
import com.reyansh.audio.audioplayer.free.R;
import com.reyansh.audio.audioplayer.free.Songs.FragmentSongs;

import java.util.HashMap;
import java.util.Map;

class SwipeAdapter extends FragmentPagerAdapter {
    private Map<Integer, String> mFragmentTags;
    private FragmentManager mFragmentManager;

    private String mPagetile[];

    public SwipeAdapter(FragmentManager fm, Context context) {
        super(fm);
        mPagetile = context.getResources().getStringArray(R.array.pageTitle);
        mFragmentManager = fm;
        mFragmentTags = new HashMap<Integer, String>();
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new FragmentAlbum();
            case 1:
                return new FragmentArtist();
            case 2:
                return new FragmentSongs();
            case 3:
                return new FragmentGenres();
            case 4:
                return new FragmentPlaylist();
            case 5:
                return new FragmentFolder();
            default:
                break;
        }
        return null;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Object obj = super.instantiateItem(container, position);
        if (obj instanceof Fragment) {
            Fragment f = (Fragment) obj;
            String tag = f.getTag();
            mFragmentTags.put(position, tag);
        }
        return obj;
    }

    public Fragment getFragment(int position) {
        String tag = mFragmentTags.get(position);
        if (tag == null)
            return null;
        return mFragmentManager.findFragmentByTag(tag);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mPagetile[position];
    }

    @Override
    public int getCount() {
        return 6;
    }
}