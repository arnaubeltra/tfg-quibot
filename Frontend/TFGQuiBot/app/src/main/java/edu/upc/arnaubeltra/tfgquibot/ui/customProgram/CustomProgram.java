package edu.upc.arnaubeltra.tfgquibot.ui.customProgram;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.upc.arnaubeltra.tfgquibot.R;
import edu.upc.arnaubeltra.tfgquibot.adapters.customProgram.CustomProgramAdapter;
import edu.upc.arnaubeltra.tfgquibot.adapters.customProgram.ItemMoveCallback;
import edu.upc.arnaubeltra.tfgquibot.models.Action;
import edu.upc.arnaubeltra.tfgquibot.ui.login.AdminLogin;
import edu.upc.arnaubeltra.tfgquibot.ui.login.Login;
import edu.upc.arnaubeltra.tfgquibot.ui.shared.viewModels.PermissionsViewModel;
import edu.upc.arnaubeltra.tfgquibot.ui.shared.viewModels.RobotConnectionViewModel;

public class CustomProgram extends Fragment implements CustomProgramAdapter.ICustomProgramRCVItemClicked, AdapterView.OnItemSelectedListener  {

    private static CustomProgram instance;

    private RecyclerView rcvCustomProgram;
    private CustomProgramAdapter customProgramAdapter;
    private ArrayList<Action> actionsList = new ArrayList<>();

    private TextView txtNoActionsCustomProgram, textViewStartSentence, textViewFinishSentence, textStartSentence2, textViewFinishSentence2;
    private Spinner spinner, spinnerSelectQuantity;
    private EditText inputInstructionsRepetition;

    private RobotConnectionViewModel robotConnectionViewModel;
    private PermissionsViewModel permissionsViewModel;
    private CustomProgramViewModel customProgramViewModel;

    private Boolean robotConnected = false;
    private int init = 0, init2 = 0;
    private int position = 0;
    private String typeAction = "";
    private int flag = 0, flag2 = 0;

    private int robot = 0;

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

        robot = getRobot();

