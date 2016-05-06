package com.iesnules.apps.campus.fragments;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.TextViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;

import com.google.android.gms.common.api.BooleanResult;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.GenericData;
import com.iesnules.apps.campus.MainActivity;
import com.iesnules.apps.campus.R;
import com.iesnules.apps.campus.backend.group.model.GroupRecord;

import java.io.IOException;
import java.security.acl.Group;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GroupDetailFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GroupDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GroupDetailFragment extends Fragment {
    private static final String ARG_GROUP_INDEX = "groupIndex";

    private int mGroupIndex;
    private GroupRecord mGroupRecord;

    //private OnFragmentInteractionListener mListener;

    public GroupDetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param groupIndex Group record index in group list.
     * @return A new instance of fragment GroupDetailFragment.
     */
    public static GroupDetailFragment newInstance(int groupIndex) {
        GroupDetailFragment fragment = new GroupDetailFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_GROUP_INDEX, groupIndex);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mGroupIndex = getArguments().getInt(ARG_GROUP_INDEX);
            mGroupRecord = MainActivity.getGroupsList().get(mGroupIndex);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Hide FAB for this fragment
        FloatingActionButton fab = (FloatingActionButton)getActivity().findViewById(R.id.fab);
        AnimatorSet set = (AnimatorSet) AnimatorInflater.loadAnimator(getContext(), R.animator.fab_animation_out);
        set.setTarget(fab);
        set.start();

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_group_detail, container, false);

        TextView msgView = (TextView)view.findViewById(R.id.msgTextView);
        msgView.setText("Group record index: " + mGroupIndex);

        return view;
    }

    /*
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }
    */

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        /*
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
        */
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //mListener = null;
    }

    public interface OnGroupDetailFragmentListener {
        void onDeleteGroup(GroupRecord record, GroupDetailFragment sender);
    }

    private class DeleteGroupAsyncTask extends AsyncTask<Void, Void, Boolean> {

        private Context mContext;

        public DeleteGroupAsyncTask(Context context) {
            mContext = context;
        }


        @Override
        protected Boolean doInBackground(Void... params) {
            GoogleAccountCredential credential = GoogleAccountCredential.usingAudience(mContext,
                    getString(R.string.server_credential));
            credential.setSelectedAccountName(MainActivity.getUserProfile().getUserAccountName());

            com.iesnules.apps.campus.backend.group.Group.Builder builder = new com.iesnules.apps.campus.backend.group.Group.Builder(AndroidHttp.newCompatibleTransport(),
                    new AndroidJsonFactory(), credential);
            com.iesnules.apps.campus.backend.group.Group service = builder.build();

            try {
                service.delete(mGroupRecord.getId()).execute();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {

            }
            else {

            }
        }
    }
}
