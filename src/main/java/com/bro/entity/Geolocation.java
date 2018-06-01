package com.bro.entity;

import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Reference;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Entity;
import org.bson.types.ObjectId;
import java.util.Date;
import java.util.Objects;


/** Représente un position d'un bro quelque part sur Terre **/
@Entity("geolocation")
@Embedded
public class Geolocation {

    private double lat;

    private double lng;

    @Reference
    private User user;

    private Date timestamp = new Date();


    /**
     * constructeur vide pour le dao
     */
    public Geolocation(){}

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

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user){
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