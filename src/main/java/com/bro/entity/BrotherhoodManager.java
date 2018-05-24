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
    private Brotherhood group;

    private Boolean isGeolocalizable = false;

    /**
     * Constructeur vide pour le DAO
     */
    public BrotherhoodManager() {}

    /**
     * Associates a user to a group with rights
     * @param user a user
     * @param group a brotherhood
     */
    public BrotherhoodManager(User user, Brotherhood group) {
        this.user = user;
        this.group = group;
    }
}
