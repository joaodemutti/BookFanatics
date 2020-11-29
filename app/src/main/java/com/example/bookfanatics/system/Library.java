package com.example.bookfanatics.system;

import java.io.Serializable;

public class Library implements Serializable {
    public String name, address, status;
    public double latitude;
    public double longitude;
    public double rate;
    public int id;
    public Library(String address, String name, Double latitude, Double longitude, double rate)
    {
        this.address = address;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.rate = rate;
    }
    public Library(int id, String address,String name,double latitude,double longitude,double rate)
    {
        this.id = id;
        this.address = address;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.rate = rate;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
            this.rate = rate;
    }
}
