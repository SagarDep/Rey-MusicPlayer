package com.reyansh.audio.audioplayer.free.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.widget.SeekBar;

import com.reyansh.audio.audioplayer.free.Common.CommonClass;
import com.reyansh.audio.audioplayer.free.R;

public class DrawableColorDialog extends Dialog {
    private Context mContext;
    private CommonClass mApp;
    private SeekBar mAlphaSeekbar;

    public DrawableColorDialog(Context context) {
        super(context);
        mContext = context;
        mApp = (CommonClass) mContext.getApplicationContext();
        setContentView(R.layout.layout_drawable_color);
        mAlphaSeekbar = (SeekBar) findViewById(R.id.color_seekbar);
        mAlphaSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }


}
