package edu.upc.arnaubeltra.tfgquibot.ui.login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.format.Formatter;
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

import edu.upc.arnaubeltra.tfgquibot.R;
import edu.upc.arnaubeltra.tfgquibot.UserNavigationRobot1d;
import edu.upc.arnaubeltra.tfgquibot.UserNavigationRobot2d;

// Class that defines the Login activity
public class Login extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    public static Login instance;
    private EditText nameUser, surnameUser;
    private LoginViewModel loginViewModel;
    private ProgressDialog loginDialog;
    private Spinner spinnerSelectRobot;
    private static String ipAddress;
    public static boolean adminLogged;

    private static int robot = 0;

    private int flag = 0;

    // Method to create the activity. Defines layout and calls methods when needed.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        instance = this;

        nameUser = findViewById(R.id.inputNameUser);
        surnameUser = findViewById(R.id.inputSurnameUser);

        findViewById(R.id.btnLogin).setOnClickListener(view -> login());
        findViewById(R.id.textViewEnterAsAdmin).setOnClickListener(view -> goToAdminLogin());
        findViewById(R.id.imgHelpLogin).setOnClickListener(view -> openHelpDialog());

        spinnerSelectRobot = findViewById(R.id.spinnerSelectRobot);

        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        loginDialog = new ProgressDialog(this);

        WifiManager wifiManager = (WifiManager) this.getApplicationContext().getSystemService(WIFI_SERVICE);
        ipAddress = Formatter.formatIpAddress(wifiManager.getConnectionInfo().getIpAddress());

        NetworkRequest networkRequest = new NetworkRequest.Builder()
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .build();

        setupSpinnerSelectRobot();

        if (!checkWifiConnection())
            dialogWifiConnection();
    }

    // Gets the instance of the Login class.
    public static Context getInstance() {
        return instance.getApplicationContext();
    }

    // Gets the context of the Login class.
    public static Login getContext() {
        return instance;
    }

    // Gets the IP address of the user.
    public static String getIpAddress() {
        return ipAddress;
    }

    // Gets the robot that the user has selected to login.
    public static int getRobotUser() {
        return robot;
    }

    // Performs a login for the normal user. Checks the fields and then performs a log in.
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
                    if (flag == 0) {
                        if (responseObject.getString("response").equals("login-user-success")) {
                            if (String.valueOf(spinnerSelectRobot.getSelectedItem()).equals(getResources().getString(R.string.txtRobot1D)))
                                robot = 1;
                            else if (String.valueOf(spinnerSelectRobot.getSelectedItem()).equals(getResources().getString(R.string.txtRobot2D)))
                                robot = 2;
                            goToHomeActivityUser();
                        } else
                            Toast.makeText(Login.this, R.string.txtErrorLoggingIn, Toast.LENGTH_SHORT).show();
                        loginDialog.dismiss();
                    } flag++;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    // Intent to the home user. Changes depending on the robot selected.
    private void goToHomeActivityUser() {
        if (robot == 1) {
            Intent intent = new Intent(Login.this, UserNavigationRobot1d.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        } else if (robot == 2) {
            Intent intent = new Intent(Login.this, UserNavigationRobot2d.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        }   //Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK
    }

    // Intent to the admin login.
    private void goToAdminLogin() {
        startActivity(new Intent(Login.this, AdminLogin.class));
    }

    // Returns whether admin is logged.
    public static boolean getAdminLogged() {
        return adminLogged;
    }

    // Sets if the admin is logged.
    public static void setAdminLogged(Boolean status) {
        adminLogged = status;
    }

    // Method to show a spinner that allows to select the robot.
    private void setupSpinnerSelectRobot() {
        spinnerSelectRobot.setOnItemSelectedListener(this);

        List<String> robots = new ArrayList<>();
        Collections.addAll(robots, getResources().getString(R.string.txtRobot1D), getResources().getString(R.string.txtRobot2D));

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, R.layout.spinner_layout, robots);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSelectRobot.setAdapter(dataAdapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) { }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) { }

    // Checks wifi connection to know if the user is connected to the QuiBot network.
    private boolean checkWifiConnection() {
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected();
    }

    // Shows a dialog if wifi is not connected.
    private void dialogWifiConnection() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.txtWifiConnectionAlertTitle)
                .setMessage(R.string.txtWifiConnectionAlert)
                .setPositiveButton(R.string.txtAccept, null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // Opens a help dialog with information on how to log in.
    private void openHelpDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.txtTitleHelpLogin)
                .setMessage(R.string.txtHelpLogin)
                .setPositiveButton(R.string.txtAccept, null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}