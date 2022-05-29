package edu.upc.arnaubeltra.tfgquibot.ui.ticTacToe;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import edu.upc.arnaubeltra.tfgquibot.api.RobotAPI;

public class TicTacToeViewModel extends ViewModel {
    private RobotAPI robotAPI = RobotAPI.getInstance();

    private static MutableLiveData<String> ticTacTocRequestResponse;

    public void startNewGameTicTacToe(String userIP) {
        robotAPI.startTicTacToe(userIP);
    }

    public void ticTacToePosition(int player, int x, int y) {
        if (player != 0) robotAPI.ticTacToePosition(player, x, y);
    }

    public void ticTacToeCheckStatus() {
        robotAPI.checkStatusTicTacToe();
    }

    public void finishGameTicTacToe() {
        robotAPI.finishGameTicTacToe();
    }

    public void ticTacToeRequestResponse() {
        if (ticTacTocRequestResponse == null)
            ticTacTocRequestResponse = new MutableLiveData<>();
    }

    public LiveData<String> getTicTacToeRequestResponse() {
        return ticTacTocRequestResponse;
    }

    public void setTicTacToeRequestResponse(String response) {
        ticTacTocRequestResponse.setValue(response);
    }

}
