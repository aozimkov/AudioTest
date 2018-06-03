package com.example.android.audiotest;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.VolumeShaper;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private MediaPlayer player = null;
    private final static int MAX_VOLUME = 100;
    private int currentVolume = 70;
    private AudioManager mAudioManager; //Init AudioManager
    private AudioManager.OnAudioFocusChangeListener mAFChangeListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Button play = (Button) findViewById(R.id.play);
        Button pause = (Button) findViewById(R.id.pause);
        Button volup = (Button) findViewById(R.id.volumeup);
        Button voldown = (Button) findViewById(R.id.volumedown);
        player = MediaPlayer.create(this, R.raw.audio);

        //Create and setup AudioManager for focus request
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mediaPLayeReleaser();
            }
        });

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mAFChangeListener = new AudioManager.OnAudioFocusChangeListener() {
                    @Override
                    public void onAudioFocusChange(int i) {
                        if(i == AudioManager.AUDIOFOCUS_GAIN){
                        // GAIN means we granted audiofocus again play started
                            player.start();
                        } else if (i == AudioManager.AUDIOFOCUS_LOSS){
                        // stop
                            mediaPLayeReleaser();
                        } else if (i == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT || i == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK){
                        // pause and reset player to start of the file. On resume will start from begin
                            player.pause();
                            player.seekTo(0);
                        }
                    }
                };

                int result = mAudioManager.requestAudioFocus(mAFChangeListener,
                        AudioManager.STREAM_MUSIC,
                        AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);

                if (result  == AudioManager.AUDIOFOCUS_REQUEST_GRANTED){
                    //we have audio focus now
                    player.start();
                }

            }
        });

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                player.pause();
            }
        });

        volup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentVolume += 10;
                final float volume = (float) (1 - (Math.log(MAX_VOLUME - currentVolume) / (Math.log(MAX_VOLUME))));
                player.setVolume(volume, volume);
            }
        });

        voldown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentVolume -= 10;
                final float volume = (float) (1 - (Math.log(MAX_VOLUME - currentVolume) / Math.log(MAX_VOLUME)));
                player.setVolume(volume, volume);
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        mediaPLayeReleaser();
    }

    public void mediaPLayeReleaser(){
        if (player != null){
            player.release();
            player = null;

            mAudioManager.abandonAudioFocus(mAFChangeListener); //close audio focus
        }
    }

}
