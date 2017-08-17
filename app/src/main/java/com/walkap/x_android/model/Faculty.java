package com.walkap.x_android.model;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Faculty {
    private String mName;

    public Faculty(){
    }

    public Faculty(String name){
        mName = name;
    }

    public String getName(){
        return mName;
    }

    public void setName(String name){
        mName = name;
    }

}
