package com.reyansh.audio.audioplayer.free.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.reyansh.audio.audioplayer.free.R;

/**
 * Created by REYANSH on 07/05/2016.
 */
public class WhatsNewDialog extends Dialog {
    Context mContext;
    public WhatsNewDialog(Context context) {
        super(context);
        mContext=context;
    }
Button dismiss;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.whats_new_layout);
        ((Button)findViewById(R.id.dismiss)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
}
