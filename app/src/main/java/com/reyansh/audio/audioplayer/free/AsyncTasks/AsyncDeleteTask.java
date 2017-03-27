package com.reyansh.audio.audioplayer.free.AsyncTasks;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.DocumentsContract;
import android.widget.Toast;

import com.reyansh.audio.audioplayer.free.FileDirectory.FragmentFolder;
import com.reyansh.audio.audioplayer.free.Utils.MusicUtils;

import java.io.File;

public class AsyncDeleteTask extends AsyncTask<String, Void, Boolean> {
    private Context mContext;
    private ProgressDialog pd;
    private FragmentFolder mFragment;
    private File mSourceFile;

    public AsyncDeleteTask(Context context, File source) {
        mContext = context;
        mSourceFile = source;
    }

    protected void onPreExecute() {
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected Boolean doInBackground(String... params) {
        Uri uri=MusicUtils.getDocumentUri(mSourceFile, false, mContext);
        if (!MusicUtils.isKitkat()) {
            if (uri!= null) {
                DocumentsContract.deleteDocument(mContext.getContentResolver(), uri);
                MusicUtils.deleteViaContentProvider(mContext, mSourceFile.getAbsolutePath());
            }
            return true;
        } else {
            try {
                boolean status = mSourceFile.delete();
                if (status == true) {
                    return true;
                } else {
                    return false;
                }
            } catch (Exception e) {
                return false;
            }
        }
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
        if (result)
        Toast.makeText(mContext,"File deleted!",Toast.LENGTH_SHORT).show();
        else
        Toast.makeText(mContext,"File can't be deleted!",Toast.LENGTH_SHORT).show();

//    	}
//        try {
//            mFragment.refreshListView();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

}