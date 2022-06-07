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
import edu.upc.arnaubeltra.tfgquibot.ui.shared.viewModels.NavigationViewModel;


// Class that handles Navigation Drawer of the admin account.
public class AdminNavigation extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private NavigationViewModel navigationViewModel;
    public static NavController navController;

    // Creates and configures views and menus.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        edu.upc.arnaubeltra.tfgquibot.databinding.ActivityAdminNavigationBinding binding = ActivityAdminNavigationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarAdminNavigation.toolbar);

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        // Declaration of main endpoints.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.homeAdmin)
                .setOpenableLayout(drawer)
                .build();

        navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_admin_navigation);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        // Instance of the navigationViewModel, used to logout admins.
        navigationViewModel = new ViewModelProvider(this).get(NavigationViewModel.class);
    }

    // Method to provide instance of the navigation controller (used in other classes).
    public static NavController getNavController() {
        return navController;
    }

    // Definition of the admin options menu. Inflating items to be added to the menu.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.admin_navigation, menu);
        return true;
    }

    // Method to handle navigation between fragments.
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_admin_navigation);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }

    // Top menu handler. Logout option handling.
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.logout) {
            navigationViewModel.logoutAdmin();
            navigationViewModel.getLogoutAdminResponse().observe(this, response -> {
                try {
                    JSONObject responseObject = new JSONObject(response);
                    if (responseObject.getString("response").equals("logout-admin-success")) {
                        Login.setAdminLogged(false);
                        goToLoginActivity();
                    }
                } catch (JSONException e) { e.printStackTrace(); }
            });
        }
        return super.onOptionsItemSelected(item);
    }

    // Method to navigate back to the login activity.
    private void goToLoginActivity() {
        Intent intent = new Intent(AdminNavigation.this, Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}