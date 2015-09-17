package com.example.musicApp;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Nick_dnepr on 22.07.2015.
 */


public class PlayerActivity extends Activity {


    BroadcastReceiver receiver;
    private int currentPosition;
    private int finalPosition;
    private List playList;
    private List orderedList;
    private Random rand = new Random();
    private int position;
    public boolean playing = false;
    private boolean repeat = false;
    private boolean randomList = false;
    private SettingsAndPlaylist serviceInfo;
    private boolean active;
    private boolean freeze;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player_layout);


        try {
            serviceInfo = (SettingsAndPlaylist) getIntent().getSerializableExtra("dataBase");
            position = serviceInfo.getPosition();
            playList = serviceInfo.getPlayList();
            Log.i("playlist", playList.toString());
        } catch (Exception e) {
            Log.i("error", e.toString());
        }




        final ImageView playButton = (ImageView) findViewById(R.id.play_button);
        final TextView currentTime = (TextView) findViewById(R.id.current_time);
        final TextView fullTime = (TextView) findViewById(R.id.full_time);
        final SeekBar prograssBar = (SeekBar) findViewById(R.id.progress_bar);
        final ImageView repeatButton = (ImageView) findViewById(R.id.repeatButton);
        final ImageView forwardButton = (ImageView) findViewById(R.id.next_audio_button);
        final ImageView backButton = (ImageView) findViewById(R.id.previous_audio_button);
        final ImageView randomButton = (ImageView) findViewById(R.id.random_list);
        final ImageView settingsButton = (ImageView)findViewById(R.id.settings_button_player_layout);
        final ImageView mainButton = (ImageView)findViewById(R.id.main_button_player_layout);


        IntentFilter filter = new IntentFilter();
        filter.addAction("broadcast_for_player");
//        filter.addAction("listSender");
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {finalPosition = intent.getIntExtra("finalPosition", 0);
                if (intent.getAction().equals("broadcast_for_player")) {


                    currentPosition = intent.getIntExtra("currentPosition", 0);

                    playing = intent.getBooleanExtra("playingStatus", false);

                    prograssBar.setProgress(currentPosition / (finalPosition / 100));

                    checkButtonStatement(playing, playButton, repeatButton, randomButton);

//                    checkPlayer();
                    Log.v("broadcast", "broadcast delivered");
                    Log.v("broadcast info", intent.getIntExtra("currentPosition", 0) + " current position delivered");
                    Log.v("broadcast info", intent.getIntExtra("finalPosition", 0) + " duration delivered");
//                Log.i("broadcast info", intent.getBooleanExtra("finalPosition", false)+" playing delivered");


                    String first = currentPosition / 1000 / 60 + ":" + currentPosition / 1000 % 60;
                    String second = finalPosition / 1000 / 60 + ":" + finalPosition / 1000 % 60;


//                    if (finalPosition - currentPosition <= 750 && playing == false) {
//                        restartSong();
//                    }
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
//                    serviceIntent.putExtra("dataBase", serviceInfo);
                    startService(serviceIntent);
                    playing = false;
                } else {
                    playButton.setImageResource(R.drawable.ic_pause_circle_outline_white_48dp);
                    serviceIntent.putExtra("command", "start");
//                    serviceIntent.putExtra("dataBase", serviceInfo);
                    startService(serviceIntent);
                    playing = true;
                }
            }
        });

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlayerActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });


        prograssBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    Intent serviceIntent = new Intent(PlayerActivity.this, MyService.class);
                    serviceIntent.putExtra("command", "setProgress");
                    serviceIntent.putExtra("progress", progress * finalPosition / 100);
//                    serviceIntent.putExtra("dataBase", serviceInfo);
                    startService(serviceIntent);
//                    serviceIntent = new Intent(PlayerActivity.this, MyService.class);
//                    serviceIntent.putExtra("command", "start");
//                    startService(serviceIntent);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlayerActivity.this, MainActivity.class);
                startActivity(intent);
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
//                    serviceInfo.setPosition(position);
//                    serviceInfo.setPlayList(playList);
                    serviceInfo.setRepeat(repeat);
                    serviceInfo.setPlayList(playList);

                }
                Intent intent = new Intent(PlayerActivity.this, MyService.class);
//                    intent.putExtra("dataBase", serviceInfo);
                intent.putExtra("command", "changeRepeatController");
                startService(intent);
            }
        });

        randomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orderedList = playList;
                if (randomList == false) {
                    for (int i = 0; i < playList.size(); i++) {
                        int index1 = rand.nextInt(playList.size());
//                    int index2 = rand.nextInt(playList.size()-1);
                        if (index1 == position || i == position) {
                            continue;
                        }
                        Info tmp = (Info) playList.get(i);
                        playList.set(i, playList.get(index1));
                        playList.set(index1, tmp);


                    }
                    randomList = true;
                    randomButton.setImageResource(R.drawable.ic_shuffle_black_48dp);
                    serviceInfo.setPosition(position);
                    serviceInfo.setPlayList(playList);
                    serviceInfo.setRepeat(repeat);
                } else {
                    randomList = false;
                    randomButton.setImageResource(R.drawable.ic_shuffle_white_48dp);
                    playList = orderedList;
                    Intent intent = new Intent(PlayerActivity.this, MyService.class);
                    serviceInfo.setPlayList(playList);
                    intent.putExtra("dataBase", serviceInfo);
                    intent.putExtra("command", "start");
                    startService(intent);
                }
            }
        });

        forwardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                position = serviceInfo.getPosition();
                Intent serviceIntent = new Intent(PlayerActivity.this, MyService.class);
                serviceIntent.putExtra("command", "forward");
//                serviceIntent.putExtra("dataBase", serviceInfo);
                startService(serviceIntent);

            }
        });


        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                position = serviceInfo.getPosition();
                Intent serviceIntent = new Intent(PlayerActivity.this, MyService.class);
                serviceIntent.putExtra("command", "back");
//                serviceIntent.putExtra("dataBase", serviceInfo);
                startService(serviceIntent);

            }
        });

    }


    private void checkButtonStatement(boolean playing, ImageView playButton, ImageView repeatButton, ImageView randomButton) {
        if (playing) {
            playButton.setImageResource(R.drawable.ic_pause_circle_outline_white_48dp);
        } else {
            playButton.setImageResource(R.drawable.ic_play_circle_outline_white_48dp);
        }
        if (repeat) {
            repeatButton.setImageResource(R.drawable.ic_repeat_black_48dp);
        } else {
            repeatButton.setImageResource(R.drawable.ic_repeat_white_48dp);
        }
        if (randomList) {
            randomButton.setImageResource(R.drawable.ic_shuffle_black_48dp);
        } else {
            randomButton.setImageResource(R.drawable.ic_shuffle_white_48dp);
        }


    }






    @Override
    protected void onPause() {
        super.onPause();
        serviceInfo.setPosition(position);
        serviceInfo.setPlayList(playList);
        serviceInfo.setRepeat(repeat);
        active = false;
    }
//
    @Override
    protected void onResume() {
        super.onResume();
        repeat = serviceInfo.isRepeat();
        active = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }
}
