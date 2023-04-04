package com.example.take3;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadata;
import android.media.MediaMetadataRetriever;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.take3.PlayerActivity;


import com.google.android.material.tabs.TabLayout;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.*;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;
import kotlin.Metadata;

import java.io.File;
import java.lang.reflect.Array;
import java.util.*;

public class MainActivity extends AppCompatActivity  {
    ListView listView;
    String[] items;
    String[] names;
    ImageView albumCover;
    Bitmap[] bitmaps;
    byte[] art;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        albumCover = findViewById(R.id.imgsong);

        listView = findViewById(R.id.listViewSong);

        runtimePermission();
    }

    public void runtimePermission() {
        Dexter.withContext(this).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        displaySongs();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
    }

    public ArrayList<File> findSong (File file) {
        ArrayList<File> arrayList = new ArrayList<>();
        File[] files = file.listFiles();
        if (files != null) {
            for (File singleFile : files) {
                if (singleFile.isDirectory() && !singleFile.isHidden()) {
                    arrayList.addAll(findSong(singleFile));
                } else {
                    if (singleFile.getName().endsWith(".mp3") || singleFile.getName().endsWith(".flac")) {
                        arrayList.add(singleFile);
                    }
                }
            }
        }
        return arrayList;
    }

    public void displaySongs () {
        final ArrayList<File> mySongs = findSong(Environment.getExternalStorageDirectory());
        items = new String[mySongs.size()];
        names = new String[mySongs.size()];
        bitmaps = new Bitmap[mySongs.size()];
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        for (int i = 0; i < mySongs.size(); i++) {
            mmr.setDataSource(String.valueOf(mySongs.get(i)));
            items[i] = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
            names[i] = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
            art = mmr.getEmbeddedPicture();
            Bitmap songImage = BitmapFactory.decodeByteArray(art, 0, art.length);
            bitmaps[i] = songImage;

        }
        customAdapter customAdapter = new customAdapter();
        listView.setAdapter(customAdapter);




        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String songName = (String) listView.getItemAtPosition(position);
                startActivity(new Intent(getApplicationContext(), PlayerActivity.class)
                        .putExtra("songs", mySongs)
                        .putExtra("songname", songName)
                        .putExtra("pos", position));
            }
        });

    }

    class customAdapter extends BaseAdapter  {

        @Override
        public int getCount() {
            return items.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View myView = getLayoutInflater().inflate(R.layout.list_item, null);
            TextView textsong = myView.findViewById(R.id.txtsongname);
            textsong.setSelected(true);
            textsong.setText(items[position]);
            TextView artistname = myView.findViewById(R.id.artist);
            artistname.setText(names[position]);
            ImageView thumbnail = myView.findViewById(R.id.imgsong);
            thumbnail.setSelected(true);
            thumbnail.setImageBitmap(bitmaps[position]);

            return myView;
        }
    }
}