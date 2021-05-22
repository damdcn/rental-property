package com.example.rentalproperty.models;

public class User {

    public String email, firstname, lastname;
    public boolean isLandlord;

    public User() {
    }

    public User(String email, String firstname, String lastname, boolean isLandlord) {
        this.email = email;
        this.firstname = firstname;
        this.lastname = lastname;
        this.isLandlord = isLandlord;
    }
}
