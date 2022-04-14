package edu.upc.arnaubeltra.tfgquibot.ui.login;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import edu.upc.arnaubeltra.tfgquibot.AdminNavigation;
import edu.upc.arnaubeltra.tfgquibot.R;
import edu.upc.arnaubeltra.tfgquibot.UserNavigation;
import edu.upc.arnaubeltra.tfgquibot.firebase.Authentication;
import edu.upc.arnaubeltra.tfgquibot.firebase.RealtimeDatabase;
import edu.upc.arnaubeltra.tfgquibot.models.User;

public class Login extends AppCompatActivity {

    private EditText nameUser, surnameUser;

    private FirebaseAuth firebaseAuth;
    private Authentication authentication;
    private ProgressDialog loginDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        nameUser = findViewById(R.id.inputNameUser);
        surnameUser = findViewById(R.id.inputSurnameUser);

        findViewById(R.id.btnLogin).setOnClickListener(view -> login());
        findViewById(R.id.textViewEnterAsAdmin).setOnClickListener(view -> goToAdminLogin());

        firebaseAuth = FirebaseAuth.getInstance();
        authentication = Authentication.getInstance();

        loginDialog = new ProgressDialog(this);
    }

    private void login() {
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

            firebaseAuth.signInAnonymously().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    loginDialog.dismiss();
                    notifyNewUserLogin(authentication.getUser(), name, surname);
                    goToHomeActivityUser();
                } else {
                    loginDialog.dismiss();
                    Toast.makeText(Login.this, R.string.txtErrorLoggingIn, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void notifyNewUserLogin(String uid, String name, String surname) {
        /*String uidHash = "";
        try {
            uidHash = createHash(name, surname);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }*/
        User userLogged = new User(uid, name, surname, false);
        RealtimeDatabase.getInstance().newUserLogged(userLogged);
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

    /*private String createHash(String name, String surname) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        messageDigest.reset();
        messageDigest.update((name + " " + surname).getBytes());
        byte[] digest = messageDigest.digest();

        BigInteger bigInteger = new BigInteger(1, digest);
        String hashCode = bigInteger.toString(16);

        while(hashCode.length() < 32 ){
            hashCode = "0" + hashCode;
        }

        return hashCode;
    }*/
}