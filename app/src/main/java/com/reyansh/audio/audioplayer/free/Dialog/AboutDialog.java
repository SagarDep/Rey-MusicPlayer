package com.reyansh.audio.audioplayer.free.Dialog;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.reyansh.audio.audioplayer.free.R;
import com.reyansh.audio.audioplayer.free.Utils.MetaRetriever;

public class AboutDialog extends Dialog {
    Context mContext;

    public AboutDialog(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_about);
        getWindow().getAttributes().windowAnimations = R.style.DialogAnim;
        ((ImageView) findViewById(R.id.mainbg)).setImageBitmap(MetaRetriever.getsInstance().getBlurredArtWorkA(mContext));
      /*  ((Button) findViewById(R.id.whatsnew)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
          WhatsNewDialog f=new WhatsNewDialog(mContext);
                f.show();
            }
        });*/

        ((TextView) findViewById(R.id.rateus)).setOnClickListener(new View.OnClickListener() {
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
    }
}
