package edu.upc.arnaubeltra.tfgquibot.ui.ticTacToe;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import edu.upc.arnaubeltra.tfgquibot.api.RobotAPI;

// View Model of the Tic Tac Toe class.
public class TicTacToeViewModel extends ViewModel {

    // Instance of the RobotAPI class, to interact with the Backend.
    private RobotAPI robotAPI = RobotAPI.getInstance();

    // Definition of the liveData object.
    private static MutableLiveData<String> ticTacTocRequestResponse;


    // Method to start a new game.
    public void startNewGameTicTacToe(String userIP) {
        robotAPI.startTicTacToe(userIP);
    }

    // Method to send a new position that the player has selected.
    public void ticTacToePosition(int player, int x, int y) {
        if (player != 0) robotAPI.ticTacToePosition(player, x, y);
    }

    // Method to check the status of the game.
    public void ticTacToeCheckStatus() {
        robotAPI.checkStatusTicTacToe();
    }

    // Method to finish the game.
    public void finishGameTicTacToe() {
        robotAPI.finishGameTicTacToe();
    }

    // Configuration of liveData, in order to receive request responses.
    public void ticTacToeRequestResponse() {
        if (ticTacTocRequestResponse == null)
            ticTacTocRequestResponse = new MutableLiveData<>();
    }

    // Method to receive the request responses (used by TicTacToe class)
    public LiveData<String> getTicTacToeRequestResponse() {
        return ticTacTocRequestResponse;
    }

    // Method to set the request responses (used by the Robot API class)
    public void setTicTacToeRequestResponse(String response) {
        ticTacTocRequestResponse.setValue(response);
    }

    // Method to reset the live data object.
    public void resetLiveData() {
        ticTacTocRequestResponse = new MutableLiveData<>();
    }

}
