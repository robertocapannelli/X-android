package com.walkap.x_android.model.user;

public abstract class UserBuilder {

    private User myUser;

    public void createUser(String email, String name){
        myUser = new User(email, name);
    }

    public User getMyUser(){
        return myUser;
    }

    public UserBuilder setSurname(String surname){
        myUser.setSurname(surname);
        return this;
    }

    public UserBuilder setUniversity(String university){
        myUser.setUniversity(university);
        return this;
    }

    public UserBuilder setFaculty(String faculty){
        myUser.setFaculty(faculty);
        return this;
    }

    public UserBuilder setDegreeCourse(String degreeCourse){
        myUser.setDegreeCourse(degreeCourse);
        return this;
    }
}