package edu.upc.arnaubeltra.tfgquibot.api;

import android.util.Log;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import edu.upc.arnaubeltra.tfgquibot.ui.connect4.Connect4ViewModel;
import edu.upc.arnaubeltra.tfgquibot.ui.customProgram.CustomProgramViewModel;
import edu.upc.arnaubeltra.tfgquibot.ui.ticTacToe.TicTacToeViewModel;
import edu.upc.arnaubeltra.tfgquibot.ui.shared.viewModels.NavigationViewModel;
import edu.upc.arnaubeltra.tfgquibot.models.listUsers.ListUsersAPI;
import edu.upc.arnaubeltra.tfgquibot.models.listUsers.User;
import edu.upc.arnaubeltra.tfgquibot.ui.login.Login;
import edu.upc.arnaubeltra.tfgquibot.ui.login.LoginViewModel;
import edu.upc.arnaubeltra.tfgquibot.ui.usersList.UsersListViewModel;
import edu.upc.arnaubeltra.tfgquibot.ui.shared.viewModels.PermissionsViewModel;
import edu.upc.arnaubeltra.tfgquibot.ui.shared.viewModels.RobotConnectionViewModel;


// Class used to interact directly with the Backend Robot API.
public class RobotAPI extends ViewModel {

    // Definition of constants.
    private static final String BASE_URL = "http://192.168.100.1:10000";
    private static final String TAG = "RobotAPI";

    // Instance of the class (used by many ViewModels).
    private static RobotAPI instance;

    // Definition of the request queues for a GET request and a POST request.
    private RequestQueue getRequestQueue;
    private RequestQueue postRequestQueue;

    private LoginViewModel loginViewModel;

    // Public instance of the RobotAPI class (singleton pattern).
    public static RobotAPI getInstance() {
        if (instance == null) instance = new RobotAPI();
        return instance;
    }


    // The following methods configure the URL, and then call the getRequest or postRequest method, to perform each request.
    // General functionalities.
    // Method that handles when a user performs a login. Uses POST request, to send user information.
    public void userLogin(String ipAddress, String name, String surname, String isAuthorized) {
        String url = BASE_URL + "/user/login";

        Map<String, String> params = new HashMap<>();
        params.put("ipAddress", ipAddress);
        params.put("name", name);
        params.put("surname", surname);
        params.put("isAuthorized", isAuthorized);

        postRequest(url, params, "userLogin");
    }

    // Method that handles when an admin performs a login.
    public void adminLogin() {
        String url = BASE_URL + "/admin/login";
        getRequest(url, "adminLogin");
    }

    // Method that handles when a user performs a logout.
    public void userLogout(String userIP) {
        String url = BASE_URL + "/user/logout?user=" + userIP;
        getRequest(url, "userLogout");
    }

    // Method that handles when an admin performs a logout.
    public void adminLogout() {
        String url = BASE_URL + "/admin/logout";
        getRequest(url, "adminLogout");
    }

    // Method used to check permissions of a certain user in a certain activity.
    public void checkPermissionsUser(String userIP, String activity) {
        String url = BASE_URL + "/user/check-permissions?user=" + userIP + "&activity=" + activity;
        getRequest(url, "checkPermissionsUser");
    }

    // Method to check whether robot is connected or not.
    public void checkRobotConnection(int robot) {
        String url = BASE_URL + "/check-robot-connection?robot=" + robot;
        getRequest(url, "checkRobotConnection");
    }

    // Experiments.
    // Method that starts any experiment of the experiments list.
    public void startExperiment(String experiment) {
        String url = BASE_URL + "/experiment?name=" + experiment;
        getRequest(url, "");
    }

    // Interact with robot.
    // Method that sends to the robot, the start of InteractWithRobot.
    public void startInteract() {
        String url = BASE_URL;
        url += "/startInteract";
        getRequest(url, "");
    }

    // Method that sends to robot the movements that has to perform when using InteractWithRobot activity.
    public void interactWithRobot(String interaction) {
        String url = BASE_URL;
        url += "/sendInstruction?instruction=" + interaction;
        getRequest(url, "interactWithRobot");
    }

    // Custom program
    // Method to send the list of actions that the user has created, and that wants the robot to perform.
    public void sendListActions(String actions) {
        String url = BASE_URL + "/custom-program";

        Map<String, String> params = new HashMap<>();
        params.put("actions", actions);

        postRequest(url, params, "sendListActions");
    }

    // Tic tac toe.
    // Method that starts a game TicTacToe
    public void startTicTacToe(String userIP) {
        String url = BASE_URL + "/start-ticTacToe"; //?user=" + userIP;
        getRequest(url, "ticTacToe");
    }

    // Method that sends the new position of any player.
    public void ticTacToePosition(int player, int x, int y) {
        String url = BASE_URL + "/ticTacToePosition?x=" + x + "&y=" + y + "&player=" + player;
        getRequest(url, "ticTacToe");
    }

    // Method that checks the status of the ticTacToe game.
    public void checkStatusTicTacToe() {
        String url = BASE_URL + "/status-ticTacToe";
        getRequest(url, "ticTacToe");
    }

    // Method to finish a TicTacToe game.
    public void finishGameTicTacToe() {
        String url = BASE_URL + "/finish-ticTacToe";
        getRequest(url, "");
    }

    // Connect 4.
    // Method that starts a game of Connect 4
    public void startConnect4(String userIP) {
        String url = BASE_URL + "/start-connect4";
        getRequest(url, "connect4");
    }

    // Method that sends the new position of any player.
    public void connect4Position(int player, int y) {
        String url = BASE_URL + "/connect4Position?column=" + y + "&player=" + player;
        getRequest(url, "connect4");
    }

