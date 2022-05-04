package edu.upc.arnaubeltra.tfgquibot.ui.ticTacToe;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import edu.upc.arnaubeltra.tfgquibot.api.RobotAPI;

public class TicTacToeViewModel extends ViewModel {
    private RobotAPI robotAPI = RobotAPI.getInstance();

    /*private static MutableLiveData<String> startGameResponseLiveData;
    private static MutableLiveData<String> ticTacToePositionResponseLiveData;
    private static MutableLiveData<String> ticTacToeStatusResponseLiveData;*/

    private static MutableLiveData<String> ticTacTocRequestResponse;

    public void startNewGameTicTacToe(String userIP) {
        /*if (startGameResponseLiveData == null)
            startGameResponseLiveData = new MutableLiveData<>();*/
        robotAPI.startTicTacToe(userIP);
    }

    /*public LiveData<String> getStartNewGameResponse() {
        return startGameResponseLiveData;
    }

    public void setStartNewGameResponse(String response) {
        startGameResponseLiveData.setValue(response);
    }*/

    public void ticTacToePosition(int player, int x, int y) {
        /*if (ticTacToePositionResponseLiveData == null)
            ticTacToePositionResponseLiveData = new MutableLiveData<>();*/
        if (player != 0) robotAPI.ticTacToePosition(player, x, y);
    }

    /*public LiveData<String> getTicTacToePositionResponse() {
        return ticTacToePositionResponseLiveData;
    }

    public void setTicTacToePositionResponse(String response) {
        ticTacToePositionResponseLiveData.setValue(response);
    }*/

    public void ticTacToeCheckStatus() {
        /*if (ticTacToeStatusResponseLiveData == null)
            ticTacToeStatusResponseLiveData = new MutableLiveData<>();*/
        robotAPI.checkStatusTicTacToe();
    }

    /*public LiveData<String> getTicTacToeCheckStatusResponse() {
        return ticTacToeStatusResponseLiveData;
    }

    public void setTicTacToeCheckStatusResponse(String response) {
        ticTacToeStatusResponseLiveData.setValue(response);
    }*/

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
