package me.gnahum12345.fbuair.models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

public class User {
    public String name;
    public String organization;
    public String phoneNumber;
    public String email;
    public String facebookURL;
    public Bitmap profileImage;
    //public Date createdAt;

    public String getName() { return name; }

    public String getOrganization() { return organization; }

    public String getPhoneNumber() { return phoneNumber; }

    public String getEmail() { return email; }

    public String getFacebookURL() { return facebookURL; }

    //public Date getCreatedAt() { return createdAt; }

    //public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Bitmap getProfileImage() { return profileImage; }

    public void setIvProfileImage(Bitmap ivProfileImage) { this.profileImage = ivProfileImage; }

    public void setName(String name) { this.name = name; }

    public void setOrganization(String organization) { this.organization = organization; }

    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public void setEmail(String email) { this.email = email; }

    public void setFacebookURL(String facebookURL) { this.facebookURL = facebookURL; }

    public static User fromJson(JSONObject json) throws JSONException {
        User user = new User();
        user.name = json.getString("name");
        user.organization = json.getString("organization");
        user.phoneNumber = json.getString("phoneNumber");
        user.email = json.getString("email");
        user.facebookURL = json.getString("facebookURL");
        String profileImage = json.getString("ivProfileImage");
        user.profileImage = stringToBitmap(profileImage);
        return user;

    }

    public static JSONObject toJson(User user) throws JSONException{
        String name = user.getName();
        String organization = user.getOrganization();
        String phoneNumber = user.getPhoneNumber();
        String email = user.getEmail();
        String facebookURL = user.getFacebookURL();
        String profileImageString = bitmapToString(user.getProfileImage());

        JSONObject json = new JSONObject();
        json.put("name", name);
        json.put("organization", organization);
        json.put("phoneNumber", phoneNumber);
        json.put("email", email);
        json.put("facebookURL", facebookURL);
        json.put("profileImage", profileImageString);

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
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        String temp=Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }



}
