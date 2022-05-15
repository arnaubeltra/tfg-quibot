package edu.upc.arnaubeltra.tfgquibot.ui.shared;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.upc.arnaubeltra.tfgquibot.R;
import edu.upc.arnaubeltra.tfgquibot.ui.interactWithRobot.InteractWithRobotViewModel;

public class BoardSize implements AdapterView.OnItemSelectedListener {

    public static BoardSize instance;

    public static BoardSize getInstance() {
        if (instance == null) instance = new BoardSize();
        return instance;
    }

    public void  createDialogBoardSize(Activity activity, ViewModelStoreOwner vmOwner, String value) {
        //AtomicReference<String> boardSize = new AtomicReference<>("");
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_custom_program, null);

        builder.setView(view);
        builder.setTitle(R.string.txtTitleDialogSelectBoard);

        Spinner spinner = setupSpinnerBoardSize(view, activity, value);

        builder.setPositiveButton(R.string.txtButonSave, (dialogInterface, i) -> {
            InteractWithRobotViewModel interactWithRobotViewModel = new ViewModelProvider(vmOwner).get(InteractWithRobotViewModel.class);
            String send = "";
            if (String.valueOf(spinner.getSelectedItem()) == "Forats grans") send = "small_matrix";
            else if (String.valueOf(spinner.getSelectedItem()) == "Forats mitjans") send = "medium_matrix";
            else if (String.valueOf(spinner.getSelectedItem()) == "Forats petits") send = "large_matrix";
            interactWithRobotViewModel.startInteract(send);
            //boardSize.set(String.valueOf(spinner.getSelectedItem()));
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        //return boardSize.get();
    }

    private Spinner setupSpinnerBoardSize(View view, Activity activity, String value) {
        Spinner spinner = view.findViewById(R.id.spinnerSelectPropertiesAndActions);
        spinner.setOnItemSelectedListener(this);

        List<String> actions = new ArrayList<>();
        Collections.addAll(actions, "Forats petits", "Forats mitjans", "Forats grans");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_item, actions);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(dataAdapter);
        if (!value.equals(""))
            spinner.setSelection(dataAdapter.getPosition(value));

        return spinner;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
