package com.bro.entity;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Reference;

/**
 * Represents a broship between two bros
 */
@Entity("brotherhood")
public class Brotherhood {

    @Id
    private ObjectId id;

    private Brolationship brolationship;

    public enum Brolationship {
        ACCEPTED,
        DENIED,
        AWAITING
    }

    @Reference
    private User sender;

    @Reference
    private User receiver;

    /**
     * Empty constructor for DAO
     */
    public Brotherhood() {
    }

    public Brotherhood(User sender, User receiver) {
        this.brolationship = Brolationship.AWAITING;
        this.sender = sender;
        this.receiver = receiver;
    }

    public ObjectId getId() {
        return id;
    }

    public User getSender() {
        return sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public Brolationship getBrolationship() {
        return brolationship;
    }
}
