package me.gnahum12345.fbuair.utils;

import org.json.JSONException;
import org.json.JSONObject;

public class FakeUsers {

    // placeholder users to add
    public JSONObject jsonUser1;
    // duplicate name
    public JSONObject jsonUser2;
    // duplicate number
    public JSONObject jsonUser3;
    // duplicate email
    public JSONObject jsonUser4;
    public JSONObject jsonUser5;
    // has +1 in number
    public JSONObject jsonUser6;
    // no last name
    public JSONObject jsonUser7;
    public JSONObject jsonUser8;

    public FakeUsers() {
        populateUsers();
    }

    private void populateUsers() {
        try {
            // populate fields
            jsonUser1 = new JSONObject()
                    .put("name", "Reese Jones")
                    .put("phoneNumber", "5478392306")
                    .put("email", "r2345@yahoo.com")
                    .put("organization", "Pinterest")
                    .put("uid", "23232")
                    .put("facebookURL", "")
                    .put("profileImage", "")
                    .put("instagramURL", "")
                    .put("linkedInURL", "");

            jsonUser2 = new JSONObject()
                    .put("name", "Reese Jones")
                    .put("uid", "13232")
                    .put("phoneNumber", "7482034937")
                    .put("email", "")
                    .put("organization", "Google")
                    .put("facebookURL", "")
                    .put("profileImage", "")
                    .put("instagramURL", "")
                    .put("linkedInURL", "");

            jsonUser3 = new JSONObject()
                    .put("name", "Mary Smith")
                    .put("uid", "232132")
                    .put("phoneNumber", "5478392306")
                    .put("email", "mary@gmail.com")
                    .put("organization", "")
                    .put("facebookURL", "")
                    .put("profileImage", "")
                    .put("instagramURL", "")
                    .put("linkedInURL", "");

            jsonUser4 = new JSONObject()
                    .put("name", "Ryan Smith")
                    .put("phoneNumber", "4958203748")
                    .put("email", "r2345@yahoo.com")
                    .put("uid", "233232")
                    .put("organization", "Airbnb")
                    .put("facebookURL", "")
                    .put("profileImage", "")
                    .put("instagramURL", "")
                    .put("linkedInURL", "");

            jsonUser5 = new JSONObject()
                    .put("name", "Gaby Nahum")
                    .put("phoneNumber", "2039481726")
                    .put("email", "gabyn@yahoo.com")
                    .put("organization", "Facebook")
                    .put("uid", "232432")
                    .put("facebookURL", "https://www.facebook.com/gnahum12345")
                    .put("profileImage", "")
                    .put("instagramURL", "")
                    .put("linkedInURL", "");

            jsonUser6 = new JSONObject()
                    .put("name", "Gaby Garcia")
                    .put("phoneNumber", "19283742837")
                    .put("email", "gabyg@gmail.com")
                    .put("uid", "263232")
                    .put("organization", "Facebook")
                    .put("facebookURL", "https://www.facebook.com/profile.php?id=100009783826406")
                    .put("profileImage", "")
                    .put("instagramURL", "")
                    .put("linkedInURL", "");

            jsonUser7 = new JSONObject()
                    .put("name", "Mike")
                    .put("phoneNumber", "2938401927")
                    .put("email", "mike@fb.com")
                    .put("uid", "232382")
                    .put("organization", "Facebook")
                    .put("facebookURL", "https://www.facebook.com/mikecole20")
                    .put("profileImage", "")
                    .put("instagramURL", "")
                    .put("linkedInURL", "");

            jsonUser8 = new JSONObject()
                    .put("name", "Mariam Diallo")
                    .put("phoneNumber", "2938472633")
                    .put("email", "mariam@gmail.com")
                    .put("organization", "Facebook")
                    .put("facebookURL", "https://www.facebook.com/ari.diallo.9")
                    .put("instagramURL", "https://www.instagram.com/mariamdiallo9/")
                    .put("linkedInURL", "https://www..com/in/mariamdiallo9/")
                    .put("uid", "203232")
                    .put("profileImage", "");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}