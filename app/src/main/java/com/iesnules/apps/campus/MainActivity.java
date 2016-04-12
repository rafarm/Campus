package com.iesnules.apps.campus;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.images.ImageManager;
import com.google.android.gms.plus.Plus;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.iesnules.apps.campus.backend.user.User;
import com.iesnules.apps.campus.backend.user.model.UserRecord;
import com.iesnules.apps.campus.dummy.DummyContent;
import com.iesnules.apps.campus.fragments.EventsFragment;
import com.iesnules.apps.campus.fragments.GroupsFragment;
import com.iesnules.apps.campus.fragments.RecentFragment;
import com.iesnules.apps.campus.fragments.ResourcesFragment;
import com.iesnules.apps.campus.model.UserProfile;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnMenuTabClickListener;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        NavigationView.OnNavigationItemSelectedListener,
        GroupsFragment.OnListFragmentInteractionListener {

    private static final String TAG = "MainActivity";

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final int RC_SIGN_IN = 9001;

    //private static GoogleSignInAccount mSignInAccount;
    private static UserProfile mUserProfile;

    private GoogleApiClient mGoogleApiClient;
    private boolean mSigningIn;

    private ImageManager mImageManager;

    //private RelativeLayout mFragmentContainer;
    private CoordinatorLayout mCoordinatorLayout;

    private TextView mUserNameTextView;
    private TextView mUserEmailTextView;
    private ImageView mUserPictureImageView;
    private BottomBar mBottomBar;

    // Fragments
    private RecentFragment mRecentFragment;
    private GroupsFragment mGroupsFragment;
    private ResourcesFragment mResourcesFragment;
    private EventsFragment mEventsFragment;
    //private Stack<Fragment> mRecentFragmentStack;
    //private Stack<Fragment> mGroupsFragmentStack;
    //private Stack<Fragment> mResourcesFragmentStack;
    //private Stack<Fragment> mEventsFragmentStack;
    private Fragment mCurrentFragment;
    private int mTagCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!checkPlayServices()) {
            // Google Play Services are required, so don't proceed until they
            // are installed.
            return;
        }

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestProfile()
                .requestEmail()
                .requestIdToken(getString(R.string.server_client_id))
                .build();

        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        // ATTENTION: This "addApi(AppIndex.API)"was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                //.addApi(AppIndex.API)
                .addApi(Plus.API)
                .build();

        mSigningIn = false;

        // Views
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        */

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        mUserNameTextView = (TextView)headerView.findViewById(R.id.userName);
        mUserEmailTextView = (TextView)headerView.findViewById(R.id.userEmail);
        mUserPictureImageView = (ImageView)headerView.findViewById(R.id.userPicture);

        mImageManager = ImageManager.create(this);

        mCoordinatorLayout = (CoordinatorLayout)findViewById(R.id.coordinator_layout);
        //mFragmentContainer = (RelativeLayout)findViewById(R.id.fragment_container);

        // Bottom bar...
        mBottomBar = BottomBar.attach(mCoordinatorLayout, savedInstanceState);
        mBottomBar.setItemsFromMenu(R.menu.bottombar_menu, new OnMenuTabClickListener() {
            @Override
            public void onMenuTabSelected(@IdRes int menuItemId) {
                switch (menuItemId) {
                    case R.id.bottomBarItemRecent:
                        switchFragment(getRecentFragment());
                        break;
                    case R.id.bottomBarItemGroups:
                        switchFragment(getGroupFragment());
                        break;
                    case R.id.bottomBarItemResources:
                        switchFragment(getResourcesFragment());
                        break;
                    case R.id.bottomBarItemEvents:
                        switchFragment(getEventsFragment());
                        break;
                }
            }

            @Override
            public void onMenuTabReSelected(@IdRes int menuItemId) {

            }
        });
    }

    /**
     * Lazily create recent fragment.
     * @return RecentFragment
     */
    private RecentFragment getRecentFragment() {
        if (mRecentFragment == null) {
            mRecentFragment = RecentFragment.newInstance("Recent", "Activities");
        }

        return mRecentFragment;
    }

    /**
     * Lazily create groups fragment.
     * @return GroupsFragment
     */
    private GroupsFragment getGroupFragment() {
        if (mGroupsFragment == null) {
            mGroupsFragment = GroupsFragment.newInstance(1);
        }

        return mGroupsFragment;
    }

    /**
     * Lazily create resources fragment.
     * @return ResourcesFragment
     */
    private ResourcesFragment getResourcesFragment() {
        if (mResourcesFragment == null) {
            mResourcesFragment = ResourcesFragment.newInstance("Resources", "List");
        }

        return mResourcesFragment;
    }

    /**
     * Lazily create events fragment.
     * @return EventsFragment
     */
    private EventsFragment getEventsFragment() {
        if (mEventsFragment == null) {
            mEventsFragment = EventsFragment.newInstance("Events", "List");
        }

        return mEventsFragment;
    }

    /**
     * Switch to a new fragment stack.
     * @param newFragment
     */
    private void switchFragment(Fragment newFragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if (mCurrentFragment != null) {
            transaction.detach(mCurrentFragment);
        }

        Fragment fragment = getSupportFragmentManager().findFragmentByTag(newFragment.getTag());
        if (fragment != null) {
            transaction.attach(fragment);
        }
        else {
            transaction.add(R.id.fragment_container, newFragment, newFragment.getClass().getName() +
                    mTagCount++);
        }

        transaction.commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        if (!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
            silentSignIn();
        }

        /*
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.iesnules.apps.campus/http/host/path")
        );
        AppIndex.AppIndexApi.start(mGoogleApiClient, viewAction);
        */
    }

    /*
    @Override
    protected void onResume() {
        super.onResume();

        // Check Google Play Services availability...
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int errCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (errCode != ConnectionResult.SUCCESS) {
            Dialog errorDialog = apiAvailability.getErrorDialog(this, errCode, 0);
            errorDialog.show();
        }
        else {
            mTextView.setText(SignInActivity.getSignInAccount().getDisplayName());
        }
    }
    */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * User profile getter.
     * @return UserProfile
     */
    public static UserProfile getUserProfile() {
        return mUserProfile;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult.getErrorMessage());
    }

    /**
     * Checks if Google Play Services are installed and if not it initializes
     * opening the dialog to allow user to install Google Play Services.
     * @return a boolean indicating if the Google Play Services are available.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode,
                        MainActivity.PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    /**
     * Tries to sign in silently
     */
    private void silentSignIn() {
        OptionalPendingResult<GoogleSignInResult> pendingResult =
                Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);

        if (pendingResult.isDone()) {
            handleSignInResult(pendingResult.get());
        }
        else {
            // There's no immediate result ready, displays some progress indicator and waits for the
            // async callback.
            showProgressIndicator();
            pendingResult.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(@NonNull GoogleSignInResult result) {
                    handleSignInResult(result);
                    hideProgressIndicator();
                }
            });
        }
    }

    /**
     * Handles user sign in result.
     * @param result User sign in result.
     */
    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.getStatus());
        if (result.isSuccess()) {
            mSigningIn = false;

            // Register user into the backend
            new RegisterUserAsyncTask().execute(result.getSignInAccount());
        } else {
            // Signed out, show unauthenticated UI.
            updateUI(false);

            if (!mSigningIn ||
                    result.getStatus().getStatusCode() == GoogleSignInStatusCodes.SIGN_IN_CANCELLED) {
                signIn();
            }
        }
    }

    /**
     * Starts Google API sign in activity
     */
    private void signIn() {
        mSigningIn = true;

        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    /**
     * Signs out of whichever user account.
     */
    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        finish();
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    /**
     * Shows and hides widgets to adapt UI to user authentication status.
     * @param signedIn Boolean value stating if the user is currently authenticated.
     */
    private void updateUI(boolean signedIn) {
        if (signedIn) {
            mUserNameTextView.setText(mUserProfile.getUserName());
            mUserEmailTextView.setText(mUserProfile.getGoogleAccount().getEmail());
            mImageManager.loadImage(mUserPictureImageView,
                    mUserProfile.getGoogleAccount().getPhotoUrl(),
                    R.mipmap.ic_launcher);
        }
        else {
            mUserNameTextView.setText("");
            mUserEmailTextView.setText("");
            mImageManager.loadImage(mUserPictureImageView, null);
        }
    }

    /**
     * Displays progress indicator for long processes, like user signing in.
     */
    private void showProgressIndicator() {
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
    }

    /**
     * Hides progress indicator.
     */
    private void hideProgressIndicator() {
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onStop() {
        super.onStop();
        /*
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.iesnules.apps.campus/http/host/path")
        );
        AppIndex.AppIndexApi.end(mGoogleApiClient, viewAction);
        */

        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            showUserProfile();
        } else if (id == R.id.nav_sign_out) {
            signOut();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Launches user profile editor Activity.
     */
    private void showUserProfile() {
        Intent profileIntent = new Intent(this, ProfileActivity.class);
        startActivity(profileIntent,
                ActivityOptionsCompat.makeSceneTransitionAnimation(this).toBundle());
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onListFragmentInteraction(DummyContent.DummyItem item) {

    }

    private class RegisterUserAsyncTask extends AsyncTask<GoogleSignInAccount, Void, UserProfile>{

        @Override
        protected UserProfile doInBackground(GoogleSignInAccount... params) {
            UserRecord record = null;
            GoogleSignInAccount account = params[0];

            User.Builder builder = new User.Builder(AndroidHttp.newCompatibleTransport(),
                    new AndroidJsonFactory(),
                    null);

            User service = builder.build();

            try {
                record = service.register(account.getIdToken()).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }

            UserProfile profile = null;

            if (record != null && record.getUserId().equals(account.getId())) {
                profile = new UserProfile(account, record);
            }

            return profile;
        }

        @Override
        protected void onPostExecute(UserProfile profile) {
            if (profile != null) { // User profile exists in backend store
                mUserProfile = profile;
                updateUI(true);
            }
            else { // Error confirming user identity in the backend -> sign in again...
                // TODO: Avoid failing signing in loop...
                signIn();
            }
        }
    }

}
