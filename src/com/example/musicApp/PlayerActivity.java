package com.example.musicApp;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nick_dnepr on 22.07.2015.
 */


public class PlayerActivity extends Activity {


    BroadcastReceiver receiver;
    private int currentPosition;
    private int finalPosition;
    private List playList;
    private int position;
    public boolean playing = false;
    private boolean repeat = false;
    private SettingsAndPlaylist serviceInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player_layout);




        try {
            serviceInfo=(SettingsAndPlaylist)getIntent().getSerializableExtra("dataBase");
            position=serviceInfo.getPosition();
        }catch (Exception e){
            Log.i("error", e.toString());
        }

        try {
            playList=serviceInfo.getPlayList();
        }catch (Exception e){

//            Toast.makeText(getApplicationContext(), "nothing to play");
        }




        final ImageView playButton = (ImageView) findViewById(R.id.play_button);
        final TextView currentTime = (TextView) findViewById(R.id.current_time);
        final TextView fullTime = (TextView) findViewById(R.id.full_time);
        final SeekBar prograssBar = (SeekBar) findViewById(R.id.progress_bar);
        final ImageView repeatButton = (ImageView) findViewById(R.id.repeatButton);
        final ImageView forwardButton = (ImageView) findViewById(R.id.next_audio_button);
        final ImageView backButton = (ImageView) findViewById(R.id.previous_audio_button);


        IntentFilter filter = new IntentFilter();
        filter.addAction("broadcast_for_player");
//        filter.addAction("listSender");
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals("broadcast_for_player")) {



                    currentPosition = intent.getIntExtra("currentPosition", 0);
                    finalPosition = intent.getIntExtra("finalPosition", 0);
                    playing = intent.getBooleanExtra("playingStatus", false);

                    prograssBar.setProgress(currentPosition / (finalPosition / 100));

                    checkButtonStatement(playing, playButton);


                    Log.v("broadcast", "broadcast delivered");
                    Log.v("broadcast info", intent.getIntExtra("currentPosition", 0) + " current position delivered");
                    Log.v("broadcast info", intent.getIntExtra("finalPosition", 0) + " duration delivered");
//                Log.i("broadcast info", intent.getBooleanExtra("finalPosition", false)+" playing delivered");


                    String first = currentPosition / 1000 / 60 + ":" + currentPosition / 1000 % 60;
                    String second = finalPosition / 1000 / 60 + ":" + finalPosition / 1000 % 60;


                    if (finalPosition-currentPosition<=750 && playing == false) {
                        restartSong();
                    }
                    currentTime.setText(first);
                    fullTime.setText(second);
                }
            }
        };
        registerReceiver(receiver, filter);


        if (playing) {
            playButton.setImageResource(R.drawable.ic_pause_circle_outline_white_48dp);
        }

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent serviceIntent = new Intent(PlayerActivity.this, MyService.class);
                if (playing) {
                    playButton.setImageResource(R.drawable.ic_play_circle_outline_white_48dp);
                    serviceIntent.putExtra("command", "pause");
                    startService(serviceIntent);
                    playing = false;
                } else {
                    playButton.setImageResource(R.drawable.ic_pause_circle_outline_white_48dp);
                    serviceIntent.putExtra("command", "start");
                    startService(serviceIntent);
                    playing = true;
                }
            }
        });


        prograssBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    Intent serviceIntent = new Intent(PlayerActivity.this, MyService.class);
                    serviceIntent.putExtra("command", "setProgress");
                    serviceIntent.putExtra("progress", progress * finalPosition / 100);
                    startService(serviceIntent);
                    serviceIntent = new Intent(PlayerActivity.this, MyService.class);
                    serviceIntent.putExtra("command", "start");
                    startService(serviceIntent);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        repeatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (repeat) {
                    repeat = false;
                    repeatButton.setImageResource(R.drawable.ic_repeat_white_48dp);
                } else {
                    repeat = true;
                    repeatButton.setImageResource(R.drawable.ic_repeat_black_48dp);
                }
            }
        });

        forwardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent serviceIntent = new Intent(PlayerActivity.this, MyService.class);
                serviceIntent.putExtra("command", "pause");
                startService(serviceIntent);
                serviceIntent = new Intent(PlayerActivity.this, MyService.class);

                position=position+1;
                if(position>=playList.size()){
                    position=position-1;
                }
                Info tmp = (Info)playList.get(position);
                String command = tmp.getStream_url()+"?client_id=b45b1aa10f1ac2941910a7f0d10f8e28";
                if (tmp.getPath_to_file()!=null){
                    command=tmp.getPath_to_file();
                }
                serviceIntent.putExtra("command", command);
                startService(serviceIntent);
            }
        });


        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent serviceIntent = new Intent(PlayerActivity.this, MyService.class);
                serviceIntent.putExtra("command", "pause");
                startService(serviceIntent);
                serviceIntent = new Intent(PlayerActivity.this, MyService.class);
                position=position-1;

                if(position<0){
                    position=position+1;
                }

                Info tmp = (Info)playList.get(position);


                String command = tmp.getStream_url()+"?client_id=b45b1aa10f1ac2941910a7f0d10f8e28";
                if (tmp.getPath_to_file()!=null){
                    command=tmp.getPath_to_file();
                }
                serviceIntent.putExtra("command", command);
                startService(serviceIntent);
            }
        });

    }


    private void checkButtonStatement(boolean playing, ImageView playButton) {
        if (playing) {
            playButton.setImageResource(R.drawable.ic_pause_circle_outline_white_48dp);
        } else {
            playButton.setImageResource(R.drawable.ic_play_circle_outline_white_48dp);
        }


    }

    private void restartSong() {
        if (repeat) {
            Intent serviceIntent = new Intent(PlayerActivity.this, MyService.class);
            serviceIntent.putExtra("command", "repeat");
            startService(serviceIntent);
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        serviceInfo.setPosition(position);
        serviceInfo.setPlayList(playList);
        serviceInfo.setRepeat(repeat);

    }

    @Override
    protected void onResume() {
        super.onResume();
        repeat=serviceInfo.isRepeat();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }
}
