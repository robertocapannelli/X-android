package com.walkap.x_android.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.walkap.x_android.R;
import com.walkap.x_android.model.Scheduler;
import com.walkap.x_android.model.SchoolSubject;
import com.walkap.x_android.model.TimeSchoolSubject;

public class ChooseLessonTimeActivity extends AppCompatActivity {

    private final String TAG = "addSchedulerActivity";

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    private DatabaseReference mDatabase;

    EditText editTextStart;
    EditText editTextEnd;

    private String classroomName;
    private String schoolSubjectName;
    private int day;

    private String userUniversityKey;
    private String userFacultyKey;
    private String userDegreeCourseKey;

    private final String UNIVERSITY = "university";
    private final String FACULTY = "faculty";
    private final String DEGREE_COURSE = "degreeCourse";

    private final String SCHEDULER = "scheduler";
    private final String SCHOOOL_SUBJECT = "schoolSubject";
    private final String SCHOOL_SUBJECT_ID = "schoolSubjectId";

    private final String CLASSROOM = "classroom";

    private String schoolSubjectKey = "";

    private Context content = this;

    private final static int TIME_PICKER_INTERVAL = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_schedule);
        setTitle("ACTIVITY");


        mDatabase = FirebaseDatabase.getInstance().getReference();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        editTextStart = (EditText) findViewById(R.id.editTextStart);
        editTextEnd = (EditText) findViewById(R.id.editTextEnd);
        editTextStart.setKeyListener(null);
        editTextEnd.setKeyListener(null);

        editTextStart.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    showPicker(view);
                }
            }
        });

        editTextStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view == editTextStart) {
                    showPicker(view);
                }
            }
        });

        editTextEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view == editTextEnd) {
                    showPicker(view);
                }
            }
        });


        editTextEnd.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    showPicker(view);
                }
            }
        });

        Intent intent= getIntent();
        Bundle bundle = intent.getExtras();

        if(bundle != null)
        {
            classroomName =(String) bundle.get(CLASSROOM);
            schoolSubjectName = (String) bundle.get(SCHOOOL_SUBJECT);
            day = (Integer) bundle.get("day");

        }

        readDataFileDb();

        findSchoolSubject(schoolSubjectName);
    }

    public void showPicker(final View editTextView)
    {

        final Dialog d = new Dialog(ChooseLessonTimeActivity.this);
        d.setTitle("timePicker");
        d.setContentView(R.layout.time_select);
        Button b1 = (Button) d.findViewById(R.id.saveTimeButton);
        Button b2 = (Button) d.findViewById(R.id.cancelButton);
        final TimePicker timePicker = (TimePicker) d.findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true);

        if(editTextView.getId() == editTextStart.getId()){
            if(!editTextStart.getText().toString().isEmpty()){
                String text = editTextStart.getText().toString();
                Log.d(TAG, text);
                timePicker.setHour(Integer.parseInt(text.substring(0, text.indexOf(":") - 1)));
                timePicker.setMinute(Integer.parseInt(text.substring(text.indexOf(":") + 2)));
            }
        }
        else{
            if(!editTextEnd.getText().toString().isEmpty()) {
                String text = editTextEnd.getText().toString();
                timePicker.setHour(Integer.parseInt(text.substring(0, text.indexOf(":") - 1)));
                timePicker.setMinute(Integer.parseInt(text.substring(text.indexOf(":") + 2)));
            }
        }

        b1.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {

                String sHour = "" + timePicker.getHour();
                String sMinute = "" + timePicker.getMinute();

                if(timePicker.getHour() < 10){
                    sHour = "0" + sHour;
                }

                if(timePicker.getMinute() < 10 ){
                    sMinute = "0" + sMinute;
                }

                if(editTextView.getId() == editTextStart.getId()){
                    editTextStart.setText(sHour + " : " + sMinute);
                }
                else{
                    editTextEnd.setText(sHour + " : " + sMinute);
                }
                d.cancel();
            }
        });
        d.show();

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.cancel();
            }
        });
    }

    private void writeNewScheduler(String classroom, String schoolSubjectName, TimeSchoolSubject time) {

        Scheduler scheduler = new Scheduler(classroom, schoolSubjectName, time);
        SchoolSubject schoolSubject = new SchoolSubject(schoolSubjectName);

        String schedulerKey = mDatabase.child(SCHEDULER).push().getKey();

        mDatabase.child(SCHEDULER).child(schedulerKey).setValue(scheduler);

        if(schoolSubjectKey.isEmpty()) {
            String newSchoolSubjectKey = mDatabase.child(SCHOOOL_SUBJECT).push().getKey();

            mDatabase.child(SCHOOOL_SUBJECT).child(newSchoolSubjectKey).setValue(schoolSubject);
            mDatabase.child(SCHOOOL_SUBJECT).child(newSchoolSubjectKey).child(UNIVERSITY).child(userUniversityKey).child(userFacultyKey).child(userDegreeCourseKey).setValue(true);

            mDatabase.child(SCHEDULER).child(schedulerKey).child(SCHOOL_SUBJECT_ID).setValue(newSchoolSubjectKey);
            mDatabase.child(SCHEDULER).child(schedulerKey).child(SCHOOOL_SUBJECT).setValue(schoolSubjectName);

            schoolSubjectKey = newSchoolSubjectKey;

        }else{

            mDatabase.child(SCHOOOL_SUBJECT).child(schoolSubjectKey).child(UNIVERSITY).child(userUniversityKey).child(userFacultyKey).child(userDegreeCourseKey).setValue(true);
            mDatabase.child(SCHEDULER).child(schedulerKey).child(SCHOOL_SUBJECT_ID).setValue(schoolSubjectKey);
            mDatabase.child(SCHEDULER).child(schedulerKey).child(SCHOOOL_SUBJECT).setValue(schoolSubjectName);
        }

        mDatabase.child(SCHEDULER).child(schedulerKey).child(UNIVERSITY).setValue(userUniversityKey);
        mDatabase.child(SCHEDULER).child(schedulerKey).child(FACULTY).setValue(userFacultyKey);
        mDatabase.child(SCHEDULER).child(schedulerKey).child(DEGREE_COURSE).setValue(userDegreeCourseKey);
    }

    public void saveScheduler(View view) {

        String textStart = editTextStart.getText().toString();
        String textEnd = editTextEnd.getText().toString();

        if(textStart.isEmpty() || textEnd.isEmpty()){

            if(textStart.isEmpty()){
                editTextStart.setError(getResources().getString(R.string.required));
            }
            if(textEnd.isEmpty()){
                editTextEnd.setError(getResources().getString(R.string.required));
            }

        }
        else{
            Integer hourStart = Integer.parseInt(textStart.substring(0, textStart.indexOf(":") - 1));
            Integer minuteStart = Integer.parseInt(textStart.substring(textStart.indexOf(":") + 2));

            Integer hourEnd = Integer.parseInt(textEnd.substring(0, textEnd.indexOf(":") - 1));
            Integer minuteEnd = Integer.parseInt(textEnd.substring(textEnd.indexOf(":") + 2));

            int duration = (hourEnd - hourStart) * 60 - (minuteStart - minuteEnd);
            if(duration > 0){

                TimeSchoolSubject time = new TimeSchoolSubject(day, hourStart, minuteStart, duration);
                writeNewScheduler(classroomName, schoolSubjectName, time);

                Intent myIntent = new Intent(ChooseLessonTimeActivity.this, MainActivity.class);
                ChooseLessonTimeActivity.this.startActivity(myIntent);

            }
            else{

                AlertDialog.Builder builder;
                builder = new AlertDialog.Builder(content);

                builder.setTitle(R.string.general_error)
                        .setMessage(R.string.wrong_end_time)
                        .setPositiveButton(R.string.got_it, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                editTextEnd.setText("");
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

            }


        }


    }

    private void readDataFileDb(){

        mDatabase.child("users").child(mFirebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userUniversityKey = dataSnapshot.child(UNIVERSITY).getValue().toString();
                userFacultyKey = dataSnapshot.child(FACULTY).getValue().toString();
                userDegreeCourseKey = dataSnapshot.child(DEGREE_COURSE).getValue().toString();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException());
            }
        });
    }

    private void findSchoolSubject(final String schoolSubjectString){

        mDatabase.child(SCHOOOL_SUBJECT).addListenerForSingleValueEvent(new ValueEventListener() {
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