        v.findViewById(R.id.btnSendCustomProgram).setOnClickListener(view -> setupSendCustomProgramToRobot());
        v.findViewById(R.id.btnAddActionCustomProgram).setOnClickListener(view -> openDialogNewAction(actionsList.size(),"new_action"));
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
                        if (flag == 1) { dialogWarningRobotNotConnected(); flag = 0; }
                        else flag = 1;
                    } else {
                        flag = 1;
                        if (Login.getAdminLogged()) onSendCustomProgram();
                        else {
                            setupPermissionsObserver();
                            if ((flag2 == 1) && init2 != 0) {
                                permissionsViewModel.checkUserPermissions(Login.getIpAddress(), "custom_program"); flag2++;
                            } else if ((flag2 == 2) && init2 != 0)
                                permissionsViewModel.checkUserPermissions(Login.getIpAddress(), "custom_program");
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

    public void openDialogNewAction(int index, String type) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_custom_program, null);

        spinner = view.findViewById(R.id.spinnerSelectPropertiesAndActions);
        textViewStartSentence = view.findViewById(R.id.textViewStartSentence);
        spinnerSelectQuantity = view.findViewById(R.id.spinnerSelectQuantity);
        textViewFinishSentence = view.findViewById(R.id.textViewFinishSentence);
        textStartSentence2 = view.findViewById(R.id.textStartSentence2);
        inputInstructionsRepetition = view.findViewById(R.id.inputInstructionsRepetition);
        textViewFinishSentence2 = view.findViewById(R.id.textViewFinishSentence2);
        textStartSentence2.setText(R.string.txtLast);
        textViewFinishSentence2.setText(R.string.txtActions);
        inputInstructionsRepetition.setText("0");

        builder.setView(view);

        position = index;

        if (type.equals("new_action")) builder.setTitle(R.string.txtTitleDialogCustomProgram);
        else builder.setTitle(R.string.txtTitleEditDialogCustomProgram);

        setupSpinner(index, type);
        typeAction = type;

        builder.setPositiveButton(R.string.txtButonSave, (dialogInterface, i) -> onAccept(type, index));
        builder.setNegativeButton(R.string.txtButonCancel, (dialogInterface, i) -> { });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void onAccept(String type, int index) {
        String action = String.valueOf(spinner.getSelectedItem());
        if (type.equals("new_action")) {
            if (action.equals(getResources().getString(R.string.txtSuckXMl)) || action.equals(getResources().getString(R.string.txtUnsuckXMl)))
                newActionAdded(action, Float.parseFloat(String.valueOf(spinnerSelectQuantity.getSelectedItem())), 0, 0);
            else if (action.equals(getResources().getString(R.string.txtRepeatPreviousActions))) {
                if (checkInputs(index))
                    newActionAdded(action, 0, Integer.parseInt(String.valueOf(spinnerSelectQuantity.getSelectedItem())), Integer.parseInt(inputInstructionsRepetition.getText().toString()));
            } else newActionAdded(action, 0, 0, 0);
        }
        else if (type.equals("edit_action")) {
            if (action.equals(getResources().getString(R.string.txtSuckXMl)) || action.equals(getResources().getString(R.string.txtUnsuckXMl)))
                editAction(index, action, Float.parseFloat(String.valueOf(spinnerSelectQuantity.getSelectedItem())), 0, 0);
            else if (action.equals(getResources().getString(R.string.txtRepeatPreviousActions))) {
                if (checkInputs(index))
                    editAction(index, action, 0, Integer.parseInt(String.valueOf(spinnerSelectQuantity.getSelectedItem())), Integer.parseInt(inputInstructionsRepetition.getText().toString()));
            } else editAction(index, action, 0, 0, 0);
        }
    }

    private boolean checkInputs(int index) {
        if (inputInstructionsRepetition.getText().toString().equals("")) {
            Toast.makeText(getContext(), R.string.txtValueCannotBeEmpty, Toast.LENGTH_SHORT).show();
            return false;
        } else if (Integer.parseInt(inputInstructionsRepetition.getText().toString()) > index) {
            Toast.makeText(getContext(), R.string.txtNotEnughInstructions, Toast.LENGTH_LONG).show();
            return false;
        } else if (Integer.parseInt(inputInstructionsRepetition.getText().toString()) <= 0) {
            Toast.makeText(getContext(), R.string.txtValueMustBeGreaterThanZero, Toast.LENGTH_SHORT).show();
            return false;
        } return true;
    }

    private void setupSpinner(int index, String type) {
        //List picker (spinner) configuration
        spinner.setOnItemSelectedListener(this);

        List<String> actions = new ArrayList<>();
        Log.d("TAG", "setupSpinner: " + robot);
        if (robot == 2)
            Collections.addAll(actions, getResources().getString(R.string.txtForward), getResources().getString(R.string.txtBackwards), getResources().getString(R.string.txtRight), getResources().getString(R.string.txtLeft), getResources().getString(R.string.txtLowerPipette), getResources().getString(R.string.txtRaisePipette), getResources().getString(R.string.txtSuck), getResources().getString(R.string.txtUnsuck), getResources().getString(R.string.txtSuckXMl), getResources().getString(R.string.txtUnsuckXMl),getResources().getString(R.string.txtRepeatPreviousActions));
        else if (robot == 1)
            Collections.addAll(actions, getResources().getString(R.string.txtRight), getResources().getString(R.string.txtLeft), getResources().getString(R.string.txtLowerPipette), getResources().getString(R.string.txtRaisePipette), getResources().getString(R.string.txtSuckUnsuck), getResources().getString(R.string.txtRepeatPreviousActions));

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, actions);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);

        if (type.equals("edit_action"))
            spinner.setSelection(dataAdapter.getPosition(actionsList.get(index).getName()));
    }

    private void setupSpinnerQuantity(String action) {
        spinnerSelectQuantity.setOnItemSelectedListener(this);

        textViewStartSentence.setVisibility(View.VISIBLE);
        textViewFinishSentence.setVisibility(View.VISIBLE);
        spinnerSelectQuantity.setVisibility(View.VISIBLE);

        List<String> quantity = new ArrayList<>();
        if (action.equals(getResources().getString(R.string.txtSuckXMl)) || action.equals(getResources().getString(R.string.txtUnsuckXMl))) {
            textViewStartSentence.setText(R.string.txtGetLiquidQuantity);
            Collections.addAll(quantity, "0.1", "0.2", "0.3", "0.4", "0.5", "0.6", "0.7", "0.8", "0.9", "1.0");
            textViewFinishSentence.setText(R.string.txtMl);
        }
        else if (action.equals(getResources().getString(R.string.txtRepeatPreviousActions))) {
            textViewStartSentence.setText(R.string.txtRepeatNTimes);
            Collections.addAll(quantity, "1", "2", "3", "4", "5");
            textViewFinishSentence.setText(R.string.txtTimes);
        }

        ArrayAdapter<String> quantityAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, quantity);
        quantityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSelectQuantity.setAdapter(quantityAdapter);

        if (typeAction.equals("edit_action")) {
            if (actionsList.get(position).getName().equals(getResources().getString(R.string.txtSuckXMl)) || actionsList.get(position).getName().equals(getResources().getString(R.string.txtUnsuckXMl)))
                spinnerSelectQuantity.setSelection(quantityAdapter.getPosition(Float.toString(actionsList.get(position).getQuantity())));
            else if (actionsList.get(position).getName().equals(getResources().getString(R.string.txtRepeatPreviousActions))) {
                spinnerSelectQuantity.setSelection(quantityAdapter.getPosition(String.valueOf(actionsList.get(position).getRepetitions())));
                inputInstructionsRepetition.setText(String.valueOf(actionsList.get(position).getLastNInstructions()));
            }
        }
    }

    private void newActionAdded(String action, float quantity, int repetitions, int repeatLast) {
        Action actionObj = new Action(action);
        if (action.equals(getResources().getString(R.string.txtSuckXMl)) || action.equals(getResources().getString(R.string.txtUnsuckXMl))) {
            actionObj.setName(action);
            actionObj.setQuantity(quantity);
        } else if (action.equals(getResources().getString(R.string.txtRepeatPreviousActions))) {
            actionObj.setName(action);
            actionObj.setRepetitions(repetitions);
            actionObj.setLastNInstructions(repeatLast);
        }
        actionsList.add(actionObj);
        customProgramAdapter.updateActionsList(actionsList);
        checkListIsEmpty();
    }

    private void editAction(int index, String action, float quantity, int repetitions, int repeatLast) {
        Action actionObj = actionsList.get(index);
        Action newAction = new Action(action);
        if (action.equals(getResources().getString(R.string.txtSuckXMl)) || action.equals(getResources().getString(R.string.txtUnsuckXMl))) {
            newAction.setQuantity(quantity);
        } else if (action.equals(getResources().getString(R.string.txtRepeatPreviousActions))) {
            newAction.setRepetitions(repetitions);
            newAction.setLastNInstructions(repeatLast);
        }
        actionsList.set(index, newAction);
        customProgramAdapter.updateActionsList(actionsList);
    }

    public void checkListIsEmpty() {
        if (customProgramAdapter.getItemCount() == 0) CustomProgram.getInstance().txtNoActionsCustomProgram.setVisibility(View.VISIBLE);
        else CustomProgram.getInstance().txtNoActionsCustomProgram.setVisibility(View.INVISIBLE);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
        String action = adapterView.getItemAtPosition(position).toString();
        switch (adapterView.getId()) {
            case R.id.spinnerSelectPropertiesAndActions:
                if (action.equals(getResources().getString(R.string.txtSuckXMl)) || action.equals(getResources().getString(R.string.txtUnsuckXMl))) {
                    setupSpinnerQuantity(action);
                    textStartSentence2.setVisibility(View.GONE);
                    inputInstructionsRepetition.setVisibility(View.GONE);
                    textViewFinishSentence2.setVisibility(View.GONE);
                } else if (action.equals(getResources().getString(R.string.txtRepeatPreviousActions))) {
                    setupSpinnerQuantity(action);
                    textStartSentence2.setVisibility(View.VISIBLE);
                    inputInstructionsRepetition.setVisibility(View.VISIBLE);
                    textViewFinishSentence2.setVisibility(View.VISIBLE);
                } else {
                    textViewStartSentence.setVisibility(View.GONE);
                    textViewFinishSentence.setVisibility(View.GONE);
                    spinnerSelectQuantity.setVisibility(View.GONE);
                    textStartSentence2.setVisibility(View.GONE);
                    inputInstructionsRepetition.setVisibility(View.GONE);
                    textViewFinishSentence2.setVisibility(View.GONE);
                }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) { }

    private void setupPermissionsObserver() {
        if (init == 0) {
            permissionsViewModel.checkUserPermissions(Login.getIpAddress(), "");
            permissionsViewModel.getUserPermissionsResponse().observe(getViewLifecycleOwner(), auth -> {
                try {
                    JSONObject responseObject = new JSONObject(auth);
                    if (responseObject.getInt("robot") != robot) Toast.makeText(getContext(), R.string.txtDifferentRobot, Toast.LENGTH_LONG).show();
                    else if (responseObject.getString("response").equals("true") && responseObject.getString("activity").equals("match")) onSendCustomProgram();
                    else Toast.makeText(getContext(), R.string.txtNoPermissions, Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });
        } init++;
    }

    private void setupSendCustomProgramToRobot() {
        flag = 1;
        robotConnectionViewModel.checkRobotConnection();
    }

    /*private void sendCustomProgramToRobot() {
        if (robotConnected) {
            setupPermissionsObserver();
            if (Login.getAdminLogged()) onSendCustomProgram();
            else permissionsViewModel.checkUserPermissions(Login.getIpAddress(), "custom_program");
        }
    }*/

    private void onSendCustomProgram() {
        if (actionsList.size() != 0) {
            setupCustomProgramResponseListener();
            String programmedActions = parseActions();
            if (!programmedActions.equals("parseError"))
                customProgramViewModel.onSendListActions(programmedActions);
        } else Toast.makeText(getContext(), R.string.txtNoProgramToSend, Toast.LENGTH_SHORT).show();
    }

    private void setupCustomProgramResponseListener() {
        if (init2 == 0) {
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
        } init2++;
    }

    private String parseActions() {
        StringBuilder parsedActionsList = new StringBuilder();
        for (int i = 0; i < actionsList.size(); i++) {
            if (actionsList.get(i).getName().equals(getResources().getString(R.string.txtRepeatPreviousActions))) {
                int repetitions = actionsList.get(i).getRepetitions();
                int nInstructions = actionsList.get(i).getLastNInstructions();

                    for (int rep = 0; rep < repetitions; rep ++) {
                        try {
                            for (int nIns = i-nInstructions; nIns < i; nIns++) {
                                parsedActionsList.append(translator(actionsList.get(nIns).getName(), nIns));
                                parsedActionsList.append(",");
                            }
                        } catch (Exception e) {
                            Toast.makeText(getContext(), R.string.txtErrorLoop, Toast.LENGTH_SHORT).show();
                            return "parseError";
                        }
                    }
            } else {
                parsedActionsList.append(translator(actionsList.get(i).getName(), i));
                parsedActionsList.append(",");
            }
        }
        return parsedActionsList.toString();
    }

    private String translator(String instruction, int i) {
        if (instruction.equals(getResources().getString(R.string.txtForward))) return "up";
        else if (instruction.equals(getResources().getString(R.string.txtBackwards))) return "down";
        else if (instruction.equals(getResources().getString(R.string.txtRight))) return "right";
        else if (instruction.equals(getResources().getString(R.string.txtLeft))) return "left";
        else if (instruction.equals(getResources().getString(R.string.txtLowerPipette))) return "lower_pipette";
        else if (instruction.equals(getResources().getString(R.string.txtRaisePipette))) return "raise_pipette";
        else if (instruction.equals(getResources().getString(R.string.txtSuck)) || instruction.equals(getResources().getString(R.string.txtSuckUnsuck))) return "suck";
        else if (instruction.equals(getResources().getString(R.string.txtUnsuck))) return "unsuck";
        else if (instruction.equals(getResources().getString(R.string.txtSuckXMl))) return "suck_" + actionsList.get(i).getQuantity();
        else if (instruction.equals(getResources().getString(R.string.txtUnsuckXMl))) return "unsuck_" + actionsList.get(i).getQuantity();
        return "";
    }

    @Override
    public void onUserClicked(int index) {
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
        builder.setTitle(R.string.menu_custom_program)
                .setMessage(R.string.txtHelpCustomProgram)
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