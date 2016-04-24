package com.iesnules.apps.campus.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.iesnules.apps.campus.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ErrorDialogFragment.ErrorDialogListener} interface
 * to handle interaction events.
 * Use the {@link ErrorDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ErrorDialogFragment extends DialogFragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_TITLE = "title";
    private static final String ARG_MESSAGE = "message";
    private static final String ARG_POS_MESSAGE = "pos_message";
    private static final String ARG_NEG_MESSAGE = "neg_message";

    // TODO: Rename and change types of parameters
    private String mTitle;
    private String mMessage;
    private String mPositiveMessage;
    private String mNegativeMessage;

    private ErrorDialogListener mListener;

    public ErrorDialogFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param title Error dialog title.
     * @param message Error dialog message.
     * @param positiveMsg Positive button message.
     * @param negativeMsg Negative button message.
     * @return A new instance of fragment ErrorDialogFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ErrorDialogFragment newInstance(String title, String message,
                                                  String positiveMsg, String negativeMsg) {
        ErrorDialogFragment fragment = new ErrorDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putString(ARG_MESSAGE, message);
        args.putString(ARG_POS_MESSAGE, positiveMsg);
        args.putString(ARG_NEG_MESSAGE, negativeMsg);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTitle = getArguments().getString(ARG_TITLE);
            mMessage = getArguments().getString(ARG_MESSAGE);
            mPositiveMessage = getArguments().getString(ARG_POS_MESSAGE);
            mNegativeMessage = getArguments().getString(ARG_NEG_MESSAGE);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstance) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(mTitle)
                .setMessage(mMessage)
                .setIcon(android.R.drawable.ic_dialog_alert);

        if (mPositiveMessage != null) {
            builder.setPositiveButton(mPositiveMessage, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mListener.onDialogPositiveClick(ErrorDialogFragment.this);
                }
            });
        }

        if (mNegativeMessage != null) {
            builder.setNegativeButton(mNegativeMessage, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mListener.onDialogNegativeClick(ErrorDialogFragment.this);
                }
            });
        }

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ErrorDialogListener) {
            mListener = (ErrorDialogListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement ErrorDialogListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * dialog fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface ErrorDialogListener {
        void onDialogPositiveClick(DialogFragment dialog);
        void onDialogNegativeClick(DialogFragment dialog);
    }
}
