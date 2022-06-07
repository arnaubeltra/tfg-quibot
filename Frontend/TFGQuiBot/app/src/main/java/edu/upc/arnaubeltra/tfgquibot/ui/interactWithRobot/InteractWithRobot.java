package edu.upc.arnaubeltra.tfgquibot.ui.interactWithRobot;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

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
import edu.upc.arnaubeltra.tfgquibot.ui.login.AdminLogin;
import edu.upc.arnaubeltra.tfgquibot.ui.login.Login;
import edu.upc.arnaubeltra.tfgquibot.ui.shared.viewModels.PermissionsViewModel;
import edu.upc.arnaubeltra.tfgquibot.ui.shared.viewModels.RobotConnectionViewModel;

// Class that defines the fragment of the InteractWithRobot activity
public class InteractWithRobot extends Fragment {

    private RobotConnectionViewModel robotConnectionViewModel;
    private PermissionsViewModel permissionsViewModel;
    private InteractWithRobotViewModel interactWithRobotViewModel;

    // Variable to define interaction that the robot has to perform
    private String interaction = "";

    // Definition of variables that handle some states (some of them caused because receiving doubled responses, so to control the flow)
    private int init = 0, init2 = 0, robot = 0, flag = 0, flag2 = 0;


    // Fragments require an empty constructor.
    public InteractWithRobot() { }

    // Method to create the fragment.
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    // Method that creates the view of the fragment, defining all the elements of the layout and calling important methods to handle status.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_interact_with_robot, container, false);

        // Creation of the ViewModel objects.
        robotConnectionViewModel = new ViewModelProvider(Login.getContext()).get(RobotConnectionViewModel.class);
        permissionsViewModel = new ViewModelProvider(Login.getContext()).get(PermissionsViewModel.class);
        interactWithRobotViewModel = new ViewModelProvider(Login.getContext()).get(InteractWithRobotViewModel.class);

        // As this fragment is used for Robot 1D and Robot 2D, we have to know which robot we are using to adapt the layout.
        robot = getRobot();

        // Definition of the layout according to the robot that we are using.
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
            v.findViewById(R.id.btnUp).setOnClickListener(view -> setupInteraction("up"));
            v.findViewById(R.id.btnDown).setOnClickListener(view -> setupInteraction("down"));
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

        setHasOptionsMenu(true);
        checkRobotConnection();
        return v;
    }

    // Method that gets the actual robot, selected when logging in.
    private int getRobot() {
        if (Login.getAdminLogged())
            return AdminLogin.getRobotAdmin();
        else
            return Login.getRobotUser();
    }

    // When the view is destroyed, resets the live data.
    @Override
    public void onDestroy() {
        super.onDestroy();
        permissionsViewModel.resetLiveData();
    }

    // Method to check if the robot is connected. All the use of flags is due to multiple responses that affected the flow of the program...
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

    // Method that shows a dialog if the robot is not connected.
    private void dialogWarningRobotNotConnected() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.txtRobotNotConnected)
                .setMessage(R.string.txtCheckRobotConnection)
                .setPositiveButton(R.string.txtAccept, null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // Setups the observer that will receive all the responses of the requests done when checking user permissions.
    private void setupPermissionsObserver() {
        if (init == 0) {
            permissionsViewModel.checkUserPermissions(Login.getIpAddress(), "");
            permissionsViewModel.getUserPermissionsResponse().observe(getViewLifecycleOwner(), auth -> {
                try {
                    JSONObject responseObject = new JSONObject(auth);
                    if (responseObject.getInt("robot") != robot) Toast.makeText(getContext(), R.string.txtDifferentRobot, Toast.LENGTH_LONG).show();
                    else if (responseObject.getString("response").equals("true") && responseObject.getString("activity").equals("match")) executeAction();
                    else Toast.makeText(getContext(), R.string.txtNoPermissions, Toast.LENGTH_SHORT).show();
                } catch (JSONException e) { e.printStackTrace(); }
            });
        } init++;
    }

    // Method that is called when an interaction has to be sent to robot, to check if robot is connected.
    private void setupInteraction(String action) {
        flag = 1;
        robotConnectionViewModel.checkRobotConnection();
        interaction = action;
    }

    // Sends action to be executed to the robot.
    private void executeAction() {
        interactWithRobotViewModel.sendInteraction(interaction);
    }

    // Changes the help menu item visibility to true.
    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        menu.findItem(R.id.help).setVisible(true);
        super.onPrepareOptionsMenu(menu);
    }

    // When the help menu item is clicked, opens help dialog.
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.help)
            openHelpDialog();
        return super.onOptionsItemSelected(item);
    }

    // Opens a dialog that explains how to use the Interact with robot activity.
    private void openHelpDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.menu_interact)
                .setMessage(R.string.txtHelpInteract)
                .setPositiveButton(R.string.txtAccept, null);
        if (robot == 2)
            builder.setNeutralButton(R.string.txtBoard, (dialogInterface, i) -> openBoardDialog());
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // Opens a dialog with a picture of the board.
    private void openBoardDialog() {
        Dialog dialog = new Dialog(getActivity());
        dialog.setCancelable(true);
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_how_to_play, null);
        ImageView image = view.findViewById(R.id.imgHowToPlay);
        image.setImageResource(R.drawable.board_empty_medium);
        dialog.setContentView(view);
        dialog.show();
    }

}