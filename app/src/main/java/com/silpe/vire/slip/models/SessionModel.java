package com.silpe.vire.slip.models;

import com.silpe.vire.slip.dtos.SlipUser;

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

    private SlipUser user;

    private SessionModel() {
    }

    public SlipUser getUser() {
        return user;
    }

    public void setUser(SlipUser user) {
        this.user = user;
    }

}
