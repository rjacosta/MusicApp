package com.example.romel.musicapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.util.Log;

import java.io.File;

import static com.example.romel.musicapp.MainMenuActivity.MMAInstance;
import static com.example.romel.musicapp.MainMenuActivity.mpm;

public class AudioPlayer extends AppCompatActivity {

    public String msConverter(int ms) {

        if (ms == 0) return "00:00";

        int minutes = 0;
        int seconds = 0;
        String time = "";
        seconds = ms / 1000;
        while (seconds >= 60) {
            minutes++;
            seconds -= 60;
        }

        if(minutes < 10) time += "0" + minutes;
        else time += minutes;

        time += ":";

        if (seconds < 10) time += "0" + seconds;
        else time += seconds;

        return time;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_player);
        final Button playPauseButton = (Button) findViewById(R.id.play_pause_button);
        final SeekBar seekBar = (SeekBar) findViewById(R.id.seek_bar);
        final TextView seekBarProgress = (TextView) findViewById(R.id.seek_bar_progress);
        final Button currentSongButton = (Button) MMAInstance.findViewById(R.id.currentSongButton);
        TextView songAndAlbumText = (TextView) findViewById(R.id.song_album_text);

        if (!mpm.isPlaying()) playPauseButton.setText("Play");

        Bundle bundle = mpm.getMediaBundle();
        String songLoc = bundle.getString("songLoc");
        Uri songUri = Uri.fromFile(new File(songLoc));
        MediaMetadataRetriever mR = new MediaMetadataRetriever();
        mR.setDataSource(AudioPlayer.this, songUri);
        String title = mR.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
        String artist = mR.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
        String genre = mR.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE);
        String duration = mR.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        String album = mR.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
        if (title != null) Log.i("Title", title);
        if (artist != null) Log.i("Artist", artist);
        if (genre != null) Log.i("Genre", genre);
        if (duration != null) Log.i("Duration", duration);
        if (album != null) Log.i("Album", album);
        byte[] albumArt = mR.getEmbeddedPicture();
        mR.release();
        if (albumArt != null) {
            Bitmap b = BitmapFactory.decodeByteArray(albumArt, 0, albumArt.length);
            Bitmap mutableBitmap = b.copy(Bitmap.Config.ARGB_8888, true);
            mutableBitmap.createScaledBitmap(mutableBitmap, 325, 325, true);
            ImageView iV = (ImageView) findViewById(R.id.album_art);
            iV.setImageBitmap(mutableBitmap);
        }
        if (title == null) title = "Unknown Title";
        if (artist == null) artist = "Unknown Artist";
        songAndAlbumText.setText(title + ", by " + artist);
        final Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(500);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                int currProgress;
                                if (mpm.getDuration() != 0) {
                                    currProgress = mpm.getCurrentPosition() * 100 / mpm.getDuration();
                                }
                                else currProgress = 100;
                                seekBar.setProgress(currProgress);
                                seekBarProgress.setText(msConverter(mpm.getCurrentPosition()) + "");
                            }
                        });
                    }
                } catch (InterruptedException e) {}
            }
        };

        mpm.getMP().setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                playPauseButton.setText("Play");
                Button currentSongButton = (Button) MMAInstance.findViewById(R.id.currentSongButton);
                currentSongButton.setVisibility(View.GONE);

            }
        });
        playPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentSongButton.setVisibility(View.VISIBLE);
                if (mpm.isPlaying()) {
                    playPauseButton.setText("Play");
                    mpm.pause();
                }
                else {
                    playPauseButton.setText("Pause");
                    mpm.start();
                }

            }
        });

        seekBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {

                    int progress_val;
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        boolean isPaused = !mpm.isPlaying(); //checks if song is already paused
                        progress_val = progress;
                        if (fromUser) {
                            mpm.pause();
                            mpm.seekTo(progress_val * mpm.getDuration() / 100);
                            seekBarProgress.setText(msConverter(mpm.getCurrentPosition()) + "");

                            //when the song has stopped playing and the user wants to start again
                            //at a desired point
                            if (mpm.getCurrentPosition() == mpm.getDuration()) mpm.start();
                            else if (!isPaused) mpm.start();
                        }
                    }
                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {}
                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {}
                }

        );

        t.start();
    }

}
