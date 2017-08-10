package com.walkap.x_android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Random;

public class addScheduleActivity extends BaseActivity {

    private DatabaseReference mDatabase;

    static private int numColumn = 6;
    static private int numRow = 20;

    static private int startHour = 8;

    static private String FILENAME = "data";

    private String classroom;
    private String schoolSubject;
    private String universityName;
    private String facultyName;

    private String MY_PREFS_NAME = "preferences";

    private int[] positionGridView = new int[] {1, 0, 0, 0, 0, 0};

    private int[][] positionListView = new int[][] {{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}};

    private GridView gridView;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_scheduler);

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
            classroom =(String) bundle.get("classroom");
            schoolSubject = (String) bundle.get("schoolSubject");
        }

        readDataFile();

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

    private void writeNewScheduler(int id, String classroom,
                                   String schoolSubject, TimeSchoolSubject time) {        //errore: trovare id univoco!

        Scheduler scheduler = new Scheduler(classroom, schoolSubject, time);

        mDatabase.child("scheduler").child(universityName).child(facultyName).child(id + "").setValue(scheduler);
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
        int min = 0;
        int max = 100;

        Random random;
        int intRandom;

        count = 0;

        for(i = 0; i < numColumn; i++){
            for(j = 0; j < numRow; j++) {
                if (positionListView[i][j] == 1){
                    count ++;
                }

                if(positionListView[i][j] == 0 && count !=0){
                    random = new Random();
                    intRandom = random.nextInt(max - min + 1) + min;
                    TimeSchoolSubject time = new TimeSchoolSubject(i, startHour + (j - count) / 4 , ((j - count) % 4) * 15, (count - 1) * 15);
                    writeNewScheduler(intRandom,classroom, schoolSubject, time);
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

            colorGridView();

        }

    };

    AdapterView.OnItemClickListener ListClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> adapter, View view,
                                int position, long id) {

            int selected = getGridViewSelected();

            if (positionListView[selected][position] == 0) {
                positionListView[selected][position] = 1;
                view.setBackgroundColor(Color.MAGENTA);
            } else {
                positionListView[selected][position] = 0;
                view.setBackgroundColor(Color.WHITE);
            }
        }

    };

    private void readDataFile(){
        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        universityName = prefs.getString("university", "");
        facultyName = prefs.getString("faculty", "");
    }



}
