package com.example.musicApp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity {


    FragmentTabHost fragmentTabHost;
    MusicDataBase dataBase;
    SettingsAndPlaylist serviceInfo = new SettingsAndPlaylist();
    public boolean needToRefresh = false;


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

    private void addTab(String tag, Class<?> fragment1Class) {
        View viev = LayoutInflater.from(this).inflate(R.layout.tab, fragmentTabHost.getTabContentView(), false);
        TabHost.TabSpec tabspec = fragmentTabHost.newTabSpec(tag).setIndicator(viev);
        fragmentTabHost.addTab(tabspec, fragment1Class, null);
    }


    public MusicDataBase getDataBase() {
        return dataBase;
    }

    public SettingsAndPlaylist getServiceInfo() {
        return serviceInfo;
    }
}
