package com.bro.entity;

import org.apache.commons.codec.digest.DigestUtils;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import java.util.List;

@Entity("user")
public class User {
    @Id
    private ObjectId id;

    private String username;

    private String firstName;

    private String lastName;

    private String email;

    private String password;

    private String token = "";

    private Boolean isGeolocalizable = false;

    private List<User> ennemies;

    /**
     * constructeur vide pour le dao
     */
    public User(){}

    /**
     * @param username
     * @param firstName
     * @param lastName
     * @param email
     * @param password
     */
    public User(String username, String firstName, String lastName, String email, String password) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }


    public void encrypt() {
        this.password = DigestUtils.sha256Hex(password);
    }


    public ObjectId getId() {
        return id;
    }

    public String getUsername() {
        return username;
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

    public String getToken() {
        return token;
    }

    public Boolean getIsGeolocalizable() {
        return isGeolocalizable;
    }

    public List<User> getEnnemies() { return ennemies; }
}