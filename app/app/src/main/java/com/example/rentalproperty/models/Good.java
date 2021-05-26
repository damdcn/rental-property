package com.example.rentalproperty.models;

import java.util.Comparator;
import java.util.Date;

public class Good {
    private String id;
    private String title;
    private String location;
    private String address;
    private String category;
    private String description;
    private String contactNumber;
    private int maxStaying;
    private double price;
    private double rate;
    private int visits;
    private String imageUrl;
    private String imageId;
    private String authorId;
    private Date date;



    public Good() {
    }

/*    public Good(String title, double price, String location, Date date) {
        this.title = title;
        this.location = location;
        this.date = date;
        this.price = price;
    }*/

    public Good(String id, String title, String location, String address, String category, String description, String contactNumber, int maxStaying, double price, double rate, int visits, String imageUrl, String imageId, String authorId, Date date) {
        this.id = id;
        this.title = title;
        this.location = location;
        this.address = address;
        this.category = category;
        this.description = description;
        this.contactNumber = contactNumber;
        this.maxStaying = maxStaying;
        this.price = price;
        this.rate = rate;
        this.visits = visits;
        this.imageUrl = imageUrl;
        this.imageId = imageId;
        this.authorId = authorId;
        this.date = date;
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

    public String getAddress() { return address; }

    public double getRate() { return rate; }

    public String getCategory() { return category; }

    public String getDescription() { return description; }

    public String getContactNumber() { return contactNumber; }

    public int getMaxStaying() { return maxStaying; }

    public String getImageUrl() { return imageUrl; }

    public String getImageId() { return imageId; }

    public String getAuthorId() { return authorId; }

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
