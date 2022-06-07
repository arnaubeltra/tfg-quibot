package edu.upc.arnaubeltra.tfgquibot.ui.shared.viewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import edu.upc.arnaubeltra.tfgquibot.api.RobotAPI;


// ViewModel used for Navigation
public class NavigationViewModel extends ViewModel {

    // Instance of the RobotAPI class, to interact with the Backend.
    private RobotAPI robotAPI = RobotAPI.getInstance();

    // Definition of the liveData objects.
    private static MutableLiveData<String> logoutUserLiveData;
    private static MutableLiveData<String> logoutAdminLiveData;

    // Method to logout the user.
    public void logoutUser(String user) {
        if (logoutUserLiveData == null)
            logoutUserLiveData = new MutableLiveData<>();
        robotAPI.userLogout(user);
    }

    // Method to set the logout request response (used by Robot API).
    public void setLogoutUserResponse(String response) {
        logoutUserLiveData.setValue(response);
    }

    // Method to logout the admin.
    public void logoutAdmin() {
        if (logoutAdminLiveData == null)
            logoutAdminLiveData = new MutableLiveData<>();
        robotAPI.adminLogout();
    }

    // Method to get the logout request response (used by AdminLogin).
    public LiveData<String> getLogoutAdminResponse() {
        return logoutAdminLiveData;
    }

    // Method to set the logout request response (used by Robot API).
    public void setLogoutAdminResponse(String response) {
        logoutAdminLiveData.setValue(response);
    }
}
