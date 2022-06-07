package edu.upc.arnaubeltra.tfgquibot.ui.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import edu.upc.arnaubeltra.tfgquibot.api.RobotAPI;


// ViewModel of the Login and LoginAdmin classes.
public class LoginViewModel extends ViewModel {

    // Instance of the RobotAPI class, to interact with the Backend.
    private RobotAPI robotAPI = RobotAPI.getInstance();

    // Definition of the liveData objects.
    private static MutableLiveData<String> newUserLoginLiveData;
    private static MutableLiveData<String> newAdminLoginLiveData;

    // Method to perform a the login of a new user.
    public void newUserLogin(String ipAddress, String name, String surname, String isAuthorized) {
        if (newUserLoginLiveData == null)
            newUserLoginLiveData = new MutableLiveData<>();
        robotAPI.userLogin(ipAddress, name, surname, isAuthorized);
    }

    // Method to get the request response when a new User is logged in (used by Login class).
    public LiveData<String> getNewUserLoginResponse() {
        return newUserLoginLiveData;
    }

    // Method to set the request response when a new User is logged in (used by RobotAPI class).
    public void setNewUserLoginResponse(String response) {
        newUserLoginLiveData.setValue(response);
    }

    // Method to perform a the login of an admin.
    public void newAdminLogin() {
        if (newAdminLoginLiveData == null)
            newAdminLoginLiveData = new MutableLiveData<>();
        robotAPI.adminLogin();
    }

    // Method to get the request response when an Admin is logged in (used by AdminLogin class).
    public LiveData<String> getNewAdminLoginResponse() {
        return newAdminLoginLiveData;
    }

    // Method to set the request response when an Admin is logged in (used by RobotAPI class).
    public void setNewAdminLoginResponse(String response) {
        newAdminLoginLiveData.setValue(response);
    }

    // Method to select the robot that is being used.
    public void selectRobot(int robot) {
        robotAPI.selectRobot(robot);
    }
}
