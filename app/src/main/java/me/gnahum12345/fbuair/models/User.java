package me.gnahum12345.fbuair.models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.io.ByteArrayOutputStream;
import java.util.Random;
import java.util.ArrayList;
import java.util.Arrays;

@Parcel
public class User {

    String uid;
    private String name;
    private String organization;
    private String phoneNumber;
    private String email;
    private Bitmap profileImage;
    private String facebookURL;
    private String instagramURL;
    private String linkedInURL;
    private String timeAddedToHistory;
    private ArrayList<SocialMedia> socialMedias = new ArrayList<>();


    // empty constructor needed by the Parceler library
    public User() {
    }

    public String getName() {
        return name;
    }
    public String getUid() {
        return uid;
    }

    public String getOrganization() {
        return organization;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public String getFacebookURL() {
        return facebookURL;
    }

    public String getInstagramURL() {
        return instagramURL;
    }

    public String getLinkedInURL() {
        return linkedInURL;
    }

    public Bitmap getProfileImage() {
        return profileImage;
    }

    public String getTimeAddedToHistory() {
        return timeAddedToHistory;
    }

    public String getId() {
        return uid;
    }

    public boolean setId() {
        if (this.name == null) {
            return false;
        }
        uid = this.name + randomDigits(this.name);
        return true;
    }

    private String randomDigits(String seed) {

        StringBuilder builder = new StringBuilder();
        for (Character c : seed.toCharArray()) {
            Random rand = new Random(c.hashCode());
            builder.append(rand.nextInt());
        }

        return builder.toString();
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setName(String name) {
        this.name = name;
        if (getId() == null || getId().isEmpty()) {
            setId();
        }
    }

    public void setOrganization(String organization) { this.organization = organization; }

    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public void setEmail(String email) { this.email = email; }

    public void setFacebookURL(String facebookURL) { this.facebookURL = facebookURL; }

    public void setProfileImage(Bitmap profileImage) {
        this.profileImage = profileImage;
    }

    public void setTimeAddedToHistory(String timeAddedToHistory) { this.timeAddedToHistory = timeAddedToHistory; }

    public ArrayList<SocialMedia> getSocialMedias() {
        return socialMedias;
    }

    // adds social media or edits old one with same name if it exists
    public void addSocialMedia (SocialMedia socialMedia) {
        boolean exists = false;
        for (SocialMedia socialMedia1: socialMedias) {
            if (socialMedia1.getName().equals(socialMedia.getName())) {
                socialMedia1.setUsername(socialMedia.getUsername());
                exists = true;
                break;
            }
        }
        if (!exists) socialMedias.add(socialMedia);
    }

    // gets social media object from social media list by name. returns null if none exists
    public SocialMedia getSocialMedia(String socialMediaName) {
        for (SocialMedia socialMedia1: socialMedias) {
            if (socialMedia1.getName().equals(socialMediaName)) {
                return socialMedia1;
            }
        }
        return null;
    }

    // removes social media by object
    public void removeSocialMedia (SocialMedia socialMedia) {
        socialMedia.setUsername(null);
        socialMedias.remove(socialMedia);
    }


    public static User fromJson(JSONObject json) throws JSONException {
        User user = new User();
        try {
            user.name = json.getString("name");
            user.uid = json.optString("uId", "obviouslyNotAnId");
            user.phoneNumber = json.optString("phoneNumber");
            user.email = json.optString("email");
            user.organization = json.optString("organization");
            user.facebookURL = json.optString("facebookURL");
            user.profileImage = stringToBitmap(json.getString("profileImage"));
            user.instagramURL = json.optString("instagramURL");
            user.linkedInURL = json.optString("linkedInURL");
            user.timeAddedToHistory = json.optString("timeAddedToHistory");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return user;
    }


    public static User fromString (String jsonString) throws JSONException {
        return fromJson(new JSONObject(jsonString));
    }

    public static JSONObject toJson(User user) throws JSONException {

        String name = user.getName();
        String organization = user.getOrganization();
        String phoneNumber = user.getPhoneNumber();
        String email = user.getEmail();
        String profileImageString = bitmapToString(user.getProfileImage());
        String facebookURL = user.getFacebookURL();
        String instagramURL = user.getInstagramURL();
        String linkedInURL = user.getLinkedInURL();
        String timeAddedToHistory = user.getTimeAddedToHistory();
        String uid = user.getId();

        JSONObject json = new JSONObject();
        json.put("name", name);
        json.put("uId", uid);
        json.put("organization", organization);
        json.put("phoneNumber", phoneNumber);
        json.put("email", email);
        json.put("facebookURL", facebookURL);
        json.put("profileImage", profileImageString);
        json.put("instagramURL", instagramURL);
        json.put("linkedInURL", linkedInURL);
        json.put("timeAddedToHistory", timeAddedToHistory);
        return json;

    }

    public static Bitmap stringToBitmap(String encodedString){
        try {
            byte [] encodeByte=Base64.decode(encodedString,Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
        } catch(Exception e) {
            e.getMessage();
            return null;
        }
    }

    public static String bitmapToString(Bitmap bitmap){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        if (bitmap == null) return "";
        bitmap.compress(Bitmap.CompressFormat.PNG,100, stream);
        byte [] b = stream.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }

}
