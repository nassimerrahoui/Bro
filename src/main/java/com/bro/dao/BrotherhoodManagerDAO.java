package com.bro.dao;

import org.bson.types.ObjectId;
import com.bro.entity.BrotherhoodManager;
import com.bro.entity.User;


import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.dao.BasicDAO;

public class BrotherhoodManagerDAO extends BasicDAO<BrotherhoodManager, ObjectId> {

    public BrotherhoodManagerDAO(Class<BrotherhoodManager> entityClass, Datastore ds) {
        super(entityClass, ds);
    }


}
