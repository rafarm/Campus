package com.iesnules.apps.campus.backend.model;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

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

    private String creationDate;

    public GroupRecord() {}

    public String getGroupName() {return groupName;}

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }



    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }



    public String getCreationDate() {return creationDate;  }

    public void setCreationDate(String creationDate) { this.creationDate = creationDate; }
}
