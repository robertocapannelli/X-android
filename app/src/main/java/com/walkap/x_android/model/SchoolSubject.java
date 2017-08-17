package com.walkap.x_android.model;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class SchoolSubject {

    private String mName;

    public SchoolSubject(){

    }

    public SchoolSubject(String name){
        mName = name;
    }

    public void setName(String name){
        mName = name;
    }

    public String getName(){
        return mName;
    }


}
