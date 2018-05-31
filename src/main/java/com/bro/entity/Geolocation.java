package com.bro.entity;


import jdk.nashorn.internal.ir.annotations.Reference;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.*;

import javax.xml.ws.RequestWrapper;
import java.util.Objects;

/** Latitude et doubleitude quelque part sur Terre, où se trouve ton/ta bro avec une précision variable. */
@Entity("geolocation")
public class Geolocation {

    @Id
    private ObjectId id;

    private double lat;

    private double lng;

    @Reference
    private User user;

    /**
     * constructeur vide pour le dao
     */
    public Geolocation() {}

    /**
     * @param lat
     * @param lng
     */
    public Geolocation(double lat, double lng, User user) {
        this.lat = lat;
        this.lng = lng;
        this.user = user;
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