package me.gnahum12345.fbuair.models;

import android.graphics.Bitmap;

import me.gnahum12345.fbuair.managers.UserManager;

// header class containing main user info
public class Header {
    String uid;
    Bitmap profileImage;
    String name;
    String organization;
    int connections;

    public Header(String uid) {
        User user = UserManager.getInstance().getUser(uid);
        this.uid = uid;
        this.profileImage = user.getProfileImage();
        this.name = user.getName();
        this.organization = user.getOrganization();
        this.connections = user.getNumConnections();
    }

    public Bitmap getProfileImage() {
        return profileImage;
    }

    public String getName() {
        return name;
    }

    public String getOrganization() {
        return organization;
    }

    public int getConnections() {
        return connections;
    }

    public String getUid() {
        return uid;
    }
}