package com.walkap.x_android.model;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class University {

    private String mName;
    private String mAddress;
    private String mCity;

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

    public void setmCity(String city){
        mCity = city;
    }


    public String getAddress(){
        return mAddress;
    }

    public void setmAddress(String address){
        mAddress = address;
    }

}
