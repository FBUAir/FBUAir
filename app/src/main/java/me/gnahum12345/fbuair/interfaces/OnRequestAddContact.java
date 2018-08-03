package me.gnahum12345.fbuair.interfaces;

import me.gnahum12345.fbuair.activities.MainActivity;

public interface OnRequestAddContact {
    void requestAddContact(String uid, OnContactAddedCallback callback);
}
