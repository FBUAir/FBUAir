package me.gnahum12345.fbuair.interfaces;

import android.support.v4.util.Pair;
import android.view.ActionMode;
import android.view.View;

public interface OnFragmentChangeListener {
    void launchDetails(String uid, Pair<View, String> p1, Pair<View, String> p2);
    void launchEditProfile();
    void launchUrlView(String url);
    void onDetailsBackPressed();
    void deleteAccount();
    void launchActionMode(ActionMode.Callback callback);
    void setMenuVisible(boolean flag);
    void setBottomNavigationVisible(boolean flag);
}
