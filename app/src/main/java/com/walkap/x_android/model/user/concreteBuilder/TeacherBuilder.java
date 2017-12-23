package com.walkap.x_android.model.user.concreteBuilder;

public class TeacherBuilder extends UserBuilder{

    @Override
    public void setType(){
        myUser.setType("Teacher");
    }
}