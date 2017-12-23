package com.walkap.x_android.model.user.concreteBuilder;

import com.walkap.x_android.model.user.User;
import com.walkap.x_android.model.user.UserBuilder;

public class UserDirector {

    private UserBuilder userBuilder;

    public User getUser() {
        return userBuilder.getMyUser();
    }

    public UserBuilder buildUser(String email, String name, String type) {
        switch (type) {
            case "Student":
                userBuilder = new StudentBuilder();
                break;
            case "Teacher":
                userBuilder = new TeacherBuilder();
                break;
        }
        userBuilder.createUser(email, name, type);
        userBuilder.setType();

        return userBuilder;
    }
    /*public static void main(String[] args) {
        UserDirector userDirector = new UserDirector();
        UserBuilder builder = userDirector.buildUser("dsf", "sdf", "Student");
        builder.setUniversity("Sapienza");
        User user = builder.getMyUser();
        System.out.println(user.getEmail() + " " + user.getUniversity());
    }*/
}
