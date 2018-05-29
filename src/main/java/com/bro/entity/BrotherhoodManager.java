package com.bro.entity;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Reference;

/**
 * Represents geolocalizable authorization between a group and a user
 */
public class BrotherhoodManager {

    @Id
    private ObjectId id;

    @Reference
    private User user;

    @Reference
    private Brotherhood brotherhood;

    private Boolean isGeolocalizable = false;

    /**
     * Constructeur vide pour le DAO
     */
    public BrotherhoodManager() {}

    /**
     * Associates a user to a group with rights
     * @param user a user
     * @param brotherhood a brotherhood
     */
    public BrotherhoodManager(User user, Brotherhood brotherhood) {
        this.user = user;
        this.brotherhood = brotherhood;
    }

    public void activateGeolocalisation(){
        this.isGeolocalizable= true;
    }

    public User getUser(){
        return this.user;
    }

    public Brotherhood getBrotherhood(){
        return this.brotherhood;
    }

    public Boolean isGeolocalizable(){
        return this.isGeolocalizable;
    }
}
