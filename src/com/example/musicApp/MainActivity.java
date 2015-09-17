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


        fragmentTabHost.addTab(fragmentTabHost.newTabSpec("search").setIndicator("search"), SearchFragment.class, null);
        fragmentTabHost.addTab(fragmentTabHost.newTabSpec("download").setIndicator("download"), FragmentTab.class, null);




        fragmentTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                Log.i("fragmentTabHost", fragmentTabHost.getCurrentTab() + "");


            }
        });



        Button start = (Button) findViewById(R.id.startButton);
        Button pause = (Button) findViewById(R.id.pauseButton);
        ImageView sharedPlayerButton = (ImageView) findViewById(R.id.shared_player_button);
        ImageView settingsButton = (ImageView)findViewById(R.id.settings_button_main_layout);
        Button nextButton = (Button)findViewById(R.id.stopButton);


        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                player.start();
                Intent serviceIntent = new Intent(MainActivity.this, MyService.class);
                serviceIntent.putExtra("command", "start");
                serviceIntent.putExtra("dataBase", serviceInfo);
                startService(serviceIntent);

            }
        });

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                player.start();
                Intent serviceIntent = new Intent(MainActivity.this, MyService.class);
                serviceIntent.putExtra("command", "pause");
                serviceIntent.putExtra("dataBase", serviceInfo);
                startService(serviceIntent);

            }
        });


        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent serviceIntent = new Intent(MainActivity.this, MyService.class);
                serviceIntent.putExtra("command", "forward");
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

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
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



    public SettingsAndPlaylist getServiceInfo() {
        return serviceInfo;
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
//        unregisterReceiver(receiver);
    }
}
