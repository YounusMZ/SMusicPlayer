package com.example.smusicplayer;

import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationChannelCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.graphics.drawable.IconCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.IBinder;
import android.widget.Button;
import android.widget.TextView;


import com.example.smusicplayer.Adapter.FileListAdapter;
import com.example.smusicplayer.Adapter.FolderListAdapter;
import com.example.smusicplayer.Service.MediaService;
import com.example.smusicplayer.databinding.ActivityMainBinding;


import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private final MediaPlayer media = new MediaPlayer();
    private MediaService mediaService;
    private String mediaLocation;
    private boolean serviceBound = false;

    private FileListAdapter fileListAdapter;
    private NotificationCompat.Builder notification;
    private NotificationManagerCompat notificationManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        FileFolderListInit fileFolderListInit = new FileFolderListInit();
        fileFolderListInit.setRootDir(this.getFilesDir());

        RecyclerView folderListView;
        RecyclerView fileListView;
        FolderListAdapter folderListAdapter;

        folderListAdapter = new FolderListAdapter(this,this, fileFolderListInit.getFolderList(), fileFolderListInit.getFileList());
        folderListView = findViewById(R.id.folderList);
        folderListView.setLayoutManager(new LinearLayoutManager(this));
        folderListView.setItemAnimator(new DefaultItemAnimator());
        folderListView.setAdapter(folderListAdapter);

        fileListAdapter = new FileListAdapter(this, this, fileFolderListInit.getFileList());
        fileListView = findViewById(R.id.fileList);
        fileListView.setLayoutManager(new LinearLayoutManager(this));
        fileListView.setItemAnimator(new DefaultItemAnimator());
        fileListView.setAdapter(fileListAdapter);


        addButtonOnClick(folderListAdapter, fileFolderListInit.getFolderList(), fileFolderListInit);
        playButtonOnClick();
        resetButtonOnClick();
        notificationSetUp();
    }


    public String getMediaLocation(){
        return this.mediaLocation;
    }

    public void setMediaLocation(String newMediaLocation){
        this.mediaLocation =  newMediaLocation;
    }

    public FileListAdapter getFileListAdapter(){
        return this.fileListAdapter;
    }

    private void addButtonOnClick(FolderListAdapter adapter, ArrayList<String> folderList, FileFolderListInit fileFolderListInit){
        Button addButton = findViewById(R.id.add_button);
        TextView folderName = findViewById(R.id.folderName);

        addButton.setOnClickListener(view -> {
            folderList.add(folderName.getText().toString());
            adapter.notifyItemInserted(folderList.size() - 1);
            fileFolderListInit.writeFolderListToStorage();
        });
    }

    private void playButtonOnClick(){
        Button playButton = findViewById(R.id.playbutton);
        playButton.setOnClickListener(view -> playPauseAudio());
    }

    private void resetButtonOnClick(){
        Button playButton = findViewById(R.id.resetbutton);
        playButton.setOnClickListener(view -> resetAudio());
    }

    private void notificationSetUp(){
        String description = "Music Player -";
        String channelID = "0";

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            int importanceLevel = NotificationManager.IMPORTANCE_LOW;

            NotificationChannelCompat channel = new NotificationChannelCompat.Builder(channelID, importanceLevel)
                    .setDescription(description)
                    .setName("AudioPlayerService")
                    .build();
            notificationManager = NotificationManagerCompat.from(getApplicationContext());
            notificationManager.createNotificationChannel(channel);
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            notification = new NotificationCompat.Builder(this, channelID)
                    .setContentTitle(mediaLocation)
                    .setSmallIcon(IconCompat.createWithResource(this, R.drawable.ic_launcher));

        }
        else{
            notification = new NotificationCompat.Builder(this, channelID)
                    .setContentTitle(mediaLocation);

        }
        notificationManager.notify(Integer.parseInt(channelID), notification.build());
    }



    private final ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {

            MediaService.LocalBinder binder = (MediaService.LocalBinder) iBinder;
            mediaService = binder.getService();
            serviceBound = true;
            startAudio();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            serviceBound = false;
        }
    };


    public void initAudio() {
        if (!serviceBound) {
            Intent playerIntent = new Intent(this, MediaService.class);
            startService(playerIntent);
            bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        }
        else{
            if(media.isPlaying()) {
                resetAudio();
            }
            startAudio();
        }
    }

    private void startAudio(){
        try {
            mediaService.Play(mediaLocation, media);
            notification.setContentTitle("Playing" + FileListAdapter.getFileName(mediaLocation));
            notificationManager.notify(0, notification.build());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void playPauseAudio(){
        TextView playPauseButton = findViewById(R.id.playbutton);

        if (mediaService != null) {

        if(media.isPlaying()){
            playPauseButton.setText(R.string.Play);
        }
        else{
            playPauseButton.setText(R.string.Pause);
        }

        mediaService.Pause(media);
        }
    }

    private void resetAudio(){
        if(mediaService != null) {
            if (mediaService.getIsMediaSet()) {
                mediaService.Reset(media);
                notification.setContentTitle("");
                notificationManager.notify(0, notification.build());
            }

            TextView nowPlaying = findViewById(R.id.now_playing);
            TextView playPauseButton = findViewById(R.id.playbutton);

            nowPlaying.setText(R.string.Now_playing);
            playPauseButton.setText(R.string.Pause);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putBoolean("ServiceState", serviceBound);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        serviceBound = savedInstanceState.getBoolean("ServiceState");
    }

    @Override
    protected void onDestroy() {
        notificationManager.cancelAll();
        if (serviceBound) {
            unbindService(serviceConnection);
            mediaService.stopSelf();
        }

        super.onDestroy();
    }
}