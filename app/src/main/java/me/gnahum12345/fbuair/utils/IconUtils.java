package me.gnahum12345.fbuair.utils;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import me.gnahum12345.fbuair.models.Icon;

public class IconUtils {
    // make sure drawable resource "ic_[social media name (lowercase)]" exists
    static String[] iconNames = {"Facebook", "Instagram", "Twitter", "Snapchat", "LinkedIn",
            "Google", "WhatsApp", "Youtube", "Reddit", "Pinterest", "Tumblr", "Soundcloud",
            "Github", "DeviantArt", "Dribbble"};

    // creates a new icon out of a name
    public static Icon getIcon(Context context, String iconName) {
        Icon icon = new Icon();
        String drawableName = "ic_" + iconName.toLowerCase();
        int resId = context.getResources().getIdentifier
                (drawableName, "drawable", context.getPackageName());
        icon.setDrawable(context.getResources().getDrawable(resId, null));
        icon.setName(iconName);
        return icon;
    }

    // returns list of all icons
    public static List<Icon> getAllIcons(Context context) {
        List<Icon> icons = new ArrayList<>();
        for (String iconName : iconNames) {
            icons.add(getIcon(context, iconName));
        }
        return icons;
    }
}
