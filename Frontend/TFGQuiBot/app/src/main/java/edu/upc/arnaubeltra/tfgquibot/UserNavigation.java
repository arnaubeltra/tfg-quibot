package edu.upc.arnaubeltra.tfgquibot;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import edu.upc.arnaubeltra.tfgquibot.databinding.ActivityUserNavigationBinding;
import edu.upc.arnaubeltra.tfgquibot.firebase.Authentication;
import edu.upc.arnaubeltra.tfgquibot.firebase.RealtimeDatabase;
import edu.upc.arnaubeltra.tfgquibot.ui.login.Login;
import edu.upc.arnaubeltra.tfgquibot.ui.login.LoginViewModel;

public class UserNavigation extends AppCompatActivity {

    public static UserNavigation instance;

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityUserNavigationBinding binding;

    private NavigationViewModel navigationViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        instance = this;

        binding = ActivityUserNavigationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarUserNavigation.toolbar);

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.homeUser, R.id.experiments, R.id.interactWithRobot, R.id.ticTacToe, R.id.connect4)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_user_navigation);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        navigationViewModel = new ViewModelProvider(this).get(NavigationViewModel.class);
    }

    public static Context getInstance() {
        return instance.getApplicationContext();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.user_navigation, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_user_navigation);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        WifiManager wifiManager = (WifiManager) this.getApplicationContext().getSystemService(WIFI_SERVICE);
        switch (item.getItemId()) {
            case R.id.logout:
                navigationViewModel.logoutUser(Formatter.formatIpAddress(wifiManager.getConnectionInfo().getIpAddress()));
                navigationViewModel.getLogoutUserResponse().observe(this, response -> {
                    try {
                        JSONObject responseObject = new JSONObject(response);
                        if (responseObject.getString("response").equals("logout-user-success"))
                            goToLoginActivity();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                });
        }
        return super.onOptionsItemSelected(item);
    }

    private void goToLoginActivity() {
        Intent intent = new Intent(UserNavigation.this, Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}