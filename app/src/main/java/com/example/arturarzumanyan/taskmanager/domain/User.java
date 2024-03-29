package com.example.arturarzumanyan.taskmanager.domain;

public class User {
    private String displayName;
    private String email;
    private String photoUrl;

    public User(String displayName, String email, String photoUrl) {
        this.displayName = displayName;
        this.email = email;
        this.photoUrl = photoUrl;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
