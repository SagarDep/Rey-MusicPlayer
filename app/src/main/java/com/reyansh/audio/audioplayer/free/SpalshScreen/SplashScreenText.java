package com.reyansh.audio.audioplayer.free.SpalshScreen;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;


/**
 * Created by REYANSH on 28/01/2016.
 */
public class SplashScreenText extends TextView {
        Typeface tf;
        public SplashScreenText(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
            init();
        }

        public SplashScreenText(Context context, AttributeSet attrs) {
            super(context, attrs);
            init();
        }

        public SplashScreenText(Context context) {
            super(context);
            init();
        }

        public void init() {
            tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/a.otf");
            setTypeface(tf, 1);
        }
    }
