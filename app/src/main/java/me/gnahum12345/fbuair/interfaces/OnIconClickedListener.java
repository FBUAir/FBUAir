package me.gnahum12345.fbuair.interfaces;

import me.gnahum12345.fbuair.models.Icon;
import me.gnahum12345.fbuair.models.User;

public interface OnIconClickedListener {
    /** adds social media object to user's profile */
    void addMedia(Icon icon);
    /** removes social media object from user's profile */
    void removeMedia(Icon icon);
}
