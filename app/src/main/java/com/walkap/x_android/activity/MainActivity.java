package com.walkap.x_android.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.walkap.x_android.R;
import com.walkap.x_android.fragment.ChoiceSchoolSubjectFragment;
import com.walkap.x_android.fragment.HomeFragment;
import com.walkap.x_android.fragment.OptionsFragment;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        NavigationView.OnNavigationItemSelectedListener,
        ChoiceSchoolSubjectFragment.OnFragmentInteractionListener,
        OptionsFragment.OnFragmentInteractionListener,
        HomeFragment.OnFragmentInteractionListener{

    private String mUsername;

    private static final String TAG = "MainActivity";
    public static final String ANONYMOUS = "anonymous";


    //Firebase instance variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mDatabase;

    //Google Api Sign in
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Set a toolbar to replace action bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Find our drawer view
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        //Enable hamburger toggle button
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //Find navigation drawer
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        //Navigation listener
        navigationView.setNavigationItemSelectedListener(this);
        //Header View (aat the top of drawer layout)
        View header = navigationView.getHeaderView(0);

        // Set default username is anonymous.
        mUsername = ANONYMOUS;
        //Playing with Firebase realtime database
        mDatabase =  FirebaseDatabase.getInstance().getReference();
        //universityEndPoint = mDatabase.child("university");
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        TextView tvUserName = (TextView) header.findViewById(R.id.tvUserName);
        TextView tvEmail = (TextView) header.findViewById(R.id.tvEmail);

        //Check if the user is logged in
        if (mFirebaseUser == null) {
            // Not signed in, launch the Sign In activity
            startActivity(new Intent(this, SignInActivity.class));
            finish();
            return;
        }else{
            // Name, email address, and profile photo Url
            String name = mFirebaseUser.getDisplayName();
            String email = mFirebaseUser.getEmail();
            Uri photoUrl = mFirebaseUser.getPhotoUrl();

            // Check if user's email is verified
            boolean emailVerified = mFirebaseUser.isEmailVerified();

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getToken() instead.
            String uid = mFirebaseUser.getUid();
            tvUserName.setText(name);
            tvEmail.setText(email);
        }

        //Google sign connection
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();


        Handler mHandler = new Handler();

        if(savedInstanceState == null){
            Runnable mPendingRunnable = new Runnable() {
                @Override
                public void run() {
                    Fragment fragment = new HomeFragment();
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.flContent, fragment);
                    fragmentTransaction.commitAllowingStateLoss();
                }
            };
            // If mPendingRunnable is not null, then add to the message queue
            if (mPendingRunnable != null) {
                mHandler.post(mPendingRunnable);
            }
        }

    }

    //make the sign out button works
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sign_out_menu:
                mFirebaseAuth.signOut();
                Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                startActivity(new Intent(this, SignInActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void saveData(View view ){
        Log.d(TAG, "clicked the save button in main activity");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    public boolean onNavigationItemSelected(MenuItem item) {

        Fragment fragment = null;
        Class fragmentClass;
        switch(item.getItemId()) {
            case R.id.add_schedule:
                fragmentClass = ChoiceSchoolSubjectFragment.class;
                break;
            case R.id.options:
                fragmentClass = OptionsFragment.class;
                break;
            default:
                fragmentClass = HomeFragment.class;
                break;
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).addToBackStack(TAG).commit();

        // Highlight the selected item has been done by NavigationView
        item.setChecked(true);
        // Set action bar title
        setTitle(item.getTitle());

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri){
        //Need this to make the fragment work
    }
}