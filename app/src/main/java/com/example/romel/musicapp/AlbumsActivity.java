package com.example.romel.musicapp;

import android.Manifest;
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

import java.util.ArrayList;

import static com.example.romel.musicapp.MainMenuActivity.mpm;
import static java.util.Collections.sort;

public class AlbumsActivity extends AppCompatActivity {
    private static final int MY_PERMISSION_REQUEST = 1;

    ArrayList<String> albums;

    ListView albumListView;

    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_albums);

        if (ContextCompat.checkSelfPermission(AlbumsActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(AlbumsActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(AlbumsActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);
            }
            else {
                ActivityCompat.requestPermissions(AlbumsActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);
            }
        }

        else {
            getMusic();
        }

    }

    public void getMusic() {
        albumListView = (ListView) findViewById(R.id.albums_listview);
        albums = new ArrayList<>();

        getAlbums();
        sort(albums);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, albums);
        albumListView.setAdapter(adapter);
        albumListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent = new Intent(AlbumsActivity.this, SongsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("Value", "Album");
                bundle.putString("Album", albums.get(position));
                mpm.setMediaBundle(bundle);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    public void getAlbums() {
        ContentResolver cR = getContentResolver();
        Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor songCursor = cR.query(songUri, null, null, null, null);
        ArrayList<String> usedAlbums = new ArrayList<>();
        if (songCursor != null && songCursor.moveToFirst()) {
            int songAlbum = songCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);

            do {
                String currentAlbum = songCursor.getString(songAlbum);
                if (!usedAlbums.contains(currentAlbum)) {
                    albums.add(currentAlbum);
                    usedAlbums.add(currentAlbum);
                }
            } while (songCursor.moveToNext());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(AlbumsActivity.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "Permission Granted!", Toast.LENGTH_SHORT).show();
                        getMusic();
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