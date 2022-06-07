package edu.upc.arnaubeltra.tfgquibot.ui.homeAdmin;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import edu.upc.arnaubeltra.tfgquibot.AdminNavigation;
import edu.upc.arnaubeltra.tfgquibot.R;


// Class that defines the admin home screen.
public class HomeAdmin extends Fragment {

    // Fragments require an empty constructor.
    public HomeAdmin() { }

    // Method that creates the fragment.
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    // Method that creates the view of the fragment. Handles navigation when menu buttons are clicked.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home_admin, container, false);

        v.findViewById(R.id.btnExperimentsHomeAdmin).setOnClickListener(view -> AdminNavigation.getNavController().navigate(R.id.experiments));
        v.findViewById(R.id.btnInteractHomeAdmin).setOnClickListener(view -> AdminNavigation.getNavController().navigate(R.id.interactWithRobot));
        v.findViewById(R.id.btnCustomProgramHomeAdmin).setOnClickListener(view -> AdminNavigation.getNavController().navigate(R.id.customProgram));
        v.findViewById(R.id.btnUserListHomeAdmin).setOnClickListener(view -> AdminNavigation.getNavController().navigate(R.id.usersList));

        ((TextView) v.findViewById(R.id.textViewTitleAdmin)).setText(Html.fromHtml(getString(R.string.txtTitleHome) + "<br>Qui-Bot H<sub>2</sub>O"));

        return v;
    }
}