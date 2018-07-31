package me.gnahum12345.fbuair.models;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import me.gnahum12345.fbuair.utils.SocialMediaUtils;

@Parcel
public class SocialMedia {
    // social media platform name (eg. facebook, instagram)
    String name;
    // user's name (could be username, full name - how user is represented on site)
    String username;
    // deep link into user's profile
    String profileUrl;

    public SocialMedia() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfileUrl() {
        if (profileUrl != null) {
            return profileUrl;
        } else {
            return SocialMediaUtils.getProfileUrl(name, username);
        }
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public static JSONObject toJson(SocialMedia socialMedia) {
        JSONObject json = new JSONObject();
        try {
            json.put("name", socialMedia.getName());
            json.put("username", socialMedia.getUsername());
            json.put("profileUrl", socialMedia.getProfileUrl());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    public static SocialMedia fromJson(JSONObject json) throws JSONException {
        SocialMedia socialMedia = new SocialMedia();
        try {
            socialMedia.setName(json.getString("name"));
            socialMedia.setUsername(json.getString("username"));
            socialMedia.setProfileUrl(json.getString("profileUrl"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return socialMedia;
    }
}