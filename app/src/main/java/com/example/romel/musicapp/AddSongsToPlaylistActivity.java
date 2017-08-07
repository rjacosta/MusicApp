package com.example.romel.musicapp;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import android.Manifest;

import java.io.File;
import java.util.ArrayList;

import static com.example.romel.musicapp.MainMenuActivity.mpm;

public class AddSongsToPlaylistActivity extends AppCompatActivity {

    private static final int MY_PERMISSION_REQUEST = 1;

    ArrayList<String> songs;
    ArrayList<String> songLocations;

    ListView songListView;
    ArrayAdapter<String> adapter;

    Button doneAddingSongsButton;
    Button cancelAddingSongsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_songs);

        doneAddingSongsButton = (Button) findViewById(R.id.done_adding_songs_button);
        cancelAddingSongsButton = (Button) findViewById(R.id.cancel_adding_songs_button);

        doneAddingSongsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doneAddingSongsButton.setVisibility(View.GONE);
                cancelAddingSongsButton.setVisibility(View.GONE);
            }
        });

        cancelAddingSongsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doneAddingSongsButton.setVisibility(View.GONE);
                cancelAddingSongsButton.setVisibility(View.GONE);
            }
        });

        if (ContextCompat.checkSelfPermission(AddSongsToPlaylistActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(AddSongsToPlaylistActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(AddSongsToPlaylistActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);
            } else {
                ActivityCompat.requestPermissions(AddSongsToPlaylistActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);
            }
        } else {
            getSongs();
        }

    }

    public void getSongs() {
        songListView = (ListView) findViewById(R.id.songs_listview);
        songs = new ArrayList<>();
        songLocations = new ArrayList<>();
        getMusic();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, songs);
        songListView.setAdapter(adapter);
        songListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                //do something about highlihgting songs here
            }
        });
    }

    public void getMusic() {

        String specifiedPlaylistName = mpm.getMediaBundle().getString("Playlist Name");
        addSongsToPlaylist(specifiedPlaylistName);

    }

    public void addSongsToPlaylist(String playlistName) {
        ArrayList<String> playlist = mpm.getMasterPlaylistMap().get(playlistName);
        getAllSongs();
        songListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        doneAddingSongsButton.setVisibility(View.VISIBLE);
        cancelAddingSongsButton.setVisibility(View.VISIBLE);
    }

    public void getAllSongs() {
        ContentResolver cR = getContentResolver();
        Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor songCursor = cR.query(songUri, null, null, null, null);

        if (songCursor != null && songCursor.moveToFirst()) {
            int songTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int songArtist = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int songLocation = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA);

            do {
                String currentArtist = songCursor.getString(songArtist);
                String currentTitle = songCursor.getString(songTitle);
                String currentLocation = songCursor.getString(songLocation);
                songs.add(currentTitle + "\n" + currentArtist);
                songLocations.add(currentLocation);
            } while (songCursor.moveToNext());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(AddSongsToPlaylistActivity.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "Permission Granted!", Toast.LENGTH_SHORT).show();
                        getSongs();
                    }
                } else {
                    Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show();
                    finish();
                }
                return;
            }
        }
    }
}