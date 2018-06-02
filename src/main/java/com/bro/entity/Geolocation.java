package com.bro.entity;

import org.mongodb.morphia.annotations.Reference;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Entity;
import org.bson.types.ObjectId;

import java.util.Date;
import java.util.Objects;

/**
 * Represent a GPS position for an user at a given time
 */
@Entity("geolocation")
public class Geolocation {

    @Id
    private ObjectId id;

    private double lat;

    private double lng;

    @Reference
    private User user;


    /**
     * Empty constructor for DAO
     */
    public Geolocation() {
    }

    /**
     * @param lat
     * @param lng
     * @param user
     */
    public Geolocation(double lat, double lng, User user) {
        this.lat = lat;
        this.lng = lng;
        this.user = user;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public User getUser() {
        return user;
    }

    public ObjectId getId() {
        return this.id;
    }

    public void setUser(User user) {
        this.user = user;
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