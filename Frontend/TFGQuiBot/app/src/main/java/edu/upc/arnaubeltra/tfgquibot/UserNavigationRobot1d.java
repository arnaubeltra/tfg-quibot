package edu.upc.arnaubeltra.tfgquibot;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;

import edu.upc.arnaubeltra.tfgquibot.databinding.ActivityUserNavigationRobot1dBinding;
import edu.upc.arnaubeltra.tfgquibot.ui.login.Login;
import edu.upc.arnaubeltra.tfgquibot.ui.shared.viewModels.NavigationViewModel;


// Class that handles Navigation Drawer of the Robot 1D account.
public class UserNavigationRobot1d extends AppCompatActivity {

    public static UserNavigationRobot1d instance;

    private AppBarConfiguration mAppBarConfiguration;
    private NavigationViewModel navigationViewModel;
    public static NavController navController;

    // Flag used to handle if logout is done by pressing logout button (as expected) or forcing logout by pressing the back button of the phone --> onDestroy of the activity.
    private int flag = 0;

    // Creates and configures views and menus.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Definition of the instance of the class.
        instance = this;

        edu.upc.arnaubeltra.tfgquibot.databinding.ActivityUserNavigationRobot1dBinding binding = ActivityUserNavigationRobot1dBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarUserNavigation.toolbar);

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        // Declaration of main endpoints.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.homeUser)
                .setOpenableLayout(drawer)
                .build();

        navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_user_navigation);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        // Instance of the navigationViewModel, used to logout user.
        navigationViewModel = new ViewModelProvider(this).get(NavigationViewModel.class);
    }

    // Method to provide context of the UserNavigationRobot1d class (used in other classes).
    public static Context getContext() {
        return instance.getApplicationContext();
    }

    // Method to provide instance of the UserNavigationRobot1d class (used in other classes).
    public static UserNavigationRobot1d getInstance() {
        return instance;
    }

    // Method to provide instance of the navigation controller (used in other classes).
    public static NavController getNavController() {
        return navController;
    }

    // Definition of the Robot1D options menu. Inflating items to be added to the menu.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user_navigation, menu);
        return true;
    }

    // Method to handle navigation between fragments.
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_user_navigation);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }

    // Top menu handler. Logout option handling.
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.logout) {
            flag = 1;
            WifiManager wifiManager = (WifiManager) this.getApplicationContext().getSystemService(WIFI_SERVICE);
            navigationViewModel.logoutUser(Formatter.formatIpAddress(wifiManager.getConnectionInfo().getIpAddress()));
            goToLoginActivity();
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    // Method to navigate back to the login activity.
    private void goToLoginActivity() {
        Intent intent = new Intent(UserNavigationRobot1d.this, Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

    // Handler when activity is destroyed (action to go back, app is closed...), to logout the user.
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (flag == 0) {
            WifiManager wifiManager = (WifiManager) this.getApplicationContext().getSystemService(WIFI_SERVICE);
            navigationViewModel.logoutUser(Formatter.formatIpAddress(wifiManager.getConnectionInfo().getIpAddress()));
        }
    }
}
