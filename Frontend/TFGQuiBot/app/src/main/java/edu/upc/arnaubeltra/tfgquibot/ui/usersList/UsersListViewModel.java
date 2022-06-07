package edu.upc.arnaubeltra.tfgquibot.ui.usersList;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

import edu.upc.arnaubeltra.tfgquibot.api.RobotAPI;
import edu.upc.arnaubeltra.tfgquibot.models.listUsers.User;


// ViewModel for the UsersList class
public class UsersListViewModel extends ViewModel {

    // Instance of the RobotAPI class, to interact with the Backend.
    private RobotAPI robotAPI = RobotAPI.getInstance();

    // Definition of the liveData object.
    private static MutableLiveData<ArrayList<User>> usersListResponseLiveData;

    // Method to update the logged in users list.
    public void updateLoggedInUsers() {
        if (usersListResponseLiveData == null)
            usersListResponseLiveData = new MutableLiveData<>();
        robotAPI.getLoggedInUsersList();
    }

    // Method to get the request response on the update of the logged in users list (used by UsersList).
    public LiveData<ArrayList<User>> getLoggedInUsersListResponse() {
        return usersListResponseLiveData;
    }

    // Method to set the request response on the update of the logged in users list (used by RobotAPI).
    public void setLoggedInUsersListResponse(ArrayList<User> response) {
        usersListResponseLiveData.setValue(response);
    }

    // Method to change the actual activity that the robot is performing.
    public void changeActualActivity(String activity) {
        robotAPI.sendRobotActualActivity(activity);
    }
}
