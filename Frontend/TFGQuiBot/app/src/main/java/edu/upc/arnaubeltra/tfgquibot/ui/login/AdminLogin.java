package edu.upc.arnaubeltra.tfgquibot.ui.login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import edu.upc.arnaubeltra.tfgquibot.AdminNavigation;
import edu.upc.arnaubeltra.tfgquibot.R;

public class AdminLogin extends AppCompatActivity {

    private EditText emailUser, passwordUser;
    private LoginViewModel loginViewModel;
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

        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        loginDialog = new ProgressDialog(this);
    }

    private void login() {
        String email = emailUser.getText().toString();
        String password = passwordUser.getText().toString();

        if (!email.matches(emailPattern)) {
            emailUser.setError("Entra un correu vàlid");
        } else if ((password.isEmpty()) || (password.length() < 6)) {
            passwordUser.setError("La longitud de la contrassenya ha de ser major que 6");
        } else if (!email.matches("a@a.com")) {
            Toast.makeText(AdminLogin.this, R.string.txtErrorLoggingIn, Toast.LENGTH_SHORT).show();
        } else {
            loginDialog.setMessage("Espera mentre s'inicia sessió");
            loginDialog.setTitle(R.string.txtLoggingIn);
            loginDialog.setCanceledOnTouchOutside(false);
            loginDialog.show();

            loginViewModel.newAdminLogin();
            loginViewModel.getNewAdminLoginResponse().observe(this, response -> {
                try {
                    JSONObject responseObject = new JSONObject(response);
                    if (responseObject.getString("response").equals("login-admin-success"))
                        goToHomeActivityAdmin();
                    else Toast.makeText(AdminLogin.this, R.string.txtErrorLoggingIn, Toast.LENGTH_SHORT).show();
                    loginDialog.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });
        }
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