package me.gnahum12345.fbuair.models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.io.ByteArrayOutputStream;

@Parcel
public class User {

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

    public String getName() { return name; }

    public String getOrganization() { return organization; }

    public String getPhoneNumber() { return phoneNumber; }

    public String getEmail() { return email; }

    public String getFacebookURL() { return facebookURL; }

    public String getInstagramURL() { return instagramURL; }

    public String getLinkedInURL() { return linkedInURL; }

    public Bitmap getProfileImage() { return profileImage; }

    public String getTimeAddedToHistory() { return timeAddedToHistory; }

    public void setProfileImage(Bitmap profileImage) { this.profileImage = profileImage; }

    public void setName(String name) { this.name = name; }

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
            Log.e("USER_MODEL_TAG", "fromJson: media isn't there. ", e);
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

        JSONObject json = new JSONObject();
        json.put("name", name);
        json.put("organization", organization);
        json.put("phoneNumber", phoneNumber);
        json.put("email", email);
        json.put("facebookURL", facebookURL);
        json.put("profileImage", profileImageString);
        json.put("instagramURL", instagramURL);
        json.put("linkedInURL", linkedInURL);
        json.put("timeAddedToHistory", timeAddedToHistory);

        Log.d("toJson", json.toString());
        return json;

    }

    public static Bitmap stringToBitmap(String encodedString){
        try {
            byte [] encodeByte=Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
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
        String temp = Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }
}
