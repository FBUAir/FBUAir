package me.gnahum12345.fbuair.interfaces;

import android.view.ActionMode;
import android.view.View;

public interface OnFragmentChangeListener {
    void launchDetails(String uid, View view);
    void launchEditProfile();
    void launchUrlView(String url);
    void onDetailsBackPressed();
    void deleteAccount();
    void launchActionMode(ActionMode.Callback callback);
    void setMenuVisible(boolean flag);
    void setBottomNavigationVisible(boolean flag);
}
