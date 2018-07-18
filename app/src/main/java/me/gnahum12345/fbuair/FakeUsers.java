package me.gnahum12345.fbuair;

import org.json.JSONException;
import org.json.JSONObject;

public class FakeUsers {

    // placeholder users to add
    public static JSONObject jsonUser0;
    {
        try {
            // populate fields
            jsonUser0 = new JSONObject()
                    .put("name", "Mariam Diallo")
                    .put("phone", "2938472633")
                    .put("email", "mariam@gmail.com")
                    .put("organization", "Facebook")
                    .put("facebookURL", "https://www.facebook.com/ari.diallo.9");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static JSONObject jsonUser1;
    {
        try {
            // populate fields
            jsonUser1 = new JSONObject()
                    .put("name", "Foo Bar")
                    .put("phone", "5478392306")
                    .put("email", "foobar@gmail.com")
                    .put("organization", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // duplicate name
    public static JSONObject jsonUser2;
    {
        try {
            // populate fields
            jsonUser2 = new JSONObject()
                    .put("name", "Foo Bar")
                    .put("phone", "7482034937")
                    .put("email", "")
                    .put("organization", "Google");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // duplicate number
    public static JSONObject jsonUser3;
    {
        try {
            // populate fields
            jsonUser3 = new JSONObject()
                    .put("name", "Mary Smith")
                    .put("phone", "5478392306")
                    .put("email", "mary@gmail.com")
                    .put("organization", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // duplicate email
    public static JSONObject jsonUser4;
    {
        try {
            // populate fields
            jsonUser4 = new JSONObject()
                    .put("name", "James Smith")
                    .put("phone", "4958203748")
                    .put("email", "foobar@gmail.com")
                    .put("organization", "Airbnb");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public static JSONObject jsonUser5;
    {
        try {
            // populate fields
            jsonUser5 = new JSONObject()
                    .put("name", "James Smith")
                    .put("phone", "2039481726")
                    .put("email", "")
                    .put("organization", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public static JSONObject jsonUser6;
    {
        try {
            // populate fields
            jsonUser6 = new JSONObject()
                    .put("name", "James")
                    .put("phone", "2039481726")
                    .put("email", "")
                    .put("organization", "Amazon");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public static JSONObject jsonUser7;
    {
        try {
            // populate fields
            jsonUser7 = new JSONObject()
                    .put("name", "Lo")
                    .put("phone", "2938401927")
                    .put("email", "")
                    .put("organization", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
