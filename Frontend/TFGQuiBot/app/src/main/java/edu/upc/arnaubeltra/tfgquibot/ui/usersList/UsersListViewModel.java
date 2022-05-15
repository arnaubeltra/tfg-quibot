package edu.upc.arnaubeltra.tfgquibot.ui.usersList;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

import edu.upc.arnaubeltra.tfgquibot.api.RobotAPI;
import edu.upc.arnaubeltra.tfgquibot.models.listUsers.User;

public class UsersListViewModel extends ViewModel {

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

    public void changeActualActivity(String activity) {
        robotAPI.sendRobotActualActivity(activity);
    }
}
