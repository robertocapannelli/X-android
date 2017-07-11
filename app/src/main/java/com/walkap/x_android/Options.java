package com.walkap.x_android;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import java.util.Calendar;

import static android.system.Os.read;

public class Options extends AppCompatActivity {

    static private String FILENAME = "data";

    private static final String TAG = "Options";

    final Context context = this;

    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        mDatabase =  FirebaseDatabase.getInstance().getReference();
    }

    public void saveData(View view){

        //byte[] buffer = new byte[256];

        EditText university = (EditText) findViewById(R.id.universityEditText);
        EditText faculty = (EditText) findViewById(R.id.facultyEditText);

        String universityString = university.getText().toString();
        String facultyString = faculty.getText().toString();

        findUniversity(mDatabase, universityString);
        findFaculty(mDatabase, universityString, facultyString);

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

    public void findUniversity(DatabaseReference mDatabase, final String universityString){

        mDatabase.child("scheduler")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        boolean find = false;
                        for (DataSnapshot noteDataSnapshot : dataSnapshot.getChildren()) {
                            String universityName = noteDataSnapshot.getKey();


                            if (universityName == universityString) {
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
                                            // continue with add university
                                        }
                                    })
                                    .setNegativeButton("select another university", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // back to options
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

    public void findFaculty(DatabaseReference mDatabase, final String universityString, final String facultyString){

        mDatabase.child("scheduler").child(universityString)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        boolean find = false;
                        for (DataSnapshot noteDataSnapshot : dataSnapshot.getChildren()) {
                            String facultyName = noteDataSnapshot.getKey();


                            if (facultyName == facultyString) {
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
                                            // continue with add faculty
                                        }
                                    })
                                    .setNegativeButton("select another faculty", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // back to options
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

}
