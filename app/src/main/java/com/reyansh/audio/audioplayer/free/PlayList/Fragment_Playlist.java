/*
package com.reyansh.audio.audioplayer.free.PlayList;

import android.app.Dialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.support.v4.app.ListFragment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.reyansh.audio.audioplayer.free.Common.CommonClass;
import com.reyansh.audio.audioplayer.free.Dialog.RecentlyAddedDialog;
import com.reyansh.audio.audioplayer.free.Dialog.RenameDialog;
import com.reyansh.audio.audioplayer.free.MusicService.MusicService;
import com.reyansh.audio.audioplayer.free.PlayList.PlaylistTracks.PlaylistTracksActivity;
import com.reyansh.audio.audioplayer.free.R;
import com.reyansh.audio.audioplayer.free.Utils.MusicUtils;

import java.util.ArrayList;
import java.util.HashMap;

public class Fragment_Playlist extends ListFragment implements
        AdapterView.OnItemClickListener, MusicUtils.Defs {
    private ListView lvAlbumList;
    Cursor cursor;
    ArrayList<HashMap<String, String>> arg1;
    private View view;
    private PlaylistAdapter albumAdapter;
    private ArrayList<HashMap<String, String>> playtList;
    Context mContext;
    CommonClass commonClass;

    public Fragment_Playlist() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_playlist_layout, container, false);
        lvAlbumList = (ListView) view.findViewById(android.R.id.list);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = new View(getActivity());
        mContext = getContext();
        new PlaylistLoaders().execute();
        albumAdapter = new PlaylistAdapter(getActivity(), android.R.id.list);
        lvAlbumList.setOnItemClickListener(this);
        lvAlbumList.setOnCreateContextMenuListener(this);
        lvAlbumList.setDividerHeight(0);
        lvAlbumList.setAdapter(albumAdapter);
        commonClass = (CommonClass) mContext.getApplicationContext();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Bundle bundle = new Bundle();
        bundle.putString("PlayListId", playtList.get(position).get("playListId"));
        bundle.putString("PlayListName", playtList.get(position).get("playListName"));
        Intent in = new Intent(getContext(), PlaylistTracksActivity.class);
        in.putExtra("PlayListName", playtList.get(position).get("playListName"));
        in.putExtra("PlayListId", playtList.get(position).get("playListId"));
        startActivity(in);
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if (menuVisible) {
            new PlaylistLoaders().execute();
            albumAdapter.setData(arg1);
            albumAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfoIn) {
        AdapterView.AdapterContextMenuInfo mi = (AdapterView.AdapterContextMenuInfo) menuInfoIn;
        if (mi.id != 0 && mi.id != 1 && mi.id != 2 && mi.id != 3) {
            menu.add(0, PLAY, 0, R.string.playit);
            menu.add(0, RENAME_PLAYLIST, 0, R.string.rename_playlist);
            menu.add(0, DELETE_PLAYLIST, 0, R.string.delete_playlist);
        } else if (mi.id == 1) {
            menu.add(0, CLEAR_FAVORITES, 0, R.string.clearfavorites);
        } else if (mi.id == 2) {
            menu.add(0, CLEAR_TOPTRACKS, 0, R.string.cleartoptracks);
        } else if (mi.id == 3) {
            menu.add(0, CLEAR_RECENTLY_PLAYED, 0, R.string.clearrecentlyplayed);
        } else {
            menu.add(0, RECENTLY_ADDED_WEEK, 0, R.string.editweek);
        }

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo mi = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case PLAY:
                commonClass.getService().setSongList(MusicUtils.playPlaylist(getContext(), Long.parseLong(arg1.get((int) mi.id).get("playListId"))));
                commonClass.getService().setSelectedSong(0, MusicService.NOTIFICATION_ID);
                break;
            case RENAME_PLAYLIST:
                RenameDialog d = new RenameDialog(mContext, Long.parseLong(arg1.get((int) mi.id).get("playListId")));
                d.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(final DialogInterface arg0) {
                        setMenuVisibility(true);
                    }
                });
                d.show();
                WindowManager.LayoutParams lp = d.getWindow().getAttributes();
                lp.dimAmount = 0.5f;
                d.getWindow().setAttributes(lp);
                d.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
                break;
            case DELETE_PLAYLIST:
                if (mi.id != 0) {
                    Uri uri = ContentUris.withAppendedId(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, Long.parseLong(arg1.get((int) mi.id).get("playListId")));
                    mContext.getContentResolver().delete(uri, null, null);
                    Toast.makeText(mContext, R.string.playlist_deleted_message, Toast.LENGTH_SHORT).show();
                    arg1.remove(mi.position);
                    albumAdapter.setData(arg1);
                    albumAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(mContext, R.string.Auto_playlist_deleted_message, Toast.LENGTH_SHORT).show();
                }
                break;
            case RECENTLY_ADDED_WEEK:
                RecentlyAddedDialog f = new RecentlyAddedDialog(mContext);
                f.show();
                WindowManager.LayoutParams lpa = f.getWindow().getAttributes();
                lpa.dimAmount = 0.5f;
                f.getWindow().setAttributes(lpa);
                f.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
                break;
            case CLEAR_FAVORITES:
                final Dialog da = new Dialog(getActivity());
                da.requestWindowFeature(Window.FEATURE_NO_TITLE);
                da.setContentView(R.layout.unfavorites_dialog);
                ((TextView) da.findViewById(R.id.playlist)).setText(R.string.unfavorite_long);
                da.getWindow().getAttributes().windowAnimations = R.style.DialogAnim;
                da.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                da.show();
                WindowManager.LayoutParams lap = da.getWindow().getAttributes();
                lap .dimAmount = 0.5f;
                da.getWindow().setAttributes(lap);
                da.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
                ((Button) da.findViewById(R.id.create)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        commonClass.getDBAccessHelper().clearFavorites();
                        Toast.makeText(mContext, R.string.favoritescleared, Toast.LENGTH_SHORT).show();
                        da.dismiss();
                    }
                });
                ((Button) da.findViewById(R.id.cancel)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        da.dismiss();
                    }
                });
                break;
            case CLEAR_TOPTRACKS:
                final Dialog daa = new Dialog(getActivity());
                daa.requestWindowFeature(Window.FEATURE_NO_TITLE);
                daa.setContentView(R.layout.unfavorites_dialog);
                ((TextView) daa.findViewById(R.id.playlist)).setText(R.string.cleartoptracks_long);
                daa.getWindow().getAttributes().windowAnimations = R.style.DialogAnim;
                daa.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                daa.show();
                ((Button) daa.findViewById(R.id.create)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        commonClass.getDBAccessHelper().clearTopTracks();
                        Toast.makeText(mContext, R.string.favoritescleared, Toast.LENGTH_SHORT).show();
                        daa.dismiss();
                    }
                });
                ((Button) daa.findViewById(R.id.cancel)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        daa.dismiss();
                    }
                });
                break;
            case CLEAR_RECENTLY_PLAYED:
                final Dialog daa1 = new Dialog(getActivity());
                daa1.requestWindowFeature(Window.FEATURE_NO_TITLE);
                daa1.setContentView(R.layout.unfavorites_dialog);
                ((TextView) daa1.findViewById(R.id.playlist)).setText(R.string.clearrecentlyplayed);
                daa1.getWindow().getAttributes().windowAnimations = R.style.DialogAnim;
                daa1.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                daa1.show();
                ((Button)daa1.findViewById(R.id.create)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        commonClass.getDBAccessHelper().clearRecentlyPlayed();
                        Toast.makeText(mContext, R.string.recentlyplayedcleared, Toast.LENGTH_SHORT).show();
                        daa1.dismiss();
                    }
                });
                ((Button) daa1.findViewById(R.id.cancel)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        daa1.dismiss();
                    }
                });
                break;
            default:
                break;
        }
        return true;
    }

    public class PlaylistLoaders extends AsyncTask<Void, Void, ArrayList<HashMap<String, String>>> {
        @Override
        protected ArrayList<HashMap<String, String>> doInBackground(Void... params) {

            try {
                playtList = new ArrayList<HashMap<String, String>>();
                String[] columns = {
                        BaseColumns._ID,
                        MediaStore.Audio.Playlists._ID,
                        MediaStore.Audio.Playlists.NAME

                };
                cursor = mContext.getContentResolver().query(
                        MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
                        columns,
                        null,
                        null,
                        MediaStore.Audio.Playlists.DEFAULT_SORT_ORDER);

                HashMap<String, String> recently = new HashMap<String, String>();
                recently.put("playListId", "-1");
                recently.put("playListName", "Recently Added");
                HashMap<String, String> favorites = new HashMap<String, String>();
                favorites.put("playListId", "-2");
                favorites.put("playListName", "Favorites");
                HashMap<String, String> toptracks = new HashMap<String, String>();
                toptracks.put("playListId", "-3");
                toptracks.put("playListName", "Top Played");
                HashMap<String, String> recentlyplayed = new HashMap<String, String>();
                recentlyplayed.put("playListId", "-4");
                recentlyplayed.put("playListName", "Recently Played");
                playtList.add(0, recently);
                playtList.add(1, favorites);
                playtList.add(2, toptracks);
                playtList.add(3, recentlyplayed);

                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        HashMap<String, String> song = new HashMap<String, String>();
                        song.put("playListId", cursor.getString(1));
                        song.put("playListName", cursor.getString(2));
                        playtList.add(song);
                    } while (cursor.moveToNext());
                }
                if (cursor != null) {
                    cursor.close();
                    cursor = null;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return playtList;
        }

        @Override
        protected void onPostExecute(ArrayList<HashMap<String, String>> hashMaps) {
            albumAdapter.setData(hashMaps);
            arg1 = hashMaps;
            super.onPostExecute(hashMaps);
        }
    }
}*/
