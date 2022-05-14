package edu.upc.arnaubeltra.tfgquibot.ui.interactWithRobot;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import edu.upc.arnaubeltra.tfgquibot.R;
import edu.upc.arnaubeltra.tfgquibot.UserNavigation;
import edu.upc.arnaubeltra.tfgquibot.ui.login.Login;
import edu.upc.arnaubeltra.tfgquibot.ui.login.LoginViewModel;
import edu.upc.arnaubeltra.tfgquibot.ui.shared.BoardSize;
import edu.upc.arnaubeltra.tfgquibot.viewModels.PermissionsViewModel;
import edu.upc.arnaubeltra.tfgquibot.viewModels.RobotConnectionViewModel;

public class InteractWithRobot extends Fragment {

    private InteractWithRobotViewModel interactWithRobotViewModel;
    private PermissionsViewModel permissionsViewModel;
    private RobotConnectionViewModel robotConnectionViewModel;
    private LoginViewModel loginViewModel;

    private Boolean isAuthorized = false;

    // Required empty public constructor
    public InteractWithRobot() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //super.onCreate(savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_interact_with_robot, container, false);

        v.findViewById(R.id.btnForward).setOnClickListener(view -> checkPermissions("forward"));
        v.findViewById(R.id.btnBackwards).setOnClickListener(view -> checkPermissions("backwards"));
        v.findViewById(R.id.btnLeft).setOnClickListener(view -> checkPermissions("left"));
        v.findViewById(R.id.btnRight).setOnClickListener(view -> checkPermissions("right"));
        v.findViewById(R.id.btnSuck).setOnClickListener(view -> checkPermissions("suck"));
        v.findViewById(R.id.btnRaisePipette).setOnClickListener(view -> checkPermissions("raise_pipette"));
        v.findViewById(R.id.btnLowerPipette).setOnClickListener(view -> checkPermissions("lower_pipette"));
        v.findViewById(R.id.btnReset).setOnClickListener(view -> checkPermissions("reset"));
        v.findViewById(R.id.btnReadColor).setOnClickListener(view -> checkPermissions("readColor"));

        interactWithRobotViewModel = new ViewModelProvider(Login.getContext()).get(InteractWithRobotViewModel.class);

        permissionsViewModel = new ViewModelProvider(Login.getContext()).get(PermissionsViewModel.class);
        robotConnectionViewModel = new ViewModelProvider(Login.getContext()).get(RobotConnectionViewModel.class);

        loginViewModel = new ViewModelProvider(Login.getContext()).get(LoginViewModel.class);

        checkRobotConnection();

        //if (Login.getAdminLogged()) {

        //}

        //BoardSize boardSize = BoardSize.getInstance();
        //boardSize.createDialogBoardSize(getActivity(), Login.getContext(), "Forats mitjans");

        return v;
    }

    private void checkRobotConnection() {
        robotConnectionViewModel.checkRobotConnection();
        robotConnectionViewModel.getCheckRobotConnectionResponse().observe(getViewLifecycleOwner(), response -> {
            try {
                JSONObject responseObject = new JSONObject(response);
                if (responseObject.getString("response").equals("robot-connection-failed"))
                    dialogWarningRobotNotConnected();
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

    private void action(String interaction) {
        //checkPermissions();
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
            default:
                break;
        }
    }

    private int i = 0;
    private void checkPermissions(String interaction) {
        if (Login.getAdminLogged()) {
            action(interaction);
        } else {
            permissionsViewModel.checkUserPermissions(Login.getIpAddress());

            if (i == 0) {
                permissionsViewModel.getUserPermissionsResponse().observe(getViewLifecycleOwner(), auth -> {
                    try {
                        JSONObject responseObject = new JSONObject(auth);
                        if (responseObject.getString("response").equals("true") && responseObject.getString("activity").equals("match"))
                            action(interaction);
                        else
                            Toast.makeText(UserNavigation.getContext(), R.string.txtNoPermissions, Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                });
            }
        }
    }
}