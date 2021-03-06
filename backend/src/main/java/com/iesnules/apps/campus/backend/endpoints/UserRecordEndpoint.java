package com.iesnules.apps.campus.backend.endpoints;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.api.server.spi.response.NotFoundException;
import com.google.appengine.api.oauth.OAuthRequestException;
import com.google.appengine.api.users.User;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.google.appengine.repackaged.com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.appengine.repackaged.com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.appengine.repackaged.com.google.api.client.http.javanet.NetHttpTransport;
import com.google.appengine.repackaged.com.google.api.client.json.jackson.JacksonFactory;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.cmd.Query;
import com.iesnules.apps.campus.backend.Constants;
import com.iesnules.apps.campus.backend.model.UserRecord;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Named;

import static com.googlecode.objectify.ObjectifyService.ofy;

@Api(
        name = "user",
        version = "v1",
        resource = "userRecord",
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
public class UserRecordEndpoint {

    static {
        ObjectifyService.register(UserRecord.class);
    }

    private static final Logger logger = Logger.getLogger(UserRecordEndpoint.class.getName());

    private static final int DEFAULT_LIST_LIMIT = 20;

    /**
     * Returns the {@link UserRecord} with the corresponding ID.
     *
     * @param id the ID of the entity to be retrieved
     * @return the entity with the corresponding ID
     * @throws NotFoundException if there is no {@code UserRecord} with the provided ID.
     * @throws OAuthRequestException if request is not from a valid user.
     */
    @ApiMethod(
            name = "get",
            path = "user/{id}",
            httpMethod = ApiMethod.HttpMethod.GET)
    public UserRecord get(@Named("id") Long id, User user) throws NotFoundException,
            OAuthRequestException {
        logger.info("Getting UserRecord with ID: " + id);

        UserRecord userRecord = null;

        if (user == null) {
            throw new OAuthRequestException("Unauthorized request.");
        }
        else {
            userRecord = ofy().load().type(UserRecord.class).id(id).now();
            if (userRecord == null) {
                throw new NotFoundException("Could not find UserRecord with ID: " + id);
            }
        }

        return userRecord;
    }

    /**
     * Returns the {@link UserRecord} with the corresponding Google userId.
     *
     * @param userId the userId of the entity to be retrieved
     * @return the entity with the corresponding userId
     * @throws NotFoundException if there is no {@code UserRecord} with the provided userId.
     */
    @ApiMethod(
            name = "search",
            path = "user.search/{userId}",
            httpMethod = ApiMethod.HttpMethod.GET)
    public UserRecord search(@Nonnull @Named("userId") String userId, User user)
            throws NotFoundException, OAuthRequestException {
        logger.info("Getting UserRecord with userId: " + userId);

        UserRecord userRecord = null;

        if (user == null) {
            throw new OAuthRequestException("Unauthorized request.");
        }
        else {
            userRecord = ofy().load().type(UserRecord.class).filter("userId", userId).first().now();
            if (userRecord == null) {
                throw new NotFoundException("Could not find UserRecord with userId: " + userId);
            }
        }

        return userRecord;
    }

    /**
     * Creates a new {@code UserRecord} by validating the received auth token.
     *
     * @param authToken the token to be validated
     * @return a new user entity created from the validated token
     * @throws NotFoundException if the token isn't valid
     */
    @ApiMethod(
            name = "register",
            path = "user.register/{authToken}",
            httpMethod = ApiMethod.HttpMethod.GET)
    public UserRecord register(@Nonnull @Named("authToken") String authToken)
            throws GeneralSecurityException {

        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(),
                new JacksonFactory())
                .setAudience(Arrays.asList(Constants.WEB_CLIENT_ID))
                .setIssuer("https://accounts.google.com")
                .build();

        GoogleIdToken idToken;
        UserRecord record =  null;

        try {
            idToken = verifier.verify(authToken);
        } catch (GeneralSecurityException e) {
            throw new GeneralSecurityException("Id token could not be verified: " + authToken);
        } catch (IOException e) {
            throw new GeneralSecurityException("Id token could not be verified: " + authToken);
        }

        if (idToken != null) {
            String userId = idToken.getPayload().getSubject();

            try {
                record = ofy().load().type(UserRecord.class)
                        .filter("userId", userId).first().safe();
            } catch (com.googlecode.objectify.NotFoundException e) {
                record = new UserRecord();
                record.setUserId(userId);

                ofy().save().entity(record).now();
                logger.info("Created UserRecord.");
            }
        }

        return ofy().load().entity(record).now();
    }

    /**
     * Updates an existing {@code UserRecord}.
     *
     * //@param id         the ID of the entity to be updated
     * @param userRecord the desired state of the entity
     * @return the updated version of the entity
     * @throws NotFoundException if the {@code id} does not correspond to an existing
     *                           {@code UserRecord}
     * @throws OAuthRequestException if request is originated by a not authenticated user or the
     *          user is trying to update other user's profile.
     */
    @ApiMethod(
            name = "update",
            path = "user",
            httpMethod = ApiMethod.HttpMethod.PUT)
    public UserRecord update(UserRecord userRecord, User user)
            throws NotFoundException, OAuthRequestException {

        UserRecord record = null;

        if (user == null) {
            throw new OAuthRequestException("Unauthorized access.");
        }
        else {
            // Only authenticated user should change her own profile data...
            // TODO: It should be done that way, but currently User.getUserId() returns null...
            //if (userRecord.getId().equals(id)) {
                checkExists(userRecord.getId());
                ofy().save().entity(userRecord).now();
                logger.info("Updated UserRecord: " + userRecord);

                record = ofy().load().entity(userRecord).now();
            //}
            //else {
            //    throw new OAuthRequestException("Unauthorized access: User is not profile owner");
            //}
        }

        return record;
    }

    /**
     * Deletes the specified {@code UserRecord}.
     *
     * @param id the ID of the entity to delete
     * @throws NotFoundException if the {@code id} does not correspond to an existing
     *                           {@code UserRecord}
     */
    @ApiMethod(
            name = "remove",
            path = "user/{id}",
            httpMethod = ApiMethod.HttpMethod.DELETE)
    public void remove(@Named("id") Long id, User user) throws NotFoundException,
            OAuthRequestException {
        if (user == null) {
            throw new OAuthRequestException("Unauthorized access.");
        }
        else {
            checkExists(id);
            ofy().delete().type(UserRecord.class).id(id).now();
            logger.info("Deleted UserRecord with ID: " + id);
        }
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
            path = "user",
            httpMethod = ApiMethod.HttpMethod.GET)
    public CollectionResponse<UserRecord> list(@Nullable @Named("cursor") String cursor,
                                               @Nullable @Named("limit") Integer limit,
                                               User user) throws OAuthRequestException {
        CollectionResponse<UserRecord> response = null;

        if (user == null) {
            throw new OAuthRequestException("Unauthorized access.");
        }
        else {
            limit = limit == null ? DEFAULT_LIST_LIMIT : limit;
            List<UserRecord> userRecordList = new ArrayList<UserRecord>(limit);
            Query<UserRecord> query = ofy().load().type(UserRecord.class).limit(limit);
            if (cursor != null) {
                query = query.startAt(Cursor.fromWebSafeString(cursor));
            }
            QueryResultIterator<UserRecord> queryIterator = query.iterator();

            while (queryIterator.hasNext()) {
                userRecordList.add(queryIterator.next());
            }

            response = CollectionResponse.<UserRecord>builder()
                    .setItems(userRecordList)
                    .setNextPageToken(queryIterator.getCursor().toWebSafeString())
                    .build();
        }

        return response;
    }

    private void checkExists(Long id) throws NotFoundException {
        try {
            ofy().load().type(UserRecord.class).id(id).safe();
        } catch (com.googlecode.objectify.NotFoundException e) {
            throw new NotFoundException("Could not find UserRecord with ID: " + id);
        }
    }
}