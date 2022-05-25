package edu.upc.arnaubeltra.tfgquibot.ui.homeUser.robot1d;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import edu.upc.arnaubeltra.tfgquibot.R;
import edu.upc.arnaubeltra.tfgquibot.UserNavigationRobot1d;
import edu.upc.arnaubeltra.tfgquibot.UserNavigationRobot2d;
import edu.upc.arnaubeltra.tfgquibot.ui.login.Login;
import edu.upc.arnaubeltra.tfgquibot.ui.shared.viewModels.NavigationViewModel;


public class HomeUserRobot1d extends Fragment {

    public HomeUserRobot1d() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home_user_robot1d, container, false);

        v.findViewById(R.id.btnExperimentsHomeUser).setOnClickListener(view -> UserNavigationRobot1d.getNavController().navigate(R.id.experiments));
        v.findViewById(R.id.btnInteractHomeUser).setOnClickListener(view -> UserNavigationRobot1d.getNavController().navigate(R.id.interactWithRobot));
        v.findViewById(R.id.btnCustomProgramHomeUser).setOnClickListener(view -> UserNavigationRobot1d.getNavController().navigate(R.id.customProgram));

        return v;
    }

    // problema login logout
    /*@Override
    public void onPause() {
        super.onPause();
        NavigationViewModel navigationViewModel = new ViewModelProvider(Login.getContext()).get(NavigationViewModel.class);
        navigationViewModel.logoutUser(Login.getIpAddress());
    }*/
}