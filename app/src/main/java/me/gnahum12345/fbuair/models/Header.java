package me.gnahum12345.fbuair.models;

import android.graphics.Bitmap;

import me.gnahum12345.fbuair.managers.MyUserManager;

// header class containing main user info
public class Header {
    private String uid;
    private Bitmap profileImage;
    private String name;
    private String organization;
    private int connections;
    private int color;
    private String phone;
    private String email;
    private boolean isAdded;

    public Header(String uid) {
        User user = MyUserManager.getInstance().getUser(uid);
        this.uid = uid;
        this.profileImage = user.getProfileImage();
        this.name = user.getName();
        this.organization = user.getOrganization();
        this.connections = user.getNumConnections();
        this.color = user.getColor();
        this.phone = user.getPhoneNumber();
        this.email = user.getEmail();
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

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public boolean isAdded() {
        return isAdded;
    }

    public void setAdded(boolean added) {
        isAdded = added;
    }
}