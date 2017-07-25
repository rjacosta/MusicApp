package com.example.romel.musicapp;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;

import java.io.IOException;

/**
 * Created by romel on 7/23/2017.
 */

public class MediaPlayerMaster {

    static MediaPlayer mp = null;
    static Bundle mediaBundle = null;
    static Intent songIntent = null;

    public MediaPlayer getMP() {
        return mp;
    }

    public void setMP(MediaPlayer m) {
        mp = m;
    }

    public Bundle getMediaBundle() { return mediaBundle; }

    public void setMediaBundle(Bundle b) { mediaBundle = b; }

    public Intent getSongIntent() { return songIntent; }

    public void setSongIntent(Intent i) { songIntent = i; }

    public void start() {
        mp.start();
    }

    public void pause() {
        mp.pause();
    }

    public void stop() {
        mp.stop();
    }

    public void release() {
        mp.release();
    }


    public boolean isPlaying() {
        return mp.isPlaying();
    }

    public int getCurrentPosition() {
        return mp.getCurrentPosition();
    }

    public int getDuration() {
        return mp.getDuration();
    }

    public void seekTo(int msec) {
        mp.seekTo(msec);
    }

}
