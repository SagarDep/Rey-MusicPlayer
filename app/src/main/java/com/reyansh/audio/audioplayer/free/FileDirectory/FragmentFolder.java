package com.reyansh.audio.audioplayer.free.FileDirectory;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.reyansh.audio.audioplayer.free.Common.CommonClass;
import com.reyansh.audio.audioplayer.free.MusicService.MusicService;
import com.reyansh.audio.audioplayer.free.NowPlaying.NowPlaying;
import com.reyansh.audio.audioplayer.free.R;
import com.reyansh.audio.audioplayer.free.Utils.MusicUtils;

import org.apache.commons.io.comparator.NameFileComparator;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class FragmentFolder extends Fragment implements MusicUtils.names, View.OnClickListener, MusicUtils.Defs {
    ImageButton mBackButton;
    TextView mCurrentPath;
    ListView mListView;
    Context mCotext;
    FoldersListViewAdapter foldersListViewAdapter;
    FragmentFolder mainActivity;
    private View v;
    //Folder parameter ArrayLists.
    private String rootDir;
    public static String currentDir;
    private List<String> fileFolderNameList = null;
    private List<String> fileFolderPathList = null;
    private List<String> fileFolderSizeList = null;
    private List<Integer> fileFolderTypeList = null;
    public static ArrayList<HashMap<String, String>> fetchedFiles = new ArrayList<HashMap<String, String>>();
    //File size/unit dividers
    private final long kiloBytes = 1024;
    private final long megaBytes = kiloBytes * kiloBytes;
    private final long gigaBytes = megaBytes * kiloBytes;
    private final long teraBytes = gigaBytes * kiloBytes;
    private Handler mHandler = new Handler();

    //List of subdirectories within a directory (Used by "Play Folder Recursively").
    private ArrayList<String> subdirectoriesList = new ArrayList<String>();

    //HashMap to store the each folder's previous scroll/position state.
    private HashMap<String, Parcelable> mFolderStateMap;
    private long mSelectedId;
    //Handler.
    public static int mSelectedPosition;
    public static final int FOLDER = 0;
    public static final int FILE = 1;
    public static final int AUDIO_FILE = 3;
    public static final int PICTURE_FILE = 4;
    public static final int VIDEO_FILE = 5;

    CommonClass mApp;
    Context mContext;
    LinearLayout mBackLinearLayout;
    private static FragmentFolder mFragmentFolder;
    private ImageButton mAddToQueueImageButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.file_directory, container, false);
        mCotext = getActivity().getApplication();
        mBackButton = (ImageButton) v.findViewById(R.id.back_imagebutton);
        mAddToQueueImageButton = (ImageButton) v.findViewById(R.id.image_button_add);
        mAddToQueueImageButton.setOnClickListener(this);
        mCurrentPath = (TextView) v.findViewById(R.id.currentPath_textView);
        mCurrentPath.setSelected(true);
        mBackButton.setOnClickListener(this);
        mListView = (ListView) v.findViewById(R.id.listView);
        mListView.setDividerHeight(0);
        mListView.setOnCreateContextMenuListener(this);
        mainActivity = this;
        mBackLinearLayout = (LinearLayout) v.findViewById(R.id.back_linear_layout);
        mBackLinearLayout.setOnClickListener(this);
        mContext = getActivity().getApplication();
        mApp = (CommonClass) mContext.getApplicationContext();
        mFolderStateMap = new HashMap<String, Parcelable>();
        rootDir = Environment.getExternalStorageDirectory().getPath();
        currentDir = rootDir;
        slideUpListView();
        return v;
    }

    private void slideUpListView() {
        getDir(rootDir, null);
    }

    private void getDir(String dirPath, Parcelable restoreState) {
        mCurrentPath.setText(dirPath);
        fileFolderNameList = new ArrayList<String>();
        fileFolderPathList = new ArrayList<String>();
        fileFolderSizeList = new ArrayList<String>();
        fileFolderTypeList = new ArrayList<Integer>();
        final File f = new File(dirPath);
        File[] files = f.listFiles();
        if (files != null) {
            //Sort the files by name.
                  Arrays.sort(files, new FileNameComparator());
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                if (file.canRead()) {
                    if (file.isDirectory()) {
                        /*
                         * Starting with Android 4.2, /storage/emulated/legacy/...
                   * is a symlink that points to the actual directory where
                   * the user's files are stored. We need to detect the
                   * actual directory's file path here.
                   */
                        String filePath;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
                            filePath = getRealFilePath(file.getAbsolutePath());
                        else
                            filePath = file.getAbsolutePath();

                        fileFolderPathList.add(filePath);
                        fileFolderNameList.add(file.getName());
                        File[] listOfFiles = file.listFiles();

                        if (listOfFiles != null) {
                            fileFolderTypeList.add(FOLDER);
                            if (listOfFiles.length == 1) {
                                fileFolderSizeList.add("" + listOfFiles.length + " item");
                            } else {
                                fileFolderSizeList.add("" + listOfFiles.length + " items");
                            }
                        } else {
                            fileFolderTypeList.add(FOLDER);
                            fileFolderSizeList.add("Unknown items");
                        }
                    } else {
                        try {
                            String path = file.getCanonicalPath();
                            fileFolderPathList.add(path);
                        } catch (IOException e) {
                            continue;
                        }
                        fileFolderNameList.add(file.getName());
                        String fileName = "";
                        try {
                            fileName = file.getCanonicalPath();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        //Add the file element to fileFolderTypeList based on the file type.
                        if (getFileExtension(fileName).equalsIgnoreCase("mp3") ||
                                getFileExtension(fileName).equalsIgnoreCase("ogg") ||
                                getFileExtension(fileName).equalsIgnoreCase("wav")) {
                            String[] columns = {BaseColumns._ID,
                                    MediaStore.Audio.Media.TITLE,
                                    MediaStore.Audio.Media.ARTIST,
                                    MediaStore.Audio.Media.DURATION,
                                    MediaStore.Audio.Media.DATA,
                                    MediaStore.Audio.Media.ALBUM,
                                    MediaStore.Audio.Media.ALBUM_ID};

                            //The file is an audio file.
                            fileFolderTypeList.add(AUDIO_FILE);
                            fileFolderSizeList.add("" + getFormattedFileSize(file.length()));
                            String[] whereArgs = new String[]{file.getPath()};
                            Cursor cursor = getActivity().getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, columns, MediaStore.Audio.Media.DATA + " LIKE ?", whereArgs, MediaStore.Audio.Media.TITLE);
                            if (cursor != null && cursor.moveToFirst()) {
                                do {
                                    HashMap<String, String> albumTracks = new HashMap<String, String>();
                                    albumTracks.put(SONG_ID, cursor.getString(0));
                                    albumTracks.put(SONG_NAME, cursor.getString(1));
                                    albumTracks.put(SONG_ARTIST, cursor.getString(2));
                                    albumTracks.put(SONG_DURATION, cursor.getString(3));
                                    albumTracks.put(SONG_PATH, cursor.getString(4));
                                    albumTracks.put(SONG_ALBUM, cursor.getString(5));
                                    albumTracks.put(SONG_ALBUM_ID, cursor.getString(6));
                                    fetchedFiles.add(albumTracks);
                                } while (cursor.moveToNext());
                            }
                        } else if (getFileExtension(fileName).equalsIgnoreCase("webp")) {
                            //The file is a picture file.
                            fileFolderTypeList.add(PICTURE_FILE);
                            fileFolderSizeList.add("" + getFormattedFileSize(file.length()));

                        } else if (getFileExtension(fileName).equalsIgnoreCase("webm")) {
                            //The file is a video file.
                            fileFolderTypeList.add(VIDEO_FILE);
                            fileFolderSizeList.add("" + getFormattedFileSize(file.length()));

                        } else {
                            //We don't have an icon for this file type so give it the generic file flag.
                            fileFolderTypeList.add(FILE);
                            fileFolderSizeList.add("" + getFormattedFileSize(file.length()));
                        }
                    }
                }
            }
        }

        foldersListViewAdapter = new FoldersListViewAdapter(getActivity(), this, fileFolderNameList,
                fileFolderTypeList,
                fileFolderSizeList,
                fileFolderPathList);
        mListView.setAdapter(foldersListViewAdapter);
        foldersListViewAdapter.notifyDataSetChanged();

        //Restore the ListView's previous state.
        if (restoreState != null) {
            mListView.onRestoreInstanceState(restoreState);
        } else if (mFolderStateMap.containsKey(dirPath)) {
            mListView.onRestoreInstanceState(mFolderStateMap.get(dirPath));
        }


        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int index, long id) {
                //Store the current folder's state in the HashMap.
                if (mFolderStateMap.size() == 3) {
                    mFolderStateMap.clear();
                }

                mFolderStateMap.put(currentDir, mListView.onSaveInstanceState());

                String newPath = fileFolderPathList.get(index);
                if ((Integer) view.getTag(R.string.folder_list_item_type) == FOLDER)
                    currentDir = newPath;
                //Check if the selected item is a folder or a file.
                if (fileFolderTypeList.get(index) == FOLDER) {
                    fetchedFiles.clear();
                    getDir(newPath, null);
                } else if (fileFolderTypeList.get(index) == AUDIO_FILE) {
                    int fileIndex = 0;
                    for (int i = 0; i < index; i++) {
                        if (fileFolderTypeList.get(i) == AUDIO_FILE)
                            fileIndex++;
                    }
                    mApp.getService().setSongList(fetchedFiles);
                    mApp.getService().setSelectedSong(fileIndex, MusicService.NOTIFICATION_ID);
                    startActivity(new Intent(mContext, NowPlaying.class));
                } else {
                    Toast.makeText(mContext, "Sorry can't play this audio!!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }public class FileNameComparator implements Comparator<File> {
        public int compare( File a, File b ) {
            return a.getName().compareTo( b.getName() );
        }
    }

    public String getFileExtension(String fileName) {
        String fileNameArray[] = fileName.split("\\.");
        String extension = fileNameArray[fileNameArray.length - 1];
        return extension;
    }

    @SuppressLint("SdCardPath")
    private String getRealFilePath(String filePath) {
        if (filePath.equals("/storage/emulated/0") ||
                filePath.equals("/storage/emulated/0/") ||
                filePath.equals("/storage/emulated/legacy") ||
                filePath.equals("/storage/emulated/legacy/") ||
                filePath.equals("/storage/sdcard0") ||
                filePath.equals("/storage/sdcard0/") ||
                filePath.equals("/sdcard") ||
                filePath.equals("/sdcard/") ||
                filePath.equals("/mnt/sdcard") ||
                filePath.equals("/mnt/sdcard/")) {
            return Environment.getExternalStorageDirectory().toString();
        }
        return filePath;
    }

    public String getFormattedFileSize(final long value) {
        final long[] dividers = new long[]{teraBytes, gigaBytes, megaBytes, kiloBytes, 1};
        final String[] units = new String[]{"TB", "GB", "MB", "KB", "bytes"};
        if (value < 1) {
            return "";
        }
        String result = null;
        for (int i = 0; i < dividers.length; i++) {
            final long divider = dividers[i];
            if (value >= divider) {
                result = format(value, divider, units[i]);
                break;
            }
        }
        return result;
    }

    public String format(final long value, final long divider, final String unit) {
        final double result = divider > 1 ? (double) value / (double) divider : (double) value;

        return new DecimalFormat("#,##0.#").format(result) + " " + unit;
    }

    public boolean getParentDir() {

        if (currentDir.equals("/"))
            return true;

        //Get the current folder's parent folder.
        File currentFolder = new File(currentDir);
        String parentFolder = "";
        try {
            parentFolder = currentFolder.getParentFile().getCanonicalPath();
        } catch (Exception e) {
            e.printStackTrace();
        }

        FragmentFolder.currentDir = parentFolder;
        fetchedFiles.clear();
        getDir(parentFolder, null);
        return false;

    }

    public String getCurrentDir() {
        return currentDir;
    }

    @Override
    public void onClick(View v) {
        if ((v.getId() == R.id.back_imagebutton) || v.getId() == R.id.back_linear_layout) {
            if (getCurrentDir().equals("/")) {
            } else {
                getParentDir();
            }
        } else if (v.getId() == R.id.image_button_add) {
            if (fetchedFiles.size() != 0 && fetchedFiles != null) {
                mApp.getService().addtoqueue(fetchedFiles);
                Toast.makeText(mContext, fetchedFiles.size() + "Added to the queue.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(mContext, "No songs in the current directory!!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void refreshListView() {
        getDir(currentDir, mListView.onSaveInstanceState());
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfoIn) {
        AdapterView.AdapterContextMenuInfo mi = (AdapterView.AdapterContextMenuInfo) menuInfoIn;
        if (fileFolderTypeList.get(mi.position) == AUDIO_FILE) {
            int fileIndex = 0;
            for (int i = 0; i < mi.position; i++) {
                if (fileFolderTypeList.get(i) == AUDIO_FILE) fileIndex++;
            }
            menu.add(1, PLAY_NEXT, 0, R.string.play_next);
            menu.add(1, ADD_TO_QUEUE, 0, R.string.add_to_queue);
            SubMenu sub = menu.addSubMenu(0, ADD_TO_PLAYLIST, 0, R.string.add_to_playlist);
            MusicUtils.makePlaylistMenu(getContext(), sub, 1);
            menu.add(1, ADD_TO_FAVORITES, 0, R.string.add_to_favorites);
            menu.add(1, USE_AS_RINGTONE, 0, R.string.ringtone_menu);
            menu.add(1, DELETE_ITEM, 0, R.string.delete_item);
            menu.add(1, SHARE_ITEM, 0, R.string.share_item);
            mSelectedPosition = fileIndex;
            mSelectedId = Long.parseLong(fetchedFiles.get(mSelectedPosition).get(SONG_ID));
        }
    }


    public Drawable getDrawable() {
        return (v.findViewById(R.id.firstbg)).getBackground();
    }

    public void setViewColor(int color) {
        v.findViewById(R.id.firstbg).setBackgroundColor(color);
    }
}
