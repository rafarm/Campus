package com.iesnules.apps.campus.backend.endpoints;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.api.server.spi.response.NotFoundException;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.google.appengine.api.oauth.OAuthRequestException;
import com.google.appengine.api.users.User;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.cmd.Query;
import com.iesnules.apps.campus.backend.Constants;
import com.iesnules.apps.campus.backend.model.GroupRecord;
import com.iesnules.apps.campus.backend.model.UserRecord;


import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.Nullable;
import javax.inject.Named;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * Created by robertoroig & andrestendero on 15/04/16.
 */
@Api(
        name = "group",
        version = "v1",
        resource = "groupRecord",
        namespace = @ApiNamespace(
                ownerDomain = "backend.campus.apps.iesnules.com",
                ownerName = "backend.campus.apps.iesnules.com",
                packagePath = ""
        ),
        root = Constants.FRONTEND_ROOT,
        scopes = {
                Constants.EMAIL_SCOPE,
                Constants.PROFILE_SCOPE
        },
        clientIds = {
                Constants.WEB_CLIENT_ID,
                Constants.ANDROID_CLIENT_ID,
                Constants.ANDROID_DEBUG_CLIENT_ID,
                com.google.api.server.spi.Constant.API_EXPLORER_CLIENT_ID
        },
        audiences = {Constants.ANDROID_AUDIENCE}
)
public class GroupRecordEndpoint {

    static { ObjectifyService.register(GroupRecord.class); }

    private static final Logger logger = Logger.getLogger(GroupRecordEndpoint.class.getName());

    private static final int DEFAULT_LIST_LIMIT = 20;

    @ApiMethod(
            name = "insert",
            path = "group/{userId}",
            httpMethod = ApiMethod.HttpMethod.POST)
    public GroupRecord insert(GroupRecord groupRecord,@Named("userId") Long userId, User user)
            throws OAuthRequestException, IllegalArgumentException {

        GroupRecord record = null;

        if (user == null) {
            throw new OAuthRequestException("Unauthorized access.");
        }
        else if (groupRecord.getId() != null) {
            throw new IllegalArgumentException("Group already created.");
        }
        else {
            UserRecord owner = ofy().load().type(UserRecord.class).id(userId).now();
            if (owner == null) {
                throw new IllegalArgumentException("Owner user doesn't exists.");
            }
            else {
                groupRecord.setOwner(owner);
                groupRecord.getGroupUsers().add(Ref.create(owner));
                ofy().save().entity(groupRecord).now();
                logger.info("Created GroupRecord: " + groupRecord);

                record = ofy().load().entity(groupRecord).now();
            }
        }

        return record;
    }

    @ApiMethod(
            name = "delete",
            path = "group/{id}",
            httpMethod = ApiMethod.HttpMethod.DELETE)
    public void delete(@Named("id") Long id, User user) throws NotFoundException,
            OAuthRequestException {
        if (user == null) {
            throw new OAuthRequestException("Unauthorized access.");
        }
        else {
            checkExists(id);
            ofy().delete().type(GroupRecord.class).id(id).now();
            logger.info("Deleted GroupRecord with ID: " + id);
        }
    }

    @ApiMethod(
            name = "get",
            path = "group/{id}",
            httpMethod = ApiMethod.HttpMethod.GET)
    public GroupRecord get(@Named("id") Long id, User user) throws NotFoundException,
            OAuthRequestException {
        logger.info("Getting GroupRecord with ID: " + id);

        GroupRecord groupRecord = null;

        if (user == null) {
            throw new OAuthRequestException("Unauthorized request.");
        }
        else {
            groupRecord = ofy().load().type(GroupRecord.class).id(id).now();
            if (groupRecord == null) {
                throw new NotFoundException("Could not find UserRecord with ID: " + id);
            }
        }

        return groupRecord;
    }

