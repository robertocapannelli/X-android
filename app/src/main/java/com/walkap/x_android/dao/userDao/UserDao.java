package com.walkap.x_android.dao.userDao;

import com.walkap.x_android.model.user.User;

public interface UserDao {

    public void getUser(String userId);

    public void getAllUsers();

    public void addUser(User user);

    public void updateUser(String userId, String key, Object value );

    public void deleteUser(User user);

    public void customQuery(String key, Object value);

    public void addUserIfNotPresent(User user);

}
