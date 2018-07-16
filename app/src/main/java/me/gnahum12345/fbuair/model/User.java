package me.gnahum12345.fbuair.model;

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

    public void setFirstName(String firstName) { this.firstName = firstName; }

    public void setLastName(String lastName) { this.lastName = lastName; }

    public void setOrganization(String organization) { this.organization = organization; }

    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public void setEmail(String email) { this.email = email; }

    public void setAddress(String address) { this.address = address; }

    public void setFacebookURL(String facebookURL) { this.facebookURL = facebookURL; }

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

    public static JSONObject toJson() throws JSONException{
        User user = new User();
        String firstName = user.getFirstName();
        String lastName = user.getLastName();
        String organization = user.getOrganization();
        String email = user.getEmail();
        String address = user.getAddress();
        String facebookURL = user.getFacebookURL();

        JSONObject json = new JSONObject();
        json.put("firstName", firstName);
        json.put("lastName", lastName);
        json.put("organization", organization);
        json.put("email", email);
        json.put("address", address);
        json.put("facebookURL", facebookURL);

        System.out.print(json);
        return json;

    }



}
