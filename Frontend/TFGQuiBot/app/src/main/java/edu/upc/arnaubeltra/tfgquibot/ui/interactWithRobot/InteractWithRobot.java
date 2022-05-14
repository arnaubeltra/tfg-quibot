package edu.upc.arnaubeltra.tfgquibot.ui.interactWithRobot;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import edu.upc.arnaubeltra.tfgquibot.R;
import edu.upc.arnaubeltra.tfgquibot.UserNavigation;
import edu.upc.arnaubeltra.tfgquibot.ui.login.Login;
import edu.upc.arnaubeltra.tfgquibot.ui.shared.viewModels.PermissionsViewModel;
import edu.upc.arnaubeltra.tfgquibot.ui.shared.viewModels.RobotConnectionViewModel;

public class InteractWithRobot extends Fragment {

    private RobotConnectionViewModel robotConnectionViewModel;
    private PermissionsViewModel permissionsViewModel;
    private InteractWithRobotViewModel interactWithRobotViewModel;

    private Boolean robotConnected = false;
    private String interaction = "";
    private int init = 0;

    // Required empty public constructor
    public InteractWithRobot() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_interact_with_robot, container, false);

        v.findViewById(R.id.btnForward).setOnClickListener(view -> sendActionToRobot("forward"));
        v.findViewById(R.id.btnBackwards).setOnClickListener(view -> sendActionToRobot("backwards"));
        v.findViewById(R.id.btnLeft).setOnClickListener(view -> sendActionToRobot("left"));
        v.findViewById(R.id.btnRight).setOnClickListener(view -> sendActionToRobot("right"));
        v.findViewById(R.id.btnSuck).setOnClickListener(view -> sendActionToRobot("suck"));
        v.findViewById(R.id.btnRaisePipette).setOnClickListener(view -> sendActionToRobot("raise_pipette"));
        v.findViewById(R.id.btnLowerPipette).setOnClickListener(view -> sendActionToRobot("lower_pipette"));
        v.findViewById(R.id.btnReset).setOnClickListener(view -> sendActionToRobot("reset"));
        v.findViewById(R.id.btnReadColor).setOnClickListener(view -> sendActionToRobot("readColor"));

        robotConnectionViewModel = new ViewModelProvider(Login.getContext()).get(RobotConnectionViewModel.class);
        permissionsViewModel = new ViewModelProvider(Login.getContext()).get(PermissionsViewModel.class);
        interactWithRobotViewModel = new ViewModelProvider(Login.getContext()).get(InteractWithRobotViewModel.class);

        checkRobotConnection();
        return v;
    }

    private void checkRobotConnection() {
        robotConnectionViewModel.checkRobotConnection();
        robotConnectionViewModel.getCheckRobotConnectionResponse().observe(getViewLifecycleOwner(), response -> {
            try {
                JSONObject responseObject = new JSONObject(response);
                if (responseObject.getString("response").equals("robot-connection-failed")) {
                    dialogWarningRobotNotConnected();
                    robotConnected = false;
                } robotConnected = true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
    }

    private void dialogWarningRobotNotConnected() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.txtRobotNotConnected)
                .setMessage(R.string.txtCheckRobotConnection)
                .setPositiveButton(R.string.txtAccept, null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void setupPermissionsObserver() {
        if (init == 0) {
            permissionsViewModel.checkUserPermissions(Login.getIpAddress(), "");
            permissionsViewModel.getUserPermissionsResponse().observe(getViewLifecycleOwner(), auth -> {
                try {
                    JSONObject responseObject = new JSONObject(auth);
                    if (responseObject.getString("response").equals("true") && responseObject.getString("activity").equals("match")) executeAction();
                    else Toast.makeText(UserNavigation.getContext(), R.string.txtNoPermissions, Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });
        } init++;
    }

    private void sendActionToRobot(String action) {
        if (robotConnected) {
            setupPermissionsObserver();
            interaction = action;
            if (Login.getAdminLogged()) executeAction();
            else permissionsViewModel.checkUserPermissions(Login.getIpAddress(), "interact");
        }
    }

    private void executeAction() {
        switch (interaction) {
            case "forward":
                interactWithRobotViewModel.sendInteraction("forward");
                break;
            case "backwards":
                interactWithRobotViewModel.sendInteraction("backwards");
                break;
            case "left":
                interactWithRobotViewModel.sendInteraction("left");
                break;
            case "right":
                interactWithRobotViewModel.sendInteraction("right");
                break;
            case "raise_pipette":
                interactWithRobotViewModel.sendInteraction("raise_pipette");
                break;
            case "lower_pipette":
                interactWithRobotViewModel.sendInteraction("lower_pipette");
                break;
            case "suck":
                interactWithRobotViewModel.sendInteraction("suck");
                break;
            case "reset":
                interactWithRobotViewModel.sendInteraction("reset");
                break;
            case "readColor":
                interactWithRobotViewModel.sendInteraction("readColor");
                break;
        }
    }
}