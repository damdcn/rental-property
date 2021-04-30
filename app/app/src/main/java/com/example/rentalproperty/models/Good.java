package com.example.rentalproperty.models;

public class Good {
    private int id;
    private String location;
    private String name;
    private double price;

    public Good() {
    }

    public Good(int id, String location, String name, double price) {
        this.id = id;
        this.location = location;
        this.name = name;
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