    /**
     * List all entities.
     *
     * @param cursor used for pagination to determine which page to return
     * @param limit  the maximum number of entries to return
     * @return a response that encapsulates the result list and the next page token/cursor
     */
    @ApiMethod(
            name = "list",
            path = "group",
            httpMethod = ApiMethod.HttpMethod.GET)
    public CollectionResponse<GroupRecord> list(@Nullable @Named("cursor") String cursor,
                                               @Nullable @Named("limit") Integer limit,
                                               User user) throws OAuthRequestException {
        CollectionResponse<GroupRecord> response = null;

        if (user == null) {
            throw new OAuthRequestException("Unauthorized access.");
        }
        else {
            limit = limit == null ? DEFAULT_LIST_LIMIT : limit;
            List<GroupRecord> groupRecordList = new ArrayList<GroupRecord>(limit);
            Query<GroupRecord> query = ofy().load().type(GroupRecord.class).limit(limit);
            if (cursor != null) {
                query = query.startAt(Cursor.fromWebSafeString(cursor));
            }
            QueryResultIterator<GroupRecord> queryIterator = query.iterator();

            while (queryIterator.hasNext()) {
                groupRecordList.add(queryIterator.next());
            }

            response = CollectionResponse.<GroupRecord>builder()
                    .setItems(groupRecordList)
                    .setNextPageToken(queryIterator.getCursor().toWebSafeString())
                    .build();
        }

        return response;
    }

    @ApiMethod(
            name = "find",
            path = "group.find/{userId}",
            httpMethod = ApiMethod.HttpMethod.GET)
    public CollectionResponse<GroupRecord> find(@Named("userId") Long userId,
                                                @Nullable @Named("cursor") String cursor,
                                                @Nullable @Named("limit") Integer limit,
                                                User user) throws OAuthRequestException {
        CollectionResponse<GroupRecord> response = null;

        if (user == null) {
            throw new OAuthRequestException("Unauthorized access.");
        }
        else {
            UserRecord member = ofy().load().type(UserRecord.class).id(userId).now();
            if (member == null) {
                throw new IllegalArgumentException("User doesn't exists.");
            }
            else {
                limit = limit == null ? DEFAULT_LIST_LIMIT : limit;
                List<GroupRecord> groupRecordGroups = new ArrayList<GroupRecord>(limit);
                Query<GroupRecord> query = ofy().load().type(GroupRecord.class)
                        .filter("groupUsers", member)
                        .limit(limit);
                if (cursor != null) {
                    query = query.startAt(Cursor.fromWebSafeString(cursor));
                }
                QueryResultIterator<GroupRecord> groupsIterator = query.iterator();

                while (groupsIterator.hasNext()) {
                    groupRecordGroups.add(groupsIterator.next());
                }

                response = CollectionResponse.<GroupRecord>builder()
                        .setItems(groupRecordGroups)
                        .setNextPageToken(groupsIterator.getCursor().toWebSafeString())
                        .build();
            }
        }

        return response;
    }

    @ApiMethod(
            name = "addUser",
            path = "group.addUser/{userId}",
            httpMethod = ApiMethod.HttpMethod.PUT)
    public GroupRecord addUser(@Named("userId") Long userId, User user)
            throws OAuthRequestException {
        if (user == null) {
            throw new OAuthRequestException("Unauthorized access.");
        }
        else {
            UserRecord userRec = ofy().load().type(UserRecord.class).id(userId).now();
            if (userRec == null){
                throw new OAuthRequestException("The member it's already in the group.");
            }
            else{
                groupRecord.getGroupUsers().add(Ref.create(owner));
                ofy().save().entity(groupRecord).now();
                logger.info("Created GroupRecord: " + groupRecord);

                record = ofy().load().entity(groupRecord).now();

            }

        }
        return record;

    }

    private void checkExists(Long id) throws NotFoundException {
        try {
            ofy().load().type(GroupRecord.class).id(id).safe();
        } catch (com.googlecode.objectify.NotFoundException e) {
            throw new NotFoundException("Could not find GroupRecord with ID: " + id);
        }
    }
}