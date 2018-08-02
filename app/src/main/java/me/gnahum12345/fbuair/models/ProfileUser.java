package me.gnahum12345.fbuair.models;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ProfileUser {

    //constant variables for shared preferences.
    public final static String MyPREFERENCES = "MyPrefs";
    private final static String BITMAP_KEY = "bitmap";
    private final static String NAME_KEY = "name";

    private String name;
    private Bitmap ivProfileImage;

    public ProfileUser(Context context) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        String current_user = sharedpreferences.getString("current_user", null);
        User user = new User();
        // TODO: Clean this up.
        try {
            if (current_user != null) {
                user = User.fromJson(new JSONObject(current_user));
            } else {
                throw new JSONException("current user was null");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        setName(user.getName());
        setIvProfileImage(user.getProfileImage());
    }

    public ProfileUser(String name, Bitmap ivProfileImage) {
        this.name = name;
        this.ivProfileImage = ivProfileImage;
    }

    public static ProfileUser fromJSONString(String profileUser) throws JSONException {
        JSONObject profile = new JSONObject(profileUser);

        String name = profile.getString(NAME_KEY);
        String bitmapString = profile.getString(BITMAP_KEY);
        Bitmap image = User.stringToBitmap(bitmapString);

        return new ProfileUser(name, image);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Bitmap getIvProfileImage() {
        return ivProfileImage;
    }

    public void setIvProfileImage(Bitmap ivProfileImage) {
        this.ivProfileImage = ivProfileImage;
    }

    @Override
    public String toString() {
        return toJSON().toString();
    }

    public File toFile(Context context) {
        File f = context.getFileStreamPath("out.txt");

        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try(FileOutputStream writer = context.openFileOutput(f.getName(), Context.MODE_PRIVATE)) {
            writer.write(toString().getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }


        return f;
    }

    public String toString(String exclude) {
        JSONObject obj = toJSON();
        switch (exclude) {
            case "image":
                try {
                    obj.put(BITMAP_KEY, "");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case "name":
                try {
                    obj.put(NAME_KEY, "");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }
        return obj.toString();
    }

    private JSONObject toJSON() {
        String bitmap = "";
        if (ivProfileImage != null) {
            bitmap = User.bitmapToString(ivProfileImage);
        }
        JSONObject jsonProfile = new JSONObject();
        try {
            jsonProfile.put(BITMAP_KEY, bitmap);
            jsonProfile.put(NAME_KEY, getName());
        } catch (JSONException e) {
            e.printStackTrace();
            //TODO handle the exception properly.
        }
        return jsonProfile;
    }
}
