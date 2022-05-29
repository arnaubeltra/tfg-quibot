package edu.upc.arnaubeltra.tfgquibot.ui.interactWithRobot;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import edu.upc.arnaubeltra.tfgquibot.R;
import edu.upc.arnaubeltra.tfgquibot.UserNavigationRobot2d;
import edu.upc.arnaubeltra.tfgquibot.ui.login.AdminLogin;
import edu.upc.arnaubeltra.tfgquibot.ui.login.Login;
import edu.upc.arnaubeltra.tfgquibot.ui.shared.viewModels.PermissionsViewModel;
import edu.upc.arnaubeltra.tfgquibot.ui.shared.viewModels.RobotConnectionViewModel;

public class InteractWithRobot extends Fragment {

    private RobotConnectionViewModel robotConnectionViewModel;
    private PermissionsViewModel permissionsViewModel;
    private InteractWithRobotViewModel interactWithRobotViewModel;

    private Boolean robotConnected = false;
    private String interaction = "";
    private int init = 0, init2 = 0;
    private int robot = 0;

    private Button btnMulti1, btnMulti2;

    // Required empty public constructor
    public InteractWithRobot() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_interact_with_robot, container, false);

        robot = getRobot();

        v.findViewById(R.id.btnLeft).setOnClickListener(view -> sendActionToRobot("left"));
        v.findViewById(R.id.btnRight).setOnClickListener(view -> sendActionToRobot("right"));
        v.findViewById(R.id.btnSuck).setOnClickListener(view -> sendActionToRobot("suck"));
        v.findViewById(R.id.btnRaisePipette).setOnClickListener(view -> sendActionToRobot("raise_pipette"));
        v.findViewById(R.id.btnLowerPipette).setOnClickListener(view -> sendActionToRobot("lower_pipette"));
        v.findViewById(R.id.btnReset).setOnClickListener(view -> sendActionToRobot("reset"));

        if (robot == 1) {
            v.findViewById(R.id.btnUp).setOnClickListener(view -> sendActionToRobot("raise_pipette"));
            v.findViewById(R.id.btnDown).setOnClickListener(view -> sendActionToRobot("lower_pipette"));
            btnMulti1 = v.findViewById(R.id.btnMultifunctions1);
            btnMulti1.setOnClickListener(view -> sendActionToRobot("infrared_control"));
            btnMulti1.setText(R.string.txtInfraredControl);
            btnMulti1 = v.findViewById(R.id.btnMultifunctions2);
            btnMulti1.setOnClickListener(view -> sendActionToRobot("color"));
            btnMulti1.setText(R.string.txtReadColor);

        } else if (robot == 2) {
            v.findViewById(R.id.btnUp).setOnClickListener(view -> sendActionToRobot("forward"));
            v.findViewById(R.id.btnDown).setOnClickListener(view -> sendActionToRobot("backwards"));
            btnMulti1 = v.findViewById(R.id.btnMultifunctions1);
            btnMulti1.setOnClickListener(view -> sendActionToRobot("suck_liquid"));
            btnMulti1.setText(R.string.txtSuck);
            btnMulti1 = v.findViewById(R.id.btnMultifunctions2);
            btnMulti1.setOnClickListener(view -> sendActionToRobot("unsuck_liquid"));
            btnMulti1.setText(R.string.txtUnsuck);
        }

        robotConnectionViewModel = new ViewModelProvider(Login.getContext()).get(RobotConnectionViewModel.class);
        permissionsViewModel = new ViewModelProvider(Login.getContext()).get(PermissionsViewModel.class);
        interactWithRobotViewModel = new ViewModelProvider(Login.getContext()).get(InteractWithRobotViewModel.class);

        setHasOptionsMenu(true);
        checkRobotConnection();
        return v;
    }

    private int getRobot() {
        if (Login.getAdminLogged())
            return AdminLogin.getRobotAdmin();
        else
            return Login.getRobotUser();
    }

    private void checkRobotConnection() {
        robotConnectionViewModel.checkRobotConnection();
        if (init2 == 0) {
            robotConnectionViewModel.getCheckRobotConnectionResponse().observe(getViewLifecycleOwner(), response -> {
                try {
                    JSONObject responseObject = new JSONObject(response);
                    if (responseObject.getString("response").equals("robot-connection-failed")) {
                        robotConnected = false;
                        dialogWarningRobotNotConnected();
                    } else robotConnected = true;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });
        } init2++;
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
                    if (responseObject.getInt("robot") != robot) {
                        Toast.makeText(getContext(), R.string.txtDifferentRobot, Toast.LENGTH_LONG).show();
                    } else if (responseObject.getString("response").equals("true") && responseObject.getString("activity").equals("match")) executeAction();
                    else Toast.makeText(UserNavigationRobot2d.getContext(), R.string.txtNoPermissions, Toast.LENGTH_SHORT).show();
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
        else Toast.makeText(getContext(), R.string.txtRobotNotConnected, Toast.LENGTH_SHORT).show();
    }

    private void executeAction() {
        switch (interaction) {
            case "forward":
                interactWithRobotViewModel.sendInteraction("up");
                break;
            case "backwards":
                interactWithRobotViewModel.sendInteraction("down");
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
            case "suck_liquid":
                interactWithRobotViewModel.sendInteraction("suck_liquid");
                break;
            case "unsuck_liquid":
                interactWithRobotViewModel.sendInteraction("unsuck_liquid");
                break;
            case "reset":
                interactWithRobotViewModel.sendInteraction("reset");
                break;
            case "infrared_control":
                interactWithRobotViewModel.sendInteraction("infrared_control");
                break;
            case "color":
                interactWithRobotViewModel.sendInteraction("color");
                break;
        }
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        menu.findItem(R.id.help).setVisible(true);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.help)
            openHelpDialog();
        return super.onOptionsItemSelected(item);
    }

    private void openHelpDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.menu_interact)
                .setMessage(R.string.txtHelpInteract)
                .setPositiveButton(R.string.txtAccept, null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}