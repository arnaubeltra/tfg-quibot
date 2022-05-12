package edu.upc.arnaubeltra.tfgquibot.ui.experiments;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
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
import edu.upc.arnaubeltra.tfgquibot.viewModels.PermissionsViewModel;


public class ExperimentsList extends Fragment {

    private PermissionsViewModel permissionsViewModel;
    private Boolean isAuthorized = false;

    int i = 0;

    // Required empty public constructor
    public ExperimentsList() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_experiments, container, false);

        v.findViewById(R.id.btnExecExperimentSeriesDisolucio).setOnClickListener(view -> onExecExperimentCheckPermissions(getResources().getString(R.string.titleSeriesDisolucio)));
        v.findViewById(R.id.btnExecExperimentBarrejaColors).setOnClickListener(view -> onExecExperimentCheckPermissions(getResources().getString(R.string.titleBarrejaColorsPrimaris)));
        v.findViewById(R.id.btnExecExperimentCapesDensitat).setOnClickListener(view -> onExecExperimentCheckPermissions(getResources().getString(R.string.titleCapesDeDensitat)));

        v.findViewById(R.id.btnBoardtSeriesDisolucio).setOnClickListener(view -> dialogHowToPrepareExperiment(getResources().getString(R.string.titleSeriesDisolucio)));
        v.findViewById(R.id.btnBoardBarrejaColors).setOnClickListener(view -> dialogHowToPrepareExperiment(getResources().getString(R.string.titleBarrejaColorsPrimaris)));
        v.findViewById(R.id.btnBoardCapesDensitat).setOnClickListener(view -> dialogHowToPrepareExperiment(getResources().getString(R.string.titleCapesDeDensitat)));

        permissionsViewModel = new ViewModelProvider(Login.getContext()).get(PermissionsViewModel.class);

        return v;
    }

    private void onExecExperimentCheckPermissions(String experimentName) {
        permissionsViewModel.checkUserPermissions(Login.getIpAddress());

        if (i == 0) {
            permissionsViewModel.getUserPermissionsResponse().observe(getViewLifecycleOwner(), auth -> {
                try {
                    JSONObject responseObject = new JSONObject(auth);
                    if (responseObject.getString("response").equals("true"))
                        execExperiment(experimentName);
                    else Toast.makeText(UserNavigation.getContext(), R.string.txtNoPermissions, Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });
        } i++;

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    private void execExperiment(String experimentName) {
        switch (experimentName) {
            case "Sèries de disolució":
                break;
            case "Barreja colors primaris":
                break;
            case "Capes de densitat":
                break;
        }
    }

    private void dialogHowToPrepareExperiment(String experimentName) {          /*Todo: change images*/
        Dialog dialog = new Dialog(getActivity());
        dialog.setCancelable(true);
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_experiments, null);

        ImageView image = view.findViewById(R.id.imgBoardExperiment);

        switch (experimentName) {
            case "Sèries de disolució":
                image.setImageResource(R.drawable.quibot_bg_light);
                break;
            case "Barreja colors primaris":
                image.setImageResource(R.drawable.quibot_bg_light);
                break;
            case "Capes de densitat":
                image.setImageResource(R.drawable.quibot_bg_light);
                break;
        }

        dialog.setContentView(view);
        dialog.show();
    }
}