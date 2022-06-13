package edu.upc.arnaubeltra.tfgquibot.ui.experiments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import edu.upc.arnaubeltra.tfgquibot.R;
import edu.upc.arnaubeltra.tfgquibot.ui.login.AdminLogin;
import edu.upc.arnaubeltra.tfgquibot.ui.login.Login;
import edu.upc.arnaubeltra.tfgquibot.ui.shared.viewModels.PermissionsViewModel;
import edu.upc.arnaubeltra.tfgquibot.ui.shared.viewModels.RobotConnectionViewModel;


// Class that defines the fragment of the Experiments activity
public class Experiments extends Fragment {

    private RobotConnectionViewModel robotConnectionViewModel;
    private PermissionsViewModel permissionsViewModel;
    private ExperimentsViewModel experimentsViewModel;

    // Variable to set the selected experiment.
    private String experimentName = "";

    // Definition of variables that handle some states (some of them caused because receiving doubled responses, so to control the flow)
    private int init = 0, init2 = 0, robot = 0, flag = 0, flag2 = 0;


    // Fragments require an empty constructor.
    public Experiments() { }

    // Method that creates the fragment.
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    // Method that creates the view of the fragment, defining all the elements of the layout and calling important methods to handle status.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_experiments, container, false);

        // Creation of the ViewModel objects.
        robotConnectionViewModel = new ViewModelProvider(Login.getContext()).get(RobotConnectionViewModel.class);
        permissionsViewModel = new ViewModelProvider(Login.getContext()).get(PermissionsViewModel.class);
        experimentsViewModel = new ViewModelProvider(Login.getContext()).get(ExperimentsViewModel.class);

        // As this fragment is used for Robot 1D and Robot 2D, we have to know which robot we are using to adapt the layout.
        robot = getRobot();

        // Definition of the elements of the activity and call to methods to perform actions.
        v.findViewById(R.id.btnExecExperimentSeriesDisolucio).setOnClickListener(view -> setupExperiment(getResources().getString(R.string.titleSeriesDisolucio)));
        v.findViewById(R.id.btnExecExperimentBarrejaColors).setOnClickListener(view -> setupExperiment(getResources().getString(R.string.titleBarrejaColorsPrimaris)));
        v.findViewById(R.id.btnExecExperimentCapesDensitat).setOnClickListener(view -> setupExperiment(getResources().getString(R.string.titleCapesDeDensitat)));
        v.findViewById(R.id.btnBoardtSeriesDisolucio).setOnClickListener(view -> dialogHowToPrepareExperiment(getResources().getString(R.string.titleSeriesDisolucio)));
        v.findViewById(R.id.btnBoardBarrejaColors).setOnClickListener(view -> dialogHowToPrepareExperiment(getResources().getString(R.string.titleBarrejaColorsPrimaris)));
        v.findViewById(R.id.btnBoardCapesDensitat).setOnClickListener(view -> dialogHowToPrepareExperiment(getResources().getString(R.string.titleCapesDeDensitat)));

        checkRobotConnection();
        setHasOptionsMenu(true);
        return v;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
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
        robotConnectionViewModel.resetLiveData();
    }

    private void checkRobotConnection() {
        robotConnectionViewModel.checkRobotConnection();
        robotConnectionViewModel.getCheckRobotConnectionResponse().observe(getViewLifecycleOwner(), response -> {
            try {
                JSONObject responseObject = new JSONObject(response);
                if (responseObject.getString("response").equals("robot-connection-failed")) dialogWarningRobotNotConnected();
                else {
                    if (Login.getAdminLogged() && (flag2 > 0)) executeExperiment();
                    else {
                        setupPermissionsObserver();
                        permissionsViewModel.checkUserPermissions(Login.getIpAddress(), "experiments");
                    }
                }
            } catch (JSONException e) { e.printStackTrace(); }
        });
    }

    // Method to check if the robot is connected. All the use of flags is due to multiple responses that affected the flow of the program...
    /*private void checkRobotConnection() {
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
                        if (Login.getAdminLogged()) executeExperiment();
                        else {
                            setupPermissionsObserver();
                            if ((flag2 == 1) && init2 != 0) {
                                permissionsViewModel.checkUserPermissions(Login.getIpAddress(), "experiments"); flag2++;
                            } else if ((flag2 == 2) && init2 != 0)
                                permissionsViewModel.checkUserPermissions(Login.getIpAddress(), "experiments");
                            else flag2++;
                        }
                    } init2++;
                } catch (JSONException e) { e.printStackTrace(); }
            });
        }
    }*/

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
                    if (responseObject.getInt("robot") != robot)
                        Toast.makeText(getContext(), R.string.txtDifferentRobot, Toast.LENGTH_LONG).show();
                    else if (responseObject.getString("response").equals("true") && responseObject.getString("activity").equals("match"))
                        executeExperiment();
                    else {
                        if (init != 0) Toast.makeText(getContext(), R.string.txtNoPermissions, Toast.LENGTH_SHORT).show();
                    } init++;
                } catch (JSONException e) { e.printStackTrace(); }
            });
        }
    }

    // Method that checks if robot is connected when a new experiment has to be performed.
    private void setupExperiment(String experiment) {
        flag = 1;
        robotConnectionViewModel.checkRobotConnection();
        experimentName = experiment;
    }

    // Executes an experiment when the method is called, using the experimentName variable to know which one to execute.
    private void executeExperiment() {
        if (experimentName.equals(getResources().getString(R.string.titleSeriesDisolucio)))
            experimentsViewModel.startExperiment("series_de_dissolucio");
        else if (experimentName.equals(getResources().getString(R.string.titleBarrejaColorsPrimaris)))
            experimentsViewModel.startExperiment("barreja_colors");
        else if (experimentName.equals(getResources().getString(R.string.titleCapesDeDensitat)))
            experimentsViewModel.startExperiment("capes_de_densitat");
    }

    // Dialog that shows the image of each experiment. Changes according to each robot.
    private void dialogHowToPrepareExperiment(String experimentName) {
        Dialog dialog = new Dialog(getActivity());
        dialog.setCancelable(true);
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_experiments, null);

        ImageView image = view.findViewById(R.id.imgBoardExperiment);
        if (experimentName.equals(getResources().getString(R.string.titleSeriesDisolucio))) {
            if (robot == 1)
                image.setImageResource(R.drawable.experiment_series_dissolucio);
            else if (robot == 2)
                image.setImageResource(R.drawable.board_series_dissolucio);
        } else if (experimentName.equals(getResources().getString(R.string.titleBarrejaColorsPrimaris))) {
            if (robot == 1)
                image.setImageResource(R.drawable.experiment_barreja_colors);
            else if (robot == 2)
                image.setImageResource(R.drawable.board_barreja_colors_primaris);
        } else if (experimentName.equals(getResources().getString(R.string.titleCapesDeDensitat))) {
            if (robot == 1)
                image.setImageResource(R.drawable.experiment_capes_de_densitat);
            else if (robot == 2)
                image.setImageResource(R.drawable.board_capes_de_densitat);
        }

        dialog.setContentView(view);
        dialog.show();
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

    // Opens a dialog that explains how to use the Experiments activity.
    private void openHelpDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.menu_experiments)
                .setMessage(R.string.txtHelpExperiments)
                .setPositiveButton(R.string.txtAccept, null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}