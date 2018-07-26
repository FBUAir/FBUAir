package me.gnahum12345.fbuair.models;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Base64;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Generated;
import org.parceler.Parcel;

import java.io.ByteArrayOutputStream;
import java.util.Random;


@Parcel
public class User {

    String uid;
    String name;
    String organization;
    String phoneNumber;
    String email;
    Bitmap profileImage;
    String facebookURL;
    String instagramURL;
    String linkedInURL;
    String timeAddedToHistory;

    // empty constructor needed by the Parceler library
    public User() {
    }

    public String getName() {
        return name;
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

    public void setProfileImage(Bitmap profileImage) { this.profileImage = profileImage; }

    public void setName(String name) {
        this.name = name;
        setId();
    }

    public void setOrganization(String organization) { this.organization = organization; }

    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public void setEmail(String email) { this.email = email; }

    public void setFacebookURL(String facebookURL) { this.facebookURL = facebookURL; }

    public void setInstagramURL(String instagramURL) { this.instagramURL = instagramURL; }

    public void setLinkedInURL(String linkedInURL) { this.linkedInURL = linkedInURL; }

    public void setTimeAddedToHistory(String timeAddedToHistory) { this.timeAddedToHistory = timeAddedToHistory; }


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
