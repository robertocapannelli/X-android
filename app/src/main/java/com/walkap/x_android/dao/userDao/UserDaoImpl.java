package com.walkap.x_android.dao.userDao;

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

    private String university;
    private String faculty;
    private String degreeCourse;
    boolean bool;

    //Firestore instance
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    //Users collections
    private CollectionReference ref = db.collection(COLLECTION);

    /**
     * This method runs a custom query
     * over the users collection
     *
     * @param key   - String
     * @param value - Object
     */
    @Override
    public void customQuery(String key, Object value) {
        Query query = ref.whereEqualTo(key, value);
        // this could be chained with another where() e.g. whereLessThan whereEqualTo whereGreaterThanOrEqualTo
    }

    /**
     * This method get a uses passing
     * a User object as parameter and use the id
     * to find it
     *
     * @param id - String
     */
    @Override
    public void getUser(String id) {
        DocumentReference docRef = ref.document(id);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
                Log.d(TAG, "getUser(): " + user.getUserId());
            }
        });
    }

    /**
     * This method is used to check if the current user
     * has the basic informations to continue to use the
     * application
     *
     * @param id - String
     * @return boolean
     */
    public boolean hasBasicInfo(String id) {
        DocumentReference docRef = db.collection("users").document(id);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
                university = user.getUniversity();
                faculty = user.getFaculty();
                degreeCourse = user.getDegreeCourse();
                bool = false;
                if (university != null && faculty != null && degreeCourse != null) {
                    if (university.equals("") || faculty.isEmpty() || degreeCourse.isEmpty()) {
                        bool = false;
                        Log.d(TAG, "hasBasicInfo(): the current user " + user.getEmail() + " has NOT basic info!");
                    } else {
                        bool = true;
                        Log.d(TAG, "hasBasicInfo(): the current user " + user.getEmail() + " already has basic info!");
                    }
                } else {
                    bool = false;
                    Log.d(TAG, "hasBasicInfo(): the current user " + user.getEmail() + " has NOT basic info!");
                }
                Log.d(TAG, "getUser(): " + user.getUserId());
            }
        });
        return bool;
    }

    /**
     * This method get all users present in the database
     */
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

    /**
     * This method check if an email user corresponds
     * to the email of the current user who wants to login
     * or sign up, if the email is not present then add it to the database
     * otherwise just let him to login
     *
     * @param user - User
     */
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

    /**
     * This method just add a new user to the database
     * setting the id of the document, we set it manually
     * to have consistency between the firestore database
     * and the firebase user id
     *
     * @param user - User
     */
    @Override
    public void addUser(User user) {
        // Add a new document with a generated ID
        ref.document(user.getUserId()).set(user);
    }

    /**
     * This method is used to update a user's document
     * Find the user by his id and then update the update
     * just the key passed as parameter
     *
     * @param userId - String
     * @param key    - String
     * @param value  - Object
     */
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

    /**
     * This method is used to delete en entire document
     * of a user
     *
     * @param user - User
     */
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