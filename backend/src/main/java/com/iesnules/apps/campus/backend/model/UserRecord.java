package com.iesnules.apps.campus.backend.model;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

/**
 * Created by rrubiom on 21/03/16.
 */
@Entity
public class UserRecord {

    @Id
    Long id;

    @Index
    private String userId;

    public UserRecord() {

    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
