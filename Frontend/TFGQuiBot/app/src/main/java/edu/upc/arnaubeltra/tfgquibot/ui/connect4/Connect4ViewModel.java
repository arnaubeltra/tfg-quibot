package edu.upc.arnaubeltra.tfgquibot.ui.connect4;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import edu.upc.arnaubeltra.tfgquibot.api.RobotAPI;


// View Model of the Connect 4 class.
public class Connect4ViewModel extends ViewModel {

    // Instance of the RobotAPI class, to interact with the Backend.
    private RobotAPI robotAPI = RobotAPI.getInstance();

    // Definition of the liveData object.
    private static MutableLiveData<String> connect4RequestResponse;


    // Method to start a new game.
    public void startNewGameConnect4(String userIP) {
        robotAPI.startConnect4(userIP);
    }

    // Method to send a new position that the player has selected.
    public void connect4Position(int player, int column) {
        if (player != 0) robotAPI.connect4Position(player, column);
    }

    // Method to check the status of the game.
    public void connect4CheckStatus() {
        robotAPI.checkStatusConnect4();
    }

    // Method to finish the game.
    public void finishGameConnect4() {
        robotAPI.finishGameConnect4();
    }

    // Configuration of liveData, in order to receive request responses.
    public void connect4RequestResponse() {
        if (connect4RequestResponse == null)
            connect4RequestResponse = new MutableLiveData<>();
    }

    // Method to receive the request responses (used by Connect4 class)
    public LiveData<String> getConnect4RequestResponse() {
        return connect4RequestResponse;
    }

    // Method to set the request responses (used by the Robot API class)
    public void setConnect4RequestResponse(String response) {
        connect4RequestResponse.setValue(response);
    }

    // Method to reset the live data object.
    public void resetLiveData() {
        connect4RequestResponse = new MutableLiveData<>();
    }

}
