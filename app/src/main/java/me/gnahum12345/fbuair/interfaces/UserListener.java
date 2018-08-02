package me.gnahum12345.fbuair.interfaces;

import me.gnahum12345.fbuair.models.User;

public interface UserListener {

    /**
     * This method will be used if a user has been added.
     */
    public void userAdded(User user);


    /**
     * This method will be used if a user has been deleted.
     */
    public void userRemoved(User user);


}
