package edu.upc.arnaubeltra.tfgquibot.ui.homeUser.robot2d;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import edu.upc.arnaubeltra.tfgquibot.R;
import edu.upc.arnaubeltra.tfgquibot.UserNavigationRobot2d;
import edu.upc.arnaubeltra.tfgquibot.ui.login.Login;
import edu.upc.arnaubeltra.tfgquibot.ui.shared.viewModels.NavigationViewModel;


public class HomeUserRobot2d extends Fragment {

    public HomeUserRobot2d() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home_user_robot2d, container, false);

        v.findViewById(R.id.btnExperimentsHomeUser).setOnClickListener(view -> UserNavigationRobot2d.getNavController().navigate(R.id.experiments));
        v.findViewById(R.id.btnInteractHomeUser).setOnClickListener(view -> UserNavigationRobot2d.getNavController().navigate(R.id.interactWithRobot));
        v.findViewById(R.id.btnCustomProgramHomeUser).setOnClickListener(view -> UserNavigationRobot2d.getNavController().navigate(R.id.customProgram));
        v.findViewById(R.id.btnTicTacToeHomeUser).setOnClickListener(view -> UserNavigationRobot2d.getNavController().navigate(R.id.ticTacToe));
        v.findViewById(R.id.btnConnect4HomeUser).setOnClickListener(view -> UserNavigationRobot2d.getNavController().navigate(R.id.connect4));

        ((TextView) v.findViewById(R.id.textViewTitleUser)).setText(Html.fromHtml(getString(R.string.txtTitleHome) + "<br>Qui-Bot H<sub>2</sub>O"));

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