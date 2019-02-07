package com.example.project;

import android.app.Application;

import com.example.project.models.ChatUser;

public class UserClient extends Application {

    private ChatUser user = null;

    public ChatUser getUser() {
        return user;
    }

    public void setUser(ChatUser user) {
        this.user = user;
    }

}
