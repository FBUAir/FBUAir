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
                    .put("uId", "23232")
                    .put("profileImage", "")
                    .put("socialMedias", "[]");

            jsonUser2 = new JSONObject()
                    .put("name", "Reese Jones")
                    .put("uId", "13232")
                    .put("phoneNumber", "7482034937")
                    .put("email", "")
                    .put("organization", "Google")
                    .put("profileImage", "")
                    .put("socialMedias", "[]");

            jsonUser3 = new JSONObject()
                    .put("name", "Mary Smith")
                    .put("uId", "232132")
                    .put("phoneNumber", "5478392306")
                    .put("email", "mary@gmail.com")
                    .put("organization", "")
                    .put("profileImage", "")
                    .put("socialMedias", "[]");

            jsonUser4 = new JSONObject()
                    .put("name", "Ryan Smith")
                    .put("phoneNumber", "4958203748")
                    .put("email", "r2345@yahoo.com")
                    .put("uId", "233232")
                    .put("organization", "Airbnb")
                    .put("profileImage", "")
                    .put("socialMedias", "[]");

            jsonUser5 = new JSONObject()
                    .put("name", "Gaby Nahum")
                    .put("phoneNumber", "2039481726")
                    .put("email", "gabyn@yahoo.com")
                    .put("organization", "Facebook")
                    .put("uId", "232432")
                    .put("profileImage", "")
                    .put("socialMedias", "[]");

            jsonUser6 = new JSONObject()
                    .put("name", "Gaby Garcia")
                    .put("phoneNumber", "19283742837")
                    .put("email", "gabyg@gmail.com")
                    .put("uId", "263232")
                    .put("organization", "Facebook")
                    .put("profileImage", "")
                    .put("socialMedias", "[]");

            jsonUser7 = new JSONObject()
                    .put("name", "Mike")
                    .put("phoneNumber", "2938401927")
                    .put("email", "mike@fb.com")
                    .put("uId", "232382")
                    .put("organization", "Facebook")
                    .put("profileImage", "")
                    .put("socialMedias", "[]");

            jsonUser8 = new JSONObject()
                    .put("name", "Mariam Diallo")
                    .put("phoneNumber", "2938472633")
                    .put("email", "mariam@gmail.com")
                    .put("organization", "Facebook")
                    .put("uId", "203232")
                    .put("profileImage", "")
                    .put("socialMedias", "[]");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
