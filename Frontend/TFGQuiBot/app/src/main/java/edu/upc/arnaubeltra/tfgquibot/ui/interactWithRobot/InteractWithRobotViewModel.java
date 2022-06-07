package edu.upc.arnaubeltra.tfgquibot.ui.interactWithRobot;

import androidx.lifecycle.ViewModel;

import edu.upc.arnaubeltra.tfgquibot.api.RobotAPI;


// View Model of the CustomProgram class.
public class InteractWithRobotViewModel extends ViewModel {

    // Instance of the RobotAPI class, to interact with the Backend.
    private final RobotAPI robotAPI = RobotAPI.getInstance();

    // Method to start interaction.
    public void startInteract(){
        robotAPI.startInteract();
    }

    // Method to send interaction to robot.
    public void sendInteraction(String interaction) {
        robotAPI.interactWithRobot(interaction);
    }
}
