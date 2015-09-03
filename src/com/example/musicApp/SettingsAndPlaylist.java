package com.example.musicApp;

import android.util.Log;

import java.io.Serializable;
import java.util.List;

/**
 * Created by ADMIN on 27.08.2015.
 */
public class SettingsAndPlaylist implements Serializable {
    private List playList;
    private int position;
    private static boolean repeat;
    private static boolean random;

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

    public static boolean isRepeat() {
        return repeat;
    }

    public static void setRepeat(boolean repeat) {
        SettingsAndPlaylist.repeat = repeat;
    }

    public static boolean isRandom() {
        return random;
    }

    public static void setRandom(boolean random) {
        SettingsAndPlaylist.random = random;
    }


}
