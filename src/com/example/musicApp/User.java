package com.example.musicApp;

import java.io.Serializable;

/**
 * Created by Nick_dnepr on 25.05.2015.
 */
public class User implements Serializable{
    private String username;

    public User(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                '}';
    }
}
