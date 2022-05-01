package edu.upc.arnaubeltra.tfgquibot.viewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import edu.upc.arnaubeltra.tfgquibot.api.RobotAPI;

public class NavigationViewModel extends ViewModel {
    private RobotAPI robotAPI = RobotAPI.getInstance();

    private static MutableLiveData<String> logoutUserLiveData;
    private static MutableLiveData<String> logoutAdminLiveData;

    public void logoutUser(String user) {
        if (logoutUserLiveData == null)
            logoutUserLiveData = new MutableLiveData<>();
        robotAPI.userLogout(user);
    }

    public LiveData<String> getLogoutUserResponse() {
        return logoutUserLiveData;
    }

    public void setLogoutUserResponse(String response) {
        logoutUserLiveData.setValue(response);
    }

    public void logoutAdmin() {
        if (logoutAdminLiveData == null)
            logoutAdminLiveData = new MutableLiveData<>();
        robotAPI.adminLogout();
    }

    public LiveData<String> getLogoutAdminResponse() {
        return logoutAdminLiveData;
    }

    public void setLogoutAdminResponse(String response) {
        logoutAdminLiveData.setValue(response);
    }
}
