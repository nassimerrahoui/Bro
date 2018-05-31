package com.bro.entity;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Reference;

import java.util.Date;


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
     *
     * @param name
     * @param beginning
     * @param end
     */
    public Brotherhood(String name, Date beginning, Date end, User leader){
        this.name = name;
        this.beginning = beginning;
        this.end = end;
        this.leader = leader;
    }

    public void setUser(User user){
        this.leader = leader;
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
