package com.bro.dao;

import org.bson.types.ObjectId;
import com.bro.entity.BrotherhoodManager;
import com.bro.entity.User;


import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.dao.BasicDAO;

import java.util.Optional;

public class BrotherhoodManagerDAO extends BasicDAO<BrotherhoodManager, ObjectId> {

    public BrotherhoodManagerDAO(Datastore ds) {
        super(ds);
    }

    public Optional<BrotherhoodManager> getBrotherhoods(User user){
        return createQuery()
                .field("users.User").hasThisOne(user)
                .asList().stream().findAny();
    }
}
