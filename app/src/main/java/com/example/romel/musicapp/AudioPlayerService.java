package com.example.romel.musicapp;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.File;

import static com.example.romel.musicapp.MainMenuActivity.MMAInstance;
import static com.example.romel.musicapp.MainMenuActivity.mpm;

public class AudioPlayerService extends Service {

    Button currentSongButton;

    public AudioPlayerService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("Service", "Started");
        Bundle bundle = mpm.getMediaBundle();
        String songLoc = bundle.getString("songLoc");
        Uri songUri = Uri.fromFile(new File(songLoc));
        mpm.setMP(MediaPlayer.create(AudioPlayerService.this, songUri));
        mpm.start();
        currentSongButton = (Button) MMAInstance.findViewById(R.id.currentSongButton);
        currentSongButton.setVisibility(View.VISIBLE);
        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        currentSongButton.setVisibility(View.GONE);
        mpm.stop();
        mpm.release();
        Log.i("Service", "Ended");
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
