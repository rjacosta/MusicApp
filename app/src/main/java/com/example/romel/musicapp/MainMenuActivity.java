package com.example.romel.musicapp;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainMenuActivity extends AppCompatActivity {

    private Button songsButton;
    private Button playlistsButton;
    private Button artistsButton;
    private Button albumsButton;
    private Button currentSongButton;
    static MediaPlayerMaster mpm;
    public static MainMenuActivity MMAInstance;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        MMAInstance = this;

        songsButton = (Button) findViewById(R.id.allSongsButton);
        songsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainMenuActivity.this, SongsActivity.class);
                startActivity(intent);
            }
        });

        playlistsButton = (Button) findViewById(R.id.allPlaylistsButton);
        playlistsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainMenuActivity.this, PlaylistsActivity.class);
                startActivity(intent);
            }
        });

        artistsButton = (Button) findViewById(R.id.allArtistsButton);
        artistsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainMenuActivity.this, ArtistsActivity.class);
                startActivity(intent);
            }
        });

        albumsButton = (Button) findViewById(R.id.allAlbumsButton);
        albumsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainMenuActivity.this, AlbumsActivity.class);
                startActivity(intent);
            }
        });

        currentSongButton = (Button) findViewById(R.id.currentSongButton);
        currentSongButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainMenuActivity.this, AudioPlayer.class);
                startActivity(intent);
            }
        });

        mpm = new MediaPlayerMaster();
    }

}
