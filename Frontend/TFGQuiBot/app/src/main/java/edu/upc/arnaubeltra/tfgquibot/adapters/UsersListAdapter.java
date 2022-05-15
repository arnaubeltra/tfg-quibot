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

public class UsersListAdapter extends RecyclerView.Adapter<UsersListAdapter.UsersListViewHolder> {

    public interface ILoggedUserListRCVItemClicked {
        void onUserClicked(int index);
    }

    private ILoggedUserListRCVItemClicked listener;
    public ArrayList<User> loggedUsersList = new ArrayList<>();
    private PermissionsViewModel permissionsViewModel;

    public UsersListAdapter(ILoggedUserListRCVItemClicked listener) {
        this.listener = listener;
    }

    public void updateLoggedUsersList(ArrayList<User> loggedUsersData) {
        loggedUsersList = loggedUsersData;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public UsersListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_list_users, parent, false);
        return new UsersListViewHolder(v, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull UsersListViewHolder holder, int position) {
        holder.txtUserNameSurname.setText(loggedUsersList.get(position).getName() + " " + loggedUsersList.get(position).getSurname());

        if (loggedUsersList.get(position).getAuthorized().equals("false")) {
            holder.btnGiveQuitAccess.setText(R.string.txtGivePermissions);
        } else {
            holder.btnGiveQuitAccess.setText(R.string.txtQuitPermissions);
        }
    }

    @Override
    public int getItemCount() {
        return loggedUsersList.size();
    }


    public class UsersListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView txtUserNameSurname;
        public Button btnGiveQuitAccess;

        ILoggedUserListRCVItemClicked listener;

        public UsersListViewHolder(@NonNull View itemView, ILoggedUserListRCVItemClicked itemClickedListener) {
            super(itemView);

            permissionsViewModel = new ViewModelProvider(Login.getContext()).get(PermissionsViewModel.class);

            txtUserNameSurname = itemView.findViewById(R.id.txtUserNameSurname);
            btnGiveQuitAccess = itemView.findViewById(R.id.btnGiveQuitAccess);
            btnGiveQuitAccess.setOnClickListener(view -> changePermissions(getAdapterPosition()));

            listener = itemClickedListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.onUserClicked(getAdapterPosition());
        }
    }

    private void changePermissions(int index) {
        if (loggedUsersList.get(index).getAuthorized().equals("true"))
            permissionsViewModel.changeUserPermissions(loggedUsersList.get(index).getUid(), "false");
        else
            permissionsViewModel.changeUserPermissions(loggedUsersList.get(index).getUid(), "true");
        UsersList.setIndex(index);
    }
}