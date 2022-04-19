package edu.upc.arnaubeltra.tfgquibot.ui.usersList;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import edu.upc.arnaubeltra.tfgquibot.R;
import edu.upc.arnaubeltra.tfgquibot.adapters.LoggedUsersListAdapter;
import edu.upc.arnaubeltra.tfgquibot.firebase.RealtimeDatabase;
import edu.upc.arnaubeltra.tfgquibot.models.User;


public class UsersList extends Fragment implements LoggedUsersListAdapter.ILoggedUserListRCVItemClicked {

    private RecyclerView rcvListLoggedInUsers;
    private LoggedUsersListAdapter loggedUsersListAdapter;

    //private static UsersList instance;
    private ArrayList<User> loggedUsersList = new ArrayList<>();

    private TextView textViewNoUsersLoggedIn;

    // Required empty public constructor
    public UsersList() {}

    /*public static UsersList getInstance() {
        if (instance == null) instance = new UsersList();
        return instance;
    }*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View v =  inflater.inflate(R.layout.fragment_users_list, container, false);

        textViewNoUsersLoggedIn = v.findViewById(R.id.textViewNoUsersLoggedIn);

        loggedUsersListAdapter = new LoggedUsersListAdapter(this);
        rcvListLoggedInUsers = v.findViewById(R.id.rcvListLoggedInUsers);
        rcvListLoggedInUsers.setLayoutManager(new LinearLayoutManager(getActivity()));
        rcvListLoggedInUsers.setAdapter(loggedUsersListAdapter);

        UsersListViewModel usersListViewModel = new ViewModelProvider(this).get(UsersListViewModel.class);
        usersListViewModel.updateLoggedInUsers();
        usersListViewModel.getLoggedInUsers().observe(getViewLifecycleOwner(), new Observer<ArrayList<User>>() {
            @Override
            public void onChanged(ArrayList<User> users) {
                loggedUsersList = users;
                loggedUsersListAdapter.updateLoggedUsersList(users);
                if (loggedUsersListAdapter.getItemCount() == 0)
                    textViewNoUsersLoggedIn.setVisibility(View.VISIBLE);
                else
                    textViewNoUsersLoggedIn.setVisibility(View.INVISIBLE);
            }
        });

        return v;
    }

    public void changePermission(int index) {

    }

    @Override
    public void onUserClicked(int index) {

    }
}