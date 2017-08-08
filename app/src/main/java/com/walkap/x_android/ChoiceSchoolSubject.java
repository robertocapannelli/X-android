package com.walkap.x_android;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class ChoiceSchoolSubject extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice_school_subject);
    }

    public void addScheduler(View view) {
        EditText classroomEditText = (EditText) findViewById(R.id.classroomEditText);
        EditText schoolSubjectEditText = (EditText) findViewById(R.id.schoolSubjectEditText);

        String classroomString = classroomEditText.getText().toString();
        String schoolSubjectString = schoolSubjectEditText.getText().toString();

        Intent myIntent = new Intent(ChoiceSchoolSubject.this, addScheduleActivity.class);
        myIntent.putExtra("classroom", classroomString); //Optional parameters
        myIntent.putExtra("schoolSubject", schoolSubjectString);
        ChoiceSchoolSubject.this.startActivity(myIntent);
    }
}