    // Method that checks the status of the Connect 4 game.
    public void checkStatusConnect4() {
        String url = BASE_URL + "/status-connect4";
        getRequest(url, "connect4");
    }

    // Method to finish a Connect 4 game.
    public void finishGameConnect4() {
        String url = BASE_URL + "/finish-connect4";
        getRequest(url, "");
    }

    // Users list.
    // Method used to change permissions of a certain user.
    public void changePermissionsUser(String userIP, String auth) {
        String url = BASE_URL + "/user/change-permissions?user=" + userIP + "&isAuthorized=" + auth;
        getRequest(url, "changePermissionsUser");
    }

    // Method that returns the list of users that are logged in.
    public void getLoggedInUsersList() {
        String url = BASE_URL + "/list-users";
        getRequest(url, "getLoggedInUsersList");
    }

    // Method to send which activity is being performed by the robot.
    public void sendRobotCurrentActivity(String activity) {
        String url = BASE_URL + "/admin/current-activity?activity=" + activity;
        getRequest(url, "");
    }

    // Method to select which robot is being used on that moment.
    public void selectRobot(int robot) {
        String url = BASE_URL + "/admin/set-robot?robot=" + robot;
        getRequest(url, "");
    }


    // The following methods perform a GET request and a POST request. They have response handler functions, to handle each response, and to know where has to be send.
    // Performs a simple GET request according to the URL, and sends the received response to the parseGetResponse function
    private void getRequest(String url, String callFun) {
        if (getRequestQueue == null)
            getRequestQueue = Volley.newRequestQueue(Login.getInstance());

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, 
                response -> {
                    try {
                        parseGetResponse(response, callFun);
                    } catch (Exception ex) {
                        Log.d(TAG, "onResponse: Cannot execute action (API)" + ex);
                    }
                }, 
                error -> Log.d(TAG, "Get error: " + error));
        getRequestQueue.add(stringRequest);
    }

    // Parses the GET request response, by sending each of them to the ViewModel that made the request.
    private void parseGetResponse(String response, String callFun) {
        loginViewModel = new ViewModelProvider(Login.getContext()).get(LoginViewModel.class);
        NavigationViewModel navigationViewModel = new ViewModelProvider(Login.getContext()).get(NavigationViewModel.class);
        PermissionsViewModel permissionsViewModel = new ViewModelProvider(Login.getContext()).get(PermissionsViewModel.class);
        UsersListViewModel usersListViewModel = new ViewModelProvider(Login.getContext()).get(UsersListViewModel.class);
        TicTacToeViewModel ticTacToeViewModel = new ViewModelProvider(Login.getContext()).get(TicTacToeViewModel.class);
        Connect4ViewModel connect4ViewModel = new ViewModelProvider(Login.getContext()).get(Connect4ViewModel.class);
        RobotConnectionViewModel robotConnectionViewModel = new ViewModelProvider(Login.getContext()).get(RobotConnectionViewModel.class);

        switch (callFun) {
            case "adminLogin":
                loginViewModel.setNewAdminLoginResponse(response);
                break;
            case "userLogout":
                navigationViewModel.setLogoutUserResponse(response);
                break;
            case "adminLogout":
                navigationViewModel.setLogoutAdminResponse(response);
                break;
            case "checkRobotConnection":
                robotConnectionViewModel.setCheckRobotConnectionResponse(response);
                break;
            case "checkPermissionsUser":
                permissionsViewModel.setUserPermissionsResponse(response);
                break;
            case "changePermissionsUser":
                permissionsViewModel.setUserPermissionsChangeResponse(response);
                break;
            case "getLoggedInUsersList":
                ArrayList<User> loggedUsersList = new ArrayList<>();
                Gson gson = new GsonBuilder().create();
                ListUsersAPI listUsersAPI = gson.fromJson(response, ListUsersAPI.class);
                if (listUsersAPI != null) {
                    for (int i = 0; i < listUsersAPI.getUsers().size(); i++) {
                        User user = new User(listUsersAPI.getUsers().get(i).getUid(),
                                listUsersAPI.getUsers().get(i).getName(),
                                listUsersAPI.getUsers().get(i).getSurname(),
                                listUsersAPI.getUsers().get(i).getAuthorized());
                        loggedUsersList.add(user);
                    }
                }
                usersListViewModel.setLoggedInUsersListResponse(loggedUsersList);
                break;
            case "ticTacToe":
                ticTacToeViewModel.setTicTacToeRequestResponse(response);
                break;
            case "connect4":
                connect4ViewModel.setConnect4RequestResponse(response);
                break;
        }
    }

    // Performs a simple POST request according to the URL, and sends the received response to the parsePostResponse function
    private void postRequest(String url, Map postParams, String callFun) {
        if (postRequestQueue == null)
            postRequestQueue = Volley.newRequestQueue(Login.getInstance());

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                response -> parsePostResponse(response, callFun),
                error -> Log.d(TAG, "onErrorResponse: " + error)
        ) {
            @Override
            protected Map<String, String> getParams() {
                return postParams;
            }
        };
        postRequestQueue.add(postRequest);
    }

    // Parses the POST request response, by sending each of them to the ViewModel that made the request.
    private void parsePostResponse(String response, String callFun) {
        loginViewModel = new ViewModelProvider(Login.getContext()).get(LoginViewModel.class);
        CustomProgramViewModel customProgramViewModel = new ViewModelProvider(Login.getContext()).get(CustomProgramViewModel.class);
        switch (callFun) {
            case "userLogin":
                loginViewModel.setNewUserLoginResponse(response);
                break;
            case "sendListActions":
                customProgramViewModel.setSendListActionsRequestResponse(response);
                break;
        }
    }
}
