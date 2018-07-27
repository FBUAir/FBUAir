package me.gnahum12345.fbuair.models;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import me.gnahum12345.fbuair.utils.SocialMediaUtils;

@Parcel
public class SocialMedia {
    String name;
    String username;
    boolean added;

    public SocialMedia() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfileUrl() {
        return SocialMediaUtils.getProfileUrl(name, username);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isAdded() {
        return added;
    }

    public void setAdded(boolean added) {
        this.added = added;
    }

    public static JSONObject toJson(SocialMedia socialMedia) {
        String name = socialMedia.getName();
        String username = socialMedia.getUsername();
        Boolean added = socialMedia.isAdded();
        JSONObject json = new JSONObject();
        try {
            json.put("name", name);
            json.put("username", username);
            json.put("added", added);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    public static SocialMedia fromJson(JSONObject json) throws JSONException {
        SocialMedia socialMedia = new SocialMedia();
        try {
            socialMedia.name = json.getString("name");
            socialMedia.username = json.getString("username");
            socialMedia.added = json.getBoolean("added");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return socialMedia;
    }
}