package com.walkap.x_android;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.walkap.x_android.models.University;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends BaseActivity  {

    ListView listView;
    Context content = this;

    private String FILENAME = "data";
    private String MY_PREFS_NAME = "preferences";

    private int[] positionGridView = new int[] {1, 0, 0, 0, 0, 0};
    GridView gridView;

    private String universityName;
    private String facultyName;

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DatabaseReference mDatabase;
        DatabaseReference universityEndPoint;

        //Playing with Firebase realtime database
        mDatabase =  FirebaseDatabase.getInstance().getReference();
        universityEndPoint = mDatabase.child("university");

        showScheduler(mDatabase);

        final List<University> mUniversity = new ArrayList<>();
        universityEndPoint.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot noteSnapshot: dataSnapshot.getChildren()){
                    University note = noteSnapshot.getValue(University.class);
                    mUniversity.add(note);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, databaseError.getMessage());
            }
        });

        gridView = (GridView) this.findViewById(R.id.mainActivityGridView);
        String[] mainGrid = new String[]{
                "L",   "M",  "M",   "G",  "V",  "S"
        };

        ListAdapter adapterGrid = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, mainGrid);

        gridView.setAdapter(adapterGrid);
        gridView.setOnItemClickListener(GridClickListener);

        listView = (ListView) findViewById(R.id.mainActivityListView);


        //It has to be applied here to make the drawer works we need to
        // find another solution for not repeating code

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //It has to be applied here to make the drawer works we need to
        // find another solution for not repeating code

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    public void showScheduler(DatabaseReference mDatabase){

        readDataFile();

        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        universityName = prefs.getString("university", "");
        facultyName = prefs.getString("faculty", "");

        if(universityName.isEmpty()|| facultyName.isEmpty()){
            Intent myIntent = new Intent(MainActivity.this, OptionsActivity.class);
            MainActivity.this.startActivity(myIntent);
        }
        else {
            mDatabase.child("scheduler").child(universityName).child(facultyName)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            List<Scheduler> list = new ArrayList<Scheduler>();
                            for (DataSnapshot noteDataSnapshot : dataSnapshot.getChildren()) {
                                Scheduler scheduler = noteDataSnapshot.getValue(Scheduler.class);

                                Calendar calendar = Calendar.getInstance();
                                int day = calendar.get(Calendar.DAY_OF_WEEK);

                                if ((day - 2) == scheduler.getTime().getDay()) {
                                    list.add(scheduler);
                                    Log.d("hey", "***************************");
                                    Log.d("hey", "la lezione di " + scheduler.getSchoolSubject() + " nell' aula " + scheduler.getClassroom()
                                            + " inizia alle " + scheduler.getTime().getHour() + ":" + scheduler.getTime().getMinute() + " e dura "
                                            + scheduler.getTime().getDuration() + " minuti.");
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

    AdapterView.OnItemClickListener GridClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> adapter, View view,
                                int position, long id) {

            if(positionGridView[position] == 0) {
                positionGridView = setAllLessOne(position);
                //repaintListView(position);
            }

            colorGridView();

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

}