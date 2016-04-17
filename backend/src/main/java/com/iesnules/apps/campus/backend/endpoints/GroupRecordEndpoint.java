package com.iesnules.apps.campus.backend.endpoints;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.NotFoundException;
import com.google.appengine.api.oauth.OAuthRequestException;
import com.google.appengine.api.users.User;
import com.googlecode.objectify.ObjectifyService;
import com.iesnules.apps.campus.backend.Constants;
import com.iesnules.apps.campus.backend.model.GroupRecord;
import com.iesnules.apps.campus.backend.model.UserRecord;


import java.util.logging.Logger;

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
public class GroupRecordEndpoint{

    static { ObjectifyService.register(GroupRecord.class);}

    private static final Logger logger = Logger.getLogger(GroupRecordEndpoint.class.getName());

    private static final int DEFAULT_LIST_LIMIT = 20;

    @ApiMethod(
            name = "create",
            path = "group.create/",
            httpMethod = ApiMethod.HttpMethod.GET)
    public GroupRecord create(GroupRecord groupRecord, User user)
            throws OAuthRequestException {

        GroupRecord record = null;

        if (user == null) {
            throw new OAuthRequestException("Unauthorized access.");
        }
        else {

            ofy().save().entity(groupRecord).now();
            logger.info("Updated GroupRecord: " + groupRecord);

            record = ofy().load().entity(groupRecord).now();

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

    private void checkExists(Long id) throws NotFoundException {
        try {
            ofy().load().type(UserRecord.class).id(id).safe();
        } catch (com.googlecode.objectify.NotFoundException e) {
            throw new NotFoundException("Could not find UserRecord with ID: " + id);
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



}