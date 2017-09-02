package com.walkap.x_android.fragment.days;


import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.walkap.x_android.R;
import com.walkap.x_android.adapter.CustomAdapter;
import com.walkap.x_android.fragment.BaseFragment;
import com.walkap.x_android.model.DegreeCourse;
import com.walkap.x_android.model.Faculty;
import com.walkap.x_android.model.Scheduler;
import com.walkap.x_android.model.University;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static android.content.Context.MODE_PRIVATE;

public class BaseDayFragment extends BaseFragment {

    private final String TAG = "BaseDayFragment";

    private String MY_PREFS_NAME = "preferences";

    private String universityKey = "";
    private String facultyKey = "";
    private String degreeCourseKey = "";

    private String universityName;
    private String facultyName;
    private String degreeCourseName;
    private Set<String> schoolSubjectList;

    ListView listView;

    public BaseDayFragment(){
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
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

    public void readDataFile(){

        SharedPreferences prefs = this.getActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        universityName = prefs.getString(UNIVERSITY, "");
        facultyName = prefs.getString(FACULTY, "");
        degreeCourseName = prefs.getString(DEGREE_COURSE, "");
        schoolSubjectList = prefs.getStringSet(SCHOOLSUBJECT, null);

        Log.d(TAG, "readDataFile(): " + universityName + facultyName + degreeCourseName);
    }

    /*public void showScheduler(int day) {

        mDatabase.child("scheduler").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Scheduler> list = new ArrayList<Scheduler>();

                // attenzione errore se lista preferenze vuota

                for (DataSnapshot noteDataSnapshot : dataSnapshot.getChildren()) {
                    Scheduler scheduler = noteDataSnapshot.getValue(Scheduler.class);

                    Log.d(TAG, "showScheduler() for: " + scheduler);

                    if (day == scheduler.getTime().getDay() && noteDataSnapshot.child(UNIVERSITY).getValue().toString().equals(universityKey)
                            && noteDataSnapshot.child(FACULTY).getValue().toString().equals(facultyKey) && noteDataSnapshot.child(DEGREE_COURSE).getValue().toString().equals(degreeCourseKey)
                            && schoolSubjectList.contains(scheduler.getSchoolSubject())) {



                        scheduler.setSchedulerId(noteDataSnapshot.getKey());

                        Log.d(TAG, "showScheduler() after if statment inside loop: " + scheduler);

                        list.add(scheduler);


                    }
                }
                CustomAdapter adapter = new CustomAdapter(getActivity(), R.layout.row, list);

                listView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException());
            }
        });
    }*/

    private void findUniversityKey(final String universityString){

        mDatabase.child(UNIVERSITY).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot noteDataSnapshot : dataSnapshot.getChildren()) {
                    University university = noteDataSnapshot.getValue(University.class);
                    if (university.getName().equals(universityString)) {
                        universityKey = noteDataSnapshot.getKey();
                        findFacultyKey(facultyName);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void findFacultyKey(final String facultyString){

        mDatabase.child(FACULTY).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot noteDataSnapshot : dataSnapshot.getChildren()) {
                    Faculty faculty = noteDataSnapshot.getValue(Faculty.class);
                    if (faculty.getName().equals(facultyString)) {
                        facultyKey = noteDataSnapshot.getKey();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void findDegreeCourseKey(final String degreeCourseString){

        mDatabase.child(DEGREE_COURSE).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot noteDataSnapshot : dataSnapshot.getChildren()) {
                    DegreeCourse degreeCourse = noteDataSnapshot.getValue(DegreeCourse.class);
                    if (degreeCourse.getName().equals(degreeCourseString)) {
                        degreeCourseKey = noteDataSnapshot.getKey();
                        if(schoolSubjectList != null) {
                            //showScheduler();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
