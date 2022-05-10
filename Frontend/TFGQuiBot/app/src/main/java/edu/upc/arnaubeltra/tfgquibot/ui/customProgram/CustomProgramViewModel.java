package edu.upc.arnaubeltra.tfgquibot.ui.customProgram;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

import edu.upc.arnaubeltra.tfgquibot.api.RobotAPI;

public class CustomProgramViewModel extends ViewModel {

    private RobotAPI robotAPI = RobotAPI.getInstance();

    private static MutableLiveData<String> customProgramRequestResponse;

    public void onSendListActions(String actions) {
        if (customProgramRequestResponse == null)
            customProgramRequestResponse = new MutableLiveData<>();
        if (!actions.equals(""))
            robotAPI.sendListActions(actions);
    }

    public LiveData<String> getSendListActionsRequestResponse() {
        return customProgramRequestResponse;
    }

    public void setSendListActionsRequestResponse(String response) {
        customProgramRequestResponse.setValue(response);
    }
}
