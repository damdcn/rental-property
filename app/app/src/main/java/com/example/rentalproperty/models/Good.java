package com.example.rentalproperty.models;

import java.util.Comparator;
import java.util.Date;

public class Good {
    private String id;
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

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

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

    public static Comparator<Good> GoodDateAscendingComparator = new Comparator<Good>() {
        @Override
        public int compare(Good o1, Good o2) {
            return o1.getDate().compareTo(o2.getDate());
        }
    };

    public static Comparator<Good> GoodDateDescendingComparator = new Comparator<Good>() {
        @Override
        public int compare(Good o1, Good o2) {
            return o2.getDate().compareTo(o1.getDate());
        }
    };

    public static Comparator<Good> GoodPriceAscendingComparator = new Comparator<Good>() {
        @Override
        public int compare(Good o1, Good o2) {
            return (int) (o1.getPrice() - o2.getPrice());
        }
    };

    public static Comparator<Good> GoodPriceDescendingComparator = new Comparator<Good>() {
        @Override
        public int compare(Good o1, Good o2) {
            return (int) (o2.getPrice() - o1.getPrice());
        }
    };

}
