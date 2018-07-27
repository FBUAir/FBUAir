package me.gnahum12345.fbuair.models;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import me.gnahum12345.fbuair.utils.SocialMediaUtils;

@Parcel
public class SocialMedia {
    private String name;
    private String username;

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

    public static JSONObject toJson(SocialMedia socialMedia) {
        String name = socialMedia.getName();
        String username = socialMedia.getUsername();
        JSONObject json = new JSONObject();
        try {
            json.put("name", name);
            json.put("username", username);
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
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return socialMedia;
    }
}