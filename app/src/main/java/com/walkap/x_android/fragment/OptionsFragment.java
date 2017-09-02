package com.walkap.x_android.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.walkap.x_android.R;

import static android.content.Context.MODE_PRIVATE;

import com.walkap.x_android.activity.MainActivity;
import com.walkap.x_android.model.DegreeCourse;
import com.walkap.x_android.model.Faculty;
import com.walkap.x_android.model.SchoolSubject;
import com.walkap.x_android.model.University;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OptionsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link OptionsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OptionsFragment extends Fragment implements View.OnClickListener{

    private static final String TAG = "OptionsFragment";

    private String MY_PREFS_NAME = "preferences";

    private final String UNIVERSITY = "university";
    private final String UNIVERSITIES = "universities";

    private final String FACULTY = "faculty";
    private final String FACULTIES = "faculties";

    private final String DEGREE_COURSE = "degreeCourse";

    private String universityName;
    private String facultyName;
    private String degreeCourseName;

    private String universityOldKey;
    private String facultyOldKey;
    private String degreeCourseOldKey;

    private AutoCompleteTextView university;
    private AutoCompleteTextView faculty;
    private AutoCompleteTextView degreeCourse;

    private DatabaseReference mDatabase;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    private OnFragmentInteractionListener mListener;

    public OptionsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDatabase =  FirebaseDatabase.getInstance().getReference();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        readDataFile();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);

        final View rootView = inflater.inflate(R.layout.fragment_options, container, false);

        university = (AutoCompleteTextView) rootView.findViewById(R.id.universityAutoCompleteTextView);
        faculty = (AutoCompleteTextView) rootView.findViewById(R.id.facultyAutoCompleteTextView);
        degreeCourse = (AutoCompleteTextView) rootView.findViewById(R.id.degreeCourseAutoCompleteTextView);

        addListAutocomplete(university, UNIVERSITY);
        addListAutocomplete(faculty, FACULTY);
        addListAutocomplete(degreeCourse, DEGREE_COURSE);

        university.setText(universityName);
        faculty.setText(facultyName);
        degreeCourse.setText(degreeCourseName);

        //Button listener
        Button btn = (Button) rootView.findViewById(R.id.saveButton);
        btn.setOnClickListener(this);

        // Inflate the layout for this fragment
        return rootView;
    }


    @Override
    public void onClick(View view) {
        saveData();
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
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


    public void alertDialog(int title, int message, int drawable){
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(title)
                .setMessage(message)
                .setIcon(drawable)
                .setPositiveButton(R.string.got_it, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    // User clicked OK button
                        dialog.cancel();
                    }
                })
                .show();
    }

    public void saveData(){

        String universityString = university.getText().toString();
        String facultyString = faculty.getText().toString();
        String degreeCourseString = degreeCourse.getText().toString();

        if(universityString.isEmpty()){
            Log.d(TAG, "University is empty");
            alertDialog(R.string.university_error, R.string.university_empty, android.R.drawable.ic_dialog_alert);
        }else{
            if(facultyString.isEmpty()){
                Log.d(TAG, "Faculty is empty");
                alertDialog(R.string.faculty_error, R.string.faculty_empty, android.R.drawable.ic_dialog_alert);
            }else{
                if(degreeCourseString.isEmpty()){
                    Log.d(TAG, "Degree Course is empty");
                    alertDialog(R.string.degree_course_error, R.string.degree_course_empty, android.R.drawable.ic_dialog_alert);
                }else{

                    String universityKey = mDatabase.child(UNIVERSITY).push().getKey();
                    String facultyKey = mDatabase.child(FACULTY).push().getKey();
                    String degreeCourseKey = mDatabase.child(DEGREE_COURSE).push().getKey();

                    findUniversity(universityString, universityKey, facultyString, facultyKey, degreeCourseString, degreeCourseKey);

                    SharedPreferences.Editor editor = getActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                    editor.putString(UNIVERSITY, universityString);
                    editor.putString(FACULTY, facultyString);
                    editor.putString(DEGREE_COURSE, degreeCourseString);
                    editor.apply();
                }
            }
        }
    }

    public void findUniversity(final String universityString,final String universityKey,
                               final String facultyString, final String facultyKey,
                               final String degreeCourseString, final String degreeCourseKey){

        mDatabase.child(UNIVERSITY).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean find = false;
                universityOldKey = "";
                for (DataSnapshot noteDataSnapshot : dataSnapshot.getChildren()) {
                    University university = noteDataSnapshot.getValue(University.class);
                    if (university.getName().equals(universityString)) {
                        universityOldKey = noteDataSnapshot.getKey();
                        find = true;
                    }
                }
                if(!find){
                    AlertDialog.Builder builder;
                    builder = new AlertDialog.Builder(getActivity());

                    builder.setTitle(R.string.university_not_found)
                            .setMessage(R.string.university_not_found_add)
                            .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    University university = new University(universityString, "", "");
                                    mDatabase.child(UNIVERSITY).child(universityKey).setValue(university);

                                    mDatabase.child("users").child(mFirebaseUser.getUid()).child(UNIVERSITY).setValue(universityKey);

                                    findFaculty(universityKey, facultyString, facultyKey, degreeCourseString, degreeCourseKey);

                                }
                            })
                            .setNegativeButton(R.string.university_select_another, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    university.setText("");
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
                else{
                    mDatabase.child("users").child(mFirebaseUser.getUid()).child(UNIVERSITY).setValue(universityOldKey);
                    findFaculty(universityKey, facultyString, facultyKey, degreeCourseString, degreeCourseKey);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException());
            }
        });

    }

    public void findFaculty(final String universityKey,
                            final String facultyString, final String facultyKey,
                            final String degreeCourseString, final String degreeCourseKey){

        mDatabase.child(FACULTY).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean find = false;
                facultyOldKey = "";
                for (DataSnapshot noteDataSnapshot : dataSnapshot.getChildren()) {
                    Faculty faculty = noteDataSnapshot.getValue(Faculty.class);
                    if (faculty.getName().equals(facultyString) ) {
                        facultyOldKey = noteDataSnapshot.getKey();
                        if(noteDataSnapshot.child(UNIVERSITIES).child(universityOldKey).exists() && !universityOldKey.isEmpty()){
                            find = true;
                        }

                    }
                }
                if(!find){
                    AlertDialog.Builder builder;
                    builder = new AlertDialog.Builder(getActivity());

                    builder.setTitle(R.string.faculty_not_found)
                            .setMessage(R.string.faculty_not_found_add)
                            .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    mDatabase.child("users").child(mFirebaseUser.getUid()).child(FACULTY).setValue(facultyKey);
                                    findDegreeCourse(universityKey, facultyString, facultyKey, degreeCourseString, degreeCourseKey);

                                }
                            })
                            .setNegativeButton(R.string.faculty_select_another, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    faculty.setText("");
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
                else{

                    mDatabase.child("users").child(mFirebaseUser.getUid()).child(FACULTY).setValue(facultyOldKey);
                    findDegreeCourse(universityKey, facultyString, facultyKey, degreeCourseString, degreeCourseKey);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException());
            }
        });
    }

    public void findDegreeCourse(final String universityKey,
                                 final String facultyString, final String facultyKey,
                                 final String degreeCourseString, final String degreeCourseKey){

        mDatabase.child(DEGREE_COURSE).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean find = false;
                degreeCourseOldKey = "";
                for (DataSnapshot noteDataSnapshot : dataSnapshot.getChildren()) {
                    DegreeCourse degreeCourse = noteDataSnapshot.getValue(DegreeCourse.class);
                    if (degreeCourse.getName().toString().equals(degreeCourseString) ) {
                        degreeCourseOldKey = noteDataSnapshot.getKey();
                        if(noteDataSnapshot.child(UNIVERSITY).child(universityOldKey).child(FACULTY).child(facultyOldKey).exists()
                                && !facultyOldKey.isEmpty() && !universityOldKey.isEmpty()){
                            find = true;
                        }

                    }
                }
                if(!find){
                    AlertDialog.Builder builder;
                    builder = new AlertDialog.Builder(getActivity());

                    builder.setTitle(R.string.degree_course_not_found)
                            .setMessage(R.string.degree_course_not_found_add)
                            .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    if(degreeCourseOldKey.isEmpty()){
                                        DegreeCourse degreeCourse = new DegreeCourse(degreeCourseString);
                                        mDatabase.child(DEGREE_COURSE).child(degreeCourseKey).setValue(degreeCourse);

                                        if(facultyOldKey.isEmpty()){
                                            Faculty faculty = new Faculty(facultyString);
                                            mDatabase.child(FACULTY).child(facultyKey).setValue(faculty);

                                            if(universityOldKey.isEmpty()) {
                                                mDatabase.child(FACULTY).child(facultyKey).child(UNIVERSITIES).child(universityKey).setValue(true);
                                                mDatabase.child(UNIVERSITY).child(universityKey).child(FACULTIES).child(facultyKey).setValue(true);
                                                mDatabase.child(DEGREE_COURSE).child(degreeCourseKey).child(UNIVERSITY).child(universityKey).child(FACULTY).child(facultyKey).setValue(true);
                                            }
                                            else{
                                                mDatabase.child(FACULTY).child(facultyKey).child(UNIVERSITIES).child(universityOldKey).setValue(true);
                                                mDatabase.child(UNIVERSITY).child(universityOldKey).child(FACULTIES).child(facultyKey).setValue(true);
                                                mDatabase.child(DEGREE_COURSE).child(degreeCourseKey).child(UNIVERSITY).child(universityOldKey).child(FACULTY).child(facultyKey).setValue(true);
                                            }
                                        }
                                        else{
                                            if(universityOldKey.isEmpty()) {
                                                mDatabase.child(FACULTY).child(facultyOldKey).child(UNIVERSITIES).child(universityKey).setValue(true);
                                                mDatabase.child(UNIVERSITY).child(universityKey).child(FACULTIES).child(facultyOldKey).setValue(true);
                                                mDatabase.child(DEGREE_COURSE).child(degreeCourseKey).child(UNIVERSITY).child(universityKey).child(FACULTY).child(facultyOldKey).setValue(true);
                                            }
                                            else{
                                                mDatabase.child(FACULTY).child(facultyOldKey).child(UNIVERSITIES).child(universityOldKey).setValue(true);
                                                mDatabase.child(UNIVERSITY).child(universityOldKey).child(FACULTIES).child(facultyOldKey).setValue(true);
                                                mDatabase.child(DEGREE_COURSE).child(degreeCourseKey).child(UNIVERSITY).child(universityOldKey).child(FACULTY).child(facultyOldKey).setValue(true);
                                            }
                                        }
                                    }
                                    else{
                                        if(facultyOldKey.isEmpty()){
                                            Faculty faculty = new Faculty(facultyString);
                                            mDatabase.child(FACULTY).child(facultyKey).setValue(faculty);

                                            if(universityOldKey.isEmpty()) {
                                                mDatabase.child(FACULTY).child(facultyKey).child(UNIVERSITIES).child(universityKey).setValue(true);
                                                mDatabase.child(UNIVERSITY).child(universityKey).child(FACULTIES).child(facultyKey).setValue(true);
                                                mDatabase.child(DEGREE_COURSE).child(degreeCourseOldKey).child(UNIVERSITY).child(universityKey).child(FACULTY).child(facultyKey).setValue(true);
                                            }
                                            else{
                                                mDatabase.child(FACULTY).child(facultyKey).child(UNIVERSITIES).child(universityOldKey).setValue(true);
                                                mDatabase.child(UNIVERSITY).child(universityOldKey).child(FACULTIES).child(facultyKey).setValue(true);
                                                mDatabase.child(DEGREE_COURSE).child(degreeCourseOldKey).child(UNIVERSITY).child(universityOldKey).child(FACULTY).child(facultyKey).setValue(true);
                                            }
                                        }
                                        else{
                                            if(universityOldKey.isEmpty()) {
                                                mDatabase.child(FACULTY).child(facultyOldKey).child(UNIVERSITIES).child(universityKey).setValue(true);
                                                mDatabase.child(UNIVERSITY).child(universityKey).child(FACULTIES).child(facultyOldKey).setValue(true);
                                                mDatabase.child(DEGREE_COURSE).child(degreeCourseOldKey).child(UNIVERSITY).child(universityKey).child(FACULTY).child(facultyOldKey).setValue(true);
                                            }
                                            else{
                                                mDatabase.child(FACULTY).child(facultyOldKey).child(UNIVERSITIES).child(universityOldKey).setValue(true);
                                                mDatabase.child(UNIVERSITY).child(universityOldKey).child(FACULTIES).child(facultyOldKey).setValue(true);
                                                mDatabase.child(DEGREE_COURSE).child(degreeCourseOldKey).child(UNIVERSITY).child(universityOldKey).child(FACULTY).child(facultyOldKey).setValue(true);
                                            }
                                        }
                                    }

                                    mDatabase.child("users").child(mFirebaseUser.getUid()).child(DEGREE_COURSE).setValue(degreeCourseKey);

                                    Fragment fragment = new HomeFragment();

                                    FragmentManager fragmentManager = getFragmentManager();

                                    fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

                                }
                            })
                            .setNegativeButton(R.string.degree_course_select_another, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    degreeCourse.setText("");
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
                else{

                    mDatabase.child("users").child(mFirebaseUser.getUid()).child(DEGREE_COURSE).setValue(degreeCourseOldKey);

                    Fragment fragment = new HomeFragment();

                    FragmentManager fragmentManager = getFragmentManager();

                    fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException());
            }
        });
    }

    private void readDataFile(){
        SharedPreferences prefs = getActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        universityName = prefs.getString(UNIVERSITY, "");
        facultyName = prefs.getString(FACULTY, "");
        degreeCourseName = prefs.getString(DEGREE_COURSE, "");
    }

    public void addListAutocomplete(final AutoCompleteTextView autoComplete, final String child) {

        mDatabase.child(child).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                List<String> list = new ArrayList<String>();

                for (DataSnapshot noteDataSnapshot : dataSnapshot.getChildren()) {

                    SchoolSubject schoolSubject = noteDataSnapshot.getValue(SchoolSubject.class);

                    list.add(schoolSubject.getName());

                }

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1, list);
                autoComplete.setAdapter(adapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("*** main activity ***", "onCancelled", databaseError.toException());
            }
        });
    }
}
