package edu.upc.arnaubeltra.tfgquibot.ui.homeAdmin;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.upc.arnaubeltra.tfgquibot.AdminNavigation;
import edu.upc.arnaubeltra.tfgquibot.R;

public class HomeAdmin extends Fragment {

    public HomeAdmin() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home_admin, container, false);

        v.findViewById(R.id.btnExperimentsHomeAdmin).setOnClickListener(view -> AdminNavigation.getNavController().navigate(R.id.experiments));
        v.findViewById(R.id.btnInteractHomeAdmin).setOnClickListener(view -> AdminNavigation.getNavController().navigate(R.id.interactWithRobot));
        v.findViewById(R.id.btnCustomProgramHomeAdmin).setOnClickListener(view -> AdminNavigation.getNavController().navigate(R.id.customProgram));
        v.findViewById(R.id.btnUserListHomeAdmin).setOnClickListener(view -> AdminNavigation.getNavController().navigate(R.id.usersList));

        return v;
    }
}