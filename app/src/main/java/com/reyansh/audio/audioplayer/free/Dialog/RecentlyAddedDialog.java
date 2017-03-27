package com.reyansh.audio.audioplayer.free.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.github.channguyen.rsv.RangeSliderView;
import com.reyansh.audio.audioplayer.free.Common.CommonClass;
import com.reyansh.audio.audioplayer.free.R;

/**
 * Created by REYANSH on 23/04/2016.
 */
public class RecentlyAddedDialog extends Dialog {
    private Context mContext;
    private TextView mWeeksTextView;
    private Button mYesButton;
    private Button mCancelButton;
    private RangeSliderView smallSlider;
    private int indexa;
    private CommonClass mApp;

    public RecentlyAddedDialog(Context context) {
        super(context);
        mContext = context;
        mApp = (CommonClass) mContext.getApplicationContext();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_recentlyadded);
        smallSlider = (RangeSliderView) findViewById(R.id.rsv_small);
        mWeeksTextView = (TextView) findViewById(R.id.weeks);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        getWindow().getAttributes().windowAnimations = R.style.DialogAnim;
        mYesButton = (Button) findViewById(R.id.create);
        mCancelButton = (Button) findViewById(R.id.cancel);
        if (mApp.getPreferencesUtility().getWeeks() != 0) {
            smallSlider.setInitialIndex(mApp.getPreferencesUtility().getWeeks() - 1);
        } else {
            smallSlider.setInitialIndex(mApp.getPreferencesUtility().getWeeks());
        }
        if (indexa == 1)
            mWeeksTextView.setText(mApp.getPreferencesUtility().getWeeks() + " " + "Week");
        else
            mWeeksTextView.setText(mApp.getPreferencesUtility().getWeeks() + " " + "Weeks");

        final RangeSliderView.OnSlideListener listener = new RangeSliderView.OnSlideListener() {
            @Override
            public void onSlide(int index) {
                indexa = index + 1;
                if (indexa == 1)
                    mWeeksTextView.setText(indexa + " " + "Week");
                else
                    mWeeksTextView.setText(indexa + " " + "Weeks");
            }
        };
        smallSlider.setOnSlideListener(listener);

        mYesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mApp.getPreferencesUtility().setWeeks(indexa);
                dismiss();
            }
        });
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        mCancelButton.setTypeface(mApp.getStripTitleTypeFace());
        mYesButton.setTypeface(mApp.getStripTitleTypeFace());
    }
}
