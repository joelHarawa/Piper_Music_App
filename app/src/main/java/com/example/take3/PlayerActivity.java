package com.example.take3;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Palette.Builder;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.ArrayList;

public class PlayerActivity extends AppCompatActivity {
    Button forwardbutton, backbutton, playbutton;
    TextView txtsn, txtstart, txtend, artname;
    SeekBar seekmusic;
    ImageView albumCover;


    String sname;
    String aname;
    public static final String EXTRA_NAME = "song_name";
    static MediaPlayer mediaPlayer;
    int position;
    ArrayList<File> mySongs;
    Thread updateSeekbar;

    byte[] art;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_player);

        //setting resources

        backbutton = findViewById(R.id.backbutton);
        forwardbutton = findViewById(R.id.forwardbutton);
        playbutton = findViewById(R.id.playbutton);
        txtsn = findViewById(R.id.txtsn);
        txtend = findViewById(R.id.txtend);
        txtstart = findViewById(R.id.txtstart);
        seekmusic = findViewById(R.id.seekmusic);
        albumCover = findViewById(R.id.albumcover);
        artname = findViewById(R.id.artName);

        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }

        Intent i = getIntent();
        Bundle bundle = i.getExtras();

        mySongs = (ArrayList) bundle.getParcelableArrayList("songs");
        String songName = i.getStringExtra("songname");
        position = bundle.getInt("pos", 0);
        txtsn.setSelected(true);
        Uri uri = Uri.parse(mySongs.get(position).toString());

        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        String songPath = String.valueOf(mySongs.get(position));
        mmr.setDataSource(songPath);
        sname = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
        txtsn.setText(sname);
        aname = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
        artname.setText(aname);

        public Palette c


        try {
            art = mmr.getEmbeddedPicture();
            Bitmap songImage = BitmapFactory.decodeByteArray(art, 0, art.length);
            albumCover.setImageBitmap(songImage);
        } catch (Exception e) {

        }

        mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
        mediaPlayer.start();

        updateSeekbar = new Thread() {
            @Override
            public void run() {
                int totalDuration = mediaPlayer.getDuration();
                int currentPosition = 0;
                while (currentPosition < totalDuration) {
                    try {
                        sleep(500);
                        currentPosition = mediaPlayer.getCurrentPosition();
                        seekmusic.setProgress(currentPosition);
                    } catch (InterruptedException | IllegalStateException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        seekmusic.setMax(mediaPlayer.getDuration());
        updateSeekbar.start();
        seekmusic.getThumb().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        seekmusic.getProgressDrawable().setColorFilter(Color.rgb(28, 202, 182), PorterDuff.Mode.SRC_ATOP);

        seekmusic.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });

        String endTime = convertTime(mediaPlayer.getDuration());
        txtend.setText(endTime);

        final Handler handler = new Handler();
        final int delay = 1000;

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String currentTime = convertTime(mediaPlayer.getCurrentPosition());
                txtstart.setText(currentTime);
                String endTime = convertTime(mediaPlayer.getDuration());
                txtend.setText(endTime);
                handler.postDelayed(this, delay);
            }
        }, delay);

        playbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()) {
                    playbutton.setBackgroundResource(R.drawable.ic_play3);
                    mediaPlayer.pause();
                } else {
                    playbutton.setBackgroundResource(R.drawable.ic_pause2);
                    mediaPlayer.start();
                }
            }
        });

        forwardbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.release();
                position = ((position + 1) % mySongs.size());
                Uri u = Uri.parse(mySongs.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(), u);
                MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                String songPath = String.valueOf(mySongs.get(position));
                mmr.setDataSource(songPath);
                sname = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);;
                txtsn.setText(sname);
                aname = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
                artname.setText(aname);
                art = mmr.getEmbeddedPicture();
                Bitmap songImage = BitmapFactory.decodeByteArray(art, 0, art.length);
                albumCover.setImageBitmap(songImage);
                mediaPlayer.start();
                playbutton.setBackgroundResource(R.drawable.ic_pause2);
            }
        });

        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.release();
                position = ((position - 1) < 0) ? (mySongs.size() - 1) : (position - 1);
                Uri u = Uri.parse(mySongs.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(), u);
                MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                String songPath = String.valueOf(mySongs.get(position));
                mmr.setDataSource(songPath);
                sname = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);;
                txtsn.setText(sname);
                aname = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
                artname.setText(aname);
                art = mmr.getEmbeddedPicture();
                Bitmap songImage = BitmapFactory.decodeByteArray(art, 0, art.length);
                albumCover.setImageBitmap(songImage);
                mediaPlayer.start();
                playbutton.setBackgroundResource(R.drawable.ic_pause2);
            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                forwardbutton.performClick();
            }
        });





    }
    public String convertTime(int duration) {
        String time = "";
        int min = duration/1000/60;
        int seconds = duration/1000%60;

        time += min + ":";
        if (seconds < 10) {
            time += "0";
        }
        time += seconds;

        return time;
    }
}