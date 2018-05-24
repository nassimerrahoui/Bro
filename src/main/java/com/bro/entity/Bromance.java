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
        AWAITING,
        ENEMY
    }

    @Reference
    private User firstBro, secondBro;

    /**
     * constructeur vide pour le dao
     */
    public Bromance(){}

    public Bromance(User firstBro, User secondBro){
        this.brolationship = Brolationship.AWAITING;
        this.firstBro = firstBro;
        this.secondBro = secondBro;
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

    public User getFirstBro() {
        return firstBro;
    }

    public User getSecondBro() {
        return secondBro;
    }
}
