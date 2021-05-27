package com.example.rentalproperty;

import android.os.Parcel;
import android.util.Log;

import com.example.rentalproperty.models.Booking;
import com.google.android.material.datepicker.CalendarConstraints.DateValidator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

/** A {@link DateValidator} that only allows dates from a given point onward to be clicked. */
class DateValidatorBookings implements DateValidator {

    private HashMap<String, HashMap<String, Object>> bookings;
    private Calendar utc = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

    DateValidatorBookings(HashMap<String, HashMap<String, Object>> bookings) {
        if(bookings == null) {
            this.bookings = new HashMap<String, HashMap<String, Object>>();
        } else {
            this.bookings = bookings;
        }
    }

    DateValidatorBookings() {}

    public static final Creator<DateValidatorBookings> CREATOR =
            new Creator<DateValidatorBookings>() {
                @Override
                public DateValidatorBookings createFromParcel(Parcel source) {
                    return new DateValidatorBookings();
                }

                @Override
                public DateValidatorBookings[] newArray(int size) {
                    return new DateValidatorBookings[size];
                }
            };

    @Override
    public boolean isValid(long date) {

        // date to check
        Date current = new Date(date);
        // today's date
        Date now = new Date();

        // if the date is before today => disabled
        if(current.before(now)){
            return false;
        }

        for (HashMap.Entry<String, HashMap<String, Object>> entry : bookings.entrySet()) {

            Long arrivalMillis = (Long) entry.getValue().get("arrival");
            Long departureMillis = (Long) entry.getValue().get("departure");

            Date arrival = new Date(arrivalMillis);
            Date departure = new Date(departureMillis);

            // if the date is in range of reservation => disabled
            if(current.after(arrival) && current.before(departure)){
                return false;
            }
        }

        // else enabled
        return true;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DateValidatorBookings)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        Object[] hashedFields = {};
        return Arrays.hashCode(hashedFields);
    }
}