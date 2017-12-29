package com.walkap.x_android.dao.userDao;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.walkap.x_android.model.user.User;

public class UserDaoImpl extends AppCompatActivity implements UserDao {

    private static final String TAG = "UserDaoImpl";
    private static final String COLLECTION = "users";

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference ref = db.collection(COLLECTION);

    @Override
    public void customQuery(String key, Object value) {
        Query query = ref.whereEqualTo(key, value);
        // this could be chained with another where() e.g. whereLessThan whereEqualTo whereGreaterThanOrEqualTo
    }

    @Override
    public void getUser(String userId) {
        ref.document(userId)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
            }
        });
    }

    @Override
    public void getAllUsers() {

        ref.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        Log.d(TAG, document.getId() + " => " + document.getData());
                    }
                } else {
                    Log.w(TAG, "Error getting documents.", task.getException());
                }
            }
        });
    }

    @Override
    public void addUserIfNotPresent(User user) {
        final User newUser = user;
        Log.d(TAG, "addUserIfNotPresent(): " + user.getEmail());
        ref.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Boolean found = false;
                    for (DocumentSnapshot document : task.getResult()) {
                        User currentUser = document.toObject(User.class);
                        if (currentUser.getEmail().equals(newUser.getEmail())) {
                            Log.d(TAG, "addUserIfNotPresent(): The user already exists!");
                            found = true;
                            break;
                        }
                        Log.d(TAG, document.getId() + " => " + document.getData());
                    }
                    if (!found) {
                        Log.d(TAG, "addUserIfNotPresent(): The user email " + newUser.getEmail() + " doesn't match, so we can add new user!");
                        addUser(newUser);
                    }

                } else {
                    Log.w(TAG, "Error getting documents.", task.getException());
                }
            }
        });
    }

    @Override
    public void addUser(User user) {
        // Add a new document with a generated ID
        ref.document(user.getUserId()).set(user);
    }

    @Override
    public void updateUser(String userId, String key, Object value) {
        ref.document(userId)
                .update(key, value)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                    }
                });
    }

    @Override
    public void deleteUser(User user) {
        ref.document(user.getUserId())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document", e);
                    }
                });
    }
}