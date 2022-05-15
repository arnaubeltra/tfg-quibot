package edu.upc.arnaubeltra.tfgquibot.ui.experiments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import edu.upc.arnaubeltra.tfgquibot.R;
import edu.upc.arnaubeltra.tfgquibot.UserNavigation;
import edu.upc.arnaubeltra.tfgquibot.ui.login.Login;
import edu.upc.arnaubeltra.tfgquibot.ui.shared.viewModels.PermissionsViewModel;
import edu.upc.arnaubeltra.tfgquibot.ui.shared.viewModels.RobotConnectionViewModel;


public class Experiments extends Fragment {

    private RobotConnectionViewModel robotConnectionViewModel;
    private PermissionsViewModel permissionsViewModel;
    private ExperimentsViewModel experimentsViewModel;

    private Boolean robotConnected = false;
    private String experimentName = "";
    private int init = 0;

    // Required empty public constructor
    public Experiments() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_experiments, container, false);

        v.findViewById(R.id.btnExecExperimentSeriesDisolucio).setOnClickListener(view -> sendExecutionExperiment(getResources().getString(R.string.titleSeriesDisolucio)));
        v.findViewById(R.id.btnExecExperimentBarrejaColors).setOnClickListener(view -> sendExecutionExperiment(getResources().getString(R.string.titleBarrejaColorsPrimaris)));
        v.findViewById(R.id.btnExecExperimentCapesDensitat).setOnClickListener(view -> sendExecutionExperiment(getResources().getString(R.string.titleCapesDeDensitat)));

        v.findViewById(R.id.btnBoardtSeriesDisolucio).setOnClickListener(view -> dialogHowToPrepareExperiment(getResources().getString(R.string.titleSeriesDisolucio)));
        v.findViewById(R.id.btnBoardBarrejaColors).setOnClickListener(view -> dialogHowToPrepareExperiment(getResources().getString(R.string.titleBarrejaColorsPrimaris)));
        v.findViewById(R.id.btnBoardCapesDensitat).setOnClickListener(view -> dialogHowToPrepareExperiment(getResources().getString(R.string.titleCapesDeDensitat)));

        robotConnectionViewModel = new ViewModelProvider(Login.getContext()).get(RobotConnectionViewModel.class);
        permissionsViewModel = new ViewModelProvider(Login.getContext()).get(PermissionsViewModel.class);
        experimentsViewModel = new ViewModelProvider(Login.getContext()).get(ExperimentsViewModel.class);

        checkRobotConnection();
        return v;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    private void checkRobotConnection() {
        robotConnectionViewModel.checkRobotConnection();
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
                    if (responseObject.getString("response").equals("true") && responseObject.getString("activity").equals("match")) executeExperiment();
                    else Toast.makeText(UserNavigation.getContext(), R.string.txtNoPermissions, Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });
        } init++;
    }

    private void sendExecutionExperiment(String experiment) {
        if (robotConnected) {
            setupPermissionsObserver();
            experimentName = experiment;
            if (Login.getAdminLogged()) executeExperiment();
            else permissionsViewModel.checkUserPermissions(Login.getIpAddress(), "experiments");
        }
    }

    private void executeExperiment() {
        if (experimentName.equals(getResources().getString(R.string.titleSeriesDisolucio)))
            experimentsViewModel.startExperiment("series_de_dissolucio");
        else if (experimentName.equals(getResources().getString(R.string.titleBarrejaColorsPrimaris)))
            experimentsViewModel.startExperiment("barreja_colors");
        else if (experimentName.equals(getResources().getString(R.string.titleCapesDeDensitat)))
            experimentsViewModel.startExperiment("capes_de_densitat");
    }

    private void dialogHowToPrepareExperiment(String experimentName) {          /*Todo: change images*/
        Dialog dialog = new Dialog(getActivity());
        dialog.setCancelable(true);
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_experiments, null);

        ImageView image = view.findViewById(R.id.imgBoardExperiment);

        if (experimentName.equals(getResources().getString(R.string.titleSeriesDisolucio)))
            image.setImageResource(R.drawable.quibot_bg_light);
        else if (experimentName.equals(getResources().getString(R.string.titleBarrejaColorsPrimaris)))
            image.setImageResource(R.drawable.quibot_bg_light);
        else if (experimentName.equals(getResources().getString(R.string.titleCapesDeDensitat)))
            image.setImageResource(R.drawable.quibot_bg_light);

        dialog.setContentView(view);
        dialog.show();
    }
}