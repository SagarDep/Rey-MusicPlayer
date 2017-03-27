/*
package com.reyansh.audio.audioplayer.free.Songs;

import android.app.Activity;
import android.graphics.Color;
import android.support.v4.graphics.ColorUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.reyansh.audio.audioplayer.free.R;
import com.reyansh.audio.audioplayer.free.Task.MusicUtils;
import com.reyansh.audio.audioplayer.free.Task.PreferencesUtility;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;


public class SongAdapter extends ArrayAdapter<HashMap<String, String>> implements MusicUtils.names, Filterable {
    private ArrayList<HashMap<String, String>> filteredData;
    private ArrayList<HashMap<String, String>> originalData;
    private Filter mFilter;
    private Activity mContext;

    public SongAdapter(Activity context, int textViewResourceId) {
        super(context, textViewResourceId);
        filteredData = originalData;
        this.mContext = context;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        AlbumViewHolder viewHolder;
        final HashMap<String, String> a;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.song_layout_demo, null);
            viewHolder = new AlbumViewHolder();
            viewHolder.songName = (TextView) convertView.findViewById(R.id.song_name);
            viewHolder.artist = (TextView) convertView.findViewById(R.id.artist_name);
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.songimage);
            viewHolder.song_layout = (RelativeLayout) convertView.findViewById(R.id.song_layout);
            viewHolder.songDuration = (TextView) convertView.findViewById(R.id.list_songduration);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (AlbumViewHolder) convertView.getTag();
        }
        a = getItem(position);
        viewHolder.songName.setText(a.get(SONG_NAME));
        viewHolder.artist.setText(a.get(SONG_ARTIST));

    */
/*   LastFmClient.getInstance(mContext).getAlbumInfo(new AlbumQuery(a.get(SONG_ALBUM),
                a.get(SONG_ARTIST)), new AlbuminfoListener() {
            @Override
            public void albumInfoSucess(LastfmAlbum album) {
                Log.d("lastfm", album.mArtwork.get(2).mUrl);
                    //loadArt(album.mArtwork.get(2).mUrl, a.get(SONG_PATH));
            }
            @Override
            public void albumInfoFailed() {

            }
        });*//*

        ImageLoader.getInstance().displayImage(MusicUtils.getAlbumArtUri(Long.parseLong(a.get(SONG_ALBUM_ID))).toString(),
                viewHolder.imageView,
                new DisplayImageOptions.Builder().cacheInMemory(true)
                        .showImageOnFail(R.drawable.default_art)
                        .showImageOnLoading(R.drawable.default_art)
                        .resetViewBeforeLoading(true)
                        .build());
        try {
            viewHolder.songDuration.setText(MusicUtils.makeShortTimeString(mContext, (Integer.parseInt(a.get(SONG_DURATION)) / 1000)));
        } catch (NumberFormatException e) {
            Log.d("NumberFormata", "" + e);
        }
        if (PreferencesUtility.getInstance(getContext()).getStripviewCheck()) {
            if (position % 2 == 0) {
                viewHolder.song_layout.setBackgroundColor(ColorUtils.setAlphaComponent(Color.parseColor("#ffffff"), 21));
            } else {
                viewHolder.song_layout.setBackgroundColor(ColorUtils.setAlphaComponent(Color.parseColor("#F19001"), 15));
            }
        }

        return convertView;
    }

    public void resetData() {
        filteredData = originalData;
    }
*/
/*
    public void loadArt(String url, String mp3filepath) throws ID3WriteException, IOException {
        File path = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File src = new File(path, "Lovers Cry.mp3");
        MusicMetadataSet src_set = new MyID3().read(src);
        File img = (new File(path, "rey.jpg"));
        Log.d("lastfm", String.valueOf(img));
        Vector<ImageData> fileList = new Vector<ImageData>();
        ImageData data = new ImageData(readFile(img), "", "", 3);
        fileList.add(data);

        MusicMetadata metadata = (MusicMetadata) src_set.getSimplified();
        metadata.setPictureList(fileList);
        File dst = new File(path, "Lovers Cry.mp3");
        new MyID3().write(src, dst, src_set, metadata);
    }*//*


    public static byte[] readFile(File file) throws IOException {
        // Open file
        RandomAccessFile f = new RandomAccessFile(file, "r");
        try {
            // Get and check length
            long longlength = f.length();
            int length = (int) longlength;
            if (length != longlength) throw new IOException("File size >= 2 GB");
            // Read file and return data
            byte[] data = new byte[length];
            f.readFully(data);
            return data;
        } finally {
            f.close();
        }
    }

    static class AlbumViewHolder {
        TextView songName;
        TextView artist;
        ImageView imageView;
        RelativeLayout song_layout;
        TextView songDuration;
    }

    public void setData(ArrayList<HashMap<String, String>> arg1) {
        clear();
        originalData = arg1;
        if (arg1 != null) {
            for (int i = 0; i < arg1.size(); i++) {
                add(arg1.get(i));
                notifyDataSetChanged();
            }
        }
    }

    public void setFilteredData(ArrayList<HashMap<String, String>> arg1) {
        clear();
        filteredData = arg1;
        if (filteredData != null) {
            for (int i = 0; i < filteredData.size(); i++) {
                add(filteredData.get(i));
                notifyDataSetChanged();
            }
        }
    }


    */
/*  public static String getDuration(long millis) {
          if (millis < 0) {
              throw new IllegalArgumentException("Duration must be greater than zero!");
          }
          long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
          millis -= TimeUnit.MINUTES.toMillis(minutes);
          long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);

          StringBuilder sb = new StringBuilder(6);
          sb.append(minutes < 10 ? "" + minutes : minutes);
          sb.append(":");
          sb.append(seconds < 10 ? "" + seconds : seconds);
          return sb.toString();
      }
  *//*

    @Override
    public Filter getFilter() {
        Log.d("ok", "getFilter");
        if (mFilter == null)
            mFilter = new ItemFilter();
        return mFilter;
    }

    public ArrayList<HashMap<String, String>> getFilteredData() {
        return filteredData;
    }

    private class ItemFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            String filterString = constraint.toString().toLowerCase();
            FilterResults results = new FilterResults();
            final ArrayList<HashMap<String, String>> nlist = new ArrayList<HashMap<String, String>>();
            if (constraint == null || constraint.length() == 0) {
                results.values = originalData;
                results.count = originalData.size();
            } else {
                Log.d("pok", "perform");
                HashMap<String, String> filteredHashmap = new HashMap<String, String>();
                for (int i = 0; i < originalData.size(); i++) {
                    String s1 = originalData.get(i).get(SONG_NAME);
                    String s2 = originalData.get(i).get(SONG_ARTIST);
                    String s3 = originalData.get(i).get(SONG_ALBUM);

                    filteredHashmap = originalData.get(i);
                    if ((s1.toLowerCase().contains(filterString) ||
                            s2.toLowerCase().contains(filterString) ||
                            s3.toLowerCase().contains(filterString))) {
                        nlist.add(filteredHashmap);
                    }
                }
                results.values = nlist;
                results.count = nlist.size();
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results.count == 0) {
                notifyDataSetInvalidated();
            } else {
                filteredData = (ArrayList<HashMap<String, String>>) results.values;
                setFilteredData(filteredData);
                notifyDataSetChanged();
            }
        }
    }
}

*/
