package com.walkap.x_android.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.walkap.x_android.R;
import com.walkap.x_android.model.SchoolSubject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AddSchoolSubjectFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AddSchoolSubjectFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddSchoolSubjectFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "AddSchoolSubjectFrag";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String CLASSROOM = "classRoom";
    private static final String SCHOOLSUBJECT = "schoolSubject";

    // TODO: Rename and change types of parameters
    private String classRoom;
    private String schoolSubject;

    private OnFragmentInteractionListener mListener;

    private AutoCompleteTextView schoolSubjectAuto;
    private ListView schoolSubjects;

    private List<String> list;

    private DatabaseReference mDatabase;

    private String MY_PREFS_NAME = "preferences";
    private Set<String> schoolSubjectList;

    public AddSchoolSubjectFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param classRoom Parameter 1.
     * @param schoolSubject Parameter 2.
     * @return A new instance of fragment AddScheduleFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddSchoolSubjectFragment newInstance(String classRoom, String schoolSubject) {
        AddSchoolSubjectFragment fragment = new AddSchoolSubjectFragment();
        Bundle args = new Bundle();
        args.putString(CLASSROOM, classRoom);
        args.putString(SCHOOLSUBJECT, schoolSubject);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            classRoom = getArguments().getString(CLASSROOM);
            schoolSubject = getArguments().getString(SCHOOLSUBJECT);
        }

        Log.d(TAG, classRoom + " " + schoolSubject);

        mDatabase =  FirebaseDatabase.getInstance().getReference();

        SharedPreferences prefs = getActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        schoolSubjectList = prefs.getStringSet(SCHOOLSUBJECT, null);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_add_school_subject, container, false);

        schoolSubjectAuto = (AutoCompleteTextView) rootView.findViewById(R.id.schoolSubjectAutoCompleteTextView);
        schoolSubjects = (ListView) rootView.findViewById(R.id.schoolSubjectListView);

        Button btn = (Button) rootView.findViewById(R.id.addSchoolSubject);
        btn.setOnClickListener(this);

        if(schoolSubjectList != null) {
            list = new ArrayList<String>(schoolSubjectList);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1,  list);
            schoolSubjects.setAdapter(adapter);
        }

        addListAutocomplete();

        schoolSubjects.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(final AdapterView<?> parent, View view, final int position, long id) {

                AlertDialog.Builder builder;
                builder = new AlertDialog.Builder(getActivity());

                builder.setTitle(R.string.delete)
                        .setMessage(getResources().getString(R.string.do_not_want_to_follow_anymore, parent.getItemAtPosition(position).toString()))
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                SharedPreferences.Editor editor = getActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                                schoolSubjectList.remove(parent.getItemAtPosition(position).toString());

                                editor.putStringSet(SCHOOLSUBJECT, schoolSubjectList);
                                editor.apply();

                                List<String> list = new ArrayList<String>(schoolSubjectList);
                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1,  list);
                                schoolSubjects.setAdapter(adapter);
                            }
                        })
                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });


        return rootView;
    }

    @Override
    public void onClick(View view) {
        addSchoolSubject();
    }


    @Override
    public void onStart(){
        super.onStart();

        /*schoolSubjectAuto = (AutoCompleteTextView) this.findViewById(R.id.schoolSubjectAutoCompleteTextView);
        schoolSubjects = (ListView) this.findViewById(R.id.schoolSubjectListView);*/
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

    public void addListAutocomplete() {

        mDatabase.child(SCHOOLSUBJECT).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> list = new ArrayList<String>();

                for (DataSnapshot noteDataSnapshot : dataSnapshot.getChildren()) {
                    SchoolSubject schoolSubject = noteDataSnapshot.getValue(SchoolSubject.class);

                    list.add(schoolSubject.getName());
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, list);
                schoolSubjectAuto.setAdapter(adapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException());
            }
        });
    }

    public void addSchoolSubject() {
        SharedPreferences.Editor editor = getActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();

        if(schoolSubjectAuto.getText().toString().isEmpty()) {
            AlertDialog.Builder builder;
            builder = new AlertDialog.Builder(getActivity());

            builder.setTitle(R.string.school_subject_error)
                    .setMessage(R.string.school_subject_empty)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
        else{
            if(schoolSubjectList == null){
                Log.d(TAG, schoolSubjectAuto.getText().toString());

                Set<String> set = new HashSet<String>();
                set.add(schoolSubjectAuto.getText().toString());
                editor.putStringSet(SCHOOLSUBJECT, set);
                editor.commit();

            }
            else {
                if (!schoolSubjectList.contains(schoolSubjectAuto.getText().toString())) {
                    Log.d(TAG, schoolSubjectAuto.getText().toString());

                    Set<String> set = new HashSet<String>();
                    list.add(schoolSubjectAuto.getText().toString());
                    set.addAll(list);
                    editor.putStringSet(SCHOOLSUBJECT, set);
                    editor.apply();
                }
            }

            /*Intent myIntent = new Intent(AddSchoolSubject.this, MainActivity.class);
            AddSchoolSubject.this.startActivity(myIntent);*/
        }
    }
}
