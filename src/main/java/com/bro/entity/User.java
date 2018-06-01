package com.bro.entity;

import org.apache.commons.codec.digest.DigestUtils;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.*;
import java.util.List;

/** Représente un bro **/
@Entity("user")
@Indexes(@Index(fields = {@Field("username"), @Field("email")}, options = @IndexOptions(unique = true)))
public class User {
    @Id
    private ObjectId id;

    private String username;

    private String firstName;

    private String lastName;

    private String email;

    private String password;

    private String token = "";

    private Boolean isLocation = false;

    private List<User> enemies;

    @Embedded
    private Geolocation position = new Geolocation();

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

    public Boolean getIsLocation() {
        return isLocation;
    }

    public List<User> getEnemies() { return enemies; }

    public Geolocation getPosition() {
        return position;
    }

    public void setPosition(Geolocation position) {
        this.position = position;
    }
}