package me.gnahum12345.fbuair.interfaces;

import android.support.annotation.Nullable;
import android.support.v4.util.Pair;
import android.view.ActionMode;
import android.view.View;

public interface OnFragmentChangeListener {
    void launchDetails(String uid, View view);
    void launchEditProfile();
    void launchUrlView(String url);
    void onDetailsBackPressed();
    void deleteAccount();
    void setMenuVisible(boolean flag);
    void setBottomNavigationVisible(boolean flag);
    void setActionModeVisible(boolean flag, @Nullable ActionMode.Callback callback);
    void hideProgressBar();
}
