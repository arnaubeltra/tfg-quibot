package com.example.tfgquibotapp.ui.login;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.example.tfgquibotapp.Home;
import com.example.tfgquibotapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {

    private EditText emailUser, passwordUser;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private ProgressDialog loginDialog;
    private String emailPattern = "[a-zA-Z0-9._-]+@+[a-z]+\\.+[a-z]+";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailUser = findViewById(R.id.inputTxtEmail);
        passwordUser = findViewById(R.id.inputTxtPassword);

        findViewById(R.id.btnLogin).setOnClickListener(view -> login());
        findViewById(R.id.textViewCreateNewAccount).setOnClickListener(view -> startActivity(new Intent(Login.this, Register.class)));

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        loginDialog = new ProgressDialog(this);
    }

    private void login() {
        String email = emailUser.getText().toString();
        String password = passwordUser.getText().toString();

        if (!email.matches(emailPattern)) {
            emailUser.setError("Enter correct email");
        } else if ((password.isEmpty()) || (password.length() < 6)) {
            passwordUser.setError("Password length must be higher than 6");
        } else {
            loginDialog.setMessage("Espera mentre s'inicia sessió");
            loginDialog.setTitle(R.string.txtLoggingIn);
            loginDialog.setCanceledOnTouchOutside(false);
            loginDialog.show();

            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    loginDialog.dismiss();
                    goToHomeActivity();
                } else {
                    loginDialog.dismiss();
                    Toast.makeText(Login.this, R.string.txtErrorLoggingIn, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void goToHomeActivity() {
        Intent intent = new Intent(Login.this, Home.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}