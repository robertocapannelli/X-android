package com.walkap.x_android.dao.user;

import com.walkap.x_android.model.user.User;

import java.util.Vector;

public interface UserDao {

    public User getUser(int id);

    public Vector<User> getAllUsers();

    public void addUser(User user);

    public void updateUser(User user);

    public void deleteUser(User user);

}
