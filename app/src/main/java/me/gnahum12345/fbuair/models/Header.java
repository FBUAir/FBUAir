package me.gnahum12345.fbuair.models;

import android.graphics.Bitmap;

import me.gnahum12345.fbuair.managers.MyUserManager;

// header class containing main user info
public class Header {
    String uid;
    Bitmap profileImage;
    String name;
    String organization;
    int connections;
    int color;

    public Header(String uid) {
        User user = MyUserManager.getInstance().getUser(uid);
        this.uid = uid;
        this.profileImage = user.getProfileImage();
        this.name = user.getName();
        this.organization = user.getOrganization();
        this.connections = user.getNumConnections();
        this.color = user.getColor();
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

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}