package com.example.smusicplayer.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.smusicplayer.MainActivity;
import com.example.smusicplayer.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class FolderListAdapter extends RecyclerView.Adapter<FolderListAdapter.FolderListVIewHolder> {

    private final List<String> folderListData;
    private final List<String> fileListData;
    private final MainActivity activity;

    public FolderListAdapter(MainActivity activity, List<String> folderList, List<String> fileList){
        this.activity = activity;
        this.fileListData = fileList;
        this.folderListData = folderList;
    }


    @NonNull
    @Override
    public FolderListVIewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.folderlist, parent, false);
        return new FolderListVIewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FolderListVIewHolder holder, int position) {
        holder.showFolder.setText(folderListData.get(position));

        holder.showFolder.setOnClickListener(view -> {
            String fileDirString = holder.showFolder.getText().toString();
            File fileDir = new File(fileDirString);

            int fileListLength = fileListData.size();
            fileListData.clear();
            activity.getFileListAdapter().notifyItemRangeRemoved(0, fileListLength);

            if(fileDir.exists()){
                File[] files = fileDir.listFiles();
                if (files != null){
                    for (File file : files){
                        if(file.isFile() && file.toString().endsWith(".mp3")){
                            fileListData.add(file.toString());
                        }
                    }
                }
            }
            activity.getFileListAdapter().notifyItemRangeInserted(0, fileListData.size());
        });
    }


    @Override
    public int getItemCount() {
        return folderListData == null ? 0 : folderListData.size();
    }




    public static class FolderListVIewHolder extends RecyclerView.ViewHolder{
        public  TextView showFolder;
        public TextView showFile;

        public FolderListVIewHolder(View itemView) {
            super(itemView);
            this.showFolder = itemView.findViewById(R.id.showFolder);
            this.showFile = itemView.findViewById(R.id.showFile);
        }
    }
}
