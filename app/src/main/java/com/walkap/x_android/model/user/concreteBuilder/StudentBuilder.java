package com.walkap.x_android.model.user.concreteBuilder;

import com.walkap.x_android.model.user.UserBuilder;

public class StudentBuilder extends UserBuilder {

    @Override
    public void setType(){
        myUser.setType("Student");
    }
}
