package com.iesnules.apps.campus;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.util.SortedList;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.util.SortedListAdapterCallback;
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
import com.google.android.gms.plus.Plus;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.iesnules.apps.campus.adapters.GroupRecyclerViewAdapter;
import com.iesnules.apps.campus.backend.group.Group;
import com.iesnules.apps.campus.backend.group.model.CollectionResponseGroupRecord;
import com.iesnules.apps.campus.backend.group.model.GroupRecord;
import com.iesnules.apps.campus.backend.user.User;
import com.iesnules.apps.campus.backend.user.model.UserRecord;
import com.iesnules.apps.campus.fragments.ErrorDialogFragment;
import com.iesnules.apps.campus.fragments.EventsFragment;
import com.iesnules.apps.campus.fragments.GroupDetailFragment;
import com.iesnules.apps.campus.fragments.GroupsFragment;
import com.iesnules.apps.campus.fragments.NewGroupFragment;
import com.iesnules.apps.campus.fragments.RecentFragment;
import com.iesnules.apps.campus.fragments.ResourcesFragment;
import com.iesnules.apps.campus.model.UserProfile;
import com.iesnules.apps.campus.utils.RoundedTransformation;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnMenuTabClickListener;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;
import java.util.Stack;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        NavigationView.OnNavigationItemSelectedListener,
        GroupsFragment.OnGroupsFragmentListener,
        ErrorDialogFragment.ErrorDialogListener,
        NewGroupFragment.OnNewGroupFragmentListener {

    private static final String TAG = "MainActivity";

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final int RC_SIGN_IN = 9001;

    private static UserProfile mUserProfile;

    private GoogleApiClient mGoogleApiClient;
    private boolean mSigningIn;

    //private RelativeLayout mFragmentContainer;
    private CoordinatorLayout mCoordinatorLayout;

    private TextView mUserNameTextView;
    private TextView mUserEmailTextView;
    private ImageView mUserPictureImageView;
    private BottomBar mBottomBar;
    private NavigationView mDrawerNavigationView;
    private FloatingActionButton mFAB;

    private final int BOTTOMBAR_ITEM_RECENT_POSITION = 0;
    private final int BOTTOMBAR_ITEM_GROUPS_POSITION = 1;
    private final int BOTTOMBAR_ITEM_RESOURCES_POSITION = 2;
    private final int BOTTOMBAR_ITEM_EVENTS_POSITION = 3;

    private OnMenuTabClickListener mBottomBarTabListener;

    // Fragments
    //private RecentFragment mRecentFragment;
    //private GroupsFragment mGroupsFragment;
    //private ResourcesFragment mResourcesFragment;
    //private EventsFragment mEventsFragment;
    private Stack<Fragment> mRecentFragmentStack;
    private Stack<Fragment> mGroupsFragmentStack;
    private Stack<Fragment> mResourcesFragmentStack;
    private Stack<Fragment> mEventsFragmentStack;
    private Stack<Fragment> mCurrentFragmentStack;
    //private Fragment mCurrentFragment;
    private int mTagCount = 0;

    // Data structures
    private static SortedList<GroupRecord> mGroupsList;
    private GroupRecyclerViewAdapter mGroupsAdapter;

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

        mFAB = (FloatingActionButton) findViewById(R.id.fab);
        mFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NewGroupFragment newGroupFragment = NewGroupFragment
                        .newInstance(mUserProfile.getUserRecord().getId());
                // Show DialogFragment+
                newGroupFragment.show(getSupportFragmentManager(), "Dialog Fragment");
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        mDrawerNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mDrawerNavigationView.setNavigationItemSelectedListener(this);

        View headerView = mDrawerNavigationView.getHeaderView(0);
        mUserNameTextView = (TextView) headerView.findViewById(R.id.userName);
        mUserEmailTextView = (TextView) headerView.findViewById(R.id.userEmail);
        mUserPictureImageView = (ImageView) headerView.findViewById(R.id.userPicture);

        //mImageManager = ImageManager.create(this);

        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);
        //mFragmentContainer = (RelativeLayout)findViewById(R.id.fragment_container);

        // Bottom bar...
        mBottomBar = BottomBar.attach(mCoordinatorLayout, savedInstanceState);
        mBottomBar.noTopOffset();
        mBottomBar.setItemsFromMenu(R.menu.bottombar_menu, getBottomBarTabListener());
        mBottomBar.setEnabled(false);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        mBottomBar.onSaveInstanceState(outState);
    }

    /**
     * Lazily create recent fragment stack.
     *
     * @return Stack<Fragment>
     */
    private Stack<Fragment> getRecentFragmentStack() {
        if (mRecentFragmentStack == null) {
            mRecentFragmentStack = new Stack<Fragment>();
            mRecentFragmentStack.push(RecentFragment.newInstance("Recent", "Activities"));
        }

        return mRecentFragmentStack;
    }

    /**
     * Lazily create groups fragment stack.
     *
     * @return Stack<Fragment>
     */
    private Stack<Fragment> getGroupsFragmentStack() {
        if (mGroupsFragmentStack == null) {
            mGroupsFragmentStack = new Stack<Fragment>();
            GroupsFragment fragment = GroupsFragment.newInstance();
            fragment.setAdapter(getGroupsAdapter());
            new GetUserGroupsAsyncTask(this).execute();
            mGroupsFragmentStack.push(fragment);
        }

        return mGroupsFragmentStack;
    }

    /**
     * Lazily create resources fragment stack.
     *
     * @return Stack<Fragment>
     */
    private Stack<Fragment> getResourcesFragmentStack() {
        if (mResourcesFragmentStack == null) {
            mResourcesFragmentStack = new Stack<Fragment>();
            mResourcesFragmentStack.push(ResourcesFragment.newInstance("Resources", "List"));
        }

        return mResourcesFragmentStack;
    }

    /**
     * Lazily create events fragment stack.
     *
     * @return Stack<Fragment>
     */
    private Stack<Fragment> getEventsFragmentStack() {
        if (mEventsFragmentStack == null) {
            mEventsFragmentStack = new Stack<Fragment>();
            mEventsFragmentStack.push(EventsFragment.newInstance("Events", "List"));
        }

        return mEventsFragmentStack;
    }

    private OnMenuTabClickListener getBottomBarTabListener() {
        if (mBottomBarTabListener == null) {
            mBottomBarTabListener = new OnMenuTabClickListener() {
                @Override
                public void onMenuTabSelected(@IdRes int menuItemId) {
                    switch (menuItemId) {
                        case R.id.bottomBarItemRecent:
                            mDrawerNavigationView.setCheckedItem(R.id.bottomBarItemRecent);
                            setTitle(R.string.nav_recent);
                            switchFragmentStack(getRecentFragmentStack());
                            break;
                        case R.id.bottomBarItemGroups:
                            mDrawerNavigationView.setCheckedItem(R.id.bottomBarItemGroups);
                            setTitle(R.string.nav_groups);
                            switchFragmentStack(getGroupsFragmentStack());
                            break;
                        case R.id.bottomBarItemResources:
                            mDrawerNavigationView.setCheckedItem(R.id.bottomBarItemResources);
                            setTitle(R.string.nav_resources);
                            switchFragmentStack(getResourcesFragmentStack());
                            break;
                        case R.id.bottomBarItemEvents:
                            mDrawerNavigationView.setCheckedItem(R.id.bottomBarItemEvents);
                            setTitle(R.string.nav_events);
                            switchFragmentStack(getEventsFragmentStack());
                            break;
                    }
                }

                @Override
                public void onMenuTabReSelected(@IdRes int menuItemId) {

                }
            };
        }

        return mBottomBarTabListener;
    }

    private GroupRecyclerViewAdapter getGroupsAdapter() {
        if (mGroupsAdapter == null) {
            mGroupsAdapter = new GroupRecyclerViewAdapter(this);

            mGroupsList = new SortedList<GroupRecord>(GroupRecord.class,
                    new SortedListAdapterCallback<GroupRecord>(mGroupsAdapter) {
                        @Override
                        public int compare(GroupRecord o1, GroupRecord o2) {
                            return (int) (o1.getCreationDate().getValue() - o2.getCreationDate().getValue());
                        }

                        @Override
                        public boolean areContentsTheSame(GroupRecord oldItem, GroupRecord newItem) {
                            if (!oldItem.getCreationDate().equals(newItem.getCreationDate())) {
                                return false;
                            }
                            if (!oldItem.getDescription().equals(newItem.getDescription())) {
                                return false;
                            }
                            if (!oldItem.getGroupName().equals(newItem.getGroupName())) {
                                return false;
                            }

                            return true;
                        }

                        @Override
                        public boolean areItemsTheSame(GroupRecord item1, GroupRecord item2) {
                            return item1.getId().equals(item2.getId());
                        }
                    });
            mGroupsAdapter.setList(mGroupsList);
        }

        return mGroupsAdapter;
    }

    /**
     * Switch to a new fragment stack.
     *
     * @param newFragmentStack
     */
    private void switchFragmentStack(Stack<Fragment> newFragmentStack) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if (mCurrentFragmentStack != null) {
            Fragment currentFragment = mCurrentFragmentStack.peek();
            transaction.detach(currentFragment);
        }

        Fragment newFragment = newFragmentStack.peek();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(newFragment.getTag());
        if (fragment != null) {
            transaction.attach(fragment);
        } else {
            transaction.add(R.id.fragment_container, newFragment, newFragment.getClass().getName() +
                    mTagCount++);
        }

        mCurrentFragmentStack = newFragmentStack;

        transaction.commit();
    }

    /**
     * Push a new fragment.
     *
     * @param newFragment
     */
    private void pushFragment(Fragment newFragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if (mCurrentFragmentStack != null) {
            Fragment currentFragment = mCurrentFragmentStack.peek();
            transaction.detach(currentFragment);
        }

        transaction.add(R.id.fragment_container, newFragment, newFragment.getClass().getName() +
                mTagCount++);

        mCurrentFragmentStack.push(newFragment);

        transaction.commit();
    }

    /**
     * Pop current fragment.
     */
    private void popFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if (mCurrentFragmentStack != null) {
            Fragment currentFragment = mCurrentFragmentStack.pop();
            transaction.remove(currentFragment);

            Fragment newFragment = mCurrentFragmentStack.peek();
            Fragment fragment = getSupportFragmentManager().findFragmentByTag(newFragment.getTag());
            if (fragment != null) {
                transaction.attach(fragment);
            } else {
                transaction.add(R.id.fragment_container, newFragment, newFragment.getClass().getName() +
                        mTagCount++);
            }
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
        if (id == R.id.action_about) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * User profile getter.
     *
     * @return UserProfile
     */
    public static UserProfile getUserProfile() {
        return mUserProfile;
    }

    /**
     * Group records getter.
     *
     * @return SortedList<GroupRecord>
     */
    public static SortedList<GroupRecord> getGroupsList() {
        return mGroupsList;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult.getErrorMessage());
    }

    /**
     * Checks if Google Play Services are installed and if not it initializes
     * opening the dialog to allow user to install Google Play Services.
     *
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
        } else {
            // There's no immediate result ready, wait for the async callback.
            pendingResult.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(@NonNull GoogleSignInResult result) {
                    handleSignInResult(result);
                }
            });
        }
    }

    /**
     * Handles user sign in result.
     *
     * @param result User sign in result.
     */
    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.getStatus());
        if (result.isSuccess()) {
            mSigningIn = false;

            // Register user into the backend if not already registered
            if (mUserProfile == null) {
                new RegisterUserAsyncTask(this).execute(result.getSignInAccount());
            }
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
     *
     * @param signedIn Boolean value stating if the user is currently authenticated.
     */
    private void updateUI(boolean signedIn) {
        if (signedIn) {
            mUserNameTextView.setText(mUserProfile.getUserName());
            mUserEmailTextView.setText(mUserProfile.getGoogleAccount().getEmail());
            int radius = mUserPictureImageView.getWidth() / 2;
            Picasso.with(this)
                    .load(mUserProfile.getGoogleAccount().getPhotoUrl())
                    .transform(new RoundedTransformation(radius, 0))
                    .into(mUserPictureImageView);
            //mImageManager.loadImage(mUserPictureImageView,
            //        mUserProfile.getGoogleAccount().getPhotoUrl(),
            //        R.mipmap.ic_launcher);
        } else {
            mUserNameTextView.setText("");
            mUserEmailTextView.setText("");
            //mImageManager.loadImage(mUserPictureImageView, null);
            mUserPictureImageView.setImageResource(R.mipmap.ic_launcher);
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

    private void enableUI(boolean enable) {
        mBottomBar.setEnabled(enable);
        Menu drawerMenu = mDrawerNavigationView.getMenu();
        drawerMenu.findItem(R.id.nav_profile).setEnabled(enable);
        drawerMenu.findItem(R.id.bottomBarItemRecent).setEnabled(enable);
        drawerMenu.findItem(R.id.bottomBarItemGroups).setEnabled(enable);
        drawerMenu.findItem(R.id.bottomBarItemResources).setEnabled(enable);
        drawerMenu.findItem(R.id.bottomBarItemEvents).setEnabled(enable);

        mFAB.setEnabled(enable);
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

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (item.getItemId()) {
            case R.id.nav_profile:
                showUserProfile();
                break;
            case R.id.nav_sign_out:
                signOut();
                break;
            case R.id.bottomBarItemRecent:
                mBottomBar.selectTabAtPosition(BOTTOMBAR_ITEM_RECENT_POSITION, true);
                break;
            case R.id.bottomBarItemGroups:
                mBottomBar.selectTabAtPosition(BOTTOMBAR_ITEM_GROUPS_POSITION, true);
                break;
            case R.id.bottomBarItemResources:
                mBottomBar.selectTabAtPosition(BOTTOMBAR_ITEM_RESOURCES_POSITION, true);
                break;
            case R.id.bottomBarItemEvents:
                mBottomBar.selectTabAtPosition(BOTTOMBAR_ITEM_EVENTS_POSITION, true);
                break;
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
        } else if (mCurrentFragmentStack.size() > 1) {
            popFragment();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onGroupSelected(int index) {
        pushFragment(GroupDetailFragment.newInstance(index));
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        silentSignIn();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        finish();
    }

    public void onCreateGroup(GroupRecord record, NewGroupFragment fragment) {
        mGroupsList.add(record);
        fragment.dismiss();
    }

    private class RegisterUserAsyncTask extends AsyncTask<GoogleSignInAccount, Void, UserProfile> {

        private Context mContext;

        public RegisterUserAsyncTask(Context context) {
            mContext = context;
        }

        @Override
        protected void onPreExecute() {
            showProgressIndicator();
            enableUI(false);
        }

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
            hideProgressIndicator();
            if (profile != null) { // User profile exists in backend store
                mUserProfile = profile;
                updateUI(true);
                enableUI(true);
            } else {
                ErrorDialogFragment fragment = ErrorDialogFragment.newInstance("Sign in error",
                        getString(R.string.main_alert_dialog_message),
                        getString(R.string.main_alert_dialog_positive_button),
                        getString(R.string.main_alert_dialog_negative_button));

                fragment.show(getSupportFragmentManager(), "signin_error");
            }
        }
    }

    private class GetUserGroupsAsyncTask extends AsyncTask<Void, Void, CollectionResponseGroupRecord> {

        private Context mContext;

        public GetUserGroupsAsyncTask(Context context) {
            mContext = context;
        }

        @Override
        protected void onPreExecute() {
            showProgressIndicator();
        }

        @Override
        protected CollectionResponseGroupRecord doInBackground(Void... params) {
            CollectionResponseGroupRecord response = null;

            if (mUserProfile != null) {
                GoogleAccountCredential credential = GoogleAccountCredential.usingAudience(mContext,
                        getString(R.string.server_credential));
                credential.setSelectedAccountName(mUserProfile.getUserAccountName());

                Group.Builder builder = new Group.Builder(AndroidHttp.newCompatibleTransport(),
                        new AndroidJsonFactory(), credential);

                Group service = builder.build();

                try {
                    response = service.find(mUserProfile.getUserRecord().getId()).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return response;
        }

        @Override
        protected void onPostExecute(CollectionResponseGroupRecord response) {
            hideProgressIndicator();
            if (response != null) { // User profile exists in backend store
                List<GroupRecord> groups = response.getItems();
                if (groups != null) {
                    mGroupsList.addAll(groups);
                }
            } else {
                ErrorDialogFragment fragment = ErrorDialogFragment.newInstance("Groups error",
                        getString(R.string.groups_alert_dialog_message), null, null);

                fragment.show(getSupportFragmentManager(), "groups_error");
            }
        }
    }
}
