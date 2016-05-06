package com.iesnules.apps.campus.fragments;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.iesnules.apps.campus.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AboutDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AboutDialogFragment extends DialogFragment {

    public AboutDialogFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     **/
    public static AboutDialogFragment newInstance() {
        AboutDialogFragment fragment = new AboutDialogFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_about_dialog, container, false);
    }
}
