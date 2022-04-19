package edu.upc.arnaubeltra.tfgquibot.ui.login;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import edu.upc.arnaubeltra.tfgquibot.AdminNavigation;
import edu.upc.arnaubeltra.tfgquibot.R;

public class AdminLogin extends AppCompatActivity {

    private EditText emailUser, passwordUser;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    private ProgressDialog loginDialog;

    private String emailPattern = "[a-zA-Z0-9._-]+@+[a-z]+\\.+[a-z]+";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        emailUser = findViewById(R.id.inputTxtEmail);
        passwordUser = findViewById(R.id.inputTxtPassword);

        findViewById(R.id.btnLoginAdmin).setOnClickListener(view -> login());
        findViewById(R.id.textViewGoBackLogin).setOnClickListener(view -> goBack());

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        loginDialog = new ProgressDialog(this);
    }

    private void login() {
        goToHomeActivityAdmin();
        /*String email = emailUser.getText().toString();
        String password = passwordUser.getText().toString();

        if (!email.matches(emailPattern)) {
            emailUser.setError("Entra un correu vàlid");
        } else if ((password.isEmpty()) || (password.length() < 6)) {
            passwordUser.setError("La longitud de la contrassenya ha de ser major que 6");
        } else {
            loginDialog.setMessage("Espera mentre s'inicia sessió");
            loginDialog.setTitle(R.string.txtLoggingIn);
            loginDialog.setCanceledOnTouchOutside(false);
            loginDialog.show();

            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    loginDialog.dismiss();
                    goToHomeActivityAdmin();
                } else {
                    loginDialog.dismiss();
                    Toast.makeText(AdminLogin.this, R.string.txtErrorLoggingIn, Toast.LENGTH_SHORT).show();
                }
            });
        }*/
    }

    private void goToHomeActivityAdmin() {
        Intent intent = new Intent(AdminLogin.this, AdminNavigation.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void goBack() {
        finish();
    }
}