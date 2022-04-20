package edu.upc.arnaubeltra.tfgquibot.ui.interactWithRobot;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import edu.upc.arnaubeltra.tfgquibot.R;
import edu.upc.arnaubeltra.tfgquibot.UserNavigation;
import edu.upc.arnaubeltra.tfgquibot.firebase.Authentication;

public class InteractWithRobot extends Fragment {

    private InteractWithRobotViewModel interactWithRobotViewModel;

    private Authentication authentication = Authentication.getInstance();

    private Boolean isAuthorized = false;

    // Required empty public constructor
    public InteractWithRobot() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_interact_with_robot, container, false);

        v.findViewById(R.id.btnForward).setOnClickListener(view -> action("forward"));
        v.findViewById(R.id.btnBackwards).setOnClickListener(view -> action("backwards"));
        v.findViewById(R.id.btnLeft).setOnClickListener(view -> action("left"));
        v.findViewById(R.id.btnRight).setOnClickListener(view -> action("right"));
        v.findViewById(R.id.btnSuck).setOnClickListener(view -> action("suck"));
        v.findViewById(R.id.btnRaisePipette).setOnClickListener(view -> action("raise_pipette"));
        v.findViewById(R.id.btnLowerPipette).setOnClickListener(view -> action("lower_pipette"));
        v.findViewById(R.id.btnReset).setOnClickListener(view -> action("reset"));
        v.findViewById(R.id.btnReadColor).setOnClickListener(view -> action("readColor"));

        interactWithRobotViewModel = new ViewModelProvider(this).get(InteractWithRobotViewModel.class);

        interactWithRobotViewModel.setupFirebaseListenerPermissionsUser(authentication.getUser());
        interactWithRobotViewModel.getPermissionsUser().observe(getViewLifecycleOwner(), permission -> {
            isAuthorized = permission;
            Log.d("TAG", "permissions charge: " + permission);
        });

        return v;
    }

    private void action(String interaction) {
        if (checkPermissions()) {
            switch (interaction) {
                case "forward":
                    interactWithRobotViewModel.sendInteraction("forward");
                    break;
                case "backwards":
                    interactWithRobotViewModel.sendInteraction("backwards");
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
                case "reset":
                    interactWithRobotViewModel.sendInteraction("reset");
                    break;
                case "readColor":
                    interactWithRobotViewModel.sendInteraction("readColor");
                    break;
                default:
                    break;
            }
        } else
            Toast.makeText(UserNavigation.getInstance(), R.string.txtNoPermissions, Toast.LENGTH_SHORT).show();
    }

    private boolean checkPermissions() {
        return isAuthorized;
    }
}