package com.walkap.x_android.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public abstract class BaseFragment extends Fragment{
    public DatabaseReference mDatabase;
    public FirebaseAuth mFirebaseAuth;
    public FirebaseUser mFirebaseUser;

    public final String UNIVERSITY = "university";
    public final String UNIVERSITIES = "universities";

    public final String FACULTY = "faculty";
    public final String FACULTIES = "faculties";

    public final String DEGREE_COURSE = "degreeCourse";

    public final String CLASSROOM = "classRoom";
    public final String SCHOOLSUBJECT = "schoolSubject";

    public final String USERS = "users";

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mDatabase =  FirebaseDatabase.getInstance().getReference();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

    }

}
