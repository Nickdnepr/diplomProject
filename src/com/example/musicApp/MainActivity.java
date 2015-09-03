package com.example.musicApp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TabHost;

import java.util.List;

public class MainActivity extends FragmentActivity {


    FragmentTabHost fragmentTabHost;
    MusicDataBase dataBase;
    SettingsAndPlaylist serviceInfo = new SettingsAndPlaylist();
    public boolean needToRefresh = false;
    BroadcastReceiver receiver;
    private boolean playing;
    private boolean repeatMain = false;
    private List playList;
    private int position;
    private int currentPosition;
    private int finalPosition;
    private boolean active;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dataBase = new MusicDataBase(this);


        setContentView(R.layout.main);

        fragmentTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        fragmentTabHost.setup(this, getSupportFragmentManager(), android.R.id.tabcontent);

//        addTab("t2", FragmentTab.class);
//        addTab("t1", SearchFragment.class);
        fragmentTabHost.addTab(fragmentTabHost.newTabSpec("search").setIndicator("search"), SearchFragment.class, null);
        fragmentTabHost.addTab(fragmentTabHost.newTabSpec("download").setIndicator("download"), FragmentTab.class, null);

        IntentFilter filter = new IntentFilter();
        filter.addAction("broadcast_for_player");
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                currentPosition = intent.getIntExtra("currentPosition", 0);
                finalPosition = intent.getIntExtra("finalPosition", 0);
                playing = intent.getBooleanExtra("playingStatus", false);
//                try {
//                    checkPlayer();
//                } catch (Exception e) {
//                    Log.i("error", e.toString());
//                }
//                checkPlayer();

            }
        };
        registerReceiver(receiver, filter);


        fragmentTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                Log.i("fragmentTabHost", fragmentTabHost.getCurrentTab() + "");


            }
        });

//        fragmentTabHost.addTab(fragmentTabHost.newTabSpec("12345").setIndicator("12345"), FragmentTab.class, null);

        Button start = (Button) findViewById(R.id.startButton);
        Button pause = (Button) findViewById(R.id.pauseButton);
        ImageView sharedPlayerButton = (ImageView) findViewById(R.id.shared_player_button);


        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                player.start();
                Intent serviceIntent = new Intent(MainActivity.this, MyService.class);
                serviceIntent.putExtra("command", "start");
                startService(serviceIntent);

            }
        });

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                player.start();
                Intent serviceIntent = new Intent(MainActivity.this, MyService.class);
                serviceIntent.putExtra("command", "pause");
                startService(serviceIntent);

            }
        });

        sharedPlayerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent playerIntent = new Intent(MainActivity.this, PlayerActivity.class);
//                playerIntent.putExtra("playlist", );
                playerIntent.putExtra("dataBase", serviceInfo);
                startActivity(playerIntent);


            }
        });


    }


    @Override
    protected void onResume() {
        super.onResume();
        playList = serviceInfo.getPlayList();
        repeatMain = serviceInfo.isRepeat();
        position = serviceInfo.getPosition();
        active = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        active = false;


    }


    public MusicDataBase getDataBase() {
        return dataBase;
    }

//    private void checkPlayer() {
//        playList = serviceInfo.getPlayList();
//        repeatMain = serviceInfo.isRepeat();
//        position = serviceInfo.getPosition();
//
//        if (finalPosition - currentPosition <= 750 && repeatMain && active) {
//            Intent pauseIntent = new Intent(MainActivity.this, MyService.class);
//            pauseIntent.putExtra("command", "pause");
//            startService(pauseIntent);
//            Intent serviceIntent = new Intent(MainActivity.this, MyService.class);
//            serviceIntent.putExtra("command", "repeat");
//            startService(serviceIntent);
//        }
//
//        if (finalPosition - currentPosition <= 750 && repeatMain == false && active) {
//            position = position + 1;
//            if (position >= playList.size()) {
//                position = position - 1;
//            }
//            serviceInfo.setPosition(position);
//            Info tmp = (Info) playList.get(position);
//            String command = tmp.getStream_url() + "?client_id=b45b1aa10f1ac2941910a7f0d10f8e28";
//            if (tmp.getPath_to_file() != null) {
//                command = tmp.getPath_to_file();
//            }
//            Intent pauseIntent = new Intent(MainActivity.this, MyService.class);
//            pauseIntent.putExtra("command", "pause");
//            startService(pauseIntent);
//            Intent serviceIntent = new Intent(MainActivity.this, MyService.class);
//            serviceIntent.putExtra("command", command);
//            startService(serviceIntent);
//            Log.i("music", "switched to next");
//        }
//    }

    public SettingsAndPlaylist getServiceInfo() {
        return serviceInfo;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }
}
