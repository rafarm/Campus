package com.iesnules.apps.campus.backend.model;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

/**
 * Created by Rafa Rubio on 21/03/16.
 */
@Entity
public class UserRecord {

    @Id
    Long id;

    @Index
    private String userId;

    private String nickName;

    public UserRecord() {

    }

    public Long getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }
}
