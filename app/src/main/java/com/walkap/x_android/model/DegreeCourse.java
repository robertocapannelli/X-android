package com.walkap.x_android.model;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class DegreeCourse {

    private String mName;

    public DegreeCourse(){
    }

    public DegreeCourse(String name){
        mName = name;
    }

    public String getName(){
        return mName;
    }

    public void setName(String name){
        mName = name;
    }
}
