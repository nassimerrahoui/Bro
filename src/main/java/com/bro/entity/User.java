package com.bro.entity;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import java.util.List;

@Entity("user")
public class User {

    @Id
    private ObjectId id;

    private String firstName;

    private String lastName;

    private String email;

    private String password;

    private String token = "";

    private Boolean isGeolocalizable = false;

    private List<User> bros;

    @Embedded
    private List<Brotherhood> brotherhoods;

    @Embedded
    private List<Geolocation> geolocations;

    /**
     * constructeur vide pour le dao
     */
    public User(){}


    /**
     * @param firstName
     * @param lastName
     * @param email
     * @param password
     */
    public User(String firstName, String lastName, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }

    public ObjectId getId() {
        return id;
    }


    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public List<User> getBros() {
        return bros;
    }

    public Boolean getIsGeolocalizable() {
        return isGeolocalizable;
    }

    public List<Geolocation> getGeolocations() {
        return geolocations;
    }

    public List<Brotherhood> getBrotherhoods() {
        return brotherhoods;
    }
}