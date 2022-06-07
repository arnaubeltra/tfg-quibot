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


// Class that defines the User list of auth screen.
public class UsersList extends Fragment implements UsersListAdapter.ILoggedUserListRCVItemClicked, AdapterView.OnItemSelectedListener{

    private UsersListViewModel usersListViewModel;
    private PermissionsViewModel permissionsViewModel;

    // Definition of the elements of the layout
    private Spinner spinner;
    private TextView textViewNoUsersLoggedIn;
    private RecyclerView rcvListLoggedInUsers;
    private UsersListAdapter loggedUsersListAdapter;

    private int robot = 0;
    private static int index;


    // Fragments require an empty constructor.
    public UsersList() {}

    // Method to create the fragment.
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    // Method that creates the view of the fragment, defining all the elements of the layout and calling important methods to handle status.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_users_list, container, false);

        // Creation of the ViewModel objects.
        usersListViewModel = new ViewModelProvider(this).get(UsersListViewModel.class);
        permissionsViewModel = new ViewModelProvider(Login.getContext()).get(PermissionsViewModel.class);

        // As this fragment is used for Robot 1D and Robot 2D, we have to know which robot we are using to adapt the layout.
        robot = getRobot();

        // Definition of the layout
        spinner = v.findViewById(R.id.spinnerSelectActivity);
        textViewNoUsersLoggedIn = v.findViewById(R.id.textViewNoUsersLoggedIn);
        rcvListLoggedInUsers = v.findViewById(R.id.rcvListLoggedInUsers);

        // Configuration of the Recycler View, setting the adapter.
        loggedUsersListAdapter = new UsersListAdapter(this);
        rcvListLoggedInUsers.setLayoutManager(new LinearLayoutManager(getActivity()));
        rcvListLoggedInUsers.setAdapter(loggedUsersListAdapter);

        // When started, refreshes the user list.
        refreshUsersList();

        // Setup of the spinner that allows to select an activity to authorize.
        setupSpinnerSelectActivity();

        // Start the observer to get the request responses.
        setupChangePermissionObserver();

        setHasOptionsMenu(true);
        return v;
    }

    // Method that gets the actual robot, selected when logging in.
    private int getRobot() {
        if (Login.getAdminLogged())
            return AdminLogin.getRobotAdmin();
        else
            return Login.getRobotUser();
    }

    // Sets to VISIBLE the reload and help icons of the top bar.
    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        menu.findItem(R.id.reload).setVisible(true);
        menu.findItem(R.id.help).setVisible(true);
        super.onPrepareOptionsMenu(menu);
    }

    // Method to call the actions to be performed when help or reload buttons are clicked.
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.reload) {
            Toast.makeText(getActivity(), R.string.txtRefreshedList, Toast.LENGTH_SHORT).show();
            refreshUsersList();
        } else if (item.getItemId() == R.id.help)
            openHelpDialog();
        return super.onOptionsItemSelected(item);
    }

    // Method to refresh the users list.
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

    // Method to configure the spinner that allows to select the current activity. Changes according the robot.
    private void setupSpinnerSelectActivity() {
        spinner.setOnItemSelectedListener(this);
        List<String> activities = new ArrayList<>();

        if (robot == 1)
            Collections.addAll(activities, getResources().getString(R.string.menu_experiments), getResources().getString(R.string.menu_interact), getResources().getString(R.string.menu_custom_program));
        else if (robot == 2)
            Collections.addAll(activities, getResources().getString(R.string.menu_experiments), getResources().getString(R.string.menu_interact), getResources().getString(R.string.menu_custom_program), getResources().getString(R.string.menu_tic_tac_toe), getResources().getString(R.string.menu_connect4));

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getContext(), R.layout.spinner_layout_2, activities);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
    }

    // Method to get the selected activity of the spinner. Sets all the auth users to false, to have the overall control.
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
        usersListViewModel.changeActualActivity(parseItemSelected(adapterView.getItemAtPosition(position).toString()));

        for (int i = 0; i < loggedUsersListAdapter.loggedUsersList.size(); i++)
            loggedUsersListAdapter.loggedUsersList.get(i).setAuthorized("false");
        loggedUsersListAdapter.updateLoggedUsersList(loggedUsersListAdapter.loggedUsersList);
    }

    // Method that specifies which activity must be shown when none of them is selected.
    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        usersListViewModel.changeActualActivity(parseItemSelected(getResources().getString(R.string.menu_experiments)));
    }

    // Acts as a translator between the string resources and a string that is understandable by the backend.
    private String parseItemSelected(String activity) {
        if (activity.equals(getResources().getString(R.string.menu_experiments))) return "experiments";
        else if (activity.equals(getResources().getString(R.string.menu_interact))) return "interact";
        else if (activity.equals(getResources().getString(R.string.menu_custom_program))) return "custom_program";
        else if (activity.equals(getResources().getString(R.string.menu_tic_tac_toe))) return "tic_tac_toe";
        else if (activity.equals(getResources().getString(R.string.menu_connect4))) return "connect4";
        return "";
    }

    // Method not used.
    @Override
    public void onUserClicked(int index) { }

    // Method to observe the request responses when a permission is changed for any user.
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

    // Method that calls to change the permission of any user.
    private void changePermission() {
        if (loggedUsersListAdapter.loggedUsersList.get(index).getAuthorized().equals("true")) loggedUsersListAdapter.loggedUsersList.get(index).setAuthorized("false");
        else loggedUsersListAdapter.loggedUsersList.get(index).setAuthorized("true");
        loggedUsersListAdapter.updateLoggedUsersList(loggedUsersListAdapter.loggedUsersList);
    }

    // Method that sets the index of a certain element of the list.
    public static void setIndex(int position) {
        index = position;
    }

    // Opens a help dialog in order to know how UsersList works.
    private void openHelpDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.menu_users_list)
                .setMessage(R.string.txtHelpUsersList)
                .setPositiveButton(R.string.txtAccept, null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}