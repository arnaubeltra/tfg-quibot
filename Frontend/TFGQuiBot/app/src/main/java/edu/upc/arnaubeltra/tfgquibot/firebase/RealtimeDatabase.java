package edu.upc.arnaubeltra.tfgquibot.firebase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import edu.upc.arnaubeltra.tfgquibot.models.User;

public class RealtimeDatabase {
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;

    private static RealtimeDatabase instance;

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
                Log.e("RD", "onCancelled: ", error.toException());
            }
        });
        firebaseAuth.getCurrentUser().delete();
    }
}
