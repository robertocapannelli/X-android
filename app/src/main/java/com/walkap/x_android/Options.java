package com.walkap.x_android;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.walkap.x_android.MainActivity.ANONYMOUS;

public class Options extends AppCompatActivity{

    static private String FILENAME = "data";

    private String universityName;
    private String facultyName;

    private String MY_PREFS_NAME = "preferences";

    private EditText university;
    private EditText faculty;

    private static final String TAG = "Options";

    private final Context context = this;

    private DatabaseReference mDatabase;

    private FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        mDatabase =  FirebaseDatabase.getInstance().getReference();
        mFirebaseAuth = FirebaseAuth.getInstance();

        readDataFile();

        university = (EditText) findViewById(R.id.universityEditText);
        faculty = (EditText) findViewById(R.id.facultyEditText);

        university.setText(universityName);
        faculty.setText(facultyName);
    }

    public void saveData(View view) throws InterruptedException {

        String universityString = university.getText().toString();
        String facultyString = faculty.getText().toString();

        if(universityString.isEmpty()){

            AlertDialog.Builder builder;
            builder = new AlertDialog.Builder(context);

            builder.setTitle("university error")
                    .setMessage("university is empty")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
        else{
            if(facultyString.isEmpty()){

                AlertDialog.Builder builder;
                builder = new AlertDialog.Builder(context);

                builder.setTitle("faculty error")
                        .setMessage("faculty is empty")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

            }
            else{

                findUniversity(universityString);
                findFaculty(universityString, facultyString);

                SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                editor.putString("university", universityString);
                editor.putString("faculty", facultyString);
                editor.apply();

                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);

            }
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
        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        universityName = prefs.getString("university", "");
        facultyName = prefs.getString("faculty", "");
    }

    //Add menu options
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sign_out_menu:
                mFirebaseAuth.signOut();
                startActivity(new Intent(this, SignInActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

}
