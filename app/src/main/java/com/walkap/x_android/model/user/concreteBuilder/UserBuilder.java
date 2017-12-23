package com.walkap.x_android.model.user.concreteBuilder;

import com.walkap.x_android.model.user.User;

public abstract class UserBuilder {

    protected User myUser;

    public void createUser(String email, String name, String type){
        myUser = new User(email, name, type);
    }

    public User getMyUser(){
        return myUser;
    }

    public UserBuilder setUserId(String userId){
        myUser.setUserId(userId);
        return this;
    }

    public UserBuilder setName(String name){
        myUser.setName(name);
        return this;
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

    public abstract void setType();

}