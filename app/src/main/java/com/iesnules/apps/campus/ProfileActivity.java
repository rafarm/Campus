package com.iesnules.apps.campus;

import android.content.Context;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Explode;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.jorgecastilloprz.FABProgressCircle;
import com.github.jorgecastilloprz.listeners.FABProgressListener;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.iesnules.apps.campus.backend.user.User;
import com.iesnules.apps.campus.backend.user.model.UserRecord;
import com.iesnules.apps.campus.fragments.ErrorDialogFragment;
import com.iesnules.apps.campus.model.UserProfile;
import com.iesnules.apps.campus.utils.RoundedTransformation;
import com.squareup.picasso.Picasso;

import java.io.IOException;

public class ProfileActivity extends AppCompatActivity
        implements ErrorDialogFragment.ErrorDialogListener, FABProgressListener {

    private UserProfile mProfile;

    // UI
    private EditText mNickNameEditText;
    private EditText mCenterNameEditText;
    private EditText mDescriptionEdiText;
    private EditText mStudiesTypeEditText;
    private EditText mTwitterEditText;
    private ImageView mGooglePhotoImageView;
    private TextView mGoogleNameTextView;
    private FABProgressCircle mUpdateFABCircle;
    private FloatingActionButton mUpdateFAB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);

        mProfile = MainActivity.getUserProfile();

        mNickNameEditText = (EditText)findViewById(R.id.nickNameEditText);
        mCenterNameEditText = (EditText)findViewById(R.id.centerNameEditText);
        mDescriptionEdiText = (EditText)findViewById(R.id.descriptionEditText);
        mStudiesTypeEditText = (EditText)findViewById(R.id.studiesTypeEditText);
        mGoogleNameTextView =  (TextView)findViewById(R.id.googleNameTextView);
        mTwitterEditText = (EditText)findViewById(R.id.twitterEditText);
        mGooglePhotoImageView = (ImageView)findViewById(R.id.googlePhoto);
        mUpdateFABCircle = (FABProgressCircle)findViewById(R.id.updateFabProgressCircle);
        mUpdateFAB = (FloatingActionButton)findViewById(R.id.updateFab);

        mUpdateFABCircle.attachListener(this);

        populateUI();
    }

    private void populateUI() {
        mGoogleNameTextView.setText(mProfile.getGoogleAccount().getDisplayName());
        int radius = mGooglePhotoImageView.getWidth() / 2;
        Picasso.with(this)
                .load(mProfile.getGoogleAccount().getPhotoUrl())
                .transform(new RoundedTransformation(radius, 0))
                .into(mGooglePhotoImageView);
        mNickNameEditText.setText(mProfile.getUserRecord().getNickName());
        mCenterNameEditText.setText(mProfile.getUserRecord().getCenterName());
        mDescriptionEdiText.setText(mProfile.getUserRecord().getDescription());
        mStudiesTypeEditText.setText(mProfile.getUserRecord().getStudiesType());
        mTwitterEditText.setText(mProfile.getUserRecord().getTwitter());
    }

    public void onUpdate(View view) {
        new UpdateUserProfileAsyncTask(this).execute();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //TODO: Add conditions to change editable textedits...
            case R.id.edit_profile:
                ;

            case android.R.id.home:
                onBackPressed();
        }

        return true;
    }
//Edit button
    /**
     * Listener methods
     */
    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        // Do nothing...
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        // Do nothing...
    }

    @Override
    public void onFABProgressAnimationEnd() {
        mUpdateFAB.setEnabled(true);
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
            updated.setDescription(mDescriptionEdiText.getText().toString());
            updated.setStudiesType(mStudiesTypeEditText.getText().toString());
            updated.setTwitter(mTwitterEditText.getText().toString());
            return updated;

        }
        @Override
        protected void onPreExecute() {
            mUpdateFAB.setEnabled(false);
            mUpdateFABCircle.show();
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
                record = service.update(record).execute();
            } catch (IOException e) {
                record = null;
                e.printStackTrace();
            }

            return record;
        }

        @Override
        protected void onPostExecute(UserRecord record) {
            if (record != null) { // Update local user profile
                mUpdateFABCircle.beginFinalAnimation();
                mProfile.setUserRecord(record);
            }
            else { // Error updating user profile
                mUpdateFABCircle.hide();
                mUpdateFAB.setEnabled(true);
                ErrorDialogFragment fragment = ErrorDialogFragment.newInstance("Error",
                        getString(R.string.prof_update_error), null, null);

                fragment.show(getSupportFragmentManager(), "update_error");
            }
        }
    }
}
