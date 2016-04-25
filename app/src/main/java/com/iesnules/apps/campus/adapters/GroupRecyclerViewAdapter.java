package com.iesnules.apps.campus.adapters;

import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.iesnules.apps.campus.R;
import com.iesnules.apps.campus.backend.group.model.GroupRecord;
import com.iesnules.apps.campus.fragments.GroupsFragment.OnListFragmentInteractionListener;
import com.iesnules.apps.campus.dummy.DummyContent.DummyItem;

import java.util.List;


/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class GroupRecyclerViewAdapter extends RecyclerView.Adapter<GroupRecyclerViewAdapter.ViewHolder> {

    private SortedList<GroupRecord> mValues;
    private final OnListFragmentInteractionListener mListener;

    public GroupRecyclerViewAdapter(OnListFragmentInteractionListener listener) {
        mListener = listener;
    }

    public void setList(SortedList<GroupRecord> list) {
        mValues = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_groups, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mGroupNameView.setText(mValues.get(position).getGroupName());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mValues != null) {
            return mValues.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mGroupNameView;
        public GroupRecord mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mGroupNameView = (TextView) view.findViewById(R.id.groupNameTextView);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mGroupNameView.getText() + "'";
        }
    }
}
