package edu.upc.arnaubeltra.tfgquibot.ui.customProgram;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
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

public class CustomProgram extends Fragment implements CustomProgramAdapter.ICustomProgramRCVItemClicked, AdapterView.OnItemSelectedListener  {

    private RecyclerView rcvCustomProgram;
    private CustomProgramAdapter customProgramAdapter;

    public TextView txtNoActionsCustomProgram;

    private static CustomProgram instance;

    private CustomProgramViewModel customProgramViewModel;

    ArrayList<String> actionsList = new ArrayList<>();

    public CustomProgram() { }

    public static CustomProgram getInstance() {
        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        instance = this;
        View v = inflater.inflate(R.layout.fragment_custom_program, container, false);

        v.findViewById(R.id.btnSendCustomProgram).setOnClickListener(view -> onSendCustomProgram());
        v.findViewById(R.id.btnAddActionCustomProgram).setOnClickListener(view -> openDialogNewAction(0,""));

        rcvCustomProgram = v.findViewById(R.id.rcvCustomProgram);

        customProgramAdapter = new CustomProgramAdapter(this);

        ItemTouchHelper.Callback callback = new ItemMoveCallback(customProgramAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(rcvCustomProgram);

        rcvCustomProgram.setLayoutManager(new LinearLayoutManager(getActivity()));
        rcvCustomProgram.setAdapter(customProgramAdapter);

        txtNoActionsCustomProgram = v.findViewById(R.id.txtNoActionsCustomProgram);

        customProgramViewModel = new ViewModelProvider(Login.getContext()).get(CustomProgramViewModel.class);
        setupCustomProgramResponseListener();

        return v;
    }

    public void openDialogNewAction(int index, String value) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_custom_program, null);

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
        Spinner spinner = view.findViewById(R.id.spinnerSelectActions);
        spinner.setOnItemSelectedListener(this);

        List<String> actions = new ArrayList<>();
        Collections.addAll(actions, getResources().getString(R.string.txtForward), getResources().getString(R.string.txtBackwards), getResources().getString(R.string.txtRight), getResources().getString(R.string.txtLeft), getResources().getString(R.string.txtLowerPipette), getResources().getString(R.string.txtRaisePipette), getResources().getString(R.string.txtActionPipette));

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(UserNavigation.getContext(), android.R.layout.simple_spinner_item, actions);
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
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
        String item = adapterView.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    private void onSendCustomProgram() {
        if (actionsList.size() != 0) customProgramViewModel.onSendListActions(parseActions());
        else Toast.makeText(UserNavigation.getContext(), R.string.txtNoProgramToSend, Toast.LENGTH_SHORT).show();
    }

    private void setupCustomProgramResponseListener() {
        customProgramViewModel.onSendListActions("");
        customProgramViewModel.getSendListActionsRequestResponse().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String response) {
                try {
                    JSONObject responseObject = new JSONObject(response);
                    if (responseObject.getString("response").equals("custom-program-actions-success"))
                        Toast.makeText(UserNavigation.getContext(), R.string.txtProgramSentCorrectly, Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(UserNavigation.getContext(), R.string.txtProgramError, Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private String parseActions() {
        String parsedActionsList = "";
        for (int i = 0; i < actionsList.size(); i++) {
            switch (actionsList.get(i)) {
                case "Endavant":
                    //parsedActionsList.add("up");
                    parsedActionsList += "up";
                    break;
                case "Enrere":
                    //parsedActionsList.add("down");
                    parsedActionsList += "down";
                    break;
                case "Dreta":
                    //parsedActionsList.add("right");
                    parsedActionsList += "right";
                    break;
                case "Esquerra":
                    //parsedActionsList.add("left");
                    parsedActionsList += "left";
                    break;
                case "Baixar xeringa":
                    //parsedActionsList.add("lower_pipette");
                    parsedActionsList += "lower_pipette";
                    break;
                case "Pujar xeringa":
                    //parsedActionsList.add("raise_pipette");
                    parsedActionsList += "raise_pipette";
                    break;
                case "Accionar xeringa":
                    //parsedActionsList.add("suck");
                    parsedActionsList += "suck";
                    break;
            }
            parsedActionsList += ",";
        }
        return parsedActionsList;
    }

    @Override
    public void onUserClicked(int index) {
    }
}