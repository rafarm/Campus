package com.iesnules.apps.campus.fragments;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.TimeInterpolator;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.util.SortedList;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.util.SortedListAdapterCallback;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;

import com.iesnules.apps.campus.adapters.GroupRecyclerViewAdapter;
import com.iesnules.apps.campus.R;
import com.iesnules.apps.campus.backend.group.model.GroupRecord;
import com.iesnules.apps.campus.backend.user.model.UserRecord;
import com.iesnules.apps.campus.dummy.DummyContent;
import com.iesnules.apps.campus.dummy.DummyContent.DummyItem;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnGroupsFragmentListener}
 * interface.
 */
public class GroupsFragment extends Fragment {

    private OnGroupsFragmentListener mListener;
    private GroupRecyclerViewAdapter mAdapter;
    private RecyclerView mListView;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public GroupsFragment() {
    }

    public static GroupsFragment newInstance() {
        GroupsFragment fragment = new GroupsFragment();
        return fragment;
    }

    /*
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }
    */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Show FAB for this fragment
        if (fab.getScaleX() < 0) {
            FloatingActionButton fab = (FloatingActionButton)getActivity().findViewById(R.id.fab);
            AnimatorSet set = (AnimatorSet)AnimatorInflater.loadAnimator(getContext(), R.animator.fab_animation_in);
            set.setTarget(fab);
            set.start();}

        View view = inflater.inflate(R.layout.fragment_groups_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            mListView = (RecyclerView) view;
            //recyclerView.setLayoutManager(new LinearLayoutManager(context));
            mListView.setAdapter(mAdapter);
        }

        return view;
    }
    
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnGroupsFragmentListener) {
            mListener = (OnGroupsFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void setAdapter(GroupRecyclerViewAdapter adapter) {
        mAdapter = adapter;
    }

    public interface OnGroupsFragmentListener {
        void onGroupSelected(int index);
    }
}
