package edu.upc.arnaubeltra.tfgquibot.ui.usersList;

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
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import edu.upc.arnaubeltra.tfgquibot.R;
import edu.upc.arnaubeltra.tfgquibot.adapters.LoggedUsersListAdapter;
import edu.upc.arnaubeltra.tfgquibot.models.user.User;


public class UsersList extends Fragment implements LoggedUsersListAdapter.ILoggedUserListRCVItemClicked {

    private RecyclerView rcvListLoggedInUsers;
    private LoggedUsersListAdapter loggedUsersListAdapter;

    //private static UsersList instance;
    private ArrayList<User> loggedUsersList = new ArrayList<>();

    private TextView textViewNoUsersLoggedIn;

    private UsersListViewModel usersListViewModel;

    // Required empty public constructor
    public UsersList() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //super.onCreate(savedInstanceState);
        View v =  inflater.inflate(R.layout.fragment_users_list, container, false);

        textViewNoUsersLoggedIn = v.findViewById(R.id.textViewNoUsersLoggedIn);

        rcvListLoggedInUsers = v.findViewById(R.id.rcvListLoggedInUsers);

        loggedUsersListAdapter = new LoggedUsersListAdapter(this);
        rcvListLoggedInUsers.setLayoutManager(new LinearLayoutManager(getActivity()));
        rcvListLoggedInUsers.setAdapter(loggedUsersListAdapter);

        usersListViewModel = new ViewModelProvider(this).get(UsersListViewModel.class);
        refreshUsersList();

        setHasOptionsMenu(true);

        return v;
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        menu.findItem(R.id.reload).setVisible(true);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.reload:
                Toast.makeText(getActivity(), R.string.txtRefreshedList, Toast.LENGTH_SHORT).show();
                refreshUsersList();
        }
        return super.onOptionsItemSelected(item);
    }

    private void refreshUsersList() {
        usersListViewModel.updateLoggedInUsers();
        usersListViewModel.getLoggedInUsersListResponse().observe(getViewLifecycleOwner(), new Observer<ArrayList<User>>() {
            @Override
            public void onChanged(ArrayList<User> loggedUsers) {
                loggedUsersListAdapter.updateLoggedUsersList(loggedUsers);
                if (loggedUsersListAdapter.getItemCount() == 0)
                    textViewNoUsersLoggedIn.setVisibility(View.VISIBLE);
                else
                    textViewNoUsersLoggedIn.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public void onUserClicked(int index) {

    }
}