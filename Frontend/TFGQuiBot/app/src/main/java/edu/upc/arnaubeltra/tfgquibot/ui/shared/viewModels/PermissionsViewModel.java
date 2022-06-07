package edu.upc.arnaubeltra.tfgquibot.ui.shared.viewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import edu.upc.arnaubeltra.tfgquibot.api.RobotAPI;


// ViewModel used for Permissions
public class PermissionsViewModel extends ViewModel {

    // Instance of the RobotAPI class, to interact with the Backend.
    private RobotAPI robotAPI = RobotAPI.getInstance();

    // Definition of the liveData objects.
    private static MutableLiveData<String> permissionsLiveData;
    private static MutableLiveData<String> changePermissionsLiveData;


    // Method to check user permissions, in a certain activity.
    public void checkUserPermissions(String user, String activity) {
        if (permissionsLiveData == null)
            permissionsLiveData = new MutableLiveData<>();
        if (!activity.equals(""))
            robotAPI.checkPermissionsUser(user, activity);
    }

    // Method to get the permissions request response (used by many classes)
    public LiveData<String> getUserPermissionsResponse() {
        return permissionsLiveData;
    }

    // Method to set the permissions request response (used by Robot API).
    public void setUserPermissionsResponse(String response) {
        permissionsLiveData.setValue(response);
    }

    // Method to change user permissions.
    public void changeUserPermissions(String user, String auth) {
        if (changePermissionsLiveData == null)
            changePermissionsLiveData = new MutableLiveData<>();
        if (!user.equals("")) robotAPI.changePermissionsUser(user, auth);
    }

    // Method to get the permissions change request response (used by many classes)
    public LiveData<String> getUserPermissionsChangeResponse() {
        return changePermissionsLiveData;
    }

    // Method to set the permissions change request response (used by Robot API).
    public void setUserPermissionsChangeResponse(String response) {
        changePermissionsLiveData.setValue(response);
    }

    // Method to reset the liveData.
    public void resetLiveData() {
        permissionsLiveData = new MutableLiveData<>();
    }
}
