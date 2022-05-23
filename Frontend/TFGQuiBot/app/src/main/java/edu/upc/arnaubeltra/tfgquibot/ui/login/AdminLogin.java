package edu.upc.arnaubeltra.tfgquibot.ui.login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.upc.arnaubeltra.tfgquibot.AdminNavigation;
import edu.upc.arnaubeltra.tfgquibot.R;

public class AdminLogin extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private EditText emailUser, passwordUser;
    private LoginViewModel loginViewModel;
    private ProgressDialog loginDialog;
    private Spinner spinnerSelectRobot;
    private String emailPattern = "[a-zA-Z0-9._-]+@+[a-z]+\\.+[a-z]+";

    private static int robot = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        emailUser = findViewById(R.id.inputTxtEmail);
        passwordUser = findViewById(R.id.inputTxtPassword);

        findViewById(R.id.btnLoginAdmin).setOnClickListener(view -> login());
        findViewById(R.id.textViewGoBackLogin).setOnClickListener(view -> goBack());

        spinnerSelectRobot = findViewById(R.id.spinnerSelectRobotAdmin);

        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        loginDialog = new ProgressDialog(this);

        setupSpinnerSelectRobot();
    }

    public static int getRobotAdmin() {
        return robot;
    }

    private void login() {
        //goToHomeActivityAdmin();
        String email = "a@a.com";// emailUser.getText().toString();
        String password = "111111";// passwordUser.getText().toString();

        if (!email.matches(emailPattern)) {
            emailUser.setError("Entra un correu vàlid");
        } else if ((password.isEmpty()) || (password.length() < 6)) {
            passwordUser.setError("La longitud de la contrassenya ha de ser major que 6");
        } else if ((!email.matches("a@a.com")) || (!password.matches("111111"))) {
            Toast.makeText(AdminLogin.this, R.string.txtErrorLoggingIn, Toast.LENGTH_SHORT).show();
        } else {
            Login.setAdminLogged(true);
            loginDialog.setMessage("Espera mentre s'inicia sessió");
            loginDialog.setTitle(R.string.txtLoggingIn);
            loginDialog.setCanceledOnTouchOutside(false);
            loginDialog.show();

            loginViewModel.newAdminLogin();
            loginViewModel.getNewAdminLoginResponse().observe(this, response -> {
                try {
                    JSONObject responseObject = new JSONObject(response);
                    if (responseObject.getString("response").equals("login-admin-success")) {
                        if (String.valueOf(spinnerSelectRobot.getSelectedItem()).equals(getResources().getString(R.string.txtRobot1D))) {
                            robot = 1;
                            loginViewModel.selectRobot(1);
                        } else if (String.valueOf(spinnerSelectRobot.getSelectedItem()).equals(getResources().getString(R.string.txtRobot2D))) {
                            robot = 2;
                            loginViewModel.selectRobot(2);
                        }
                        goToHomeActivityAdmin();
                    } else if (responseObject.getString("response").equals("another-admin-loged"))
                        Toast.makeText(AdminLogin.this, R.string.txtErrorAnotherAdminLogged, Toast.LENGTH_SHORT).show();
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

    private void setupSpinnerSelectRobot() {
        spinnerSelectRobot.setOnItemSelectedListener(this);

        List<String> robots = new ArrayList<>();
        Collections.addAll(robots, getResources().getString(R.string.txtRobot1D), getResources().getString(R.string.txtRobot2D));

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, R.layout.spinner_layout, robots);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSelectRobot.setAdapter(dataAdapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}