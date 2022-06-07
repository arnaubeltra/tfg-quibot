package edu.upc.arnaubeltra.tfgquibot.models.listUsers;

import java.util.List;

// Model class to define the structure of the List of Users returned by the request.
public class ListUsersAPI {
    private List<User> users = null;

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }
}
