package com.walkap.x_android;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class OptionsActivity extends BaseActivity{

    private DatabaseReference mDatabase;

    static private String FILENAME = "data";

    private String universityName;
    private String facultyName;

    private String MY_PREFS_NAME = "preferences";

    private EditText university;
    private EditText faculty;

    private static final String TAG = "OptionsActivity";

    private final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        FirebaseAuth mFirebaseAuth;

        mDatabase =  FirebaseDatabase.getInstance().getReference();
        mFirebaseAuth = FirebaseAuth.getInstance();

        readDataFile();

        university = (EditText) findViewById(R.id.universityEditText);
        faculty = (EditText) findViewById(R.id.facultyEditText);

        university.setText(universityName);
        faculty.setText(facultyName);

        //It has to be applied here to make the drawer works

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //It has to be applied here to make the drawer works

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
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

}
