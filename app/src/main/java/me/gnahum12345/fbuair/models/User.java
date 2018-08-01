package me.gnahum12345.fbuair.models;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.Base64;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;
import java.util.ArrayList;

@Parcel
public class User implements Comparable {


    String uid;
    String name;
    String organization;
    String phoneNumber;
    String email;
    Bitmap profileImage;
    String timeAddedToHistory;
    Integer numConnections;
    ArrayList<SocialMedia> socialMedias = new ArrayList<>();
    boolean seen = false;

    // empty constructor needed by the Parceler library
    public User() {
    }

    public boolean isSeen() {
        return seen;
    }
    public void hasSeen(boolean seen) {
        this.seen = seen;
    }
    public Integer getNumConnections() {return numConnections; }

    public void setNumConnections(Integer numConnections) { this.numConnections = numConnections; }

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

    public void setName(String name) {
        this.name = name;
        if (getId() == null || getId().isEmpty()) {
            setId();
        }
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setProfileImage(Bitmap profileImage) {
        this.profileImage = profileImage;
    }

    public void setTimeAddedToHistory(String timeAddedToHistory) {
        this.timeAddedToHistory = timeAddedToHistory;
    }

    public ArrayList<SocialMedia> getSocialMedias() {
        return socialMedias;
    }

    // adds social media or edits old one with same name if it exists
    public void addSocialMedia(SocialMedia socialMedia) {
        boolean exists = false;
        for (SocialMedia socialMedia1 : socialMedias) {
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
        for (SocialMedia socialMedia1 : socialMedias) {
            if (socialMedia1.getName().equals(socialMediaName)) {
                return socialMedia1;
            }
        }
        return null;
    }

    // checks if user has social media by name
    public boolean hasSocialMedia(SocialMedia socialMedia) {
        return getSocialMedia(socialMedia.getName()) != null;
    }

    // removes social media by object
    public void removeSocialMedia(SocialMedia socialMedia) {
        socialMedias.remove(socialMedia);
    }

    @Override
    public int compareTo(@NonNull Object o) {
        if (o instanceof User) {
            return getName().compareTo(((User) o).getName());
        } else {
            return 1;
        }
    }

    public java.io.File toFile(Context context) {
        File f = context.getFileStreamPath("user.txt");

        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try (FileOutputStream writer = context.openFileOutput(f.getName(), Context.MODE_PRIVATE)) {
            String content = this.toString();
            writer.write(content.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return f;

    }
    public String toString() {
        try {
            return User.toJson(this).toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static User fromJson(JSONObject json){
        User user = new User();
        try {
            user.name = json.getString("name");
            user.uid = json.optString("uId", "obviouslyNotAnId");
            user.phoneNumber = json.optString("phoneNumber");
            user.email = json.optString("email");
            user.organization = json.optString("organization");
            user.profileImage = stringToBitmap(json.getString("profileImage"));
            user.timeAddedToHistory = json.optString("timeAddedToHistory");
            user.numConnections = json.optInt("numConnections");
            user.socialMedias = User.jsonArrayToArrayList(json.optJSONArray("socialMedias"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return user;
    }


    public static User fromString(String jsonString) throws JSONException {
        return fromJson(new JSONObject(jsonString));
    }

    public static JSONObject toJson(User user) throws JSONException {

        String name = user.getName();
        String organization = user.getOrganization();
        String phoneNumber = user.getPhoneNumber();
        String email = user.getEmail();
        String profileImageString = bitmapToString(user.getProfileImage());
        String timeAddedToHistory = user.getTimeAddedToHistory();
        Integer numConnections = user.getNumConnections();
        String uid = user.getId();
        JSONArray socialMedias = User.arrayListToJsonArray(user.getSocialMedias());

        JSONObject json = new JSONObject();
        json.put("name", name);
        json.put("uId", uid);
        json.put("organization", organization);
        json.put("phoneNumber", phoneNumber);
        json.put("email", email);
        json.put("profileImage", profileImageString);
        json.put("timeAddedToHistory", timeAddedToHistory);
        json.put("socialMedias", socialMedias);
        json.put("numConnections",numConnections);
        return json;

    }

    public static Bitmap stringToBitmap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    public static String bitmapToString(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        if (bitmap == null) return "";
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] b = stream.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    public static ArrayList<SocialMedia> jsonArrayToArrayList(JSONArray jsonArray) {
        ArrayList<SocialMedia> arrayList = new ArrayList<>();
        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    arrayList.add(SocialMedia.fromJson(jsonArray.getJSONObject(i)));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return arrayList;
    }

    public static JSONArray arrayListToJsonArray(ArrayList<SocialMedia> arrayList) {
        JSONArray jsonArray = new JSONArray();
        for (SocialMedia socialMedia : arrayList) {
            jsonArray.put(SocialMedia.toJson(socialMedia));
        }
        return jsonArray;
    }
}