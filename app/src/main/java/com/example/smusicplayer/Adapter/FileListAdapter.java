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

import java.util.ArrayList;
import java.util.List;

public class FileListAdapter extends RecyclerView.Adapter<FileListAdapter.FileViewHolder> {
    private final ArrayList<String> fileList;
    private final MainActivity activity;
    private final LayoutInflater layoutInflater;

    public FileListAdapter(MainActivity activity,Context context, ArrayList<String> fileListString){
        this.activity = activity;
        this.fileList = fileListString;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public FileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.filelist, parent, false);
        return new FileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FileViewHolder holder, int position) {
        String fileNamePositionDefined = fileList.get(position);
        holder.fileCollection.setText(getFileName(fileNamePositionDefined));

        holder.fileCollection.setOnClickListener(view -> {
            String fileName = fileList.get(position);
            TextView nowPlaying = activity.findViewById(R.id.now_playing);

            activity.setMediaLocation(fileName);
            activity.initAudio();
            nowPlaying.setText(getFileName(activity.getMediaLocation()));
        });
    }

    @Override
    public int getItemCount() {
        return fileList == null ? 0 : fileList.size();
    }

    public static String getFileName(String fileName){
        List<Integer> slashPosition = new ArrayList<>();

        for(int index = 0; index < fileName.length(); index++){
            if(fileName.charAt(index) == '/'){
                slashPosition.add(index);
            }
        }
        return fileName.substring(slashPosition.get(slashPosition.size() - 1));
    }


    public static class FileViewHolder extends RecyclerView.ViewHolder{
        public TextView fileCollection;
        public FileViewHolder(@NonNull View itemView) {
            super(itemView);
            fileCollection = itemView.findViewById(R.id.showFile);
        }
    }
}
