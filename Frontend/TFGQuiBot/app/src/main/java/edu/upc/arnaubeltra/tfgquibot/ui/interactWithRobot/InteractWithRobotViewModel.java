package edu.upc.arnaubeltra.tfgquibot.ui.interactWithRobot;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

import edu.upc.arnaubeltra.tfgquibot.api.RobotAPI;
import edu.upc.arnaubeltra.tfgquibot.firebase.RealtimeDatabase;
import edu.upc.arnaubeltra.tfgquibot.models.User;

public class InteractWithRobotViewModel extends ViewModel {
    private final RobotAPI robotAPI = RobotAPI.getInstance();
    private RealtimeDatabase realtimeDatabase = RealtimeDatabase.getInstance();

    public void sendInteraction(String interaction) {
        robotAPI.interactWithRobot(interaction);
    }

    public void setupFirebaseListenerPermissionsUser(String uid) {
        realtimeDatabase.setupFirebaseListenerPermissionsUser(uid);
    }

    public LiveData<Boolean> getPermissionsUser() {
        return realtimeDatabase.getPermissionsUser();
    }
}
