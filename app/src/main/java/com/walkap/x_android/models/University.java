package com.walkap.x_android.models;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class University {
    public String id;
    public String name;
    public String address;
    public String city;

    public Faculty faculty;


    //Constructors
    public University(){
    }

    public University(String name, String address, String city){
        this.name = name;
        this.address = address;
        this.city = city;
    }


    //Getter and setter
    public String getName(String name){
        return name;
    }

    public String getCity(String city){
        return city;
    }

    public String getAddress(String address){
        return address;
    }

}
