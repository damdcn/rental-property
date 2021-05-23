package com.example.rentalproperty.models;

import java.util.Date;

public class Good {
    private String title;
    private String location;
    private Date date;
    private double price;
    private int image;

    public Good() {
    }

    public Good(String title, double price, String location, Date date) {
        this.title = title;
        this.location = location;
        this.date = date;
        this.price = price;
    }

    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Date getDate() { return date; }

    public void setDate(Date date) { this.date = date; }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
