package edu.upc.arnaubeltra.tfgquibot.ui.shared;

import androidx.lifecycle.ViewModel;

import edu.upc.arnaubeltra.tfgquibot.api.RobotAPI;

public class BoardSizeViewModel extends ViewModel {

    private RobotAPI robotAPI = RobotAPI.getInstance();

    public void setBoardSize(String size) {
        robotAPI.setBoardSize(size);
    }
}
