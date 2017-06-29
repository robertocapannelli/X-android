package com.walkap.x_android.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.walkap.x_android.R;
import com.walkap.x_android.models.University;
import com.walkap.x_android.viewHolder.UniversityViewHolder;

public class UniversityListFragment extends Fragment {

    // [START define_database_reference]
    private DatabaseReference mDatabase;
    // [END define_database_reference]

    private FirebaseRecyclerAdapter<University, UniversityViewHolder> mAdapter;
    private RecyclerView mRecycler;
    private LinearLayoutManager mManager;

    public UniversityListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_item_list, container, false);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        mRecycler = (RecyclerView) rootView.findViewById(R.id.university_list);
        mRecycler.setHasFixedSize(true);

        return rootView;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);



        // Set up Layout Manager, reverse layout
        /*mManager = new LinearLayoutManager(getActivity());
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        mRecycler.setLayoutManager(mManager);

        RecyclerView uniList = (RecyclerView) findViewById(R.id.university_list);
        uniList.setLayoutManager(new LinearLayoutManager(this));



        // Set up FirebaseRecyclerAdapter with the Query
        Query postsQuery = getQuery(mDatabase);

        mAdapter = new FirebaseRecyclerAdapter<University, UniversityViewHolder>(University.class, R.layout.fragment_item,
                UniversityViewHolder.class, postsQuery) {
            @Override
            protected void populateViewHolder(UniversityViewHolder viewHolder, University model, int position) {
                viewHolder.setName(University.getName());
                viewHolder.setCity(University.getCity());

            }
        };

        mRecycler.setAdapter(mAdapter);*/

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        /*if (mAdapter != null) {
            mAdapter.cleanup();
        }*/
    }

    public Query getQuery(DatabaseReference databaseReference) {
        // All my posts
        return databaseReference.child("university");
    }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

}
