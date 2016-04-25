package com.iesnules.apps.campus.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.iesnules.apps.campus.MainActivity;
import com.iesnules.apps.campus.R;
import com.iesnules.apps.campus.backend.group.Group;
import com.iesnules.apps.campus.backend.group.model.GroupRecord;

import java.io.IOException;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * Use the {@link NewGroupFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewGroupFragment extends DialogFragment {

    private static final String ARG_USER_ID = "userId";

    private Long mUserId;

    private EditText mGroupNameEditText;
    private EditText mGroupDescEditText;
    //private OnNewGroupFragmentListener mListener;

    public NewGroupFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment NewGroupFragment.
     */
    // TODO: add username to parameter
    public static NewGroupFragment newInstance(Long userId) {
        NewGroupFragment fragment = new NewGroupFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mUserId = getArguments().getLong(ARG_USER_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_new_group, container, false);

        mGroupNameEditText = (EditText)view.findViewById(R.id.groupNameEditText);
        mGroupDescEditText = (EditText)view.findViewById(R.id.groupDescEditText);

        return view;
    }

    public void onNewGroup(View view) {
        if (view.getId() == R.id.buttonNewGroup) {
            new CreateGroupAsyncTask(getContext()).execute();
        }
    }

    /*
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnNewGroupFragmentListener) {
            mListener = (OnNewGroupFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnNewGroupFragmentListener {
        void onCreateGroup(GroupRecord record);
    }
    */

    private class CreateGroupAsyncTask extends AsyncTask<Void, Void, GroupRecord> {

        private Context mContext;

        public CreateGroupAsyncTask(Context context) {
            mContext = context;
        }

        /**
         * Creates a new group record with data introduced by user.
         * @return GroupRecord
         */
        private GroupRecord getNewRecord() {
            GroupRecord record = new GroupRecord();

            // Set group data
            record.setGroupName(mGroupNameEditText.getText().toString());
            record.setDescription(mGroupDescEditText.getText().toString());

            return record;

        }
        @Override
        protected void onPreExecute() {

        }

        @Override
        protected GroupRecord doInBackground(Void... params) {
            GoogleAccountCredential credential = GoogleAccountCredential.usingAudience(mContext,
                    getString(R.string.server_credential));
            credential.setSelectedAccountName(MainActivity.getUserProfile().getUserAccountName());

            Group.Builder builder = new Group.Builder(AndroidHttp.newCompatibleTransport(),
                    new AndroidJsonFactory(), credential);
            Group service = builder.build();

            GroupRecord record = getNewRecord();
            try {
                record = service.insert(mUserId, record).execute();
            } catch (IOException e) {
                record = null;
                e.printStackTrace();
            }

            return record;
        }

        protected void onPostExecute(GroupRecord record) {
            if (record != null) { // Update local user profile

            }
            else { // Error updating user profile
                ErrorDialogFragment fragment = ErrorDialogFragment.newInstance("Error",
                        getString(R.string.prof_update_error), null, null);

                fragment.show(getSupportFragmentManager(), "update_error");
            }
        }
    }
}
