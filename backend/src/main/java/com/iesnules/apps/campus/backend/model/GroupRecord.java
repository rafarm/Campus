package com.iesnules.apps.campus.backend.model;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.OnSave;

import java.util.Date;

/**
 * Created by robertoroig & andrestendero on 12/04/16.
 */
@Entity
public class GroupRecord {
    @Id
    Long id;
    Key<UserRecord> creator;

    @Index
    private String groupName;

    private String description;

    private Date creationDate;

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
