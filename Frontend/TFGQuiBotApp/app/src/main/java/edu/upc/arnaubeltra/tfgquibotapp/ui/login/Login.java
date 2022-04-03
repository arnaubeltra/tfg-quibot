package edu.upc.arnaubeltra.tfgquibotapp.ui.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import edu.upc.arnaubeltra.tfgquibotapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import edu.upc.arnaubeltra.tfgquibotapp.Admin;
import edu.upc.arnaubeltra.tfgquibotapp.User;
import edu.upc.arnaubeltra.tfgquibotapp.models.Users;

public class Login extends AppCompatActivity {

    private EditText emailUser, passwordUser;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private ProgressDialog loginDialog;
    private String emailPattern = "[a-zA-Z0-9._-]+@+[a-z]+\\.+[a-z]+";

    private DatabaseReference usersDb;

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

        usersDb = FirebaseDatabase.getInstance().getReference();
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
                    Log.d("hello", "login: " + email);
                    if (email.equals("a@a.com"))
                        goToHomeActivityAdmin();
                    else {
                        notifyNewUserLogin(email);
                        goToHomeActivity();
                    }
                } else {
                    loginDialog.dismiss();
                    Toast.makeText(Login.this, R.string.txtErrorLoggingIn, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void notifyNewUserLogin(String email) {
        Users newUser = new Users("", email);
        usersDb.child("active-users").setValue(newUser);
    }

    private void goToHomeActivity() {
        Log.d("hello", "goToHomeActivity: ");
        Intent intent = new Intent(Login.this, User.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void goToHomeActivityAdmin() {
        Log.d("hello", "goToHomeActivityAdmin: ");
        Intent intent = new Intent(Login.this, Admin.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


}