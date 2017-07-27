package com.example.romel.musicapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import static com.example.romel.musicapp.MainMenuActivity.mpm;
import static java.util.Collections.sort;

public class PlaylistMasterListActivity extends AppCompatActivity {

    private static final int MY_PERMISSION_REQUEST = 1;

    ArrayList<String> stringPlaylists;
    HashMap<String, ArrayList<String>> masterPlaylistMap =
            mpm.getMasterPlaylistMap();

    ListView playlistListView;
    EditText newPlaylistEditText;
    Button addPlaylistButton;
    Button deletePlaylistButton;
    Button newPlaylistEnterButton;
    String newPlaylistName;
    RelativeLayout addPlaylistLayout;
    Button cancelButton;
    Button finalDeleteButton;
    LinearLayout finalDeleteCancelLayout;
    boolean deleteMode = false;
    ArrayList<Integer> playlistsToDelete;

    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlists);
        newPlaylistEditText = (EditText) findViewById(R.id.playlists_edittext);
        newPlaylistEnterButton = (Button) findViewById(R.id.playlists_button);
        addPlaylistButton = (Button) findViewById(R.id.add_playlist_button);
        deletePlaylistButton = (Button) findViewById(R.id.delete_playlist_button);
        cancelButton = (Button) findViewById(R.id.cancel_button);
        addPlaylistLayout = (RelativeLayout) findViewById(R.id.add_playlist_layout);
        finalDeleteButton = (Button) findViewById(R.id.final_delete_button);
        finalDeleteCancelLayout = (LinearLayout) findViewById(R.id.finalDelete_cancel_layout);

        addPlaylistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPlaylistLayout.setVisibility(View.VISIBLE);
                addPlaylistButton.setVisibility(View.GONE);
                deletePlaylistButton.setVisibility(View.GONE);
                cancelButton.setVisibility(View.VISIBLE);
                finalDeleteButton.setVisibility(View.GONE);
                playlistListView.setEnabled(false);
            }
        });

        newPlaylistEnterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newPlaylistName = newPlaylistEditText.getText().toString();
                addPlaylistLayout.setVisibility(View.GONE);
                cancelButton.setVisibility(View.GONE);
                addPlaylistButton.setVisibility(View.VISIBLE);
                deletePlaylistButton.setVisibility(View.VISIBLE);
                stringPlaylists.add(newPlaylistName);
                masterPlaylistMap.put(newPlaylistName, new ArrayList<String>());
                sort(stringPlaylists);
                adapter.notifyDataSetChanged();
                deletePlaylistButton.setEnabled(true);
                playlistListView.setEnabled(true);
            }
        });

        deletePlaylistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteMode = true;
                finalDeleteButton.setVisibility(View.VISIBLE);
                finalDeleteButton.setEnabled(false);
                cancelButton.setVisibility(View.VISIBLE);
                playlistListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                playlistsToDelete = new ArrayList<>();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteMode = false;
                if (addPlaylistLayout.getVisibility() == View.VISIBLE) {
                    addPlaylistLayout.setVisibility(View.GONE);
                    cancelButton.setVisibility(View.GONE);
                    addPlaylistButton.setVisibility(View.VISIBLE);
                    deletePlaylistButton.setVisibility(View.VISIBLE);
                }
                else {
                    cancelButton.setVisibility(View.GONE);
                    finalDeleteButton.setVisibility(View.GONE);
                    addPlaylistButton.setVisibility(View.VISIBLE);
                    deletePlaylistButton.setVisibility(View.VISIBLE);
                    for (int i = 0; i < playlistsToDelete.size(); i++) {
                        playlistListView.getChildAt(playlistsToDelete.get(i).intValue())
                                .setBackgroundColor(Color.WHITE);
                    }
                    playlistListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                }
            }
        });

        finalDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelButton.setVisibility(View.GONE);
                finalDeleteButton.setVisibility(View.GONE);
                addPlaylistButton.setVisibility(View.VISIBLE);
                deletePlaylistButton.setVisibility(View.VISIBLE);
                deleteMode = false;
                playlistListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                Collections.sort(playlistsToDelete, Collections.<Integer>reverseOrder());
                for (int i = 0; i < playlistsToDelete.size(); i++) {
                    playlistListView.getChildAt(playlistsToDelete.get(i).intValue())
                            .setBackgroundColor(Color.WHITE);
                    masterPlaylistMap.remove(stringPlaylists.get(playlistsToDelete.get(i).intValue()));
                    stringPlaylists.remove(playlistsToDelete.get(i).intValue());
                }
                adapter.notifyDataSetChanged();
                if (stringPlaylists.size() == 0) {
                    deletePlaylistButton.setEnabled(false);
                }
                else deletePlaylistButton.setEnabled(true);
            }
        });

        if (ContextCompat.checkSelfPermission(PlaylistMasterListActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(PlaylistMasterListActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(PlaylistMasterListActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);
            }
            else {
                ActivityCompat.requestPermissions(PlaylistMasterListActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);
            }
        }

        else {
            getPlaylists();
        }

    }

    public void getPlaylists() {
        playlistListView = (ListView) findViewById(R.id.playlists_listview);

        stringPlaylists = mpm.getPlaylistNames();
        if (stringPlaylists.size() == 0) {
            deletePlaylistButton.setEnabled(false);
        }
        sort(stringPlaylists);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, stringPlaylists);
        playlistListView.setAdapter(adapter);
        playlistListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                //handle deletion
                if (deleteMode == true) {
                    finalDeleteButton.setEnabled(true);
                    playlistListView.getChildAt(position).setBackgroundColor(Color.BLUE);
                    playlistsToDelete.add(position);
                }

                //handle entering playlist
                else if (deleteMode == false) {
                    Intent intent = new Intent(PlaylistMasterListActivity.this, SongsActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("Value", "Playlist");
                    bundle.putString("Playlist", stringPlaylists.get(position));
                    mpm.setMediaBundle(bundle);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }

            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(PlaylistMasterListActivity.this,
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
