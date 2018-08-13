package me.gnahum12345.fbuair.interfaces;

import me.gnahum12345.fbuair.models.ProfileUser;
import me.gnahum12345.fbuair.models.User;

public interface UserListener {

    /**
     * This method will be used if a user has been added.
     */
    void userAdded(User user);


    /**
     * This method will be used if a user has been deleted.
     */
    void userRemoved(User user);

    /**
     * This method will be used when the current user sends their profile to someone.
     */
    void profileSent();
}
