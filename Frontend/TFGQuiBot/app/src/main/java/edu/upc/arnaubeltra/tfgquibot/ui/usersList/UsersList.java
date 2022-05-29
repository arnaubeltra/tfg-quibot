package edu.upc.arnaubeltra.tfgquibot.ui.usersList;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.upc.arnaubeltra.tfgquibot.R;
import edu.upc.arnaubeltra.tfgquibot.adapters.UsersListAdapter;
import edu.upc.arnaubeltra.tfgquibot.ui.login.AdminLogin;
import edu.upc.arnaubeltra.tfgquibot.ui.login.Login;
import edu.upc.arnaubeltra.tfgquibot.ui.shared.viewModels.PermissionsViewModel;


public class UsersList extends Fragment implements UsersListAdapter.ILoggedUserListRCVItemClicked, AdapterView.OnItemSelectedListener{

    private RecyclerView rcvListLoggedInUsers;
    private UsersListAdapter loggedUsersListAdapter;

    private static int index;
    
    private Spinner spinner;
    private TextView textViewNoUsersLoggedIn;

    private UsersListViewModel usersListViewModel;
    private PermissionsViewModel permissionsViewModel;

    private int robot = 0;

    // Required empty public constructor
    public UsersList() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_users_list, container, false);

        robot = getRobot();

        spinner = v.findViewById(R.id.spinnerSelectActivity);
        textViewNoUsersLoggedIn = v.findViewById(R.id.textViewNoUsersLoggedIn);
        rcvListLoggedInUsers = v.findViewById(R.id.rcvListLoggedInUsers);

        loggedUsersListAdapter = new UsersListAdapter(this);
        rcvListLoggedInUsers.setLayoutManager(new LinearLayoutManager(getActivity()));
        rcvListLoggedInUsers.setAdapter(loggedUsersListAdapter);

        usersListViewModel = new ViewModelProvider(this).get(UsersListViewModel.class);
        permissionsViewModel = new ViewModelProvider(Login.getContext()).get(PermissionsViewModel.class);
        refreshUsersList();
        setupSpinnerSelectActivity();
        setupChangePermissionObserver();

        setHasOptionsMenu(true);
        return v;
    }

    private int getRobot() {
        if (Login.getAdminLogged())
            return AdminLogin.getRobotAdmin();
        else
            return Login.getRobotUser();
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        menu.findItem(R.id.reload).setVisible(true);
        menu.findItem(R.id.help).setVisible(true);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.reload) {
            Toast.makeText(getActivity(), R.string.txtRefreshedList, Toast.LENGTH_SHORT).show();
            refreshUsersList();
        } else if (item.getItemId() == R.id.help)
            openHelpDialog();
        return super.onOptionsItemSelected(item);
    }

    private void refreshUsersList() {
        usersListViewModel.updateLoggedInUsers();
        usersListViewModel.getLoggedInUsersListResponse().observe(getViewLifecycleOwner(), loggedUsers -> {
            loggedUsersListAdapter.updateLoggedUsersList(loggedUsers);
            if (loggedUsersListAdapter.getItemCount() == 0)
                textViewNoUsersLoggedIn.setVisibility(View.VISIBLE);
            else
                textViewNoUsersLoggedIn.setVisibility(View.INVISIBLE);
        });
    }

    private void setupSpinnerSelectActivity() {
        spinner.setOnItemSelectedListener(this);
        List<String> activities = new ArrayList<>();

        if (robot == 1)
            Collections.addAll(activities, getResources().getString(R.string.menu_experiments), getResources().getString(R.string.menu_interact), getResources().getString(R.string.menu_custom_program));
        else if (robot == 2)
            Collections.addAll(activities, getResources().getString(R.string.menu_experiments), getResources().getString(R.string.menu_interact), getResources().getString(R.string.menu_custom_program), getResources().getString(R.string.menu_tic_tac_toe), getResources().getString(R.string.menu_connect4));

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, activities);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
        usersListViewModel.changeActualActivity(parseItemSelected(adapterView.getItemAtPosition(position).toString()));

        for (int i = 0; i < loggedUsersListAdapter.loggedUsersList.size(); i++)
            loggedUsersListAdapter.loggedUsersList.get(i).setAuthorized("false");
        loggedUsersListAdapter.updateLoggedUsersList(loggedUsersListAdapter.loggedUsersList);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        usersListViewModel.changeActualActivity(parseItemSelected(getResources().getString(R.string.menu_experiments)));
    }

    private String parseItemSelected(String activity) {
        if (activity.equals(getResources().getString(R.string.menu_experiments))) return "experiments";
        else if (activity.equals(getResources().getString(R.string.menu_interact))) return "interact";
        else if (activity.equals(getResources().getString(R.string.menu_custom_program))) return "custom_program";
        else if (activity.equals(getResources().getString(R.string.menu_tic_tac_toe))) return "tic_tac_toe";
        else if (activity.equals(getResources().getString(R.string.menu_connect4))) return "connect4";
        return "";
    }

    @Override
    public void onUserClicked(int index) { }

    private void setupChangePermissionObserver() {
        permissionsViewModel.changeUserPermissions("", "");
        permissionsViewModel.getUserPermissionsChangeResponse().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String response) {
                try {
                    JSONObject responseObject = new JSONObject(response);
                    if (responseObject.getString("response").equals("change-permissions-success"))
                        changePermission();
                    else if (responseObject.getString("response").equals("max-number-of-users"))
                        Toast.makeText(getContext(), R.string.txtCannotChangePermission, Toast.LENGTH_LONG).show();
                    else Toast.makeText(getContext(), R.string.txtErrorChangingPermissions, Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void changePermission() {
        if (loggedUsersListAdapter.loggedUsersList.get(index).getAuthorized().equals("true")) loggedUsersListAdapter.loggedUsersList.get(index).setAuthorized("false");
        else loggedUsersListAdapter.loggedUsersList.get(index).setAuthorized("true");
        loggedUsersListAdapter.updateLoggedUsersList(loggedUsersListAdapter.loggedUsersList);
    }
    
    public static void setIndex(int position) {
        index = position;
    }

    private void openHelpDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.menu_users_list)
                .setMessage(R.string.txtHelpUsersList)
                .setPositiveButton(R.string.txtAccept, null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}