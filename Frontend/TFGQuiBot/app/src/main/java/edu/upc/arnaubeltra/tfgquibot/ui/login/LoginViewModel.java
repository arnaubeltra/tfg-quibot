package edu.upc.arnaubeltra.tfgquibot.ui.login;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import edu.upc.arnaubeltra.tfgquibot.api.RobotAPI;

public class LoginViewModel extends ViewModel {
    private RobotAPI robotAPI = RobotAPI.getInstance();

    private static MutableLiveData<String> newUserLoginLiveData;
    private static MutableLiveData<String> newAdminLoginLiveData;

    public void newUserLogin(String ipAddress, String name, String surname, String isAuthorized) {
        if (newUserLoginLiveData == null)
            newUserLoginLiveData = new MutableLiveData<>();
        robotAPI.userLogin(ipAddress, name, surname, isAuthorized);
    }

    public LiveData<String> getNewUserLoginResponse() {
        return newUserLoginLiveData;
    }

    public void setNewUserLoginResponse(String response) {
        newUserLoginLiveData.setValue(response);
    }

    public void newAdminLogin() {
        if (newAdminLoginLiveData == null)
            newAdminLoginLiveData = new MutableLiveData<>();
        robotAPI.adminLogin();
    }

    public LiveData<String> getNewAdminLoginResponse() {
        return newAdminLoginLiveData;
    }

    public void setNewAdminLoginResponse(String response) {
        newAdminLoginLiveData.setValue(response);
    }

    public void selectRobot(int robot) {
        robotAPI.selectRobot(robot);
    }
}
