package com.walkap.x_android.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.walkap.x_android.R;
import com.walkap.x_android.adapter.CustomAdapter;
import com.walkap.x_android.model.DegreeCourse;
import com.walkap.x_android.model.Faculty;
import com.walkap.x_android.model.Scheduler;
import com.walkap.x_android.model.University;

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
public class HomeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private DatabaseReference mDatabase;

    ListView listView;

    Context content;

    private String MY_PREFS_NAME = "preferences";

    private String universityKey = "";
    private String facultyKey = "";
    private String degreeCourseKey = "";

    private int[] positionGridView = new int[] {0, 0, 0, 0, 0, 0};
    GridView gridView;

    private String universityName;
    private String facultyName;
    private String degreeCourseName;
    private Set<String> schoolSubjectList;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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

        readDataFile();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onStart(){
        super.onStart();

        content = getActivity().getApplicationContext();

        if(universityName.isEmpty() || facultyName.isEmpty() || degreeCourseName.isEmpty()){
            Fragment fragment = new OptionsFragment();

            FragmentManager fragmentManager = getFragmentManager();

            fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
        }
        else{
            findUniversityKey(universityName);
            findFacultyKey(facultyName);
            findDegreeCourseKey(degreeCourseName);
        }

        listView = (ListView) getView().findViewById(R.id.mainActivityListView);

        gridView = (GridView) getView().findViewById(R.id.mainActivityGridView);
        String[] mainGrid = new String[]{
                "L",   "M",  "M",    "G",  "V",  "S"
        };

        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        positionGridView[day - 2] = 1;

        /*ListAdapter adapterGrid = new ArrayAdapter<String>(content,
                android.R.layout.simple_list_item_1, mainGrid);*/

        gridView.setAdapter(new ArrayAdapter<String>(content,
                android.R.layout.simple_list_item_1, mainGrid)
        {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text = (TextView) view.findViewById(android.R.id.text1);
                text.setTextColor(Color.BLACK);
                return view;
            }
        });
        gridView.setOnItemClickListener(GridClickListener);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(final AdapterView<?> parent, View view, final int position, long id) {

                AlertDialog.Builder builder;
                builder = new AlertDialog.Builder(getActivity());

                final Scheduler scheduler = (Scheduler) parent.getItemAtPosition(position);

                builder.setTitle("delete")
                        .setMessage("do u want delete this " + scheduler.getSchoolSubject() + " scheduler? ")
                        .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                mDatabase.child("scheduler").child(scheduler.getSchedulerId()).removeValue();
                                showScheduler();

                            }
                        })
                        .setNegativeButton("no", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });

    }

    /*@Override
    public void onStop(){
        super.onStop();

        gridView.dispatchSystemUiVisibilityChanged(0);
    }*/

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
        universityName = prefs.getString("university", "");
        facultyName = prefs.getString("faculty", "");
        degreeCourseName = prefs.getString("degreeCourse", "");
        schoolSubjectList = prefs.getStringSet("schoolSubject", null);

    }

    AdapterView.OnItemClickListener GridClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> adapter, View view,
                                int position, long id) {

            if(positionGridView[position] == 0) {
                positionGridView = setAllLessOne(position);
            }

            colorGridView();

            showScheduler();

        }

    };

    private void colorGridView() {
        for(int i = 0; i < gridView.getNumColumns(); i++) {
            if(positionGridView[i] == 0) {
                gridView.getChildAt(i).setBackgroundColor(Color.WHITE);
            }
            else {
                gridView.getChildAt(i).setBackgroundColor(Color.CYAN);
            }
        }
    }

    public int[] setAllLessOne(int one) {
        int[] positionGridView = new int[]{0, 0, 0, 0, 0, 0};
        positionGridView[one] = 1;
        return  positionGridView;
    }

    public int actualPositionGridView(){
        int i;
        for(i = 0; i <= 5; i++){
            if(positionGridView[i] == 1){
                return i;
            }
        }
        return -1;
    }

    public void showScheduler() {

        mDatabase.child("scheduler").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Scheduler> list = new ArrayList<Scheduler>();
                final int day = actualPositionGridView();

                // attenzione errore se lista preferenze vuota

                for (DataSnapshot noteDataSnapshot : dataSnapshot.getChildren()) {
                    Scheduler scheduler = noteDataSnapshot.getValue(Scheduler.class);

                    if (day == scheduler.getTime().getDay() && noteDataSnapshot.child("university").getValue().toString().equals(universityKey)
                            && noteDataSnapshot.child("faculty").getValue().toString().equals(facultyKey) && noteDataSnapshot.child("degreeCourse").getValue().toString().equals(degreeCourseKey)
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
                Log.e("*** main activity ***", "onCancelled", databaseError.toException());
            }
        });
    }

    private void findUniversityKey(final String universityString){

        mDatabase.child("university").addListenerForSingleValueEvent(new ValueEventListener() {
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

        mDatabase.child("faculty").addListenerForSingleValueEvent(new ValueEventListener() {
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

        mDatabase.child("degreeCourse").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot noteDataSnapshot : dataSnapshot.getChildren()) {
                    DegreeCourse degreeCourse = noteDataSnapshot.getValue(DegreeCourse.class);
                    if (degreeCourse.getName().equals(degreeCourseString)) {
                        degreeCourseKey = noteDataSnapshot.getKey();
                        if(schoolSubjectList != null) {
                            showScheduler();
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
