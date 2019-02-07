package com.example.project.models;

import android.os.Parcel;
import android.os.Parcelable;

public class ChatUser implements Parcelable{

    private String email;
    private String user_id;
    private String username;

    public ChatUser(String email, String user_id, String username) {
        this.email = email;
        this.user_id = user_id;
        this.username = username;
    }

    public ChatUser() {

    }

    protected ChatUser(Parcel in) {
        email = in.readString();
        user_id = in.readString();
        username = in.readString();
    }

    public static final Creator<ChatUser> CREATOR = new Creator<ChatUser>() {
        @Override
        public ChatUser createFromParcel(Parcel in) {
            return new ChatUser(in);
        }

        @Override
        public ChatUser[] newArray(int size) {
            return new ChatUser[size];
        }
    };

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                ", user_id='" + user_id + '\'' +
                ", username='" + username + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(email);
        dest.writeString(user_id);
        dest.writeString(username);
    }
}
