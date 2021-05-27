package com.example.rentalproperty.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Comparator;
import java.util.Date;

public class Booking implements Parcelable {

    private Long arrival, departure;
    private String clientId;
    private String hostId;
    private int days;
    private double price;
    private String creditCard;
    private String id;

    public Booking() {}

    public Booking(Long arrival, Long departure, String clientId, String hostId, int days, double price, String creditCard) {
        this.arrival = arrival;
        this.departure = departure;
        this.clientId = clientId;
        this.hostId = hostId;
        this.days = days;
        this.price = price;
        this.creditCard = creditCard;
    }

    public String toString(){
        return
                "iencli : " + clientId + " \n" +
                "host : " + hostId + " \n" +
                "delta : " + days + " \n" +
                "cb : " + creditCard + " \n";
    }

    public Long getArrival() {
        return arrival;
    }

    public Long getDeparture() {
        return departure;
    }

    public String getClientId() {
        return clientId;
    }

    public String getHostId() {
        return hostId;
    }

    public int getDays() {
        return days;
    }

    public double getPrice() {
        return price;
    }

    public String getCreditCard() {
        return creditCard;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    protected Booking(Parcel in) {
    }

    public static final Creator<Booking> CREATOR = new Creator<Booking>() {
        @Override
        public Booking createFromParcel(Parcel in) {
            return new Booking(in);
        }

        @Override
        public Booking[] newArray(int size) {
            return new Booking[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }
}
