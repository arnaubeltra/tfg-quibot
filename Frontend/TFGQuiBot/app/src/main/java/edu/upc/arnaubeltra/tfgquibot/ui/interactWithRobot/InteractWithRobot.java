package edu.upc.arnaubeltra.tfgquibot.ui.interactWithRobot;

import android.app.AlertDialog;
import android.app.Dialog;
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
import android.widget.ImageView;
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

    private String interaction = "";
    private int init = 0, init2 = 0;
    private int robot = 0;
    private int flag = 0, flag2 = 0;

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

        Button btnMulti1, btnMulti2, btnReset1, btnReset2, btnColor;

        v.findViewById(R.id.btnLeft).setOnClickListener(view -> setupInteraction("left"));
        v.findViewById(R.id.btnRight).setOnClickListener(view -> setupInteraction("right"));
        v.findViewById(R.id.btnSuck).setOnClickListener(view -> setupInteraction("suck"));
        v.findViewById(R.id.btnRaisePipette).setOnClickListener(view -> setupInteraction("raise_pipette"));
        v.findViewById(R.id.btnLowerPipette).setOnClickListener(view -> setupInteraction("lower_pipette"));

        if (robot == 1) {
            v.findViewById(R.id.btnUp).setOnClickListener(view -> setupInteraction("raise_pipette"));
            v.findViewById(R.id.btnDown).setOnClickListener(view -> setupInteraction("lower_pipette"));
            btnMulti1 = v.findViewById(R.id.btnMultifunctions1);
            btnMulti1.setVisibility(View.GONE);
            btnMulti2 = v.findViewById(R.id.btnMultifunctions2);
            btnMulti2.setVisibility(View.GONE);
            btnColor = v.findViewById(R.id.btnReadColor);
            btnColor.setOnClickListener(view -> setupInteraction("color"));
            btnColor.setVisibility(View.VISIBLE);
            btnReset2 = v.findViewById(R.id.btnReset2);
            btnReset2.setOnClickListener(view -> setupInteraction("reset"));
            btnReset2.setVisibility(View.VISIBLE);
            btnReset1 = v.findViewById(R.id.btnReset);
            btnReset1.setVisibility(View.GONE);
        } else if (robot == 2) {
            v.findViewById(R.id.btnUp).setOnClickListener(view -> setupInteraction("forward"));
            v.findViewById(R.id.btnDown).setOnClickListener(view -> setupInteraction("backwards"));
            btnMulti1 = v.findViewById(R.id.btnMultifunctions1);
            btnMulti1.setOnClickListener(view -> setupInteraction("suck_liquid"));
            btnMulti1.setText(R.string.txtSuck);
            btnMulti1 = v.findViewById(R.id.btnMultifunctions2);
            btnMulti1.setOnClickListener(view -> setupInteraction("unsuck_liquid"));
            btnMulti1.setText(R.string.txtUnsuck);
            btnColor = v.findViewById(R.id.btnReadColor);
            btnColor.setVisibility(View.GONE);
            btnReset2 = v.findViewById(R.id.btnReset2);
            btnReset2.setVisibility(View.GONE);
            btnReset1 = v.findViewById(R.id.btnReset);
            btnReset1.setOnClickListener(view -> setupInteraction("reset"));
            btnReset1.setVisibility(View.VISIBLE);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        permissionsViewModel.resetLiveData();
    }

    private void checkRobotConnection() {
        robotConnectionViewModel.checkRobotConnection();
        if (init2 == 0) {
            robotConnectionViewModel.getCheckRobotConnectionResponse().observe(getViewLifecycleOwner(), response -> {
                try {
                    JSONObject responseObject = new JSONObject(response);
                    if (responseObject.getString("response").equals("robot-connection-failed")) {
                        if (flag == 1) { dialogWarningRobotNotConnected(); flag = 0; }
                        else flag = 1;
                    } else {
                        flag = 1;
                        if (Login.getAdminLogged()) executeAction();
                        else {
                            setupPermissionsObserver();
                            if ((flag2 == 1) && init2 != 0) {
                                permissionsViewModel.checkUserPermissions(Login.getIpAddress(), "interact"); flag2++;
                            } else if ((flag2 == 2) && init2 != 0)
                                permissionsViewModel.checkUserPermissions(Login.getIpAddress(), "interact");
                            else flag2++;
                        }
                    } init2++;
                } catch (JSONException e) { e.printStackTrace(); }
            });
        }
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
                    if (responseObject.getInt("robot") != robot) Toast.makeText(getContext(), R.string.txtDifferentRobot, Toast.LENGTH_LONG).show();
                    else if (responseObject.getString("response").equals("true") && responseObject.getString("activity").equals("match")) executeAction();
                    else Toast.makeText(getContext(), R.string.txtNoPermissions, Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });
        } init++;
    }

    private void setupInteraction(String action) {
        flag = 1;
        robotConnectionViewModel.checkRobotConnection();
        interaction = action;
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
                .setPositiveButton(R.string.txtAccept, null)
                .setNeutralButton(R.string.txtBoard, (dialogInterface, i) -> openBoardDialog());
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void openBoardDialog() {
        Dialog dialog = new Dialog(getActivity());
        dialog.setCancelable(true);
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_how_to_play, null);
        ImageView image = view.findViewById(R.id.imgHowToPlay);
        image.setImageResource(R.drawable.board_medium);
        dialog.setContentView(view);
        dialog.show();
    }

}