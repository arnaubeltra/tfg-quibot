package edu.upc.arnaubeltra.tfgquibot.firebase;

import com.google.firebase.auth.FirebaseAuth;

public class Authentication {
    private FirebaseAuth firebaseAuth;
    private static Authentication instance;

    public Authentication() {
        this.firebaseAuth = FirebaseAuth.getInstance();
    }

    public static Authentication getInstance() {
        if (instance == null) instance = new Authentication();
        return instance;
    }

    public String getUser() {
        return firebaseAuth.getCurrentUser().getUid();
    }
}
