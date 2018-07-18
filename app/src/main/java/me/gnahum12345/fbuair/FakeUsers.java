package me.gnahum12345.fbuair;

import org.json.JSONException;
import org.json.JSONObject;

public class FakeUsers {
    // placeholder users to add
    public JSONObject jsonUser1;
    {
        try {
            // populate fields
            jsonUser1 = new JSONObject()
                    .put("name", "Reese Jones")
                    .put("phone", "5478392306")
                    .put("email", "r2345@yahoo.com")
                    .put("organization", "Pinterest")
                    .put("facebookURL", "")
                    .put("profileImage", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // duplicate name
    public JSONObject jsonUser2;
    {
        try {
            // populate fields
            jsonUser2 = new JSONObject()
                    .put("name", "Reese Jones")
                    .put("phone", "7482034937")
                    .put("email", "")
                    .put("organization", "Google")
                    .put("facebookURL", "")
                    .put("profileImage", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // duplicate number
    public JSONObject jsonUser3;
    {
        try {
            // populate fields
            jsonUser3 = new JSONObject()
                    .put("name", "Mary Smith")
                    .put("phone", "5478392306")
                    .put("email", "mary@gmail.com")
                    .put("organization", "")
                    .put("facebookURL", "")
                    .put("profileImage", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // duplicate email
    public JSONObject jsonUser4;
    {
        try {
            // populate fields
            jsonUser4 = new JSONObject()
                    .put("name", "Ryan Smith")
                    .put("phone", "4958203748")
                    .put("email", "r2345@yahoo.com")
                    .put("organization", "Airbnb")
                    .put("facebookURL", "")
                    .put("profileImage", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public JSONObject jsonUser5;
    {
        try {
            // populate fields
            jsonUser5 = new JSONObject()
                    .put("name", "Gaby Nahum")
                    .put("phone", "2039481726")
                    .put("email", "gabyn@yahoo.com")
                    .put("organization", "Facebook")
                    .put("facebookURL", "https://www.facebook.com/gnahum12345")
                    .put("profileImage", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    // has +1 in number
    public JSONObject jsonUser6;
    {
        try {
            // populate fields
            jsonUser6 = new JSONObject()
                    .put("name", "Gaby Garcia")
                    .put("phone", "19283742837")
                    .put("email", "gabyg@gmail.com")
                    .put("organization", "Facebook")
                    .put("facebookURL", "https://www.facebook.com/profile.php?id=100009783826406")
                    .put("profileImage", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    // no last name
    public JSONObject jsonUser7;
    {
        try {
            // populate fields
            jsonUser7 = new JSONObject()
                    .put("name", "Mike")
                    .put("phone", "2938401927")
                    .put("email", "mike@fb.com")
                    .put("organization", "Facebook")
                    .put("facebookURL", "https://www.facebook.com/mikecole20")
                    .put("profileImage", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public JSONObject jsonUser8;
    {
        try {
            // populate fields
            jsonUser8 = new JSONObject()
                    .put("name", "Mariam Diallo")
                    .put("phone", "2938472633")
                    .put("email", "mariam@gmail.com")
                    .put("organization", "Facebook")
                    .put("facebookURL", "https://www.facebook.com/ari.diallo.9")
                    .put("profileImage", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
