package edu.upc.arnaubeltra.tfgquibot.viewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import edu.upc.arnaubeltra.tfgquibot.api.RobotAPI;

public class PermissionsViewModel extends ViewModel {

    private RobotAPI robotAPI = RobotAPI.getInstance();

    private static MutableLiveData<String> permissionsLiveData;

    public void checkUserPermissions(String user) {
        if (permissionsLiveData == null)
            permissionsLiveData = new MutableLiveData<>();
        robotAPI.checkPermissionsUser(user);
    }

    public LiveData<String> getUserPermissions() {
        return permissionsLiveData;
    }

    public void setUserPermissions(String response) {
        permissionsLiveData.setValue(response);
    }
}
