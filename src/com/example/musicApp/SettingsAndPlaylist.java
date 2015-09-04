package com.example.musicApp;

import android.util.Log;

import java.io.Serializable;
import java.util.List;

/**
 * Created by ADMIN on 27.08.2015.
 */



public class SettingsAndPlaylist implements Serializable {
    private static final long serialVersionUID = 42L;
    private  List playList;
    private  int position;
    private  boolean repeat;
    private  boolean random;



    public SettingsAndPlaylist() {
        Log.i("settings", "testtest");
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public List getPlayList() {

        return playList;
    }

    public void setPlayList(List playList) {
        this.playList = playList;
    }

    public  boolean isRepeat() {
        return repeat;
    }

    public  void setRepeat(boolean repeat) {
        this.repeat = repeat;
    }

    public  boolean isRandom() {
        return random;
    }

    public  void setRandom(boolean random) {
        this.random = random;
    }


}
