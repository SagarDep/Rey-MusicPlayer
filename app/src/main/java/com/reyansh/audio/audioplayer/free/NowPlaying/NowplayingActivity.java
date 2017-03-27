package com.reyansh.audio.audioplayer.free.NowPlaying;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.reyansh.audio.audioplayer.free.Common.CommonClass;
import com.reyansh.audio.audioplayer.free.MusicService.MusicService;
import com.reyansh.audio.audioplayer.free.R;
import com.reyansh.audio.audioplayer.free.Utils.MetaRetriever;
import com.mobeta.android.dslv.DragSortController;
import com.mobeta.android.dslv.DragSortListView;

import java.util.ArrayList;
import java.util.HashMap;

public class NowplayingActivity extends Fragment implements AdapterView.OnItemClickListener {
    DragSortListView lk;
    ImageView img_view_album_art;
    ImageView btn_nxt, btn_previous;
    LinearLayout imageViewbg;
    NowPlayingQueue mAdapter;
    View v;
    MusicService musicService = new MusicService();
    CommonClass mApp;
    private Context mContext;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.nowplaying_queue, container, false);
        lk = (DragSortListView) v.findViewById(R.id.dragsortlistview);
        img_view_album_art = (ImageView) v.findViewById(R.id.image_view_album_art);
        btn_nxt = (ImageView) v.findViewById(R.id.image_btn_nxt);
        btn_previous = (ImageView) v.findViewById(R.id.image_btn_previous);
        imageViewbg = (LinearLayout) v.findViewById(R.id.playlistbg);
        lk.setDividerHeight(0);
        return v;
    }

    private DragSortListView.DropListener onDrop = new DragSortListView.DropListener() {
        @Override
        public void drop(int from, int to) {
            if (from != to) {
                HashMap<String, String> item = musicService.getSongList().get(from);
                musicService.getSongList().remove(from);
                musicService.getSongList().add(to, item);
                if (from == musicService.getCurrentSongIndex()) {
                    musicService.setCurrentSongIndex(to);
                } else if (from > musicService.getCurrentSongIndex() && to <= musicService.getCurrentSongIndex()) {
                    int i = musicService.getCurrentSongIndex();
                    musicService.setCurrentSongIndex(i + 1);
                } else if (from < musicService.getCurrentSongIndex() && to >= musicService.getCurrentSongIndex()) {
                    int i = musicService.getCurrentSongIndex();
                    musicService.setCurrentSongIndex(i - 1);
                }
                mAdapter.notifyDataSetChanged();

            }
        }
    };
    private DragSortListView.RemoveListener onRemove = new DragSortListView.RemoveListener() {
        @Override
        public void remove(int which) {
            if (((NowPlaying)getActivity()) == null)
                return;
            if (musicService.getSongList().size() == 1) {
                mApp.getService().getMediaPlayer().stop();
                mApp.getService().stopSelf();
                getActivity().finish();
                return;
            } else if (musicService.getCurrentSongIndex() == which) {
                mApp.getService().nextSong();
                mApp.getService().getSongList().remove(which);
                musicService.setCurrentSongIndex(musicService.getCurrentSongIndex() - 1);
            } else if (which < musicService.getCurrentSongIndex()) {
                mApp.getService().getSongList().remove(which);
                musicService.setCurrentSongIndex(musicService.getCurrentSongIndex() - 1);
            } else {
                mApp.getService().getSongList().remove(which);
            }

            mAdapter.remove(mAdapter.getItem(which));
            mAdapter.notifyDataSetChanged();
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        mAdapter = new NowPlayingQueue(getActivity(), musicService.getSongList(), android.R.id.list);
        lk.setAdapter(mAdapter);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        v = new View(getActivity());
        DragSortController controller = new DragSortController(lk);
        controller.setDragHandleId(R.id.dragger);
        controller.setRemoveEnabled(true);
        controller.setSortEnabled(true);
        controller.setDragInitMode(1);
        mApp = (CommonClass) mContext.getApplicationContext();
        lk.setFloatViewManager(controller);
        updateUI();
        lk.setOnTouchListener(controller);
        lk.setOnItemClickListener(this);
        btn_nxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mApp.getService().nextSong();
            }
        });
        btn_previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mApp.getService().previousSong();
            }
        });
        lk.setDragEnabled(true);
        mAdapter = new NowPlayingQueue(getActivity(), musicService.getSongList(), android.R.id.list);
        lk.setAdapter(mAdapter);
        lk.setDropListener(onDrop);
        lk.setRemoveListener(onRemove);
        mAdapter.notifyDataSetChanged();
    }

    public void updateUI() {
        imageViewbg.setBackground(MetaRetriever.getsInstance().getBlurredArtWork(getActivity()));
        img_view_album_art.setImageBitmap(MetaRetriever.getsInstance().getArtWork());
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mApp.getService().setSelectedSong(position, MusicService.NOTIFICATION_ID);
        android.app.FragmentManager manager = getActivity().getFragmentManager();
        android.app.FragmentTransaction trans = manager.beginTransaction();
        trans.remove(this);
        trans.commit();
        manager.popBackStack();
    }
}

class NowPlayingQueue extends ArrayAdapter {
    private Context mContext;
    private ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();

    public NowPlayingQueue(Context context, ArrayList<HashMap<String, String>> list, int resource) {
        super(context, resource);
        mContext = context;
        this.songsList = list;
    }

    @Override
    public int getCount() {
        return songsList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.queue_drawer_list_layout, null);
        }
        TextView mtxtSongName = (TextView) convertView.findViewById(R.id.queue_song_title);
        mtxtSongName.setText(songsList.get(position).get("songName"));
        return convertView;
    }


}