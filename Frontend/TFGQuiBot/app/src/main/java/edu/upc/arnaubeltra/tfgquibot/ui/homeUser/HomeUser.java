package edu.upc.arnaubeltra.tfgquibot.ui.homeUser;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.upc.arnaubeltra.tfgquibot.R;
import edu.upc.arnaubeltra.tfgquibot.UserNavigation;
import edu.upc.arnaubeltra.tfgquibot.ui.login.Login;
import edu.upc.arnaubeltra.tfgquibot.ui.shared.viewModels.NavigationViewModel;


public class HomeUser extends Fragment {

    public HomeUser() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home_user, container, false);

        v.findViewById(R.id.btnExperimentsHomeUser).setOnClickListener(view -> UserNavigation.getNavController().navigate(R.id.experiments));
        v.findViewById(R.id.btnInteractHomeUser).setOnClickListener(view -> UserNavigation.getNavController().navigate(R.id.interactWithRobot));
        v.findViewById(R.id.btnCustomProgramHomeUser).setOnClickListener(view -> UserNavigation.getNavController().navigate(R.id.customProgram));
        v.findViewById(R.id.btnTicTacToeHomeUser).setOnClickListener(view -> UserNavigation.getNavController().navigate(R.id.ticTacToe));
        v.findViewById(R.id.btnConnect4HomeUser).setOnClickListener(view -> UserNavigation.getNavController().navigate(R.id.connect4));

        return v;
    }

    // problema login logout
    /*@Override
    public void onDestroy() {
        super.onDestroy();
        NavigationViewModel navigationViewModel = new ViewModelProvider(Login.getContext()).get(NavigationViewModel.class);
        navigationViewModel.logoutUser(Login.getIpAddress());
    }*/
}