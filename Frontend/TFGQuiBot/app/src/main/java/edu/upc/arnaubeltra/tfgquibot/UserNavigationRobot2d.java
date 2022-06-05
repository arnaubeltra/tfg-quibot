package edu.upc.arnaubeltra.tfgquibot;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.MenuItem;
import android.view.Menu;
import android.widget.Button;

import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import edu.upc.arnaubeltra.tfgquibot.databinding.ActivityUserNavigationRobot2dBinding;
import edu.upc.arnaubeltra.tfgquibot.databinding.ActivityUserNavigationRobot2dBinding;
import edu.upc.arnaubeltra.tfgquibot.ui.login.Login;
import edu.upc.arnaubeltra.tfgquibot.ui.shared.viewModels.NavigationViewModel;

public class UserNavigationRobot2d extends AppCompatActivity {

    public static UserNavigationRobot2d instance;

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityUserNavigationRobot2dBinding binding;

    private NavigationViewModel navigationViewModel;

    private Button btnExperiments;
    public static NavController navController;

    private int flag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        instance = this;

        binding = ActivityUserNavigationRobot2dBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarUserNavigation.toolbar);

        //btnExperiments = findViewById(R.id.btnExperimentsHomeUser);

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.homeUser)
                .setOpenableLayout(drawer)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_user_navigation);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        /*btnExperiments.setOnClickListener(view -> {
            Log.d("TAG", "pressed");
            navController.navigate(R.id.experiments);
        });*/

        navigationViewModel = new ViewModelProvider(this).get(NavigationViewModel.class);
    }

    public static Context getContext() {
        return instance.getApplicationContext();
    }

    public static UserNavigationRobot2d getInstance() {
        return instance;
    }

    public static NavController getNavController() {
        return navController;
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
                flag = 1;
                navigationViewModel.logoutUser(Formatter.formatIpAddress(wifiManager.getConnectionInfo().getIpAddress()));
                Intent intent = new Intent(UserNavigationRobot2d.this, Login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP );
                startActivity(intent);
                finish();
                /*navigationViewModel.logoutUser(Formatter.formatIpAddress(wifiManager.getConnectionInfo().getIpAddress()));
                navigationViewModel.getLogoutUserResponse().observe(this, response -> {
                    try {
                        JSONObject responseObject = new JSONObject(response);
                        if (responseObject.getString("response").equals("logout-user-success"))
                            finish();
                        goToLoginActivity();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                });*/
        }
        return super.onOptionsItemSelected(item);
    }

    private void goToLoginActivity() {
        Intent intent = new Intent(UserNavigationRobot2d.this, Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (flag == 0) {
            WifiManager wifiManager = (WifiManager) this.getApplicationContext().getSystemService(WIFI_SERVICE);
            navigationViewModel.logoutUser(Formatter.formatIpAddress(wifiManager.getConnectionInfo().getIpAddress()));
        }
    }
}