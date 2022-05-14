package edu.upc.arnaubeltra.tfgquibot.ui.interactWithRobot;

import androidx.lifecycle.ViewModel;

import edu.upc.arnaubeltra.tfgquibot.api.RobotAPI;

public class InteractWithRobotViewModel extends ViewModel {
    private final RobotAPI robotAPI = RobotAPI.getInstance();

    public void startInteract(String boardSize){
        robotAPI.startInteract(boardSize);
    }

    public void sendInteraction(String interaction) {
        robotAPI.interactWithRobot(interaction);
    }
}
