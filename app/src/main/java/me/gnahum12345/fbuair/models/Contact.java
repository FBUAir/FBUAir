package me.gnahum12345.fbuair.models;

import me.gnahum12345.fbuair.managers.MyUserManager;

// contact class containing user contact info
public class Contact {
    String phone;
    String email;
    boolean isAdded;

    public Contact(String uid) {
        User user = MyUserManager.getInstance().getUser(uid);
        this.phone = user.getPhoneNumber();
        this.email = user.getEmail();
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public boolean isAdded() {
        return isAdded;
    }

    public void setAdded(boolean added) {
        isAdded = added;
    }

    public boolean isEmpty() {
        return phone.isEmpty() && email.isEmpty();
    }
}