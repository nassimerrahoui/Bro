package com.bro.dao;

import com.bro.entity.Brotherhood;
import com.bro.entity.User;
import com.mongodb.WriteResult;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.dao.BasicDAO;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.mongodb.morphia.query.UpdateResults;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class BrotherhoodDAO extends BasicDAO<Brotherhood, ObjectId> {

    public BrotherhoodDAO(Class<Brotherhood> entityClass, Datastore ds) {
        super(entityClass, ds);
    }

    public List<Brotherhood> getBrotherhoods(String user){
        return createQuery()
                .field("users").hasThisOne(user)
                .asList();
    }

    public Optional<Brotherhood> getBrotherhood(String name, User user){
        return createQuery()
                .field("name").equal(name)
                .field("users.User").hasThisOne(user)
                .asList().stream().findAny();
    }

    public UpdateResults addBro(Brotherhood brotherhood, User user){
        Query<Brotherhood> query = createQuery()
                .field("_id").equal(brotherhood.getId());

        UpdateOperations<Brotherhood> ops = getDatastore()
                .createUpdateOperations(Brotherhood.class)
                .addToSet("users", Collections.singletonMap(false, user));

        return update(query, ops);
    }

    public UpdateResults removeBro(Brotherhood brotherhood, User user){
        Query<Brotherhood> query = createQuery()
                .field("_id").equal(brotherhood.getId());


        UpdateOperations<Brotherhood> ops = getDatastore()
                .createUpdateOperations(Brotherhood.class)
                .removeAll("users", Collections.singleton(user));

        return update(query, ops);
    }

    public UpdateResults isGeolocalizable(Brotherhood brotherhood, User user, Boolean isGeolocalizable){
        Query<Brotherhood> query = createQuery()
                .field("_id").equal(brotherhood.getId())
                .field("users.User").hasThisOne(user);

        UpdateOperations<Brotherhood> ops = getDatastore()
                .createUpdateOperations(Brotherhood.class)
                .addToSet("users.Boolean",isGeolocalizable);

        return update(query, ops);
    }


}
