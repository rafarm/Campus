package com.iesnules.apps.campus.model;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.iesnules.apps.campus.backend.user.model.UserRecord;

/**
 * This class stores user's google and our own profiles
 *
 * Created by rafa on 24/3/16.
 */
public class UserProfile {
    private GoogleSignInAccount mGoogleAccount;
    private UserRecord mUserRecord;

    public UserProfile(GoogleSignInAccount googleSignInAccount, UserRecord userRecord) {
        mGoogleAccount = googleSignInAccount;
        mUserRecord = userRecord;
    }

    public GoogleSignInAccount getGoogleAccount() {
        return mGoogleAccount;
    }

    public UserRecord getUserRecord() {
        return mUserRecord;
    }

    public void setUserRecord(UserRecord record) {
        mUserRecord = record;
    }

    public String getUserName() {
        String name = mUserRecord.getNickName();

        if (name == null) {
            name = mGoogleAccount.getDisplayName();
        }

        return name;
    }

    public String getUserAccountName() {
        return mGoogleAccount.getEmail();
    }
}
