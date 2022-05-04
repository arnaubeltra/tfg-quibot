package edu.upc.arnaubeltra.tfgquibot.viewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import edu.upc.arnaubeltra.tfgquibot.api.RobotAPI;

public class RobotConnectionViewModel extends ViewModel {
    private RobotAPI robotAPI = RobotAPI.getInstance();
    private static MutableLiveData<String> robotConnectionResponseLiveData;

    public void checkRobotConnection() {
        if (robotConnectionResponseLiveData == null)
            robotConnectionResponseLiveData = new MutableLiveData<>();
        robotAPI.checkRobotConnection();
    }

    public LiveData<String> getCheckRobotConnectionResponse() {
        return robotConnectionResponseLiveData;
    }

    public void setCheckRobotConnectionResponse(String response) {
        robotConnectionResponseLiveData.setValue(response);
    }
}
