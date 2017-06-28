package com.walkap.x_android.models;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Faculty {
    public String id;
    public String name;

    public Faculty(){
    }

    public Faculty(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }

}
