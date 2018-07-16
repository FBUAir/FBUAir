package me.gnahum12345.fbuair.Models;

import org.json.JSONException;
import org.json.JSONObject;

public class User {
    public String firstName;
    public String lastName;
    public String organization;
    public String phoneNumber;
    public String email;
    public String address;
    public String facebookURL;

    public String getFirstName() { return firstName; }

    public String getLastName() { return lastName; }

    public String getOrganization() { return organization; }

    public String getPhoneNumber() { return phoneNumber; }

    public String getEmail() { return email; }

    public String getAddress() { return address; }

    public String getFacebookURL() { return facebookURL; }

    public static User fromJson(JSONObject json) throws JSONException {
        User user = new User();
        user.firstName = json.getString("firstName");
        user.lastName = json.getString("lastName");
        user.organization = json.getString("organization");
        user.phoneNumber = json.getString("phoneNumber");
        user.email = json.getString("email");
        user.address = json.getString("address");
        user.facebookURL = json.getString("facebookURL");
        return user;

    }


}
