package com.example.romel.musicapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static com.example.romel.musicapp.MainMenuActivity.mpm;
import static java.util.Collections.sort;

public class PlaylistsActivity extends AppCompatActivity {

    private static final int MY_PERMISSION_REQUEST = 1;

    ArrayList<String> playlists;

    ListView playlistListView;
    EditText newPlaylistEditText;
    Button newPlaylistEnterButton;
    String newPlaylistName;
    RelativeLayout playlistLayout;

    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlists);
        newPlaylistEditText = (EditText) findViewById(R.id.playlists_edittext);
        newPlaylistEnterButton = (Button) findViewById(R.id.playlists_button);

        newPlaylistEnterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newPlaylistName = newPlaylistEditText.getText().toString();
                playlists.add(newPlaylistName);
                adapter.notifyDataSetChanged();
                newPlaylistEditText.setVisibility(View.GONE);
                newPlaylistEnterButton.setVisibility(View.GONE);
                playlistLayout.setVisibility(View.GONE);
            }
        });

        playlistLayout = (RelativeLayout) findViewById(R.id.playlist_layout);

        if (ContextCompat.checkSelfPermission(PlaylistsActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(PlaylistsActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(PlaylistsActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);
            }
            else {
                ActivityCompat.requestPermissions(PlaylistsActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);
            }
        }

        else {
            getPlaylists();
        }

    }

    public void getPlaylists() {
        playlistListView = (ListView) findViewById(R.id.playlists_listview);

        playlists = mpm.getPlaylists();

        if (playlists.size() == 0) {
            playlists.add("+ Add Playlist");
        }
        sort(playlists);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, playlists);
        playlistListView.setAdapter(adapter);
        playlistListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (id == 0) {
                    playlistLayout.setVisibility(View.VISIBLE);
                    newPlaylistEditText.setVisibility(View.VISIBLE);
                    newPlaylistEnterButton.setVisibility(View.VISIBLE);
                    newPlaylistEditText.bringToFront();
                    newPlaylistEnterButton.bringToFront();
                }
                /*
                Intent intent = new Intent(PlaylistsActivity.this, SongsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("Value", "Playlist");
                bundle.putString("Playlist", playlists.get(position));
                mpm.setMediaBundle(bundle);
                intent.putExtras(bundle);
                startActivity(intent);
                */
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(PlaylistsActivity.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "Permission Granted!", Toast.LENGTH_SHORT).show();
                        getPlaylists();
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
