package edu.upc.arnaubeltra.tfgquibot.firebase;

import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import edu.upc.arnaubeltra.tfgquibot.models.User;

public class RealtimeDatabase {
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;

    private static RealtimeDatabase instance;

    private ArrayList<User> usersList;
    private MutableLiveData<ArrayList<User>> usersListLiveData;

    public RealtimeDatabase() {
        this.firebaseDatabase = FirebaseDatabase.getInstance();
        this.firebaseAuth = FirebaseAuth.getInstance();
    }

    public static RealtimeDatabase getInstance() {
        if (instance == null) instance = new RealtimeDatabase();
        return instance;
    }

    public void newUserLogged(User user) {
        DatabaseReference databaseReference = firebaseDatabase.getReference("users");
        databaseReference.child(user.getUid()).setValue(user);
    }

    public void deleteUserLoggedOut(String uid) {
        DatabaseReference databaseReference = firebaseDatabase.getReference("users");
        Query query = databaseReference.equalTo(uid);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                snapshot.getRef().removeValue();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("TAG", "onCancelled: ", error.toException());
            }
        });
        firebaseAuth.getCurrentUser().delete();
    }

    public LiveData<ArrayList<User>> getLoggedInUsers() {
        if (usersListLiveData == null)
            usersListLiveData = new MutableLiveData<ArrayList<User>>();
        return usersListLiveData;
    }

    public void setLoggedInUsers() {
        DatabaseReference databaseReference = firebaseDatabase.getReference("users");
        ValueEventListener newLoggedInUserListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("TAG", "onDataChange: ha canviat");
                usersList = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);
                    usersList.add(user);
                }
                usersListLiveData.setValue(usersList);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("TAG", "onCancelled: ", databaseError.toException());
            }
        };
        databaseReference.addListenerForSingleValueEvent(newLoggedInUserListener);
    }

    public void updateAuthorizationUser(String uid, boolean status) {
        DatabaseReference databaseReference = firebaseDatabase.getReference("users");
        databaseReference.child(uid).child("authorized").setValue(status);
    }
}
