package com.example.musicApp;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import android.view.View;

import java.io.IOException;
import java.util.List;

/**
 * Created by Nick_dnepr on 10.06.2015.
 */
public class MyService extends Service implements MediaPlayer.OnPreparedListener {
    private MediaPlayer player;
    private AudioManager manager;
    private boolean taskController;
    private boolean repeatController;
    private SettingsAndPlaylist serviceInfo;
    private List playList;
    private int position;



    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            return super.onStartCommand(intent, flags, startId);
        }

        String command = intent.getStringExtra("command");
        if (command == null) {
            return super.onStartCommand(intent, flags, startId);
        }

            serviceInfo = (SettingsAndPlaylist) intent.getSerializableExtra("dataBase");
            position = serviceInfo.getPosition();
            repeatController = serviceInfo.isRepeat();
            playList = serviceInfo.getPlayList();


//        String url = intent.getStringExtra("url");
        if (command.equals("pause")) {
            player.pause();
        } else {
            if (command.equals("start")) {
                player.start();
            } else {
                if (command.equals("setProgress")) {
                    player.seekTo(intent.getIntExtra("progress", -1));
                    if (intent.getIntExtra("progress", -1) == -1) {
                        try {
                            throw new NullPointerException("progress statement is null");
                        } catch (Exception e) {
                            return super.onStartCommand(intent, flags, startId);

                        }
                    }


                } else {


                    if (command.equals("forward")) {

//                        if(repeatController)
                        position = position + 1;
                        if (position >= playList.size()) {
                            position = position - 1;
                        }
                        serviceInfo.setPosition(position);
                        Info tmp = (Info) playList.get(position);
                        String cmd = tmp.getStream_url() + "?client_id=b45b1aa10f1ac2941910a7f0d10f8e28";
                        if (tmp.getPath_to_file() != null) {
                            cmd = tmp.getPath_to_file();
                        }
                        try {
                            player.setDataSource(cmd);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                    } else {

                        if (command.equals("back")) {
                            position = position - 1;
                            serviceInfo.setPosition(position);
                            if (position < 0) {
                                position = position + 1;
                            }

                            Info tmp = (Info) playList.get(position);

                            Log.i("music", "clicked back");
                            String cmd = tmp.getStream_url() + "?client_id=b45b1aa10f1ac2941910a7f0d10f8e28";
                            if (tmp.getPath_to_file() != null) {
                                cmd = tmp.getPath_to_file();
                            }
                            try {
                                player.setDataSource(cmd);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        } else {
                            try {

                                player.stop();
                                player = new MediaPlayer();
                                player.setAudioStreamType(manager.STREAM_MUSIC);
                                player.pause();
                                player.setDataSource(command);
//                    player.start();
                                player.setOnPreparedListener(this);
                                player.prepareAsync();
                                ProgressTask task = new ProgressTask();
                                task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                            } catch (Exception e) {
                                Log.i("uuu", e.toString());
                            }
                        }
                    }
                }

            }
        }

        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (repeatController) {
                    mp.seekTo(0);
                    mp.start();
                } else {
                    position = position + 1;
                    if (position >= playList.size()) {
                        position = position - 1;
                    }
                    serviceInfo.setPosition(position);
                    Info tmp = (Info) playList.get(position);
                    String command = tmp.getStream_url() + "?client_id=b45b1aa10f1ac2941910a7f0d10f8e28";
                    if (tmp.getPath_to_file() != null) {
                        command = tmp.getPath_to_file();
                    }
                    try {
                        player.stop();
                        player = new MediaPlayer();
                        player.setAudioStreamType(manager.STREAM_MUSIC);
                        player.pause();
                        player.setDataSource(command);
                        player.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });


        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public void onCreate() {
        super.onCreate();
        manager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        player = new MediaPlayer();
        player.setAudioStreamType(manager.STREAM_MUSIC);
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.i("ready", "ready");
        mp.start();
    }


    private class ProgressTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {

            while (true) {
                Intent intent = new Intent("broadcast_for_player");
                intent.putExtra("currentPosition", player.getCurrentPosition());
                intent.putExtra("finalPosition", player.getDuration());
                intent.putExtra("playingStatus", player.isPlaying());


                sendBroadcast(intent);
                Log.v("broadcast info", player.getCurrentPosition() + " current position");
                Log.v("broadcast info", player.getDuration() + " duration");
                Log.v("broadcast info", player.isPlaying() + "  playing");
                Log.v("broadcast", "broadcast sended");

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Log.i("serviceError", e.toString());
                }
                if (taskController) {
                    taskController = false;
                    return null;
                }
            }
        }
    }
}
