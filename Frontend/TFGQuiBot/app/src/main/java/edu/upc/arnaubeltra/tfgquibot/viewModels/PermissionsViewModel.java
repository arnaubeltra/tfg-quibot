package edu.upc.arnaubeltra.tfgquibot.viewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import edu.upc.arnaubeltra.tfgquibot.api.RobotAPI;

public class PermissionsViewModel extends ViewModel {

    private RobotAPI robotAPI = RobotAPI.getInstance();

    private static MutableLiveData<String> permissionsLiveData;
    private static MutableLiveData<String> changePermissionsLiveData;

    public void checkUserPermissions(String user) {
        if (permissionsLiveData == null)
            permissionsLiveData = new MutableLiveData<>();
        robotAPI.checkPermissionsUser(user);
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
        robotAPI.changePermissionsUser(user, auth);
    }

    public LiveData<String> getUserPermissionsChangeResponse() {
        return changePermissionsLiveData;
    }

    public void setUserPermissionsChangeResponse(String response) {
        changePermissionsLiveData.setValue(response);
    }
}
