package me.gnahum12345.fbuair.model;

import org.json.JSONException;
import org.json.JSONObject;

public class Gaby {
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

    public static Gaby fromJson(JSONObject json) throws JSONException {
        Gaby gaby = new Gaby();
        gaby.firstName = json.getString("firstName");
        gaby.lastName = json.getString("lastName");
        gaby.organization = json.getString("organization");
        gaby.phoneNumber = json.getString("phoneNumber");
        gaby.email = json.getString("email");
        gaby.address = json.getString("address");
        gaby.facebookURL = json.getString("facebookURL");
        return gaby;

    }


}
