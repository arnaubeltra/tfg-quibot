package edu.upc.arnaubeltra.tfgquibot.ui.usersList;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

import edu.upc.arnaubeltra.tfgquibot.firebase.RealtimeDatabase;
import edu.upc.arnaubeltra.tfgquibot.models.User;

public class UsersListViewModel extends ViewModel {

    private final RealtimeDatabase realtimeDatabase = RealtimeDatabase.getInstance();

    public void updateLoggedInUsers() {
        realtimeDatabase.setupFirebaseListenerLoggedInUsers();
    }

    public LiveData<ArrayList<User>> getLoggedInUsers() {
        return realtimeDatabase.getLoggedInUsers();
    }
}
