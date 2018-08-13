package me.gnahum12345.fbuair.models;

import android.graphics.Bitmap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static me.gnahum12345.fbuair.models.User.bitmapToString;
import static me.gnahum12345.fbuair.models.User.stringToBitmap;

public class SentToUser {
    private Bitmap profileImage;
    private String name;
    private boolean contactSent;
    private ArrayList<SocialMedia> socialMedias;
    private String timeAddedToHistory;

    public Bitmap getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(Bitmap profileImage) {
        this.profileImage = profileImage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isContactSent() {
        return contactSent;
    }

    public void setContactSent(boolean contactSent) {
        this.contactSent = contactSent;
    }

    public ArrayList<SocialMedia> getSocialMedias() {
        return socialMedias;
    }

    public void setSocialMedias(ArrayList<SocialMedia> socialMedias) {
        this.socialMedias = socialMedias;
    }

    public String getTimeAddedToHistory() {
        return timeAddedToHistory;
    }

    public void setTimeAddedToHistory(String timeAddedToHistory) {
        this.timeAddedToHistory = timeAddedToHistory;
    }

    public static SentToUser fromJson(JSONObject json){
        SentToUser user = new SentToUser();
        try {
            user.profileImage = stringToBitmap(json.getString("profileImage"));
            user.name = json.getString("name");
            user.contactSent = json.getBoolean("contactSent");
            user.socialMedias = User.jsonArrayToArrayList(json.optJSONArray("socialMedias"));
            user.timeAddedToHistory = json.getString("timeAddedToHistory");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return user;
    }

    public static JSONObject toJson(SentToUser user) throws JSONException {
        String name = user.getName();
        String profileImageString = bitmapToString(user.getProfileImage());
        boolean contactSent = user.isContactSent();
        JSONArray socialMedias = User.arrayListToJsonArray(user.getSocialMedias());
        String timeAddedToHistory = user.getTimeAddedToHistory();

        JSONObject json = new JSONObject();
        json.put("name", name);
        json.put("profileImage", profileImageString);
        json.put("contactSent", contactSent);
        json.put("socialMedias", socialMedias);
        json.put("timeAddedToHistory", timeAddedToHistory);
        return json;

    }

     public static User toUser(SentToUser sentToUser) {
        User user = new User();
        user.setName(sentToUser.getName());
        user.setProfileImage(sentToUser.getProfileImage());
        user.setTimeAddedToHistory(sentToUser.getTimeAddedToHistory());
        user.setPhoneNumber("1");
        for (SocialMedia socialMedia : sentToUser.getSocialMedias()) {
            user.addSocialMedia(socialMedia);
        }
        return user;
     }
}
