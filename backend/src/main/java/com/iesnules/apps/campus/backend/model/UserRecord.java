package com.iesnules.apps.campus.backend.model;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
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

    private String centerName;

    private String description;

    private String studiesType;

    private String twitter;

    public UserRecord() {

    }

    public Long getId() {
        return id;
    }

    /*
    public Key getKey() {
        Key key = null;

        if (id != null) {
            KeyFactory.createKey(this.getClass().getName(), id);
        }

        return key;
    }
    */
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

    public String getCenterName() {
        return centerName;
    }

    public void setCenterName(String centerName) {
        this.centerName = centerName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStudiesType() {
        return studiesType;
    }

    public void setStudiesType(String studiesType) {
        this.studiesType = studiesType;
    }

    public String getTwitter() {
        return twitter;
    }

    public void setTwitter(String twitter) {
        this.twitter = twitter;
    }
}
