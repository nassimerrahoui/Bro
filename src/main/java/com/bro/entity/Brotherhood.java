package com.bro.entity;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Reference;

import java.util.Date;
import java.util.HashMap;


public class Brotherhood {

    @Id
    private ObjectId id;

    private String name;

    @Reference
    private User leader;

    private Date beginning;

    private Date end;

    /**
     * constructeur vide pour le dao
     */
    public Brotherhood(){}

    /**
     * @param name
     * @param leader
     */
    public Brotherhood(String name, User leader, Date beginning, Date end){
        this.name = name;
        this.leader = leader;
        this.beginning = beginning;
        this.end = end;
    }

    public ObjectId getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public User getLeader() {
        return leader;
    }

    public Date getBeginning() { return this.beginning; }

    public Date getEnd() { return this.end;}
}
