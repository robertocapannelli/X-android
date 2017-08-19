package com.walkap.x_android.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ToggleButton;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.walkap.x_android.R;
import com.walkap.x_android.model.DegreeCourse;
import com.walkap.x_android.model.Faculty;
import com.walkap.x_android.model.Scheduler;
import com.walkap.x_android.model.SchoolSubject;
import com.walkap.x_android.model.TimeSchoolSubject;
import com.walkap.x_android.model.University;

import java.util.Vector;

public class addScheduleActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;

    static private int numColumn = 6;
    static private int numRow = 20;

    static private int startHour = 8;

    private String classroomName;
    private String schoolSubjectName;
    private String universityName;
    private String facultyName;
    private String degreeCourseName;

    private Boolean waitForSecondTap = false;
    private int beginning = 0;

    private String universityKey = "";
    private String facultyKey = "";
    private String degreeCourseKey = "";
    private String schoolSubjectKey = "";

    private String MY_PREFS_NAME = "preferences";

    private int[] positionGridView = new int[] {1, 0, 0, 0, 0, 0};

    private int[][] positionListView = new int[][] {{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}};

    private GridView gridView;
    private ListView listView;

    //TglButton animation

    public void initialization() {

        // Loading 'checkedAnimation' animation...
        final Animation checkedAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.enable_toggle_button_anim);

        // Loading 'unCheckedAnimation' animation...
        final Animation unCheckedAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.disable_toggle_button_anim);

        // Initialize all "ToggleButton" object...
        Vector<ToggleButton> myToggleButtons = new Vector<>();

        myToggleButtons.add((ToggleButton) findViewById(R.id.tglBtn_M));
        myToggleButtons.add((ToggleButton) findViewById(R.id.tglBtn_T));
        myToggleButtons.add((ToggleButton) findViewById(R.id.tglBtn_W));
        myToggleButtons.add((ToggleButton) findViewById(R.id.tglBtn_Th));
        myToggleButtons.add((ToggleButton) findViewById(R.id.tglBtn_F));
        myToggleButtons.add((ToggleButton) findViewById(R.id.tglBtn_S));

        // Add event and animation...
        for(ToggleButton myToggleButton : myToggleButtons)
        {
            myToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                    if (isChecked) {
                        compoundButton.setTextColor(getResources().getColor(R.color.green20, null));
                        compoundButton.setTypeface(Typeface.DEFAULT_BOLD);
                        compoundButton.startAnimation(checkedAnimation);

                    } else {
                        compoundButton.setTextColor(getResources().getColor(R.color.primary_text, null));
                        compoundButton.setTypeface(Typeface.DEFAULT);
                        compoundButton.startAnimation(unCheckedAnimation);
                    }
                }

            });
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_schedule);

        initialization();


        mDatabase = FirebaseDatabase.getInstance().getReference();

        gridView = (GridView) this.findViewById(R.id.schedulerGridView);
        String[] schedulerGrid = new String[]{
                "L",   "M",  "M",    "G",  "V",  "S"
        };

        ListAdapter adapterGrid = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, schedulerGrid);
        gridView.setAdapter(adapterGrid);
        gridView.setOnItemClickListener(GridClickListener);

        listView = (ListView) this.findViewById(R.id.schedulerListView);
        String[] schedulerList = new String[]{
                "8:00",  "8:15",  "8:30",  "8:45",  "9:00",  "9:15",  "9:30",  "9:45",
                "10:00", "10:15", "10:30", "10:45", "11:00", "11:15", "11:30", "11:45",
                "12:00", "12:15", "12:30", "12:45"};

        ListAdapter adapterList = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, schedulerList);
        listView.setAdapter(adapterList);
        listView.setOnItemClickListener(ListClickListener);

        Intent intent= getIntent();
        Bundle bundle = intent.getExtras();

        if(bundle != null)
        {
            classroomName =(String) bundle.get("classroom");
            schoolSubjectName = (String) bundle.get("schoolSubject");
        }

        readDataFile();

        findUniversityKey(universityName);
        findFacultyKey(facultyName);
        findDegreeCourse(degreeCourseName);
        findSchoolSubject(schoolSubjectName);

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        for(int i = 0; i < gridView.getNumColumns(); i++) {
            if(positionGridView[i] == 0) {
                gridView.getChildAt(i).setBackgroundColor(Color.WHITE);
            }
            else {
                gridView.getChildAt(i).setBackgroundColor(Color.CYAN);
            }
        };
    }

    private void writeNewScheduler(String classroom, String schoolSubjectName,
                                   String degreeCourse, TimeSchoolSubject time) {

        Scheduler scheduler = new Scheduler(classroom, schoolSubjectName, time);

        SchoolSubject schoolSubject = new SchoolSubject(schoolSubjectName);
        String schedulerKey = mDatabase.child("scheduler").push().getKey();

        mDatabase.child("scheduler").child(schedulerKey).setValue(scheduler);

        if(schoolSubjectKey.isEmpty()) {
            String newSchoolSubjectKey = mDatabase.child("schoolSubject").push().getKey();

            mDatabase.child("schoolSubject").child(newSchoolSubjectKey).setValue(schoolSubject);
            mDatabase.child("schoolSubject").child(newSchoolSubjectKey).child("university").child(universityKey).child(facultyKey).child(degreeCourseKey).setValue(true);

            mDatabase.child("scheduler").child(schedulerKey).child("schoolSubjectId").setValue(newSchoolSubjectKey);
            mDatabase.child("scheduler").child(schedulerKey).child("schoolSubject").setValue(schoolSubjectName);

            schoolSubjectKey = newSchoolSubjectKey;

        }
        else{

            mDatabase.child("schoolSubject").child(schoolSubjectKey).child("university").child(universityKey).child(facultyKey).child(degreeCourseKey).setValue(true);
            mDatabase.child("scheduler").child(schedulerKey).child("schoolSubjectId").setValue(schoolSubjectKey);
            mDatabase.child("scheduler").child(schedulerKey).child("schoolSubject").setValue(schoolSubjectName);
        }

        mDatabase.child("scheduler").child(schedulerKey).child("university").setValue(universityKey);
        mDatabase.child("scheduler").child(schedulerKey).child("faculty").setValue(facultyKey);
        mDatabase.child("scheduler").child(schedulerKey).child("degreeCourse").setValue(degreeCourseKey);
    }

    private int getGridViewSelected() {
        int selected = 0;

        for(int i = 0; i < gridView.getNumColumns(); i++) {
            if(positionGridView[i] == 1)
                selected = i;
        }

        return selected;
    }

    public int[] setAllLessOne(int one) {
        int[] positionGridView = new int[]{0, 0, 0, 0, 0, 0};
        positionGridView[one] = 1;
        return  positionGridView;
    }

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

    public void repaintListView(int day) {
        for(int i = 0; i < numRow; i++) {
            if(positionListView[day][i] == 0) {
                getViewByPosition(i, listView).setBackgroundColor(Color.WHITE);
            }
            else {
                getViewByPosition(i, listView).setBackgroundColor(Color.MAGENTA);
            }
        }
    }

    public View getViewByPosition(int pos, ListView listView) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition ) {
            return listView.getAdapter().getView(pos, null, listView);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }

    public void saveScheduler(View view) {
        int i,j,count;

        count = 0;

        for(i = 0; i < numColumn; i++){
            for(j = 0; j < numRow; j++) {
                if (positionListView[i][j] == 1){
                    count ++;
                }

                if(positionListView[i][j] == 0 && count !=0){
                    TimeSchoolSubject time = new TimeSchoolSubject(i, startHour + (j - count) / 4 , ((j - count) % 4) * 15, (count - 1) * 15);
                    writeNewScheduler(classroomName, schoolSubjectName, degreeCourseName, time);
                    count = 0;
                }
            }

        }

        Intent myIntent = new Intent(addScheduleActivity.this, MainActivity.class);
        addScheduleActivity.this.startActivity(myIntent);

    }

    AdapterView.OnItemClickListener GridClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> adapter, View view,
                                int position, long id) {

            if(positionGridView[position] == 0) {
                positionGridView = setAllLessOne(position);
                repaintListView(position);
            }

            waitForSecondTap = false;
            colorGridView();

        }

    };

    AdapterView.OnItemClickListener ListClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> adapter, View view,
                                int position, long id) {

            int selected = getGridViewSelected();

            if(!waitForSecondTap) {
                if (positionListView[selected][position] == 0) {
                    positionListView[selected][position] = 1;
                    view.setBackgroundColor(Color.MAGENTA);
                    waitForSecondTap = true;
                    beginning = position;
                } else {
                    positionListView[selected][position] = 0;
                    view.setBackgroundColor(Color.WHITE);
                }
            }
            else {
                if (positionListView[selected][position] == 0) {
                    setPosition(beginning, position, selected);
                } else {
                    positionListView[selected][position] = 0;
                    view.setBackgroundColor(Color.WHITE);
                }
                waitForSecondTap = false;
            }
            Log.d("*** second tap ***", "" + waitForSecondTap );
        }

    };

    private void setPosition(int beginning, int end, int day){
        for(int i = beginning; i <= end; i++){
            positionListView[day][i] = 1;
            listView.getChildAt(i).setBackgroundColor(Color.MAGENTA);
        }
    }

    private void readDataFile(){
        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        universityName = prefs.getString("university", "");
        facultyName = prefs.getString("faculty", "");
        degreeCourseName = prefs.getString("degreeCourse", "");
    }

    private void findUniversityKey(final String universityString){

        mDatabase.child("university").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot noteDataSnapshot : dataSnapshot.getChildren()) {
                    University university = noteDataSnapshot.getValue(University.class);
                    if (university.getName().equals(universityString)) {
                        universityKey = noteDataSnapshot.getKey();
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

    private void findDegreeCourse(final String degreeCourseString){

        mDatabase.child("degreeCourse").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot noteDataSnapshot : dataSnapshot.getChildren()) {
                    DegreeCourse degreeCourse = noteDataSnapshot.getValue(DegreeCourse.class);
                    if (degreeCourse.getName().equals(degreeCourseString)) {
                        degreeCourseKey = noteDataSnapshot.getKey();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void findSchoolSubject(final String schoolSubjectString){

        mDatabase.child("schoolSubject").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot noteDataSnapshot : dataSnapshot.getChildren()) {
                    SchoolSubject schoolSubject = noteDataSnapshot.getValue(SchoolSubject.class);
                    if (schoolSubject.getName().equals(schoolSubjectString)) {
                        schoolSubjectKey = noteDataSnapshot.getKey();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
