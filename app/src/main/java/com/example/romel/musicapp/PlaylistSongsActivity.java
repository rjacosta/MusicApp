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

public class PlaylistSongsActivity extends AppCompatActivity {

    private static final int MY_PERMISSION_REQUEST = 1;

    ArrayList<String> songs;
    ArrayList<String> songLocations;

    ListView songListView;
    ArrayAdapter<String> adapter;

    LinearLayout addDeleteSongsLayout;
    Button addSongsButton;
    Button deleteSongsButton;
    boolean deleteMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_songs);

        addDeleteSongsLayout = (LinearLayout) findViewById(R.id.add_delete_songs_layout);
        addSongsButton = (Button) findViewById(R.id.add_songs_button);
        deleteSongsButton = (Button) findViewById(R.id.delete_songs_button);


        addSongsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlaylistSongsActivity.this, AddSongsToPlaylistActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("Playlist Name", mpm.getMediaBundle().getString("Playlist Name"));
                mpm.setMediaBundle(bundle);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        deleteSongsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                songListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                deleteMode = true;
            }
        });


        if (ContextCompat.checkSelfPermission(PlaylistSongsActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(PlaylistSongsActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(PlaylistSongsActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);
            } else {
                ActivityCompat.requestPermissions(PlaylistSongsActivity.this,
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
                if (mpm.getSongIntent() != null) stopService(mpm.getSongIntent());
                Intent intent = new Intent(PlaylistSongsActivity.this, AudioPlayer.class);
                Bundle bundle = new Bundle();
                bundle.putString("songLoc", songLocations.get(position));
                mpm.setMediaBundle(bundle);
                intent.putExtras(bundle);
                startActivity(intent);
                Intent intentService = new Intent(PlaylistSongsActivity.this, AudioPlayerService.class);
                intentService.putExtras(bundle);
                mpm.setSongIntent(intentService);
                startService(intentService);

            }
        });
    }

    public void getMusic() {
        String specifiedValue = mpm.getMediaBundle().getString("Value");

        if (specifiedValue.equals("All Songs")) {
            getAllSongs();
        } else if (specifiedValue.equals("Artist")) {
            getSpecifiedArtistsSongs(mpm.getMediaBundle().getString("Artist"));
        } else if (specifiedValue.equals("Album")) {
            getSpecifiedAlbumsSongs(mpm.getMediaBundle().getString("Album"));
        } else if (specifiedValue.equals("Playlist")) {
            String specifiedPlaylistName = mpm.getMediaBundle().getString("Playlist Name");
            if (mpm.getMediaBundle().getString(specifiedPlaylistName) != null && mpm.getMediaBundle().getString(specifiedPlaylistName).equals("Add Songs")) {
                addSongsToPlaylist(specifiedPlaylistName);
            } else {
                addDeleteSongsLayout.setVisibility(View.VISIBLE);
                addSongsButton.setVisibility(View.VISIBLE);
                deleteSongsButton.setVisibility(View.VISIBLE);
                getSpecifiedPlaylistSongs(mpm.getMediaBundle().getString("Playlist Name"));
            }
        }
    }

    public void addSongsToPlaylist(String playlistName) {
        ArrayList<String> playlist = mpm.getMasterPlaylistMap().get(playlistName);
        getAllSongs();
        songListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        addDeleteSongsLayout.setVisibility(View.VISIBLE);
        addSongsButton.setVisibility(View.GONE);
        deleteSongsButton.setVisibility(View.GONE);
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

    public void getSpecifiedArtistsSongs(String artist) {
        ContentResolver cR = getContentResolver();
        Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor songCursor = cR.query(songUri, null, null, null, null);

        if (songCursor != null && songCursor.moveToFirst()) {
            int songTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int songArtist = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int songLocation = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA);

            do {
                String currentArtist = songCursor.getString(songArtist);
                if (currentArtist.equals(artist)) {
                    String currentTitle = songCursor.getString(songTitle);
                    String currentLocation = songCursor.getString(songLocation);
                    songs.add(currentTitle + "\n" + currentArtist);
                    songLocations.add(currentLocation);
                }
            } while (songCursor.moveToNext());
        }
    }

    public void getSpecifiedAlbumsSongs(String album) {
        ContentResolver cR = getContentResolver();
        Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor songCursor = cR.query(songUri, null, null, null, null);

        if (songCursor != null && songCursor.moveToFirst()) {
            int songTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int songArtist = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int songLocation = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            int songAlbum = songCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);

            do {
                String currentAlbum = songCursor.getString(songAlbum);
                if (currentAlbum.equals(album)) {
                    String currentArtist = songCursor.getString(songArtist);
                    String currentTitle = songCursor.getString(songTitle);
                    String currentLocation = songCursor.getString(songLocation);
                    songs.add(currentTitle + "\n" + currentArtist);
                    songLocations.add(currentLocation);
                }
            } while (songCursor.moveToNext());
        }
    }

    public void getSpecifiedPlaylistSongs(String playlistName) {
        ArrayList<String> playlist = mpm.getMasterPlaylistMap().get(playlistName);
        Uri songUri;
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        for (int i = 0; i < playlist.size(); i++) {
            songUri = Uri.fromFile(new File(playlist.get(i)));
            mmr.setDataSource(this, songUri);
            String songTitle = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
            String songArtist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
            songs.add(songTitle + "\n" + songArtist);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(PlaylistSongsActivity.this,
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