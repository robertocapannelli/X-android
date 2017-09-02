package com.walkap.x_android.fragment;

import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ToggleButton;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.walkap.x_android.R;
import com.walkap.x_android.adapter.CustomAdapter;
import com.walkap.x_android.model.Scheduler;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends BaseFragment {

    private OnFragmentInteractionListener mListener;

    private DatabaseReference mDatabase;

    ListView listView;

    Context content;

    private String MY_PREFS_NAME = "preferences";

    private String userUniversityKey;
    private String userFacultyKey;
    private String userDegreeCourseKey;

    private Set<String> schoolSubjectList;

    private String[] daysArray;

    private final String TAG = "HomeFragment";

    private ToggleButton toggleButton1;
    private ToggleButton toggleButton2;
    private ToggleButton toggleButton3;
    private ToggleButton toggleButton4;
    private ToggleButton toggleButton5;
    private ToggleButton toggleButton6;


    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDatabase =  FirebaseDatabase.getInstance().getReference();
        daysArray = getResources().getStringArray(R.array.daysArray);
        readDataFile();
        readDataFileDb();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        toggleButton1 = (ToggleButton) rootView.findViewById(R.id.toggleButton1);
        toggleButton2 = (ToggleButton) rootView.findViewById(R.id.toggleButton2);
        toggleButton3 = (ToggleButton) rootView.findViewById(R.id.toggleButton3);
        toggleButton4 = (ToggleButton) rootView.findViewById(R.id.toggleButton4);
        toggleButton5 = (ToggleButton) rootView.findViewById(R.id.toggleButton5);
        toggleButton6 = (ToggleButton) rootView.findViewById(R.id.toggleButton6);

        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK) - 2;

        switch (day){
            case 0: toggleButton1.setChecked(true); break;
            case 1: toggleButton2.setChecked(true); break;
            case 2: toggleButton3.setChecked(true); break;
            case 3: toggleButton4.setChecked(true); break;
            case 4: toggleButton5.setChecked(true); break;
            case 5: toggleButton6.setChecked(true); break;
            default:                                break;
        }

        return rootView;
    }

    @Override
    public void onStart(){
        super.onStart();

        content = getActivity().getApplicationContext();

        listView = (ListView) getView().findViewById(R.id.mainActivityListView);

        toggleButton1.setOnClickListener(toggleButtonListener);
        toggleButton2.setOnClickListener(toggleButtonListener);
        toggleButton3.setOnClickListener(toggleButtonListener);
        toggleButton4.setOnClickListener(toggleButtonListener);
        toggleButton5.setOnClickListener(toggleButtonListener);
        toggleButton6.setOnClickListener(toggleButtonListener);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(final AdapterView<?> parent, View view, final int position, long id) {

                AlertDialog.Builder builder;
                builder = new AlertDialog.Builder(getActivity());

                final Scheduler scheduler = (Scheduler) parent.getItemAtPosition(position);

                builder.setTitle(R.string.delete)
                        .setMessage(getResources().getString(R.string.do_you_want_delete_this_schedule, scheduler.getSchoolSubject()))
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                mDatabase.child("scheduler").child(scheduler.getSchedulerId()).removeValue();
                                showScheduler();

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

    private void readDataFile(){
        SharedPreferences prefs = this.getActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        schoolSubjectList = prefs.getStringSet(SCHOOLSUBJECT, null);
    }

    private void readDataFileDb(){

        mDatabase.child("users").child(mFirebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userUniversityKey = dataSnapshot.child(UNIVERSITY).getValue().toString();
                userFacultyKey = dataSnapshot.child(FACULTY).getValue().toString();
                userDegreeCourseKey = dataSnapshot.child(DEGREE_COURSE).getValue().toString();

                Log.d("*** read db ***", userUniversityKey + "  " + userFacultyKey + "  " + userDegreeCourseKey);

                if(userUniversityKey.isEmpty() || userFacultyKey.isEmpty() || userDegreeCourseKey.isEmpty()){
                    Fragment fragment = new OptionsFragment();

                    FragmentManager fragmentManager = getFragmentManager();

                    fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
                }

                showScheduler();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException());
            }
        });
    }

    final View.OnClickListener toggleButtonListener = new View.OnClickListener() {
        public void onClick(final View v) {
            switch(v.getId()) {
                case R.id.toggleButton1:
                    setAllLessOne(0);
                    break;
                case R.id.toggleButton2:
                    setAllLessOne(1);
                    break;
                case R.id.toggleButton3:
                    setAllLessOne(2);
                    break;
                case R.id.toggleButton4:
                    setAllLessOne(3);
                    break;
                case R.id.toggleButton5:
                    setAllLessOne(4);
                    break;
                case R.id.toggleButton6:
                    setAllLessOne(5);
                    break;
            }

            showScheduler();
        }
    };

    public void setAllLessOne(int one) {
        toggleButton1.setChecked(false);
        toggleButton2.setChecked(false);
        toggleButton3.setChecked(false);
        toggleButton4.setChecked(false);
        toggleButton5.setChecked(false);
        toggleButton6.setChecked(false);

        switch (one){
            case 0: toggleButton1.setChecked(true); break;
            case 1: toggleButton2.setChecked(true); break;
            case 2: toggleButton3.setChecked(true); break;
            case 3: toggleButton4.setChecked(true); break;
            case 4: toggleButton5.setChecked(true); break;
            case 5: toggleButton6.setChecked(true); break;
            default:                                break;
        }
    }

    public int actualDaySelected(){
        if(toggleButton1.isChecked()){
            return 0;
        }
        if(toggleButton2.isChecked()){
            return 1;
        }
        if(toggleButton3.isChecked()){
            return 2;
        }
        if(toggleButton4.isChecked()){
            return 3;
        }
        if(toggleButton5.isChecked()){
            return 4;
        }
        if(toggleButton6.isChecked()){
            return 5;
        }

        return -1;
    }

    public void showScheduler() {

        mDatabase.child("scheduler").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Scheduler> list = new ArrayList<Scheduler>();
                final int day = actualDaySelected();

                for (DataSnapshot noteDataSnapshot : dataSnapshot.getChildren()) {
                    Scheduler scheduler = noteDataSnapshot.getValue(Scheduler.class);

                    if (!schoolSubjectList.isEmpty() && day == scheduler.getTime().getDay() && noteDataSnapshot.child(UNIVERSITY).getValue().toString().equals(userUniversityKey)
                            && noteDataSnapshot.child(FACULTY).getValue().toString().equals(userFacultyKey)
                            && noteDataSnapshot.child(DEGREE_COURSE).getValue().toString().equals(userDegreeCourseKey)
                            && schoolSubjectList.contains(scheduler.getSchoolSubject())) {
                        scheduler.setSchedulerId(noteDataSnapshot.getKey());
                        list.add(scheduler);
                    }
                }
                CustomAdapter adapter = new CustomAdapter(content, R.layout.row, list);

                listView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException());
            }
        });
    }

}
