package com.example.uav_client.Data.Common;

public class Station {
    String id;
    String name;
    String status;
    double lon;
    double lat;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }

    public double getLon() {
        return lon;
    }

    public double getLat() {
        return lat;
    }

    public Station(String id, String name, String status, double lon, double lat) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.lon = lon;
        this.lat = lat;
    }
}
