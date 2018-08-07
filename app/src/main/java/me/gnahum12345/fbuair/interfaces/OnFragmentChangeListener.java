package me.gnahum12345.fbuair.interfaces;

import android.view.ActionMode;

public interface OnFragmentChangeListener {
    public void launchDetails(String uid);
    public void launchEditProfile();
    public void launchUrlView(String url);
    public void deleteAccount();
    public void startAction(ActionMode.Callback callback);
}
