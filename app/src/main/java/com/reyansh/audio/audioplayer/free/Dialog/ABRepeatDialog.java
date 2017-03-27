package com.reyansh.audio.audioplayer.free.Dialog;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.reyansh.audio.audioplayer.free.Common.CommonClass;
import com.reyansh.audio.audioplayer.free.R;
import com.reyansh.audio.audioplayer.free.Views.RangeSeekBar;

/**
 * Created by REYANSH on 23/07/2016.
 */
public class ABRepeatDialog extends Dialog {
    private Context mContext;
    private CommonClass mApp;

    private int repeatPointA;
    private int repeatPointB;
    private int currentSongIndex;
    private int currentSongDurationMillis;
    private int currentSongDurationSecs;
    private BroadcastReceiver receiver;

    private TextView repeatSongATime;
    private TextView repeatSongBTime;
    private SeekBar seekBar;
    private RangeSeekBar<Integer> rangeSeekBar;
    private ViewGroup viewGroup;

    private Button mYesButton;
    private Button mCancelButton;

    public ABRepeatDialog(Context context) {
        super(context);
        mContext = context;
        mApp = (CommonClass) mContext.getApplicationContext();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_ab_repeat);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        getWindow().getAttributes().windowAnimations = R.style.DialogAnim;
        currentSongIndex = mApp.getService().getCurrentSongIndex();

        repeatSongATime = (TextView) findViewById(R.id.repeat_song_range_A_time);
        repeatSongBTime = (TextView) findViewById(R.id.repeat_song_range_B_time);

        currentSongDurationMillis = (int) mApp.getService().getMediaPlayer().getDuration();
        currentSongDurationSecs = (int) currentSongDurationMillis / 1000;

        //Remove the placeholder seekBar and replace it with the RangeSeekBar.
        seekBar = (SeekBar) findViewById(R.id.repeat_song_range_placeholder_seekbar);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) seekBar.getLayoutParams();
        viewGroup = (ViewGroup) seekBar.getParent();
        viewGroup.removeView(seekBar);

        rangeSeekBar = new RangeSeekBar<Integer>(0, currentSongDurationSecs, mContext);
        rangeSeekBar.setLayoutParams(params);
        viewGroup.addView(rangeSeekBar);
        mYesButton = (Button) findViewById(R.id.button_yes);
        mCancelButton = (Button) findViewById(R.id.button_cancel);
        mYesButton.setTypeface(mApp.getStripTitleTypeFace());
        mCancelButton.setTypeface(mApp.getStripTitleTypeFace());
        mYesButton.setOnClickListener(yesClicked);
        mCancelButton.setOnClickListener(cancelClicked);
//        receiver = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                initDialog();
//            }
//        };
        initDialog();
    }

    private void initDialog() {
        currentSongDurationMillis = (int) mApp.getService().getMediaPlayer().getDuration();
        currentSongDurationSecs = (int) currentSongDurationMillis / 1000;
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) rangeSeekBar.getLayoutParams();
        viewGroup = (ViewGroup) rangeSeekBar.getParent();
        viewGroup.removeView(rangeSeekBar);

        rangeSeekBar = new RangeSeekBar<Integer>(0, currentSongDurationSecs, mContext);
        rangeSeekBar.setLayoutParams(params);
        viewGroup.addView(rangeSeekBar);

        if (mApp.getPreferencesUtility().getReateMode() == 3) {
            repeatSongATime.setText(convertMillisToMinsSecs(mApp.getService().getRepeatSongRangePointA()));
            repeatSongBTime.setText(convertMillisToMinsSecs(mApp.getService().getRepeatSongRangePointB()));
            rangeSeekBar.setSelectedMinValue(mApp.getService().getRepeatSongRangePointA());
            rangeSeekBar.setSelectedMaxValue(mApp.getService().getRepeatSongRangePointB());
            repeatPointA = mApp.getService().getRepeatSongRangePointA();
            repeatPointB = mApp.getService().getRepeatSongRangePointB();
        } else {
            repeatSongATime.setText("0:00");
            repeatSongBTime.setText(convertMillisToMinsSecs(currentSongDurationMillis));
            repeatPointA = 0;
            repeatPointB = currentSongDurationMillis;
        }
        rangeSeekBar.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener<Integer>() {

            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Integer minValue, Integer maxValue) {
                repeatPointA = minValue * 1000;
                repeatPointB = maxValue * 1000;
                repeatSongATime.setText(convertMillisToMinsSecs(minValue * 1000));
                repeatSongBTime.setText(convertMillisToMinsSecs(maxValue * 1000));
            }

        });

    }

    View.OnClickListener yesClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mApp.getService().setRepeatSongRange(repeatPointA, repeatPointB);
            mApp.getPreferencesUtility().setRepeateMode(3);
            dismiss();
        }
    };
    View.OnClickListener cancelClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dismiss();
        }
    };


    //Convert millisseconds to hh:mm:ss format.
    public static String convertMillisToMinsSecs(long milliseconds) {

        int secondsValue = (int) (milliseconds / 1000) % 60;
        int minutesValue = (int) ((milliseconds / (1000 * 60)) % 60);
        int hoursValue = (int) ((milliseconds / (1000 * 60 * 60)) % 24);

        String seconds = "";
        String minutes = "";
        String hours = "";

        if (secondsValue < 10) {
            seconds = "0" + secondsValue;
        } else {
            seconds = "" + secondsValue;
        }

        if (minutesValue < 10) {
            minutes = "0" + minutesValue;
        } else {
            minutes = "" + minutesValue;
        }

        if (hoursValue < 10) {
            hours = "0" + hoursValue;
        } else {
            hours = "" + hoursValue;
        }

        String output = "";

        if (hoursValue != 0) {
            output = hours + ":" + minutes + ":" + seconds;
        } else {
            output = minutes + ":" + seconds;
        }

        return output;
    }


    @Override
    public void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(mContext).registerReceiver((receiver), new IntentFilter("com.reyansh.audio.audioplayer.free.UPDATE_NOW_PLAYING"));
    }

    @Override
    public void onStop() {
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(receiver);
        super.onStop();
    }

}
