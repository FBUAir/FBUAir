package me.gnahum12345.fbuair.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.gnahum12345.fbuair.models.SocialMedia;

public class SocialMediaUtils {
    // make sure drawable resource "ic_[social media name (lowercase)]" exists
    static String[] socialMediaNames = {"Facebook", "Instagram", "Twitter", "Snapchat", "LinkedIn",
            "Google", "WhatsApp", "Youtube", "Pinterest", "Tumblr", "Soundcloud",
            "Github"};

    static String[] socialMediaUrls = { "facebook.com", "instagram.com", "twitter.com",
            "snapchat.com/add", "linkedin.com/in", "plus.google.com", "wa.me",
            "youtube.com/channel", "pinterest.com", "tumblr.com",
            "soundcloud.com", "github.com"};

    static HashMap<String, String> urlMap = getUrlMap();

    // gets corresponding resource drawable for given social media
    public static Drawable getIconDrawable(Context context, SocialMedia socialMedia) {
        String drawableName = "ic_" + socialMedia.getName().toLowerCase();
        int resId = context.getResources().getIdentifier
                (drawableName, "drawable", context.getPackageName());
        return context.getResources().getDrawable(resId, null);
    }

    // returns list of all socialMedias
    public static List<SocialMedia> getAllSocialMedias() {
        List<SocialMedia> socialMedias = new ArrayList<>();
        for (String socialMediaName : socialMediaNames) {
            SocialMedia socialMedia = new SocialMedia();
            socialMedia.setName(socialMediaName);
            socialMedias.add(socialMedia);
        }
        return socialMedias;
    }

    // creates hashmap of social media to its profile url domain
    public static HashMap<String, String> getUrlMap() {
        HashMap<String, String> urlMap = new HashMap<String, String>();
        for (int i = 0; i < socialMediaNames.length; i++) {
            urlMap.put(socialMediaNames[i], socialMediaUrls[i]);
        }
        return urlMap;
    }

    // returns profile url created from username and domain
    public static String getProfileUrl(String socialMediaName, String username) {
        String profileUrl;
        String prefix = "https://www.";
        if (socialMediaName.equals("Tumblr")) {
            profileUrl = prefix + username + "." + urlMap.get(socialMediaName);
        } else {
            profileUrl = prefix + urlMap.get(socialMediaName) + "/" + username;
        }
        return profileUrl;
    }

    // adds social media object to list according to order defined by socialMediaNames
    // or edits old one if already existed
    public static void addSocialMedia (SocialMedia socialMedia, List<SocialMedia> socialMedias) {
        int targetIndex = getIndex(socialMedia.getName(), socialMediaNames);
        int currentIndex;
        int previousIndex = 0;
        for (int i = 0; i < socialMedias.size(); i++) {
            currentIndex = getIndex(socialMedias.get(i).getName(), socialMediaNames);
            if (targetIndex == currentIndex) {
                socialMedias.get(i).setUsername(socialMedia.getUsername());
                return;
            }
            else if (targetIndex > previousIndex && targetIndex < currentIndex) {
                socialMedias.add(i, socialMedia);
                return;
            }
            previousIndex = currentIndex;
        }
        socialMedias.add(socialMedia);
    }

    private static int getIndex(String target, String[] list) {
        for (int i = 0; i < list.length; i++) {
            if (list[i] == target) return i;
        }
        throw new RuntimeException("SocialMediaUtils - Using getIndex on element not in list");
    }
}
