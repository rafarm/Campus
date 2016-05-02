package com.iesnules.apps.campus.fragments;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Context;
import android.net.Uri;
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

import com.iesnules.apps.campus.R;

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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    /*
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
    */
}
