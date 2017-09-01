package com.walkap.x_android.model;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User {
    private String email;
    private String name;
    private String surname;
    private String university;
    private String faculty;
    private String degreeCourse;


    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String email, String name, String surname, String university, String faculty, String degreeCourse) {
        this.email = email;
        this.name = name;
        this.surname = surname;
        this.university = university;
        this.faculty = faculty;
        this.degreeCourse = degreeCourse;
    }

    public String getEmail(){
        return email;
    }

    //Name
    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    //Surname
    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname){
        this.surname = surname;
    }

    //University

    public String getUniversity(){
        return university;
    }

    public void setUniversity(String university){
        this.university = university;
    }

    //Faculty

    public String getFaculty(){
        return faculty;
    }

    public void setFaculty(String faculty){
        this.faculty = faculty;
    }

    //Course Degree

    public String getCourseDegree(){
        return degreeCourse;
    }

    public void setCourseDegree(String courseDegree){
        this.degreeCourse = degreeCourse;
    }
}
