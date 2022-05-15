package edu.upc.arnaubeltra.tfgquibot.ui.customProgram;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.upc.arnaubeltra.tfgquibot.R;
import edu.upc.arnaubeltra.tfgquibot.UserNavigation;
import edu.upc.arnaubeltra.tfgquibot.adapters.customProgram.CustomProgramAdapter;
import edu.upc.arnaubeltra.tfgquibot.adapters.customProgram.ItemMoveCallback;
import edu.upc.arnaubeltra.tfgquibot.ui.login.Login;
import edu.upc.arnaubeltra.tfgquibot.ui.shared.BoardSize;
import edu.upc.arnaubeltra.tfgquibot.ui.shared.viewModels.PermissionsViewModel;
import edu.upc.arnaubeltra.tfgquibot.ui.shared.viewModels.RobotConnectionViewModel;

public class CustomProgram extends Fragment implements CustomProgramAdapter.ICustomProgramRCVItemClicked, AdapterView.OnItemSelectedListener  {

    private static CustomProgram instance;

    private RecyclerView rcvCustomProgram;
    private CustomProgramAdapter customProgramAdapter;
    private ArrayList<String> actionsList = new ArrayList<>();

    public TextView txtNoActionsCustomProgram;

    private RobotConnectionViewModel robotConnectionViewModel;
    private PermissionsViewModel permissionsViewModel;
    private CustomProgramViewModel customProgramViewModel;

    private Boolean robotConnected = false;
    private int init = 0;

    // Required empty public constructor
    public CustomProgram() { }

    public static CustomProgram getInstance() {
        return instance;
    }
    public Context getCustomProgramContext() {
        return getContext();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        instance = this;
        View v = inflater.inflate(R.layout.fragment_custom_program, container, false);

        v.findViewById(R.id.btnSendCustomProgram).setOnClickListener(view -> sendCustomProgramToRobot());
        v.findViewById(R.id.btnAddActionCustomProgram).setOnClickListener(view -> openDialogNewAction(0,""));
        txtNoActionsCustomProgram = v.findViewById(R.id.txtNoActionsCustomProgram);

        rcvCustomProgram = v.findViewById(R.id.rcvCustomProgram);
        customProgramAdapter = new CustomProgramAdapter(this);

        ItemTouchHelper.Callback callback = new ItemMoveCallback(customProgramAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(rcvCustomProgram);

        rcvCustomProgram.setLayoutManager(new LinearLayoutManager(getActivity()));
        rcvCustomProgram.setAdapter(customProgramAdapter);

        robotConnectionViewModel = new ViewModelProvider(Login.getContext()).get(RobotConnectionViewModel.class);
        permissionsViewModel = new ViewModelProvider(Login.getContext()).get(PermissionsViewModel.class);
        customProgramViewModel = new ViewModelProvider(Login.getContext()).get(CustomProgramViewModel.class);

        checkRobotConnection();
        return v;
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

    public void openDialogNewAction(int index, String value) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_with_spinner, null);

        builder.setView(view);
        if (value.equals("")) builder.setTitle(R.string.txtTitleDialogCustomProgram);
        else builder.setTitle(R.string.txtTitleEditDialogCustomProgram);

        Spinner spinner = setupSpinner(view, value);

        builder.setPositiveButton(R.string.txtButonSave, (dialogInterface, i) -> {
            if (value.equals("")) newActionAdded(String.valueOf(spinner.getSelectedItem()));
            else editAction(index, String.valueOf(spinner.getSelectedItem()));
        });
        builder.setNegativeButton(R.string.txtButonCancel, (dialogInterface, i) -> { });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private Spinner setupSpinner(View view, String value) {
        //List picker (spinner) configuration
        Spinner spinner = view.findViewById(R.id.spinnerSelectPropertiesAndActions);
        spinner.setOnItemSelectedListener(this);

        List<String> actions = new ArrayList<>();
        Collections.addAll(actions, getResources().getString(R.string.txtForward), getResources().getString(R.string.txtBackwards), getResources().getString(R.string.txtRight), getResources().getString(R.string.txtLeft), getResources().getString(R.string.txtLowerPipette), getResources().getString(R.string.txtRaisePipette), getResources().getString(R.string.txtActionPipette), getResources().getString(R.string.txtRepeatPreviousActions));

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, actions);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);

