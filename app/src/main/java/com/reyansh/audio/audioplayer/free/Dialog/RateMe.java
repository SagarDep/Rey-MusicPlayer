package com.reyansh.audio.audioplayer.free.Dialog;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.reyansh.audio.audioplayer.free.Common.CommonClass;
import com.reyansh.audio.audioplayer.free.R;

public class RateMe extends Dialog {
    Context mContext;
    CommonClass mApp;

    public RateMe(Context context) {
        super(context);
        mContext = context;
        mApp = (CommonClass) mContext.getApplicationContext();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.ratings_layout);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        getWindow().getAttributes().windowAnimations = R.style.DialogAnim;
        ((Button) findViewById(R.id.ratenow)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("market://details?id=" + mContext.getPackageName());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                        Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET |
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                try {
                    mContext.startActivity(goToMarket);
                } catch (ActivityNotFoundException e) {
                    mContext.startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id=" + mContext.getPackageName())));
                }
            }
        });
        ((Button) findViewById(R.id.remindmelater)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        ((Button) findViewById(R.id.nothanks)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mApp.getPreferencesUtility().setRateUs(true);
                dismiss();
            }
        });
        ((Button) findViewById(R.id.remindmelater)).setTypeface(mApp.getStripTitleTypeFace());
        ((Button) findViewById(R.id.nothanks)).setTypeface(mApp.getStripTitleTypeFace());

    }
}
