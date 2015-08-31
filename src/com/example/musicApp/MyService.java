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

/**
 * Created by Nick_dnepr on 10.06.2015.
 */
public class MyService extends Service implements MediaPlayer.OnPreparedListener {
    private MediaPlayer player;
    private AudioManager manager;
    private boolean taskController;
    private boolean repeatController;

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
        if(command==null){
             return super.onStartCommand(intent, flags, startId);
        }
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


                    if (command.equals("repeat")) {

//                        if(repeatController)

                        player.seekTo(0);
                        player.start();



                    } else {
                        try {
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
