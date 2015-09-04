package com.example.musicApp;

import android.util.Log;

import java.io.Serializable;
import java.util.List;

/**
 * Created by ADMIN on 27.08.2015.
 */



public class SettingsAndPlaylist implements Serializable {
    private static final long serialVersionUID = 42L;
    private  static List playList;
    private static int position;
    private static boolean repeat;
    private static boolean random;



    public SettingsAndPlaylist() {
        Log.i("settings", "testtest");
    }

    public static int getPosition() {
        return position;
    }

    public static void setPosition(int position) {
        SettingsAndPlaylist.position = position;
    }

    public List getPlayList() {

        return playList;
    }

    public static void setPlayList(List playList) {
        SettingsAndPlaylist.playList = playList;
    }

    public static boolean isRepeat() {
        return repeat;
    }

    public static void setRepeat(boolean repeat) {
        SettingsAndPlaylist.repeat = repeat;
    }

    public  boolean isRandom() {
        return random;
    }

    public  void setRandom(boolean random) {
        this.random = random;
    }


}
