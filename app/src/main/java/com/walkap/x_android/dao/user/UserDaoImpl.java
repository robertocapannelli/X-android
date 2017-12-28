package com.walkap.x_android.dao.user;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
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
    public void customQuery(String key, Object value){
        Query query = ref.whereEqualTo(key, value);
        // this could be chained with another where() e.g. whereLessThan whereEqualTo whereGreaterThanOrEqualTo
    }

    @Override
    public void getUser(String userId) {
        User user;
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
    public void addUserIfNotPresent(User user){
        final User checkUser = user;
        Log.d(TAG, "addUserIfNotPresent(): " + user.getEmail());
        ref.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Boolean found = false;
                    for (DocumentSnapshot document : task.getResult()) {
                        User user = document.toObject(User.class);
                        Log.d(TAG, "addUserIfNotPresent(): " + user.getEmail());
                        if(user.getEmail().equals(checkUser.getEmail())){
                            found = true;
                            break;
                        }
                        Log.d(TAG, document.getId() + " => " + document.getData());
                    }
                    if(!found){
                        Log.d(TAG, "addUserIfNotPresent(): The user email doesn't match, so we can add new user!" );
                        addUser(checkUser);
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
        ref.add(user) //add generate a unique id for the document id
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
