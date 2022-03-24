package com.example.tfgquibotapp.ui.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.example.tfgquibotapp.User;
import com.example.tfgquibotapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Register extends AppCompatActivity {

    private EditText emailRegister, passwordRegister, confirmPasswordRegister;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private ProgressDialog registerDialog;
    private String emailPattern = "[a-zA-Z0-9._-]+@+[a-z]+\\.+[a-z]+";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        emailRegister = findViewById(R.id.inputTxtEmailRegister);
        passwordRegister = findViewById(R.id.inputTxtPasswordRegister);
        confirmPasswordRegister = findViewById(R.id.inputTxtConfirmPasswordRegister);

        findViewById(R.id.btnCreateAccountRegister).setOnClickListener(view -> registerNewUser());
        findViewById(R.id.textViewAlreadyHaveAnAccount).setOnClickListener(view -> finish());

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        registerDialog = new ProgressDialog(this);
    }

    private void registerNewUser() {
        String email = emailRegister.getText().toString();
        String password = passwordRegister.getText().toString();
        String confirmPassword = confirmPasswordRegister.getText().toString();

        if (!email.matches(emailPattern)) {
            emailRegister.setError("Enter correct email");
        } else if ((password.isEmpty()) || (password.length() < 6)) {
            passwordRegister.setError("Password length must be higher than 6");
        } else if (!password.equals(confirmPassword)) {
            confirmPasswordRegister.setError("Passwords do not match");
        } else {
            registerDialog.setMessage("Espera mentre es realitza el procés de registre");
            registerDialog.setTitle(R.string.registering);
            registerDialog.setCanceledOnTouchOutside(false);
            registerDialog.show();

            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        registerDialog.dismiss();
                        goToHomeActivity();
                        Toast.makeText(Register.this, R.string.newUserRegistered, Toast.LENGTH_SHORT).show();
                    } else {
                        registerDialog.dismiss();
                        Toast.makeText(Register.this, R.string.txtErrorRegistering, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void goToHomeActivity() {
        Intent intent = new Intent(Register.this, User.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}