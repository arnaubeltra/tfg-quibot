package edu.upc.arnaubeltra.tfgquibot.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

import edu.upc.arnaubeltra.tfgquibot.R;
import edu.upc.arnaubeltra.tfgquibot.models.listUsers.User;
import edu.upc.arnaubeltra.tfgquibot.ui.login.Login;
import edu.upc.arnaubeltra.tfgquibot.ui.shared.viewModels.PermissionsViewModel;
import edu.upc.arnaubeltra.tfgquibot.ui.usersList.UsersList;


// Adapter used in the Recycler View of the UsersList class.
public class UsersListAdapter extends RecyclerView.Adapter<UsersListAdapter.UsersListViewHolder> {

    // Interface used to handle element in list clicked.
    public interface ILoggedUserListRCVItemClicked {
        void onUserClicked(int index);
    }

    // Listener of the element clicked.
    private ILoggedUserListRCVItemClicked listener;
    // ArrayList where are stored the elements displayed in the RecyclerView.
    public ArrayList<User> loggedUsersList = new ArrayList<>();
    // Instance of the PermissionsViewModel class
    private PermissionsViewModel permissionsViewModel;

    // Constructor
    public UsersListAdapter(ILoggedUserListRCVItemClicked listener) {
        this.listener = listener;
    }

    // Method called when Recycler View needs to be updated as new data has ben updated or data has been removed.
    public void updateLoggedUsersList(ArrayList<User> loggedUsersData) {
        loggedUsersList = loggedUsersData;
        notifyDataSetChanged();
    }

    // Inflate the layout of each element of the Recycler View. This layout represent each individual list element.
    @NonNull
    @Override
    public UsersListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_list_users, parent, false);
        return new UsersListViewHolder(v, listener);
    }

    // Configure each new element in the Recycler View, adding all the content needed.
    @Override
    public void onBindViewHolder(@NonNull UsersListViewHolder holder, int position) {
        holder.txtUserNameSurname.setText(loggedUsersList.get(position).getName() + " " + loggedUsersList.get(position).getSurname());
        if (loggedUsersList.get(position).getAuthorized().equals("false")) {
            holder.btnGiveQuitAccess.setText(R.string.txtGivePermissions);
        } else {
            holder.btnGiveQuitAccess.setText(R.string.txtQuitPermissions);
        }
    }

    // Method that returns the number of items of the list.
    @Override
    public int getItemCount() {
        return loggedUsersList.size();
    }

    // ViewHolder class, that defines the structure of each element of the Recycler View.
    public class UsersListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView txtUserNameSurname;
        public Button btnGiveQuitAccess;

        ILoggedUserListRCVItemClicked listener;

        // Definition of the elements of the view and call to methods if they have to perform any action.
        public UsersListViewHolder(@NonNull View itemView, ILoggedUserListRCVItemClicked itemClickedListener) {
            super(itemView);

            permissionsViewModel = new ViewModelProvider(Login.getContext()).get(PermissionsViewModel.class);

            txtUserNameSurname = itemView.findViewById(R.id.txtUserNameSurname);
            btnGiveQuitAccess = itemView.findViewById(R.id.btnGiveQuitAccess);
            btnGiveQuitAccess.setOnClickListener(view -> changePermissions(getAdapterPosition()));

            listener = itemClickedListener;
            itemView.setOnClickListener(this);
        }

        // Handler when clicking one of the Recycler View list elements.
        @Override
        public void onClick(View view) {
            listener.onUserClicked(getAdapterPosition());
        }
    }

    // Method used to change permissions of a user. Changes button text when clicked.
    private void changePermissions(int index) {
        if (loggedUsersList.get(index).getAuthorized().equals("true"))
            permissionsViewModel.changeUserPermissions(loggedUsersList.get(index).getUid(), "false");
        else
            permissionsViewModel.changeUserPermissions(loggedUsersList.get(index).getUid(), "true");
        UsersList.setIndex(index);
    }
}