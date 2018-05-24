package com.bro.entity;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Reference;
import java.util.HashMap;


public class Brotherhood {

    @Id
    private ObjectId id;

    private String name;

    @Reference
    private User leader;

    @Embedded
    private HashMap<Boolean, User> users;

    /**
     * constructeur vide pour le dao
     */
    public Brotherhood(){}

    /**
     * @param name
     * @param leader
     */
    public Brotherhood(String name, User leader){
        this.name = name;
        this.leader = leader;
        this.users.put(false, leader);
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
}
