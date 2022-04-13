package edu.upc.arnaubeltra.tfgquibot.ui.interactWithRobot;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.upc.arnaubeltra.tfgquibot.R;

public class InteractWithRobot extends Fragment {

    private InteractWithRobotViewModel interactWithRobotViewModel;

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

        return v;
    }

    private void action(String interaction) {
        switch (interaction) {
            case "forward":
                interactWithRobotViewModel.sendInteraction("forward");
            case "backwards":
                interactWithRobotViewModel.sendInteraction("backwards");
            case "left":
                interactWithRobotViewModel.sendInteraction("left");
            case "right":
                interactWithRobotViewModel.sendInteraction("right");
            case "raise_pipette":
                interactWithRobotViewModel.sendInteraction("raise_pipette");
            case "lower_pipette":
                interactWithRobotViewModel.sendInteraction("lower_pipette");
            case "suck":
                interactWithRobotViewModel.sendInteraction("suck");
            case "reset":
                interactWithRobotViewModel.sendInteraction("reset");
            case "readColor":
                interactWithRobotViewModel.sendInteraction("readColor");
        }
    }
}