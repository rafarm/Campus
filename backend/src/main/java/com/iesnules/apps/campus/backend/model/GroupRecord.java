package com.iesnules.apps.campus.backend.model;

import com.google.appengine.api.datastore.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Load;
import com.googlecode.objectify.annotation.OnSave;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by robertoroig & andrestendero on 12/04/16.
 */
@Entity
public class GroupRecord {
    @Id
    Long id;

    @Index
    private String groupName;

    private String description;

    private Date creationDate;

    @Index
    List<Key> groupUsers = new ArrayList<Key>();

    Key owner;    // Person is an @Entity

    public GroupRecord() {}

    public Long getId() {
        return id;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // This attribute will be readonly
    public Date getCreationDate() {
        return creationDate;
    }

    public List<Key> getGroupUsers() {
        return groupUsers;
    }

    public Key getOwner() {
        return owner;
    }

    public void setOwner(Key key) {
        owner = key;
    }

    /**
     * Set creation date on first save.
     */
    @OnSave
    void setCreationDate() {
        if (creationDate == null) {
            creationDate = new Date();
        }
    }
}
