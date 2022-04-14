package edu.upc.arnaubeltra.tfgquibot.adapters;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import edu.upc.arnaubeltra.tfgquibot.models.User;

public class LoggedUsersListAdapter extends RecyclerView.Adapter<LoggedUsersListAdapter.LoggedUsersListViewHolder> {

    public interface ILoggedUserListRCVItemClicked {
        void onUserClicked(int index);
    }

    private ILoggedUserListRCVItemClicked listener;

    private ArrayList<User> loggedUsersList;

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
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull LoggedUsersListAdapter.LoggedUsersListViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return loggedUsersList.size();
    }

    public class LoggedUsersListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ILoggedUserListRCVItemClicked listener;

        public LoggedUsersListViewHolder(@NonNull View itemView, ILoggedUserListRCVItemClicked itemClickedListener) {
            super(itemView);

            listener = itemClickedListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.onUserClicked(getAdapterPosition());
        }
    }
}