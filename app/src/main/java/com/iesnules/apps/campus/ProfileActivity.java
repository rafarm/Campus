package com.iesnules.apps.campus;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.iesnules.apps.campus.backend.user.User;
import com.iesnules.apps.campus.backend.user.model.UserRecord;
import com.iesnules.apps.campus.model.UserProfile;

import java.io.IOException;
import java.util.Arrays;

public class ProfileActivity extends AppCompatActivity {

    private UserProfile mProfile;

    // UI
    private EditText mNickNameEditText;
    private EditText mCenterNameEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mProfile = MainActivity.getUserProfile();

        mNickNameEditText = (EditText)findViewById(R.id.nickNameEditText);
        mCenterNameEditText = (EditText)findViewById(R.id.centerNameEditText);

        populateUI();
    }

    private void populateUI() {
        mNickNameEditText.setText(mProfile.getUserRecord().getNickName());
        mCenterNameEditText.setText(mProfile.getUserRecord().getCenterName());
    }

    public void onUpdate(View view) {
        new UpdateUserProfileAsyncTask(this).execute();
    }

    private class UpdateUserProfileAsyncTask extends AsyncTask<Void, Void, UserRecord> {

        private Context mContext;

        public UpdateUserProfileAsyncTask(Context context) {
            mContext = context;
        }

        /**
         * Clones current user record and updates it with data introduced by user.
         * @return UserRecord
         */
        private UserRecord getUpdatedRecord() {
            UserRecord updated = mProfile.getUserRecord().clone();
            updated.setNickName(mNickNameEditText.getText().toString());
            updated.setCenterName(mCenterNameEditText.getText().toString());
            return updated;
        }

        @Override
        protected UserRecord doInBackground(Void... params) {
            GoogleAccountCredential credential = GoogleAccountCredential.usingAudience(mContext,
                    getString(R.string.server_credential));
            credential.setSelectedAccountName(mProfile.getUserAccountName());

            User.Builder builder = new User.Builder(AndroidHttp.newCompatibleTransport(),
                    new AndroidJsonFactory(), credential);
            User service = builder.build();

            UserRecord record = getUpdatedRecord();
            try {
                record = service.update(record.getId(), record).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return record;
        }

        @Override
        protected void onPostExecute(UserRecord record) {
            if (record != null) { // Update local user profile
                mProfile.setUserRecord(record);
            }
            else { // Error updating user profile
                // TODO: Notify error...
            }
        }
    }
}
