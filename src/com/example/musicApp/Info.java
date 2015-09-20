package com.example.musicApp;

import java.io.Serializable;

/**
 * Created by Nick_dnepr on 20.05.2015.
 */
public class Info implements Serializable{
    private String title;
    private int duration;
    private User user;
    private int likes_count;
    private String stream_url;

    private String path_to_file;
    private String artwork_url;

    public Info(String title, int duration, User user, int likes_count, String stream_url, String path_to_file, String artwork_url) {
        this.title = title;
        this.duration = duration;
        this.user = user;
        this.likes_count = likes_count;
        this.stream_url = stream_url;

        this.path_to_file = path_to_file;
        this.artwork_url=artwork_url;
    }

    public String getTitle() {
        return title;
    }

    public int getDuration() {
        return duration;
    }

    public User getUser() {
        return user;
    }

    public String getStream_url() {
        return stream_url;
    }

    public String getArtwork_url() {
        return artwork_url;
    }

    //    +"?client_id=b45b1aa10f1ac2941910a7f0d10f8e28"
    public int getLikes_count() {

        return likes_count;
    }

    public String getPath_to_file() {
        return path_to_file;
    }

    public void setPath_to_file(String path_to_file) {
        this.path_to_file = path_to_file;
    }


    @Override
    public String toString() {
        return "Info{" +
                "title='" + title + '\'' +
                ", duration=" + duration +
                ", user=" + user +
                ", likes_count=" + likes_count +
                ", stream_url='" + stream_url + '\'' +
                ", path_to_file='" + path_to_file + '\'' +
                ", artwork_url='" + artwork_url + '\'' +
                '}';
    }
}
