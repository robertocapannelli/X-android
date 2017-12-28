package com.walkap.x_android.model.user;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User {
    //mandatory
    private String email;
    private String name;
    private String type;
    //optional
    private String userId;
    private String surname;
    private String university;
    private String faculty;
    private String degreeCourse;

    public User(){}

    /**
     * Default constructor
     * @param email - String
     * @param name - String
     */
    public User(String email, String name, String type) {
        this.email = email;
        this.name = name;
        this.type = type;
    }

    /*public User(String email, String name, String surname, String university, String faculty, String degreeCourse) {
        this.email = email;
        this.name = name;
        this.surname = surname;
        this.university = university;
        this.faculty = faculty;
        this.degreeCourse = degreeCourse;
    }*/

    //TODO add timestamp

    /** Getters **/

    public String getUserId(){
        return userId;
    }

    public String getEmail(){
        return email;
    }

    public String getType(){
        return type;
    }

    public String getName(){
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getUniversity(){
        return university;
    }

    public String getFaculty(){
        return faculty;
    }

    public String getDegreeCourse(){
        return degreeCourse;
    }

    /** Setters **/

    public void setUserId(String userId){
        this.userId = userId;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setSurname(String surname){
        this.surname = surname;
    }

    public void setType(String type){
        this.type = type;
    }

    public void setUniversity(String university){
        this.university = university;
    }

    public void setFaculty(String faculty){
        this.faculty = faculty;
    }

    public void setDegreeCourse(String degreeCourse){
        this.degreeCourse = degreeCourse;
    }


}
