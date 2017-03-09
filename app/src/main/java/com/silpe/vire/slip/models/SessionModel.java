package com.silpe.vire.slip.models;

import com.silpe.vire.slip.dtos.User;

/**
 * Singleton class that contains the current session models.
 */
public class SessionModel {

    private static SessionModel model = null;

    public static SessionModel get() {
        if (model == null) {
            model = new SessionModel();
        }
        return model;
    }

    private User user;

    private SessionModel() {
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}
