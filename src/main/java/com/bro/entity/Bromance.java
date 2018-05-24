package com.bro.entity;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Reference;

@Entity("bromance")
public class Bromance {

    @Id
    private ObjectId id;

    private Brolationship brolationship;

    public enum Brolationship {
        ACCEPTED,
        DENIED,
        AWAITING
    }

    @Reference
    private User sender, receiver;

    /**
     * constructeur vide pour le dao
     */
    public Bromance(){}

    public Bromance(User sender, User receiver){
        this.brolationship = Brolationship.AWAITING;
        this.sender = sender;
        this.receiver = receiver;
    }

    public ObjectId getId() {
        return id;
    }

    public Brolationship getBrolationship() {
        return brolationship;
    }

    public void setBrolationship(Brolationship brolationship) {
        this.brolationship = brolationship;
    }

    public User getSender() {
        return sender;
    }

    public User getReceiver() {
        return receiver;
    }
}
