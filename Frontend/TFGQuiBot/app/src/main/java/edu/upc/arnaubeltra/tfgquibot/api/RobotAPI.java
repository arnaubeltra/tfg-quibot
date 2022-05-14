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

import edu.upc.arnaubeltra.tfgquibot.ui.customProgram.CustomProgramViewModel;
import edu.upc.arnaubeltra.tfgquibot.ui.ticTacToe.TicTacToeViewModel;
import edu.upc.arnaubeltra.tfgquibot.ui.shared.viewModels.NavigationViewModel;
import edu.upc.arnaubeltra.tfgquibot.models.ListUsersAPI;
import edu.upc.arnaubeltra.tfgquibot.models.User;
import edu.upc.arnaubeltra.tfgquibot.ui.login.Login;
import edu.upc.arnaubeltra.tfgquibot.ui.login.LoginViewModel;
import edu.upc.arnaubeltra.tfgquibot.ui.usersList.UsersListViewModel;
import edu.upc.arnaubeltra.tfgquibot.ui.shared.viewModels.PermissionsViewModel;
import edu.upc.arnaubeltra.tfgquibot.ui.shared.viewModels.RobotConnectionViewModel;

public class RobotAPI extends ViewModel {

    private static final String BASE_URL = "http://192.168.100.2:10000";
    private static final String TAG = "RobotAPI";

    private static RobotAPI instance;

    private RequestQueue getRequestQueue;
    private RequestQueue postRequestQueue;

    private LoginViewModel loginViewModel;
    private NavigationViewModel navigationViewModel;
    private PermissionsViewModel permissionsViewModel;
    private UsersListViewModel usersListViewModel;
    private TicTacToeViewModel ticTacToeViewModel;
    private RobotConnectionViewModel robotConnectionViewModel;
    private CustomProgramViewModel customProgramViewModel;

    private ArrayList<User> loggedUsersList;

    public static RobotAPI getInstance() {
        if (instance == null) instance = new RobotAPI();
        return instance;
    }

    public void startInteract(String boardSize) {
        String url = BASE_URL;
        url += "/startInteract?board-size=" + boardSize;
        getRequest(url, "");
    }

    public void interactWithRobot(String interaction) {
        String url = BASE_URL;
        switch (interaction) {
            case "forward":
                url += "/sendInstruction?instruction=up";
                break;
            case "backwards":
                url += "/sendInstruction?instruction=down";
                break;
            case "left":
                url += "/sendInstruction?instruction=left";
                break;
            case "right":
                url += "/sendInstruction?instruction=right";
                break;
            case "raise_pipette":
                url += "/sendInstruction?instruction=raise_pipette";
                break;
            case "lower_pipette":
                url += "/sendInstruction?instruction=lower_pipette";
                break;
            case "suck":
                url += "/sendInstruction?instruction=suck";
                break;
            case "reset":
                url += "/sendInstruction?instruction=reset";
                break;
            case "readColor":
                url += "/sendInstruction?instruction=readColor";
                break;
            default:
                break;
        }
        getRequest(url, "interactWithRobot");
    }

    public void userLogin(String ipAddress, String name, String surname, String isAuthorized) {
        String url = BASE_URL + "/user/login";

        Map<String, String> params = new HashMap<>();
        params.put("ipAddress", ipAddress);
        params.put("name", name);
        params.put("surname", surname);
        params.put("isAuthorized", isAuthorized);

        postRequest(url, params, "userLogin");
    }

    public void adminLogin() {
        String url = BASE_URL + "/admin/login";
        getRequest(url, "adminLogin");
    }

    public void userLogout(String userIP) {
        String url = BASE_URL + "/user/logout?user=" + userIP;
        getRequest(url, "userLogout");
    }

    public void adminLogout() {
        String url = BASE_URL + "/admin/logout";
        getRequest(url, "adminLogout");
    }

    public void checkPermissionsUser(String userIP, String activity) {
        String url = BASE_URL + "/user/check-permissions?user=" + userIP + "&activity=" + activity;
        getRequest(url, "checkPermissionsUser");
    }

    public void changePermissionsUser(String userIP, String auth) {
        String url = BASE_URL + "/user/change-permissions?user=" + userIP + "&isAuthorized=" + auth;
        getRequest(url, "changePermissionsUser");
    }

    public void getLoggedInUsersList() {
        String url = BASE_URL + "/list-users";
        getRequest(url, "getLoggedInUsersList");
    }

    public void startTicTacToe(String userIP) {
        String url = BASE_URL + "/start-ticTacToe"; //?user=" + userIP;
        getRequest(url, "ticTacToe");
    }

    public void ticTacToePosition(int player, int x, int y) {
        String url = BASE_URL + "/ticTacToePosition?x=" + x + "&y=" + y + "&player=" + player;
        getRequest(url, "ticTacToe");
    }

    public void checkStatusTicTacToe() {
        String url = BASE_URL + "/status-ticTacToe";
        getRequest(url, "ticTacToe");
    }

    public void finishGameTicTacToe() {
        String url = BASE_URL + "/finish-ticTacToe";
        getRequest(url, "");
    }

    public void checkRobotConnection() {
        String url = BASE_URL + "/check-robot-connection";
        getRequest(url, "checkRobotConnection");
    }

    public void setBoardSize(String size) {
        String url = BASE_URL + "/board-size?type=" + size;
        getRequest(url, "");
    }

    public void sendListActions(String actions) {
        String url = BASE_URL + "/custom-program";

        /*GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        String actionsJSON = gson.toJson(actions);*/

        Map<String, String> params = new HashMap<>();
        params.put("actions", actions);

        postRequest(url, params, "sendListActions");
    }

    public void startExperiment(String experiment) {
        String url = BASE_URL + "/experiment?name=" + experiment;
        getRequest(url, "");
    }

    public void sendRobotActualActivity(String activity) {
        String url = BASE_URL + "/admin/actual-activity?activity=" + activity;
        getRequest(url, "sendRobotActualActivity");
    }

    public void sendUserActualActivity(String user, String activity) {
        String url = BASE_URL + "/user/actual-activity?user=" + user + "&activity=" + activity;
        getRequest(url, "sendUserActualActivity");
    }

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

    private void parseGetResponse(String response, String callFun) {
        loginViewModel = new ViewModelProvider(Login.getContext()).get(LoginViewModel.class);
        navigationViewModel = new ViewModelProvider(Login.getContext()).get(NavigationViewModel.class);
        permissionsViewModel = new ViewModelProvider(Login.getContext()).get(PermissionsViewModel.class);
        usersListViewModel = new ViewModelProvider(Login.getContext()).get(UsersListViewModel.class);
        ticTacToeViewModel = new ViewModelProvider(Login.getContext()).get(TicTacToeViewModel.class);
        robotConnectionViewModel = new ViewModelProvider(Login.getContext()).get(RobotConnectionViewModel.class);

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
                loggedUsersList = new ArrayList<>();
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
            case "sendRobotActualActivity":
                permissionsViewModel.setRobotActualActivityResponse(response);
                break;
            case "sendUserActualActivity":
                permissionsViewModel.setUserActualActivityResponse(response);
                break;
        }
    }

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

    private void parsePostResponse(String response, String callFun) {
        loginViewModel = new ViewModelProvider(Login.getContext()).get(LoginViewModel.class);
        customProgramViewModel = new ViewModelProvider(Login.getContext()).get(CustomProgramViewModel.class);
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
