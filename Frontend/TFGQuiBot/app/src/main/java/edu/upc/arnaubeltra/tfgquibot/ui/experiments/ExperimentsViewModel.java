package edu.upc.arnaubeltra.tfgquibot.ui.experiments;

import androidx.lifecycle.ViewModel;

import edu.upc.arnaubeltra.tfgquibot.api.RobotAPI;

public class ExperimentsViewModel extends ViewModel {
    private final RobotAPI robotAPI = RobotAPI.getInstance();

    public void startExperiment(String experiment){
        robotAPI.startExperiment(experiment);
    }
}