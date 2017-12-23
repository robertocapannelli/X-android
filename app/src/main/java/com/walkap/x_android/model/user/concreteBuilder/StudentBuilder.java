package com.walkap.x_android.model.user.concreteBuilder;

public class StudentBuilder extends UserBuilder {

    @Override
    public void setType(){
        myUser.setType("Student");
    }
}
