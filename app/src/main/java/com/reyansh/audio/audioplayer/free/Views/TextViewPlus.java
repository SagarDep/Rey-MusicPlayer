package com.reyansh.audio.audioplayer.free.Views;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.reyansh.audio.audioplayer.free.Common.CommonClass;

/**
 * Created by REYANSH on 16/01/2016.
 */
public class TextViewPlus extends TextView {
    Typeface tf;
    Context mContext;
    CommonClass mApp;

    public TextViewPlus(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        init();
    }

    public TextViewPlus(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public TextViewPlus(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public void init() {
        mApp = (CommonClass) mContext.getApplicationContext();
        if (mApp.getPreferencesUtility().getFont().equals("NULL")) {
            tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/Lato-Hairline.ttf");
        } else if (mApp.getPreferencesUtility().getFont().equals("December Reaper")) {
            tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/December_Reaper.ttf");
        } else if (mApp.getPreferencesUtility().getFont().equals("Alighty Nesia")) {
            tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/Alighty_Nesia.ttf");
        } else if (mApp.getPreferencesUtility().getFont().equals("Roboto Thin")) {
            tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/JosefinSans_Light.ttf");
        } else if (mApp.getPreferencesUtility().getFont().equals("Josefin Sans")) {
            tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/julius-sans-one.ttf");
        } else if (mApp.getPreferencesUtility().getFont().equals("csl")) {
            tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/csl.ttf");
        } else if (mApp.getPreferencesUtility().getFont().equals("a")) {
            tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/a.otf");
        } else if (mApp.getPreferencesUtility().getFont().equals("h")) {
            tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/Lato-Hairline.ttf");
        } else {
            tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/Lato-Hairline.ttf.ttf");
        }
        setTypeface(tf, 1);
    }
}
