package com.reyansh.audio.audioplayer.free.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.reyansh.audio.audioplayer.free.Common.CommonClass;
import com.reyansh.audio.audioplayer.free.MainActivity.Main;
import com.reyansh.audio.audioplayer.free.R;

public class FontDialog extends Dialog {

    TextView f1, f2, f3, f4, f5, f6, f7;
    Context mContext;
    CommonClass mApp;

    public FontDialog(Context context) {
        super(context);
        mContext = context;
        mApp = (CommonClass) mContext.getApplicationContext();
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.font_dialog);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        getWindow().getAttributes().windowAnimations = R.style.DialogAnim;
        Typeface font1 = Typeface.createFromAsset(getContext().getAssets(), "fonts/JosefinSans_Light.ttf");
        f1 = (TextView) findViewById(R.id.f1);
        f1.setTypeface(font1);
        f1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mApp.getPreferencesUtility().setFont("Roboto Thin");
                dismiss();
                Toast.makeText(mContext,"Please restart the app to apply the settings.",Toast.LENGTH_SHORT).show();
                mContext.startActivity(new Intent(mContext, Main.class));

            }
        });
        Typeface font2 = Typeface.createFromAsset(getContext().getAssets(), "fonts/December_Reaper.ttf");
        f2 = (TextView) findViewById(R.id.f2);
        f2.setTypeface(font2);
        f2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mApp.getPreferencesUtility().setFont("December Reaper");
                dismiss();
                mContext.startActivity(new Intent(mContext, Main.class));
            }
        });

        Typeface font3 = Typeface.createFromAsset(getContext().getAssets(), "fonts/Alighty_Nesia.ttf");
        f3 = (TextView) findViewById(R.id.f3);
        f3.setTypeface(font3);
        f3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mApp.getPreferencesUtility().setFont("Alighty Nesia");
                dismiss();
                mContext.startActivity(new Intent(mContext, Main.class));
            }
        });

        Typeface font4 = Typeface.createFromAsset(getContext().getAssets(), "fonts/julius-sans-one.ttf");
        f4 = (TextView) findViewById(R.id.f4);
        f4.setTypeface(font4);
        f4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mApp.getPreferencesUtility().setFont("Josefin Sans");
                dismiss();
                mContext.startActivity(new Intent(mContext, Main.class));
            }
        });
        Typeface font6 = Typeface.createFromAsset(getContext().getAssets(), "fonts/csl.ttf");

        Typeface font5 = Typeface.createFromAsset(getContext().getAssets(), "fonts/a.otf");
        f5 = (TextView) findViewById(R.id.f5);
        f5.setTypeface(font5);
        f5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mApp.getPreferencesUtility().setFont("a");
                dismiss();
                mContext.startActivity(new Intent(mContext, Main.class));
            }
        });


        f6 = (TextView) findViewById(R.id.f6);
        f6.setTypeface(font6);
        f6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mApp.getPreferencesUtility().setFont("csl");
                dismiss();
                mContext.startActivity(new Intent(mContext, Main.class));
            }
        });

        Typeface font7 = Typeface.createFromAsset(getContext().getAssets(), "fonts/Lato-Hairline.ttf");
        f7 = (TextView) findViewById(R.id.f7);
        f7.setTypeface(font7);
        f7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mApp.getPreferencesUtility().setFont("h");
                dismiss();
                mContext.startActivity(new Intent(mContext, Main.class));
            }
        });




        if (mApp.getPreferencesUtility().getFont().equals("NULL")) {
            f1.setBackgroundColor(Color.parseColor("#60000000"));
        } else if (mApp.getPreferencesUtility().getFont().equals("December Reaper")) {
            f2.setBackgroundColor(Color.parseColor("#60000000"));
        } else if (mApp.getPreferencesUtility().getFont().equals("Alighty Nesia")) {
            f3.setBackgroundColor(Color.parseColor("#60000000"));
        } else if (mApp.getPreferencesUtility().getFont().equals("Roboto Thin")) {
            f1.setBackgroundColor(Color.parseColor("#60000000"));
        } else if (mApp.getPreferencesUtility().getFont().equals("Josefin Sans")) {
            f4.setBackgroundColor(Color.parseColor("#60000000"));
        } else if (mApp.getPreferencesUtility().getFont().equals("csl")) {
            f6.setBackgroundColor(Color.parseColor("#60000000"));
        } else if (mApp.getPreferencesUtility().getFont().equals("a")) {
            f5.setBackgroundColor(Color.parseColor("#60000000"));
        } else if (mApp.getPreferencesUtility().getFont().equals("h")) {
            f5.setBackgroundColor(Color.parseColor("#60000000"));
        }
    }


}
