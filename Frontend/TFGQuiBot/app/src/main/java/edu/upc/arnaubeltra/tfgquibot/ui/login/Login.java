package edu.upc.arnaubeltra.tfgquibot.ui.login;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import edu.upc.arnaubeltra.tfgquibot.AdminNavigation;
import edu.upc.arnaubeltra.tfgquibot.R;
import edu.upc.arnaubeltra.tfgquibot.UserNavigation;
import edu.upc.arnaubeltra.tfgquibot.firebase.Authentication;
import edu.upc.arnaubeltra.tfgquibot.firebase.RealtimeDatabase;
import edu.upc.arnaubeltra.tfgquibot.models.User;
import edu.upc.arnaubeltra.tfgquibot.socket.SocketConnection;

public class Login extends AppCompatActivity {

    private EditText nameUser, surnameUser;

    private SocketConnection socketConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        nameUser = findViewById(R.id.inputNameUser);
        surnameUser = findViewById(R.id.inputSurnameUser);

        findViewById(R.id.btnLogin).setOnClickListener(view -> login());
        findViewById(R.id.textViewEnterAsAdmin).setOnClickListener(view -> goToAdminLogin());

        socketConnection = SocketConnection.getInstance();

        socketConnection.onStartConnection();
    }

    private void login() {
        String name = nameUser.getText().toString();
        String surname = surnameUser.getText().toString();

        if (name.isEmpty())
            nameUser.setError("El camp de nom no pot estar buit");
        else if (surname.isEmpty())
            surnameUser.setError("El camp de cognoms no pot estar buit");
        else {
            Socket socket = socketConnection.socketConnectionServer;

            if (socket != null) {
                JSONObject userJSONObject = createUserJSONObject(name, surname);
                new Thread(() -> {
                    try {
                        PrintWriter newUserLogin = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),true);
                        newUserLogin.println(userJSONObject);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).start();
                goToHomeActivityUser();
            } else
                Toast.makeText(Login.this, R.string.txtErrorLoggingIn, Toast.LENGTH_SHORT).show();
        }
    }

    private void goToHomeActivityUser() {
        Log.d("hello", "goToHomeActivity: ");
        Intent intent = new Intent(Login.this, UserNavigation.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void goToAdminLogin() {
        startActivity(new Intent(Login.this, AdminLogin.class));
    }

    private JSONObject createUserJSONObject(String name, String surname) {
        WifiManager wifiManager = (WifiManager) this.getApplicationContext().getSystemService(WIFI_SERVICE);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("ipAddress", Formatter.formatIpAddress(wifiManager.getConnectionInfo().getIpAddress()));
            jsonObject.put("name", name);
            jsonObject.put("surname", surname);
            jsonObject.put("isAuthorized", "false");
            jsonObject.put("userType", "user");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
}