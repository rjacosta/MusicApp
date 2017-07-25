package com.example.romel.musicapp;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.Manifest;

import java.util.ArrayList;

import static com.example.romel.musicapp.MainMenuActivity.mpm;

public class SongsActivity extends AppCompatActivity {

    private static final int MY_PERMISSION_REQUEST = 1;

    ArrayList<String> songs;
    ArrayList<String> songLocations;

    ListView songListView;

    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_songs);

        if (ContextCompat.checkSelfPermission(SongsActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(SongsActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(SongsActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);
            }
            else {
                ActivityCompat.requestPermissions(SongsActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);
            }
        }

        else {
            doStuff();
        }

    }

    public void doStuff() {
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
                Intent intent = new Intent(SongsActivity.this, AudioPlayer.class);
                Bundle bundle = new Bundle();
                bundle.putString("songLoc", songLocations.get(position));
                mpm.setMediaBundle(bundle);
                intent.putExtras(bundle);
                startActivity(intent);
                Intent intentService = new Intent(SongsActivity.this, AudioPlayerService.class);
                intentService.putExtras(bundle);
                mpm.setSongIntent(intentService);
                startService(intentService);

            }
        });
    }

    public void getMusic() {
        ContentResolver cR = getContentResolver();
        Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor songCursor = cR.query(songUri, null, null, null, null);

        if (songCursor != null && songCursor.moveToFirst()) {
            int songTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int songArtist = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int songLocation = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA);

            do {
                String currentTitle = songCursor.getString(songTitle);
                String currentArtist = songCursor.getString(songArtist);
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
                    if (ContextCompat.checkSelfPermission(SongsActivity.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "Permission Granted!", Toast.LENGTH_SHORT).show();
                        doStuff();
                    }
                }
                else {
                    Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show();
                    finish();
                }
                return;
            }
        }
    }
}
