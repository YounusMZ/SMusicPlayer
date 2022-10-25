package com.example.smusicplayer.Service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.os.Binder;

import java.io.IOException;

public class MediaService extends Service {

    private Boolean isMediaSet = false;
    private final IBinder ibinder = new LocalBinder();

    @Override
    public IBinder onBind(Intent intent){
        return ibinder;
    }

    public class LocalBinder extends Binder {
        public MediaService getService(){
            return MediaService.this;
        }
    }


    public void Play(String loc, MediaPlayer media) throws IOException {
        media.setDataSource(loc);
        media.prepare();
        media.start();
        isMediaSet = true;
    }

    public void Pause(MediaPlayer media){
        if(media.isPlaying()){
            media.pause();
        }
        else{
            media.start();
        }
    }

    public void Reset(MediaPlayer media){
        media.reset();
        isMediaSet = false;
    }

    public Boolean getIsMediaSet(){
        return this.isMediaSet;
    }
}
