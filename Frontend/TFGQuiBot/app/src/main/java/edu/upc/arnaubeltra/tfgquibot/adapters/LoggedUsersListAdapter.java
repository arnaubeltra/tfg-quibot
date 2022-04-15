package edu.upc.arnaubeltra.tfgquibot.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import edu.upc.arnaubeltra.tfgquibot.R;
import edu.upc.arnaubeltra.tfgquibot.firebase.RealtimeDatabase;
import edu.upc.arnaubeltra.tfgquibot.models.User;
import edu.upc.arnaubeltra.tfgquibot.ui.usersList.UsersList;

public class LoggedUsersListAdapter extends RecyclerView.Adapter<LoggedUsersListAdapter.LoggedUsersListViewHolder> {

    public interface ILoggedUserListRCVItemClicked {
        void onUserClicked(int index);
    }

    private ILoggedUserListRCVItemClicked listener;

    private ArrayList<User> loggedUsersList = new ArrayList<>();
    //private int index = 0;

    private RealtimeDatabase realtimeDatabase = RealtimeDatabase.getInstance();

    public LoggedUsersListAdapter(ILoggedUserListRCVItemClicked listener) {
        this.listener = listener;
    }

    public void updateLoggedUsersList(ArrayList<User> loggedUsersData) {
        loggedUsersList = loggedUsersData;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public LoggedUsersListAdapter.LoggedUsersListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_list_users, parent, false);
        LoggedUsersListViewHolder loggedUsersListViewHolder = new LoggedUsersListViewHolder(v, listener);
        return loggedUsersListViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull LoggedUsersListAdapter.LoggedUsersListViewHolder holder, int position) {
        holder.txtUserNameSurname.setText(loggedUsersList.get(position).getName() + " " + loggedUsersList.get(position).getSurname());

        if (loggedUsersList.get(position).getAuthorized().equals(false)) {
            holder.btnGiveQuitAccess.setText(R.string.txtGivePermissions);
        } else {
            holder.btnGiveQuitAccess.setText(R.string.txtQuitPermissions);
        }
    }

    @Override
    public int getItemCount() {
        return loggedUsersList.size();
    }

    public class LoggedUsersListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView txtUserNameSurname;
        public Button btnGiveQuitAccess;

        ILoggedUserListRCVItemClicked listener;

        public LoggedUsersListViewHolder(@NonNull View itemView, ILoggedUserListRCVItemClicked itemClickedListener) {
            super(itemView);

            txtUserNameSurname = itemView.findViewById(R.id.txtUserNameSurname);
            btnGiveQuitAccess = itemView.findViewById(R.id.btnGiveQuitAccess);
            btnGiveQuitAccess.setOnClickListener(view -> changePermission(getAdapterPosition()));

            listener = itemClickedListener;
            itemView.setOnClickListener(this);

            //index = getAdapterPosition();
            //Log.d("TAG", "onClick: " + index);
        }

        @Override
        public void onClick(View view) {
            listener.onUserClicked(getAdapterPosition());
        }

        private void changePermission(int index) {
            if (loggedUsersList.get(index).getAuthorized()) {
                loggedUsersList.get(index).setAuthorized(false);
                realtimeDatabase.updateAuthorizationUser(loggedUsersList.get(index).getUid(), false);
            }
            else {
                loggedUsersList.get(index).setAuthorized(true);
                realtimeDatabase.updateAuthorizationUser(loggedUsersList.get(index).getUid(),true);
            }
            updateLoggedUsersList(loggedUsersList);
        }
    }
}