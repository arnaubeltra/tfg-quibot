package edu.upc.arnaubeltra.tfgquibot.ui.connect4;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import edu.upc.arnaubeltra.tfgquibot.api.RobotAPI;

public class Connect4ViewModel extends ViewModel {
    private RobotAPI robotAPI = RobotAPI.getInstance();

    private static MutableLiveData<String> connect4RequestResponse;


    public void startNewGameConnect4(String userIP) {
        robotAPI.startConnect4(userIP);
    }

    public void connect4Position(int player, int column) {
        if (player != 0) robotAPI.connect4Position(player, column);
    }

    public void connect4CheckStatus() {
        robotAPI.checkStatusConnect4();
    }

    public void finishGameConnect4() {
        robotAPI.finishGameConnect4();
    }

    public void connect4RequestResponse() {
        if (connect4RequestResponse == null)
            connect4RequestResponse = new MutableLiveData<>();
    }

    public LiveData<String> getConnect4RequestResponse() {
        return connect4RequestResponse;
    }

    public void setConnect4RequestResponse(String response) {
        connect4RequestResponse.setValue(response);
    }
}