        if (!value.equals(""))
            spinner.setSelection(dataAdapter.getPosition(value));
        return spinner;
    }

    private void newActionAdded(String optionSelected) {
        actionsList.add(optionSelected);
        customProgramAdapter.updateActionsList(actionsList);
        checkListIsEmpty();
    }

    private void editAction(int index, String value) {
        actionsList.set(index, value);
        customProgramAdapter.updateActionsList(actionsList);
    }

    public void checkListIsEmpty() {
        if (customProgramAdapter.getItemCount() == 0) CustomProgram.getInstance().txtNoActionsCustomProgram.setVisibility(View.VISIBLE);
        else CustomProgram.getInstance().txtNoActionsCustomProgram.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) { }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) { }

    private void setupPermissionsObserver() {
        if (init == 0) {
            permissionsViewModel.checkUserPermissions(Login.getIpAddress(), "");
            permissionsViewModel.getUserPermissionsResponse().observe(getViewLifecycleOwner(), auth -> {
                Log.d("TAG", "setupPermissionsObserver: " + auth);
                try {
                    JSONObject responseObject = new JSONObject(auth);
                    if (responseObject.getString("response").equals("true") && responseObject.getString("activity").equals("match")) onSendCustomProgram();
                    else Toast.makeText(UserNavigation.getContext(), R.string.txtNoPermissions, Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });
        } init++;
    }

    private void sendCustomProgramToRobot() {
        if (robotConnected) {
            setupPermissionsObserver();
            if (Login.getAdminLogged()) onSendCustomProgram();
            else permissionsViewModel.checkUserPermissions(Login.getIpAddress(), "custom_program");
        }
    }

    private void onSendCustomProgram() {
        if (actionsList.size() != 0) {
            setupCustomProgramResponseListener();
            customProgramViewModel.onSendListActions(parseActions());
        } else Toast.makeText(getContext(), R.string.txtNoProgramToSend, Toast.LENGTH_SHORT).show();
    }

    private void setupCustomProgramResponseListener() {
        if (init == 0) {
            customProgramViewModel.onSendListActions("");
            customProgramViewModel.getSendListActionsRequestResponse().observe(getViewLifecycleOwner(), response -> {
                try {
                    JSONObject responseObject = new JSONObject(response);
                    if (responseObject.getString("response").equals("custom-program-actions-success"))
                        Toast.makeText(getContext(), R.string.txtProgramSentCorrectly, Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(getContext(), R.string.txtProgramError, Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });
        } init++;
    }

    private String parseActions() {
        String parsedActionsList = "";
        for (int i = 0; i < actionsList.size(); i++) {
            if (actionsList.get(i).equals(getResources().getString(R.string.txtForward))) parsedActionsList += "up";
            else if (actionsList.get(i).equals(getResources().getString(R.string.txtBackwards))) parsedActionsList += "down";
            else if (actionsList.get(i).equals(getResources().getString(R.string.txtRight))) parsedActionsList += "right";
            else if (actionsList.get(i).equals(getResources().getString(R.string.txtLeft))) parsedActionsList += "left";
            else if (actionsList.get(i).equals(getResources().getString(R.string.txtLowerPipette))) parsedActionsList += "lower_pipette";
            else if (actionsList.get(i).equals(getResources().getString(R.string.txtRaisePipette))) parsedActionsList += "raise_pipette";
            else if (actionsList.get(i).equals(getResources().getString(R.string.txtActionPipette))) parsedActionsList += "suck";
            parsedActionsList += ",";
        }
        return parsedActionsList;
    }

    @Override
    public void onUserClicked(int index) {
    }
}