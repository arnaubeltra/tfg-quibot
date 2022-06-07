package edu.upc.arnaubeltra.tfgquibot.ui.homeUser.robot1d;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import edu.upc.arnaubeltra.tfgquibot.R;
import edu.upc.arnaubeltra.tfgquibot.UserNavigationRobot1d;


// Class that defines the Robot 1D home screen.
public class HomeUserRobot1d extends Fragment {

    // Fragments require an empty constructor.
    public HomeUserRobot1d() { }

    // Method that creates the fragment.
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    // Method that creates the view of the fragment. Handles navigation when menu buttons are clicked.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home_user_robot1d, container, false);

        v.findViewById(R.id.btnExperimentsHomeUser).setOnClickListener(view -> UserNavigationRobot1d.getNavController().navigate(R.id.experiments));
        v.findViewById(R.id.btnInteractHomeUser).setOnClickListener(view -> UserNavigationRobot1d.getNavController().navigate(R.id.interactWithRobot));
        v.findViewById(R.id.btnCustomProgramHomeUser).setOnClickListener(view -> UserNavigationRobot1d.getNavController().navigate(R.id.customProgram));

        ((TextView) v.findViewById(R.id.textViewTitleUser)).setText(Html.fromHtml(getString(R.string.txtTitleHome) + "<br>Qui-Bot H<sub>2</sub>O"));

        return v;
    }
}