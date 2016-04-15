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
    private String nameGroup;

    private String description;

    private String groupUsers;

    public GroupRecord() {}

    public String getNameGroup() {
        return nameGroup;
    }

    public void setNameGroup(String nameGroup) {
        this.nameGroup = nameGroup;
    }



    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }



    public String getGroupUsers() {
        return groupUsers;
    }

    public void setGroupUsers(String groupUsers) {
        this.groupUsers = groupUsers;
    }
}
