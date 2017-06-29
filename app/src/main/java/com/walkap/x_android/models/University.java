package com.walkap.x_android.models;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class University {
    public String mId;
    public String mName;
    public String mAddress;
    public String mCity;

    public Faculty faculty;


    //Constructors
    public University(){
    }

    public University(String name, String address, String city){
        this.mName = name;
        this.mAddress = address;
        this.mCity = city;
    }


    //Getter and setter
    public String getName(){
        return mName;
    }

    public void setName(String name){
        mName = name;
    }

    public String getCity(){
        return mCity;
    }

    public String getAddress(){
        return mAddress;
    }

}
