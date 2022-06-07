package edu.upc.arnaubeltra.tfgquibot.models.listUsers;

// Model class to define a User object
public class User {
    private String uid, name, surname, isAuthorized;

    public User(String uid, String name, String surname, String isAuthorized) {
        this.uid = uid;
        this.name = name;
        this.surname = surname;
        this.isAuthorized = isAuthorized;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getAuthorized() {
        return isAuthorized;
    }

    public void setAuthorized(String authorized) {
        isAuthorized = authorized;
    }
}

