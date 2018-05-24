package com.bro.entity;


import jdk.nashorn.internal.ir.annotations.Reference;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.*;

import javax.xml.ws.RequestWrapper;
import java.util.Objects;

/** Latitude et Longitude quelque part sur Terre, où se trouve ton/ta bro avec une précision variable. */
@Entity("geolocation")
public class Geolocation {

    @Id
    private ObjectId id;

    private String placeName;

    private double lat;

    private double lng;

    private double accuracy;

    private User user;

    /**
     * constructeur vide pour le dao
     */
    public Geolocation() {}

    /**
     * @param lat
     * @param lng
     */
    public Geolocation(double lat, double lng, double accuracy, User user) {
        this.lat = lat;
        this.lng = lng;
        this.accuracy = accuracy;
        this.user = user;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(double accuracy) {
        this.accuracy = accuracy;
    }

    @Reference
    public User getUser() {
        return user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Geolocation geolocation = (Geolocation) o;
        return Double.compare(geolocation.lat, lat) == 0 && Double.compare(geolocation.lng, lng) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(lat, lng);
    }
}