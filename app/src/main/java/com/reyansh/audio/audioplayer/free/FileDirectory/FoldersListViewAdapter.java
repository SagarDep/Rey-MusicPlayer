package com.reyansh.audio.audioplayer.free.FileDirectory;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.reyansh.audio.audioplayer.free.R;

import java.util.List;

public class FoldersListViewAdapter extends ArrayAdapter<String> {

    private Context mContext;
    private FragmentFolder mFragment;

    private int mItemType;
    private String mItemPath;
    private int mItemPosition;

    private List<String> mFileFolderNameList;
    private List<Integer> mFileFolderTypeList;
    private List<String> mFileFolderSizeList;
    private List<String> mFileFolderPathsList;
    //Handler.
    public FoldersListViewAdapter(Context context,
                                  FragmentFolder fragment,
                                  List<String> nameList,
                                  List<Integer> fileFolderTypeList,
                                  List<String> sizeList,
                                  List<String> fileFolderPathsList) {
        super(context, -1, nameList);
        mContext = context;
        mFragment = fragment;

        mFileFolderNameList = nameList;
        mFileFolderTypeList = fileFolderTypeList;
        mFileFolderSizeList = sizeList;
        mFileFolderPathsList = fileFolderPathsList;
    }
    public View getView(int position, View convertView, ViewGroup parent) {
        FoldersViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_view_item, parent, false);
            holder = new FoldersViewHolder();
            holder.fileFolderSizeText = (TextView) convertView.findViewById(R.id.listViewSubText);
            holder.fileFolderNameText = (TextView) convertView.findViewById(R.id.listViewTitleText);
            holder.rightSubText = (TextView) convertView.findViewById(R.id.listViewRightSubText);
            holder.rightSubText.setVisibility(View.INVISIBLE);
            convertView.setTag(holder);
        } else {
            holder = (FoldersViewHolder) convertView.getTag();
        }
        if (mFileFolderTypeList.get(position) == FragmentFolder.FOLDER) {
            convertView.setTag(R.string.folder_list_item_type, FragmentFolder.FOLDER);
        }
        holder.fileFolderNameText.setText(mFileFolderNameList.get(position));
        holder.fileFolderSizeText.setText(mFileFolderSizeList.get(position));

        //Set the icon based on whether the item is a folder or a file.
        if (mFileFolderTypeList.get(position) == FragmentFolder.FOLDER) {
            convertView.setTag(R.string.folder_list_item_type, FragmentFolder.FOLDER);
            convertView.setTag(R.string.folder_path, mFileFolderPathsList.get(position));
            convertView.setTag(R.string.position, position);

        } else if (mFileFolderTypeList.get(position) == FragmentFolder.AUDIO_FILE) {

            convertView.setTag(R.string.folder_list_item_type, FragmentFolder.AUDIO_FILE);
            convertView.setTag(R.string.folder_path, mFileFolderPathsList.get(position));
            convertView.setTag(R.string.position, position);

        } else if (mFileFolderTypeList.get(position) == FragmentFolder.PICTURE_FILE) {

            convertView.setTag(R.string.folder_list_item_type, FragmentFolder.PICTURE_FILE);
            convertView.setTag(R.string.folder_path, mFileFolderPathsList.get(position));
            convertView.setTag(R.string.position, position);

        } else if (mFileFolderTypeList.get(position) == FragmentFolder.VIDEO_FILE) {

            convertView.setTag(R.string.folder_list_item_type, FragmentFolder.VIDEO_FILE);
            convertView.setTag(R.string.folder_path, mFileFolderPathsList.get(position));
            convertView.setTag(R.string.position, position);

        } else {
            convertView.setTag(R.string.folder_list_item_type, FragmentFolder.FILE);
            convertView.setTag(R.string.folder_path, mFileFolderPathsList.get(position));
            convertView.setTag(R.string.position, position);
        }
        return convertView;
    }

    static class FoldersViewHolder {
        public TextView fileFolderNameText;
        public TextView fileFolderSizeText;
        public TextView rightSubText;
    }

}
