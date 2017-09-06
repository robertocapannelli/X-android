package com.walkap.x_android.fragment;

import android.content.Context;
import android.content.DialogInterface;
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
import com.google.firebase.database.ValueEventListener;
import com.walkap.x_android.R;
import com.walkap.x_android.model.SchoolSubject;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AddSchoolSubjectFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AddSchoolSubjectFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddSchoolSubjectFragment extends BaseFragment implements View.OnClickListener {

    private static final String TAG = "AddSchoolSubjectFrag";

    private static final String USERS = "users";
    private static final String PREFERENCES = "preferences";

    private String schoolSubjectKey = "";

    private boolean find;

    private OnFragmentInteractionListener mListener;

    private AutoCompleteTextView schoolSubjectAuto;
    private ListView schoolSubjects;

    public AddSchoolSubjectFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        setAdapterList();

        addListAutocomplete();

        schoolSubjects.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(final AdapterView<?> parent, View view, final int position, long id) {

                AlertDialog.Builder builder;
                builder = new AlertDialog.Builder(getActivity());

                builder.setTitle(R.string.delete)
                        .setMessage(getResources().getString(R.string.do_not_want_to_follow_anymore, parent.getItemAtPosition(position).toString()))
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                deleteSchoolSubject(parent.getItemAtPosition(position).toString());

                                setAdapterList();

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
        getSchoolSubjectKey(schoolSubjectAuto.getText().toString());
    }


    @Override
    public void onStart(){
        super.onStart();
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

    private void setAdapterList(){
        mDatabase.child(USERS).child(mFirebaseUser.getUid()).child(PREFERENCES).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> list = new ArrayList<String>();

                for (DataSnapshot noteDataSnapshot : dataSnapshot.getChildren()) {
                    list.add(noteDataSnapshot.getValue().toString());
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, list);

                schoolSubjects.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException());
            }
        });
    }

    private void getSchoolSubjectKey(final String name){

        mDatabase.child(SCHOOLSUBJECT).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot noteDataSnapshot : dataSnapshot.getChildren()) {
                    if (noteDataSnapshot.child("name").getValue().toString().equals(name)){
                        Log.d("*** setKey ***", "  " + noteDataSnapshot.child("name").getValue().toString());
                        schoolSubjectKey = noteDataSnapshot.getKey();
                        if(schoolSubjectKey.isEmpty()){
                            /*** materia non trovata gestiscila ***/
                        }
                        else {
                            Log.d("*** setKey ***", "  " + schoolSubjectKey);
                            findInPreferences(schoolSubjectKey, name);
                        }
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException());
            }
        });
    }

    private void findInPreferences(final String key, final String name){
        mDatabase.child(USERS).child(mFirebaseUser.getUid()).child(PREFERENCES).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                find = false;
                for (DataSnapshot noteDataSnapshot : dataSnapshot.getChildren()) {
                    if (noteDataSnapshot.exists() && noteDataSnapshot.getKey().equals(key)){
                        Log.d("*** findInPrefer ***", " find ");
                        find = true;
                        break;
                    }
                }
                Log.d("*** findInPrefer ***", "  " + find);
                if(!find){
                    addInPreferences(schoolSubjectKey, name);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException());
            }
        });
    }

    private void addInPreferences(String key, String name){
        mDatabase.child(USERS).child(mFirebaseUser.getUid()).child(PREFERENCES).child(key).setValue(name);
        setAdapterList();

    }

    private void deleteSchoolSubject(final String name){
        mDatabase.child(SCHOOLSUBJECT).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot noteDataSnapshot : dataSnapshot.getChildren()) {
                    if (noteDataSnapshot.child("name").getValue().toString().equals(name)){
                        Log.d("*** delete ***", "  " + noteDataSnapshot.child("name").getValue().toString());
                        String Key = noteDataSnapshot.getKey();
                        mDatabase.child(USERS).child(mFirebaseUser.getUid()).child(PREFERENCES).child(Key).removeValue();
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException());
            }
        });
    }
}
