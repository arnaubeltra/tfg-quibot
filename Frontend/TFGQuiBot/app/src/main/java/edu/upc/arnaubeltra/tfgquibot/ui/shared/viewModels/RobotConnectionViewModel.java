package edu.upc.arnaubeltra.tfgquibot.ui.shared.viewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import edu.upc.arnaubeltra.tfgquibot.api.RobotAPI;


// ViewModel used to check the robot connection.
public class RobotConnectionViewModel extends ViewModel {

    // Instance of the RobotAPI class, to interact with the Backend.
    private RobotAPI robotAPI = RobotAPI.getInstance();

    // Definition of the liveData object.
    private static MutableLiveData<String> robotConnectionResponseLiveData;

    // Method to check the robot connection.
    public void checkRobotConnection(int robot) {
        if (robotConnectionResponseLiveData == null)
            robotConnectionResponseLiveData = new MutableLiveData<>();
        robotAPI.checkRobotConnection(robot);
    }

    // Method to get the robot connection. request response.
    public LiveData<String> getCheckRobotConnectionResponse() {
        return robotConnectionResponseLiveData;
    }

    // Method to set the robot connection. request response (used by Robot API).
    public void setCheckRobotConnectionResponse(String response) {
        robotConnectionResponseLiveData.setValue(response);
    }

    // Method to reset the liveData.
    public void resetLiveData() {
        robotConnectionResponseLiveData = new MutableLiveData<>();
    }
}
