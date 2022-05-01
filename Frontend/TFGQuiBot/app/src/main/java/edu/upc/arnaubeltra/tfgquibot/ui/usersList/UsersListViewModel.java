package edu.upc.arnaubeltra.tfgquibot.ui.usersList;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

import edu.upc.arnaubeltra.tfgquibot.api.RobotAPI;
import edu.upc.arnaubeltra.tfgquibot.models.User;

public class UsersListViewModel extends ViewModel {

    /*private final RealtimeDatabase realtimeDatabase = RealtimeDatabase.getInstance();

    public void updateLoggedInUsers() {
        realtimeDatabase.setupFirebaseListenerLoggedInUsers();
    }

    public LiveData<ArrayList<User>> getLoggedInUsers() {
        return realtimeDatabase.getLoggedInUsers();
    }*/

    private RobotAPI robotAPI = RobotAPI.getInstance();

    private static MutableLiveData<ArrayList<User>> usersListResponseLiveData;

    public void updateLoggedInUsers() {
        if (usersListResponseLiveData == null)
            usersListResponseLiveData = new MutableLiveData<>();
        robotAPI.getLoggedInUsersList();
    }

    public LiveData<ArrayList<User>> getLoggedInUsersListResponse() {
        return usersListResponseLiveData;
    }

    public void setLoggedInUsersListResponse(ArrayList<User> response) {
        usersListResponseLiveData.setValue(response);
    }
}
