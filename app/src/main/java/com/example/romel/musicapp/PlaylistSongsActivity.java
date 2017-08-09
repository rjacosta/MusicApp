package com.example.romel.musicapp;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
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
import java.util.Collections;

import static com.example.romel.musicapp.MainMenuActivity.mpm;

public class PlaylistSongsActivity extends AppCompatActivity {

    private static final int MY_PERMISSION_REQUEST = 1;
    private static final int OPEN_NEW_ACTIVITY = 2;

    String specifiedPlaylistName = mpm.getMediaBundle().getString("Playlist Name");
    ArrayList<String> specifiedPlaylistSongLocs = mpm.getMasterPlaylistMap().get(specifiedPlaylistName);

    ArrayList<String> songs;
    ArrayList<String> songLocations;

    ListView songListView;
    ArrayAdapter<String> adapter;

    LinearLayout addDeleteSongsLayout;
    Button addSongsButton;
    Button deleteSongsButton;
    Button doneDeletingSongsButton;
    Button cancelDeletingSongsButton;
    boolean deleteMode = false;
    ArrayList<Integer> songsToDeleteIndexes;

    int newSongIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_songs);

        songListView = (ListView) findViewById(R.id.songs_listview);
        songs = new ArrayList<>();
        songLocations = new ArrayList<>();
        songsToDeleteIndexes = new ArrayList<>();

        addDeleteSongsLayout = (LinearLayout) findViewById(R.id.add_delete_songs_layout);
        addSongsButton = (Button) findViewById(R.id.add_songs_button);
        deleteSongsButton = (Button) findViewById(R.id.delete_songs_button);
        doneDeletingSongsButton = (Button) findViewById(R.id.done_deleting_songs_button);
        cancelDeletingSongsButton = (Button) findViewById(R.id.cancel_deleting_songs_button);

        addSongsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlaylistSongsActivity.this, AddSongsToPlaylistActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("Playlist Name", specifiedPlaylistName);
                mpm.setMediaBundle(bundle);
                intent.putExtras(bundle);
                startActivityForResult(intent, OPEN_NEW_ACTIVITY);
            }
        });

        deleteSongsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                songListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                deleteMode = true;
                addSongsButton.setVisibility(View.GONE);
                deleteSongsButton.setVisibility(View.GONE);
                doneDeletingSongsButton.setVisibility(View.VISIBLE);
                cancelDeletingSongsButton.setVisibility(View.VISIBLE);
            }
        });

        doneDeletingSongsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                songListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                addSongsButton.setVisibility(View.VISIBLE);
                deleteSongsButton.setVisibility(View.VISIBLE);
                doneDeletingSongsButton.setVisibility(View.GONE);
                cancelDeletingSongsButton.setVisibility(View.GONE);

                Collections.sort(songsToDeleteIndexes, Collections.<Integer>reverseOrder());
                for (int i = 0; i < songsToDeleteIndexes.size(); i++) {
                    songListView.getChildAt(songsToDeleteIndexes.get(i).intValue())
                            .setBackgroundColor(Color.WHITE);
                    songs.remove(songsToDeleteIndexes.get(i).intValue());
                    specifiedPlaylistSongLocs.remove(songsToDeleteIndexes.get(i).intValue());
                }
                songsToDeleteIndexes.clear();
                adapter.notifyDataSetChanged();
                deleteMode = false;
                newSongIndex = songs.size();
                if (songs.size() == 0) deleteSongsButton.setEnabled(false);
            }
        });

        cancelDeletingSongsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                songListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                addSongsButton.setVisibility(View.VISIBLE);
                deleteSongsButton.setVisibility(View.VISIBLE);
                doneDeletingSongsButton.setVisibility(View.GONE);
                cancelDeletingSongsButton.setVisibility(View.GONE);
                deleteMode = false;
                for (int i = 0; i < songsToDeleteIndexes.size(); i++) {
                    songListView.getChildAt(songsToDeleteIndexes.get(i).intValue())
                            .setBackgroundColor(Color.WHITE);
                }
                songsToDeleteIndexes.clear();
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
            prepPlaylistSongs();
        }

    }

    public void prepPlaylistSongs() {

        getPlaylistSongs();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, songs);
        songListView.setAdapter(adapter);
        songListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (!deleteMode){
                    if (mpm.getSongIntent() != null) stopService(mpm.getSongIntent());
                    Intent intent = new Intent(PlaylistSongsActivity.this, AudioPlayer.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("songLoc", specifiedPlaylistSongLocs.get(position));
                    mpm.setMediaBundle(bundle);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    Intent intentService = new Intent(PlaylistSongsActivity.this, AudioPlayerService.class);
                    intentService.putExtras(bundle);
                    mpm.setSongIntent(intentService);
                    startService(intentService);
                }
                else if (deleteMode) {
                    songListView.getChildAt(position).setBackgroundColor(Color.BLUE);
                    songsToDeleteIndexes.add(position);
                }
            }
        });
    }

    public void getPlaylistSongs() {
        Uri songUri;
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        for (int i = newSongIndex; i < specifiedPlaylistSongLocs.size(); i++) {
            songUri = Uri.fromFile(new File(specifiedPlaylistSongLocs.get(i)));
            mmr.setDataSource(this, songUri);
            String songTitle = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
            String songArtist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
            songs.add(songTitle + "\n" + songArtist);
        }
        newSongIndex = specifiedPlaylistSongLocs.size();
        if (newSongIndex != 0) deleteSongsButton.setEnabled(true);
        else deleteSongsButton.setEnabled(false);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(PlaylistSongsActivity.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "Permission Granted!", Toast.LENGTH_SHORT).show();
                        prepPlaylistSongs();
                    }
                } else {
                    Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show();
                    finish();
                }
                return;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == OPEN_NEW_ACTIVITY) {
            if (songListView.getAdapter().getCount() < specifiedPlaylistSongLocs.size()) {
                getPlaylistSongs();
                adapter.notifyDataSetChanged();
            }
        }
    }
}