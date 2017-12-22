package com.walkap.x_android.model.user;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User {
    //mandatory
    private String email;
    private String name;
    //optional
    private String surname;
    private String university;
    private String faculty;
    private String degreeCourse;

    /**
     * Default constructor
     * @param email - String
     * @param name - String
     */
    public User(String email, String name) {
        this.email = email;
        this.name = name;
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

    public String getDegreeCourse(){
        return degreeCourse;
    }

    public void setDegreeCourse(String degreeCourse){
        this.degreeCourse = degreeCourse;
    }
}
