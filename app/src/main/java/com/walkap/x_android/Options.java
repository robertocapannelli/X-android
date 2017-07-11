package com.walkap.x_android;

import android.content.Context;
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
            //Log.d("hey", "ho scritto il file");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        /*try {
            FileInputStream fis = openFileInput(FILENAME);
            fis.read(buffer);
            fis.close();
            //fileString = new String(buffer);
            //int endString = fileString.indexOf(';');
            //Log.d("hey", fileString.substring(0, endString));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/

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
                            Log.d("hey", "***************************");
                            Log.d("hey", "universita' non trovata vuoi aggiungerla?");
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
                            Log.d("hey", "***************************");
                            Log.d("hey", "facolta' non trovata nella tua universita' vuoi aggiungerla?");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e(TAG, "onCancelled", databaseError.toException());
                    }
                });

    }

}
