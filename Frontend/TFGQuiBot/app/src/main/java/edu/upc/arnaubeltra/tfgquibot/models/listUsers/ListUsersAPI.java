package edu.upc.arnaubeltra.tfgquibot.models.listUsers;

import java.util.List;

public class ListUsersAPI {
    private List<User> users = null;

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }
}
