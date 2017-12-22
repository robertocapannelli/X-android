package com.walkap.x_android.dao.user;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.walkap.x_android.model.user.User;

import java.util.Vector;

public class UserDaoImpl implements UserDao {

    private static final String TAG = "UserDaoImpl";

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    public User getUser(int id) {

        User user = null;

        return user;
    }

    @Override
    public Vector<User> getAllUsers() {

        Vector<User> vector = null;

        return vector;

    }

    @Override
    public void addUser(User user) {

        db.collection("users")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });

    }

    @Override
    public void updateUser(User user) {

    }

    @Override
    public void deleteUser(User user) {

    }

    public static void main(String[] args) {

        User user = new User();

    }

}
