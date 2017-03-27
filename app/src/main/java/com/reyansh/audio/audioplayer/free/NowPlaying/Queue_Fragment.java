package com.reyansh.audio.audioplayer.free.NowPlaying;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.reyansh.audio.audioplayer.free.Common.CommonClass;
import com.reyansh.audio.audioplayer.free.Dialog.PlaylistDialog;
import com.reyansh.audio.audioplayer.free.MusicService.MusicService;
import com.reyansh.audio.audioplayer.free.R;
import com.reyansh.audio.audioplayer.free.Utils.MetaRetriever;

import java.util.HashMap;

/**
 * Created by REYANSH on 11/06/2016.
 */
public class Queue_Fragment extends Fragment implements OnStartDragListener {
    LinearLayout imageViewbg;
    Queue_Adapter queue_adapter;
    RecyclerView recyclerView;
    View v;
    CommonClass mApp;
    ItemTouchHelper itemTouchHelper;
    ImageView img_view_album_art;
    TextView txtView_song_name, txtView_artist_name;
    ImageView btn_nxt, btn_previous, btn_save;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.a, container, false);
        mApp=(CommonClass) getActivity().getApplicationContext();
        imageViewbg = (LinearLayout) v.findViewById(R.id.playlistbg);
        img_view_album_art = (ImageView) v.findViewById(R.id.image_view_album_art);
        btn_nxt = (ImageView) v.findViewById(R.id.image_btn_nxt);
        btn_previous = (ImageView) v.findViewById(R.id.image_btn_previous);
        txtView_song_name = (TextView) v.findViewById(R.id.text_view_song_name);
        txtView_artist_name = (TextView) v.findViewById(R.id.text_view_artist_name);
        btn_nxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mApp.getService().nextSong();
                queue_adapter.notifyDataSetChanged();
            }
        });
        btn_previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mApp.getService().previousSong();
                queue_adapter.notifyDataSetChanged();
            }
        });
        btn_save = (ImageView) v.findViewById(R.id.image_btn_save);
        btn_save.setOnClickListener(saveQueue);

        recyclerView = (RecyclerView) v.findViewById(R.id.nowplayingqueue);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);
        queue_adapter = new Queue_Adapter(getActivity(),mApp.getService().getSongList(), this, mApp);
        recyclerView.setAdapter(queue_adapter);
        DragSortRecycler dragSortRecycler = new DragSortRecycler();
        dragSortRecycler.setViewHandleId(R.id.dragger);
        dragSortRecycler.setOnItemMovedListener(new DragSortRecycler.OnItemMovedListener() {
            @Override
            public void onItemMoved(int from, int to) {
                HashMap<String, String> item = mApp.getService().getSongList().get(from);
                mApp.getService().getSongList().remove(from);
                mApp.getService().getSongList().add(to, item);
                if (from == mApp.getService().getCurrentSongIndex()) {
                    mApp.getService().setCurrentSongIndex(to);
                } else if (from > mApp.getService().getCurrentSongIndex() && to <= mApp.getService().getCurrentSongIndex()) {
                    int i = mApp.getService().getCurrentSongIndex();
                    mApp.getService().setCurrentSongIndex(i + 1);
                } else if (from < mApp.getService().getCurrentSongIndex() && to >= mApp.getService().getCurrentSongIndex()) {
                    int i = mApp.getService().getCurrentSongIndex();
                    mApp.getService().setCurrentSongIndex(i - 1);
                }
                queue_adapter.notifyItemMoved(from, to);
            }
        });
        recyclerView.addItemDecoration(dragSortRecycler);
        recyclerView.addOnItemTouchListener(dragSortRecycler);
        recyclerView.addOnScrollListener(dragSortRecycler.getScrollListener());
        recyclerView.getLayoutManager().scrollToPosition(mApp.getService().getCurrentSongIndex());
        return v;
    }

    View.OnClickListener saveQueue = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            PlaylistDialog b = new PlaylistDialog(getActivity(), mApp.getService().getSongList());
            b.show();
            WindowManager.LayoutParams lp = b.getWindow().getAttributes();
            lp.dimAmount = 0.5f;
            b.getWindow().setAttributes(lp);
            b.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
        }
    };

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        imageViewbg.setBackground(MetaRetriever.getsInstance().getBlurredArtWork(getActivity()));
        itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        updateUI();
        mApp = (CommonClass) getActivity().getApplicationContext();
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                mApp.getService().getSongList().get(position);
                mApp.getService().setSelectedSong(position, MusicService.NOTIFICATION_ID);
                android.app.FragmentManager manager = getActivity().getFragmentManager();
                android.app.FragmentTransaction trans = manager.beginTransaction();
                trans.remove(new Queue_Fragment());
                trans.commit();
                manager.popBackStack();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }

    public void updateUI() {
        txtView_song_name.setText(MetaRetriever.getsInstance().getSongName());
        txtView_artist_name.setText(MetaRetriever.getsInstance().getSongArtist());
        imageViewbg.setBackground(MetaRetriever.getsInstance().getBlurredArtWork(getActivity()));
        img_view_album_art.setImageBitmap(MetaRetriever.getsInstance().getArtWork());
    }

    ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
   /*         int from = viewHolder.getAdapterPosition();
            int to = target.getAdapterPosition();
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
            queue_adapter.notifyItemMoved(from, to);
   */
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
            if (mApp.getService().getSongList().size() == 1) {
                mApp.getService().getMediaPlayer().stop();
                getActivity().finish();
            } else if (viewHolder.getAdapterPosition()==mApp.getService().getCurrentSongIndex()){
                mApp.getService().nextSong();
                mApp.getService().getSongList().remove(viewHolder.getAdapterPosition());
                mApp.getService().setCurrentSongIndex(mApp.getService().getCurrentSongIndex() - 1);
            }else if (viewHolder.getAdapterPosition() < mApp.getService().getCurrentSongIndex()) {
                mApp.getService().getSongList().remove(viewHolder.getAdapterPosition());
                mApp.getService().setCurrentSongIndex(mApp.getService().getCurrentSongIndex() - 1);
            } else {
                mApp.getService().getSongList().remove(viewHolder.getAdapterPosition());
            }
            queue_adapter.notifyDataSetChanged();
        }
    };

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        itemTouchHelper.startDrag(viewHolder);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {
        private GestureDetector gestureDetector;
        private ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }

}
