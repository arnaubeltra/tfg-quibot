package edu.upc.arnaubeltra.tfgquibot.ui.login;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.format.Formatter;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import edu.upc.arnaubeltra.tfgquibot.AdminNavigation;
import edu.upc.arnaubeltra.tfgquibot.R;
import edu.upc.arnaubeltra.tfgquibot.socket.SocketConnection;

public class AdminLogin extends AppCompatActivity {

    private EditText emailUser, passwordUser;

    private String emailPattern = "[a-zA-Z0-9._-]+@+[a-z]+\\.+[a-z]+";

    private SocketConnection socketConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        emailUser = findViewById(R.id.inputTxtEmail);
        passwordUser = findViewById(R.id.inputTxtPassword);

        findViewById(R.id.btnLoginAdmin).setOnClickListener(view -> login());
        findViewById(R.id.textViewGoBackLogin).setOnClickListener(view -> goBack());

        socketConnection = SocketConnection.getInstance();
    }

    private void login() {
        String email = emailUser.getText().toString();
        String password = passwordUser.getText().toString();

        if (!email.matches(emailPattern)) {
            emailUser.setError("Entra un correu vàlid");
        } else if ((password.isEmpty()) || (password.length() < 6)) {
            passwordUser.setError("La longitud de la contrassenya ha de ser major que 6");
        } else {
            Socket socket = socketConnection.socketConnectionServer;
            if (socket != null) {
                JSONObject adminJSONObject = createAdminJSONObject();
                new Thread(() -> {
                    try {
                        PrintWriter newUserLogin = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                        newUserLogin.println(adminJSONObject);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).start();
                
                goToHomeActivityAdmin();
            } else
                Toast.makeText(AdminLogin.this, R.string.txtErrorLoggingIn, Toast.LENGTH_SHORT).show();
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

    private JSONObject createAdminJSONObject() {
        WifiManager wifiManager = (WifiManager) this.getApplicationContext().getSystemService(WIFI_SERVICE);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("address", Formatter.formatIpAddress(wifiManager.getConnectionInfo().getIpAddress()));
            jsonObject.put("type", "admin");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
}