package com.walkap.x_android.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
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
public class OptionsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private String universityName;
    private String facultyName;
    private String degreeCourseName;

    private String MY_PREFS_NAME = "preferences";

    private AutoCompleteTextView university;
    private AutoCompleteTextView faculty;
    private AutoCompleteTextView degreeCourse;

    private String universityOldKey;
    private String facultyOldKey;
    private String degreeCourseOldKey;

    private static final String TAG = "OptionsFragment";

    private DatabaseReference mDatabase;

    private FirebaseAuth mFirebaseAuth;


    private OnFragmentInteractionListener mListener;

    public OptionsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OptionsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OptionsFragment newInstance(String param1, String param2) {
        OptionsFragment fragment = new OptionsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        mDatabase =  FirebaseDatabase.getInstance().getReference();
        mFirebaseAuth = FirebaseAuth.getInstance();

        readDataFile();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);

        final View rootView = inflater.inflate(R.layout.fragment_options, container, false);

        university = (AutoCompleteTextView) getActivity().findViewById(R.id.universityAutoCompleteTextView);
        faculty = (AutoCompleteTextView) getActivity().findViewById(R.id.facultyAutoCompleteTextView);
        degreeCourse = (AutoCompleteTextView) getActivity().findViewById(R.id.degreeCourseAutoCompleteTextView);


        //Button listener
        Button btn = (Button) rootView.findViewById(R.id.saveButton);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "button clicked");
                saveData();
            }
        });

        addListAutocomplete(university, "university");
        addListAutocomplete(faculty, "faculty");
        addListAutocomplete(degreeCourse, "degreeCourse");

        if(university != null){
            university.setText(universityName);
        }

        if(faculty != null){
            faculty.setText(facultyName);
        }

        if(degreeCourse != null){
            degreeCourse.setText(degreeCourseName);
        }

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_options, container, false);
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


    public void saveData(){

        String universityString = university.getText().toString();
        String facultyString = faculty.getText().toString();
        String degreeCourseString = degreeCourse.getText().toString();

        if(universityString.isEmpty()){

            Log.d(TAG, "uni empty");

            AlertDialog.Builder builder;
            builder = new AlertDialog.Builder(getActivity());

            builder.setTitle("university error")
                    .setMessage("university is empty")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
        else{
            if(facultyString.isEmpty()){

                Log.d(TAG, "faculty empty");

                AlertDialog.Builder builder;
                builder = new AlertDialog.Builder(getActivity());

                builder.setTitle("faculty error")
                        .setMessage("faculty is empty")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

            }
            else{
                if(degreeCourseString.isEmpty()){

                    Log.d(TAG, "degree empty");

                    AlertDialog.Builder builder;
                    builder = new AlertDialog.Builder(getActivity());

                    builder.setTitle("degree course error")
                            .setMessage("degree course is empty")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();

                }else {

                    String universityKey = mDatabase.child("university").push().getKey();
                    String facultyKey = mDatabase.child("faculty").push().getKey();
                    String degreeCourseKey = mDatabase.child("degreeCourse").push().getKey();

                    findUniversity(universityString, universityKey, facultyString, facultyKey, degreeCourseString, degreeCourseKey);

                    SharedPreferences.Editor editor = getActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                    editor.putString("university", universityString);
                    editor.putString("faculty", facultyString);
                    editor.putString("degreeCourse", degreeCourseString);
                    editor.apply();

                }
            }
        }
    }

    public void findUniversity(final String universityString,final String universityKey,
                               final String facultyString, final String facultyKey,
                               final String degreeCourseString, final String degreeCourseKey){

        mDatabase.child("university").addListenerForSingleValueEvent(new ValueEventListener() {
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

                    builder.setTitle("university not find")
                            .setMessage("university not found, do you want to add it?")
                            .setPositiveButton("add", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    University university = new University(universityString, "", "");
                                    mDatabase.child("university").child(universityKey).setValue(university);
                                    findFaculty(universityKey, facultyString, facultyKey, degreeCourseString, degreeCourseKey);

                                }
                            })
                            .setNegativeButton("select another university", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    university.setText("");
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
                else{
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

        mDatabase.child("faculty").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean find = false;
                facultyOldKey = "";
                for (DataSnapshot noteDataSnapshot : dataSnapshot.getChildren()) {
                    Faculty faculty = noteDataSnapshot.getValue(Faculty.class);
                    if (faculty.getName().equals(facultyString) ) {
                        facultyOldKey = noteDataSnapshot.getKey();
                        if(noteDataSnapshot.child("universities").child(universityOldKey).exists() && !universityOldKey.isEmpty()){
                            find = true;
                        }

                    }
                }
                if(!find){
                    AlertDialog.Builder builder;
                    builder = new AlertDialog.Builder(getActivity());

                    builder.setTitle("faculty not find")
                            .setMessage("faculty not found, do you want to add it?")
                            .setPositiveButton("add", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    findDegreeCourse(universityKey, facultyString, facultyKey, degreeCourseString, degreeCourseKey);

                                }
                            })
                            .setNegativeButton("select another faculty", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    faculty.setText("");
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
                else{
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

        mDatabase.child("degreeCourse").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean find = false;
                degreeCourseOldKey = "";
                for (DataSnapshot noteDataSnapshot : dataSnapshot.getChildren()) {
                    DegreeCourse degreeCourse = noteDataSnapshot.getValue(DegreeCourse.class);
                    Log.d("*** findDegree ***", degreeCourse.getName() + " = " + degreeCourseString + "?");
                    if (degreeCourse.getName().toString().equals(degreeCourseString) ) {
                        degreeCourseOldKey = noteDataSnapshot.getKey();
                        if(noteDataSnapshot.child("university").child(universityOldKey).child("faculty").child(facultyOldKey).exists()
                                && !facultyOldKey.isEmpty() && !universityOldKey.isEmpty()){
                            find = true;
                        }

                    }
                }
                if(!find){
                    AlertDialog.Builder builder;
                    builder = new AlertDialog.Builder(getActivity());

                    builder.setTitle("degree course not find")
                            .setMessage("degree course not found, do you want to add it?")
                            .setPositiveButton("add", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    if(degreeCourseOldKey.isEmpty()){
                                        DegreeCourse degreeCourse = new DegreeCourse(degreeCourseString);
                                        mDatabase.child("degreeCourse").child(degreeCourseKey).setValue(degreeCourse);

                                        if(facultyOldKey.isEmpty()){
                                            Faculty faculty = new Faculty(facultyString);
                                            mDatabase.child("faculty").child(facultyKey).setValue(faculty);

                                            if(universityOldKey.isEmpty()) {
                                                mDatabase.child("faculty").child(facultyKey).child("universities").child(universityKey).setValue(true);
                                                mDatabase.child("university").child(universityKey).child("faculties").child(facultyKey).setValue(true);
                                                mDatabase.child("degreeCourse").child(degreeCourseKey).child("university").child(universityKey).child("faculty").child(facultyKey).setValue(true);
                                            }
                                            else{
                                                mDatabase.child("faculty").child(facultyKey).child("universities").child(universityOldKey).setValue(true);
                                                mDatabase.child("university").child(universityOldKey).child("faculties").child(facultyKey).setValue(true);
                                                mDatabase.child("degreeCourse").child(degreeCourseKey).child("university").child(universityOldKey).child("faculty").child(facultyKey).setValue(true);
                                            }
                                        }
                                        else{
                                            if(universityOldKey.isEmpty()) {
                                                mDatabase.child("faculty").child(facultyOldKey).child("universities").child(universityKey).setValue(true);
                                                mDatabase.child("university").child(universityKey).child("faculties").child(facultyOldKey).setValue(true);
                                                mDatabase.child("degreeCourse").child(degreeCourseKey).child("university").child(universityKey).child("faculty").child(facultyOldKey).setValue(true);
                                            }
                                            else{
                                                mDatabase.child("faculty").child(facultyOldKey).child("universities").child(universityOldKey).setValue(true);
                                                mDatabase.child("university").child(universityOldKey).child("faculties").child(facultyOldKey).setValue(true);
                                                mDatabase.child("degreeCourse").child(degreeCourseKey).child("university").child(universityOldKey).child("faculty").child(facultyOldKey).setValue(true);
                                            }
                                        }
                                    }
                                    else{
                                        if(facultyOldKey.isEmpty()){
                                            Faculty faculty = new Faculty(facultyString);
                                            mDatabase.child("faculty").child(facultyKey).setValue(faculty);

                                            if(universityOldKey.isEmpty()) {
                                                mDatabase.child("faculty").child(facultyKey).child("universities").child(universityKey).setValue(true);
                                                mDatabase.child("university").child(universityKey).child("faculties").child(facultyKey).setValue(true);
                                                mDatabase.child("degreeCourse").child(degreeCourseOldKey).child("university").child(universityKey).child("faculty").child(facultyKey).setValue(true);
                                            }
                                            else{
                                                mDatabase.child("faculty").child(facultyKey).child("universities").child(universityOldKey).setValue(true);
                                                mDatabase.child("university").child(universityOldKey).child("faculties").child(facultyKey).setValue(true);
                                                mDatabase.child("degreeCourse").child(degreeCourseOldKey).child("university").child(universityOldKey).child("faculty").child(facultyKey).setValue(true);
                                            }
                                        }
                                        else{
                                            if(universityOldKey.isEmpty()) {
                                                mDatabase.child("faculty").child(facultyOldKey).child("universities").child(universityKey).setValue(true);
                                                mDatabase.child("university").child(universityKey).child("faculties").child(facultyOldKey).setValue(true);
                                                mDatabase.child("degreeCourse").child(degreeCourseOldKey).child("university").child(universityKey).child("faculty").child(facultyOldKey).setValue(true);
                                            }
                                            else{
                                                mDatabase.child("faculty").child(facultyOldKey).child("universities").child(universityOldKey).setValue(true);
                                                mDatabase.child("university").child(universityOldKey).child("faculties").child(facultyOldKey).setValue(true);
                                                mDatabase.child("degreeCourse").child(degreeCourseOldKey).child("university").child(universityOldKey).child("faculty").child(facultyOldKey).setValue(true);
                                            }
                                        }
                                    }

                                    Intent myIntent = new Intent(getActivity(), MainActivity.class);
                                    getActivity().startActivity(myIntent);

                                }
                            })
                            .setNegativeButton("select another degree course", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    degreeCourse.setText("");
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
                else{
                    Intent myIntent = new Intent(getActivity(), MainActivity.class);
                    getActivity().startActivity(myIntent);
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
        universityName = prefs.getString("university", "");
        facultyName = prefs.getString("faculty", "");
        degreeCourseName = prefs.getString("degreeCourse", "");
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

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, list);

                if(autoComplete != null){
                    autoComplete.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("*** main activity ***", "onCancelled", databaseError.toException());
            }
        });
    }
}
