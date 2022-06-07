package edu.upc.arnaubeltra.tfgquibot.ui.customProgram;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


// View Model of the Custom Program class.
import edu.upc.arnaubeltra.tfgquibot.api.RobotAPI;


// View Model of the CustomProgram class.
public class CustomProgramViewModel extends ViewModel {

    // Instance of the RobotAPI class, to interact with the Backend.
    private RobotAPI robotAPI = RobotAPI.getInstance();

    // Definition of the liveData object.
    private static MutableLiveData<String> customProgramRequestResponse;

    // Method to send the list of actions to the robot.
    public void onSendListActions(String actions) {
        if (customProgramRequestResponse == null)
            customProgramRequestResponse = new MutableLiveData<>();
        if (!actions.equals("")) {
            robotAPI.sendListActions(actions);
        }
    }

    // Method to get the request response when sent the list of actions (used by CustomProgram class).
    public LiveData<String> getSendListActionsRequestResponse() {
        return customProgramRequestResponse;
    }

    // Method to set the request response when sent the list of actions (used by RobotAPI class).
    public void setSendListActionsRequestResponse(String response) {
        customProgramRequestResponse.setValue(response);
    }
}
