package com.walkap.x_android;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class Options extends AppCompatActivity{

    static private String FILENAME = "data";

    private String universityName;
    private String facultyName;

    private EditText university;
    private EditText faculty;

    private static final String TAG = "Options";

    private final Context context = this;

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        mDatabase =  FirebaseDatabase.getInstance().getReference();

        readDataFile();

        university = (EditText) findViewById(R.id.universityEditText);
        faculty = (EditText) findViewById(R.id.facultyEditText);

        university.setText(universityName);
        faculty.setText(facultyName);
    }

    public void saveData(View view) throws InterruptedException {

        String universityString = university.getText().toString();
        String facultyString = faculty.getText().toString();

        findUniversity(universityString);
        findFaculty(universityString, facultyString);

        String fileString = universityString + "-" + facultyString + ";";

        try {
            FileOutputStream fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);

            fos.write(fileString.getBytes());
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void findUniversity(final String universityString){

        mDatabase.child("scheduler").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        boolean find = false;
                        for (DataSnapshot noteDataSnapshot : dataSnapshot.getChildren()) {
                            String universityName = noteDataSnapshot.getKey();
                            if (universityName.equals(universityString)) {
                                find = true;
                            }
                        }
                        if(!find){
                            AlertDialog.Builder builder;
                            builder = new AlertDialog.Builder(context);

                            builder.setTitle("university not find")
                                    .setMessage("University not found, do you want to add it?")
                                    .setPositiveButton("add", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            mDatabase.child("scheduler").child(universityString).setValue(-1 + "");
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
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e(TAG, "onCancelled", databaseError.toException());
                    }
                });

    }

    public void findFaculty(final String universityString, final String facultyString){

        mDatabase.child("scheduler").child(universityString)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        boolean find = false;
                        for (DataSnapshot noteDataSnapshot : dataSnapshot.getChildren()) {
                            String facultyName = noteDataSnapshot.getKey();


                            if (facultyName.equals(facultyString)) {
                                find = true;
                            }
                        }
                        if(!find){
                            AlertDialog.Builder builder;
                            builder = new AlertDialog.Builder(context);

                            builder.setTitle("faculty not find")
                                    .setMessage("faculty not found, do you want to add it?")
                                    .setPositiveButton("add", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            mDatabase.child("scheduler").child(universityString).child(facultyString).setValue(-1 + "");
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
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e(TAG, "onCancelled", databaseError.toException());
                    }
                });

    }

    private void readDataFile(){
        try {
            byte[] buffer = new byte[256];
            FileInputStream fis = openFileInput(FILENAME);
            fis.read(buffer);
            fis.close();
            String fileString = new String(buffer);
            int endString = fileString.indexOf(';');
            int midString = fileString.indexOf('-');
            universityName = fileString.substring(0, midString);
            facultyName = fileString.substring(midString + 1, endString);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
