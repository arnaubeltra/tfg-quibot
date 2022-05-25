package edu.upc.arnaubeltra.tfgquibot;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Menu;

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

import edu.upc.arnaubeltra.tfgquibot.databinding.ActivityAdminNavigationBinding;
import edu.upc.arnaubeltra.tfgquibot.ui.login.Login;
import edu.upc.arnaubeltra.tfgquibot.ui.login.LoginViewModel;
import edu.upc.arnaubeltra.tfgquibot.ui.shared.viewModels.NavigationViewModel;

public class AdminNavigation extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityAdminNavigationBinding binding;

    private NavigationViewModel navigationViewModel;
    private LoginViewModel loginViewModel;

    public static NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAdminNavigationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarAdminNavigation.toolbar);

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.homeAdmin)
                .setOpenableLayout(drawer)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_admin_navigation);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        navigationViewModel = new ViewModelProvider(this).get(NavigationViewModel.class);
        loginViewModel = new ViewModelProvider(Login.getContext()).get(LoginViewModel.class);
    }

    public static NavController getNavController() {
        return navController;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.admin_navigation, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_admin_navigation);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                navigationViewModel.logoutAdmin();
                navigationViewModel.getLogoutAdminResponse().observe(this, response -> {
                    try {
                        JSONObject responseObject = new JSONObject(response);
                        if (responseObject.getString("response").equals("logout-admin-success")) {
                            Login.setAdminLogged(false);
                            goToLoginActivity();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                });
        }
        return super.onOptionsItemSelected(item);
    }

    private void goToLoginActivity() {
        Intent intent = new Intent(AdminNavigation.this, Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}