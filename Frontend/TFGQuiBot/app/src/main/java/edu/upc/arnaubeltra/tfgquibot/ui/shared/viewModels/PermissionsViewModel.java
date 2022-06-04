package edu.upc.arnaubeltra.tfgquibot.ui.shared.viewModels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import edu.upc.arnaubeltra.tfgquibot.api.RobotAPI;

public class PermissionsViewModel extends ViewModel {

    private RobotAPI robotAPI = RobotAPI.getInstance();

    private static MutableLiveData<String> permissionsLiveData;
    private static MutableLiveData<String> changePermissionsLiveData;

    private static MutableLiveData<String> robotActualActivityLiveData;
    private static MutableLiveData<String> userActualActivityLiveData;

    public void checkUserPermissions(String user, String activity) {
        if (permissionsLiveData == null)
            permissionsLiveData = new MutableLiveData<>();
        if (!activity.equals(""))
            robotAPI.checkPermissionsUser(user, activity);
    }

    public LiveData<String> getUserPermissionsResponse() {
        return permissionsLiveData;
    }

    public void setUserPermissionsResponse(String response) {
        permissionsLiveData.setValue(response);
    }

    public void changeUserPermissions(String user, String auth) {
        if (changePermissionsLiveData == null)
            changePermissionsLiveData = new MutableLiveData<>();
        if (!user.equals("")) robotAPI.changePermissionsUser(user, auth);
    }

    public LiveData<String> getUserPermissionsChangeResponse() {
        return changePermissionsLiveData;
    }

    public void setUserPermissionsChangeResponse(String response) {
        changePermissionsLiveData.setValue(response);
    }

    public void robotActualActivity(String activity) {
        if (robotActualActivityLiveData == null)
            robotActualActivityLiveData = new MutableLiveData<>();
        robotAPI.sendRobotActualActivity(activity);
    }

    public LiveData<String> getRobotActualActivityResponse() {
        return robotActualActivityLiveData;
    }

    public void setRobotActualActivityResponse(String response) {
        robotActualActivityLiveData.setValue(response);
    }

    public void userActualActivity(String user, String activity) {
        if (userActualActivityLiveData == null)
            userActualActivityLiveData = new MutableLiveData<>();
        robotAPI.sendUserActualActivity(user, activity);
    }

    public LiveData<String> getUserActualActivityResponse() {
        return userActualActivityLiveData;
    }

    public void setUserActualActivityResponse(String response) {
        userActualActivityLiveData.setValue(response);
    }

    public void resetLiveData() {
        permissionsLiveData = new MutableLiveData<>();
    }
}
