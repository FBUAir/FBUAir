package me.gnahum12345.fbuair.interfaces;

import java.util.List;

import me.gnahum12345.fbuair.services.ConnectionService;

public interface ConnectionListener {
    //TODO: possibly change this to boolean instead of void in order to assure the endpoint has been updated.
    /**
     * This method will be called when an Endpoint needs to be updated. (Either the data is a ProfileUser or
     * it is a User.
     * @Param endpoint, the endpoint that is referred to.
     * @Param userData, the data that is sent through
     * @Param isProfile, True if the data is a ProfileUser and False if the data is a User.
     */
    public void updateEndpoint(ConnectionService.Endpoint endpoints, Object userData, boolean isProfile);

    /**
     * This method will be called when an Endpoint is connected. and the Listener can do what it
     * wishes with it.
     * @Param endpoint
     */
    public void addEndpoint(ConnectionService.Endpoint endpoint);

    /**
     * This method will be called when an Endpoint is disconnected. and the Listener can do what it
     * wishes with it.
     * @Param endpoint
     */
    public void removeEndpoint(ConnectionService.Endpoint endpoint);






}
