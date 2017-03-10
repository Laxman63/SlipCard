package com.silpe.vire.slip.models;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.silpe.vire.slip.dtos.User;

// TODO Link this class with SharedPreferences to gracefully handle and cache information

/**
 * Singleton class that contains the current session models.
 */
public class SessionModel {

    private static final String USER_SESSION_CACHE = "userSession";
    private static final String USER_SESSION_KEY = "user";

    private static SessionModel model = null;

    public static SessionModel get() {
        if (model == null) {
            model = new SessionModel();
        }
        return model;
    }

    private SessionModel() {
    }

    private User user;

    public User getUser() {
        return user;
    }

    public User getUser(Context context) {
        if (user != null) return user;
        return readUser(context);
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setUser(User user, Context context) {
        setUser(user);
        cacheUser(context);
    }

    public void cacheUser(@NonNull Context context) {
        if (user == null) return;
        SharedPreferences userSessionCache = context.getSharedPreferences(USER_SESSION_CACHE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = userSessionCache.edit();
        editor.putString(USER_SESSION_KEY, user.encode());
        editor.commit();
    }

    public User readUser(@NonNull Context context) {
        SharedPreferences userSessionCache = context.getSharedPreferences(USER_SESSION_CACHE, Context.MODE_PRIVATE);
        String serial = userSessionCache.getString(USER_SESSION_KEY, null);
        return serial == null ? null : new User().decode(serial);
    }

}
