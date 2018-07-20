package me.gnahum12345.fbuair.models;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;

import org.json.JSONException;
import org.json.JSONObject;


public class ProfileUser {

    //constant variables for shared preferences.
    private final static String MyPREFERENCES = "MyPrefs";
    private final static String BITMAP_KEY = "bitmap";
    private final static String NAME_KEY = "name";

    private String name;
    private Bitmap ivProfileImage;

    public ProfileUser(Context context) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        String current_user = sharedpreferences.getString("current_user", null);
        User user = new User();
        try {
            user = User.fromJson(new JSONObject(current_user));
        } catch (JSONException e) {
            e.printStackTrace();
            user.setName("Unknown Name");
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

    public String convertToString() {
        String bitmap = User.bitmapToString(ivProfileImage);
        JSONObject jsonProfile = new JSONObject();
        try {
            jsonProfile.put(BITMAP_KEY, getIvProfileImage());
            jsonProfile.put(NAME_KEY, getName());
        } catch (JSONException e) {
            e.printStackTrace();
            //TODO handle the exception properly.
        }
        return jsonProfile.toString();
    }
}
