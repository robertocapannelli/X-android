package com.walkap.x_android.model;

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
