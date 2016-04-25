package com.iesnules.apps.campus.backend.model;

import com.google.api.server.spi.config.AnnotationBoolean;
import com.google.api.server.spi.config.ApiResourceProperty;
import com.googlecode.objectify.Ref;
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
    List<Ref<UserRecord>> groupUsers = new ArrayList<Ref<UserRecord>>();

    @Load
    Ref<UserRecord> owner;    // Person is an @Entity

    public GroupRecord() {}

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

    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public List<Ref<UserRecord>> getGroupUsers() {
        return groupUsers;
    }

    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public UserRecord getOwner() {
        return owner.get();
    }

    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public void setOwner(UserRecord userRecord) {
        owner = Ref.create(userRecord);
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
