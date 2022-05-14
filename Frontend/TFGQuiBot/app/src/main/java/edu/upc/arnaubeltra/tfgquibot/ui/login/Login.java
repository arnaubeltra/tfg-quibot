package edu.upc.arnaubeltra.tfgquibot.ui.login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import edu.upc.arnaubeltra.tfgquibot.R;
import edu.upc.arnaubeltra.tfgquibot.UserNavigation;

public class Login extends AppCompatActivity {

    public static Login instance;
    private EditText nameUser, surnameUser;
    private LoginViewModel loginViewModel;
    private ProgressDialog loginDialog;
    private static String ipAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        instance = this;

        nameUser = findViewById(R.id.inputNameUser);
        surnameUser = findViewById(R.id.inputSurnameUser);

        findViewById(R.id.btnLogin).setOnClickListener(view -> login());
        findViewById(R.id.textViewEnterAsAdmin).setOnClickListener(view -> goToAdminLogin());

        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        loginDialog = new ProgressDialog(this);

        WifiManager wifiManager = (WifiManager) this.getApplicationContext().getSystemService(WIFI_SERVICE);
        ipAddress = Formatter.formatIpAddress(wifiManager.getConnectionInfo().getIpAddress());
    }

    public static Context getInstance() {
        return instance.getApplicationContext();
    }

    public static Login getContext() {
        return instance;
    }

    public static String getIpAddress() {
        return ipAddress;
    }

    private void login() {
        //goToHomeActivityUser();
        String name = nameUser.getText().toString();
        String surname = surnameUser.getText().toString();

        if (name.isEmpty())
            nameUser.setError("El camp de nom no pot estar buit");
        else if (surname.isEmpty())
            surnameUser.setError("El camp de cognoms no pot estar buit");
        else {
            loginDialog.setMessage("Espera mentre s'inicia sessió");
            loginDialog.setTitle(R.string.txtLoggingIn);
            loginDialog.setCanceledOnTouchOutside(false);
            loginDialog.show();

            loginViewModel.newUserLogin(ipAddress, name, surname, "false");
            loginViewModel.getNewUserLoginResponse().observe(this, response -> {
                try {
                    JSONObject responseObject = new JSONObject(response);
                    if (responseObject.getString("response").equals("login-user-success"))
                        goToHomeActivityUser();
                    else Toast.makeText(Login.this, R.string.txtErrorLoggingIn, Toast.LENGTH_SHORT).show();
                    loginDialog.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    private void goToHomeActivityUser() {
        Intent intent = new Intent(Login.this, UserNavigation.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void goToAdminLogin() {
        startActivity(new Intent(Login.this, AdminLogin.class));
    }

    public static boolean adminLogged;

    public static boolean getAdminLogged() {
        Log.d("TAG", "getAdminLogged: " + adminLogged);
        return adminLogged;
    }

    public static void setAdminLogged(Boolean status) {
        adminLogged = status;
        Log.d("TAG", "setAdminLogged: " + adminLogged);
    }
}