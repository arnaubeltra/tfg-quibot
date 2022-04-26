package edu.upc.arnaubeltra.tfgquibot.api;

import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

import edu.upc.arnaubeltra.tfgquibot.NavigationViewModel;
import edu.upc.arnaubeltra.tfgquibot.UserNavigation;
import edu.upc.arnaubeltra.tfgquibot.ui.login.Login;
import edu.upc.arnaubeltra.tfgquibot.ui.login.LoginViewModel;
import edu.upc.arnaubeltra.tfgquibot.viewModels.PermissionsViewModel;

public class RobotAPI extends ViewModel {

    private static final String BASE_URL = "http://192.168.100.2:10000";
    private static final String TAG = "RobotAPI";

    private static RobotAPI instance;

    private RequestQueue getRequestQueue;
    private RequestQueue postRequestQueue;

    private String getRequestResponse;
    private String postRequestResponse;

    private static MutableLiveData<String> postRequestResponseLiveData;

    private LoginViewModel loginViewModel;
    private NavigationViewModel navigationViewModel;
    private PermissionsViewModel permissionsViewModel;

    public static RobotAPI getInstance() {
        if (instance == null) instance = new RobotAPI();
        return instance;
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

    public void checkPermissionsUser(String userIP) {
        String url = BASE_URL + "/user/check-permissions?user=" + userIP;
        getRequest(url, "checkPermissionsUser");
    }

    public String changePermissionsUser(String userIP, String auth) {
        String url = BASE_URL + "/user/change-permissions?user=" + userIP + "&isAuthorized=" + auth;
        getRequest(url, "changePermissionsUser");
        return getRequestResponse;
    }

    private void getRequest(String url, String callFun) {
        if (getRequestQueue == null)
            getRequestQueue = Volley.newRequestQueue(Login.getInstance());

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, 
                response -> {
                    try {
                        parseGetResponse(response, callFun);
                    } catch (Exception ex) {
                        Log.d(TAG, "onResponse: Cannot execute action (API)");
                    }
                }, 
                error -> Log.d(TAG, "Get error: " + error));
        getRequestQueue.add(stringRequest);
    }

    private void parseGetResponse(String response, String callFun) {
        loginViewModel = new ViewModelProvider(Login.getContext()).get(LoginViewModel.class);
        navigationViewModel = new ViewModelProvider(Login.getContext()).get(NavigationViewModel.class);
        permissionsViewModel = new ViewModelProvider(Login.getContext()).get(PermissionsViewModel.class);

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
            case "checkPermissionsUser":
                permissionsViewModel.setUserPermissions(response);
                break;
        }
    }

    private void postRequest(String url, Map postParams, String callFun) {
        if (postRequestQueue == null)
            postRequestQueue = Volley.newRequestQueue(Login.getInstance());

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                response -> parsePostResponse(response),
                error -> Log.d(TAG, "onErrorResponse: " + error)
        ) {
            @Override
            protected Map<String, String> getParams() {
                return postParams;
            }
        };
        postRequestQueue.add(postRequest);
    }

    private void parsePostResponse(String response) {
        loginViewModel = new ViewModelProvider(Login.getContext()).get(LoginViewModel.class);
        loginViewModel.setNewUserLoginResponse(response);
    }
}
