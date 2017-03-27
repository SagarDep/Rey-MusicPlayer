package com.reyansh.audio.audioplayer.free.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.reyansh.audio.audioplayer.free.Common.CommonClass;
import com.reyansh.audio.audioplayer.free.R;

/**
 * Created by REYANSH on 22/04/2016.
 */
public class ScrollViewDialog extends Dialog {

    TextView f1, f2, f3, f4, f5, f6, f7, f8;
    Context mContext;
    CommonClass mApp;

    public ScrollViewDialog(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.scrollview_dialog);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        getWindow().getAttributes().windowAnimations = R.style.DialogAnim;
        f1 = (TextView) findViewById(R.id.f1);
        mApp = (CommonClass) mContext.getApplicationContext();
        f1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mApp.getPreferencesUtility().setScrollView("Default Transformer");
                dismiss();
            }
        });
        f2 = (TextView) findViewById(R.id.f2);
        f2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mApp.getPreferencesUtility().setScrollView("Accordion Transformer");
                dismiss();
            }
        });

        f3 = (TextView) findViewById(R.id.f3);
        f3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mApp.getPreferencesUtility().setScrollView("Rotate Down Transformer");
                dismiss();
            }
        });

        f4 = (TextView) findViewById(R.id.f4);

        f4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mApp.getPreferencesUtility().setScrollView("Rotate Up Transformer");
                dismiss();
            }
        });
        f5 = (TextView) findViewById(R.id.f5);
        f5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mApp.getPreferencesUtility().setScrollView("Scale In Out Transformer");
                dismiss();
            }
        });
        f6 = (TextView) findViewById(R.id.f6);

        f6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mApp.getPreferencesUtility().setScrollView("Stack Transformer");
                dismiss();
            }
        });
        f7 = (TextView) findViewById(R.id.f7);

        f7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mApp.getPreferencesUtility().setScrollView("Tablet Transformer");
                dismiss();
            }
        });
        f8 = (TextView) findViewById(R.id.f8);

        f8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mApp.getPreferencesUtility().setScrollView("Zoom Out Slide Transformer");
                dismiss();
            }
        });

        if (mApp.getPreferencesUtility().getScrollView().equals("Default Transformer")) {
            f1.setBackgroundColor(Color.parseColor("#60000000"));
        } else if (mApp.getPreferencesUtility().getScrollView().equals("Accordion Transformer")) {
            f2.setBackgroundColor(Color.parseColor("#60000000"));
        } else if (mApp.getPreferencesUtility().getScrollView().equals("Rotate Down Transformer")) {
            f3.setBackgroundColor(Color.parseColor("#60000000"));
        } else if (mApp.getPreferencesUtility().getScrollView().equals("Rotate Up Transformer")) {
            f4.setBackgroundColor(Color.parseColor("#60000000"));
        } else if (mApp.getPreferencesUtility().getScrollView().equals("Scale In Out Transformer")) {
            f5.setBackgroundColor(Color.parseColor("#60000000"));
        } else if (mApp.getPreferencesUtility().getScrollView().equals("Stack Transformer")) {
            f6.setBackgroundColor(Color.parseColor("#60000000"));
        } else if (mApp.getPreferencesUtility().getScrollView().equals("Tablet Transformer")) {
            f7.setBackgroundColor(Color.parseColor("#60000000"));
        } else if (mApp.getPreferencesUtility().getScrollView().equals("Zoom Out Slide Transformer")) {
            f8.setBackgroundColor(Color.parseColor("#60000000"));
        } else {
            f4.setBackgroundColor(Color.parseColor("#60000000"));
        }

    }


}

