package com.walkap.x_android.fragment.days;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.walkap.x_android.R;
import com.walkap.x_android.adapter.LessonAdapter;
import com.walkap.x_android.fragment.OptionsFragment;
import com.walkap.x_android.model.Scheduler;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseDayFragment extends Fragment {

    private final String TAG = "BaseDayFragment";

    public DatabaseReference mDatabase;
    public FirebaseAuth mFirebaseAuth;
    public FirebaseUser mFirebaseUser;

    public final String UNIVERSITY = "university";
    public final String UNIVERSITIES = "universities";

    public final String FACULTY = "faculty";
    public final String FACULTIES = "faculties";

    public final String DEGREE_COURSE = "degreeCourse";

    public final String CLASSROOM = "classRoom";
    public final String SCHOOLSUBJECT = "schoolSubject";

    public final String USER = "users";
    public final String SCHEDULER = "scheduler";
    public final String PREFERENCES = "preferences";

    public List<Scheduler> list;

    private List<String> preferences = new ArrayList<>();

    public String userUniversityKey;
    public String userFacultyKey;
    public String userDegreeCourseKey;

    private RecyclerView mRecycler;

    Context content;

    public BaseDayFragment(){
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        mDatabase =  FirebaseDatabase.getInstance().getReference();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        content = getActivity().getApplicationContext();

        readDataFileDb();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.fragment_day, container, false);

        mRecycler = (RecyclerView) rootView.findViewById(R.id.mainActivityListView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecycler.setLayoutManager(linearLayoutManager);
        mRecycler.setHasFixedSize(true);

        return rootView;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public abstract int getDay();

    public void readDataFileDb(){

        mDatabase.child(USER).child(mFirebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userUniversityKey = dataSnapshot.child(UNIVERSITY).getValue().toString();
                userFacultyKey = dataSnapshot.child(FACULTY).getValue().toString();
                userDegreeCourseKey = dataSnapshot.child(DEGREE_COURSE).getValue().toString();

                Log.d("*** read db ***", userUniversityKey + "  " + userFacultyKey + "  " + userDegreeCourseKey);

                if(userUniversityKey.isEmpty() || userFacultyKey.isEmpty() || userDegreeCourseKey.isEmpty()){
                    Fragment fragment = new OptionsFragment();
                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.mainActivityListView, fragment).commit();
                }

                fillListPreferences();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException());
            }
        });
    }

    private void fillListPreferences(){

        mDatabase.child(USER).child(mFirebaseUser.getUid()).child(PREFERENCES).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot noteDataSnapshot : dataSnapshot.getChildren()) {
                    Log.d("*** fillList ***", "  " + noteDataSnapshot.getValue().toString());
                    preferences.add(noteDataSnapshot.getValue().toString());
                }
                showScheduler();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException());
            }
        });

    }

    private void showScheduler() {

        mDatabase.child(SCHEDULER).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                list = new ArrayList<Scheduler>();
                final int day = getDay();

                for (DataSnapshot noteDataSnapshot : dataSnapshot.getChildren()) {
                    Scheduler scheduler = noteDataSnapshot.getValue(Scheduler.class);

                    if (!preferences.isEmpty() && day == scheduler.getTime().getDay() && noteDataSnapshot.child(UNIVERSITY).getValue().toString().equals(userUniversityKey)
                            && noteDataSnapshot.child(FACULTY).getValue().toString().equals(userFacultyKey)
                            && noteDataSnapshot.child(DEGREE_COURSE).getValue().toString().equals(userDegreeCourseKey)
                            && preferences.contains(scheduler.getSchoolSubject())) {
                        scheduler.setSchedulerId(noteDataSnapshot.getKey());
                        list.add(scheduler);

                        Log.d(TAG, "Scheduler: " + scheduler.getClassroom() + scheduler.getSchoolSubject());
                    }
                }

                Log.d(TAG, "List: " + list);

                LessonAdapter mAdapter = new LessonAdapter(getActivity(), list);
                mRecycler.setAdapter(mAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException());
            }
        });
    }

}
