package com.iesnules.apps.campus.backend.model;

import com.google.appengine.api.users.User;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

/**
 * Created by rrubiom on 13/04/16.
 */
@Entity
public class GoogleUserRecord {

    @Id
    Long id;

    User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
